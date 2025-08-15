package pro.themed.audhdlauncher.database

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pro.themed.audhdlauncher.AuDHDLauncherActivity

// Extension properties for DataStore instances
private val Context.categoriesDataStore by preferencesDataStore(name = "categories")
private val Context.categoryDetailsDataStore by preferencesDataStore(name = "category_details")
private val Context.appLaunchDataStore by preferencesDataStore(name = "app_launches")

@Serializable
data class CategoryData(
    val name: String,
    val defaultInclude: String? = null,
    val defaultExclude: String? = null,
    val customInclude: String? = null,
    val customExclude: String? = null,
    val rows: Int = 3, // Default rows changed to 3
    val customBool: Int = 0,
    val customInt: Int = 0,
    val color: String = "",
)

class AppDataStoreRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    // Key for storing list of category names
    private val CATEGORY_NAMES_KEY = stringSetPreferencesKey("category_names")


    // Progressive loading configuration
    companion object {
        private const val CATEGORY_ADD_DELAY = 50L
        private const val APPS_POPULATE_DELAY = 100L  
        private const val ICON_LOAD_DELAY = 10L
        private const val ENABLE_PROGRESSIVE_LOADING = true // Toggle for progressive vs normal loading
    }

    // Initialize default categories on repository creation
    init {
        CoroutineScope(Dispatchers.IO).launch { initializeDefaultCategories() }
    }

    private suspend fun initializeDefaultCategories() {
        val existingCategories =
            context.categoriesDataStore.data.first()[CATEGORY_NAMES_KEY]?.toSet() ?: emptySet()

        // Process each default category
        DefaultCategoryData.categories.forEach { defaultCategory ->
            val categoryExists = existingCategories.contains(defaultCategory.name)

            if (categoryExists) {
                // Category exists - update its default include/exclude values
                val existingCategory = getCategoryDetails(defaultCategory.name)
                if (existingCategory != null) {
                    // Update default include/exclude fields from DefaultCategoryData, preserve other custom fields.
                    val updatedCategory =
                        existingCategory.copy(
                            defaultInclude = defaultCategory.defaultInclude,
                            defaultExclude = defaultCategory.defaultExclude,
                        )
                    updateCategory(updatedCategory)
                }
            } else {
                // Category doesn't exist - add it
                addCategory(defaultCategory)
            }
        }
    }

    // Get all categories as a Flow
    val allCategories: Flow<List<CategoryData>> =
        combine(
            context.categoriesDataStore.data,
            context.categoryDetailsDataStore.data // Add this
        ) { categoryPrefs, detailPrefs -> // Preferences from both DataStores
            val categoryNames = categoryPrefs[CATEGORY_NAMES_KEY] ?: emptySet()
            categoryNames.mapNotNull { name ->
                val detailKey = stringPreferencesKey(name)
                val jsonString = detailPrefs[detailKey] // Read from the detailPrefs provided by combine
                if (jsonString != null) {
                    try {
                        json.decodeFromString<CategoryData>(jsonString)
                    } catch (e: Exception) {
                        // Log error or handle corrupt data if necessary
                        Log.e("AppDataStoreRepository", "Error decoding category: $name", e)
                        null
                    }
                } else {
                    // This case might happen if a category name exists but its details don't
                    // Or if a detail key was removed but name list hasn't updated yet.
                    // Depending on desired behavior, might log this.
                    null
                }
            }
        }
        .flowOn(Dispatchers.IO)
        .distinctUntilChanged()

    // Get apps sorted by category with launch counts
    private fun performInitialCategorization(
        categories: List<CategoryData>,
        allApps: List<ResolveInfo>,
        launchCounts: Map<String, Int>,
        minAppsPerCategory: Int
    ): Triple<MutableList<Pair<CategoryData, List<ResolveInfo>>>, MutableList<ResolveInfo>, MutableSet<String>> {
        val categorizedAppLists = mutableListOf<Pair<CategoryData, List<ResolveInfo>>>()
        val orphanApps = mutableListOf<ResolveInfo>()
        val categorizedPackageNames = mutableSetOf<String>()

        categories.forEach { category ->
            // Skip Uncategorized - it gets handled separately with orphan apps
            if (category.name == "Uncategorized") return@forEach
            
            val appsForCategory = filterAppsByCategory(category, allApps)
            if (appsForCategory.isNotEmpty()) {
                if (appsForCategory.size < minAppsPerCategory && category.name != "Uncategorized") { // Don't treat pre-existing "Uncategorized" as needing orphans
                    orphanApps.addAll(appsForCategory)
                } else {
                    val sortedApps = appsForCategory.sortedByDescending {
                        launchCounts["${it.activityInfo.packageName}_${category.name}"] ?: 0
                    }.distinctBy { it.activityInfo.packageName }

                    categorizedPackageNames.addAll(sortedApps.map { it.activityInfo.packageName })
                    categorizedAppLists.add(category to sortedApps)
                }
            }
        }
        return Triple(categorizedAppLists, orphanApps, categorizedPackageNames)
    }

    private fun addUncategorizedApps(
        allApps: List<ResolveInfo>,
        orphanAppsFromCategorization: List<ResolveInfo>,
        categorizedPackageNames: Set<String>,
        finalCategorizedAppLists: MutableList<Pair<CategoryData, List<ResolveInfo>>>,
        launchCounts: Map<String, Int>
    ) {
        val trulyUncategorizedApps = (allApps.filter { it.activityInfo.packageName !in categorizedPackageNames } +
                                     orphanAppsFromCategorization.filter { it.activityInfo.packageName !in categorizedPackageNames })
                                     .distinctBy { it.activityInfo.packageName }

        if (trulyUncategorizedApps.isNotEmpty()) {
            // Use proper Uncategorized category from defaults  
            val uncategorizedCategory = CategoryData("Uncategorized", rows = 3, color = "#808080")
            
            // Sort uncategorized apps by launch count
            val sortedUncategorizedApps = trulyUncategorizedApps.sortedByDescending {
                val count = launchCounts["${it.activityInfo.packageName}_Uncategorized"] ?: 0
                Log.d("LaunchCount", "Uncategorized app ${it.activityInfo.packageName}: $count launches")
                count
            }
            
            finalCategorizedAppLists.add(uncategorizedCategory to sortedUncategorizedApps)
        }
    }

    // Reactive progressive loading state
    private val _progressiveState = MutableStateFlow<List<Pair<CategoryData, List<ResolveInfo>>>>(emptyList())
    private var isProgressiveInitialized = false
    private var reactiveListenerStarted = false
    
    // Trigger for package change events
    private val _packageChangeEvent = MutableStateFlow(0L)
    private var lastPackageEventTime = 0L
    
    // Main method that provides reactive progressive loading
    fun getProgressivelySortedAppsByCategory(): Flow<List<Pair<CategoryData, List<ResolveInfo>>>> {
        Log.d("ReactiveTest", "🎯 INIT: getProgressivelySortedAppsByCategory called")
        // Start progressive initialization if not already started
        if (!isProgressiveInitialized) {
            isProgressiveInitialized = true
            Log.d("ReactiveTest", "🚀 START: Starting progressive initialization")
            CoroutineScope(Dispatchers.IO).launch {
                initializeProgressively()
            }
        } else {
            Log.d("ReactiveTest", "⚡ SKIP: Progressive initialization already started")
        }
        
        // Also listen for reactive updates to launch counts and categories (only start once)
        if (!reactiveListenerStarted) {
            reactiveListenerStarted = true
            Log.d("ReactiveTest", "👂 LISTENER: Starting reactive listener for launch counts")
            CoroutineScope(Dispatchers.IO).launch {
                combine(
                    allCategories,
                    getAllLaunchCounts(),
                    _packageChangeEvent
                ) { categories, launchCounts, packageEvent ->
                    Triple(categories, launchCounts, packageEvent)
                }.collect { (categories, launchCounts, packageEvent) ->
                    Log.d("ReactiveTest", "🔔 REACTIVE-TRIGGER: categories=${categories.size}, launchCounts=${launchCounts.size}, packageEvent=$packageEvent")
                    Log.d("ReactiveTest", "📊 PROGRESSIVE-STATE: isEmpty=${_progressiveState.value.isEmpty()}, size=${_progressiveState.value.size}")
                    Log.d("ReactiveTest", "⏰ LAST-EVENT-TIME: $lastPackageEventTime")
                    
                    // Only update if progressive loading is complete (not empty)
                    if (_progressiveState.value.isNotEmpty()) {
                        // Check if this is a package event (new timestamp) or just data change
                        val isPackageEvent = packageEvent > lastPackageEventTime && packageEvent > 0
                        Log.d("ReactiveTest", "🤔 EVENT-CHECK: packageEvent=$packageEvent, lastPackageEventTime=$lastPackageEventTime, isPackageEvent=$isPackageEvent")
                        
                        if (isPackageEvent) {
                            Log.d("ReactiveTest", "📦 PACKAGE-EVENT: Package change detected (time: $packageEvent), doing full refresh")
                            lastPackageEventTime = packageEvent
                            refreshAppList(categories, launchCounts)
                        } else {
                            Log.d("ReactiveTest", "🔔 REACTIVE: Launch counts or categories changed, updating sort order")
                            Log.d("ReactiveTest", "📊 COUNTS: Total launch count entries: ${launchCounts.size}")
                            updateSortOrder(categories, launchCounts)
                        }
                    } else {
                        Log.d("ReactiveTest", "⏳ WAITING: Progressive loading not complete yet, skipping reactive update")
                    }
                }
            }
        } else {
            Log.d("ReactiveTest", "👂 SKIP: Reactive listener already started")
        }
        
        return _progressiveState
    }
    
    // Trigger a refresh when package events occur (install/update/uninstall)
    fun triggerPackageRefresh() {
        val timestamp = System.currentTimeMillis()
        Log.d("PackageEvent", "🔄 Triggering package refresh at timestamp: $timestamp")
        Log.d("PackageEvent", "📊 Current progressive state size: ${_progressiveState.value.size}")
        Log.d("PackageEvent", "🔍 Progressive initialized: $isProgressiveInitialized")
        
        // If progressive loading hasn't started yet, force it to start
        if (!isProgressiveInitialized) {
            Log.d("PackageEvent", "🚀 Progressive loading not started, triggering initialization...")
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Force progressive loading to start
                    getProgressivelySortedAppsByCategory().first()
                    Log.d("PackageEvent", "✅ Progressive loading initialized")
                } catch (e: Exception) {
                    Log.e("PackageEvent", "❌ Failed to initialize progressive loading", e)
                }
            }
        }
        
        Log.d("PackageEvent", "🔄 Setting _packageChangeEvent to: $timestamp")
        _packageChangeEvent.value = timestamp
        Log.d("PackageEvent", "✅ Package refresh event emitted")
    }
    
    // Refresh app list completely (for package changes)
    private suspend fun refreshAppList(categories: List<CategoryData>, launchCounts: Map<String, Int>) {
        Log.d("ReactiveTest", "🔄 FULL-REFRESH: Starting complete app list refresh")
        
        // Add a small delay to allow PackageManager to update its cache
        delay(500)
        
        // Get fresh app list with retry mechanism
        var allApps = getLaunchableApps(context, forceRefresh = true)
        
        // Store original package names for comparison
        val originalPackageNames = _progressiveState.value.flatMap { it.second.map { app -> app.activityInfo.packageName } }.toSet()
        val newPackageNames = allApps.map { it.activityInfo.packageName }.toSet()
        
        Log.d("PackageEvent", "📊 Package comparison: original=${originalPackageNames.size}, new=${newPackageNames.size}")
        Log.d("PackageEvent", "🆕 New packages: ${newPackageNames - originalPackageNames}")
        Log.d("PackageEvent", "🗑️ Removed packages: ${originalPackageNames - newPackageNames}")
        
        // If no change detected, try a few more times with delays
        var retryCount = 0
        val maxRetries = 2
        
        while (retryCount < maxRetries && newPackageNames == originalPackageNames) {
            retryCount++
            Log.d("PackageEvent", "🔄 No package changes detected, retrying... (attempt $retryCount)")
            delay(1000L * retryCount) // 1s, 2s delays
            allApps = getLaunchableApps(context, forceRefresh = true)
            val retryPackageNames = allApps.map { it.activityInfo.packageName }.toSet()
            
            if (retryPackageNames != originalPackageNames) {
                Log.d("PackageEvent", "✅ Package changes detected on retry $retryCount")
                break
            }
        }
        
        Log.d("ReactiveTest", "📊 FRESH-APPS: Got ${allApps.size} apps after ${retryCount} retries")
        val minAppsPerCategory = 6
        
        // Perform complete re-categorization with fresh app list
        val (categorizedAppLists, orphanApps, categorizedPackageNames) = 
            performInitialCategorization(categories, allApps, launchCounts, minAppsPerCategory)
        
        val finalCategorizedAppLists = categorizedAppLists.toMutableList()
        
        // Add uncategorized apps
        addUncategorizedApps(allApps, orphanApps, categorizedPackageNames, finalCategorizedAppLists, launchCounts)
        
        // Update the state with completely fresh data
        _progressiveState.value = finalCategorizedAppLists
        Log.d("ReactiveTest", "📡 FULL-REFRESH: Complete refresh finished, ${finalCategorizedAppLists.size} categories")
    }
    
    // Update sort order when launch counts or categories change
    private suspend fun updateSortOrder(categories: List<CategoryData>, launchCounts: Map<String, Int>) {
        Log.d("ReactiveTest", "🔄 FULL-RESORT: Starting full re-sort of all categories")
        val currentState = _progressiveState.value.toMutableList()
        val allApps = getLaunchableApps(context)
        Log.d("ReactiveTest", "📊 RESORT-DATA: ${categories.size} categories, ${launchCounts.size} launch counts")
        
        // Re-sort each category's apps based on new launch counts AND update category data
        for (i in currentState.indices) {
            val (oldCategory, apps) = currentState[i]
            
            // Find the updated category data from the reactive listener
            val updatedCategory = categories.find { it.name == oldCategory.name } ?: oldCategory
            
            if (updatedCategory != oldCategory) {
                Log.d("ReactiveTest", "🔄 CATEGORY-UPDATE: ${oldCategory.name} settings changed (rows: ${oldCategory.rows} -> ${updatedCategory.rows})")
            }
            
            if (apps.isNotEmpty()) {
                Log.d("ReactiveTest", "🔄 SORTING: Category ${updatedCategory.name} with ${apps.size} apps")
                val sortedApps = apps.sortedByDescending { app ->
                    val count = launchCounts["${app.activityInfo.packageName}_${updatedCategory.name}"] ?: 0
                    Log.d("ReactiveTest", "📊 SORT: App ${app.activityInfo.packageName} in ${updatedCategory.name}: $count launches")
                    count
                }
                currentState[i] = updatedCategory to sortedApps  // Use updated category data
                Log.d("ReactiveTest", "✅ SORTED: Category ${updatedCategory.name} re-sorted with updated settings")
            } else {
                // Even if no apps, still update the category data
                currentState[i] = updatedCategory to apps
                Log.d("ReactiveTest", "✅ UPDATED: Category ${updatedCategory.name} settings updated (no apps to sort)")
            }
        }
        
        // Update the state
        _progressiveState.value = currentState
        Log.d("ReactiveTest", "📡 FULL-EMIT: Full re-sort complete, state updated with new category settings")
    }

    // Add a new category
    suspend fun addCategory(category: CategoryData) {
        // Add to category names list
        context.categoriesDataStore.edit { preferences ->
            val currentNames = preferences[CATEGORY_NAMES_KEY]?.toMutableSet() ?: mutableSetOf()
            currentNames.add(category.name)
            preferences[CATEGORY_NAMES_KEY] = currentNames
        }

        // Store category details
        saveCategoryDetails(category)
    }

    // Update an existing category
    suspend fun updateCategory(category: CategoryData) {
        saveCategoryDetails(category)
    }

    // Delete a category
    suspend fun deleteCategory(categoryName: String) {
        // Remove from category names list
        context.categoriesDataStore.edit { preferences ->
            val currentNames = preferences[CATEGORY_NAMES_KEY]?.toMutableSet() ?: return@edit
            currentNames.remove(categoryName)
            preferences[CATEGORY_NAMES_KEY] = currentNames
        }

        // Remove category details
        val detailKey = stringPreferencesKey(categoryName)
        context.categoryDetailsDataStore.edit { preferences -> preferences.remove(detailKey) }

        // Clean up any launch counts for this category
        removeAllLaunchCountsForCategory(categoryName)
    }

    // Clear app launch count for a specific category
    suspend fun clearAppLaunchCount(categoryName: String, packageName: String) {
        val key = intPreferencesKey("${packageName}_${categoryName}")
        Log.d("LaunchCount", "Clearing launch count for $packageName in category $categoryName")
        context.appLaunchDataStore.edit { preferences ->
            preferences.remove(key)
            Log.d("LaunchCount", "Launch count cleared for $packageName in $categoryName")
        }
        
        // Immediately update the position of this specific app (move to end since count is now 0)
        if (_progressiveState.value.isNotEmpty()) {
            updateSingleAppPosition(categoryName, packageName, 0)
        }
    }

    // Increment app launch count for a specific category
    suspend fun incrementAppLaunchCount(categoryName: String, packageName: String) {
        val key = intPreferencesKey("${packageName}_${categoryName}")
        Log.d("ReactiveTest", "🚀 LAUNCH: Recording launch for $packageName in category $categoryName")
        
        var newCount = 0
        context.appLaunchDataStore.edit { preferences ->
            val currentCount = preferences[key] ?: 0
            newCount = currentCount + 1
            preferences[key] = newCount
            Log.d("ReactiveTest", "📊 COUNT: Launch count for $packageName in $categoryName: $currentCount -> $newCount")
        }
        
        Log.d("ReactiveTest", "⚡ IMMEDIATE: Calling updateSingleAppPosition for $packageName")
        // Immediately update the position of this specific app (only if progressive loading is complete)
        if (_progressiveState.value.isNotEmpty()) {
            updateSingleAppPosition(categoryName, packageName, newCount)
        } else {
            Log.d("ReactiveTest", "⏳ SKIP: Progressive loading not complete, reactive listener will handle the update")
        }
    }
    
    // Optimized method to update just one app's position without full re-sort
    private suspend fun updateSingleAppPosition(categoryName: String, packageName: String, newCount: Int) {
        Log.d("ReactiveTest", "🔍 SEARCH: Looking for $packageName in category $categoryName")
        val currentState = _progressiveState.value.toMutableList()
        val launchCounts = getAllLaunchCounts().first() // Cache the launch counts
        Log.d("ReactiveTest", "📋 STATE: Current state has ${currentState.size} categories")
        
        // Find the category and app
        for (i in currentState.indices) {
            val (category, apps) = currentState[i]
            Log.d("ReactiveTest", "🔎 CHECKING: Category ${category.name} (${apps.size} apps)")
            if (category.name == categoryName) {
                val appIndex = apps.indexOfFirst { it.activityInfo.packageName == packageName }
                Log.d("ReactiveTest", "📍 FOUND: App $packageName at index $appIndex in category $categoryName")
                if (appIndex != -1) {
                    val app = apps[appIndex]
                    val mutableApps = apps.toMutableList()
                    
                    // Remove the app from its current position
                    mutableApps.removeAt(appIndex)
                    Log.d("ReactiveTest", "❌ REMOVED: App from position $appIndex")
                    
                    // Find the correct new position based on launch count (higher counts go first)
                    var newPosition = 0
                    for (j in mutableApps.indices) {
                        val otherAppCount = launchCounts["${mutableApps[j].activityInfo.packageName}_$categoryName"] ?: 0
                        Log.d("ReactiveTest", "⚖️ COMPARE: $packageName($newCount) vs ${mutableApps[j].activityInfo.packageName}($otherAppCount)")
                        if (newCount > otherAppCount) {
                            newPosition = j
                            break
                        }
                        newPosition = j + 1
        }
                    
                    // Insert at the new position
                    mutableApps.add(newPosition, app)
                    currentState[i] = category to mutableApps
                    
                    Log.d("ReactiveTest", "✅ MOVED: $packageName from position $appIndex to $newPosition (count: $newCount)")
                    Log.d("ReactiveTest", "🔄 UPDATE: Updating _progressiveState with new order")
                    break
                } else {
                    Log.w("ReactiveTest", "⚠️ NOT FOUND: App $packageName not found in category $categoryName")
                }
            }
        }
        
        // Update the state
        val oldStateSize = _progressiveState.value.size
        _progressiveState.value = currentState
        Log.d("ReactiveTest", "📡 EMIT: State updated from $oldStateSize to ${currentState.size} categories")
    }

    // Private methods

    private suspend fun getCategoryDetails(categoryName: String): CategoryData? {
        val detailKey = stringPreferencesKey(categoryName)
        val jsonString = context.categoryDetailsDataStore.data.first()[detailKey] ?: return null
        return try {
            json.decodeFromString<CategoryData>(jsonString)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun saveCategoryDetails(category: CategoryData) {
        val detailKey = stringPreferencesKey(category.name)
        val jsonString = json.encodeToString(CategoryData.serializer(), category)
        context.categoryDetailsDataStore.edit { preferences -> preferences[detailKey] = jsonString }
    }

    fun getAllLaunchCounts(): Flow<Map<String, Int>> {
        return context.appLaunchDataStore.data.map { preferences ->
            preferences
                .asMap()
                .mapNotNull { (key, value) ->
                    if (key is Preferences.Key<*> && value is Int) {
                        key.name to value
                    } else null
                }
                .toMap()
        }
    }

    private suspend fun removeAllLaunchCountsForCategory(categoryName: String) {
        context.appLaunchDataStore.edit { preferences ->
            val keysToRemove =
                preferences.asMap().keys.filter { it.name.endsWith("_$categoryName") }
            keysToRemove.forEach { preferences.remove(it) }
        }
    }

    // App filtering methods

    private fun filterAppsByCategory(
        category: CategoryData,
        apps: List<ResolveInfo>
    ): List<ResolveInfo> {
        // Special case: Uncategorized should never match apps during normal filtering
        if (category.name == "Uncategorized") {
            return emptyList() // Uncategorized gets populated separately with orphan apps
        }
        
        return apps.filter { app ->
            val packageName = app.activityInfo.packageName.lowercase()

            // Rule 1: Custom Exclude - if matched, always exclude.
            if (category.customExclude?.split(",")?.any { packageName.contains(it.lowercase()) } == true) {
                return@filter false
            }

            // Rule 2: Default Exclude - if matched, always exclude (unless overridden by custom include).
            val isDefaultExcluded = category.defaultExclude?.split(",")?.any { packageName.contains(it.lowercase()) } == true
            val isCustomIncluded = category.customInclude?.split(",")?.any { packageName.contains(it.lowercase()) } == true

            if (isDefaultExcluded && !isCustomIncluded) {
                return@filter false
            }

            // Rule 3: Inclusion logic
            // If defaultInclude is specified, app must match it OR be customIncluded.
            // If defaultInclude is NOT specified, app is considered included at this stage (unless customIncluded is specified and doesn't match).

            val isDefaultIncluded = category.defaultInclude?.split(",")?.any { packageName.contains(it.lowercase()) }

            when {
                isCustomIncluded -> true // Custom include takes precedence
                category.defaultInclude != null -> isDefaultIncluded == true // Must match default include if specified
                else -> true // No default include specified, and not custom included (already checked), so included by default
            }
        }
    }

    // Progressive loading: shows categories first, then populates apps, then loads icons async
    private suspend fun initializeProgressively() {
        Log.d("Progressive", "Starting progressive initialization: ${System.currentTimeMillis() - AuDHDLauncherActivity.startTime}ms")
        
        val progressiveList = mutableListOf<Pair<CategoryData, List<ResolveInfo>>>()
        val allApps = getLaunchableApps(context)
        val launchCounts = getAllLaunchCounts().first()
        val categories = allCategories.first()
        val minAppsPerCategory = 6
        
        // Pre-filter categories to only include those with apps
        val orphanApps = mutableListOf<ResolveInfo>()
        val categorizedPackageNames = mutableSetOf<String>()
        val categoriesWithApps = mutableListOf<Pair<CategoryData, List<ResolveInfo>>>()
        
        // First pass: determine which categories have apps (skip Uncategorized)
        categories.forEach { category ->
            // Skip Uncategorized - it gets handled separately with orphan apps
            if (category.name == "Uncategorized") return@forEach
            
            val appsForCategory = filterAppsByCategory(category, allApps)
            if (appsForCategory.isNotEmpty()) {
                if (appsForCategory.size < minAppsPerCategory && category.name != "Uncategorized") {
                    orphanApps.addAll(appsForCategory)
                } else {
                    val sortedApps = appsForCategory.sortedByDescending {
                        launchCounts["${it.activityInfo.packageName}_${category.name}"] ?: 0
                    }.distinctBy { it.activityInfo.packageName }
                    
                    categorizedPackageNames.addAll(sortedApps.map { it.activityInfo.packageName })
                    categoriesWithApps.add(category to sortedApps)
                }
            }
        }
        
        // Stage 1: Add categories one by one (only those with apps)
        categoriesWithApps.forEach { (category, _) ->
            Log.d("Progressive", "Adding category ${category.name}: ${System.currentTimeMillis() - AuDHDLauncherActivity.startTime}ms")
            
            // Add category with empty app list first
            progressiveList.add(category to emptyList())
            _progressiveState.value = progressiveList.toList() // Update state
            
            // Small delay to make it visible
            if (ENABLE_PROGRESSIVE_LOADING) {
                delay(CATEGORY_ADD_DELAY)
            }
        }
        
        // Stage 2: Populate each category with apps
        categoriesWithApps.forEachIndexed { index, (category, sortedApps) ->
            Log.d("Progressive", "Populating apps for ${category.name}: ${System.currentTimeMillis() - AuDHDLauncherActivity.startTime}ms")
            
            // Update the category with its apps
            progressiveList[index] = category to sortedApps
            _progressiveState.value = progressiveList.toList() // Update state
            
            // Small delay between categories
            if (ENABLE_PROGRESSIVE_LOADING) {
                delay(APPS_POPULATE_DELAY)
            }
        }
        
        // Stage 3: Add uncategorized apps if needed
        val trulyUncategorizedApps = (allApps.filter { it.activityInfo.packageName !in categorizedPackageNames } +
                                     orphanApps.filter { it.activityInfo.packageName !in categorizedPackageNames })
                                     .distinctBy { it.activityInfo.packageName }
        
        if (trulyUncategorizedApps.isNotEmpty()) {
            Log.d("Progressive", "Adding uncategorized apps: ${System.currentTimeMillis() - AuDHDLauncherActivity.startTime}ms")
            
            // Find the proper Uncategorized category from defaults
            val uncategorizedCategory = categories.find { it.name == "Uncategorized" } 
                ?: CategoryData("Uncategorized", rows = 3, color = "#808080") // Fallback
            
            // Sort uncategorized apps by launch count too
            val sortedUncategorizedApps = trulyUncategorizedApps.sortedByDescending {
                val count = launchCounts["${it.activityInfo.packageName}_Uncategorized"] ?: 0
                Log.d("LaunchCount", "Progressive uncategorized app ${it.activityInfo.packageName}: $count launches")
                count
            }
            
            progressiveList.add(uncategorizedCategory to sortedUncategorizedApps)
            _progressiveState.value = progressiveList.toList()
        }
        
        Log.d("Progressive", "Progressive initialization complete: ${System.currentTimeMillis() - AuDHDLauncherActivity.startTime}ms")
        Log.d("ReactiveTest", "🎉 COMPLETE: Progressive loading finished, reactive updates now active!")
    }
}

// Utility functions
fun getLaunchableApps(context: Context, forceRefresh: Boolean = false): List<ResolveInfo> {
    val pm = context.packageManager
    val mainIntent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
    
    // Always use the same flags for consistency between init and refresh
    val flags = PackageManager.MATCH_ALL
    
    val apps = pm.queryIntentActivities(mainIntent, flags)
    Log.d("PackageEvent", "📱 getLaunchableApps: Found ${apps.size} raw apps (forceRefresh=$forceRefresh)")
    
    // Filter out system/non-launchable apps that shouldn't appear in launcher
    val filteredApps = apps.filter { app ->
        val packageName = app.activityInfo.packageName.lowercase()
        val activityName = app.activityInfo.name.lowercase()
        
        // Exclude common system apps that shouldn't be launched
        val excludedPackages = listOf(
            "com.android.systemui",
            "com.android.settings.fuelgauge",
            "com.android.emergency",
            "com.android.cellbroadcastreceiver",
            "com.android.cts",
            "com.android.shell"
        )
        
        val excludedActivities = listOf(
            "terminalemulator",
            "emergencybroadcast",
            "cellbroadcast",
            "fuelgauge",
            "batterysaver"
        )
        
        // Keep app if it's not in excluded packages and doesn't have excluded activity names
        !excludedPackages.any { packageName.contains(it) } &&
        !excludedActivities.any { activityName.contains(it) }
    }
    
    Log.d("PackageEvent", "📱 getLaunchableApps: Filtered to ${filteredApps.size} launchable apps")
    
    return filteredApps.sortedBy {
        it.loadLabel(pm).toString().lowercase()
    }
}

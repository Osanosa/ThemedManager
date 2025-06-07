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
import kotlinx.coroutines.flow.Flow
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
    val rows: Int = 2,
    val customBool: Int = 0,
    val customInt: Int = 0,
    val color: String = "",
)

class AppDataStoreRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    // Key for storing list of category names
    private val CATEGORY_NAMES_KEY = stringSetPreferencesKey("category_names")

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
        finalCategorizedAppLists: MutableList<Pair<CategoryData, List<ResolveInfo>>>
    ) {
        val trulyUncategorizedApps = (allApps.filter { it.activityInfo.packageName !in categorizedPackageNames } +
                                     orphanAppsFromCategorization.filter { it.activityInfo.packageName !in categorizedPackageNames })
                                     .distinctBy { it.activityInfo.packageName }

        if (trulyUncategorizedApps.isNotEmpty()) {
            val uncategorizedCategory = CategoryData("Uncategorized") // Consider if this should be a predefined constant
            finalCategorizedAppLists.add(uncategorizedCategory to trulyUncategorizedApps)
        }
    }

    fun getSortedAppsByCategory(): Flow<List<Pair<CategoryData, List<ResolveInfo>>>> =
        allCategories
            .combine(getAllLaunchCounts()) { categories, launchCounts ->
                Log.d("Performance", "started sorting apps: ${System.currentTimeMillis() - AuDHDLauncherActivity.startTime}ms")

                val allApps = getLaunchableApps(context)
                val minAppsPerCategory = 6 // Could be a constant or configurable

                val (initialCategorizedLists, orphanApps, categorizedPackages) =
                    performInitialCategorization(categories, allApps, launchCounts, minAppsPerCategory)

                addUncategorizedApps(allApps, orphanApps, categorizedPackages, initialCategorizedLists)

                Log.d("Performance", "finished sorting apps: ${System.currentTimeMillis() - AuDHDLauncherActivity.startTime}ms")
                initialCategorizedLists // This is now the final list
            }
            .flowOn(Dispatchers.IO)
            .distinctUntilChanged()

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

    // Increment app launch count for a specific category
    suspend fun incrementAppLaunchCount(categoryName: String, packageName: String) {
        val key = intPreferencesKey("${packageName}_${categoryName}")
        context.appLaunchDataStore.edit { preferences ->
            val currentCount = preferences[key] ?: 0
            preferences[key] = currentCount + 1
        }
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

    private fun getAllLaunchCounts(): Flow<Map<String, Int>> {
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
}

// Utility functions
fun getLaunchableApps(context: Context): List<ResolveInfo> {
    val pm = context.packageManager
    val mainIntent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
    return pm.queryIntentActivities(mainIntent, PackageManager.MATCH_ALL).sortedBy {
        it.loadLabel(pm).toString().lowercase()
    }
}

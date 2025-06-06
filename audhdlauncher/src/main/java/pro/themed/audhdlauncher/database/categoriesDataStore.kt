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
                    // Update with default values but preserve customizations
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
        context.categoriesDataStore.data
            .map { preferences ->
                val categoryNames = preferences[CATEGORY_NAMES_KEY] ?: emptySet()
                categoryNames.mapNotNull { name -> getCategoryDetails(name) }
            }
            .flowOn(Dispatchers.IO)
            .distinctUntilChanged()

    // Get apps sorted by category with launch counts
    fun getSortedAppsByCategory(): Flow<List<Pair<CategoryData, List<ResolveInfo>>>> =
        allCategories
            .combine(getAllLaunchCounts()) { categories, launchCounts ->
                val totalTime1 = System.currentTimeMillis() - AuDHDLauncherActivity.startTime
                Log.d("Performance", "started sorting apps: ${totalTime1}ms")

                val allApps = getLaunchableApps(context)
                val categorizedApps = mutableSetOf<String>()
                val categoryAppLists = mutableListOf<Pair<CategoryData, List<ResolveInfo>>>()
                val orphanApps = mutableListOf<ResolveInfo>()
                val minAppsPerCategory = 6

                // First pass - categorize apps based on filters
                categories.forEach { category ->
                    val apps = filterAppsByCategory(category, allApps)

                    if (apps.isNotEmpty()) {
                        if (apps.size < minAppsPerCategory) {
                            orphanApps.addAll(apps)
                        } else {
                            // Sort by launch count
                            val sortedApps =
                                apps.sortedByDescending {
                                    launchCounts["${it.activityInfo.packageName}_${category.name}"]
                                        ?: 0
                                }
                            categorizedApps.addAll(sortedApps.map { it.activityInfo.packageName })
                            categoryAppLists.add(
                                category to sortedApps.distinctBy { it.activityInfo.packageName }
                            )
                        }
                    }
                }

                // Handle uncategorized apps
                val uncategorizedApps =
                    allApps.filter { it.activityInfo.packageName !in categorizedApps } +
                        orphanApps.filter { it.activityInfo.packageName !in categorizedApps }

                if (uncategorizedApps.isNotEmpty()) {
                    val uncategorizedCategory = CategoryData("Uncategorized")
                    categoryAppLists.add(
                        uncategorizedCategory to
                            uncategorizedApps.distinctBy { it.activityInfo.packageName }
                    )
                }

                val totalTime2 = System.currentTimeMillis() - AuDHDLauncherActivity.startTime
                Log.d("Performance", "finished sorting apps: ${totalTime2}ms")
                categoryAppLists
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
        apps: List<ResolveInfo>,
    ): List<ResolveInfo> {
        return apps.filter { app ->
            val packageName = app.activityInfo.packageName.lowercase()

            // First pass - check default filters
            val defaultIncluded =
                category.defaultInclude?.split(",")?.any { packageName.contains(it.lowercase()) }
                    ?: false
            val defaultExcluded =
                category.defaultExclude?.split(",")?.any { packageName.contains(it.lowercase()) }
                    ?: false

            // Only proceed to check custom filters if app passed default filters
            if (defaultExcluded) return@filter false

            // If not included by default filters, check custom filters
            if (!defaultIncluded) {
                val customIncluded =
                    category.customInclude?.split(",")?.any { packageName.contains(it.lowercase()) }
                        ?: false

                if (!customIncluded) return@filter false
            }

            // Final check - custom exclude overrides all includes
            val customExcluded =
                category.customExclude?.split(",")?.any { packageName.contains(it.lowercase()) }
                    ?: false

            !customExcluded
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

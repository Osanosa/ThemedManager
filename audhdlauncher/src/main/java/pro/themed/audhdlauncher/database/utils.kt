package pro.themed.audhdlauncher.database

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo

// Get all launchable apps
fun getLaunchableApps(context: Context): List<ResolveInfo> {
    val pm = context.packageManager
    val mainIntent =
        Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
    return pm.queryIntentActivities(mainIntent, 0)
}

// Filter apps into categories based on include/exclude rules
fun getAppsByCategory(
    context: Context,
    category: CategoryData,
    apps: List<ResolveInfo>,
): List<ResolveInfo> {
    return apps.filter { app ->
        val packageName = app.activityInfo.packageName
        val include =
            category.defaultInclude?.split(",")?.any { packageName.contains(it) } == true ||
                category.customInclude?.split(",")?.any { packageName.contains(it) } == true
        val exclude =
            category.defaultExclude?.split(",")?.any { packageName.contains(it) } == true ||
                category.customExclude?.split(",")?.any { packageName.contains(it) } == true

        include && !exclude
    }
}

// Sort apps by launch count in each category
fun getSortedAppsByCategory(
    context: Context,
    dbHelper: LauncherDbHelper,
): List<Pair<CategoryData, List<ResolveInfo>>> {
    val categories = dbHelper.getAllCategories()
    val allApps = getLaunchableApps(context)

    // This set tracks package names that have already been assigned to a valid category.
    val categorizedApps = mutableSetOf<String>()
    // We'll build a list of (category, apps) pairs for valid categories.
    val categoryAppLists = mutableListOf<Pair<CategoryData, List<ResolveInfo>>>()
    // Apps from categories that don’t meet the minimum threshold will be orphaned.
    val orphanApps = mutableListOf<ResolveInfo>()
    // Define a minimum count threshold; categories with fewer apps are discarded.
    val minAppsPerCategory = 2

    categories.forEach { category ->
        // Get apps for this category (using your filtering logic).
        val apps = getAppsByCategory(context, category, allApps)
        if (apps.isNotEmpty()) {
            if (apps.size < minAppsPerCategory) {
                // Too few apps—don’t show a separate folder.
                orphanApps.addAll(apps)
            } else {
                val launchCounts =
                    dbHelper.getLaunchCountsForCategory(dbHelper.getCategoryId(category.name))
                val sortedApps =
                    apps.sortedByDescending { launchCounts[it.activityInfo.packageName] ?: 0 }
                categorizedApps.addAll(sortedApps.map { it.activityInfo.packageName })
                categoryAppLists.add(category to sortedApps)
            }
        }
    }

    // Apps that haven't been placed in any category.
    val uncategorizedApps =
        allApps.filter { it.activityInfo.packageName !in categorizedApps } + orphanApps

    if (uncategorizedApps.isNotEmpty()) {
        val uncategorizedCategory = CategoryData("Uncategorized", null, null, null, null, 0, 0)
        categoryAppLists.add(uncategorizedCategory to uncategorizedApps)
    }

    return categoryAppLists
}

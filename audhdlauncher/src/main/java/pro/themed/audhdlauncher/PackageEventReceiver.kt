package pro.themed.audhdlauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pro.themed.audhdlauncher.database.AppDataStoreRepository

/**
 * Broadcast receiver that listens for app install, update, and uninstall events
 * to automatically refresh the launcher app list and clear relevant data
 */
class PackageEventReceiver : BroadcastReceiver(), KoinComponent {
    
    // Inject the same repository instance used by the ViewModel
    private val repository: AppDataStoreRepository by inject()
    
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val packageName = intent.data?.schemeSpecificPart
        val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
        
        Log.d("PackageEvent", "📦 Received broadcast: action=$action, package=$packageName")
        Log.d("PackageEvent", "🔍 Intent extras: isReplacing=$isReplacing, data=${intent.data}")
        Log.d("PackageEvent", "📱 Context: ${context.javaClass.simpleName}")
        Log.d("PackageEvent", "🔧 Repository instance: ${repository.hashCode()}")
        
        if (packageName == null) {
            Log.w("PackageEvent", "⚠️ Package name is null, ignoring broadcast")
            return
        }
        
        // Skip our own package to avoid unnecessary work
        if (packageName == context.packageName) {
            Log.d("PackageEvent", "🔄 Ignoring event for our own package: $packageName")
            return
        }
        
        when (action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                if (isReplacing) {
                    Log.d("PackageEvent", "🔄 App updated: $packageName")
                    handleAppUpdate(context, packageName)
                } else {
                    Log.d("PackageEvent", "➕ App installed: $packageName")
                    handleAppInstall(context, packageName)
                }
            }
            
            Intent.ACTION_PACKAGE_REMOVED -> {
                val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                if (!isReplacing) {
                    Log.d("PackageEvent", "❌ App uninstalled: $packageName")
                    handleAppUninstall(context, packageName)
                }
            }
            
            Intent.ACTION_PACKAGE_REPLACED -> {
                Log.d("PackageEvent", "🔄 App replaced: $packageName")
                handleAppUpdate(context, packageName)
            }
            
            Intent.ACTION_PACKAGE_CHANGED -> {
                Log.d("PackageEvent", "📝 App changed: $packageName")
                handleAppUpdate(context, packageName)
            }
            
            else -> {
                Log.d("PackageEvent", "🤷 Unknown action: $action for package: $packageName")
            }
        }
    }
    
    private fun handleAppInstall(context: Context, packageName: String) {
        Log.d("PackageEvent", "🚀 Processing app install: $packageName")
        
        // Clear any cached icon (in case this is a reinstall with a different icon)
        Log.d("PackageEvent", "🖼️ Clearing cached icon for installed app: $packageName")
        IconLoader.clearCachedIcon(packageName)
        
        // For new installs, the progressive loading system will automatically pick up
        // the new app on the next refresh. No specific action needed.
        triggerAppListRefresh(context, "App installed: $packageName")
    }
    
    private fun handleAppUpdate(context: Context, packageName: String) {
        Log.d("PackageEvent", "🔄 Processing app update: $packageName")
        
        // Clear cached icon for the updated app so new icon loads
        Log.d("PackageEvent", "🖼️ Clearing cached icon for updated app: $packageName")
        IconLoader.clearCachedIcon(packageName)
        
        // For updates, the app should remain in the same categories with the same launch counts
        // The progressive loading system will handle the refresh automatically
        triggerAppListRefresh(context, "App updated: $packageName")
    }
    
    private fun handleAppUninstall(context: Context, packageName: String) {
        Log.d("PackageEvent", "🗑️ Processing app uninstall: $packageName")
        
        // Clean up launch count data for the uninstalled app using injected repository
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Get all launch counts to find entries for this package
                repository.getAllLaunchCounts().collect { launchCounts ->
                    val keysToRemove = launchCounts.keys.filter { key ->
                        key.startsWith("${packageName}_")
                    }
                    
                    Log.d("PackageEvent", "🧹 Found ${keysToRemove.size} launch count entries to clean up for $packageName")
                    
                    if (keysToRemove.isNotEmpty()) {
                        // Clear launch counts for each category this app was in
                        keysToRemove.forEach { key ->
                            val categoryName = key.substringAfter("${packageName}_")
                            if (categoryName.isNotEmpty()) {
                                Log.d("PackageEvent", "🗑️ Clearing launch count for $packageName in category $categoryName")
                                repository.clearAppLaunchCount(categoryName, packageName)
                            }
                        }
                        Log.d("PackageEvent", "✅ Cleanup completed for uninstalled app: $packageName")
                    } else {
                        Log.d("PackageEvent", "ℹ️ No launch count data found for $packageName")
                    }
                    
                    // Stop collecting after processing
                    return@collect
                }
                
            } catch (e: Exception) {
                Log.e("PackageEvent", "❌ Error cleaning up data for uninstalled app $packageName", e)
            }
        }
        
        triggerAppListRefresh(context, "App uninstalled: $packageName")
    }
    
    private fun triggerAppListRefresh(context: Context, reason: String) {
        Log.d("PackageEvent", "🔄 Triggering app list refresh: $reason")
        Log.d("PackageEvent", "📱 Current time: ${System.currentTimeMillis()}")
        
        // Trigger immediate refresh through the reactive system using injected repository
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("PackageEvent", "📞 Calling repository.triggerPackageRefresh() on injected instance")
                repository.triggerPackageRefresh()
                Log.d("PackageEvent", "✅ Package refresh triggered successfully for: $reason")
            } catch (e: Exception) {
                Log.e("PackageEvent", "❌ Error triggering package refresh for: $reason", e)
            }
        }
    }
} 
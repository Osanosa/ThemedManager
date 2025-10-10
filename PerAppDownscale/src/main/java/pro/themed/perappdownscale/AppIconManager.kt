package pro.themed.perappdownscale

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages app icon caching with drawable caching and package change monitoring
 */
object AppIconManager {
    val iconCache = ConcurrentHashMap<String, Drawable>()
    private val _loadingState = MutableStateFlow<Set<String>>(emptySet())
    val loadingState: StateFlow<Set<String>> = _loadingState.asStateFlow()

    private var packageReceiver: BroadcastReceiver? = null
    private var isReceiverRegistered = false

    /**
     * Initialize package change receiver
     */
    fun initialize(context: Context) {
        if (isReceiverRegistered) return

        packageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    Intent.ACTION_PACKAGE_ADDED,
                    Intent.ACTION_PACKAGE_REPLACED,
                    Intent.ACTION_PACKAGE_REMOVED -> {
                        val packageName = intent.data?.schemeSpecificPart
                        if (packageName != null) {
                            Log.d("AppIconManager", "Package changed: $packageName, clearing cache")
                            clearCachedIcon(packageName)
                        }
                    }
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }

        context.registerReceiver(packageReceiver, filter)
        isReceiverRegistered = true
        Log.d("AppIconManager", "Package change receiver registered")
    }

    /**
     * Cleanup resources
     */
    fun cleanup(context: Context) {
        if (isReceiverRegistered && packageReceiver != null) {
            context.unregisterReceiver(packageReceiver)
            isReceiverRegistered = false
            Log.d("AppIconManager", "Package change receiver unregistered")
        }
    }

    /**
     * Load icon drawable asynchronously
     */
    fun loadIconAsync(
        context: Context,
        packageName: String,
        scope: CoroutineScope,
        onIconLoaded: (Drawable) -> Unit
    ) {
        // Check cache first
        iconCache[packageName]?.let {
            onIconLoaded(it)
            return
        }

        // Check if already loading
        if (_loadingState.value.contains(packageName)) {
            Log.d("AppIconManager", "Icon already loading for $packageName")
            return
        }

        // Mark as loading
        _loadingState.value = _loadingState.value + packageName

        scope.launch(Dispatchers.IO) {
            try {
                delay(10) // Small delay to prevent overwhelming

                // Double-check cache after delay
                iconCache[packageName]?.let { cachedIcon ->
                    launch(Dispatchers.Main) {
                        onIconLoaded(cachedIcon)
                    }
                    return@launch
                }

                // Load drawable directly
                val packageManager = context.packageManager
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                val drawable = packageManager.getApplicationIcon(appInfo)

                // Cache the drawable
                iconCache[packageName] = drawable

                launch(Dispatchers.Main) {
                    onIconLoaded(drawable)
                    Log.d("AppIconManager", "Icon loaded for $packageName")
                }
            } catch (e: Exception) {
                Log.e("AppIconManager", "Failed to load icon for $packageName", e)
                // Try default icon as fallback
                try {
                    val defaultIcon = context.packageManager.defaultActivityIcon
                    iconCache[packageName] = defaultIcon
                    launch(Dispatchers.Main) {
                        onIconLoaded(defaultIcon)
                    }
                } catch (fallbackException: Exception) {
                    Log.e("AppIconManager", "Failed to load default icon for $packageName", fallbackException)
                }
            } finally {
                _loadingState.value = _loadingState.value - packageName
            }
        }
    }

    /**
     * Get cached drawable
     */
    fun getCachedIcon(packageName: String): Drawable? = iconCache[packageName]

    /**
     * Clear cached icon for specific package
     */
    fun clearCachedIcon(packageName: String) {
        val removed = iconCache.remove(packageName)
        Log.d("AppIconManager", "Cleared cached icon for $packageName: ${if (removed != null) "removed" else "not found"}")

        // Also remove from loading state if it was being loaded
        _loadingState.value = _loadingState.value - packageName
    }

    /**
     * Clear all cached icons
     */
    fun clearCache() {
        Log.d("AppIconManager", "Clearing icon cache (${iconCache.size} items)")
        iconCache.clear()
        _loadingState.value = emptySet()
    }

    /**
     * Force reload icon
     */
    fun forceReloadIcon(
        context: Context,
        packageName: String,
        scope: CoroutineScope,
        onIconLoaded: (Drawable) -> Unit
    ) {
        iconCache.remove(packageName)
        loadIconAsync(context, packageName, scope, onIconLoaded)
    }
}

/**
 * Composable that loads app icon as drawable
 */
@Composable
fun AppIconDrawable(
    packageName: String,
    scope: CoroutineScope,
    onIconLoaded: ((Drawable) -> Unit)? = null
) {
    val context = LocalContext.current

    var drawable by remember(packageName) { mutableStateOf<Drawable?>(null) }

    LaunchedEffect(packageName) {
        val cachedIcon = AppIconManager.getCachedIcon(packageName)
        if (cachedIcon != null) {
            drawable = cachedIcon
            onIconLoaded?.invoke(cachedIcon)
        } else {
            AppIconManager.loadIconAsync(context, packageName, scope) { loadedIcon ->
                drawable = loadedIcon
                onIconLoaded?.invoke(loadedIcon)
            }
        }
    }

    drawable
}

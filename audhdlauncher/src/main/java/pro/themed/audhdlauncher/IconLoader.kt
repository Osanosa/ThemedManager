package pro.themed.audhdlauncher

import android.content.Context
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pro.themed.audhdlauncher.AuDHDLauncherActivity.Companion.startTime
import java.util.concurrent.ConcurrentHashMap

/**
 * Progressive icon loader that loads icons asynchronously
 */
object IconLoader {
    private val iconCache = ConcurrentHashMap<String, Drawable>()
    private val _loadingState = MutableStateFlow<Set<String>>(emptySet())
    val loadingState: StateFlow<Set<String>> = _loadingState.asStateFlow()

    /**
     * Loads an icon progressively for a given package with improved error handling
     */
    fun loadIconAsync(
        context: Context,
        resolveInfo: ResolveInfo,
        scope: CoroutineScope,
        onIconLoaded: (Drawable) -> Unit
    ) {
        val packageName = resolveInfo.activityInfo.packageName
        
        // Check if already cached
        iconCache[packageName]?.let { 
            onIconLoaded(it)
            return 
        }
        
        // Check if already loading - if so, return early to prevent race conditions
        if (_loadingState.value.contains(packageName)) {
            Log.d("IconLoader", "Icon already loading for $packageName, skipping duplicate request")
            return
        }
        
        // Mark as loading
        _loadingState.value = _loadingState.value + packageName
        
        scope.launch(Dispatchers.IO) {
            try {
                Log.d("IconLoader", "Loading icon for $packageName: ${System.currentTimeMillis() - startTime}ms")
                
                // Small delay to prevent overwhelming the system
                delay(10)
                
                // Double-check cache after delay (another thread might have loaded it)
                iconCache[packageName]?.let { cachedIcon ->
                    launch(Dispatchers.Main) {
                        onIconLoaded(cachedIcon)
                        Log.d("IconLoader", "Icon found in cache during load for $packageName")
                    }
                    return@launch
                }
                
                val icon = resolveInfo.loadIcon(context.packageManager)
                
                // Verify icon was loaded successfully
                if (icon != null) {
                    iconCache[packageName] = icon
                    
                    // Switch to main thread to update UI
                    launch(Dispatchers.Main) {
                        onIconLoaded(icon)
                        Log.d("IconLoader", "Icon loaded successfully for $packageName: ${System.currentTimeMillis() - startTime}ms")
                    }
                } else {
                    Log.w("IconLoader", "Icon loading returned null for $packageName")
                }
            } catch (e: Exception) {
                Log.e("IconLoader", "Failed to load icon for $packageName", e)
                // Try to load default icon as fallback
                try {
                    val defaultIcon = context.packageManager.defaultActivityIcon
                    if (defaultIcon != null) {
                        iconCache[packageName] = defaultIcon
                        launch(Dispatchers.Main) {
                            onIconLoaded(defaultIcon)
                            Log.d("IconLoader", "Using default icon for $packageName")
                        }
                    }
                } catch (fallbackException: Exception) {
                    Log.e("IconLoader", "Failed to load default icon for $packageName", fallbackException)
                }
            } finally {
                // Remove from loading state
                _loadingState.value = _loadingState.value - packageName
            }
        }
    }
    
    /**
     * Preloads icons for a list of apps
     */
    fun preloadIcons(
        context: Context,
        apps: List<ResolveInfo>,
        scope: CoroutineScope,
        onBatchComplete: () -> Unit = {}
    ) {
        scope.launch(Dispatchers.IO) {
            Log.d("IconLoader", "Starting preload for ${apps.size} apps")
            
            apps.forEach { resolveInfo ->
                val packageName = resolveInfo.activityInfo.packageName
                
                if (!iconCache.containsKey(packageName) && !_loadingState.value.contains(packageName)) {
                    loadIconAsync(context, resolveInfo, scope) { /* Icon loaded */ }
                    delay(5) // Small delay between each icon
                }
            }
            
            onBatchComplete()
        }
    }
    
    /**
     * Gets a cached icon if available
     */
    fun getCachedIcon(packageName: String): Drawable? = iconCache[packageName]
    
    /**
     * Clears the icon cache (useful for debugging or memory management)
     */
    fun clearCache() {
        Log.d("IconLoader", "Clearing icon cache (${iconCache.size} items)")
        iconCache.clear()
        _loadingState.value = emptySet()
    }
    
    /**
     * Clears cached icon for a specific package (useful when app is updated)
     */
    fun clearCachedIcon(packageName: String) {
        val removed = iconCache.remove(packageName)
        Log.d("IconLoader", "Cleared cached icon for $packageName: ${if (removed != null) "found and removed" else "not found in cache"}")
        
        // Also remove from loading state if it was being loaded
        _loadingState.value = _loadingState.value - packageName
    }
    
    /**
     * Forces reload of a specific icon (clears from cache and reloads)
     */
    fun forceReloadIcon(
        context: Context,
        resolveInfo: ResolveInfo,
        scope: CoroutineScope,
        onIconLoaded: (Drawable) -> Unit
    ) {
        val packageName = resolveInfo.activityInfo.packageName
        iconCache.remove(packageName)
        loadIconAsync(context, resolveInfo, scope, onIconLoaded)
    }
}

/**
 * Composable that loads an icon progressively
 */
@Composable
fun ProgressiveIcon(
    resolveInfo: ResolveInfo,
    scope: CoroutineScope,
    fallbackIcon: Drawable? = null
) {
    val context = LocalContext.current
    val packageName = resolveInfo.activityInfo.packageName
    var icon by remember(packageName) { mutableStateOf<Drawable?>(null) }
    
    // Check for cached icon first
    LaunchedEffect(packageName) {
        val cachedIcon = IconLoader.getCachedIcon(packageName)
        if (cachedIcon != null) {
            icon = cachedIcon
        } else {
            // Load icon asynchronously
            IconLoader.loadIconAsync(context, resolveInfo, scope) { loadedIcon ->
                icon = loadedIcon
            }
        }
    }
    
    // Use the loaded icon or fallback
    val painterIcon = icon ?: fallbackIcon
    painterIcon?.let { rememberDrawablePainter(it) }
} 
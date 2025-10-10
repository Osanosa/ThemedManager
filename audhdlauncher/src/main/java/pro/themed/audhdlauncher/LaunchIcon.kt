package pro.themed.audhdlauncher

import android.app.ActivityOptions
import android.content.Context
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.window.SplashScreen
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import pro.themed.audhdlauncher.AuDHDLauncherActivity.Companion.startTime
import pro.themed.audhdlauncher.database.AppDataStoreRepository
import pro.themed.audhdlauncher.util.getActivityOrNull

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIcon(
    resolveInfo: ResolveInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit = {}
) {
    val totalTime1 = System.currentTimeMillis() - startTime
    Log.d("Performance", "Started Icon draw: ${totalTime1}ms")

    val context = LocalContext.current
    val activity = context.getActivityOrNull()
    val packageName = remember { resolveInfo.activityInfo.packageName }
    val intent = remember { context.packageManager.getLaunchIntentForPackage(packageName) }
    val scope = rememberCoroutineScope()
    
    // Progressive icon loading with retry mechanism
    var iconDrawable by remember(packageName) { mutableStateOf<Drawable?>(null) }
    var retryCount by remember(packageName) { mutableStateOf(0) }
    
    // Load icon progressively
    LaunchedEffect(packageName, retryCount) {
        val cachedIcon = IconLoader.getCachedIcon(packageName)
        if (cachedIcon != null) {
            iconDrawable = cachedIcon
            Log.d("Performance", "Icon from cache: ${System.currentTimeMillis() - startTime}ms")
        } else {
            // Load asynchronously
            IconLoader.loadIconAsync(context, resolveInfo, scope) { loadedIcon ->
                iconDrawable = loadedIcon
                Log.d("Performance", "Icon loaded async: ${System.currentTimeMillis() - startTime}ms")
            }
        }
    }
    
    // Retry mechanism with delay if icon fails to load
    LaunchedEffect(iconDrawable, packageName) {
        if (iconDrawable == null && retryCount < 3) {
            delay(500L + (retryCount * 500)) // Increasing delay: 1s, 1.5s, 2s
            if (iconDrawable == null) { // Check again after delay
                retryCount++
                Log.d("IconLoader", "Retrying icon load for $packageName (attempt ${retryCount + 1})")
            }
        }
    }
    
    // Use the loaded icon or a placeholder
    val icon = iconDrawable?.let { rememberDrawablePainter(it) }
    
    var position by remember { mutableStateOf(Offset.Companion.Zero) }
    val centerX = remember { derivedStateOf { position.x.toInt() + 24 } }
    val centerY = remember { derivedStateOf { position.y.toInt() + 24 } }
    val options = remember {
        derivedStateOf {
            ActivityOptions.makeScaleUpAnimation(
                activity?.window?.peekDecorView(), centerX.value, centerY.value, 0, 0
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+

                    setSplashScreenStyle(SplashScreen.SPLASH_SCREEN_STYLE_ICON)

                }
            }
        }
    }

    Box(
        modifier = modifier
            .size(48.dp)
            .onGloballyPositioned { coordinates ->
                position = coordinates.localToRoot(Offset.Companion.Zero)
            }
            .clip(CircleShape)
            .combinedClickable(onClick = {
                context.startActivity(intent, options.value.toBundle())
                scope.launch { onClick() }
            }, onLongClick = {
                onLongClick()
            })
    ) {
        // Show icon if loaded, otherwise show placeholder or loading state
        if (icon != null) {
            Image(
                painter = icon, 
                contentDescription = null, 
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .bounceClick().clip(CircleShape)
            )
        } else {
            // Placeholder while loading
            Box(
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .bounceClick()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        CircleShape
                    )
            ) {
                // Simple loading placeholder
            }
        }
        
        LaunchedEffect(Unit) {
            Log.d("Performance", "Finished Icon draw: ${System.currentTimeMillis() - startTime}ms")
        }
    }
}

@Composable
fun LaunchIcon(
    resolveInfo: ResolveInfo,
    categoryName: String,
    context: Context,
    modifier: Modifier = Modifier,
) {
    var showPopup by remember { mutableStateOf(false) }
    val repository: AppDataStoreRepository = koinInject()

    AppIcon(
        resolveInfo = resolveInfo,
        onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                repository.incrementAppLaunchCount(categoryName, resolveInfo.activityInfo.packageName)
            }
        },
        modifier = modifier,
        onLongClick = { showPopup = true },
    )

    InfoDialog(
        resolveInfo = resolveInfo,
        showpopup = showPopup,
        onDismiss = { showPopup = false },
        context = context,
        categoryName = categoryName,
    )
}

enum class ButtonState {
    Pressed,
    Idle,
}

fun Modifier.bounceClick() = composed {
    val buttonState = remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(if (buttonState.value == ButtonState.Pressed) 0.8f else 1f)

    this.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(buttonState.value) {
            awaitPointerEventScope {
                buttonState.value =
                    if (buttonState.value == ButtonState.Pressed) {
                        waitForUpOrCancellation()
                        ButtonState.Idle
                    } else {
                        awaitFirstDown(false)
                        ButtonState.Pressed
                    }
            }
        }
}

package pro.themed.perappdownscale

import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import android.window.SplashScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ClickableIcon(
    app: Pair<String, AppInfo>,
    privilegedHelper: PrivilegedCommandHelper? = null,
    pm: PackageManager,
    density: Density,
) {
    val context = LocalContext.current
    val packageName = app.first

    // Load painter directly with caching
    val drawablePainter = remember(packageName) {
        // Load drawable: cachemap[package] ?: loadicon()
        AppIconManager.iconCache[packageName] ?: run {
            try {
                "Loading icon".log()
                val appInfo = pm.getApplicationInfo(packageName, 0)
                val loadedIcon = pm.getApplicationIcon(appInfo)
                AppIconManager.iconCache[packageName] = loadedIcon
                loadedIcon
            } catch (e: Exception) {
                val defaultIcon = context.packageManager.defaultActivityIcon
                AppIconManager.iconCache[packageName] = defaultIcon
                defaultIcon
            }
        }
    }.let { drawable -> rememberDrawablePainter(drawable) }

    Box(
        modifier = Modifier
            .size(56.dp)
            .padding(8.dp)
            .background(
                color = Color.Transparent, shape = CircleShape
            )
            .bounceClick()
            .clickable {
                try {
                    // Force stop the app first, then launch it
                    if (privilegedHelper != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            privilegedHelper.executeCommand("am force-stop $packageName")
                            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
                            intent?.log()
                            if (intent != null) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
                                    val opts = ActivityOptions.makeBasic().apply {
                                        setSplashScreenStyle(SplashScreen.SPLASH_SCREEN_STYLE_ICON)
                                    }
                                    context.startActivity(intent, opts.toBundle())
                                } else {
                                    context.startActivity(intent)
                                }
                            } else {
                                Toast.makeText(context, "Cannot launch $packageName", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error launching app: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    ){

        AnimatedVisibility (true, // painter is always available after loading
            enter = scaleIn(), exit = scaleOut()
        ) {
            Image(
                painter = drawablePainter,
                contentDescription = null,

            )

        }

    }
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
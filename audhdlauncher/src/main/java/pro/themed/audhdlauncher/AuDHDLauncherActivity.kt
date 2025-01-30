package pro.themed.audhdlauncher

import android.app.ActivityOptions
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import pro.themed.audhdlauncher.ui.theme.ThemedManagerTheme
import pro.themed.audhdlauncher.ui.theme.contentcol
import pro.themed.audhdlauncher.ui.theme.cookieForeground

fun getCurrentWallPaper(mContext: Context): Bitmap {
    val wallpaperManager = WallpaperManager.getInstance(mContext)
    return if (Build.VERSION.SDK_INT >= 24) {
        val pfd = wallpaperManager.getWallpaperFile(WallpaperManager.FLAG_SYSTEM)
        pfd?.let {
            val result = BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
            try {
                it.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            result
        } ?: wallpaperManager.drawable?.toBitmap() ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    } else {
        wallpaperManager.drawable?.toBitmap() ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }
}

fun getLaunchableApps(context: Context): List<ResolveInfo> {
    val pm = context.packageManager
    val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }
    return pm.queryIntentActivities(mainIntent, 0)
}

class AuDHDLauncherActivity : ComponentActivity() {

    @OptIn(ExperimentalLayoutApi::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            var isVisible by remember { mutableStateOf(true) }

            val readPermissionState = rememberMultiplePermissionsState(
                listOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                readPermissionState.launchMultiplePermissionRequest()
                if (!readPermissionState.allPermissionsGranted) {
                    Toast.makeText(context, "No permission, change in settings", Toast.LENGTH_SHORT).show()
                    isVisible = true
                } else {
                    Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (!Environment.isExternalStorageManager()) {
                    Toast.makeText(context, "No permission, change in settings", Toast.LENGTH_SHORT).show()
                    isVisible = true
                }
            }
            if (!Environment.isExternalStorageManager()) {
                context.startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        Uri.parse("package:pro.themed.audhdlauncher")
                    )
                )
            }
            ThemedManagerTheme {
                Wallpaper()
                Box(
                    modifier = Modifier
                        .safeDrawingPadding()
                        .padding(horizontal = 8.dp)
                ) {
                    Column {

                        CookieCard {
                            FlowRow(
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(
                                        rememberScrollState()
                                    )
                            ) {
                                getLaunchableApps(this@AuDHDLauncherActivity).filter {
                                    it.activityInfo.packageName.contains(
                                        "google"
                                    ) || it.loadLabel(packageManager).contains("google", true)
                                }.forEach { resolveInfo ->
                                    AndroidView(
                                        factory = { context ->
                                            val imageView = ImageView(context)
                                            imageView.setImageDrawable(resolveInfo.loadIcon(context.packageManager))
                                            imageView.setOnClickListener {
                                                val intent =
                                                    context.packageManager.getLaunchIntentForPackage(resolveInfo.activityInfo.packageName)
                                                val centerX = imageView.width / 2
                                                val centerY = imageView.height / 2
                                                val options = ActivityOptions.makeScaleUpAnimation(
                                                    imageView,
                                                    centerX,
                                                    centerY,
                                                    0,
                                                    0
                                                )
                                                context.startActivity(intent, options.toBundle())
                                            }
                                            imageView
                                        }, modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                        }

                        CookieCard {
                            FlowRow(
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(
                                        rememberScrollState()
                                    )
                            ) {
                                getLaunchableApps(this@AuDHDLauncherActivity).filter {
                                    !it.activityInfo.packageName.contains(
                                        "google"
                                    ) || !it.activityInfo.loadLabel(packageManager).contains("google", true)
                                }.forEach { resolveInfo ->
                                    AndroidView(
                                        factory = { context ->
                                            val imageView = ImageView(context)
                                            imageView.setImageDrawable(resolveInfo.loadIcon(context.packageManager))

                                            // Set transitionName for shared element transition
                                            imageView.transitionName = "iconTransition"

                                            imageView.setOnClickListener {
                                                val intent =
                                                    context.packageManager.getLaunchIntentForPackage(resolveInfo.activityInfo.packageName)

                                                // Wait for layout to be ready to get correct width and height
                                                imageView.viewTreeObserver.addOnGlobalLayoutListener {
                                                    val centerX = imageView.width / 2
                                                    val centerY = imageView.height / 2
                                                    val options = ActivityOptions.makeScaleUpAnimation(
                                                        imageView,
                                                        centerX,
                                                        centerY,
                                                        0,
                                                        0
                                                    )
                                                    context.startActivity(intent, options.toBundle())
                                                }
                                            }
                                            imageView
                                        },
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Wallpaper() {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(getCurrentWallPaper(LocalContext.current)).build(),
        contentDescription = "wallpaper",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
        loading = {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        })
}

@Preview
@Composable
fun CookieCard(
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CircularProgressIndicator()
            Text(text = "Loading...")
        }

    },
) {
    Surface(
        color = cookieForeground.copy(0.7f),
        contentColor = contentcol,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .border(
                width = 8.dp, shape = RoundedCornerShape(16.0.dp), color = cookieForeground.copy(alpha = 0.5f)
            )
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))

    ) {
        Column(modifier = Modifier.clip(RoundedCornerShape(16.dp))) {
            Column(modifier = Modifier.padding(8.dp)) {
                content()
            }
        }
    }
}
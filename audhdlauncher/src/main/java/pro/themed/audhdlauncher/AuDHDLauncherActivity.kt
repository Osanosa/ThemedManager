package pro.themed.audhdlauncher

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import com.google.accompanist.drawablepainter.rememberDrawablePainter
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

    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThemedManagerTheme {
                Wallpaper()
                Box(
                    modifier = Modifier
                        .safeDrawingPadding()
                        .padding(horizontal = 16.dp)
                ) {

                    CookieCard {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            getLaunchableApps(this@AuDHDLauncherActivity).forEach { resolveInfo ->
                                val context = LocalContext.current
                                Image(
                                    painter = rememberDrawablePainter(resolveInfo.loadIcon(this@AuDHDLauncherActivity.packageManager)),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clickable {
                                            val intent =
                                                context.packageManager.getLeanbackLaunchIntentForPackage(resolveInfo.activityInfo.packageName)
                                            intent?.addCategory(Intent.CATEGORY_LAUNCHER)
                                            intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            context.startActivity(intent)
                                        }

                                )
                            }
                        }
                        getLaunchableApps(this@AuDHDLauncherActivity).forEach { resolveInfo ->
                            Text(text = resolveInfo.loadLabel(this@AuDHDLauncherActivity.packageManager).toString())
                        }
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
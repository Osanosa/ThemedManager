package pro.themed.audhdlauncher

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.crossfade
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.koin.androidx.compose.koinViewModel
import pro.themed.audhdlauncher.database.CategoryData
import pro.themed.audhdlauncher.database.LauncherViewModel
import pro.themed.audhdlauncher.ui.theme.ThemedManagerTheme

class AuDHDLauncherActivity : ComponentActivity() {
    companion object {

        val startTime = System.currentTimeMillis()

        lateinit var appContext: Context
            private set
    }

    @OptIn(ExperimentalLayoutApi::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        appContext = applicationContext
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            setSingletonImageLoaderFactory { context ->
                ImageLoader.Builder(this)
                    .crossfade(true)
                    .memoryCache { MemoryCache.Builder().maxSizePercent(this, 0.25).build() }
                    .diskCache {
                        DiskCache.Builder()
                            .directory(cacheDir.resolve("icons"))
                            .maxSizePercent(0.02)
                            .build()
                    }
                    .build()
            }
            val viewModel: LauncherViewModel = koinViewModel()

            ThemedManagerTheme {
                val context = LocalContext.current
                val categories by viewModel.categorizedApps.collectAsState()
                val useVerticalList by viewModel.useVerticalListLayout.collectAsState()

                Box(modifier = Modifier.safeDrawingPadding().padding(horizontal = 8.dp)) {
                    if (useVerticalList) {
                        VerticalAppList(categories = categories, context = context)
                    } else {
                        StaggeredGridAppList(categories = categories, context = context)
                    }
                }
                LaunchedEffect(Unit) {
                    val totalTime = System.currentTimeMillis() - startTime
                    Log.d("Performance", "Launch to composition: ${totalTime}ms")
                }
            }
        }
    }
}

@Composable
fun VerticalAppList(categories: List<Pair<CategoryData, List<android.content.pm.ResolveInfo>>>, context: Context) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        categories.forEach { (category, apps) ->
            CategoryCard(category, apps, context) // Assuming CategoryCard is an existing composable
        }
    }
}

@OptIn(ExperimentalLayoutApi::class) // Ensure this import is present
@Composable
fun StaggeredGridAppList(categories: List<Pair<CategoryData, List<android.content.pm.ResolveInfo>>>, context: Context) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(300.dp), // Or use a setting for column size
        contentPadding = PaddingValues(8.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        categories.forEach { (category, apps) ->
            item { CategoryCard(category, apps, context) }
        }
    }
}

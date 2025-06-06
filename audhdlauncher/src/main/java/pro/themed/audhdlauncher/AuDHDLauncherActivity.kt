package pro.themed.audhdlauncher

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.ContextWrapper
import android.content.pm.ResolveInfo
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.crossfade
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import pro.themed.audhdlauncher.database.AppDataStoreRepository
import pro.themed.audhdlauncher.database.AppSettingsDataStore
import pro.themed.audhdlauncher.database.CategoryData
import pro.themed.audhdlauncher.database.LauncherViewModel
import pro.themed.audhdlauncher.ui.theme.ThemedManagerTheme

lateinit var GlobalViewModel: LauncherViewModel

class LauncherViewModelFactory(
    private val repository: AppDataStoreRepository,
    private val settingsDataStore: AppSettingsDataStore,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LauncherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LauncherViewModel(repository, settingsDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

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
            val repository = AppDataStoreRepository(LocalContext.current)
            val settingsDataStore = AppSettingsDataStore(LocalContext.current)
            GlobalViewModel =
                viewModel(factory = LauncherViewModelFactory(repository, settingsDataStore))

            ThemedManagerTheme {
                val context = LocalContext.current

                val categories = GlobalViewModel.categorizedApps.collectAsState().value
                Box(modifier = Modifier.safeDrawingPadding().padding(horizontal = 8.dp)) {
                    if (true) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.verticalScroll(rememberScrollState()),
                        ) {
                            categories.forEach { (category, apps) ->
                                CategoryCard(category, apps, context)
                            }
                        }
                    } else {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Adaptive(300.dp),
                            contentPadding = PaddingValues(8.dp),
                            verticalItemSpacing = 8.dp,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            categories.forEach { (category, apps) ->
                                item { CategoryCard(category, apps, context) }
                            }
                        }
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
internal fun DebugList(category: CategoryData, apps: List<ResolveInfo>, context: Context) {
    val clipboardManager =
        LocalContext.current.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    CookieCard(alpha = 1f) {
        LazyColumn {
            item {
                Text(
                    text = "${category.name}: ${apps.size}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp),
                )
            }
            items(apps, key = { it.activityInfo.packageName }) { resolveInfo ->
                Row(
                    modifier =
                        Modifier.fillMaxWidth().padding(2.dp).clickable {
                            // Copy package name to clipboard on row click
                            val clipData =
                                ClipData.newPlainText(
                                    "Package Name",
                                    resolveInfo.activityInfo.packageName,
                                )
                            clipboardManager.setPrimaryClip(clipData)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppIcon(resolveInfo = resolveInfo, onClick = {})

                    Spacer(modifier = Modifier.width(8.dp))
                    val name by remember {
                        mutableStateOf(resolveInfo.loadLabel(context.packageManager).toString())
                    }
                    Column {
                        Text(text = name, fontSize = 16.sp)
                        Text(
                            text = resolveInfo.activityInfo.packageName,
                            fontSize = 12.sp,
                            color = Color.Gray,
                        )
                    }
                }
            }
        }
    }
}

fun Context.getActivityOrNull(): Activity? {
    var context = this
    if (context is Activity) return context
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }

    return null
}

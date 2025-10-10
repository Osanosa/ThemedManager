package pro.themed.audhdlauncher

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.component.KoinComponent
import pro.themed.audhdlauncher.database.CategoryData
import pro.themed.audhdlauncher.database.LauncherViewModel
import pro.themed.audhdlauncher.ui.theme.ThemedManagerTheme

class AuDHDLauncherActivity : ComponentActivity(), KoinComponent {
    companion object {

        val startTime = System.currentTimeMillis()

        lateinit var appContext: Context
            private set
    }
    
     var runtimePackageReceiver: PackageEventReceiver? = null

    @OptIn(ExperimentalLayoutApi::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        appContext = applicationContext
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Register runtime package receiver for Android 14+ compatibility
        registerRuntimePackageReceiver()
        setContent {
            val viewModel: LauncherViewModel = koinViewModel()

            ThemedManagerTheme {
                val context = LocalContext.current
                val categories by viewModel.categorizedApps.collectAsState()
                val useVerticalList by viewModel.useVerticalListLayout.collectAsState()

                StatusBarGradient(modifier = Modifier)
                Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                    if (useVerticalList) {
                        VerticalAppList(categories = categories, context = context)
                    } else {
                        StaggeredGridAppList(categories = categories, context = context)
                    }
                }
                
                // Log when categories are loaded progressively
                LaunchedEffect(categories.size) {
                    Log.d("Progressive", "Categories loaded: ${categories.size} at ${System.currentTimeMillis() - startTime}ms")
                    categories.forEach { (category, apps) ->
                        Log.d("Progressive", "  - ${category.name}: ${apps.size} apps")
                    }
                }
                
                LaunchedEffect(Unit) {
                    val totalTime = System.currentTimeMillis() - startTime
                    Log.d("Performance", "Launch to composition: ${totalTime}ms")
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        unregisterRuntimePackageReceiver()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerticalAppList(categories: List<Pair<CategoryData, List<android.content.pm.ResolveInfo>>>, context: Context) {
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    fun refresh() {
        scope.launch {
            isRefreshing = true
            try {
                // Trigger app list refresh through the repository
                val repository = org.koin.core.context.GlobalContext.get().get<pro.themed.audhdlauncher.database.AppDataStoreRepository>()
                repository.triggerPackageRefresh()
            } finally {
                isRefreshing = false
            }
        }
    }
    
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = ::refresh,
        modifier = Modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.verticalScroll(rememberScrollState()).safeDrawingPadding(),
        ) {
            categories.forEach { (category, apps) ->
                key(category.name) {
                    CategoryCard(category, apps, context)
                }
            }
        }
    }
}

@Composable
fun StatusBarGradient(modifier: Modifier = Modifier) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()*2
    val color = MaterialTheme.colorScheme.surface
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(statusBarHeight)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        color.copy(alpha = 0.7f),
                        color.copy(alpha = 0.35f),
                        color.copy(alpha = 0f)
                    )
                )
            )
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StaggeredGridAppList(categories: List<Pair<CategoryData, List<android.content.pm.ResolveInfo>>>, context: Context) {
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    fun refresh() {
        scope.launch {
            isRefreshing = true
            try {
                // Trigger app list refresh through the repository
                val repository = org.koin.core.context.GlobalContext.get().get<pro.themed.audhdlauncher.database.AppDataStoreRepository>()
                repository.triggerPackageRefresh()
            } finally {
                isRefreshing = false
            }
        }
    }
    
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = ::refresh,
        modifier = Modifier
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(300.dp), // Or use a setting for column size
            contentPadding = PaddingValues(8.dp),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            categories.forEach { (category, apps) ->
                item(key = category.name) { 
                    CategoryCard(category, apps, context) 
                }
            }
        }
    }
}

private fun AuDHDLauncherActivity.registerRuntimePackageReceiver() {
    try {
        Log.d("PackageEvent", "🔧 Registering runtime package receiver for Android ${Build.VERSION.SDK_INT}")
        
        runtimePackageReceiver = PackageEventReceiver()
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
            priority = 1000
        }
        
        // Use RECEIVER_EXPORTED for Android 14+ compatibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            registerReceiver(runtimePackageReceiver, intentFilter, Context.RECEIVER_EXPORTED)
            Log.d("PackageEvent", "✅ Runtime receiver registered with RECEIVER_EXPORTED flag")
        } else {
            registerReceiver(runtimePackageReceiver, intentFilter)
            Log.d("PackageEvent", "✅ Runtime receiver registered (pre-Android 14)")
        }
    } catch (e: Exception) {
        Log.e("PackageEvent", "❌ Failed to register runtime package receiver", e)
    }
}

private fun AuDHDLauncherActivity.unregisterRuntimePackageReceiver() {
    try {
        runtimePackageReceiver?.let { receiver ->
            unregisterReceiver(receiver)
            runtimePackageReceiver = null
            Log.d("PackageEvent", "✅ Runtime package receiver unregistered")
        }
    } catch (e: Exception) {
        Log.e("PackageEvent", "❌ Failed to unregister runtime package receiver", e)
    }
}

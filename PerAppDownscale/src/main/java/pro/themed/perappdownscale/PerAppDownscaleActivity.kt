@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class)

package pro.themed.perappdownscale

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pro.themed.perappdownscale.ui.theme.ThemedManagerTheme
import rikka.shizuku.Shizuku
import kotlin.math.roundToInt

class PerAppDownscaleActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics
    
    // Shizuku permission request result listener
    private val permissionResultListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, handle accordingly
            Toast.makeText(this, "Shizuku permission granted", Toast.LENGTH_SHORT).show()
        } else {
            // Permission denied
            Toast.makeText(this, "Shizuku permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Shizuku binder received listener
    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        // Shizuku binder is available - notify about status change
        runOnUiThread {
            Toast.makeText(this, "Shizuku service available", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Shizuku binder dead listener 
    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        // Shizuku binder died - notify about status change
        runOnUiThread {
            Toast.makeText(this, "Shizuku service disconnected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        analytics = Firebase.analytics

        val scope = CoroutineScope(Dispatchers.Main)

        super.onCreate(savedInstanceState)

        fun shareStackTrace(stackTrace: String) {
            scope.launch {
                val intent =
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, stackTrace)
                    }
                startActivity(Intent.createChooser(intent, "Share stack trace"))
            }
        }

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            val stackTrace = Log.getStackTraceString(throwable)
            scope.launch { shareStackTrace(stackTrace) }
        }
        
        // Add Shizuku listeners
        Shizuku.addRequestPermissionResultListener(permissionResultListener)
        Shizuku.addBinderReceivedListener(binderReceivedListener)
        Shizuku.addBinderDeadListener(binderDeadListener)
        
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val prefs: SharedPreferences =
                context.getSharedPreferences("app_tips", MODE_PRIVATE)
            
            // Tips data
            data class Tip(val id: String, val text: String)
            val tips = listOf(
                Tip("app_launch_tip", "💡 Tap any app icon to launch it directly"),
                Tip("incompatibility_tip", "⚠️ If your device's ROM/OS has built-in game optimization features like Game Space, it is recommended to disable them altogether or remove selected games to avoid conflicts that might result in broken scaling")
            )
            
            // Get active tips (not dismissed) - reactive to changes
            var dismissedTips by remember { mutableStateOf(setOf<String>()) }
            val activeTips = remember(dismissedTips) {
                tips.filter { tip ->
                    !prefs.getBoolean("${tip.id}_dismissed", false) && !dismissedTips.contains(tip.id)
                }
            }
            
            @Composable
            fun TipBanner(tip: Tip) {
                Surface(
                    modifier =
                        Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = tip.text,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(
                            onClick = {
                                prefs.edit().putBoolean("${tip.id}_dismissed", true).apply()
                                dismissedTips = dismissedTips + tip.id
                            },
                            modifier = Modifier.size(32.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Dismiss tip",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }

            @Composable
            fun PerAppDownscale(modifier: Modifier = Modifier) {

                val context = LocalContext.current
                
                // Privileged command helper and dialog states
                val privilegedHelper = remember { PrivilegedCommandHelper() }
                var showModeDialog by remember { mutableStateOf(false) }
                var showSettingsDialog by remember { mutableStateOf(false) }
                var shizukuAvailable by remember { mutableStateOf(false) }
                var shizukuInstalled by remember { mutableStateOf(false) }
                var rootAvailable by remember { mutableStateOf(false) }
                var rootPermissionDenied by remember { mutableStateOf(false) }
                val lifecycleOwner = LocalLifecycleOwner.current
                val coroutineScope = rememberCoroutineScope()
                
                // Function to check availability status
                suspend fun checkAvailabilityStatus(skipRootIfDenied: Boolean = false) {
                    val status = privilegedHelper.checkAvailability(context, skipRootIfDenied)
                    shizukuInstalled = status.shizukuInstalled
                    shizukuAvailable = status.shizukuAvailable && status.shizukuPermissionGranted
                    rootAvailable = status.rootAvailable
                    rootPermissionDenied = status.rootPermissionDenied
                }
                
                // Lifecycle observer for resume events
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            coroutineScope.launch {
                                checkAvailabilityStatus(skipRootIfDenied = true)
                            }
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
                
                // Startup and periodic status checking
                LaunchedEffect(Unit) {
                    // Initial startup check
                    val sharedPref = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                    val savedMode = sharedPref.getInt("execution_mode", -1)
                    
                    // Check availability status immediately
                    val status = privilegedHelper.checkAvailability(context)
                    shizukuInstalled = status.shizukuInstalled
                    shizukuAvailable = status.shizukuAvailable && status.shizukuPermissionGranted
                    rootAvailable = status.rootAvailable
                    rootPermissionDenied = status.rootPermissionDenied
                    
                    if (savedMode == -1) {
                        // No mode selected yet, show dialog
                        showModeDialog = true
                    } else {
                        // Mode was previously selected, use it
                        val mode = when (savedMode) {
                            0 -> PrivilegedCommandHelper.ExecutionMode.ROOT
                            1 -> PrivilegedCommandHelper.ExecutionMode.SHIZUKU
                            else -> PrivilegedCommandHelper.ExecutionMode.NONE
                        }
                        privilegedHelper.setExecutionMode(mode)
                        
                        // If saved mode is root but permission is denied, show dialog again
                        if (savedMode == 0 && status.rootPermissionDenied) {
                            showModeDialog = true
                        }
                    }
                    
                    // Periodic status checking (every 5 seconds, only when app is active)
                    while (true) {
                        delay(5000) // Wait 5 seconds
                        // Only check if the app is in the foreground
                        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            checkAvailabilityStatus(skipRootIfDenied = true)
                        }
                    }
                }
                val displayMetrics = context.resources.displayMetrics

                val pm = context.packageManager
                val mainIntent = Intent(Intent.ACTION_MAIN, null)
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                var filter by remember { mutableStateOf("") }

                var resolvedInfos by remember { mutableStateOf(emptyList<ResolveInfo>()) }

                val items = remember { mutableStateMapOf<String, AppInfo>() }

                val appslist =
                    items
                        .filter { app ->
                            // App is NOT a game if no game package pattern matches
                            !gamesList.any { gamePackage ->
                                app.key.contains(gamePackage, ignoreCase = true)
                            }
                        }
                        .filter {
                            filter.isBlank() ||
                                it.value.label.contains(filter, ignoreCase = true) ||
                                it.key.contains(filter, ignoreCase = true)
                        }
                        .toList()
                        .sortedBy { it.second.label }
                val gameslist =
                    items
                        .filter { app ->
                            // App IS a game if any game package pattern matches
                            gamesList.any { gamePackage ->
                                app.key.contains(gamePackage, ignoreCase = true)
                            }
                        }
                        .filter {
                            filter.isBlank() ||
                                it.value.label.contains(filter, ignoreCase = true) ||
                                it.key.contains(filter, ignoreCase = true)
                        }
                        .toList()
                        .sortedBy { it.second.label }

                var finishedLoading by remember { mutableStateOf(false) }

                LaunchedEffect(key1 = Unit) {
                    withContext(Dispatchers.IO) {
                        resolvedInfos =
                            resolvedInfos.ifEmpty {
                                pm.queryIntentActivities(
                                    mainIntent,
                                    PackageManager.ResolveInfoFlags.of(0L),
                                )
                            }

                        resolvedInfos.forEach {
                            val packageName = it.activityInfo.packageName
                            val appIcon = it.loadIcon(pm)
                            
                            // Example: Convert app icon to fixed 48dp bitmap
                            // val standardIconBitmap = AppIconUtils.getStandardAppIconBitmap(appIcon, displayMetrics)
                            // val customSizeBitmap = appIcon.toBitmapWithDpSize(64f, 64f, displayMetrics)
                            
                            val interventions = if (privilegedHelper.getCurrentMode() != PrivilegedCommandHelper.ExecutionMode.NONE) {
                                try {
                                    val result = privilegedHelper.executeCommand("cmd game list-configs $packageName")
                                    result.output
                                } catch (e: Exception) {
                                    ""
                                }
                            } else {
                                ""
                            }
                            
                            items.putIfAbsent(
                                packageName,
                                AppInfo(
                                    it.loadLabel(pm).toString(),
                                    appIcon,
                                    interventions,
                                ),
                            )
                        }
                        finishedLoading = true
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {
                        var selectedIndex by remember { mutableStateOf(0) }
                        val tabs = listOf("Games", "Apps")
                        val clipboardManager: ClipboardManager = LocalClipboardManager.current
                        val refreshScope = rememberCoroutineScope()
                        var isRefreshing by remember { mutableStateOf(false) }

                        // Topbar with title and settings icon
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Status indicator
                            Column(
                                modifier = Modifier.size(48.dp)
                                    .clickable {
                                        val statusMessage = buildString {
                                            append("Execution Modes:\n")
                                            if (rootAvailable) {
                                                append("✓ Root Available\n")
                                            } else if (rootPermissionDenied) {
                                                append("✗ Root Permission Denied\n")
                                            } else {
                                                append("✗ Root Not Available\n")
                                            }
                                            if (shizukuAvailable) {
                                                append("✓ Shizuku Ready\n")
                                            } else if (shizukuInstalled) {
                                                append("⚙ Shizuku Installed (needs configuration)\n")
                                            } else {
                                                append("✗ Shizuku Not Installed\n")
                                            }
                                            if (!rootAvailable && !shizukuAvailable) {
                                                append("⚠ No privileged access ready")
                                            }
                                        }
                                        Toast.makeText(context, statusMessage, Toast.LENGTH_LONG).show()
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (rootAvailable) {
                                    Text(
                                        text = "R",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Green
                                    )
                                } else if (rootPermissionDenied) {
                                    Text(
                                        text = "R",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Red
                                    )
                                }
                                if (shizukuAvailable) {
                                    Text(
                                        text = "S",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Blue
                                    )
                                } else if (shizukuInstalled) {
                                    Text(
                                        text = "S",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Yellow
                                    )
                                }
                                if (!rootAvailable && !shizukuAvailable && !shizukuInstalled && !rootPermissionDenied) {
                                    Text(
                                        text = "⚠",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Red
                                    )
                                }
                            }
                            
                            // Title
                            Text(
                                text = "TMPAD",
                                style = MaterialTheme.typography.headlineLarge,
                                modifier =
                                    Modifier.clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            if (privilegedHelper != null) {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    val result = privilegedHelper.executeCommand(
                                                        """getprop | grep '\[ro\.serialno\]' | sed 's/.*\[\(.*\)\]/\1/' | md5sum -b"""
                                                    )
                                                    clipboardManager.setText(AnnotatedString(result.output))
                                                }
                                            }
                                        }
                                        .padding(4.dp),
                            )
                            
                            // Settings icon
                            IconButton(
                                onClick = { showSettingsDialog = true },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        TabRow(selectedTabIndex = selectedIndex) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selectedContentColor = MaterialTheme.colorScheme.primary,
                                    unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            BadgedBox(
                                                badge = {
                                                    if (index == 0) {
                                                        Badge {
                                                            Text(text = gameslist.size.toString())
                                                        }
                                                    } else {
                                                        Badge {
                                                            Text(text = appslist.size.toString())
                                                        }
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    painter =
                                                        painterResource(
                                                            id =
                                                                if (index == 0)
                                                                    R.drawable.videogame_asset_24px
                                                                else R.drawable.apps_24px
                                                        ),
                                                    contentDescription = null,
                                                    modifier = Modifier.padding(8.dp),
                                                )
                                            }
                                            Spacer(Modifier.width(16.dp))

                                            Text(title)
                                        }
                                    },
                                    selected = selectedIndex == index,
                                    onClick = { selectedIndex = index },
                                )
                            }
                        }

                        val visibleList = if (selectedIndex == 0) gameslist else appslist

                        fun triggerRefresh() =
                            refreshScope.launch {
                                isRefreshing = true
                                finishedLoading = false
                                withContext(Dispatchers.IO) {
                                    resolvedInfos =
                                        pm.queryIntentActivities(
                                            mainIntent,
                                            PackageManager.ResolveInfoFlags.of(0L),
                                        )
                                    items.clear()
                                    for (info in resolvedInfos) {
                                        val packageName = info.activityInfo.packageName
                                        val appIcon = info.loadIcon(pm)
                                        
                                        // Example: Convert app icon to fixed dp bitmap during refresh
                                        // val iconBitmap = AppIconUtils.getAppIconBitmap(appIcon, displayMetrics)
                                        
                                        val interventions = if (privilegedHelper.getCurrentMode() != PrivilegedCommandHelper.ExecutionMode.NONE) {
                                            try {
                                                val result = privilegedHelper.executeCommand("cmd game list-configs $packageName")
                                                result.output
                                            } catch (e: Exception) {
                                                ""
                                            }
                                        } else {
                                            ""
                                        }
                                        
                                        items.putIfAbsent(
                                            packageName,
                                            AppInfo(
                                                info.loadLabel(pm).toString(),
                                                appIcon,
                                                interventions,
                                            ),
                                        )
                                    }
                                }
                                finishedLoading = true
                                isRefreshing = false
                            }

                        val pullRefreshState = PullToRefreshState()

                        RefreshIndicator(state = pullRefreshState)
                        LazyColumn(
                          modifier =  Modifier.fillMaxSize()
                                .pullToRefresh(
                                    isRefreshing,
                                    pullRefreshState,
                                    threshold = 36.dp,
                                    onRefresh = { triggerRefresh() },
                                )
                        ) {
                            item {
                                AnimatedVisibility(visible = !finishedLoading) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier =
                                            Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                                                .background(
                                                    color =
                                                        MaterialTheme.colorScheme.primaryContainer,
                                                    shape = RoundedCornerShape(16.dp),
                                                )
                                                .padding(16.dp)
                                                .fillMaxWidth(),
                                    ) {
                                        LoadingIndicator()
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(text = "Loading list...")
                                    }
                                }
                            }
                            // Show all active tips
                            items(activeTips) { tip ->
                                TipBanner(tip)
                            }
                            if (visibleList.isEmpty() && finishedLoading) {
                                item {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(16.dp),
                                    ) {
                                        Column(Modifier.padding(8.dp)) {
                                            Text(
                                                text =
                                                    if (selectedIndex == 0) "No known games found"
                                                    else "No known apps found",
                                                fontSize = 24.sp,
                                            )
                                            Text(
                                                text =
                                                    if (selectedIndex == 0)
                                                        "Check out apps tab tho →"
                                                    else "Check out games tab tho ↩",
                                                fontSize = 16.sp,
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                Row {
                                    OutlinedTextField(
                                        value = filter,
                                        onValueChange = { filter = it },
                                        label = { Text("Filter") },
                                        modifier =
                                            Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                    )
                                }
                            }
                            itemsIndexed(items = visibleList, key = { _, app -> app.first }) {
                                index,
                                app ->
                                Box(Modifier) { Item(index, app, privilegedHelper) }
                            }
                            if (selectedIndex == 0) {
                                item {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(16.dp),
                                    ) {
                                        Column(Modifier.padding(8.dp)) {
                                            Text(text = "Something missing?", fontSize = 24.sp)
                                            Text(
                                                text =
                                                    "If a game isn't filtered to this tab report it to support group at t.me/themedsupport",
                                                fontSize = 16.sp,
                                            )
                                        }
                                    }
                                }
                                item {
                                    LinkButtons(
                                        modifier =
                                            Modifier.fillMaxWidth()
                                                .padding(horizontal = 72.dp, vertical = 8.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.primary,
                                                    CircleShape,
                                                )
                                    )
                                }
                            }
                        }
                        Row { AdmobBanner(context) }
                    }
                }
                
                // Mode Selection Dialog
                if (showModeDialog) {
                    ModeSelectionDialog(
                        onDismiss = { /* Don't allow dismissal on startup */ },
                        onModeSelected = { mode ->
                            privilegedHelper.setExecutionMode(mode)
                            showModeDialog = false
                        },
                        helper = privilegedHelper
                    )
                }
                
                // Settings Dialog
                if (showSettingsDialog) {
                    SettingsDialog(
                        onDismiss = { showSettingsDialog = false },
                        onChangeModeClicked = {
                            showSettingsDialog = false
                            showModeDialog = true
                        }
                    )
                }
            }

            ThemedManagerTheme {
                PerAppDownscale()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Remove Shizuku listeners to prevent memory leaks
        Shizuku.removeRequestPermissionResultListener(permissionResultListener)
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeBinderDeadListener(binderDeadListener)
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Handle regular Android permissions here if needed
        // Shizuku permissions are handled by the permissionResultListener above
    }
}

fun <T> T.measureTimeMillis(): T {
    val startTime = System.currentTimeMillis()
    val result = this
    val endTime = System.currentTimeMillis()
    val time = endTime - startTime
    println("Execution time: $time ms")
    return result
}

data class AppInfo(val label: String, val drawable: Drawable, val interventions: String)

// Utility functions for bitmap conversion to fixed dp sizes
object BitmapUtils {
    
    /**
     * Converts dp to pixels based on the device's display density
     */
    fun dpToPx(dp: Float, displayMetrics: DisplayMetrics): Int {
        return (dp * displayMetrics.density + 0.5f).toInt()
    }
    
    /**
     * Converts a Drawable to a Bitmap with the specified dp dimensions
     * @param drawable The drawable to convert
     * @param widthDp The desired width in dp
     * @param heightDp The desired height in dp  
     * @param displayMetrics The display metrics to calculate pixel density
     * @return A bitmap with the specified dp dimensions converted to pixels
     */
    fun drawableToBitmapWithDpSize(
        drawable: Drawable,
        widthDp: Float,
        heightDp: Float,
        displayMetrics: DisplayMetrics
    ): Bitmap {
        // Convert dp to pixels
        val widthPx = dpToPx(widthDp, displayMetrics)
        val heightPx = dpToPx(heightDp, displayMetrics)
        
        return drawableToBitmap(drawable, widthPx, heightPx)
    }
    
    /**
     * Converts a Drawable to a Bitmap with the specified pixel dimensions
     * @param drawable The drawable to convert
     * @param widthPx The desired width in pixels
     * @param heightPx The desired height in pixels
     * @return A bitmap with the specified pixel dimensions
     */
    fun drawableToBitmap(drawable: Drawable, widthPx: Int, heightPx: Int): Bitmap {
        // If it's already a BitmapDrawable, extract and scale it
        if (drawable is BitmapDrawable) {
            drawable.bitmap?.let { originalBitmap ->
                return Bitmap.createScaledBitmap(originalBitmap, widthPx, heightPx, true)
            }
        }
        
        // Create a new bitmap with the specified dimensions
        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Set the drawable bounds to match the canvas size
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        
        // Draw the drawable onto the canvas
        drawable.draw(canvas)
        
        return bitmap
    }
}

// Extension functions for easier usage
/**
 * Extension function to convert a Drawable to a fixed dp size Bitmap
 */
fun Drawable.toBitmapWithDpSize(
    widthDp: Float,
    heightDp: Float, 
    displayMetrics: DisplayMetrics
): Bitmap {
    return BitmapUtils.drawableToBitmapWithDpSize(this, widthDp, heightDp, displayMetrics)
}

/**
 * App icon helper functions that use the bitmap conversion utilities
 */
object AppIconUtils {
    
    /**
     * Standard app icon sizes in dp for different use cases
     */
    object IconSizes {
        const val LAUNCHER_ICON = 48f
        const val NOTIFICATION_ICON = 24f
        const val SMALL_ICON = 32f
        const val MEDIUM_ICON = 56f
        const val LARGE_ICON = 72f
    }
    
    /**
     * Converts an app icon drawable to a bitmap with standard launcher icon size (48dp)
     */
    fun getStandardAppIconBitmap(drawable: Drawable, displayMetrics: DisplayMetrics): Bitmap {
        return drawable.toBitmapWithDpSize(IconSizes.LAUNCHER_ICON, IconSizes.LAUNCHER_ICON, displayMetrics)
    }
    
    /**
     * Gets an app icon bitmap for a specific use case
     */
    fun getAppIconBitmap(
        drawable: Drawable, 
        displayMetrics: DisplayMetrics,
        iconSize: Float = IconSizes.MEDIUM_ICON
    ): Bitmap {
        return drawable.toBitmapWithDpSize(iconSize, iconSize, displayMetrics)
    }
    
    /**
     * Example: Batch convert app icons to consistent 48dp bitmaps
     * Useful for creating uniform icon grids or processing multiple icons
     */
    fun convertAppIconsToBitmaps(
        appIcons: Map<String, Drawable>,
        displayMetrics: DisplayMetrics,
        iconSizeDp: Float = IconSizes.LAUNCHER_ICON
    ): Map<String, Bitmap> {
        return appIcons.mapValues { (_, drawable) ->
            drawable.toBitmapWithDpSize(iconSizeDp, iconSizeDp, displayMetrics)
        }
    }
}

@OptIn()
@Composable
fun RefreshIndicator(state: PullToRefreshState) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier.fillMaxWidth()
                .animateContentSize()
                .padding(
                    vertical = (state.distanceFraction * 10).roundToInt().dp,
                    horizontal = 8.dp,
                )
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .height((state.distanceFraction * 50).roundToInt().dp),
    ) {
        Row(Modifier.padding(horizontal = 16.dp)) {
            val text =
                when {
                    state.distanceFraction < 1f -> "Pull to refresh"
                    state.distanceFraction < 1.5f -> "Release to refresh ✅"
                    state.distanceFraction < 1.8f -> "Release to refresh 🦎"
                    else ->
                        List(state.distanceFraction.times(100).minus(180).toInt()) { "🦎" }
                            .joinToString("")
                }
            Text(text = text)
        }
    }
}

@Composable
fun Item(index: Int, app: Pair<String, AppInfo>,  privilegedHelper: PrivilegedCommandHelper? = null) {

    var interventions by rememberSaveable { mutableStateOf(app.second.interventions) }
    var availableModes by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = app.first) {
        if (interventions.isEmpty() && privilegedHelper != null) {
            val result = privilegedHelper.executeCommand("cmd game list-configs ${app.first}")
            interventions = result.output
        }

        // Get available game modes
        if (availableModes.isEmpty() && !interventions.contains("not of game type") && privilegedHelper != null) {
            val modesResult = privilegedHelper.executeCommand("cmd game list-modes ${app.first}")
            val output = modesResult.output
            // Parse: "package current mode: custom, available game modes: [standard,custom]"
            val modesStart = output.indexOf("available game modes: [")
            if (modesStart != -1) {
                val modesEnd = output.indexOf("]", modesStart)
                if (modesEnd != -1) {
                    val modesString = output.substring(modesStart + 23, modesEnd)
                    availableModes = modesString.split(",").map { it.trim() }
                }
            } else {
                // Fallback: assume standard and custom are available if parsing fails
                availableModes = listOf("standard", "custom")
            }
        }
    }
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .background(
                    color =
                        if (index % 2 == 0) Color.LightGray.copy(alpha = 0.05f)
                        else Color.Transparent
                )
                .background(
                    color =
                        if (interventions.contains("Name")) Color.Yellow.copy(alpha = 0.1f)
                        else Color.Transparent
                )
    ) {
        Row(
            Modifier.fillMaxWidth().clickable {
                expanded = !expanded
                if (privilegedHelper != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val result = privilegedHelper.executeCommand("cmd game list-configs ${app.first}")
                        interventions = result.output
                    }
                }
            },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BadgedBox(
                badge = {
                    if (interventions.contains("Name"))
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp),
                        )

                    if (interventions.contains("not of game type"))
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(12.dp).rotate(45f),
                        )
                }
            ) {
                ClickableIcon(app.first, app.second.drawable, privilegedHelper)
            }
            Column {
                Text(text = app.second.label)
                Text(text = app.first, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.weight(1f))
        }

        Controls(expanded, interventions, availableModes,  app.first, privilegedHelper) {
            if (privilegedHelper != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val result = privilegedHelper.executeCommand("cmd game list-configs ${app.first}")
                    interventions = result.output
                }
            }
        }
    }
}

@Composable
private fun Icon(icon: Drawable) {

    Image(
        painter = rememberDrawablePainter(icon),
        contentDescription = null,
        modifier = Modifier.size(56.dp).padding(8.dp),
    )
}

@Composable
private fun ClickableIcon(packageName: String, icon: Drawable, privilegedHelper: PrivilegedCommandHelper? = null) {
    val context = LocalContext.current

    Image(
        painter = rememberDrawablePainter(icon),
        contentDescription = null,
        modifier =
            Modifier.size(56.dp).padding(8.dp).bounceClick().clickable {
                try {
                    // Force stop the app first, then launch it
                    if (privilegedHelper != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            privilegedHelper.executeCommand("am force-stop $packageName")
                            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
                            if (intent != null) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "Cannot launch $packageName", Toast.LENGTH_SHORT)
                                    .show()
                            }}
                    }


                } catch (e: Exception) {
                    Toast.makeText(context, "Error launching app: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            },
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

@Composable
private fun ColumnScope.Controls(
    expanded: Boolean,
    interventions: String,
    availableModes: List<String>,

    packageName: String?,
    privilegedHelper: PrivilegedCommandHelper? = null,
    callback: () -> Unit = {},
) {

    AnimatedVisibility(visible = expanded) {
        if (interventions.contains("not of game type"))
            Text(
                text = interventions + "\n\nThis was added in A15, there's no direct workaround",
                modifier =
                    Modifier.background(MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
                        .padding(16.dp),
            )
        else {
            interventions
                .substringAfter("Game Mode:", "UNSET/UNKNOWN")
                .substringBefore(",", "UNSET/UNKNOWN")
            var scaling =
                interventions
                    .substringAfter("Scaling:", "UNSET/UNKNOWN")
                    .substringBefore(",", "UNSET/UNKNOWN")
            interventions.substringAfter("Use Angle:").substringBefore(",", "UNSET/UNKNOWN")
            var fps =
                interventions
                    .substringAfter("Fps:", "UNSET/UNKNOWN")
                    .substringBefore(",", "UNSET/UNKNOWN")

            // Applied values (what's actually set in the system)
            var appliedFps by remember { mutableStateOf(fps.toFloatOrNull() ?: 0f) }
            var appliedDownscale by remember {
                mutableStateOf(scaling.toFloatOrNull().takeIf { (it ?: 1f) >= 0f } ?: 1f)
            }

            // Check actual ANGLE settings from global settings
            var angleEnabled by remember { mutableStateOf(false) }

            // Function to check ANGLE state
            suspend fun checkAngleState() {
                if (privilegedHelper == null) return
                
                val pkgsResult = privilegedHelper.executeCommand("settings get global angle_gl_driver_selection_pkgs")
                val currentPkgs = pkgsResult.output.trim()
                
                val valuesResult = privilegedHelper.executeCommand("settings get global angle_gl_driver_selection_values")
                val currentValues = valuesResult.output.trim()

                angleEnabled =
                    if (
                        currentPkgs != "null" &&
                            currentPkgs.isNotEmpty() &&
                            currentValues != "null" &&
                            currentValues.isNotEmpty()
                    ) {
                        val pkgList = currentPkgs.split(",").map { it.trim() }
                        val valueList = currentValues.split(",").map { it.trim() }

                        val pkgIndex = pkgList.indexOf(packageName)
                        if (pkgIndex >= 0 && pkgIndex < valueList.size) {
                            valueList[pkgIndex] == "angle"
                        } else {
                            false
                        }
                    } else {
                        false
                    }
            }

            // Check ANGLE state when controls are expanded
            LaunchedEffect(expanded, interventions) {
                if (expanded) {
                    checkAngleState()
                }
            }

            // Current slider values (what user is currently adjusting)
            var currentFps by remember {
                mutableFloatStateOf(if (appliedFps > 0f) appliedFps else 60f)
            }
            var currentDownscale by remember { mutableFloatStateOf(appliedDownscale) }

            // Update all states when interventions change
            LaunchedEffect(interventions) {
                val newFps = fps.toFloatOrNull() ?: 0f
                val newDownscale = scaling.toFloatOrNull().takeIf { (it ?: 1f) >= 0f } ?: 1f

                appliedFps = newFps
                appliedDownscale = newDownscale

                // Only update current values if not currently being dragged
                // For FPS: if it becomes set, use that value; if unset, use 60 as default
                currentFps = if (newFps > 0f) newFps else 60f
                currentDownscale = newDownscale
            }

            Column(
                Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(8.dp)
            ) {

                // System Override Warning
                var systemOverride by remember { mutableStateOf<String?>(null) }
                var overrideCheckFailed by remember { mutableStateOf(false) }

                suspend fun checkSystemOverride() {
                    // Check standard device_config game_overlay with root
                    val standardResult = if (privilegedHelper != null) {
                        try {
                            val result = privilegedHelper.executeCommand("device_config get game_overlay $packageName")
                            result.output.trim()
                        } catch (e: Exception) {
                            ""
                        }
                    } else {
                        ""
                    }

                    // Check if any meaningful configuration exists
                    systemOverride =
                        if (standardResult != "null" && standardResult.isNotEmpty()) {
                            standardResult
                        } else {
                            null
                        }
                }

                LaunchedEffect(Unit) { checkSystemOverride() }

                if (systemOverride != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text =
                                    if (overrideCheckFailed) {
                                        "Unable to remove system override. Remove it manually through Game Space settings or similar ROM features."
                                    } else {
                                        "System override detected: $systemOverride. This app has pre-configured game settings that may cause scaling issues like incorrect fullscreen behavior. Remove from Game Space library or disable similar ROM settings."
                                    },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onError,
                                modifier = Modifier.weight(1f),
                            )

                            if (!overrideCheckFailed) {
                                Button(
                                    onClick = {
                                        // Try to delete with root privileges
                                        if (privilegedHelper != null) {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                privilegedHelper.executeCommand("device_config delete game_overlay $packageName")
                                                checkSystemOverride()
                                                if (systemOverride != null) {
                                                    overrideCheckFailed = true
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.padding(start = 8.dp),
                                ) {
                                    Text("Force Remove")
                                }
                            }
                        }
                    }
                }

                // ANGLE Control
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Use ANGLE",
                        style = MaterialTheme.typography.headlineSmallEmphasized,
                    )
                    Switch(
                        checked = angleEnabled,
                        onCheckedChange = { enabled ->
                            if (privilegedHelper == null) return@Switch
                            
                            CoroutineScope(Dispatchers.IO).launch {
                                // Ensure ANGLE debug package is set for rooted devices
                                privilegedHelper.executeCommand("settings put global angle_debug_package com.android.angle")

                                // Get current package list
                                val pkgsResult = privilegedHelper.executeCommand("settings get global angle_gl_driver_selection_pkgs")
                                val currentPkgs = pkgsResult.output.trim()
                                
                                val valuesResult = privilegedHelper.executeCommand("settings get global angle_gl_driver_selection_values")
                                val currentValues = valuesResult.output.trim()

                            val pkgList =
                                if (currentPkgs == "null" || currentPkgs.isEmpty()) {
                                    emptyList()
                                } else {
                                    currentPkgs.split(",").map { it.trim() }
                                }

                            val valueList =
                                if (currentValues == "null" || currentValues.isEmpty()) {
                                    emptyList()
                                } else {
                                    currentValues.split(",").map { it.trim() }
                                }

                            val newPkgList = mutableListOf<String>()
                            val newValueList = mutableListOf<String>()

                            // Copy existing entries except for current package
                            for (i in pkgList.indices) {
                                if (pkgList[i] != packageName) {
                                    newPkgList.add(pkgList[i])
                                    newValueList.add(
                                        if (i < valueList.size) valueList[i] else "default"
                                    )
                                }
                            }

                            // Add current package with desired setting
                            if (enabled) {
                                newPkgList.add(packageName.toString())
                                newValueList.add("angle")
                            } else {
                                newPkgList.add(packageName.toString())
                                newValueList.add("native")
                            }

                            // Set the new lists
                            if (newPkgList.isNotEmpty() && privilegedHelper != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    privilegedHelper.executeCommand(
                                        "settings put global angle_gl_driver_selection_pkgs ${newPkgList.joinToString(",")}"
                                    )
                                    privilegedHelper.executeCommand(
                                        "settings put global angle_gl_driver_selection_values ${newValueList.joinToString(",")}"
                                    )
                                    // Refresh ANGLE state to verify the change
                                    checkAngleState()
                                }
                            }

                            callback()
                            }
                        },
                    )
                }

                var showDownscaleInfo by remember { mutableStateOf(false) }
                // manual input dialog with textfield for downscale
                if (showDownscaleInfo) {
                    Dialog(
                        onDismissRequest = { showDownscaleInfo = false },
                        properties =
                            DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true,
                            ),
                    ) {
                        Column(
                            modifier =
                                Modifier.padding(16.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.large)
                                    .padding(16.dp)
                        ) {
                            Text(
                                text =
                                    "Enter downscale factor manually. It can be either less or greater than 1. Greater values result in higher than native resolution (oversampling). Please note that some apps may opt-out of these interventions.",
                                modifier = Modifier.padding(bottom = 8.dp),
                            )
                            var temp by remember { mutableStateOf(appliedDownscale) }
                            OutlinedTextField(
                                value = temp.toString(),
                                onValueChange = { temp = it.toFloatOrNull() ?: appliedDownscale },
                                label = { Text("Downscale") },
                                keyboardOptions =
                                    KeyboardOptions(keyboardType = KeyboardType.Number),
                                keyboardActions =
                                    KeyboardActions(onDone = { showDownscaleInfo = false }),
                                singleLine = true,
                                placeholder = { Text("0.5") },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            if (privilegedHelper != null) {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    privilegedHelper.executeCommand("cmd game set --downscale $temp $packageName")
                                                    currentDownscale = temp
                                                    appliedDownscale = temp
                                                    callback()
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Save",
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                },
                            )
                            AnimatedVisibility(temp > 2f) {
                                Text(
                                    "You sure about that?",
                                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp).background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                                        MaterialTheme.shapes.large).padding(16.dp)
                                )
                            }
                        }
                    }
                }
                Row {
                    Text(
                        text = "Scaling: $appliedDownscale",
                        style = MaterialTheme.typography.headlineSmallEmphasized,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier =
                            Modifier.size(24.dp).clickable(onClick = { showDownscaleInfo = true }),
                    )
                }

                var showFpsInfo by remember { mutableStateOf(false) }
                // manual input dialog with textfield for FPS
                if (showFpsInfo) {
                    Dialog(
                        onDismissRequest = { showFpsInfo = false },
                        properties =
                            DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true,
                            ),
                    ) {
                        Column(
                            modifier =
                                Modifier.padding(16.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.large)
                                    .padding(16.dp)
                        ) {
                            Text(
                                text =
                                    "Enter FPS value manually. This will limit the app's frame rate to the specified value. Please note that not all apps/roms support this feature.",
                                modifier = Modifier.padding(bottom = 8.dp),
                            )
                            var temp by remember {
                                mutableStateOf(if (appliedFps > 0f) appliedFps.toInt() else 60)
                            }
                            OutlinedTextField(
                                value = temp.toString(),
                                onValueChange = {
                                    val input = it.substringBefore('.') // Remove fractional part
                                    temp = input.toIntOrNull() ?: temp
                                },
                                label = { Text("FPS") },
                                keyboardOptions =
                                    KeyboardOptions(keyboardType = KeyboardType.Number),
                                keyboardActions = KeyboardActions(onDone = { showFpsInfo = false }),
                                singleLine = true,
                                placeholder = { Text("60") },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            if (privilegedHelper != null) {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    privilegedHelper.executeCommand("cmd game set --fps $temp $packageName")
                                                    currentFps = temp.toFloat()
                                                    appliedFps = temp.toFloat()
                                                    callback()
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Save",
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                },
                            )
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                ) {
                    val downscaleValues = listOf(
                        0.5f to "0.5×",
                        0.33f to "0.33×", 
                        0.25f to "0.25×",
                        0.2f to "0.2×",
                        0.1f to "0.1×",
                        1.0f to "Reset"
                    )
                    
                    downscaleValues.forEach { (value, label) ->
                        Button(
                            contentPadding = PaddingValues(0.dp),
                            onClick = {
                                if (privilegedHelper != null) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        privilegedHelper.executeCommand("cmd game set --downscale $value $packageName")
                                        currentDownscale = value
                                        appliedDownscale = value
                                        callback()
                                    }
                                }
                            },
                        ) {
                            Text(text = label)
                        }
                    }
                }
                Row {
                    Text(
                        text =
                            "FPS: ${
                            if (appliedFps <= 0f) {
                                "UNSET/UNKNOWN"
                            } else {
                                "${appliedFps.roundToInt()}"
                            }
                        }",
                        style = MaterialTheme.typography.headlineSmallEmphasized,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Edit FPS",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp).clickable(onClick = { showFpsInfo = true }),
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                ) {
                    val fpsValues = listOf(30, 40, 60, 90, 120)
                    
                    fpsValues.forEach { fps ->
                        Button(
                            contentPadding = PaddingValues(0.dp),
                            onClick = {
                                if (privilegedHelper != null) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        privilegedHelper.executeCommand("cmd game set --fps $fps $packageName")
                                        currentFps = fps.toFloat()
                                        appliedFps = fps.toFloat()
                                        callback()
                                    }
                                }
                            },
                        ) {
                            Text(text = fps.toString())
                        }
                    }
                    
                    Button(
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            if (privilegedHelper != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    privilegedHelper.executeCommand("cmd game reset $packageName")
                                    callback()
                                }
                            }
                        },
                    ) {
                        Text(text = "Reset")
                    }
                }

                Button(
                    onClick = {
                        if (privilegedHelper != null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                privilegedHelper.executeCommand("cmd game reset $packageName")
                                callback() // This will trigger LaunchedEffect to update all states
                            }
                        }
                    }
                ) {
                    Text(text = "Reset all")
                }
            }
        }
    }
}

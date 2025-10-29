@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class)

package pro.themed.perappdownscale

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pro.themed.perappdownscale.ui.theme.ThemedManagerTheme
import rikka.shizuku.Shizuku
import java.security.MessageDigest
import androidx.compose.runtime.collectAsState
import kotlin.math.roundToInt

class PerAppDownscaleActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var billingManager: BillingManager

    // Shizuku permission request result listener
    private val permissionResultListener =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, handle accordingly
                Toast.makeText(this, "Shizuku permission granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied
                Toast.makeText(this, "Shizuku permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    // Shizuku binder received listener
    private val binderReceivedListener =
        Shizuku.OnBinderReceivedListener {
            // Shizuku binder is available - notify about status change
            runOnUiThread {
                Toast.makeText(this, "Shizuku service available", Toast.LENGTH_SHORT).show()
            }
        }

    // Shizuku binder dead listener
    private val binderDeadListener =
        Shizuku.OnBinderDeadListener {
            // Shizuku binder died - notify about status change
            runOnUiThread {
                Toast.makeText(this, "Shizuku service disconnected", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        analytics = Firebase.analytics
        billingManager = BillingManager(this)

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

        // Check for AdFree purchases within 10 second timeout
        billingManager.checkPurchasesWithinTimeout(10000)

        setContent {
            val context = LocalContext.current
            val prefs: SharedPreferences = context.getSharedPreferences("app_tips", MODE_PRIVATE)

            // Tips data
            data class Tip(val id: String, val text: String)
            val tips =
                listOf(
                    Tip("app_launch_tip", "💡 Tap any app icon to launch it directly"),
                    Tip(
                        "incompatibility_tip",
                        "⚠️ If your device's ROM/OS has built-in game optimization features like Game Space, it is recommended to disable them altogether or remove selected games to avoid conflicts that might result in broken scaling or resetting parameters.\nIf your device seem to not care about applied settings please report it to the support group.",
                    ),
                )

            // Get active tips (not dismissed) - reactive to changes
            var dismissedTips by remember{ mutableStateOf(setOf<String>()) }
            val activeTips =
                remember (dismissedTips) {
                    tips.filter { tip ->
                        !prefs.getBoolean("${tip.id}_dismissed", false) &&
                            !dismissedTips.contains(tip.id)
                    }
                }

            @Composable
            fun TipBanner(tip: Tip) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
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
                var showModeDialog by rememberSaveable { mutableStateOf(false) }
                var showSettingsDialog by rememberSaveable { mutableStateOf(false) }
                var showCommandModeDialog by rememberSaveable { mutableStateOf(false) }
                var showAdFreeTipDialog by rememberSaveable { mutableStateOf(false) }
                var shizukuAvailable by rememberSaveable { mutableStateOf(false) }
                var shizukuInstalled by rememberSaveable { mutableStateOf(false) }
                var rootAvailable by rememberSaveable { mutableStateOf(false) }
                var rootPermissionDenied by rememberSaveable { mutableStateOf(false) }
                var isFirebaseContributor by rememberSaveable { mutableStateOf(false) }
                val lifecycleOwner = LocalLifecycleOwner.current
                val coroutineScope = rememberCoroutineScope()
                
                // Command mode state
                val commandModePrefs = context.getSharedPreferences("command_mode_prefs", MODE_PRIVATE)
                var currentCommandMode by rememberSaveable {
                    mutableStateOf(
                        when (commandModePrefs.getInt("command_mode", if (android.os.Build.VERSION.SDK_INT != 33) 0 else 1)) {
                            0 -> CommandMode.DEFAULT
                            1 -> CommandMode.ALTERNATIVE
                            else -> CommandMode.DEFAULT
                        }
                    )
                }

                // Function to check availability status
                suspend fun checkAvailabilityStatus(skipRootIfDenied: Boolean = false) {
                    val status = privilegedHelper.checkAvailability(context, skipRootIfDenied)
                    shizukuInstalled = status.shizukuInstalled
                    shizukuAvailable = status.shizukuAvailable && status.shizukuPermissionGranted
                    rootAvailable = status.rootAvailable
                    rootPermissionDenied = status.rootPermissionDenied

                    // Check if current execution mode is no longer valid
                    val sharedPref = context.getSharedPreferences("my_preferences", MODE_PRIVATE)
                    val currentSavedMode = sharedPref.getInt("execution_mode", -1)

                    // If Shizuku was the saved mode but is no longer available, show mode dialog
                    if (
                        currentSavedMode == 1 &&
                            !(status.shizukuAvailable && status.shizukuPermissionGranted)
                    ) {
                        showModeDialog = true
                        // Show notification about why dialog appeared
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                    context,
                                    "Shizuku needs reconfiguration - please restart service or grant permission",
                                    Toast.LENGTH_LONG,
                                )
                                .show()
                        }
                    }

                    // If root was the saved mode but permission is denied, show mode dialog
                    if (currentSavedMode == 0 && status.rootPermissionDenied) {
                        showModeDialog = true
                    }
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

                    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                }

                // Startup and periodic status checking
                LaunchedEffect(Unit) {
                    // Initial startup check
                    val sharedPref = context.getSharedPreferences("my_preferences", MODE_PRIVATE)
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
                        val mode =
                            when (savedMode) {
                                0 -> PrivilegedCommandHelper.ExecutionMode.ROOT
                                1 -> PrivilegedCommandHelper.ExecutionMode.SHIZUKU
                                else -> PrivilegedCommandHelper.ExecutionMode.NONE
                            }
                        privilegedHelper.setExecutionMode(mode)

                        // If saved mode is root but permission is denied, show dialog again
                        if (savedMode == 0 && status.rootPermissionDenied) {
                            showModeDialog = true
                        }

                        // If saved mode is Shizuku but it's no longer available (e.g., after
                        // reboot), show dialog
                        if (
                            savedMode == 1 &&
                                !(status.shizukuAvailable && status.shizukuPermissionGranted)
                        ) {
                            showModeDialog = true
                        }
                    }

                    // Periodic status checking (every 5 seconds, only when app is active)
                    while (true) {
                        delay(5000) // Wait 5 seconds
                        // Only check if the app is in the foreground
                        if (
                            lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
                        ) {
                            checkAvailabilityStatus(skipRootIfDenied = true)
                        }
                    }
                }

                val pm = context.packageManager
                val mainIntent = Intent(Intent.ACTION_MAIN, null)
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                var filter by rememberSaveable { mutableStateOf("") }

                var resolvedInfos by remember { mutableStateOf(emptyList<ResolveInfo>()) }
                val items = remember { mutableStateMapOf<String, AppInfo>() }
                var rawGames by remember { mutableStateOf(emptyList<Pair<String, AppInfo>>()) }
                var rawApps by remember { mutableStateOf(emptyList<Pair<String, AppInfo>>()) }
                var finishedLoading by remember { mutableStateOf(false) }
                var dumpsysGameData by rememberSaveable { mutableStateOf("") }
                var gamespaceGameList by remember { mutableStateOf<String?>(null) }
                
                // Helper function to check GameSpace game list
                suspend fun checkGameSpaceList(): String? {
                    return if (privilegedHelper.currentMode != PrivilegedCommandHelper.ExecutionMode.NONE) {
                        try {
                            val result = privilegedHelper.executeCommand("settings get system gamespace_game_list")
                            val output = result.output.trim()
                            if (output != "null" && output.isNotEmpty()) output else null
                        } catch (e: Exception) {
                            null
                        }
                    } else null
                }
                
                // Helper function to refresh dumpsys game
                suspend fun refreshDumpsysGame(): String {
                    return if (privilegedHelper.currentMode != PrivilegedCommandHelper.ExecutionMode.NONE) {
                        try {
                            val result = privilegedHelper.executeCommand("dumpsys game")
                            result.output
                        } catch (e: Exception) {
                            ""
                        }
                    } else ""
                }
                
                // Helper function to check if package is in dumpsys game
                fun isPackageInDumpsysGame(packageName: String, dumpsys: String): Boolean {
                    return dumpsys.contains(packageName)
                }

                LaunchedEffect(finishedLoading) {
                    val (games, apps) = items.toList().partition { (packageName, _) ->
                        // Check if has game category
                        val resolveInfo = resolvedInfos.find { it.activityInfo.packageName == packageName }
                        val isGameCategory = resolveInfo?.activityInfo?.applicationInfo?.category == android.content.pm.ApplicationInfo.CATEGORY_GAME
                        
                        // Or contained in manual override list
                        val inGamesList = gamesList.any { packageName.contains(it, ignoreCase = true) }
                        
                        isGameCategory || inGamesList
                    }
                    rawGames = games
                    rawApps = apps
                }

                val gameslist = remember(rawGames, filter) {
                    rawGames.filter {
                        filter.isBlank() ||
                                it.second.label.contains(filter, ignoreCase = true) ||
                                it.first.contains(filter, ignoreCase = true)
                    }.sortedBy { it.second.label }
                }

                val appslist = remember(rawApps, filter) {
                    rawApps.filter {
                        filter.isBlank() ||
                                it.second.label.contains(filter, ignoreCase = true) ||
                                it.first.contains(filter, ignoreCase = true)
                    }.sortedBy { it.second.label }
                }
                LaunchedEffect(key1 = Unit, showModeDialog) {
                    withContext(Dispatchers.IO) {
                        resolvedInfos =
                            resolvedInfos.ifEmpty {
                                pm.queryIntentActivities(
                                    mainIntent,
                                    PackageManager.ResolveInfoFlags.of(0L),
                                )
                            }
                        dumpsysGameData = refreshDumpsysGame()
                        gamespaceGameList = checkGameSpaceList()

                        resolvedInfos.forEach { info ->
                            val packageName = info.activityInfo.packageName
                            val intervention =
                                dumpsysGameData.split("\n").firstOrNull { it.contains(packageName) }
                                    ?: ""

                            items.put(
                                packageName,
                                AppInfo(info.loadLabel(pm).toString(), intervention),
                            )
                        }
                        finishedLoading = true
                    }
                }
                val pullRefreshState = PullToRefreshState()

                val refreshScope = rememberCoroutineScope()
                var isRefreshing by rememberSaveable { mutableStateOf(false) }
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
                            dumpsysGameData = refreshDumpsysGame()
                            gamespaceGameList = checkGameSpaceList()
                            
                            for (info in resolvedInfos) {
                                val packageName = info.activityInfo.packageName
                                val intervention =
                                    dumpsysGameData.split("\n").firstOrNull {
                                        it.contains(packageName)
                                    } ?: ""

                                items.put(
                                    packageName,
                                    AppInfo(info.loadLabel(pm).toString(), intervention),
                                )
                            }
                        }
                        finishedLoading = true
                        isRefreshing = false
                    }

                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {
                        var selectedIndex by rememberSaveable { mutableStateOf(0) }
                        val tabs = listOf("Games", "Apps")
                        val clipboardManager: ClipboardManager = LocalClipboardManager.current

                        // Topbar with title and settings icon
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Status indicator
                            Column(
                                modifier =
                                    Modifier
                                        .size(48.dp)
                                        .clickable {
                                            val statusMessage = buildString {
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
                                                    append(
                                                        "⚙ Shizuku Installed (needs configuration)\n"
                                                    )
                                                } else {
                                                    append("✗ Shizuku Not Installed\n")
                                                }
                                                if (!rootAvailable && !shizukuAvailable) {
                                                    append("⚠ No privileged access ready")
                                                }
                                            }
                                            Toast.makeText(context, statusMessage, Toast.LENGTH_LONG)
                                                .show()
                                        },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                // Root status
                                Text(
                                    text = when {
                                        rootAvailable -> "R: ✅"
                                        rootPermissionDenied -> "R: ⛔"
                                        else -> "R: ❌"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                
                                // Shizuku status
                                Text(
                                    text = when {
                                        shizukuAvailable -> "S: ✅"
                                        shizukuInstalled -> "S: ⚙️"
                                        else -> "S: ❌"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }

                            // Title
                            Text(
                                text = "TMPAD",
                                style = MaterialTheme.typography.headlineLargeEmphasized,
                                modifier =
                                    Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            if (privilegedHelper != null) {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    val result =
                                                        privilegedHelper.executeCommand(
                                                            """getprop | grep '\[ro\.serialno\]' | sed 's/.*\[\(.*\)\]/\1/' | md5sum -b"""
                                                        )
                                                    clipboardManager.setText(
                                                        AnnotatedString(result.output)
                                                    )
                                                }
                                            }
                                        }
                                        .padding(4.dp),
                            )

                            // Settings icon
                            IconButton(
                                onClick = { showSettingsDialog = true },
                                modifier = Modifier.size(48.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onSurface,
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
                        val density = LocalDensity.current

                        val visibleList = if (selectedIndex == 0) gameslist else appslist
                        fun categoryToName(cat: Int): String {
                            return when (cat) {
                                android.content.pm.ApplicationInfo.CATEGORY_GAME -> "game"
                                android.content.pm.ApplicationInfo.CATEGORY_AUDIO -> "audio"
                                android.content.pm.ApplicationInfo.CATEGORY_VIDEO -> "video"
                                android.content.pm.ApplicationInfo.CATEGORY_IMAGE -> "image"
                                android.content.pm.ApplicationInfo.CATEGORY_SOCIAL -> "social"
                                android.content.pm.ApplicationInfo.CATEGORY_NEWS -> "news"
                                android.content.pm.ApplicationInfo.CATEGORY_MAPS -> "maps"
                                android.content.pm.ApplicationInfo.CATEGORY_PRODUCTIVITY ->
                                    "productivity"
                                android.content.pm.ApplicationInfo.CATEGORY_ACCESSIBILITY ->
                                    "accessibility"
                                else -> "unknown"
                            }
                        }
                        fun String.sha256() =
                            MessageDigest.getInstance("SHA-256").digest(toByteArray()).toHexString()

                        LaunchedEffect(finishedLoading) {
                            val prefs = getSharedPreferences("my_preferences", MODE_PRIVATE)

                            val database =
                                FirebaseDatabase.getInstance(
                                    "https://themed-manager-default-rtdb.europe-west1.firebasedatabase.app"
                                )
                            if (
                                resolvedInfos.isNotEmpty() &&
                                    finishedLoading &&
                                    System.currentTimeMillis() - 86400000 >
                                        prefs.getLong("last_update", 0)
                            ) {
                                resolvedInfos.forEach {
                                    val categoryInt = it.activityInfo.applicationInfo.category
                                    val categoryName = categoryToName(categoryInt)
                                    val packageName = it.activityInfo.packageName
                                    val label = it.activityInfo.loadLabel(pm).toString()
                                    val ref =
                                        database
                                            .getReference("packages")
                                            .child(packageName.sha256().toString())
                                    ref.setValue(
                                        hashMapOf(
                                            "packageName" to packageName,
                                            "label" to label,
                                            "category" to categoryName,
                                        )
                                    )
                                    Log.d(
                                        "category",
                                        "$packageName ($categoryInt) -> $categoryName",
                                    )
                                }
                                prefs.edit { putLong("last_update", System.currentTimeMillis()) }
                            }
                        }

                        RefreshIndicator(state = pullRefreshState)
                        LazyColumn(
                            state = rememberLazyListState(
                                cacheWindow = LazyLayoutCacheWindow(1f)
                            ),
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .pullToRefresh(
                                        isRefreshing,
                                        pullRefreshState,
                                        threshold = 36.dp,
                                        enabled = !isRefreshing,
                                        onRefresh = { triggerRefresh() },
                                    )
                        ) {
                            item {
                                AnimatedVisibility(visible = !finishedLoading) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier =
                                            Modifier
                                                .padding(start = 8.dp, end = 8.dp, top = 8.dp)
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
                                        Text(text = stringResource(R.string.loading_list))
                                    }
                                }
                            }

                            item {

                            AnimatedVisibility(visible = visibleList.isEmpty() && finishedLoading) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(16.dp),
                                    ) {
                                        Column(Modifier.padding(8.dp)) {
                                            Text(
                                                text =
                                                    if (selectedIndex == 0) stringResource(R.string.no_known_games_found)
                                                    else stringResource(R.string.no_known_apps_found),
                                                fontSize = 24.sp,
                                            )
                                            Text(
                                                text =
                                                    if (selectedIndex == 0)
                                                        stringResource(R.string.check_out_apps_tab_tho)
                                                    else stringResource(R.string.check_out_games_tab_tho),
                                                fontSize = 16.sp,
                                            )
                                        }
                                    }
                                }
                            }
                            item("filter") {
                                Row {
                                    OutlinedTextField(
                                        value = filter,
                                        onValueChange = { filter = it },
                                        label = { Text("Filter") },
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp),
                                    )
                                }
                            }
                            // Show all active tips
                            items(activeTips) { tip -> TipBanner(tip) }
                            
                            // GameSpace game list warning
                            item("gamespace_warning") {
                                AnimatedVisibility(visible = gamespaceGameList != null && finishedLoading) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
                                        shape = RoundedCornerShape(12.dp),
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = stringResource(R.string.gamespace_conflict_detected),
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                            Text(
                                                text = stringResource(R.string.gamespacewarning),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                            )
                                            Button(
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.error,
                                                    contentColor = MaterialTheme.colorScheme.onError,
                                                ),
                                                onClick = {
                                                    if (privilegedHelper != null) {
                                                        CoroutineScope(Dispatchers.IO).launch {
                                                            privilegedHelper.executeCommand(
                                                                "settings delete system gamespace_game_list"
                                                            )
                                                            gamespaceGameList = checkGameSpaceList()
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .align(Alignment.End)
                                                    .padding(top = 8.dp),
                                            ) {
                                                Text("Clear GameSpace List")
                                            }
                                        }
                                    }
                                }
                            }
                            
                            item("a13_warning") {
                            // A13 Warning Banner
                            AnimatedVisibility(visible = android.os.Build.VERSION.SDK_INT == 33 && finishedLoading) {

                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
                                        shape = RoundedCornerShape(12.dp),
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = stringResource(R.string.a13warning),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                            )
                                            if (currentCommandMode == CommandMode.DEFAULT) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = stringResource(R.string.android_13_usually_doesn_t_work_with_default_mode_switch_to_alternative_mode_in_settings),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            item("alternative_mode_warning") {

                            // Alternative Mode Warning Banner
                            AnimatedVisibility(visible = currentCommandMode == CommandMode.ALTERNATIVE && finishedLoading) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.9f),
                                        shape = RoundedCornerShape(12.dp),
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = stringResource(R.string.alternative_mode_is_active_only_apps_with_game_category_0_in_manifest_are_supported_by_system),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                            )
                                        }
                                    }
                                }
                            }
                            itemsIndexed(items = visibleList, key = { _, app -> app.first }) {
                                index,
                                app ->
                                Box(Modifier) { 
                                    Item(
                                        index = index, 
                                        app = app, 
                                        privilegedHelper = privilegedHelper, 
                                        pm = pm, 
                                        density = density,
                                        commandMode = currentCommandMode,
                                        dumpsysGame = dumpsysGameData,
                                        onRefreshNeeded = { triggerRefresh() }
                                    ) 
                                }
                            }
                            if (selectedIndex == 0) {
                                item {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(16.dp),
                                    ) {
                                        Column(Modifier.padding(8.dp)) {
                                            Text(text = stringResource(R.string.something_missing), fontSize = 24.sp)
                                            Text(
                                                text =
                                                    stringResource(R.string.if_a_game_isn_t_filtered_to_this_tab_report_it_to_support_group_at_t_me_themedsupport),
                                                fontSize = 16.sp,
                                            )
                                        }
                                    }
                                }
                                item {
                                    LinkButtons(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 72.dp, vertical = 8.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.primaryContainer,
                                                    CircleShape,
                                                )
                                    )
                                }
                            }
                        }
                        LaunchedEffect(Unit) {
                            FirebaseIsContributor(context, privilegedHelper) { isContributor, date ->
                                isFirebaseContributor = isContributor
                                Log.d("PerAppDownscaleActivity", "Firebase contributor status updated: $isContributor")
                            }
                        }

                        // Show ads only if user is not a contributor (neither Firebase nor AdFree purchase)
                        val purchaseState by billingManager.purchaseState.collectAsState()
                        if (!purchaseState.isAdFreePurchased && !isFirebaseContributor) {
                            Row { AdmobBanner(context) }
                        }
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
                        helper = privilegedHelper,
                    )
                }

                // Settings Dialog
                if (showSettingsDialog) {
                    SettingsDialog(
                        onDismiss = { showSettingsDialog = false },
                        onChangeModeClicked = {
                            showSettingsDialog = false
                            showModeDialog = true
                        },
                        onCommandModeClicked = {
                            showSettingsDialog = false
                            showCommandModeDialog = true
                        },
                        onAdFreeTipClicked = {
                            showSettingsDialog = false
                            showAdFreeTipDialog = true
                        }
                    )
                }
                
                // Command Mode Selection Dialog
                if (showCommandModeDialog) {
                    CommandModeSelectionDialog(
                        currentMode = currentCommandMode,
                        onDismiss = { showCommandModeDialog = false },
                        onModeSelected = { newMode ->
                            currentCommandMode = newMode
                            commandModePrefs.edit().putInt(
                                "command_mode",
                                when (newMode) {
                                    CommandMode.DEFAULT -> 0
                                    CommandMode.ALTERNATIVE -> 1
                                }
                            ).apply()
                        }
                    )
                }

                // AdFree/Tip Dialog
                if (showAdFreeTipDialog) {
                    AdFreeTipDialog(
                        onDismiss = { showAdFreeTipDialog = false },
                        billingManager = billingManager,
                        activity = this@PerAppDownscaleActivity
                    )
                }
            }
            ThemedManagerTheme { PerAppDownscale() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove Shizuku listeners to prevent memory leaks
        Shizuku.removeRequestPermissionResultListener(permissionResultListener)
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeBinderDeadListener(binderDeadListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Handle regular Android permissions here if needed
        // Shizuku permissions are handled by the permissionResultListener above
    }
}

data class AppInfo(val label: String, val interventions: String)

@OptIn()
@Composable
fun RefreshIndicator(state: PullToRefreshState) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
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
                    state.distanceFraction < 1f -> "Pull to refresh ⤵️"
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

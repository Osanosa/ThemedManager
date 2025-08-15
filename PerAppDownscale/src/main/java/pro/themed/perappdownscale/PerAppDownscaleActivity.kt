@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class)

package pro.themed.perappdownscale

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
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pro.themed.perappdownscale.ui.theme.ThemedManagerTheme
import kotlin.math.roundToInt

class PerAppDownscaleActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics

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
        enableEdgeToEdge()
        setContent {
            var noRoot by remember { mutableStateOf(false) }
            val context = LocalContext.current
            val prefs: SharedPreferences =
                context.getSharedPreferences("app_tips", MODE_PRIVATE)
            var showTip by remember {
                mutableStateOf(!prefs.getBoolean("tip_dismissed", false))
            }
            @Composable
            fun TipBanner() {



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
                                text = "💡 Tip: Tap any app icon to launch it directly",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(
                                onClick = {
                                    showTip = false
                                    prefs.edit().putBoolean("tip_dismissed", true).apply()
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
                val displayMetrics = context.resources.displayMetrics
                val shell = Shell.SH
                shell.run("su")
                shell.addOnStderrLineListener(
                    object : Shell.OnLineListener {
                        override fun onLine(line: String) {
                            CoroutineScope(Dispatchers.Main).launch {
                                when {
                                    line.contains(
                                        "su: inaccessible or not found",
                                        ignoreCase = true,
                                    ) || line.contains("permission denied", ignoreCase = true) -> {
                                        noRoot = true
                                    }
                                    line.contains("service stopped", ignoreCase = true) -> {
                                        Toast.makeText(context, line, Toast.LENGTH_SHORT).show()
                                    }
                                    line.contains("not stopped", ignoreCase = true) -> {
                                        Toast.makeText(context, line, Toast.LENGTH_SHORT).show()
                                    }
                                    else -> shareStackTrace(line)
                                }
                            }
                        }
                    }
                )
                shell.addOnStdoutLineListener(
                    object : Shell.OnLineListener {
                        override fun onLine(line: String) {
                            CoroutineScope(Dispatchers.Main).launch {
                                if (line.contains("set")) // showInterstitial(context) {}
                                // line.log()
                                if (line.contains("not supported"))
                                        Toast.makeText(context, line, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )

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
                            
                            items.putIfAbsent(
                                packageName,
                                AppInfo(
                                    it.loadLabel(pm).toString(),
                                    appIcon,
                                    shell.run("cmd game list-configs $packageName").stdout(),
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

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Text(
                                text = "TMPAD",
                                style = MaterialTheme.typography.headlineLarge,
                                modifier =
                                    Modifier.clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            clipboardManager.setText(
                                                AnnotatedString(
                                                    Shell.SH.run(
                                                            """su -c getprop | grep '\[ro\.serialno\]' | sed 's/.*\[\(.*\)\]/\1/' | md5sum -b"""
                                                        )
                                                        .stdout()
                                                )
                                            )
                                        }
                                        .padding(4.dp),
                            )
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
                                    resolvedInfos.forEach {
                                        val packageName = it.activityInfo.packageName
                                        val appIcon = it.loadIcon(pm)
                                        
                                        // Example: Convert app icon to fixed dp bitmap during refresh
                                        // val iconBitmap = AppIconUtils.getAppIconBitmap(appIcon, displayMetrics)
                                        
                                        items.putIfAbsent(
                                            packageName,
                                            AppInfo(
                                                it.loadLabel(pm).toString(),
                                                appIcon,
                                                shell
                                                    .run("cmd game list-configs $packageName")
                                                    .stdout(),
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
                            if (showTip)  item { TipBanner() }
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
                                Box(Modifier) { Item(index, app, shell) }
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
            }

            ThemedManagerTheme {
                if (noRoot)
                    Surface(
                        modifier =
                            Modifier.padding(32.dp)
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets.safeDrawing),
                        color = MaterialTheme.colorScheme.error,
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text(
                            "Root access required",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                else PerAppDownscale()
            }
        }
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
                    state.distanceFraction < 1.5f -> "Release to refresh"
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
fun Item(index: Int, app: Pair<String, AppInfo>, shell: Shell) {

    var interventions by rememberSaveable { mutableStateOf(app.second.interventions) }
    var availableModes by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = app.first) {
        if (interventions.isEmpty())
            interventions = shell.run("cmd game list-configs ${app.first}").stdout()

        // Get available game modes
        if (availableModes.isEmpty() && !interventions.contains("not of game type")) {
            val modesResult = shell.run("cmd game list-modes ${app.first}").stdout()
            // Parse: "package current mode: custom, available game modes: [standard,custom]"
            val modesStart = modesResult.indexOf("available game modes: [")
            if (modesStart != -1) {
                val modesEnd = modesResult.indexOf("]", modesStart)
                if (modesEnd != -1) {
                    val modesString = modesResult.substring(modesStart + 23, modesEnd)
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
                interventions = shell.run("cmd game list-configs ${app.first}").stdout()
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
                ClickableIcon(app.first, app.second.drawable)
            }
            Column {
                Text(text = app.second.label)
                Text(text = app.first, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.weight(1f))
        }

        Controls(expanded, interventions, availableModes, shell, app.first) {
            interventions = shell.run("cmd game list-configs ${app.first}").stdout()
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
private fun ClickableIcon(packageName: String, icon: Drawable) {
    val context = LocalContext.current

    Image(
        painter = rememberDrawablePainter(icon),
        contentDescription = null,
        modifier =
            Modifier.size(56.dp).padding(8.dp).bounceClick().clickable {
                try {
                    // Force stop the app first, then launch it
                    val shell = Shell.SH
                    shell.run("am force-stop $packageName")

                    val intent = context.packageManager.getLaunchIntentForPackage(packageName)
                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Cannot launch $packageName", Toast.LENGTH_SHORT)
                            .show()
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
    shell: Shell,
    packageName: String?,
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
            fun checkAngleState() {
                val currentPkgs =
                    shell.run("settings get global angle_gl_driver_selection_pkgs").stdout().trim()
                val currentValues =
                    shell
                        .run("settings get global angle_gl_driver_selection_values")
                        .stdout()
                        .trim()

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

                fun checkSystemOverride() {
                    // Check standard device_config game_overlay with root
                    val standardResult =
                        shell
                            .run("su -c \"device_config get game_overlay $packageName\"")
                            .stdout()
                            .trim()

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
                                        shell.run(
                                            "su -c \"device_config delete game_overlay $packageName\""
                                        )
                                        checkSystemOverride()
                                        if (systemOverride != null) {
                                            overrideCheckFailed = true
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
                            // Ensure ANGLE debug package is set for rooted devices
                            shell.run("settings put global angle_debug_package com.android.angle")

                            // Get current package list
                            val currentPkgs =
                                shell
                                    .run("settings get global angle_gl_driver_selection_pkgs")
                                    .stdout()
                                    .trim()
                            val currentValues =
                                shell
                                    .run("settings get global angle_gl_driver_selection_values")
                                    .stdout()
                                    .trim()

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
                            if (newPkgList.isNotEmpty()) {
                                shell.run(
                                    "settings put global angle_gl_driver_selection_pkgs ${newPkgList.joinToString(",")}"
                                )
                                shell.run(
                                    "settings put global angle_gl_driver_selection_values ${newValueList.joinToString(",")}"
                                )
                            }

                            // Refresh ANGLE state to verify the change
                            checkAngleState()
                            callback()
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
                                    .background(Color.White, shape = MaterialTheme.shapes.large)
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
                                            shell.run("cmd game set --downscale $temp $packageName")
                                            currentDownscale = temp
                                            appliedDownscale = temp
                                            callback()
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
                                    .background(Color.White, shape = MaterialTheme.shapes.large)
                                    .padding(16.dp)
                        ) {
                            Text(
                                text =
                                    "Enter FPS value manually. This will limit the app's frame rate to the specified value. PLease note that not all apps/roms support this feature.",
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
                                            shell.run("cmd game set --fps $temp $packageName")
                                            currentFps = temp.toFloat()
                                            appliedFps = temp.toFloat()
                                            callback()
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
                /*
                                Box(contentAlignment = Alignment.CenterStart) {
                                    Slider(
                                        value = currentDownscale,
                                        onValueChange = { currentDownscale = ((it * 100).toInt()).toFloat() / 100 },
                                        onValueChangeFinished = {
                                            shell.run("cmd game set --downscale $currentDownscale $packageName")
                                            appliedDownscale = currentDownscale
                                            callback()
                                        },
                                        valueRange = 0f..2f,
                                        steps = 199,
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Text(
                                            text = "0×",
                                            style =
                                                MaterialTheme.typography.bodyMedium.copy(
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Black,
                                                    shadow =
                                                        Shadow(
                                                            color = MaterialTheme.colorScheme.primary,
                                                            offset = Offset(0f, 0f),
                                                            blurRadius = 8f,
                                                        ),
                                                ),
                                        )
                                        Text(
                                            text = "0.5×",
                                            style =
                                                MaterialTheme.typography.bodyMedium.copy(
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Black,
                                                    shadow =
                                                        Shadow(
                                                            color = MaterialTheme.colorScheme.primary,
                                                            offset = Offset(0f, 0f),
                                                            blurRadius = 8f,
                                                        ),
                                                ),
                                        )
                                        Text(
                                            text = "1×",
                                            style =
                                                MaterialTheme.typography.bodyMedium.copy(
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Black,
                                                    shadow =
                                                        Shadow(
                                                            color = MaterialTheme.colorScheme.primary,
                                                            offset = Offset(0f, 0f),
                                                            blurRadius = 8f,
                                                        ),
                                                ),
                                        )
                                        Text(
                                            text = "1.5×",
                                            style =
                                                MaterialTheme.typography.bodyMedium.copy(
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Black,
                                                    shadow =
                                                        Shadow(
                                                            color = MaterialTheme.colorScheme.primary,
                                                            offset = Offset(0f, 0f),
                                                            blurRadius = 8f,
                                                        ),
                                                ),
                                        )
                                        Text(
                                            text = "2×",
                                            style =
                                                MaterialTheme.typography.bodyMedium.copy(
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Black,
                                                    shadow =
                                                        Shadow(
                                                            color = MaterialTheme.colorScheme.primary,
                                                            offset = Offset(0f, 0f),
                                                            blurRadius = 8f,
                                                        ),
                                                ),
                                        )
                                    }
                                }
                */
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                ) {
                    Button(
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            shell.run("cmd game set --downscale 0.5 $packageName")
                            currentDownscale = 0.5f
                            appliedDownscale = 0.5f
                            callback()
                        },
                    ) {
                        Text(text = "0.5×")
                    }
                    Button(
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            shell.run("cmd game set --downscale 0.33 $packageName")
                            currentDownscale = 0.33f
                            appliedDownscale = 0.33f
                            callback()
                        },
                    ) {
                        Text(text = "0.33×")
                    }
                    Button(
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            shell.run("cmd game set --downscale 0.25 $packageName")
                            currentDownscale = 0.25f
                            appliedDownscale = 0.25f
                            callback()
                        },
                    ) {
                        Text(text = "0.25×")
                    }
                    Button(
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            shell.run("cmd game set --downscale 0.20 $packageName")
                            currentDownscale = 0.2f
                            appliedDownscale = 0.2f
                            callback()
                        },
                    ) {
                        Text(text = "0.2×")
                    }
                    Button(
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            shell.run("cmd game set --downscale 0.10 $packageName")
                            currentDownscale = 0.1f
                            appliedDownscale = 0.1f
                            callback()
                        },
                    ) {
                        Text(text = "0.1×")
                    }
                    Button(
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            shell.run("cmd game set --downscale 1.0 $packageName")
                            currentDownscale = 1f
                            appliedDownscale = 1f
                            callback()
                        },
                    ) {
                        Text(text = "Reset")
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
                /*
                                Slider(
                                    value = currentFps,
                                    onValueChange = { currentFps = ((it * 100).toInt()).toFloat() / 100 },
                                    onValueChangeFinished = {
                                        val fpsValue = currentFps.toInt()
                                        shell.run("cmd game set --fps $fpsValue $packageName")
                                        appliedFps = currentFps
                                        callback()
                                    },
                                    valueRange = 0f..500f,
                                    steps = 49,
                                )
                */
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                ) {
                    Button(
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            shell.run("cmd game set --fps 30 $packageName")
                            currentFps = 30f
                            appliedFps = 30f
                            callback()
                        },
                    ) {
                        Text(text = "30")
                    }
                    Button(
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            shell.run("cmd game set --fps 40 $packageName")
                            currentFps = 40f
                            appliedFps = 40f
                            callback()
                        },
                    ) {
                        Text(text = "40")
                    }
                    Button(
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            shell.run("cmd game set --fps 60 $packageName")
                            currentFps = 60f
                            appliedFps = 60f
                            callback()
                        },
                    ) {
                        Text(text = "60")
                    }
                    Button(
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            shell.run("cmd game set --fps 90 $packageName")
                            currentFps = 90f
                            appliedFps = 90f
                            callback()
                        },
                    ) {
                        Text(text = "90")
                    }
                    Button(
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            shell.run("cmd game set --fps 120 $packageName")
                            currentFps = 120f
                            appliedFps = 120f
                            callback()
                        },
                    ) {
                        Text(text = "120")
                    }
                    Button(
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            shell.run("cmd game reset $packageName")
                            callback() // This will trigger LaunchedEffect to update all states
                        },
                    ) {
                        Text(text = "Reset")
                    }
                }

                Button(
                    onClick = {
                        shell.run("cmd game reset $packageName")
                        callback() // This will trigger LaunchedEffect to update all states
                    }
                ) {
                    Text(text = "Reset all")
                }
            }
        }
    }
}

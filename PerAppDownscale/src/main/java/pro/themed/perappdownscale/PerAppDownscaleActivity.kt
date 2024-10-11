package pro.themed.perappdownscale

import android.content.*
import android.content.pm.*
import android.graphics.drawable.*
import android.os.*
import android.util.*
import android.widget.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.*
import com.google.accompanist.drawablepainter.*
import com.google.firebase.analytics.*
import com.google.firebase.analytics.ktx.*
import com.google.firebase.ktx.*
import com.jaredrummler.ktsh.*
import kotlinx.coroutines.*
import pro.themed.perappdownscale.ui.theme.*
import kotlin.Pair
import kotlin.math.*

class PerAppDownscaleActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        analytics = Firebase.analytics

        val scope = CoroutineScope(Dispatchers.Main)

        super.onCreate(savedInstanceState)

        fun shareStackTrace(stackTrace: String) {
            scope.launch {
                val intent = Intent(Intent.ACTION_SEND).apply {
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
        val suVersion by lazy {
            Shell.SH.run("su -v").stdout()
        }
        enableEdgeToEdge()
        setContent {
            var noRoot by remember { mutableStateOf(false) }



            @Composable fun PerAppDownscale(modifier: Modifier = Modifier) {

                val context = LocalContext.current
                val shell = Shell.SH
                shell.run("su")
                shell.addOnStderrLineListener(object : Shell.OnLineListener {
                    override fun onLine(line: String) {
                        CoroutineScope(Dispatchers.Main).launch {
                            CoroutineScope(Dispatchers.Main).launch {
                                when {
                                    line.contains("su: inaccessible or not found",
                                        ignoreCase = true) || line.contains("permission denied", ignoreCase = true) -> {
                                        noRoot = true
                                    }

                                    line.contains("service stopped", ignoreCase = true)                             -> {
                                        Toast.makeText(context, line, Toast.LENGTH_SHORT).show()
                                    }

                                    line.contains("not stopped", ignoreCase = true)                                 -> {
                                        Toast.makeText(context, line, Toast.LENGTH_SHORT).show()
                                    }

                                    else                                                                            -> shareStackTrace(
                                        line)
                                }
                            }

                        }
                    }
                })
                shell.addOnStdoutLineListener(object : Shell.OnLineListener {
                    override fun onLine(line: String) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (line.contains("set"))// showInterstitial(context) {}
                            //line.log()
                                if (line.contains("not supported")) Toast.makeText(context, line, Toast.LENGTH_SHORT)
                                    .show()

                        }
                    }
                })

                val pm = context.packageManager
                val mainIntent = Intent(Intent.ACTION_MAIN, null)
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                var filter by remember { mutableStateOf("") }

                var resolvedInfos by remember { mutableStateOf(emptyList<ResolveInfo>()) }

                val items = remember { mutableStateMapOf<String, AppInfo>() }
                val appslist = items.filter { it.key !in gamesList }
                    .filter { it.value.label.contains(filter, true) || it.key.contains(filter, true) }.toList()
                    .sortedBy { it.second.label }
                val gameslist = items.filter { it.key in gamesList }
                    .filter { it.value.label.contains(filter, true) || it.key.contains(filter, true) }.toList()
                    .sortedBy { it.second.label }

                var finishedLoading by remember { mutableStateOf(false) }

                LaunchedEffect(key1 = Unit) {
                    resolvedInfos = if (resolvedInfos.isEmpty()) {

                        pm.queryIntentActivities(mainIntent, PackageManager.ResolveInfoFlags.of(0L))
                    }
                    else {
                        resolvedInfos
                    }

                    resolvedInfos.forEach {
                        val packageName = it.activityInfo.packageName
                        items.putIfAbsent(packageName,
                            AppInfo(it.loadLabel(pm).toString(),
                                it.loadIcon(pm),
                                shell.run("cmd game list-configs $packageName").stdout()))
                    }
                    finishedLoading = true
                }



                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {

                        val pagerState = rememberPagerState(0, 0f) { 2 }
                        var selectedIndex by remember { mutableStateOf(0) }
                        val tabs = listOf("Games", "Apps")
                        LaunchedEffect(selectedIndex) {
                            pagerState.animateScrollToPage(selectedIndex)
                        }
                        val clipboardManager: ClipboardManager = LocalClipboardManager.current

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Text(text = "TMPAD", style = MaterialTheme.typography.headlineLarge,

                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(Shell.SH
                                            .run("""su -c getprop | grep '\[ro\.serialno\]' | sed 's/.*\[\(.*\)\]/\1/' | md5sum -b""")
                                            .stdout()))
                                    }
                                    .padding(4.dp)

                            )
                        }
                        TabRow(
                            selectedTabIndex = selectedIndex,
                        ) {
                            tabs.forEachIndexed { index, title ->

                                Tab(selectedContentColor = MaterialTheme.colorScheme.primary,
                                    unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            BadgedBox(badge = {

                                                if (index == 0) {
                                                    Badge {
                                                        Text(text = gameslist.size.toString())
                                                    }
                                                }
                                                else {
                                                    Badge {
                                                        Text(text = appslist.size.toString())
                                                    }
                                                }
                                            }) {
                                                Icon(painter = painterResource(id = if (index == 0) R.drawable.videogame_asset_24px else R.drawable.apps_24px),
                                                    contentDescription = null,
                                                    modifier = Modifier.padding(8.dp))
                                            }
                                            Spacer(Modifier.width(16.dp))

                                            Text(title)
                                        }

                                    },
                                    selected = selectedIndex == index,
                                    onClick = {

                                        selectedIndex = index
                                    })
                            }
                        }

                        if (!finishedLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Row {
                                    CircularProgressIndicator()

                                    Text(text = "Loading...")
                                }
                            }
                        }
                        else HorizontalPager(pagerState, modifier = Modifier.weight(1f)) {
                            when (it) {

                                0 -> {
                                    LaunchedEffect(it) { selectedIndex = it }

                                    LazyColumn(Modifier.fillMaxSize()) {
                                        if (gameslist.isEmpty()) {
                                            item {
                                                //big card with big bold text and arrow left
                                                Surface(modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                    color = MaterialTheme.colorScheme.primary,
                                                    shape = RoundedCornerShape(16.dp)) {
                                                    Column(Modifier.padding(8.dp)) {
                                                        Text(text = "No games found", fontSize = 32.sp)
                                                        Text(text = "Check out apps tab tho →", fontSize = 16.sp)
                                                    }

                                                }

                                            }
                                        }
                                        item {
                                            Row() {
                                                OutlinedTextField(value = filter,
                                                    onValueChange = { filter = it },
                                                    label = { Text("Filter") },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(8.dp))
                                            }

                                        }
                                        gameslist.forEachIndexed { index, app ->
                                            item {
                                                Item(index, app, shell)
                                            }
                                        }
                                    }
                                }
                                1 -> {
                                    LaunchedEffect(it) { selectedIndex = it }
                                    LazyColumn(Modifier.fillMaxSize()) {
                                        if (appslist.isEmpty()) {
                                            item {

                                                Surface(modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                    color = MaterialTheme.colorScheme.primary,
                                                    shape = RoundedCornerShape(16.dp)) {
                                                    Column(Modifier.padding(8.dp)) {
                                                        Text(text = "No apps found", fontSize = 32.sp)
                                                        Text(text = "Check out games tab tho ↩", fontSize = 16.sp)
                                                    }

                                                }

                                            }
                                        }
                                        item {
                                            Row() {
                                                OutlinedTextField(value = filter,
                                                    onValueChange = { filter = it },
                                                    label = { Text("Filter") },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(8.dp))
                                            }

                                        }
                                        appslist.forEachIndexed { index, app ->
                                            item {
                                                Item(index, app, shell)
                                            }
                                        }
                                    }
                                }
                            }

                        }
                        Row(
                        ) {

                            AdmobBanner(context)
                        }
                    }
                }
            }

            ThemedManagerTheme {
                if (noRoot) Surface(modifier = Modifier.padding(32.dp).fillMaxWidth().windowInsetsPadding(WindowInsets.safeDrawing),
                    color = MaterialTheme.colorScheme.error,
                    shape = MaterialTheme.shapes.medium) {
                    Text("Root access required",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp))
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

data class AppInfo(
    val label: String,
    val drawable: Drawable,
    val interventions: String,
)

@Composable fun Item(index: Int, app: Pair<String, AppInfo>, shell: Shell) {

    var interventions by rememberSaveable {
        mutableStateOf(app.second.interventions)
    }
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = app.first) {
        if (interventions.isEmpty()) interventions = shell.run("cmd game list-configs ${app.first}").stdout()
    }
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(color = if (index % 2 == 0) Color.LightGray.copy(alpha = 0.1f) else Color.Transparent)
        .background(color = if (interventions.contains("Name")) Color.Yellow.copy(alpha = 0.1f) else Color.Transparent)) {

        Row(Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically) {

            Icon(app.second.drawable)
            Column {

                Text(text = app.second.label)
                Text(text = app.first, maxLines = 1)
            }
            Spacer(Modifier.weight(1f))
            if (interventions.contains("Name")) Icon(imageVector = Icons.Filled.AddCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(4.dp)
                    .size(12.dp))
        }
        Controls(expanded, interventions, shell, app.first) {
            interventions = shell.run("cmd game list-configs ${app.first}").stdout()
        }
    }
}

@Composable private fun Icon(icon: Drawable) {

    Image(painter = rememberDrawablePainter(icon),
        contentDescription = null,
        modifier = Modifier
            .size(56.dp)
            .padding(8.dp))
}

@Composable private fun ColumnScope.Controls(
    expanded: Boolean,
    interventions: String,
    shell: Shell,
    packageName: String?,
    callback: () -> Unit = {},
) {

    AnimatedVisibility(visible = expanded) {
        var gameMode = interventions.substringAfter("Game Mode:", "UNSET/UNKNOWN").substringBefore(",", "UNSET/UNKNOWN")
        var scaling = interventions.substringAfter("Scaling:", "UNSET/UNKNOWN").substringBefore(",", "UNSET/UNKNOWN")
        var useAngle = interventions.substringAfter("Use Angle:").substringBefore(",", "UNSET/UNKNOWN")
        var fps = interventions.substringAfter("Fps:", "UNSET/UNKNOWN").substringBefore(",", "UNSET/UNKNOWN")


        Column(Modifier.padding(8.dp)) {

            Text(text = "Gamemode: " + when (gameMode) {
                "1"  -> "standard"
                "2"  -> "performance"
                "3"  -> "battery"
                "4"  -> "custom"
                else -> "UNSET/UNKNOWN"
            })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically) {

                Button(onClick = {
                    shell.run("cmd game mode 1 $packageName")
                    callback()
                    gameMode = interventions.substringAfter("Game Mode:", "UNSET/UNKNOWN")
                        .substringBefore(",", "UNSET/UNKNOWN")
                }) {
                    Text(text = "Standard")
                }
                Button(onClick = {
                    shell.run("cmd game mode 2 $packageName")
                    callback()
                    gameMode = interventions.substringAfter("Game Mode:", "UNSET/UNKNOWN")
                        .substringBefore(",", "UNSET/UNKNOWN")
                }) {
                    Text(text = "Performance")
                }
                Button(onClick = {
                    shell.run("cmd game mode 3 $packageName")
                    callback()
                    gameMode = interventions.substringAfter("Game Mode:", "UNSET/UNKNOWN")
                        .substringBefore(",", "UNSET/UNKNOWN")
                }) {
                    Text(text = "Battery")
                }
                Button(onClick = {
                    shell.run("cmd game mode 4 $packageName")
                    callback()
                    gameMode = interventions.substringAfter("Game Mode:", "UNSET/UNKNOWN")
                        .substringBefore(",", "UNSET/UNKNOWN")
                }) {
                    Text(text = "Custom")
                }

            }
            var floatDownscale by remember {
                mutableFloatStateOf(scaling.toFloatOrNull().takeIf {
                    (it ?: 1f) >= 0f
                } ?: 1f)
            }
            Text(text = "Scaling: $floatDownscale")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.horizontalScroll(rememberScrollState())) {

                Button(onClick = {
                    shell.run("cmd game set --downscale 0.5 $packageName")
                    callback()

                    floatDownscale = 0.5f
                }) { Text(text = "1/2") }
                Button(onClick = {
                    shell.run("cmd game set --downscale 0.33 $packageName")
                    callback()

                    floatDownscale = 0.33f
                }) { Text(text = "1/3") }
                Button(onClick = {
                    shell.run("cmd game set --downscale 0.25 $packageName")
                    callback()

                    floatDownscale = 0.25f
                }) { Text(text = "1/4") }
                Button(onClick = {
                    shell.run("cmd game set --downscale 0.20 $packageName")
                    callback()

                    floatDownscale = 0.2f
                }) { Text(text = "1/5") }

                Button(onClick = {
                    shell.run("cmd game set --downscale 1.0 $packageName")
                    callback()

                    floatDownscale = 1f
                }) { Text(text = "Reset") }
            }
            Slider(value = floatDownscale, onValueChange = {
                floatDownscale = ((it * 100).toInt()).toFloat() / 100
            }, onValueChangeFinished = {
                shell.run("cmd game set --downscale $floatDownscale $packageName")
                callback()
            }, valueRange = 0f..2f, steps = 199)
            Text(text = "Use Angle: $useAngle")
            var floatFps by remember {
                mutableFloatStateOf(fps.toFloatOrNull() ?: 1f)
            }
            Text(text = "FPS: ${
                if (fps == "UNSET/UNKNOWN" || fps == "" || fps == "0") {
                    "UNSET/UNKNOWN"
                }
                else {
                    floatFps.roundToInt()
                }
            }")
            Slider(value = floatFps, onValueChange = {
                floatFps = ((it * 100).toInt()).toFloat() / 100
            }, onValueChangeFinished = {
                shell.run("cmd game set --fps ${floatFps.toInt()} $packageName")
                callback()
            }, valueRange = 0f..500f, steps = 49)


            Button(onClick = {
                shell.run("cmd game reset $packageName")
                callback()
                floatDownscale =
                    interventions.substringAfter("Scaling:", "UNSET/UNKNOWN").substringBefore(",", "UNSET/UNKNOWN")
                        .toFloatOrNull() ?: 1f
                floatFps = interventions.substringAfter("Fps:", "UNSET/UNKNOWN").substringBefore(",", "UNSET/UNKNOWN")
                    .toFloatOrNull() ?: 1f

            }) {
                Text(text = "Reset all")
            }
        }
    }
}

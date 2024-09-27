package pro.themed.perappdownscale

import android.content.*
import android.content.pm.*
import android.os.*
import android.widget.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.core.graphics.drawable.*
import com.jaredrummler.ktsh.*
import kotlinx.coroutines.*
import pro.themed.perappdownscale.ui.theme.*
import kotlin.math.*

class PerAppDownscaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThemedManagerTheme {
                PerAppDownscale()
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

@Preview
@Composable
fun PerAppDownscale(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val pm = context.packageManager
    val mainIntent = Intent(Intent.ACTION_MAIN, null)
    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
    var filter by remember { mutableStateOf("") }

    var resolvedInfos by remember {mutableStateOf(emptyList<ResolveInfo>())}




    LaunchedEffect(key1 = Unit, key2 = filter) {
        resolvedInfos = if (resolvedInfos.isEmpty()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.queryIntentActivities(
                    mainIntent, PackageManager.ResolveInfoFlags.of(0L)
                )
            } else {
                pm.queryIntentActivities(mainIntent, 0)
            }
        } else {
            resolvedInfos
        }
    }

    val shell = Shell("su")
    shell.addOnStderrLineListener(object : Shell.OnLineListener {
        override fun onLine(line: String) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, line, Toast.LENGTH_SHORT).show()
            }
        }
    })
    shell.addOnStdoutLineListener(object : Shell.OnLineListener {
        override fun onLine(line: String) {
            CoroutineScope(Dispatchers.Main).launch {
                if (line.contains("set"))// showInterstitial(context) {}
                //line.log()
                    if (line.contains("not supported")) Toast.makeText(
                        context, line, Toast.LENGTH_SHORT
                    ).show()

            }
        }
    })

    Surface(modifier = Modifier.fillMaxSize()) {
        if (resolvedInfos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Row {
                    CircularProgressIndicator()

                    Text(text = "Loading...")
                }
            }
        }
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing
                )
        ) {

            item {

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = filter,
                        onValueChange = { filter = it },
                        label = { Text("Filter") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
            resolvedInfos.filter {
                it.activityInfo.applicationInfo.loadLabel(pm).toString().trim()
                    .contains(filter.trim(), true)
            }.sortedBy { it.activityInfo.applicationInfo.loadLabel(pm).toString().trim() }
                .forEachIndexed { index, resolveInfo ->
                    item {

                        val label by remember {

                            mutableStateOf(

                                resolveInfo.activityInfo.applicationInfo.loadLabel(pm).toString()

                            )
                        }
                        val icon by remember {
                            mutableStateOf(
                                resolveInfo.loadIcon(pm).toBitmap().asImageBitmap()
                            )
                        }
                        val packageName by remember {
                            mutableStateOf(resolveInfo.activityInfo.packageName)
                        }

                        var expanded by rememberSaveable {
                            mutableStateOf(false)
                        }
                        var interventions by remember {
                            mutableStateOf(
                                shell.run("cmd game list-configs $packageName").stdout()
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = if (index % 2 == 0) Color.LightGray.copy(alpha = 0.1f) else Color.Transparent
                                )
                                .background(
                                    color = if (interventions.contains("Name")) Color.Yellow.copy(
                                        alpha = 0.1f
                                    ) else Color.Transparent
                                )
                        ) {


                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = !expanded }) {

                                Image(
                                    bitmap = icon,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(8.dp)
                                )
                                Column {

                                    Text(
                                        text = label
                                    )
                                    Text(text = packageName, maxLines = 1)
                                }
                                if (interventions.contains("Name")) Icon(
                                    imageVector = Icons.Filled.AddCircle,
                                    contentDescription = null,
                                    tint = Color.Yellow,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            AnimatedVisibility(visible = expanded) {
                                var gameMode =
                                    interventions.substringAfter("Game Mode:", "UNSET/UNKNOWN")
                                        .substringBefore(",", "UNSET/UNKNOWN")
                                var scaling =
                                    interventions.substringAfter("Scaling:", "UNSET/UNKNOWN")
                                        .substringBefore(",", "UNSET/UNKNOWN")
                                var useAngle = interventions.substringAfter("Use Angle:")
                                    .substringBefore(",", "UNSET/UNKNOWN")
                                var fps = interventions.substringAfter("Fps:", "UNSET/UNKNOWN")
                                    .substringBefore(",", "UNSET/UNKNOWN")


                                Column(Modifier.padding(8.dp)) {


                                    Text(
                                        text = "Gamemode: " + when (gameMode) {
                                            "1" -> "standard"
                                            "2" -> "performance"
                                            "3" -> "battery"
                                            "4" -> "custom"
                                            else -> "UNSET/UNKNOWN"
                                        }
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {


                                        Button(onClick = {
                                            shell.run("cmd game mode 1 $packageName")
                                            interventions =
                                                shell.run("cmd game list-configs $packageName")
                                                    .stdout()
                                            gameMode = interventions.substringAfter(
                                                "Game Mode:", "UNSET/UNKNOWN"
                                            ).substringBefore(",", "UNSET/UNKNOWN")
                                        }) {
                                            Text(text = "Standard")
                                        }
                                        Button(onClick = {
                                            shell.run("cmd game mode 2 $packageName")
                                            interventions =
                                                shell.run("cmd game list-configs $packageName")
                                                    .stdout()
                                            gameMode = interventions.substringAfter(
                                                "Game Mode:", "UNSET/UNKNOWN"
                                            ).substringBefore(",", "UNSET/UNKNOWN")
                                        }) {
                                            Text(text = "Performance")
                                        }
                                        Button(onClick = {
                                            shell.run("cmd game mode 3 $packageName")
                                            interventions =
                                                shell.run("cmd game list-configs $packageName")
                                                    .stdout()

                                            gameMode = interventions.substringAfter(
                                                "Game Mode:", "UNSET/UNKNOWN"
                                            ).substringBefore(",", "UNSET/UNKNOWN")
                                        }) {
                                            Text(text = "Battery")
                                        }
                                        Button(onClick = {
                                            shell.run("cmd game mode 4 $packageName")
                                            interventions =
                                                shell.run("cmd game list-configs $packageName")
                                                    .stdout()
                                            gameMode = interventions.substringAfter(
                                                "Game Mode:", "UNSET/UNKNOWN"
                                            ).substringBefore(",", "UNSET/UNKNOWN")
                                        }) {
                                            Text(text = "Custom")
                                        }

                                    }
                                    var floatDownscale by remember {
                                        mutableFloatStateOf(scaling.toFloatOrNull().takeIf {
                                            (it ?: 1f) >= 0f
                                        } ?: 1f)
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Text(text = "Scaling: $floatDownscale")
                                        Button(onClick = {
                                            shell.run("cmd game set --downscale 1.0 $packageName")
                                            interventions =
                                                shell.run("cmd game list-configs $packageName")
                                                    .stdout()
                                            floatDownscale = interventions.substringAfter(
                                                "Scaling:", "UNSET/UNKNOWN"
                                            ).substringBefore(",", "UNSET/UNKNOWN").toFloatOrNull()
                                                ?: 1f
                                        }) { Text(text = "Reset") }
                                    }
                                    Slider(value = floatDownscale, onValueChange = {
                                        floatDownscale = ((it * 100).toInt()).toFloat() / 100
                                    }, onValueChangeFinished = {
                                        shell.run("cmd game set --downscale $floatDownscale $packageName")
                                        interventions =
                                            shell.run("cmd game list-configs $packageName").stdout()


                                    }, valueRange = 0f..2f, steps = 199
                                    )
                                    Text(text = "Use Angle: $useAngle")
                                    var floatFps by remember {
                                        mutableFloatStateOf(fps.toFloatOrNull() ?: 1f)
                                    }
                                    Text(
                                        text = "FPS: ${
                                            if (fps == "UNSET/UNKNOWN" || fps == "" || fps == "0") {
                                                "UNSET/UNKNOWN"
                                            } else {
                                                floatFps.roundToInt()
                                            }
                                        }"
                                    )
                                    Slider(value = floatFps, onValueChange = {
                                        floatFps = ((it * 100).toInt()).toFloat() / 100
                                    }, onValueChangeFinished = {
                                        shell.run("cmd game set --fps ${floatFps.toInt()} $packageName")
                                        interventions =
                                            shell.run("cmd game list-configs $packageName").stdout()


                                    }, valueRange = 0f..500f, steps = 49
                                    )


                                    Button(onClick = {
                                        shell.run("cmd game reset $packageName")
                                        interventions =
                                            shell.run("cmd game list-configs $packageName").stdout()
                                        floatDownscale = interventions.substringAfter(
                                            "Scaling:", "UNSET/UNKNOWN"
                                        ).substringBefore(",", "UNSET/UNKNOWN").toFloatOrNull()
                                            ?: 1f
                                        floatFps = interventions.substringAfter(
                                            "Fps:", "UNSET/UNKNOWN"
                                        ).substringBefore(",", "UNSET/UNKNOWN").toFloatOrNull()
                                            ?: 1f

                                    }) {
                                        Text(text = "Reset all")
                                    }
                                }
                            }
                        }
                    }

                }
        }
    }
}

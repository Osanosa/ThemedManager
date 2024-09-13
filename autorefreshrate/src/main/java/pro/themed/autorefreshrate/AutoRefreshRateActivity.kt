@file:OptIn(ExperimentalLayoutApi::class)

package pro.themed.autorefreshrate

import android.content.*
import android.hardware.display.*
import android.os.*
import android.util.*
import android.widget.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.google.firebase.analytics.*
import com.google.firebase.analytics.ktx.*
import com.google.firebase.crashlytics.ktx.*
import com.google.firebase.database.*
import com.google.firebase.ktx.*
import com.jaredrummler.ktsh.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.*
import pro.themed.autorefreshrate.ui.theme.*
import java.security.*
import kotlin.math.*

class AutoRefreshRateActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics
    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
       analytics = Firebase.analytics
        fun shareStackTrace(stackTrace: String) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, stackTrace)
            }
            startActivity(Intent.createChooser(intent, "Share stack trace"))
        }

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            val stackTrace = Log.getStackTraceString(throwable)
            shareStackTrace(stackTrace)
        }
        val suVersion by lazy {
            Shell.SH.run("su -v").stdout()
        }
        Firebase.crashlytics.setCustomKey("su version", suVersion)

        fun String.sha256() = MessageDigest.getInstance("SHA-256").digest(toByteArray()).toHexString()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedPreferences = this.applicationContext.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val context = LocalContext.current
            ThemedManagerTheme {
                Toast.makeText(context, "YOUR FEEDBACK IS IMPORTANT AND VERY MUCH WELCOMED (on telegram or email)",
                    Toast
                    .LENGTH_SHORT).show()

                Surface(Modifier.fillMaxSize()) {
                    val testCommand = "service call SurfaceFlinger 1035 i32"
                    val displayManager = this.getSystemService(DisplayManager::class.java).displays[0]
                    val unsupportedModes by remember { mutableStateOf(displayManager.supportedModes.size < 2) }

                    var currentRefreshRate by remember { mutableFloatStateOf(displayManager.refreshRate) }
                    LaunchedEffect(Unit) {
                        val database = FirebaseDatabase.getInstance("https://themed-manager-default-rtdb.europe-west1.firebasedatabase.app")
                        val gpShell = Shell.SH
                        val gpList = gpShell.run("getprop").stdout //already split!!!

                        val cpu = gpList.find { it.contains("ro.soc.model") }?.substringAfter(":")?.trim('[', ']', ' ')
                        val currentBuidId = gpList.find { it.contains("ro.build.id") }?.substringAfter(":")
                            ?.trim('[', ']', ' ')
                        val currentVersion = gpList.find { it.contains("ro.build.version.release") }
                            ?.substringAfter(":")?.trim('[', ']', ' ')
                        val manufacturer = gpList.find { it.contains("ro.product.vendor.manufacturer") }
                            ?.substringAfter(":")?.trim('[', ']', ' ')
                        val model = gpList.find { it.contains("ro.product.vendor.model") }?.substringAfter(":")
                            ?.trim('[', ']', ' ')
                        val modes = displayManager.supportedModes.map { it.refreshRate.roundToInt() }.joinToString()
                        val rom = gpList.find { it.contains("ro.product.name") }?.substringAfter(":")
                            ?.trim('[', ']', ' ')
                        val vendorBuildId = gpList.find { it.contains("ro.vendor.build.id") }?.substringAfter(":")
                            ?.trim('[', ']', ' ')
                        val vendorVersion = gpList.find { it.contains("ro.vendor.build.version.release") }
                            ?.substringAfter(":")?.trim('[', ']', ' ')
                        val document = "$manufacturer/$model/$vendorBuildId/$currentVersion"

                        val ref = database.getReference("devices").child(document.sha256())
                        if (!ref.get().await().exists()) {

                            ref.setValue(hashMapOf("ref" to document.sha256(), "cpu" to cpu, "currentBuildId" to currentBuidId, "currentVersion" to currentVersion, "manufacturer" to manufacturer, "model" to model, "modes" to modes, "rom" to rom, "vendorBuildId" to vendorBuildId, "vendorVersion" to vendorVersion))
                        }

                    }
                    LaunchedEffect(displayManager) {
                        Log.d("DISPLAY", displayManager.toString())
                    }
                    Column(Modifier.fillMaxSize()) {
                        Column(Modifier
                            .windowInsetsPadding(WindowInsets.safeDrawing)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())) {
                            Text(text = "TMARR", style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center, modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp))
                            Text(displayManager.name + ": " + currentRefreshRate.roundToInt()
                                .toString() + "Hz, " + displayManager.mode.physicalHeight.toString() + "x" + displayManager.mode.physicalWidth.toString(), fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                            var noRoot by remember { mutableStateOf(false) }
                            AnimatedVisibility(noRoot) {
                                Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.error, shape = MaterialTheme.shapes.medium) {
                                    Text("Root access required", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(16.dp))
                                }

                            }
                            AnimatedVisibility(noRoot && unsupportedModes) {
                                Spacer(Modifier.height(16.dp))
                            }
                            AnimatedVisibility(unsupportedModes) {
                                Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.error, shape = MaterialTheme.shapes.medium) {
                                    Text("Uh oh, looks like your device doesn't support multiple refresh rates", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(16.dp))
                                }

                            }

                            val shell = Shell.SH.addOnStderrLineListener(object : Shell.OnLineListener {
                                override fun onLine(line: String) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        when {
                                            line.contains("su: inaccessible or not found", ignoreCase = true) || line.contains("permission denied", ignoreCase = true) -> {
                                                noRoot = true
                                            }

                                            line.contains("service stopped", ignoreCase = true)                                                                        -> {
                                                Toast.makeText(context, line, Toast.LENGTH_SHORT).show()
                                            }

                                            line.contains("not stopped", ignoreCase = true)                                                                            -> {
                                                Toast.makeText(context, line, Toast.LENGTH_SHORT).show()
                                            }

                                            else                                                                                                                       -> shareStackTrace(line)
                                        }
                                    }
                                }
                            })
                            shell.run("su")
                            var showRate by remember {
                                mutableStateOf(shell.run("service call SurfaceFlinger 1034 i32 2").stdout()
                                    .contains("1"))
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Show rate: ")
                                Switch(checked = showRate, onCheckedChange = {
                                    showRate = it
                                    CoroutineScope(Dispatchers.IO).launch {

                                        if (it) {
                                            shell.run("service call SurfaceFlinger 1034 i32 1")
                                        } else {
                                            shell.run("service call SurfaceFlinger 1034 i32 0")
                                        }
                                    }

                                })
                            }
                            val supportedModesArray = displayManager.supportedModes.withIndex()
                            Text("Test supported modes:")
                            FlowRow(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {

                                for ((index, mode) in supportedModesArray) {
                                    Button(colors = ButtonColors(containerColor = if (displayManager.mode.modeId
                                        == index+1) {
                                        MaterialTheme.colorScheme.tertiary
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }, contentColor = MaterialTheme.colorScheme.onPrimary, disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant, disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant), onClick = {
                                        //coroutine

                                        CoroutineScope(Dispatchers.IO).launch {
                                            shell.run("$testCommand $index")
                                            Thread.sleep(100)
                                            currentRefreshRate = displayManager.refreshRate
                                        }

                                    }) {
                                        Text(index.toString() + ": " + mode.refreshRate.roundToInt())
                                    }
                                }

                            }
                            Text("Foreground service settings:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 16.dp))
                            Text("Set max mode:")
                            var maxRate by remember {
                                mutableStateOf(sharedPreferences.getString("maxRate", "0"))
                            }
                            FlowRow(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {

                                for ((index, mode) in supportedModesArray) {
                                    Button(colors = ButtonColors(containerColor = if (maxRate?.toInt() == index) {
                                        MaterialTheme.colorScheme.tertiary
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }, contentColor = MaterialTheme.colorScheme.onPrimary, disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant, disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant), onClick = {
                                        sharedPreferences.edit().putString("maxRate", index.toString()).apply()
                                        maxRate = index.toString()
                                    }) {
                                        Text(index.toString() + ": " + mode.refreshRate.roundToInt())
                                    }
                                }
                            }
                            Text("Set min mode:")
                            var minRate by remember {
                                mutableStateOf(sharedPreferences.getString("minRate", "0"))
                            }
                            FlowRow(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {

                                for ((index, mode) in supportedModesArray) {
                                    Button(colors = ButtonColors(containerColor = if (minRate?.toInt() == index) {
                                        MaterialTheme.colorScheme.tertiary
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }, contentColor = MaterialTheme.colorScheme.onPrimary, disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant, disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant), onClick = {
                                        sharedPreferences.edit().putString("minRate", index.toString()).apply()
                                        minRate = index.toString()
                                    }) {
                                        Text(index.toString() + ": " + mode.refreshRate.roundToInt())
                                    }
                                }
                            }
                            var timeout by remember {
                                mutableIntStateOf(sharedPreferences.getInt("countdown", 3))
                            }
                            Text("Set timeout: $timeout")
                            Slider(

                                value = timeout.toFloat(), onValueChange = { timeout = it.roundToInt() }, onValueChangeFinished = {
                                    sharedPreferences.edit().putInt("countdown", timeout).apply()
                                }, valueRange = 1f..10f, steps = 8

                            )
                            var autoRateOnBoot by remember {
                                mutableStateOf(sharedPreferences.getBoolean("autoRateOnBoot", false))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)) {

                                Text(text = "Auto rate on boot: $autoRateOnBoot", style = MaterialTheme.typography.titleMedium)
                                Switch(checked = autoRateOnBoot, onCheckedChange = {
                                    if (!noRoot) {
                                        autoRateOnBoot = it
                                        sharedPreferences.edit().putBoolean("autoRateOnBoot", it).apply()
                                    } else {
                                        Toast.makeText(applicationContext, "No root", Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }

                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Button(onClick = {
                                    shell.run("am stop-service pro.themed.manager.autorefreshrate/pro.themed.autorefreshrate.AutoRefreshRateForegroundService")
                                    showInterstitial(applicationContext)

                                }) {
                                    Text("Stop service")
                                }
                                Button(onClick = {
                                    shell.run("am start-foreground-service pro.themed.manager.autorefreshrate/pro.themed.autorefreshrate.AutoRefreshRateForegroundService")
                                    showInterstitial(applicationContext)
                                }) {
                                    Text("Start service")
                                }
                            }
                            LinkButtons(Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 72.dp, vertical = 8.dp)
                                .background(MaterialTheme.colorScheme.inversePrimary.copy(0.2f), CircleShape))

                        }
                        Row (Modifier.weight(1f)){

                            AdmobBanner(applicationContext)
                        }
                    }
                }

            }
        }
    }
}



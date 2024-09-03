package pro.themed.autorefreshrate

import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pro.themed.autorefreshrate.ui.theme.ThemedManagerTheme
import kotlin.math.roundToInt


class AutoRefreshRateActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedPreferences = this.applicationContext.getSharedPreferences(
                "my_preferences", Context.MODE_PRIVATE
            )
            val context = LocalContext.current
            ThemedManagerTheme {
                Surface(Modifier.fillMaxSize()) {
                    val testCommand = "service call SurfaceFlinger 1035 i32"
                    val displayManager =
                        this.getSystemService(DisplayManager::class.java).displays[0]
                    val unsupportedModes by remember { mutableStateOf(displayManager.supportedModes.size < 2) }

                    var currentRefreshRate by remember { mutableStateOf(displayManager.refreshRate) }

                    LaunchedEffect(displayManager) {
                        Log.d("DISPLAY", displayManager.toString())
                    }
                    Column(
                        Modifier
                            .windowInsetsPadding(WindowInsets.safeDrawing)
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "TMARR",
                            style = MaterialTheme.typography.headlineLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        Text(
                            displayManager.name + ": " + currentRefreshRate.roundToInt()
                                .toString() + "Hz, " + displayManager.height.toString() + "x" + displayManager.width.toString(),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        var noRoot by remember { mutableStateOf(false) }
                        AnimatedVisibility(noRoot) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.error,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(
                                    "Root access required",
                                    style = MaterialTheme.typography.headlineLarge,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                        }
                        AnimatedVisibility(noRoot && unsupportedModes) {
                            Spacer(Modifier.height(16.dp))
                        }
                        AnimatedVisibility(unsupportedModes) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.error,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(
                                    "Uh oh, looks like your device doesn't support multiple refresh rates",
                                    style = MaterialTheme.typography.headlineLarge,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                        }


                        val shell = Shell.SH.addOnStderrLineListener(object : Shell.OnLineListener {
                            override fun onLine(line: String) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    when {
                                        line.contains(
                                            "su: inaccessible or not found", ignoreCase = true
                                        ) || line.contains(
                                            "permission denied", ignoreCase = true
                                        ) -> {
                                            noRoot = true
                                        }

                                        line.contains("service stopped", ignoreCase = true) -> {
                                            Toast.makeText(
                                                context, line, Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        line.contains("not stopped", ignoreCase = true) -> {
                                            Toast.makeText(
                                                context, line, Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        else -> shareStackTrace(line)
                                    }
                                }
                            }
                        })
                        shell.run("su")
                        var showRate by remember {
                            mutableStateOf(
                                shell.run("service call SurfaceFlinger 1034 i32 2").stdout()
                                    .contains("1")
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
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
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            repeat(5) {

                                for ((index, mode) in supportedModesArray) {
                                    Button(onClick = {
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
                        }
                        Text(
                            "Foreground service settings:",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                        Text("Set max mode:")
                        var maxRate by remember {
                            mutableStateOf(
                                sharedPreferences.getString(
                                    "maxRate", "0"
                                )
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {

                            for ((index, mode) in supportedModesArray) {
                                Button(colors = ButtonColors(
                                    containerColor = if (maxRate?.toInt() == index) {
                                        MaterialTheme.colorScheme.tertiary
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    },
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ), onClick = {
                                    sharedPreferences.edit().putString("maxRate", index.toString())
                                        .apply()
                                    maxRate = index.toString()
                                }) {
                                    Text(index.toString() + ": " + mode.refreshRate.roundToInt())
                                }
                            }
                        }
                        Text("Set min mode:")
                        var minRate by remember {
                            mutableStateOf(
                                sharedPreferences.getString(
                                    "minRate", "0"
                                )
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {

                            for ((index, mode) in supportedModesArray) {
                                Button(colors = ButtonColors(
                                    containerColor = if (minRate?.toInt() == index) {
                                        MaterialTheme.colorScheme.tertiary
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    },
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ), onClick = {
                                    sharedPreferences.edit().putString("minRate", index.toString())
                                        .apply()
                                    minRate = index.toString()
                                }) {
                                    Text(index.toString() + ": " + mode.refreshRate.roundToInt())
                                }
                            }
                        }
                        var timeout by remember {
                            mutableStateOf(
                                sharedPreferences.getInt(
                                    "countdown", 3
                                )
                            )
                        }
                        Text("Set timeout: $timeout")
                        Slider(

                            value = timeout.toFloat(),
                            onValueChange = { timeout = it.roundToInt() },
                            onValueChangeFinished = {
                                sharedPreferences.edit().putInt("countdown", timeout).apply()
                            },
                            valueRange = 1f..10f,
                            steps = 8

                        )
                        var autoRateOnBoot by remember {
                            mutableStateOf(
                                sharedPreferences.getBoolean(
                                    "autoRateOnBoot", false
                                )
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {

                            Text(
                                text = "Auto rate on boot: $autoRateOnBoot",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Switch(checked = autoRateOnBoot, onCheckedChange = {
                                autoRateOnBoot = it
                                sharedPreferences.edit().putBoolean("autoRateOnBoot", it).apply()
                            })
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = {
                                shell.run("am stop-service pro.themed.manager.autorefreshrate/pro.themed.autorefreshrate.AutoRefreshRateForegroundService")

                            }) {
                                Text("Stop service")
                            }
                            Button(onClick = {
                                shell.run("am start-foreground-service pro.themed.manager.autorefreshrate/pro.themed.autorefreshrate.AutoRefreshRateForegroundService")
                            }) {
                                Text("Start service")
                            }
                        }


                    }
                }


            }
        }
    }
}



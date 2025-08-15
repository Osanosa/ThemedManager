@file:OptIn(ExperimentalLayoutApi::class)

package pro.themed.autorefreshrate

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pro.themed.autorefreshrate.ui.theme.ThemedManagerTheme
import pro.themed.manager.autorefreshrate.R.drawable
import java.security.MessageDigest
import kotlin.math.roundToInt

@Suppress("DEPRECATION") // Deprecated for third party Services.
fun <T> Context.isServiceForegrounded(service: Class<T>) =
    (getSystemService(ACTIVITY_SERVICE) as? ActivityManager)
        ?.getRunningServices(Integer.MAX_VALUE)
        ?.find { it.service.className == service.name }
        ?.foreground == true

class AutoRefreshRateActivity : ComponentActivity() {

    private lateinit var analytics: FirebaseAnalytics

    @OptIn(ExperimentalStdlibApi::class, ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        analytics = Firebase.analytics
        val scope = CoroutineScope(Dispatchers.Main)

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
        val suVersion by lazy { Shell.SH.run("su -v").stdout() }
        Firebase.crashlytics.setCustomKey("su version", suVersion)

        fun String.sha256() =
            MessageDigest.getInstance("SHA-256").digest(toByteArray()).toHexString()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedPreferences =
                this.applicationContext.getSharedPreferences("my_preferences", MODE_PRIVATE)
            val context = LocalContext.current
            ThemedManagerTheme {
                Toast.makeText(
                        context,
                        "YOUR FEEDBACK IS IMPORTANT AND VERY MUCH WELCOMED (on telegram or email)",
                        Toast.LENGTH_SHORT
                    )
                    .show()

                FirebaseIsContributor(sharedPreferences, context)
                Surface(Modifier.fillMaxSize()) {
                    val testCommand = "service call SurfaceFlinger 1035 i32"
                    val displayManager =
                        this.getSystemService(DisplayManager::class.java).displays[0]
                    val unsupportedModes by remember {
                        mutableStateOf(
                            displayManager.supportedModes.distinctBy { it.refreshRate }.size < 2
                        )
                    }

                    var currentRefreshRate by remember {
                        mutableFloatStateOf(displayManager.refreshRate)
                    }
                    LaunchedEffect(Unit) {
                        val database =
                            FirebaseDatabase.getInstance(
                                "https://themed-manager-default-rtdb.europe-west1.firebasedatabase.app"
                            )
                        val gpShell = Shell.SH
                        val gpList = gpShell.run("getprop").stdout // already split!!!
                        // if returns empty alternative command
                        val cpu =
                            gpList
                                .find { it.contains("ro.soc.model") }
                                ?.substringAfter(":")
                                ?.trim('[', ']', ' ')
                                ?: gpList
                                    .find { it.contains("ro.hardware") }
                                    ?.substringAfter(":")
                                    ?.trim('[', ']', ' ')
                                ?: gpList
                                    .find { it.contains("ro.boot.hardware") }
                                    ?.substringAfter(":")
                                    ?.trim('[', ']', ' ')
                        val currentBuidId =
                            gpList
                                .find { it.contains("ro.build.id") }
                                ?.substringAfter(":")
                                ?.trim('[', ']', ' ')
                                ?: gpList
                                    .find { it.contains("ro.system.build.id") }
                                    ?.substringAfter(":")
                                    ?.trim('[', ']', ' ')
                        val currentVersion =
                            gpList
                                .find { it.contains("ro.build.version.release") }
                                ?.substringAfter(":")
                                ?.trim('[', ']', ' ')
                                ?: gpList
                                    .find { it.contains("ro.build.version.release_or_codename") }
                                    ?.substringAfter(":")
                                    ?.trim('[', ']', ' ')
                        val manufacturer =
                            gpList
                                .find { it.contains("ro.product.vendor.manufacturer") }
                                ?.substringAfter(":")
                                ?.trim('[', ']', ' ')
                                ?: gpList
                                    .find { it.contains("ro.carrier") }
                                    ?.substringAfter(":")
                                    ?.trim('[', ']', ' ')
                        val model =
                            gpList
                                .find { it.contains("ro.product.vendor.model") }
                                ?.substringAfter(":")
                                ?.trim('[', ']', ' ')
                                ?: gpList
                                    .find { it.contains("ro.build.fingerprint") }
                                    ?.substringAfter(":")
                                    ?.substringAfter("/")
                                    ?.substringBefore("/")
                                    ?.trim('[', ']', ' ')
                        val modes =
                            displayManager.supportedModes

                                .sortedByDescending { it.refreshRate }
                                .distinctBy { it.refreshRate }
                                .map { it.refreshRate.roundToInt() }

                                .joinToString()
                        val rom =
                            gpList
                                .find { it.contains("ro.product.name") }
                                ?.substringAfter(":")
                                ?.trim('[', ']', ' ')
                                ?: gpList
                                    .find { it.contains("ro.build.product") }
                                    ?.substringAfter(":")
                                    ?.trim('[', ']', ' ')
                        val vendorBuildId =
                            gpList
                                .find { it.contains("ro.vendor.build.id") }
                                ?.substringAfter(":")
                                ?.trim('[', ']', ' ')
                                ?: gpList
                                    .find { it.contains("ro.mediatek.version.release") }
                                    ?.substringAfter(":")
                                    ?.trim('[', ']', ' ')
                                ?: gpList
                                    .find { it.contains("ro.fota.version") }
                                    ?.substringAfter(":")
                                    ?.trim('[', ']', ' ')
                        val vendorVersion =
                            gpList
                                .find { it.contains("ro.vendor.build.version.release") }
                                ?.substringAfter(":")
                                ?.trim('[', ']', ' ')
                                ?: gpList
                                    .find { it.contains("ro.keymaster.xxx.release") }
                                    ?.substringAfter(":")
                                    ?.trim('[', ']', ' ')

                        val document = "$manufacturer/$model/$vendorBuildId/$currentVersion"

                        val ref = database.getReference("devices").child(document.sha256())
                        if (!ref.get().await().exists()) {

                            ref.setValue(
                                hashMapOf(
                                    "ref" to document.sha256(),
                                    "cpu" to cpu,
                                    "currentBuildId" to currentBuidId,
                                    "currentVersion" to currentVersion,
                                    "manufacturer" to manufacturer,
                                    "model" to model,
                                    "modes" to modes,
                                    "rom" to rom,
                                    "vendorBuildId" to vendorBuildId,
                                    "vendorVersion" to vendorVersion
                                )
                            )
                        }
                    }
                    LaunchedEffect(displayManager) { Log.d("DISPLAY", displayManager.toString()) }
                    var noRoot by remember { mutableStateOf(false) }

                    Column(
                        Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        AnimatedVisibility(noRoot) {
                            Column {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.error,
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text(
                                        "Root access required",
                                        style = MaterialTheme.typography.headlineMedium,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }

                                Text(
                                    "Supported modes: ",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (!unsupportedModes) {
                                    displayManager.supportedModes.forEach {
                                        Text(text = it.refreshRate.roundToInt().toString())
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(noRoot && unsupportedModes) {
                            Spacer(Modifier.height(8.dp))
                        }
                        AnimatedVisibility(unsupportedModes) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.error,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(
                                    "Uh oh, looks like your device doesn't support multiple refresh rates",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        AnimatedVisibility(!noRoot && !unsupportedModes) {
                            Column {
                                val clipboardManager: ClipboardManager =
                                    LocalClipboardManager.current

                                Row(
                                    Modifier.fillMaxWidth().padding(4.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "TMARR",
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
                                                .padding(4.dp)
                                    )
                                }
                                AdmobBanner(applicationContext)
                                Image(
                                    painterResource(drawable.gridfps_00000),
                                    null,
                                    Modifier.fillMaxWidth()
                                        .basicMarquee(
                                            iterations = Int.MAX_VALUE,
                                            spacing = MarqueeSpacing(0.dp),
                                            initialDelayMillis = 0,
                                            repeatDelayMillis = 0,
                                            velocity = 500.dp
                                        )
                                )
                                Text(
                                    displayManager.name +
                                        ": " +
                                        currentRefreshRate.roundToInt().toString() +
                                        "Hz, " +
                                        displayManager.mode.physicalHeight.toString() +
                                        "x" +
                                        displayManager.mode.physicalWidth.toString(),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                var isRunning by remember {
                                    mutableStateOf(
                                        isServiceForegrounded(
                                            AutoRefreshRateForegroundService::class.java
                                        )
                                    )
                                }

                                Text(
                                    if (isRunning) "Foreground service is running"
                                    else "Foreground service is not running",
                                    color = if (isRunning) Color.Green else Color.Red,
                                )

                                AnimatedVisibility(unsupportedModes) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.error,
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        Text(
                                            "Uh oh, looks like your device doesn't support multiple refresh rates",
                                            style = MaterialTheme.typography.headlineMedium,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }

                                val shell =
                                    Shell.SH.addOnStderrLineListener(
                                        object : Shell.OnLineListener {
                                            override fun onLine(line: String) {
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    when {
                                                        line.contains(
                                                            "su: inaccessible or not found",
                                                            ignoreCase = true
                                                        ) ||
                                                            line.contains(
                                                                "permission denied",
                                                                ignoreCase = true
                                                            ) -> {
                                                            noRoot = true
                                                        }
                                                        line.contains(
                                                            "service stopped",
                                                            ignoreCase = true
                                                        ) -> {
                                                            Toast.makeText(
                                                                    context,
                                                                    line,
                                                                    Toast.LENGTH_SHORT
                                                                )
                                                                .show()
                                                        }
                                                        line.contains(
                                                            "not stopped",
                                                            ignoreCase = true
                                                        ) -> {
                                                            Toast.makeText(
                                                                    context,
                                                                    line,
                                                                    Toast.LENGTH_SHORT
                                                                )
                                                                .show()
                                                        }
                                                        else -> shareStackTrace(line)
                                                    }
                                                }
                                            }
                                        }
                                    )
                                LaunchedEffect(Unit) { shell.run("su") }
                                var showRate by remember {
                                    mutableStateOf(
                                        shell
                                            .run("service call SurfaceFlinger 1034 i32 2")
                                            .stdout()
                                            .contains("1")
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Show rate: ")
                                    Switch(
                                        checked = showRate,
                                        onCheckedChange = {
                                            showRate = it
                                            CoroutineScope(Dispatchers.IO).launch {
                                                if (it) {
                                                    shell.run(
                                                        "service call SurfaceFlinger 1034 i32 1"
                                                    )
                                                } else {
                                                    shell.run(
                                                        "service call SurfaceFlinger 1034 i32 0"
                                                    )
                                                }
                                            }
                                        }
                                    )
                                }
                                @Composable
                                fun ModeSelectionButton(
                                    index: Int,
                                    mode: Display.Mode,
                                    isSelected: Boolean,
                                    onClick: () -> Unit
                                ) {
                                    Button(
                                        colors =
                                            ButtonColors(
                                                containerColor =
                                                    if (isSelected) {
                                                        MaterialTheme.colorScheme.tertiary
                                                    } else {
                                                        MaterialTheme.colorScheme.primary
                                                    },
                                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                                disabledContainerColor =
                                                    MaterialTheme.colorScheme.surfaceVariant,
                                                disabledContentColor =
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        onClick = onClick
                                    ) {
                                        Text("${index}: ${mode.refreshRate.roundToInt()}")
                                    }
                                }

                                val supportedModesArray = displayManager.supportedModes
                                    .withIndex()
                                    .sortedByDescending { it.value.refreshRate }
                                    .distinctBy { it.value.refreshRate }
                                Text("Test supported modes:")
                                FlowRow(
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    for ((index, mode) in supportedModesArray) {
                                        ModeSelectionButton(
                                            index = index,
                                            mode = mode,
                                            isSelected = displayManager.mode.modeId == index + 1,
                                            onClick = {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    shell.run("$testCommand $index")
                                                    Thread.sleep(100)
                                                    currentRefreshRate = displayManager.refreshRate
                                                }
                                            }
                                        )
                                    }
                                }
                                var expanded by remember { mutableStateOf(false) }
//TODO: перенести изменения в основную ветку
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    Text(if (expanded) "Collapse all modes:" else "Expand all modes:",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)).clickable {
                                            expanded = !expanded
                                        }.padding(16.dp)
                                    )
                                    AnimatedVisibility(visible = expanded) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            for ((index, mode) in supportedModesArray) {
Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
        .clickable {
        CoroutineScope(Dispatchers.IO).launch {
            shell.run("$testCommand $index")
            Thread.sleep(100)
            currentRefreshRate = displayManager.refreshRate
        }
    }.padding(8.dp)
) {
    Column{
        Text("Mode: ${mode.modeId -1}; Refresh rate: ${mode.refreshRate}")
        Text("${mode.physicalHeight}x${mode.physicalWidth}")

    }
}                                            }
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
                                    mutableStateOf(sharedPreferences.getString("maxRate", "0"))
                                }
                                FlowRow(
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    for ((index, mode) in supportedModesArray) {
                                        ModeSelectionButton(
                                            index = index,
                                            mode = mode,
                                            isSelected = maxRate?.toInt() == index,
                                            onClick = {
                                                sharedPreferences
                                                    .edit()
                                                    .putString("maxRate", index.toString())
                                                    .apply()
                                                maxRate = index.toString()
                                            }
                                        )
                                    }
                                }

                                Text("Set min mode:")
                                var minRate by remember {
                                    mutableStateOf(sharedPreferences.getString("minRate", "0"))
                                }
                                FlowRow(
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    for ((index, mode) in supportedModesArray) {
                                        ModeSelectionButton(
                                            index = index,
                                            mode = mode,
                                            isSelected = minRate?.toInt() == index,
                                            onClick = {
                                                sharedPreferences
                                                    .edit()
                                                    .putString("minRate", index.toString())
                                                    .apply()
                                                minRate = index.toString()
                                            }
                                        )
                                    }
                                }
                                var timeout by remember {
                                    mutableIntStateOf(sharedPreferences.getInt("countdown", 3))
                                }
                                Text("Set timeout: $timeout")
                                Slider(
                                    value = timeout.toFloat(),
                                    onValueChange = { timeout = it.roundToInt() },
                                    onValueChangeFinished = {
                                        sharedPreferences
                                            .edit()
                                            .putInt("countdown", timeout)
                                            .apply()
                                    },
                                    valueRange = 1f..10f,
                                    steps = 8
                                )
                                var autoRateOnBoot by remember {
                                    mutableStateOf(
                                        sharedPreferences.getBoolean("autoRateOnBoot", false)
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                                ) {
                                    Text(
                                        text = "Auto rate on boot: $autoRateOnBoot",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Switch(
                                        checked = autoRateOnBoot,
                                        onCheckedChange = {
                                            if (!noRoot) {
                                                autoRateOnBoot = it
                                                sharedPreferences
                                                    .edit()
                                                    .putBoolean("autoRateOnBoot", it)
                                                    .apply()
                                            } else {
                                                Toast.makeText(
                                                        applicationContext,
                                                        "No root",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            }
                                        }
                                    )
                                }

                                var autoRestart by remember {
                                    mutableStateOf(
                                        sharedPreferences.getBoolean("autoRestartService", false)
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            if (!noRoot)
                                                shell.run(
                                                    "am stop-service pro.themed.manager.autorefreshrate/pro.themed.autorefreshrate.AutoRefreshRateForegroundService"
                                                )
                                            showInterstitial(context)
                                            CoroutineScope(Dispatchers.IO).launch {
                                                isRunning =
                                                    isServiceForegrounded(
                                                        AutoRefreshRateForegroundService::class.java
                                                    )
                                                var retryCount = 0
                                                while (retryCount < 10 && !isRunning) {
                                                    Thread.sleep(100)
                                                    isRunning =
                                                        isServiceForegrounded(
                                                            AutoRefreshRateForegroundService::class
                                                                .java
                                                        )
                                                    retryCount++
                                                }
                                            }
                                        }
                                    ) {
                                        Text("Stop service")
                                    }
                                    Button(
                                        onClick = {
                                            if (!noRoot)
                                                shell.run(
                                                    "am start-foreground-service pro.themed.manager.autorefreshrate/pro.themed.autorefreshrate.AutoRefreshRateForegroundService"
                                                )
                                            showInterstitial(context)
                                            CoroutineScope(Dispatchers.IO).launch {
                                                isRunning =
                                                    isServiceForegrounded(
                                                        AutoRefreshRateForegroundService::class.java
                                                    )
                                                var retryCount = 0
                                                while (retryCount < 10 && !isRunning) {
                                                    Thread.sleep(100)
                                                    isRunning =
                                                        isServiceForegrounded(
                                                            AutoRefreshRateForegroundService::class
                                                                .java
                                                        )
                                                    retryCount++
                                                }
                                            }
                                        }
                                    ) {
                                        Text("Start service")
                                    }
                                }
                            }
                        }
                        LinkButtons(
                            Modifier.fillMaxWidth()
                                .padding(horizontal = 72.dp, vertical = 8.dp)
                                .background(
                                    MaterialTheme.colorScheme.inversePrimary.copy(0.2f),
                                    CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}

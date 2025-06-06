package pro.themed.manager.comps

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.R
import pro.themed.manager.components.CookieCard
import pro.themed.manager.components.HeaderRow
import pro.themed.manager.ui.theme.background
import pro.themed.manager.utils.SharedPreferencesManager
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolboxPage() {
    val context = LocalContext.current
    Surface(modifier = Modifier.fillMaxSize(), color = background) {
        var progress by rememberSaveable { mutableStateOf(false) }
        var progresstext by remember { mutableStateOf("Waiting to start") }

        Box {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                SharedPreferencesManager.initialize(context)
                val sharedPreferences: SharedPreferences =
                    context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()

                Column(
                    Modifier.padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    HeaderRow(
                        stringResource(R.string.disable_overlays),
                        stringResource(R.string.disable_overlays_header),
                        button1text = stringResource(R.string.all),
                        button1onClick = {
                            Shell("su")
                                .run(
                                    """for ol in $(cmd overlay list | grep -E '[x]' | grep  -E '.x'  | sed -E 's/^....//'); do cmd overlay disable "$""" +
                                            """ol"; done"""
                                )
                        },
                        button2text = stringResource(R.string.stock),
                        button2onClick = {
                            Shell("su")
                                .run(
                                    """for ol in $(cmd overlay list | grep -E 'com.android.theme' | grep  -E '.x'  | sed -E 's/^....//'); do cmd overlay disable "$""" +
                                            """ol"; done"""
                                )
                            Shell("su")
                                .run(
                                    """for ol in $(cmd overlay list | grep -E 'com.android.system' | grep  -E '.x'  | sed -E 's/^....//'); do cmd overlay disable "$""" +
                                            """ol"; done"""
                                )
                            Shell("su")
                                .run(
                                    """for ol in $(cmd overlay list | grep -E 'com.accent' | grep  -E '.x'  | sed -E 's/^....//'); do cmd overlay disable "$""" +
                                            """ol"; done"""
                                )
                        },
                        button3text = stringResource(R.string.themed),
                        button3onClick = {
                            Shell("su")
                                .run(
                                    """for ol in $(cmd overlay list | grep -E '.x..themed.'  | sed -E 's/^....//'); do cmd overlay disable "$""" +
                                            """ol"; done"""
                                )
                        },
                    )
                    HeaderRow(
                        stringResource(R.string.restart_systemui),
                        stringResource(R.string.systemui_restart_header),
                        button1text = stringResource(R.string.restart_now),
                        button1onClick = { Shell("su").run("su -c killall com.android.systemui") },
                    )
                    HeaderRow(
                        stringResource(R.string.change_system_theme),
                        stringResource(R.string.change_a_theme_of_your_device),
                        button1text = stringResource(R.string.light),
                        button1onClick = { Shell("su").run("cmd uimode night no") },
                        button2text = stringResource(R.string.dark),
                        button2onClick = { Shell("su").run("cmd uimode night yes") },
                        button3text = stringResource(R.string.auto),
                        button3onClick = { Shell("su").run("cmd uimode night auto") },
                    )
                    HeaderRow(
                        stringResource(R.string.clear_app_caches),
                        stringResource(R.string.clears_cache_of_all_apps_data_is_safe),
                        button1text = stringResource(R.string.clear_all),
                        button1onClick = {
                            val freebefore =
                                Shell("su")
                                    .run("df -k /data | awk 'NR==2{print \$4}'\n")
                                    .stdout
                                    .toString()
                                    .replace(Regex("[^0-9]"), "")
                                    .toLong()
                            Shell("su").run("pm trim-caches 100000g")
                            val freeafter =
                                Shell("su")
                                    .run("df -k /data | awk 'NR==2{print \$4}'\n")
                                    .stdout
                                    .toString()
                                    .replace(Regex("[^0-9]"), "")
                                    .toLong()
                            val difference: Float = freeafter.toFloat() - freebefore.toFloat()
                            val toast =
                                when {
                                    difference > 1024 * 1024 ->
                                        "${
                                            DecimalFormat("#.##").format(
                                                difference / 1024 / 1024
                                            )
                                        }Gb"

                                    difference > 1024 ->
                                        "${
                                            DecimalFormat("#.##").format(
                                                difference / 1024
                                            )
                                        }Mb"

                                    else -> "${DecimalFormat("#").format(difference)}Kb"
                                }

                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(context, "+$toast", Toast.LENGTH_SHORT).show()
                            }
                        },
                    )
                    val forcedex2oat =
                        if (sharedPreferences.getBoolean("force_dex2oat", false)) " -f" else ""

                    HeaderRow(
                        stringResource(R.string.dex2oat),
                        stringResource(R.string.dex2oat_subheader),
                        button1weight = 1.2f,
                        button1text = "Everything",
                        button1onClick = {
                            progress = true
                            Shell("su").run("cmd package compile -m everything -a$forcedex2oat") {
                                onStdOut = { line: String -> progresstext = line }
                                timeout = Shell.Timeout(1, TimeUnit.SECONDS)
                            }
                            Shell("su").run(
                                "cmd package compile -m everything --secondary-dex -a$forcedex2oat"
                            ) {
                                onStdOut = { line: String -> progresstext = line }
                                timeout = Shell.Timeout(1, TimeUnit.SECONDS)
                            }
                        },
                        button2text = "Layouts",
                        button2onClick = {
                            progress = true
                            Shell("su").run(
                                "cmd package compile --compile-layouts -a$forcedex2oat"
                            ) {
                                onStdOut = { line: String -> progresstext = line }
                                timeout = Shell.Timeout(1, TimeUnit.SECONDS)
                            }
                        },
                        button3text = "Reset",
                        button3onClick = {
                            progress = true
                            Shell("su").run("cmd package compile --reset -a") {
                                onStdOut = { line: String -> progresstext = line }
                                timeout = Shell.Timeout(1, TimeUnit.SECONDS)
                            }
                        },
                        showSwitch = true,
                        switchDescription = "Force recompile even if not needed",
                        isChecked = sharedPreferences.getBoolean("force_dex2oat", false),
                        onCheckedChange = { editor.putBoolean("force_dex2oat", it).apply() },
                    )
                    var customresShown by remember { mutableStateOf(false) }
                    var customres by remember { mutableStateOf("540") }
                    if (customresShown) {
                        BasicAlertDialog(
                            onDismissRequest = { customresShown = false },
                            content = {
                                CookieCard {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(stringResource(R.string.enter_your_custom_resolution))
                                        Text(stringResource(R.string.downscale_warning))
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            TextField(
                                                modifier =
                                                    Modifier
                                                        .fillMaxWidth(),
                                                value = customres,
                                                singleLine = true,
                                                onValueChange = {
                                                    customres = it.replace(Regex("[^0-9]"), "")
                                                },
                                                keyboardOptions =
                                                    KeyboardOptions(
                                                        keyboardType = KeyboardType.Number
                                                    ),
                                                label = { Text("Enter custom resolution") },
                                                placeholder = {
                                                    Text("720", Modifier.basicMarquee())
                                                },
                                                colors = TextFieldDefaults.colors(
                                                    focusedIndicatorColor = if ((customres.toIntOrNull()
                                                            ?: 0) < 200 || (customres.toIntOrNull() ?: 0) > 2200
                                                    ) Color.Red
                                                    else Color.Unspecified,
                                                    focusedContainerColor = if ((customres.toIntOrNull()
                                                            ?: 0) < 200 || (customres.toIntOrNull() ?: 0) > 2200
                                                    ) Color.Red
                                                    else Color.Unspecified

                                                )
                                            )
                                            AnimatedVisibility(
                                                visible = (customres.toIntOrNull()
                                                    ?: 0) < 200 && customres.isNotEmpty()
                                            ) {
                                                Text(
                                                    text = "Resolution too low, you sure about that?",
                                                    color = Color.Red,
                                                    textAlign = TextAlign.Center

                                                )
                                            }
                                            AnimatedVisibility(visible = (customres.toIntOrNull() ?: 0) > 2200) {
                                                Text(
                                                    text = "Resolution too high, you sure about that?",
                                                    color = Color.Red,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                            AnimatedVisibility(visible = customres.isEmpty()) {
                                                Text(
                                                    text = "Enter your custom resolution",
                                                    color = Color.Red,
                                                )
                                            }
                                            AnimatedVisibility(customres.isNotEmpty()) {

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(8.dp),
                                                ) {
                                                    Button(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .weight(1f),
                                                        onClick = {
                                                            downscalebynumber(width = customres)
                                                            Shell("su")
                                                                .run(
                                                                    "sleep 10 ; wm size reset ; wm density reset"
                                                                )
                                                        },
                                                    ) {
                                                        Text(text = stringResource(R.string.test))
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Button(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .weight(1f),
                                                        onClick = {
                                                            downscalebynumber(width = customres)
                                                        },
                                                    ) {
                                                        Text(text = stringResource(R.string.apply))
                                                    }
                                                }
                                            }
                                            Text(
                                                text = stringResource(R.string.close),
                                                modifier =
                                                    Modifier
                                                        .clip(CircleShape)
                                                        .clickable { customresShown = false }
                                                        .padding(8.dp),
                                            )
                                        }
                                    }
                                }
                            },
                        )
                    }
                    HeaderRow(
                        stringResource(R.string.downscale),
                        stringResource(R.string.downscale_subheader),
                        button1text = "1/2",
                        button1onClick = {
                            downscalebydivisor("2")
                            if (sharedPreferences.getBoolean("resetwm", false)) {
                                Thread.sleep(10000)
                                Shell("su").run("wm size reset ; wm density reset")
                            }
                        },
                        button2text = "1/3",
                        button2onClick = {
                            downscalebydivisor("3")
                            if (sharedPreferences.getBoolean("resetwm", false)) {
                                Thread.sleep(10000)
                                Shell("su").run("wm size reset ; wm density reset")
                            }
                        },
                        button3text = "Set",
                        button3onClick = { customresShown = true },
                        button4text = "Reset",
                        button4onClick = { Shell("su").run("wm size reset ; wm density reset") },
                        showSwitch = true,
                        switchDescription =
                            stringResource(R.string.reset_to_defaults_after_10_seconds),
                        isChecked = sharedPreferences.getBoolean("resetwm", false),
                        onCheckedChange = { editor.putBoolean("resetwm", it).apply() },
                    )
                    SectionDialogHeader(header = "PerAppDownscale", onClick = {
                        val packageName = "pro.themed.perappdownscale"
                        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
                        if (launchIntent != null) {
                            context.startActivity(launchIntent)
                        } else {
                            try {
                                val playStoreIntent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
                                context.startActivity(playStoreIntent)
                            } catch (e: ActivityNotFoundException) {
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                                    )
                                )
                            }
                        }

                    })
                    SectionDialogHeader("AutoRefreshRate") {
                        val packageName = "pro.themed.manager.autorefreshrate"
                        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
                        if (launchIntent != null) {
                            context.startActivity(launchIntent)
                        } else {
                            try {
                                val playStoreIntent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
                                context.startActivity(playStoreIntent)
                            } catch (e: ActivityNotFoundException) {
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                                    )
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
            AnimatedVisibility(
                visible = progress,
                Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                enter = scaleIn(),
            ) {
                Card(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp),
                        ) {
                            if (progresstext.contains("/")) CircularProgressIndicator()
                            if (progresstext.contains("/")) Spacer(Modifier.width(16.dp))
                            Text(text = progresstext)
                        }
                        if (!progresstext.contains("/"))
                            Button(onClick = { progress = false }) { Text("Hide") }
                    }
                }
            }
        }
    }
}

fun downscalebydivisor(divisor: String) {
    Shell("su")
        .run(
            command =
                """
                # Set the number of division
                divisor=$divisor
                
                # Get the current resolution
                resolution=${'$'}(wm size | awk '{if (${'$'}1 == "Physical") {print ${'$'}3}}')
                
                # Extract the width and height values
                width=${'$'}(echo ${'$'}resolution | awk -F 'x' '{print ${'$'}1}')
                height=${'$'}(echo ${'$'}resolution | awk -F 'x' '{print ${'$'}2}')
                
                # Get the current density
                density=${'$'}(wm density | awk '{if (${'$'}1 == "Physical") {print ${'$'}3}}')
                
                # Check if width and height are odd
                if [ ${'$'}((width % ${'$'}divisor)) -eq 1 ]; then
                    width=${'$'}((${'$'}width - 1))
                fi
                
                if [ ${'$'}((height % ${'$'}divisor)) -eq 1 ]; then
                    height=${'$'}((${'$'}height - 1))
                fi
                
                # Divide the width, height and density by divisor
                width=${'$'}((${'$'}width / ${'$'}divisor))
                height=${'$'}((${'$'}height / ${'$'}divisor))
                density=${'$'}((${'$'}density / ${'$'}divisor))
                
                # Set the new resolution and density
                wm size ${'$'}width"x"${'$'}height
                wm density ${'$'}density
                """
        )
}

private fun downscalebynumber(width: String) {
    Shell("su")
        .run(
            command =
                """
                # Get current screen resolution
                resolution=${'$'}(wm size | awk '{if (${'$'}1 == "Physical") {print ${'$'}3}}')
                
                # Extract width and height from resolution
                width=${'$'}(echo ${'$'}resolution | cut -d'x' -f1 | cut -d':' -f2)
                height=${'$'}(echo ${'$'}resolution | cut -d'x' -f2)
                
                # Calculate aspect ratio
                aspect_ratio=${'$'}(echo "scale=2; ${'$'}width/${'$'}height" | bc)
                
                # Calculate new height
                new_height=${'$'}(echo "scale=0; $width/${'$'}aspect_ratio" | bc)
                
                # Get current density
                density=${'$'}(wm density | awk '{if (${'$'}1 == "Physical") {print ${'$'}3}}')
                
                
                
                #Calculate new density
                density_ratio=${'$'}(echo "scale=2; ${'$'}width/$width" | bc)
                new_density=${'$'}(echo "scale=0; ${'$'}density/${'$'}density_ratio" | bc)
                
                # Set new resolution
                wm size $width"x"${'$'}new_height
                
                # Set new density
                wm density ${'$'}(printf "%.0f" ${'$'}new_density)
                
                """
        )
}

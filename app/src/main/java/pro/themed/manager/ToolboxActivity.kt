package pro.themed.manager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.jaredrummler.ktsh.Shell.Companion.SU
import pro.themed.manager.comps.HeaderRow
import pro.themed.manager.ui.theme.ThemedManagerTheme
import pro.themed.manager.ui.theme.cardcol
import java.text.DecimalFormat

class ToolboxActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemedManagerTheme {
                ToolboxPage()

            }
        }
    }

    @Preview
    @Composable
    fun ToolboxPage() {
        val context = LocalContext.current
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.cardcol
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TopAppToolbox()
                SharedPreferencesManager.initialize(applicationContext)
                val sharedPreferences: SharedPreferences =
                    context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()


                Column(Modifier.padding(horizontal = 8.dp)) {
                    HeaderRow(stringResource(R.string.disable_overlays),
                        stringResource(R.string.disable_overlays_header),
                        button1text = stringResource(R.string.all),
                        button1onClick = { SU.run("""for ol in $(cmd overlay list | grep -E '[x]' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable "$""" + """ol"; done""") },
                        button2text = stringResource(R.string.stock),
                        button2onClick = {
                            SU.run("""for ol in $(cmd overlay list | grep -E 'com.android.theme' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable "$""" + """ol"; done""")
                            SU.run("""for ol in $(cmd overlay list | grep -E 'com.android.system' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable "$""" + """ol"; done""")
                            SU.run("""for ol in $(cmd overlay list | grep -E 'com.accent' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable "$""" + """ol"; done""")
                        },
                        button3text = stringResource(R.string.themed),
                        button3onClick = { SU.run("""for ol in $(cmd overlay list | grep -E '^.x..themed.'  | sed -E 's/^....//'); do cmd overlay disable "$""" + """ol"; done""") })
                    HeaderRow(
                        stringResource(R.string.restart_systemui),
                        stringResource(R.string.systemui_restart_header),
                        button1text = stringResource(R.string.restart_now),
                        button1onClick = { SU.run("su -c killall com.android.systemui") },
                    )
                    HeaderRow(stringResource(R.string.change_system_theme),
                        stringResource(R.string.change_a_theme_of_your_device),
                        button1text = stringResource(R.string.light),
                        button1onClick = {
                            SU.run("cmd uimode night no")
                        },
                        button2text = stringResource(R.string.dark),
                        button2onClick = {
                            SU.run("cmd uimode night yes")
                        },
                        button3text = stringResource(R.string.auto),
                        button3onClick = {
                            SU.run("cmd uimode night auto")
                        })
                    HeaderRow(
                        stringResource(R.string.clear_app_caches),
                        stringResource(R.string.clears_cache_of_all_apps_data_is_safe),
                        button1text = stringResource(R.string.clear_all),
                        button1onClick = {
                            val freebefore =
                                SU.run("df -k /data | awk 'NR==2{print \$4}'\n").stdout.toString()
                                    .replace(Regex("[^0-9]"), "").toLong()
                            SU.run("pm trim-caches 100000g")
                            val freeafter =
                                SU.run("df -k /data | awk 'NR==2{print \$4}'\n").stdout.toString()
                                    .replace(Regex("[^0-9]"), "").toLong()
                            val difference: Float = freeafter.toFloat() - freebefore.toFloat()
                            val toast = when {
                                difference > 1024 * 1024 -> "${
                                    DecimalFormat("#.##").format(
                                        difference / 1024 / 1024
                                    )
                                }Gb"

                                difference > 1024 -> "${
                                    DecimalFormat("#.##").format(
                                        difference / 1024
                                    )
                                }Mb"

                                else -> "${DecimalFormat("#").format(difference)}Kb"
                            }
                            Toast.makeText(
                                context, "+$toast", Toast.LENGTH_SHORT
                            ).show()
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
                            SU.run("cmd package compile -m everything -a$forcedex2oat")
                            SU.run("cmd package compile -m everything --secondary-dex -a$forcedex2oat")
                        },
                        button2text = "Layouts",
                        button2onClick = {
                            SU.run("cmd package compile --compile-layouts -a$forcedex2oat")
                        },
                        button3text = "Reset",
                        button3onClick = {
                            SU.run("cmd package compile --reset -a")
                        },
                        showSwitch = true,
                        switchDescription = "Force recompile even if not needed",
                        isChecked = sharedPreferences.getBoolean("force_dex2oat", false),
                        onCheckedChange = {
                            if (it) {
                                editor.putBoolean("force_dex2oat", true)
                                editor.apply()
                                Toast.makeText(context, "true", Toast.LENGTH_SHORT).show()
                            } else {
                                editor.putBoolean("force_dex2oat", false)
                                editor.apply()
                                Toast.makeText(context, "false", Toast.LENGTH_SHORT).show()

                            }
                        },
                    )
                    var customresShown by remember { mutableStateOf(false) }
                    var customres by remember { mutableStateOf("") }
                    if (customresShown) {
                        AlertDialog(onDismissRequest = { /* Handle the dismissal here */ },
                            title = { Text(stringResource(R.string.enter_your_custom_resolution)) },
                            text = { Text(stringResource(R.string.downscale_warning)) },
                            buttons = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    TextField(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                        value = customres,
                                        singleLine = true,
                                        onValueChange = { customres = it },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        label = { Text("Enter custom resolution") })
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        Button(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f),
                                            onClick = {

                                                downscalebynumber(width = customres)
                                                SU.run("sleep 10 ; wm size reset ; wm density reset")
                                            }) { Text(text = stringResource(R.string.test)) }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f),
                                            onClick = {

                                                downscalebynumber(width = customres)
                                            },
                                        ) { Text(text = stringResource(R.string.apply)) }
                                    }
                                    Text(text = stringResource(R.string.close),
                                        modifier = Modifier.clickable { customresShown = false })
                                    Spacer(modifier = Modifier.height(8.dp))

                                }
                            })
                    }
                    HeaderRow(
                        stringResource(R.string.downscale),
                        stringResource(R.string.downscale_subheader),
                        button1text = "1/2",
                        button1onClick = {
                            downscalebydivisor("2")
                            if (sharedPreferences.getBoolean("resetwm", false)) {
                                Thread.sleep(10000)
                                SU.run("wm size reset ; wm density reset")
                            }

                        },
                        button2text = "1/3",
                        button2onClick = {
                            downscalebydivisor("3")
                            if (sharedPreferences.getBoolean("resetwm", false)) {
                                Thread.sleep(10000)
                                SU.run("wm size reset ; wm density reset")
                            }

                        },
                        button3text = "Set",
                        button3onClick = {

                            customresShown = true


                        },
                        button4text = "Reset",
                        button4onClick = {
                            SU.run("wm size reset ; wm density reset")

                        },
                        showSwitch = true,
                        switchDescription = stringResource(R.string.reset_to_defaults_after_10_seconds),
                        isChecked = sharedPreferences.getBoolean("resetwm", false),
                        onCheckedChange = {
                            if (it) {
                                editor.putBoolean("resetwm", true)
                                editor.apply()
                                Toast.makeText(context, "true", Toast.LENGTH_SHORT).show()
                            } else {
                                editor.putBoolean("resetwm", false)
                                editor.apply()
                                Toast.makeText(context, "false", Toast.LENGTH_SHORT).show()

                            }
                        },
                    )


                }
            }
        }

    }


    private fun downscalebydivisor(divisor: String) {

        SU.run(
            command = """
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
        SU.run(
            command = """
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

    @Composable
    fun TopAppToolbox() {
        val context = LocalContext.current
        val navController = rememberNavController()
        TopAppBar(elevation = 0.dp,
            title = { Text(text = stringResource(R.string.toolbox)) },
            backgroundColor = MaterialTheme.colors.cardcol,
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigateUp()
                    finish()
                }) {
                    Icon(Icons.Filled.ArrowBack, "backIcon")
                }
            },
            actions = {
                IconButton(onClick = {
                    context.startActivity(Intent(context, DebugActivity::class.java))

                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_bug_report_24),
                        contentDescription = "debug"
                    )
                }

                IconButton(onClick = {
                    context.startActivity(Intent(context, FaqActivity::class.java))
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_help_24),
                        contentDescription = "faq"
                    )
                }
            })
    }
}

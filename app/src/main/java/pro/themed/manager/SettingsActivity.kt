package pro.themed.manager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.comps.HeaderRow
import pro.themed.manager.ui.theme.ThemedManagerTheme
import pro.themed.manager.ui.theme.cardcol

class SettingsActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThemedManagerTheme {
                val context = LocalContext.current
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    val sharedPreferences: SharedPreferences =
                        context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()



                    Column {
                        val context = LocalContext.current
                        TopAppBarSettings()
                        HeaderRow(header = "Automatically restart SystemUI",
                            subHeader = "Enabling this will automatically restart system interface after applying overlays",
                            isChecked = sharedPreferences.getBoolean("restart_system_ui", false),
                            onCheckedChange = {
                                editor.putBoolean("restart_system_ui", it).apply()

                            },
                            showSwitch = true
                        )
                        val scope = rememberCoroutineScope()

                        HeaderRow(header = stringResource(R.string.uninstall_unused_overlays_header),
                            subHeader = stringResource(R.string.uninstall_unused_overlays_subheader),
                            button1onClick = {
                                Looper.prepare()
                                Toast.makeText(
                                    context, R.string.process_started_now_wait, Toast.LENGTH_SHORT
                                ).show()
                                Shell.SU.run(
                                    """cmd overlay list | grep themed. | grep -Ev '^.x..themed.' | sed -E 's/^....//' | while read -r ol; do
                                             path=${'$'}(cmd package path "${'$'}ol" | awk -F':' '{print ${'$'}2}')
                                             rm_path="/data/adb/modules/ThemedProject/system${'$'}(echo "${'$'}path" | sed 's/^package://')"
                                             rm "${'$'}rm_path" done"""
                                )
                                Toast.makeText(
                                    context, R.string.done, Toast.LENGTH_SHORT
                                ).show()
                            },
                            button1text = "Uninstall"
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun TopAppBarSettings() {
        val context = LocalContext.current
        val navController = rememberNavController()
        androidx.compose.material.TopAppBar(elevation = 0.dp,
            title = { Text(text = "Settings") },
            backgroundColor = MaterialTheme.colors.cardcol,
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigateUp()
                    finish()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "backIcon")
                }
            },
            actions = {
                IconButton(onClick = {
                    context.startActivity(
                        Intent(
                            context, DebugActivity::class.java
                        )
                    )

                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_bug_report_24),
                        contentDescription = "debug"
                    )
                }

                IconButton(onClick = {
                    context.startActivity(
                        Intent(
                            context, FaqActivity::class.java
                        )
                    )
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_help_24),
                        contentDescription = "faq"
                    )
                }
            })
    }
}


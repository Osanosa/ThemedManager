package pro.themed.manager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import pro.themed.manager.comps.HeaderRowWithSwitch
import pro.themed.manager.ui.theme.ThemedManagerTheme

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
                        HeaderRowWithSwitch(header = "Automatically restart SystemUI",
                            subHeader = "Enabling this will automatically restart system interface after applying overlays",
                            isChecked = sharedPreferences.getBoolean("restart_system_ui", false),
                            onCheckedChange = {
                                if (it) {
                                    editor.putBoolean("restart_system_ui", true)
                                    editor.apply()
                                    Toast.makeText(context, "true", Toast.LENGTH_SHORT).show()
                                } else {
                                    editor.putBoolean("restart_system_ui", false)
                                    editor.apply()
                                    Toast.makeText(context, "false", Toast.LENGTH_SHORT).show()

                                }
                            })

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
                    Icon(Icons.Filled.ArrowBack, "backIcon")
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


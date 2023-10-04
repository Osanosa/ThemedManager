package pro.themed.manager

import android.content.*
import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.navigation.compose.*
import pro.themed.manager.ui.theme.*

class SettingsActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThemedManagerTheme {

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
                          imageVector = ImageVector.vectorResource(id = R.drawable.baseline_bug_report_24),
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
                          imageVector = ImageVector.vectorResource(id = R.drawable.baseline_help_24),
                        contentDescription = "faq"
                    )
                }
            })
    }
}


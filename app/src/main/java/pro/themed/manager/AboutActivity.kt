@file:OptIn(
    ExperimentalFoundationApi::class
)

package pro.themed.manager

import android.content.*
import android.net.*
import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.navigation.compose.*
import pro.themed.manager.comps.*
import pro.themed.manager.ui.theme.*

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemedManagerTheme {
                AboutPage()

            }
        }
    }




    @Composable
    fun TopAppBarAbout() {
        val navController = rememberNavController()
        TopAppBar(elevation = 0.dp,
            title = { Text(text = stringResource(R.string.about)) },
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
                val context = LocalContext.current
                val webIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.t.me/ThemedSupport"))

                IconButton(onClick = { context.startActivity(webIntent) }) {
                    Image(
                          imageVector = ImageVector.vectorResource(R.drawable.telegram_logo),
                        contentDescription = "Telegram support group"
                    )
                }

                val webIntent1 = Intent(
                    Intent.ACTION_VIEW, Uri.parse("https://www.github.com/Osanosa/ThemedProject/")
                )

                IconButton(onClick = { context.startActivity(webIntent1) }) {
                    Image(
                          imageVector = ImageVector.vectorResource(R.drawable.iconmonstr_github_1),
                        contentDescription = null
                    )
                }

                val webIntent2 = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themed.pro/"))

                IconButton(onClick = { context.startActivity(webIntent2) }) {
                    Image(
                          imageVector = ImageVector.vectorResource(R.drawable.baseline_language_24),
                        contentDescription = null
                    )
                }
            }

        )
    }
}



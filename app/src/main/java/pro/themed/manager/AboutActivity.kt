package pro.themed.manager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import pro.themed.manager.ui.theme.ThemedManagerTheme

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemedManagerTheme {
                AboutPage()

            }
        }
    }

    @Preview
    @Composable
    fun AboutPage() {
        Surface {
            Column {
                TopAppBarAbout()
                Image(
                    painter = painterResource(id = R.drawable.together),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(alignment = CenterHorizontally).padding(12.dp),
                    contentScale = ContentScale.FillWidth

                )
                Text(text = "Thank you for using the app. Please report testing, it is very important!")
            }
        }

    }

    @Composable
    fun TopAppBarAbout() {
        val navController = rememberNavController()
        TopAppBar(elevation = 0.dp,
            title = { Text(text = "About") },
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
                val context = LocalContext.current
                val webIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.t.me/ThemedSupport"))

                IconButton(onClick = { context.startActivity(webIntent) }) {
                    Image(
                        painter = painterResource(R.drawable.telegram_svgrepo_com),
                        contentDescription = "Telegram support group"
                    )
                }

                val webIntent1 = Intent(
                    Intent.ACTION_VIEW, Uri.parse("https://www.github.com/Osanosa/ThemedProject/")
                )

                IconButton(onClick = { context.startActivity(webIntent1) }) {
                    Image(
                        painter = painterResource(R.drawable.iconmonstr_github_1),
                        contentDescription = null
                    )
                }

                val webIntent2 = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themed.pro/"))

                IconButton(onClick = { context.startActivity(webIntent2) }) {
                    Image(
                        painter = painterResource(R.drawable.baseline_language_24),
                        contentDescription = null
                    )
                }
            }

        )
    }
}



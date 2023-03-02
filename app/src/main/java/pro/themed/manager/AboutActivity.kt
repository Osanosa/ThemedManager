@file:OptIn(
    ExperimentalMaterialApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class
)

package pro.themed.manager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.jaredrummler.ktsh.Shell
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


    @OptIn(ExperimentalMaterialApi::class)
    @Preview
    @Composable
    fun AboutPage() {
        Surface(color = MaterialTheme.colors.cardcol) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = CenterHorizontally
            ) {
                var easteregg by remember { mutableStateOf(false) }
                var tapcount by remember { mutableStateOf(0) }
                TopAppBarAbout()
                Surface(
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth(0.5F)
                        .align(alignment = CenterHorizontally)
                        .padding(12.dp),
                    onClick = {
                        if (tapcount >= 9) easteregg = true else tapcount += 1

                    }
                ) {
                    Image(
                        contentDescription = null,
                        painter = painterResource(id = R.drawable.main_logo),
                        modifier = Modifier,
                        contentScale = ContentScale.FillWidth
                    )
                }
                val context = LocalContext.current
                val versionName = BuildConfig.VERSION_NAME
                val versionCode = BuildConfig.VERSION_CODE
                Text(text = "Themed Project by Osanosa")
                Text(
                    text = "Installed manager version is v$versionName built on $versionCode",
                    textAlign = TextAlign.Center
                )
                OutlinedButton(
                    onClick = {

                            Shell.SU.run("cd /sdcard/themeddebug")
                            Shell.SU.run("curl -O -s -L https://github.com/osanosa/themedproject/releases/latest/download/ThemedProject.zip")
                            Shell.SU.run("magisk --install-module /sdcard/themeddebug/ThemedProject.zip")

                    },
                    modifier = Modifier,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.cardcol,
                    )
                ) {
                    Row {
                        Text(text = "Download latest module")
                    }
                }

                AnimatedVisibility(easteregg, modifier = Modifier.padding(8.dp)) {
                    Column {
                        Image(
                            painter = painterResource(id = R.drawable.together),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(alignment = CenterHorizontally),
                            contentScale = ContentScale.FillWidth

                        )
                        Text(text = "Thank you for using the app. Please report testing, it is very important!")
                    }
                }

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
                        painter = painterResource(R.drawable.telegram_logo),
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



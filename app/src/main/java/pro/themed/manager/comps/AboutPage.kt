package pro.themed.manager.comps

import android.app.*
import android.content.*
import android.net.*
import android.os.*
import android.widget.*
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.jaredrummler.ktsh.*
import kotlinx.coroutines.*
import pro.themed.manager.*
import pro.themed.manager.R
import pro.themed.manager.components.CookieCard
import pro.themed.manager.ui.theme.*
import pro.themed.manager.utils.*

@Composable
fun LinkButtons(modifier: Modifier = Modifier) {
    Row(horizontalArrangement = Arrangement.SpaceAround) {
        val context = LocalContext.current
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.t.me/ThemedSupport"))

        IconButton(onClick = { context.startActivity(webIntent) }) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.telegram_logo),
                contentDescription = "Telegram support group",
            )
        }

        val webIntent1 =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/Osanosa/ThemedProject/"))

        IconButton(onClick = { context.startActivity(webIntent1) }) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.iconmonstr_github_1),
                contentDescription = null,
            )
        }

        val webIntent2 = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themed.pro/"))

        IconButton(onClick = { context.startActivity(webIntent2) }) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.baseline_language_24),
                contentDescription = null,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AboutPage() {
    CookieCard(modifier = Modifier.padding(8.dp)) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = CenterHorizontally,
        ) {
            var easteregg by remember { mutableStateOf(false) }
            Surface(
                shape = CircleShape,
                modifier =
                    Modifier.fillMaxWidth(0.5F).align(alignment = CenterHorizontally).padding(12.dp),
            ) {
                Image(
                    contentDescription = null,
                    painter = painterResource(id = R.drawable.main_logo),
                    modifier =
                        Modifier.combinedClickable(
                            onClick = {},
                            onLongClick = { easteregg = !easteregg },
                        ),
                    contentScale = ContentScale.FillWidth,
                )
            }
            LinkButtons(
                Modifier.fillMaxWidth()
                    .padding(horizontal = 72.dp, vertical = 8.dp)
                    .background(contentcol.copy(0.2f), CircleShape)
            )

            val context = LocalContext.current
            val versionName =
                context.packageManager.getPackageInfo(context.packageName, 0).versionName
            Text(text = stringResource(R.string.about_description))
            Text(
                text = stringResource(R.string.app_version, versionName.toString()),
                textAlign = TextAlign.Center,
            )
            val path =
                Environment.getExternalStorageDirectory().path +
                    "/" +
                    Environment.DIRECTORY_DOWNLOADS +
                    "/ThemedProject.zip"

            fun showBigTextMessage(context: Context, message: String) {
                val dialog =
                    AlertDialog.Builder(context)
                        .setMessage(message)
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .create()

                dialog.setOnShowListener {
                    val textView = dialog.findViewById<TextView>(android.R.id.message)
                    textView?.setTextIsSelectable(true)
                }

                dialog.show()
            }
            OutlinedButton(
                onClick = {
                    Toast.makeText(
                            context,
                            MainActivity.appContext.getString(R.string.process_started_now_wait),
                            Toast.LENGTH_SHORT,
                        )
                        .show()

                    Shell("su").run("rm $path")

                    AndroidDownloader(MainActivity.appContext)
                        .downloadFile(
                            "https://github.com/osanosa/themedproject/releases/latest/download/ThemedProject.zip"
                        )
                },
                modifier = Modifier,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = background),
            ) {
                Row { Text(text = stringResource(R.string.install_latest_module)) }
            }
            LaunchedEffect(GlobalVariables.isdownloaded) {
                if (GlobalVariables.isdownloaded) {
                    Toast.makeText(
                            context,
                            MainActivity.appContext.getString(R.string.installing),
                            Toast.LENGTH_SHORT,
                        )
                        .show()
                    val test =
                        Shell("su")
                            .run(
                                """path=$path ; while : ; do [[  -f "${'$'}path" ]] && break ; sleep 1 ; echo "fuck" ; done ;  su -c magisk --install-module $path"""
                            )
                            .stdout()
                    showBigTextMessage(context, test)
                    GlobalVariables.isdownloaded = false
                }
            }
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Support this project by watching an ad",
            )
            Button(
                onClick = { CoroutineScope(Dispatchers.Main).launch { showRewarded(context) {} } }
            ) {
                Text(text = "Show ad")
            }
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(easteregg, modifier = Modifier.padding(8.dp)) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.together),
                        contentDescription = null,
                        modifier =
                            Modifier.fillMaxWidth()
                                .align(alignment = CenterHorizontally)
                                .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.FillWidth,
                    )
                    Text(text = stringResource(R.string.feedback_thank_you))
                }
            }
        }
    }
}

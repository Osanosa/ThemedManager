package pro.themed.manager.comps


import android.app.AlertDialog
import android.content.Context
import android.os.Environment
import android.widget.TextView
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.BuildConfig
import pro.themed.manager.MyApplication
import pro.themed.manager.R
import pro.themed.manager.ui.theme.cardcol
import pro.themed.manager.utils.AndroidDownloader
import pro.themed.manager.utils.GlobalVariables


@OptIn(ExperimentalFoundationApi::class)
@Composable
    fun AboutPage() {
        Surface(color = MaterialTheme.colors.cardcol, modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = CenterHorizontally
            ) {
                var easteregg by remember { mutableStateOf(false) }
                Surface(
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth(0.5F)
                        .align(alignment = CenterHorizontally)
                        .padding(12.dp)
                ) {
                    Image(contentDescription = null,
                        painter = painterResource(id = R.drawable.main_logo),
                        modifier = Modifier.combinedClickable(onClick = {},
                            onLongClick = { easteregg = !easteregg }),
                        contentScale = ContentScale.FillWidth
                    )
                }
                val context = LocalContext.current
                val versionName = BuildConfig.VERSION_NAME
                Text(text = stringResource(R.string.about_description))
                Text(
                    text = stringResource(R.string.app_version, versionName),
                    textAlign = TextAlign.Center
                )
                val path =
                    Environment.getExternalStorageDirectory().path + "/" + Environment.DIRECTORY_DOWNLOADS + "/ThemedProject.zip"


                fun showBigTextMessage(context: Context, message: String) {
                    val dialog = AlertDialog.Builder(context).setMessage(message)
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }.create()

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
                           MyApplication.appContext.getString(R.string.process_started_now_wait),
                            Toast.LENGTH_SHORT
                        ).show()

                        Shell("su").run("rm $path")

                        AndroidDownloader(MyApplication.appContext).downloadFile("https://github.com/osanosa/themedproject/releases/latest/download/ThemedProject.zip")


                    },
                    modifier = Modifier,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.cardcol,
                    )
                ) {
                    Row {
                        Text(text = stringResource(R.string.install_latest_module))
                    }
                }
                LaunchedEffect(GlobalVariables.isdownloaded) {
                    if (GlobalVariables.isdownloaded) {
                        Toast.makeText(
                            context, MyApplication.appContext.getString(R.string.installing), Toast.LENGTH_SHORT
                        ).show()
                        val test = Shell("su").run(
                            """path=$path ; while : ; do [[  -f "${'$'}path" ]] && break ; sleep 1 ; echo "fuck" ; done ;  su -c magisk --install-module $path"""
                        ).stdout()
                        showBigTextMessage(context, test)
                        GlobalVariables.isdownloaded = false
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
                        Text(text = stringResource(R.string.feedback_thank_you))
                    }
                }

            }
        }

    }
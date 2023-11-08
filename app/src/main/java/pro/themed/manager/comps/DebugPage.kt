package pro.themed.manager.comps

import android.widget.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.jaredrummler.ktsh.*
import pro.themed.manager.*
import pro.themed.manager.R
import pro.themed.manager.ui.theme.*
import pro.themed.manager.utils.GlobalVariables.themedId

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DebugPage() {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.cardcol
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            var showDialog by rememberSaveable { mutableStateOf(false) }
            var dialogtext by rememberSaveable { mutableStateOf("empty") }
            var dialogname by rememberSaveable {
                mutableStateOf(
                    "empty".removePrefix("[x] ").removePrefix("[ ] ").removePrefix("--- ")
                )
            }
            val clipboardManager: ClipboardManager = LocalClipboardManager.current
            val scroll = rememberScrollState()
            if (showDialog) {
                Dialog(
                    onDismissRequest = {
                        showDialog = false
                    },
                ) {
                    Card {

                        Column(modifier = Modifier.padding(16.dp)) {


                            Column(
                                Modifier

                                    .fillMaxHeight(0.5f).verticalScroll(scroll)

                            ) {
                                Text(dialogtext)
                            }

                            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                                clipboardManager.setText(AnnotatedString((dialogtext)))
                            }) {
                                Text(text = "Copy")
                            }

                        }
                    }
                }
            }


            Text(text = "Your ThemedId is ${themedId}, your prevelegies are due ${
                SharedPreferencesManager.getSharedPreferences()
                    .getString("isContributorDate", "null")
            }", modifier = Modifier.clickable {
                clipboardManager.setText(AnnotatedString((themedId)))
            })
            Row {
                OutlinedButton(
                    onClick = {

                        Shell.SU.run("rm /sdcard/themeddebug/logs")
                        Shell.SU.run("mkdir /sdcard/themeddebug")
                        Shell.SU.run("mkdir /sdcard/themeddebug/logs")

                        Shell.SU.run("cmd overlay list | tee -a /sdcard/themeddebug/logs/cmdoverlaylist.txt")
                        Shell.SU.run("ls /data/adb/modules  | tee -a /sdcard/themeddebug/logs/modules.txt")

                        Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_SHORT).show()


                    },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.cardcol,
                    )

                ) {
                    Text(text = stringResource(R.string.collect_logs))
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = {
                        Shell.SU.run("rm -r /sdcard/themeddebug/files")
                        Shell.SU.run("mkdir /sdcard/themeddebug/")
                        Shell.SU.run("mkdir /sdcard/themeddebug/files")

                        Shell.SU.run("cp \$( cmd package path android | sed -E 's/^........//' ) /sdcard/themeddebug/files")
                        Shell.SU.run("cp \$( cmd package path com.android.systemui | sed -E 's/^........//' ) /sdcard/themeddebug/files")

                        Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_SHORT).show()


                    },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.cardcol,
                    )

                ) {
                    Text(text = stringResource(R.string.collect_files))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "If you're having issues of something not working you need to collect files and send zipped /sdcard/themeddebug")
            Spacer(modifier = Modifier.height(8.dp))


            if (getOverlayList().unsupportedOverlays.isNotEmpty()) {
                Text(text = "Unsupported")
                getOverlayList().unsupportedOverlays.forEach { overlay ->
                    Text(text = overlay, modifier = Modifier.combinedClickable(onClick = {
                        dialogname = overlay
                        dialogtext = Shell.SU.run(
                            "cmd overlay dump ${
                                overlay.removePrefix("[x] ").removePrefix("[ ] ")
                                    .removePrefix("--- ")
                            }"
                        ).stdout()
                        showDialog = true

                    }))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (getOverlayList().enabledOverlays.isNotEmpty()) {
                Text(text = "Enabled")
                getOverlayList().enabledOverlays.forEach { overlay ->
                    Text(text = overlay, modifier = Modifier.combinedClickable(onClick = {
                        dialogname = overlay
                        dialogtext = Shell.SU.run(
                            "cmd overlay dump ${
                                overlay.removePrefix("[x] ").removePrefix("[ ] ")
                                    .removePrefix("--- ")
                            }"
                        ).stdout()
                        showDialog = true

                    }))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (getOverlayList().disabledOverlays.isNotEmpty()) {
                Text(text = "Disabled")
                getOverlayList().disabledOverlays.forEach { overlay ->
                    Text(text = overlay, modifier = Modifier.combinedClickable(onClick = {
                        dialogname = overlay
                        dialogtext = Shell.SU.run(
                            "cmd overlay dump ${
                                overlay.removePrefix("[x] ").removePrefix("[ ] ")
                                    .removePrefix("--- ")
                            }"
                        ).stdout()
                        showDialog = true

                    }))
                }
            }
        }
    }
}
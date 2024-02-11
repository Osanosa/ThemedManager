package pro.themed.manager.comps

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.MainActivity
import pro.themed.manager.R
import pro.themed.manager.SharedPreferencesManager
import pro.themed.manager.fetchOverlayList
import pro.themed.manager.ui.theme.cardcol
import pro.themed.manager.utils.GlobalVariables.themedId

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DebugPage() {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(), color = cardcol
    ) {
        val overlayList = MainActivity.overlayList
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            var showDialog by rememberSaveable { mutableStateOf(false) }
            var dialogtext by rememberSaveable { mutableStateOf("empty") }
            var dialogname by rememberSaveable {
                mutableStateOf(
                    "empty"
                )
            }
            val clipboardManager: ClipboardManager = LocalClipboardManager.current
            val scroll = rememberScrollState()
            if (showDialog) {
                Dialog(
                    onDismissRequest = {
                        showDialog = false
                    }, properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Card {

                        Column(modifier = Modifier.padding(16.dp)) {
                            Column(
                                Modifier
                                    .weight(1f)
                                    .verticalScroll(scroll)
                            ) {
                                Text(dialogtext)
                            }

                            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                                clipboardManager.setText(AnnotatedString((dialogtext)))
                            }) {
                                Text(text = "Copy")
                            }

                            Row {
                                Button(modifier = Modifier.weight(1f), onClick = {
                                    Shell("su").run("cmd overlay enable $dialogname")
                                    MainActivity.overlayList = fetchOverlayList()
                                }) {
                                    Text(text = "Enable")
                                }
                                Spacer(modifier = Modifier.width(8.dp))

                                Button(modifier = Modifier.weight(1f), onClick = {
                                    Shell("su").run("cmd overlay disable $dialogname")
                                    MainActivity.overlayList = fetchOverlayList()
                                }) {
                                    Text(text = "Disable")
                                }
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

                        Shell("su").run("rm /sdcard/themeddebug/logs")
                        Shell("su").run("mkdir /sdcard/themeddebug")
                        Shell("su").run("mkdir /sdcard/themeddebug/logs")

                        Shell("su").run("cmd overlay list | tee -a /sdcard/themeddebug/logs/cmdoverlaylist.txt")
                        Shell("su").run("ls /data/adb/modules  | tee -a /sdcard/themeddebug/logs/modules.txt")

                        Toast.makeText(
                            context, context.getString(R.string.done), Toast.LENGTH_SHORT
                        ).show()


                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cardcol,
                    )

                ) {
                    Text(text = stringResource(R.string.collect_logs))
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = {
                        Shell("su").run("rm -r /sdcard/themeddebug/files")
                        Shell("su").run("mkdir /sdcard/themeddebug/")
                        Shell("su").run("mkdir /sdcard/themeddebug/files")

                        Shell("su").run("cp \$( cmd package path android | sed -E 's/^........//' ) /sdcard/themeddebug/files")
                        Shell("su").run("cp \$( cmd package path com.android.systemui | sed -E 's/^........//' ) /sdcard/themeddebug/files")

                        Toast.makeText(
                            context, context.getString(R.string.done), Toast.LENGTH_SHORT
                        ).show()


                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cardcol,
                    )

                ) {
                    Text(text = stringResource(R.string.collect_files))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "If you're having issues of something not working you need to collect files and send zipped /sdcard/themeddebug")
            Spacer(modifier = Modifier.height(8.dp))


            if (overlayList.unsupportedOverlays.isNotEmpty()) {
                Text(text = "Unsupported")
                overlayList.unsupportedOverlays.forEach { overlay ->
                    Text(text = overlay, modifier = Modifier.combinedClickable(onClick = {
                        dialogname =
                            overlay.removePrefix("[x] ").removePrefix("[ ] ").removePrefix("--- ")
                        dialogtext = Shell("su").run(
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

            if (overlayList.enabledOverlays.isNotEmpty()) {
                Text(text = "Enabled")
                overlayList.enabledOverlays.forEach { overlay ->
                    Text(text = overlay, modifier = Modifier.combinedClickable(onClick = {
                        dialogname =
                            overlay.removePrefix("[x] ").removePrefix("[ ] ").removePrefix("--- ")
                        dialogtext = Shell("su").run(
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
            if (overlayList.disabledOverlays.isNotEmpty()) {
                Text(text = "Disabled")
                overlayList.disabledOverlays.forEach { overlay ->
                    Text(text = overlay, modifier = Modifier.combinedClickable(onClick = {
                        dialogname =
                            overlay.removePrefix("[x] ").removePrefix("[ ] ").removePrefix("--- ")
                        dialogtext = Shell("su").run(
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
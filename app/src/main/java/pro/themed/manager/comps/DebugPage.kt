package pro.themed.manager.comps

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.R
import pro.themed.manager.SharedPreferencesManager
import pro.themed.manager.getOverlayList
import pro.themed.manager.ui.theme.cardcol
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
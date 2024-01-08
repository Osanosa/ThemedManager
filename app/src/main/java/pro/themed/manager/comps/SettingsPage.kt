package pro.themed.manager.comps

import android.content.Context
import android.content.SharedPreferences
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.R

@Composable
fun SettingsPage() {
    val context = LocalContext.current
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
    ) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()



        Column(   modifier = Modifier.verticalScroll(rememberScrollState())) {
            HeaderRow(header = "Automatically restart SystemUI",
                subHeader = "Enabling this will automatically restart system interface after applying overlays",
                isChecked = sharedPreferences.getBoolean("restart_system_ui", false),
                onCheckedChange = {
                    editor.putBoolean("restart_system_ui", it).apply()

                },
                showSwitch = true
            )
            val scope = rememberCoroutineScope()

            HeaderRow(header = stringResource(R.string.uninstall_unused_overlays_header),
                subHeader = stringResource(R.string.uninstall_unused_overlays_subheader),
                button1onClick = {
                    Looper.prepare()
                    Toast.makeText(
                        context, R.string.process_started_now_wait, Toast.LENGTH_SHORT
                    ).show()
                    Shell("su").run(
                        """cmd overlay list | grep themed. | grep -Ev '^.x..themed.' | sed -E 's/^....//' | while read -r ol; do
                                             path=${'$'}(cmd package path "${'$'}ol" | awk -F':' '{print ${'$'}2}')
                                             rm_path="/data/adb/modules/ThemedProject/system${'$'}(echo "${'$'}path" | sed 's/^package://')"
                                             rm "${'$'}rm_path" done"""
                    )
                    Toast.makeText(
                        context, R.string.done, Toast.LENGTH_SHORT
                    ).show()
                },
                button1text = "Uninstall"
            )
            DebugPage()
        }
    }
}
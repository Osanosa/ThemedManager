package pro.themed.manager

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.ui.theme.ThemedManagerTheme

class DebugActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemedManagerTheme {
                DebugPage()

            }
        }
    }


    @Preview
    @Composable
    fun DebugPage() {
        val context = LocalContext.current
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.cardcol
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                TopAppBarDebug()
                Row {
                    OutlinedButton(
                        onClick = {

                            Shell.SU.run("rm /sdcard/themeddebug/logs")
                            Shell.SU.run("mkdir /sdcard/themeddebug")
                            Shell.SU.run("mkdir /sdcard/themeddebug/logs")

                            Shell.SU.run("cmd overlay list | tee -a /sdcard/themeddebug/logs/cmdoverlaylist.txt")
                            Shell.SU.run("ls /data/adb/modules  | tee -a /sdcard/themeddebug/logs/modules.txt")

                            Toast.makeText(context, getString(R.string.done), Toast.LENGTH_SHORT)
                                .show()


                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
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

                            Toast.makeText(context, getString(R.string.done), Toast.LENGTH_SHORT)
                                .show()


                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.cardcol,
                        )

                    ) {
                        Text(text = stringResource(R.string.collect_files))
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Unsupported")
                getOverlayList().unsupportedOverlays.forEach { overlay ->
                    Text(
                        text = overlay,
                    )
                }

                Text(text = "Enabled")
                getOverlayList().enabledOverlays.forEach { overlay ->
                    Text(
                        text = overlay,
                    )
                }
                Text(text = "Disabled")
                getOverlayList().disabledOverlays.forEach { overlay ->
                    Text(
                        text = overlay,
                    )
                }

            }
        }
    }

    @Composable
    fun TopAppBarDebug() {
        val context = LocalContext.current
        val navController = rememberNavController()
        TopAppBar(
            elevation = 0.dp,
            title = { Text(text = stringResource(R.string.debug)) },
            backgroundColor = MaterialTheme.colors.cardcol,
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigateUp()
                    finish()
                }) {
                    Icon(Icons.Filled.ArrowBack, "backIcon")
                }
            },
        )
    }
}



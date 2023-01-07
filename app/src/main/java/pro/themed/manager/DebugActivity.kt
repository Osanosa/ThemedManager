package pro.themed.manager

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(8.dp)) {
                TopAppBarDebug()

                Row {
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(
                                context, "Creating directory", Toast.LENGTH_SHORT
                            ).show()
                            Shell.SU.run("mkdir /sdcard/themeddebug")
                            Toast.makeText(
                                context, "Checking overlays", Toast.LENGTH_SHORT
                            ).show()
                            Shell.SU.run("cmd overlay list | tee -a /sdcard/themeddebug/cmdoverlaylist.txt")
                            Toast.makeText(
                                context, "Checking installed modules", Toast.LENGTH_SHORT
                            ).show()
                            Shell.SU.run("cd /data/adb/modules ; ls | tee -a /sdcard/themeddebug/modules.txt")
                            Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()


                        }, modifier = Modifier
                            .fillMaxWidth()
                        .weight(1f),
                        shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.cardcol,
                        )

                    ) {
                        Text(text = "Collect logs")
                    }
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(
                                context, "Creating directory", Toast.LENGTH_SHORT
                            ).show()
                            Shell.SU.run("mkdir /sdcard/themeddebug")
                            Toast.makeText(
                                context, "Copying framework-res.apk", Toast.LENGTH_SHORT
                            ).show()
                            Shell.SU.run("cp \$( cmd package path android | sed -E 's/^........//' ) /sdcard/themeddebug/")
                            Toast.makeText(
                                context, "Copying SystemUI.apk", Toast.LENGTH_SHORT
                            ).show()
                            Shell.SU.run("cp \$( cmd package path com.android.systemui | sed -E 's/^........//' ) /sdcard/themeddebug/")



                            Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()


                        }, modifier = Modifier
                            .fillMaxWidth()
                        .weight(1f),
                         shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.cardcol,
                        )

                    ) {
                        Text(text = "Collect files")
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))

            }
        }

    }

    @Composable
    fun TopAppBarDebug() {
        val context = LocalContext.current
        val navController = rememberNavController()
        TopAppBar(
            elevation = 0.dp,
            title = { Text(text = "Debug") },
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



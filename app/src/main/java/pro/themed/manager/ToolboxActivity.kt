package pro.themed.manager

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.ui.theme.ThemedManagerTheme


class ToolboxActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemedManagerTheme {
                SettingsPage()

            }
        }
    }


    @Preview
    @Composable
    fun SettingsPage() {
        val context = LocalContext.current
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.cardcol
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TopAppBarSettings()

                Column(Modifier.padding(horizontal = 8.dp)) {

                    Card(
                        border = BorderStroke(
                            width = 1.dp, color = MaterialTheme.colors.bordercol
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        elevation = (0.dp),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp, vertical = 4.dp
                                    ),
                                    text = "Disable overlays",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = "Disable all overlays, stock  android themes, or only themed project's",
                                fontSize = 18.sp
                            )

                            Row {
                                Spacer(modifier = Modifier.width(8.dp))
                                var isDialogShown by remember { mutableStateOf(false) }
                                if (isDialogShown) {
                                    AlertDialog(onDismissRequest = { /* Handle the dismissal here */ },
                                        title = { Text("Disabling all overlays may cause some system settings to reset") },
                                        text = { Text("Are you sure you want to proceed?") },
                                        buttons = {
                                            Row() {


                                                Button(onClick = {
                                                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E '[x]' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
                                                    Toast.makeText(
                                                        context,
                                                        "Done",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()

                                                }) { Text(text = "Yes") }
                                                Button(
                                                    onClick = {
                                                        isDialogShown = false
                                                    },
                                                ) { Text(text = "No") }
                                            }
                                        }
                                    )
                                }

                                OutlinedButton(
                                    onClick = {


                                        isDialogShown = true


                                        /* Toast.makeText(
                                             context,
                                             "Process started, now wait",
                                             Toast.LENGTH_SHORT
                                         ).show()
                                         Shell.SU.run("for ol in \$(cmd overlay list | grep -E '[x]' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
                                         Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()
                                    */
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.cardcol,
                                        contentColor = Color.Red
                                    )
                                ) {
                                    Text(text = "All")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        Toast.makeText(
                                            context, "Process started, now wait", Toast.LENGTH_SHORT
                                        ).show()
                                        Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'com.android.theme' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
                                        Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'com.android.system' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
                                        Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'com.accent' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
                                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()


                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.cardcol,
                                    )

                                ) {
                                    Text(text = "Stock")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        Toast.makeText(
                                            context, "Process started, now wait", Toast.LENGTH_SHORT
                                        ).show()
                                        Shell.SU.run("for ol in \$(cmd overlay list | grep -E '^....themed.' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
                                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()


                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.cardcol,
                                    )

                                ) {
                                    Text(text = "Themed")
                                }
                                Spacer(modifier = Modifier.width(8.dp))

                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Card(
                        border = BorderStroke(
                            width = 1.dp, color = MaterialTheme.colors.bordercol
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        elevation = (0.dp),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp, vertical = 8.dp
                                    ),
                                    text = "Restart SystemUI",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = "Restarts SystemUI in case if your rom doesn't refreshes it automatically",
                                fontSize = 18.sp
                            )

                            Row {
                                Spacer(modifier = Modifier.width(8.dp))

                                OutlinedButton(
                                    onClick = {
                                        Shell.SU.run("su -c killall com.android.systemui")

                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.cardcol,
                                    )

                                ) {
                                    Text(text = "Restart now")
                                }
                                Spacer(modifier = Modifier.width(8.dp))

                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))

                    Card(
                        border = BorderStroke(
                            width = 1.dp, color = MaterialTheme.colors.bordercol
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        elevation = (0.dp),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp, vertical = 8.dp
                                    ),
                                    text = "Change system theme",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = "Change a theme of your device",
                                fontSize = 18.sp
                            )

                            Row {
                                Spacer(modifier = Modifier.width(8.dp))

                                OutlinedButton(
                                    onClick = {
                                        Shell.SU.run("cmd uimode night no")

                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.cardcol,
                                    )
                                ) {
                                    Row {
/*
                                    Image(
                                        painter = painterResource(R.drawable.baseline_light_mode_24),
                                        contentDescription = null,
                                        contentScale = ContentScale.Fit
                                    )
*/                                    Text(text = "Light")

                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        Shell.SU.run("cmd uimode night yes")

                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.cardcol,
                                    )

                                ) {
                                    Text(text = "Dark")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        Shell.SU.run("cmd uimode night auto")

                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.cardcol,
                                    )

                                ) {
                                    Text(text = "Auto")
                                }
                                Spacer(modifier = Modifier.width(8.dp))

                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))

                    Card(
                        border = BorderStroke(
                            width = 1.dp, color = MaterialTheme.colors.bordercol
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        elevation = (0.dp),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp, vertical = 8.dp
                                    ),
                                    text = "Clear app caches",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = "Clears cache of all apps (data is safe)",
                                fontSize = 18.sp
                            )

                            Row {
                                Spacer(modifier = Modifier.width(8.dp))

                                OutlinedButton(
                                    onClick = {
                                        Shell.SU.run("pm trim-caches 100g")
                                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()

                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.cardcol,
                                    )

                                ) {
                                    Text(text = "Clear all")
                                }
                                Spacer(modifier = Modifier.width(8.dp))

                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))

                    Card(
                        border = BorderStroke(
                            width = 1.dp, color = MaterialTheme.colors.bordercol
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        elevation = (0.dp),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp, vertical = 8.dp
                                    ),
                                    text = "Dex2oat",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = "Pre-compiles code of all installed apps to reduce lag ans stutter",
                                fontSize = 18.sp
                            )

                            Row(modifier = Modifier) {
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        Toast.makeText(
                                            context, "Process started, now wait", Toast.LENGTH_SHORT
                                        ).show()
                                        Toast.makeText(
                                            context,
                                            "This will take a lot of time at first time, please be patient",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Toast.makeText(
                                            context,
                                            "You can minimize app to background",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Toast.makeText(
                                            context,
                                            "If your phone freezes badly you can safely reboot it to stop the process",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        Shell.SU.run("cmd package compile -m everything -a")
                                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()

                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.cardcol,
                                    )

                                ) {
                                    Text(text = "Everything")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        Toast.makeText(
                                            context, "Process started, now wait", Toast.LENGTH_SHORT
                                        ).show()
                                        Toast.makeText(
                                            context,
                                            "This will take a lot of time at first time, please be patient",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Toast.makeText(
                                            context,
                                            "You can minimize app to background",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Toast.makeText(
                                            context,
                                            "If your phone freezes badly you can safely reboot it to stop the process",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        Shell.SU.run("cmd package compile --compile-layouts -a")
                                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()

                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.cardcol,
                                    )

                                ) {
                                    Text(text = "Layouts")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        Toast.makeText(
                                            context, "Process started, now wait", Toast.LENGTH_SHORT
                                        ).show()
                                        Toast.makeText(
                                            context,
                                            "This may take a lot of time, please be patient",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Toast.makeText(
                                            context,
                                            "You can minimize app to background",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        Shell.SU.run("cmd package compile --reset -a")
                                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()

                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.cardcol,
                                    )

                                ) {
                                    Text(text = "Reset")
                                }
                                Spacer(modifier = Modifier.width(8.dp))

                            }
                        }
                    }
                }
            }
        }

    }

    @Composable
    fun TopAppBarSettings() {
        val context = LocalContext.current
        val navController = rememberNavController()
        TopAppBar(elevation = 0.dp,
            title = { Text(text = "Settings") },
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
                IconButton(onClick = {
                    context.startActivity(Intent(context, DebugActivity::class.java))

                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_bug_report_24),
                        contentDescription = "debug"
                    )
                }

                IconButton(onClick = {
                    context.startActivity(Intent(context, FaqActivity::class.java))
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_help_24),
                        contentDescription = "faq"
                    )
                }
            })
    }
}



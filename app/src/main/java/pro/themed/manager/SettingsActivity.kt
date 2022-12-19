package pro.themed.manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.ui.theme.ThemedManagerTheme

class SettingsActivity : ComponentActivity() {
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
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.cardcol
        ) {
            Column {
                TopAppBarSettings()
                Card(
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.bordercol),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 0.dp),
                    elevation = (0.dp),
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = MaterialTheme.colors.cardcol
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                text = "Disable overlays",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = "Disable all overlays, stock android themes, or only themed project's",
                            fontSize = 18.sp
                        )

                        Row {
                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedButton(
                                onClick = {
                                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E '[x]' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")

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
                                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'com.android.theme' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
                                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'com.android.system' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")

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
                                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E '^....themed.' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")

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
                Card(
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.bordercol),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 0.dp),
                    elevation = (0.dp),
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = MaterialTheme.colors.cardcol
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
                Card(
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.bordercol),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 0.dp),
                    elevation = (0.dp),
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = MaterialTheme.colors.cardcol
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
                Card(
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.bordercol),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 0.dp),
                    elevation = (0.dp),
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = MaterialTheme.colors.cardcol
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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

                        Row {
                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedButton(
                                onClick = {
                                    Shell.SU.run("cmd package compile -m speed -f -a")

                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.cardcol,
                                )

                            ) {
                                Text(text = "Odex now")
                            }
                            Spacer(modifier = Modifier.width(8.dp))

                        }
                    }
                }

            }
        }

    }

    @Composable
    fun TopAppBarSettings() {
        val navController = rememberNavController()
        TopAppBar(
            elevation = 0.dp,
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


            )
    }
}



package pro.themed.filesizes

import android.content.*
import android.net.*
import android.os.*
import android.provider.*
import android.widget.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.google.accompanist.permissions.*
import com.jaredrummler.ktsh.*

@OptIn(ExperimentalPermissionsApi::class) @Preview(showBackground = true) @Composable fun ModeDialog() {
    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(true) }
    if (isVisible) Dialog(onDismissRequest = { isVisible = false }) {
        CookieCard {
            //header
            Text(text = "Choose operation mode",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp))
            val sharedPref = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

            var mode by remember { mutableIntStateOf(0) }
            Row(Modifier
                .fillMaxWidth()
                .clickable { mode = 0 }, verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = mode == 0, onClick = { mode = 0 })
                Text(text = "Root (recommended)", style = MaterialTheme.typography.bodyLarge)
            }
            Row(Modifier
                .fillMaxWidth()
                .clickable { mode = 1 }, verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = mode == 1, onClick = { mode = 1 })
                Text(text = "File access permission", style = MaterialTheme.typography.bodyLarge)
            }
            val readPermissionState =
                rememberMultiplePermissionsState(listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE))


            //row button green
            Row(Modifier
                .fillMaxWidth()
                .clickable {
                    isVisible = false
                    sharedPref.edit().putInt("mode", mode).apply()
                    when (mode) {
                        0 -> {
                            Shell.SH.run("su") {
                                onStdErr = { line: String ->
                                    if (Looper.myLooper() == null) Looper.prepare()
                                    Toast.makeText(context, line, Toast.LENGTH_SHORT).show()
                                    isVisible = true
                                }
                            }
                        }
                        1 -> {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                readPermissionState.launchMultiplePermissionRequest()
                                if (!readPermissionState.allPermissionsGranted) {
                                    Toast.makeText(context, "No permission, change in settings", Toast.LENGTH_SHORT).show()
                                    isVisible = true
                                }
                                else {
                                    Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else {
                                if (!Environment.isExternalStorageManager()) {
                                    Toast.makeText(context, "No permission, change in settings", Toast.LENGTH_SHORT).show()
                                    isVisible = true
                                }
                            }
                            if (!Environment.isExternalStorageManager()) {
                                context.startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                    Uri.parse("package:pro.themed.filesizes")))

                            }
                        }
                    }
                }
                .background(color = Color.hsl(140f, 1f, 0.5f)),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                //big text proceed
                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(text = "Proceed", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(8.dp))
            }

        }
    }
}
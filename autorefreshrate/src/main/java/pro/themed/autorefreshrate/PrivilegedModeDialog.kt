package pro.themed.autorefreshrate

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch

@Composable
fun PrivilegedModeDialog(
    onDismiss: () -> Unit,
    onModeSelected: (PrivilegedCommandHelper.ExecutionMode) -> Unit,
    helper: PrivilegedCommandHelper
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var availabilityStatus by remember { mutableStateOf<PrivilegedCommandHelper.AvailabilityStatus?>(null) }
    var selectedMode by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            availabilityStatus = helper.checkAvailability()
            isLoading = false
            
            // Auto-select best available option
            availabilityStatus?.let { status ->
                selectedMode = when {
                    status.shizukuAvailable && status.shizukuPermissionGranted -> 1
                    status.rootAvailable -> 0
                    else -> -1
                }
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Choose Execution Mode",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (isLoading) {
                    Text("Checking availability...")
                } else {
                    availabilityStatus?.let { status ->
                        if (!status.rootAvailable && !status.shizukuAvailable) {
                            // Neither available - show error and Shizuku download option
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Warning,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = "Privileged Access Required",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "This app requires either root access or Shizuku to function properly. Neither is currently available.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.padding(8.dp))

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api")
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Get Shizuku from Play Store")
                            }
                        } else {
                            // Show available options
                            if (status.rootAvailable) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedMode = 0 },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedMode == 0,
                                        onClick = { selectedMode = 0 }
                                    )
                                    Column {
                                        Text(
                                            text = "Root Access",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium
                                        )
                                        status.rootVersion?.let { version ->
                                            Text(
                                                text = "Version: $version",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            if (status.shizukuAvailable) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { 
                                            if (status.shizukuPermissionGranted) {
                                                selectedMode = 1
                                            } else {
                                                // Request permission
                                                helper.requestShizukuPermission(1001)
                                                Toast.makeText(context, "Please grant Shizuku permission", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedMode == 1,
                                        onClick = { 
                                            if (status.shizukuPermissionGranted) {
                                                selectedMode = 1
                                            } else {
                                                helper.requestShizukuPermission(1001)
                                                Toast.makeText(context, "Please grant Shizuku permission", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        enabled = status.shizukuPermissionGranted
                                    )
                                    Column {
                                        Text(
                                            text = "Shizuku",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = if (status.shizukuPermissionGranted) "Permission granted" else "Permission required",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (status.shizukuPermissionGranted) 
                                                Color.Green else MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.padding(8.dp))

                            // Proceed button
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val mode = when (selectedMode) {
                                            0 -> PrivilegedCommandHelper.ExecutionMode.ROOT
                                            1 -> PrivilegedCommandHelper.ExecutionMode.SHIZUKU
                                            else -> PrivilegedCommandHelper.ExecutionMode.NONE
                                        }
                                        
                                        if (mode != PrivilegedCommandHelper.ExecutionMode.NONE) {
                                            // Save preference
                                            val sharedPref = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                                            sharedPref.edit().putInt("execution_mode", selectedMode).apply()
                                            
                                            onModeSelected(mode)
                                            onDismiss()
                                        } else {
                                            Toast.makeText(context, "Please select an available option", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .background(
                                        color = if (selectedMode >= 0) Color.hsl(140f, 1f, 0.5f) 
                                               else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = if (selectedMode >= 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Proceed",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (selectedMode >= 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


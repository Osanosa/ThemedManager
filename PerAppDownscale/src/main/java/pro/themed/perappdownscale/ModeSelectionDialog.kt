package pro.themed.perappdownscale

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ModeSelectionDialog(
    onDismiss: () -> Unit,
    onModeSelected: (PrivilegedCommandHelper.ExecutionMode) -> Unit,
    helper: PrivilegedCommandHelper
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    var availabilityStatus by remember { mutableStateOf<PrivilegedCommandHelper.AvailabilityStatus?>(null) }
    
    // Read current mode from SharedPreferences to show correct selection
    val currentSavedMode = remember {
        val sharedPref = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        sharedPref.getInt("execution_mode", -1)
    }
    var selectedMode by remember { mutableIntStateOf(currentSavedMode) }
    var isLoading by remember { mutableStateOf(true) }

    // Function to update status
    fun updateStatus(skipRootIfDenied: Boolean = false) {
        scope.launch {
            try {
                Log.d("ModeSelectionDialog", "Starting status update (skipRootIfDenied=$skipRootIfDenied)...")
                val newStatus = helper.checkAvailability(context, skipRootIfDenied)
                Log.d("ModeSelectionDialog", "Status update successful: $newStatus")
                availabilityStatus = newStatus
                isLoading = false
                
                // Auto-select best available option only if no mode was previously saved
                if (currentSavedMode == -1 && selectedMode == -1) {
                    selectedMode = when {
                        newStatus.rootAvailable -> 0
                        newStatus.shizukuAvailable && newStatus.shizukuPermissionGranted -> 1
                        else -> -1
                    }
                    Log.d("ModeSelectionDialog", "Auto-selected mode: $selectedMode")
                }
            } catch (e: Exception) {
                Log.e("ModeSelectionDialog", "Status update failed", e)
                // If check fails completely, create a fallback status
                availabilityStatus = PrivilegedCommandHelper.AvailabilityStatus(
                    rootAvailable = false,
                    rootVersion = null,
                    rootPermissionDenied = true, // Assume permission denied if check fails
                    shizukuInstalled = false,
                    shizukuAvailable = false,
                    shizukuPermissionGranted = false
                )
                isLoading = false
            }
        }
    }
    
    // Function to install Shizuku with fallback
    fun installShizuku() {
        try {
            // Try Play Store first
            val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=moe.shizuku.privileged.api")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(playStoreIntent)
        } catch (e: Exception) {
            try {
                // Fallback to browser Play Store
                val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(browserIntent)
            } catch (e2: Exception) {
                // Final fallback to GitHub releases
                val githubIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://github.com/RikkaApps/Shizuku/releases")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(githubIntent)
            }
        }
    }
    
    // Function to open Shizuku app for configuration
    fun openShizukuApp() {
        try {
            val shizukuIntent = context.packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api")
            if (shizukuIntent != null) {
                shizukuIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(shizukuIntent)
            } else {
                Toast.makeText(context, "Shizuku app not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to open Shizuku app", Toast.LENGTH_SHORT).show()
        }
    }

    // Initial status check
    LaunchedEffect(Unit) {
        updateStatus()
    }
    
    // Periodic status updates every 5 seconds (only when app is active)
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            // Only update if the app is in the foreground
            if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                updateStatus(skipRootIfDenied = true)
            }
        }
    }
    
    // Lifecycle-based updates (on resume) - skip root if previously denied
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                updateStatus(skipRootIfDenied = true)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Choose Operation Mode",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                when {
                    isLoading -> {
                        Text("Checking availability...")
                    }
                    availabilityStatus == null -> {
                        // Status is null - show error state with retry
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Failed to check device capabilities",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Button(
                                onClick = { updateStatus() }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                    else -> {
                        val status = availabilityStatus!!
                        
                        // Root option - always shown
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    if (status.rootAvailable) {
                                        selectedMode = 0
                                    } else {
                                        Toast.makeText(context, "Root access not available on this device", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedMode == 0,
                                onClick = { 
                                    if (status.rootAvailable) {
                                        selectedMode = 0
                                    } else {
                                        Toast.makeText(context, "Root access not available on this device", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                enabled = status.rootAvailable
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (status.rootAvailable) "Root (Recommended)" else "Root",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = if (status.rootAvailable) MaterialTheme.colorScheme.onSurface 
                                           else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = when {
                                        status.rootAvailable && !status.rootPermissionDenied -> status.rootVersion ?: "Available"
                                        status.rootAvailable && status.rootPermissionDenied -> "Permission denied"
                                        status.rootAvailable -> "Ready to test"
                                        else -> "Not available"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = when {
                                        status.rootAvailable && !status.rootPermissionDenied -> Color.Green
                                        status.rootAvailable && status.rootPermissionDenied -> MaterialTheme.colorScheme.error
                                        status.rootAvailable -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.error
                                    }
                                )
                            }
                        }

                        // Shizuku option - always shown
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    when {
                                        status.shizukuAvailable && status.shizukuPermissionGranted -> {
                                            selectedMode = 1
                                        }
                                        status.shizukuAvailable -> {
                                            helper.requestShizukuPermission(1001)
                                            Toast.makeText(context, "Please grant Shizuku permission", Toast.LENGTH_SHORT).show()
                                            // Update status after permission request
                                            scope.launch {
                                                delay(1500)
                                                updateStatus()
                                            }
                                        }
                                        status.shizukuInstalled -> {
                                            Toast.makeText(context, "Please start Shizuku service first", Toast.LENGTH_LONG).show()
                                        }
                                        else -> {
                                            Toast.makeText(context, "Please install Shizuku first", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedMode == 1,
                                onClick = { 
                                    when {
                                        status.shizukuAvailable && status.shizukuPermissionGranted -> {
                                            selectedMode = 1
                                        }
                                        status.shizukuAvailable -> {
                                            helper.requestShizukuPermission(1001)
                                            Toast.makeText(context, "Please grant Shizuku permission", Toast.LENGTH_SHORT).show()
                                            // Update status after permission request
                                            scope.launch {
                                                delay(1500)
                                                updateStatus()
                                            }
                                        }
                                        status.shizukuInstalled -> {
                                            Toast.makeText(context, "Please start Shizuku service first", Toast.LENGTH_LONG).show()
                                        }
                                        else -> {
                                            Toast.makeText(context, "Please install Shizuku first", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                enabled = status.shizukuAvailable && status.shizukuPermissionGranted
                            )
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Shizuku",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = if (status.shizukuAvailable && status.shizukuPermissionGranted) 
                                               MaterialTheme.colorScheme.onSurface
                                           else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = when {
                                        status.shizukuAvailable && status.shizukuPermissionGranted -> "Ready"
                                        status.shizukuAvailable -> "Permission required"
                                        status.shizukuInstalled -> "Please configure Shizuku first"
                                        else -> "Not installed"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = when {
                                        status.shizukuAvailable && status.shizukuPermissionGranted -> Color.Green
                                        status.shizukuAvailable -> MaterialTheme.colorScheme.primary
                                        status.shizukuInstalled -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.error
                                    }
                                )
                            }
                            
                            // Action icons based on Shizuku status
                            when {
                                // Install icon - show if not installed
                                !status.shizukuInstalled -> {
                                    IconButton(
                                        onClick = { 
                                            installShizuku()
                                            // Update status after a short delay to check if install started
                                            scope.launch {
                                                delay(1000)
                                                updateStatus()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.apk_install_24px),
                                            contentDescription = "Install Shizuku",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                // Configuration icon - show if installed but not configured
                                status.shizukuInstalled && !status.shizukuAvailable -> {
                                    IconButton(
                                        onClick = { 
                                            openShizukuApp()
                                            // Update status after user returns
                                            scope.launch {
                                                delay(1000)
                                                updateStatus()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.mobile_wrench_24px),
                                            contentDescription = "Configure Shizuku",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }



                        // Proceed button - always shown
                        Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scope.launch {
                                            val mode = when (selectedMode) {
                                                0 -> PrivilegedCommandHelper.ExecutionMode.ROOT
                                                1 -> PrivilegedCommandHelper.ExecutionMode.SHIZUKU
                                                else -> PrivilegedCommandHelper.ExecutionMode.NONE
                                            }
                                            
                                            when (selectedMode) {
                                                0 -> {
                                                    // Test root permissions only when user proceeds with root
                                                    if (status.rootAvailable) {
                                                        Log.d("ModeSelectionDialog", "Testing root permissions...")
                                                        val hasRootPermission = helper.testRootPermissions()
                                                        if (hasRootPermission) {
                                                            // Save preference and proceed
                                                            val sharedPref = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                                                            sharedPref.edit().putInt("execution_mode", selectedMode).apply()
                                                            onModeSelected(mode)
                                                            onDismiss()
                                                        } else {
                                                            Toast.makeText(context, "Root permission denied", Toast.LENGTH_SHORT).show()
                                                            // Update status to reflect the denial
                                                            updateStatus(skipRootIfDenied = false)
                                                        }
                                                    } else {
                                                        Toast.makeText(context, "Root access not available on this device", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                                1 -> {
                                                    // Check if Shizuku is ready
                                                    if (status.shizukuAvailable && status.shizukuPermissionGranted) {
                                                        // Save preference and proceed
                                                        val sharedPref = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                                                        sharedPref.edit().putInt("execution_mode", selectedMode).apply()
                                                        onModeSelected(mode)
                                                        onDismiss()
                                                    } else if (!status.shizukuInstalled) {
                                                        Toast.makeText(context, "Please install Shizuku first", Toast.LENGTH_SHORT).show()
                                                    } else if (!status.shizukuAvailable) {
                                                        Toast.makeText(context, "Please start Shizuku service", Toast.LENGTH_SHORT).show()
                                                    } else if (!status.shizukuPermissionGranted) {
                                                        Toast.makeText(context, "Please grant Shizuku permission", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                                else -> {
                                                    Toast.makeText(context, "Please select a mode", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }
                                    .background(
                                        color = if (selectedMode >= 0) Color.hsl(140f, 1f, 0.5f) 
                                               else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(16.dp),
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
                                    color = if (selectedMode >= 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

}



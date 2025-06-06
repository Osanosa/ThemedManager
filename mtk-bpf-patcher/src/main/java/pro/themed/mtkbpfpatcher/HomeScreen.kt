package pro.themed.mtkbpfpatcher

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.font.FontFamily

@Composable
fun HomeScreen(
    currentBootSlot: String?,
    magiskBootAvailable: Boolean,
    onPickFile: () -> Unit,
    onSelectOutputLocation: () -> Unit,
    onPatchCurrentBoot: (Boolean) -> Unit,
    onPatchFile: (String, String, Boolean) -> Unit,
    inputFile: String?,
    outputFile: String
) {
    var verifyMode by remember { mutableStateOf(false) }
    var showMagiskbootDialog by remember { mutableStateOf(false) }
    
    // Get ViewModel to access logs
    val viewModel: KernelPatcherViewModel = viewModel()
    val extractionLog = viewModel.getMagiskbootExtractionLog()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MTK BPF Kernel Patcher",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 8.dp),
            maxLines = 1
        )
        val context = LocalContext.current
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.r0rt1z2),
                    contentDescription = "R0rt1z2",
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp, color = Color(0xFF8000FF), shape = CircleShape
                        )
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse("https://github.com/R0rt1z2")
                            context.startActivity(intent)
                        }

                )
                Text(
                    text = "R0rt1z2",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Icon(
                imageVector = Icons.Default.Close, contentDescription = "X",

                modifier = Modifier.padding(bottom = 20.dp)
            )

            Column(
                modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.osanosa),
                    contentDescription = "Osanosa",
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp, color = Color(0xFFFFBF00), shape = CircleShape
                        )
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse("https://github.com/Osanosa")
                            context.startActivity(intent)
                        }

                )
                Text(
                    text = "Osanosa",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        Text(
            text = "Honorable mentions: Claude 3.7, Slim K, TopJhonWu, Jared Rummler",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://t.me/Papacu_GSI_MODs/5941")
                    context.startActivity(intent)
                }) {
                Text(text = "Join support group")
            }
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://send.monobank.ua/jar/7hAzjJjvRh")
                    context.startActivity(intent)
                }) {
                Text(text = "Donate 😻")
            }

        }


        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp), color = MaterialTheme.colorScheme.primary
        )
        
        // MagiskBoot status - only show warning if not available
        if (!magiskBootAvailable) {
            OutlinedButton(
                onClick = { showMagiskbootDialog = true },
                shape = CircleShape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Red
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 2.dp,
                    color = Color.Red
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        modifier = Modifier.size(20.dp)
                    )
                    Text("Binary Issue")
                }
            }
            
            if (showMagiskbootDialog) {
                AlertDialog(
                    onDismissRequest = { showMagiskbootDialog = false },
                    title = { Text("MagiskBoot Initialization Logs") },
                    text = { 
                        Column {
                            Text(
                                "The app's bundled magiskboot binary is not functioning properly. " +
                                "This may affect patching operations."
                            )
                            
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            
                            // Show scrollable log content
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 300.dp)
                                    .padding(top = 8.dp)
                            ) {
                                // Split log by lines and display
                                items(extractionLog.split("\n")) { line ->
                                    Text(
                                        text = line,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showMagiskbootDialog = false }) {
                            Text("Close")
                        }
                    },
                    dismissButton = {
                        val context1 = LocalContext.current
                        OutlinedButton(
                            onClick = {
                                // Copy logs to clipboard
                                val clipboardManager = context1.getSystemService(
                                    Context.CLIPBOARD_SERVICE
                                ) as ClipboardManager
                                val clip = ClipData.newPlainText("Extraction Logs", extractionLog)
                                clipboardManager.setPrimaryClip(clip)
                                Toast.makeText(context1, "Logs copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text("Copy Logs")
                        }
                    }
                )
            }
        }
        
        // Boot slot info
        if (currentBootSlot != null) {
            Text(
                text = "Current boot slot: $currentBootSlot",
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Verification mode toggle
        Text(
            text = "Retrieve current boot partition:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )

        // Patch current boot button
        Button(
            onClick = { clear(); onPatchCurrentBoot(verifyMode) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(if (verifyMode) "Verify Current Boot Partition" else "Patch Current Boot Partition")
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        // File selection section
        Text(
            text = "Or select a file to patch:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )

        Button(
            onClick = onPickFile, modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Select Input File")
        }

        // Display selected file
        inputFile?.let {
            Text(
                text = "Selected: ${it.split("/").last()}", modifier = Modifier.padding(8.dp)
            )

            // Output file path
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = outputFile,
                    onValueChange = { /* Handle manually entered output path */ },
                    label = { Text("Output file path") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Browse",
                            modifier = Modifier.clickable { onSelectOutputLocation() })
                    },
                    modifier = Modifier.weight(1f)

                )

            }

            // Patch button
            Button(
                onClick = { clear(); onPatchFile(inputFile, outputFile, verifyMode) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(if (verifyMode) "Verify File" else "Patch File")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = "Verification Mode (only repackage without patching; used for debugging)",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(0.7f)
            )

            Switch(
                checked = verifyMode, onCheckedChange = { verifyMode = it })
        }

    }
}
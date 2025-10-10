package pro.themed.perappdownscale

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CommandModeSelectionDialog(
    currentMode: CommandMode,
    onDismiss: () -> Unit,
    onModeSelected: (CommandMode) -> Unit,
) {
    var selectedMode by remember { mutableStateOf(currentMode) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
    ) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Operation Mode",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                Text(
                    text =
                        "Select how game interventions are applied. Changing modes will require reconfiguring all apps.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                Column(
                    modifier =
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedMode = CommandMode.DEFAULT
                                onModeSelected(CommandMode.DEFAULT)
                            }
                            .background(
                                color =
                                    if (selectedMode == CommandMode.DEFAULT)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.primaryContainer.copy(0.3f),
                            )
                            .padding(8.dp)
                ) {
                    // Default Mode Option
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedMode == CommandMode.DEFAULT,
                            onClick = {
                                selectedMode = CommandMode.DEFAULT
                                onModeSelected(CommandMode.DEFAULT)
                            },
                        )
                        Text(
                            text = "Default Mode",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color =
                                if (selectedMode == CommandMode.DEFAULT)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                   // Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text =
                            "- Uses cmd game commands\n- Recommended for most users\n- Works reliably on Android 14 onwards\n- More stable and well-tested\n- Resets upon reboot",
                        style = MaterialTheme.typography.bodySmall,
                        color =
                            if (selectedMode == CommandMode.DEFAULT)
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier =
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedMode = CommandMode.ALTERNATIVE
                                onModeSelected(CommandMode.ALTERNATIVE)
                            }
                            .background(
                                color =
                                    if (selectedMode == CommandMode.ALTERNATIVE)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.primaryContainer.copy(0.3f),

                            )
                            .padding(8.dp)
                ) {
                    // Alternative Mode Option
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedMode == CommandMode.ALTERNATIVE,
                            onClick = {
                                selectedMode = CommandMode.ALTERNATIVE
                                onModeSelected(CommandMode.ALTERNATIVE)
                            },
                        )
                        Text(
                            text = "Alternative Mode",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color =
                                if (selectedMode == CommandMode.ALTERNATIVE)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                   // Spacer(modifier = Modifier.height(8.dp))

                    Column(
                    ) {
                        Text(
                            text =
                                "- Uses device_config game_overlay commands\n- Known to potentially cause conflicts\n- Allows testing alternative rendering backends\n- May not work properly on all ROMs\n- Experimental support for Android 13\n- Persistant across reboots",
                            style = MaterialTheme.typography.bodySmall,
                            color =
                                if (selectedMode == CommandMode.ALTERNATIVE)
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        )
                    }
                }
            }
        }
    }
}


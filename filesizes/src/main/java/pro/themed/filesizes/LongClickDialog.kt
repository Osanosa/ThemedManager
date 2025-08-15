package pro.themed.filesizes

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.jaredrummler.ktsh.Shell

@Composable
fun LongClickDialog(onDismissRequest: () -> Unit, fullPath: String, onDeletion: () -> ItemState?) {
    var showDeletionDialog by remember { mutableStateOf(false) }

    if (showDeletionDialog) {
        DelitionDialog(onDismissRequest, fullPath, onDeletion)
    }

    Dialog(onDismissRequest = onDismissRequest) {
        CookieCard {
            Column(Modifier.padding(8.dp)) {

                // copy move info delete etc
                Text(fullPath)
                Row(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showDeletionDialog = true }
                        .padding(8.dp),
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun DelitionDialog(onDismissRequest: () -> Unit, fullPath: String, onDeletion: () -> ItemState?) {

    var additionalConfirmationPrompt by remember { mutableStateOf(false) }
    if (additionalConfirmationPrompt) {
        FolderConfirmationDialog(onDismissRequest, fullPath, onDeletion)
    }
    Dialog(onDismissRequest = onDismissRequest) {
        CookieCard {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontSize = 16.sp)) {
                        append("Confirm deletion of\n")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(fullPath) }
                        append("\n?")
                    }
                },
                Modifier.padding(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { onDismissRequest() }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        if (fullPath.endsWith("/")) {
                            additionalConfirmationPrompt = true
                        } else {
                            delete(fullPath)
                            onDeletion()
                        }
                        onDismissRequest()
                    }
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun FolderConfirmationDialog(
    onDismissRequest: () -> Unit,
    fullPath: String,
    onDeletion: () -> ItemState?
) {
    Dialog(onDismissRequest) {
        CookieCard {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontSize = 16.sp)) {
                        append("Are you sure you want to delete\n")

                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(fullPath) }

                        append("\n?")
                    }
                },
                Modifier.padding(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { onDismissRequest() }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        delete(fullPath)
                        onDeletion()
                        onDismissRequest()
                    }
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete")
                }
            }
        }
    }
}

fun delete(path: String) {
    Shell.SU.run("rm -rf \"$path\"")
}

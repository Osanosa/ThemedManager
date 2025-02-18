package pro.themed.filesizes

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*

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
    lsShell.run("rm -rf \"$path\"")
}

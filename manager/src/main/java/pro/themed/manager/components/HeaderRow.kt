package pro.themed.manager.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Stable
@Preview
@Composable
fun HeaderRow(
    header: String = "",
    subHeader: String = "",
    button1text: String = "",
    button1onClick: () -> Unit = {},
    button1weight: Float = 1f,
    button2text: String = "",
    button2onClick: () -> Unit = {},
    button2weight: Float = 1f,
    button3text: String = "",
    button3onClick: () -> Unit = {},
    button3weight: Float = 1f,
    button4text: String = "",
    button4onClick: () -> Unit = {},
    button4weight: Float = 1f,
    switchDescription: String = "",
    onCheckedChange: (Boolean) -> Unit = {},
    isChecked: Boolean = false,
    showSwitch: Boolean = false,
    content: @Composable () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    var checkedState by remember { mutableStateOf(isChecked) }

    LaunchedEffect(isChecked) { checkedState = isChecked }
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = pro.themed.manager.ui.theme.contentcol.copy(alpha = 0.05f),
                contentColor = pro.themed.manager.ui.theme.contentcol,
            ),
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = pro.themed.manager.ui.theme.contentcol.copy(alpha = 0.05f),
                    contentColor = pro.themed.manager.ui.theme.contentcol,
                ),
            modifier = Modifier.padding(8.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(8.dp)
                // verticalAlignment = Alignment.CenterVertically,
                // horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (showSwitch && switchDescription.isEmpty()) {
                    Text(
                        text = header,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        if (subHeader.isNotEmpty()) {
                            Text(
                                text = subHeader,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                            )
                        }

                        Switch(
                            checked = checkedState,
                            onCheckedChange = {
                                checkedState = it
                                onCheckedChange(it)
                            },
                            modifier = Modifier,
                        )
                    }
                } else if (showSwitch && switchDescription.isNotEmpty()) {
                    Column(Modifier) {
                        Text(
                            text = header,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(text = subHeader, style = MaterialTheme.typography.bodyMedium)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = switchDescription,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                            )
                            Switch(
                                checked = checkedState,
                                onCheckedChange = {
                                    checkedState = it
                                    onCheckedChange(it)
                                },
                                modifier = Modifier,
                            )
                        }
                    }
                } else {
                    Text(
                        text = header,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(text = subHeader, style = MaterialTheme.typography.bodyMedium)
                }
                content()
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (button1text.isNotEmpty()) {
                        Button(
                            onClick = {
                                scope.launch { withContext(Dispatchers.IO) { button1onClick() } }
                            },
                            modifier = Modifier.weight(button1weight),
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        pro.themed.manager.ui.theme.contentcol.copy(alpha = 0.1f)
                                ),
                        ) {
                            Text(text = button1text, modifier = Modifier.basicMarquee())
                        }
                    }

                    if (button2text.isNotEmpty()) {
                        Button(
                            onClick = {
                                scope.launch { withContext(Dispatchers.IO) { button2onClick() } }
                            },
                            modifier = Modifier.weight(button2weight),
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        pro.themed.manager.ui.theme.contentcol.copy(alpha = 0.1f)
                                ),
                        ) {
                            Text(text = button2text, modifier = Modifier.basicMarquee())
                        }
                    }
                    if (button3text.isNotEmpty()) {
                        Button(
                            onClick = {
                                scope.launch { withContext(Dispatchers.IO) { button3onClick() } }
                            },
                            modifier = Modifier.weight(button3weight),
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        pro.themed.manager.ui.theme.contentcol.copy(alpha = 0.1f)
                                ),
                        ) {
                            Text(text = button3text, modifier = Modifier.basicMarquee())
                        }
                    }
                    if (button4text.isNotEmpty()) {
                        Button(
                            onClick = {
                                scope.launch { withContext(Dispatchers.IO) { button4onClick() } }
                            },
                            modifier = Modifier.weight(button4weight),
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        pro.themed.manager.ui.theme.contentcol.copy(alpha = 0.1f)
                                ),
                        ) {
                            Text(text = button4text, modifier = Modifier.basicMarquee())
                        }
                    }
                }
            }
        }
    }
}

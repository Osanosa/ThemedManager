package pro.themed.filesizes

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Item(
    name: String,
    itemState: ItemState,
    mimetype: String? = null,
    onClick: () -> Unit = {},
    fullPath: String,
    onDeletion: () -> ItemState?
) {
    var clicked by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = clicked) {
        delay(1000)
        clicked = false
    }

    var showLongClickDialog by remember { mutableStateOf(false) }

    if (showLongClickDialog) {
        LongClickDialog({ showLongClickDialog = false }, fullPath, onDeletion)
    }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.background(MaterialTheme.colorScheme.surface)
                    .combinedClickable(
                        onClick = {
                            onClick()
                            clicked = true
                        },
                        onLongClick = { showLongClickDialog = true }
                    )
                    .height(48.dp)
                    .padding(horizontal = 8.dp)
        ) {
            ItemIcon(name, itemState.du, mimetype)
            Text(
                if (name == "/" || name.isEmpty())
                    fullPath.removeSuffix("/").substringAfterLast("/") + "/"
                else name.removeSuffix("/"),
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            )
            SizeText(itemState.size)
        }
        AnimatedVisibility(
            clicked,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
            exit = fadeOut(animationSpec = tween(durationMillis = 20000))
        ) {
            Row(
                Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
            ) {
                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

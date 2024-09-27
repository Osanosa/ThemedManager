package pro.themed.manager.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import pro.themed.manager.ui.theme.*

@Preview
@Composable
fun CookieCard(
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CircularProgressIndicator()
            Text(text = "Loading...")
        }

    },
) {
    Surface(color = cookieForeground, contentColor = contentcol, modifier = Modifier

        .clip(RoundedCornerShape(16.dp))
        .clickable { onClick() }
        .border(width = 8.dp, shape = RoundedCornerShape(16.0.dp), color = cookieBackdrop)) {
        Column(modifier = Modifier.clip(RoundedCornerShape(16.dp))) {
            Column(modifier = Modifier.padding(8.dp)) {

                content()
            }
        }
    }
}


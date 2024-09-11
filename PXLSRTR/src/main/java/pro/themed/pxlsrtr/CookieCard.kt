package pro.themed.pxlsrtr

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import pro.themed.pxlsrtr.ui.theme.*

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

    Card(
        onClick = { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = cookieForeground, contentColor = contentcol),
        modifier = Modifier
            .border(width = 8.dp, shape = RoundedCornerShape(16.0.dp), color = cookieBackdrop)
            .padding(8.dp)

    ) {
        Column {

            content()
        }
    }
}


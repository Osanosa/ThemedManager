package pro.themed.manager.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pro.themed.manager.ui.theme.contentcol
import pro.themed.manager.ui.theme.cookieBackdrop
import pro.themed.manager.ui.theme.cookieForeground

@Preview
@Composable
fun CookieCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CircularProgressIndicator()
            Text(text = "Loading...")
        }
    },
) {

    Card(
        onClick = { onClick() }, shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(
            containerColor = cookieForeground, contentColor = contentcol
        ), modifier = Modifier
            .animateContentSize()
            .border(
                width = 8.dp, shape = RoundedCornerShape(16.0.dp), color = cookieBackdrop
            )
            .padding(8.dp)

    ) {
        Column(Modifier.padding(8.dp)) {

            content()
        }
    }
}


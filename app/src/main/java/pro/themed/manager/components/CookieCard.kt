package pro.themed.manager.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pro.themed.manager.ui.theme.contentcol
import pro.themed.manager.ui.theme.cookieBackdrop
import pro.themed.manager.ui.theme.cookieForeground


@Composable
fun CookieCard(
    modifier: Modifier = Modifier, onClick: () -> Unit = {}, content: @Composable () -> Unit,
) {
    Card(
        onClick = { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cookieBackdrop, contentColor = contentcol
        ),
        modifier = Modifier.animateContentSize()
    ) {
        Card(
            shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(
                containerColor = cookieForeground, contentColor = contentcol
            ), modifier = Modifier
                .animateContentSize()
                .padding(8.dp)
        ) {
            Column {

                content()
            }
        }
    }
}

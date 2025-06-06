package pro.themed.audhdlauncher

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pro.themed.audhdlauncher.ui.theme.contentcol
import pro.themed.audhdlauncher.ui.theme.cookieForeground

@Preview
@Composable
fun CookieCard(
modifier: Modifier = Modifier,
    alpha: Float = 0.5f,
    content: @Composable () -> Unit = {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CircularProgressIndicator()
            Text(text = "Loading...")
        }
    },
) {
    Surface(
        color = cookieForeground.copy(alpha),
        contentColor = contentcol,
        modifier =
            modifier.clip(RoundedCornerShape(16.dp))

                .border(
                    width = 8.dp,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.0.dp),
                    color = cookieForeground.copy(alpha - 0.2f),
                )
                .padding(8.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
    ) {
        Column(
            modifier =
                Modifier.Companion.clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
        ) {
            content()
        }
    }
}

package pro.themed.audhdlauncher

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pro.themed.audhdlauncher.ui.theme.contentcol
import pro.themed.audhdlauncher.ui.theme.cookieForeground

@Preview
@Composable
fun CookieCard(
    modifier: Modifier = Modifier,
    alpha: Float = 0.5f, // Keep this for existing behavior
    content: @Composable () -> Unit = {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CircularProgressIndicator()
            Text(text = "Loading...")
        }
    }
) {
    Surface(
        color = cookieForeground.copy(alpha = alpha), // Explicitly name alpha for clarity
        contentColor = contentcol,
        shape = RoundedCornerShape(16.dp), // Apply shape directly to Surface
        border = BorderStroke( // Use BorderStroke for clarity
            width = 8.dp,
            color = cookieForeground.copy(alpha = (alpha - 0.2f).coerceAtLeast(0f)) // Ensure alpha doesn't go negative
        ),
        modifier = modifier, // Apply the passed-in modifier here
    ) {
        // The content is placed directly inside the Surface, padding can be part of the content's layout if needed,
        // or applied here if it's always required for CookieCard's content.
        // The manager version applies padding to an inner column. Let's follow that.
        Column(modifier = Modifier.padding(8.dp)) {
            content()
        }
    }
}

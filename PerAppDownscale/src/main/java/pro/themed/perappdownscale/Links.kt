package pro.themed.perappdownscale

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource

@Composable
fun LinkButtons(modifier: Modifier = Modifier) {
    Row(horizontalArrangement = Arrangement.SpaceAround, modifier = modifier) {
        val context = LocalContext.current
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.t.me/ThemedSupport"))

        IconButton(
            onClick = { context.startActivity(webIntent) },
            colors =
                IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.telegram_logo),
                contentDescription = "Telegram support group",
            )
        }

        val webIntent1 =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/Osanosa/ThemedProject/"))

        IconButton(onClick = { context.startActivity(webIntent1) },
            colors =
                IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.iconmonstr_github_1),
                contentDescription = null,
            )
        }

        val webIntent2 = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themed.pro/"))

        IconButton(onClick = { context.startActivity(webIntent2) },
            colors =
                IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.baseline_language_24),
                contentDescription = null,
            )
        }
    }
}

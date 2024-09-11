package pro.themed.autorefreshrate

import android.content.*
import android.net.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import pro.themed.manager.autorefreshrate.R

@Composable
fun LinkButtons(modifier: Modifier = Modifier) {
    Row(horizontalArrangement = Arrangement.SpaceAround, modifier = modifier) {
        val context = LocalContext.current
        val webIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.t.me/ThemedSupport"))

        IconButton(onClick = { context.startActivity(webIntent) }) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.telegram_logo),
                contentDescription = "Telegram support group"
            )
        }

        val webIntent1 = Intent(
            Intent.ACTION_VIEW, Uri.parse("https://www.github.com/Osanosa/ThemedProject/")
        )

        IconButton(onClick = { context.startActivity(webIntent1) }) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.iconmonstr_github_1),
                contentDescription = null
            )
        }

        val webIntent2 = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themed.pro/"))

        IconButton(onClick = { context.startActivity(webIntent2) }) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.baseline_language_24),
                contentDescription = null
            )
        }
    }
}

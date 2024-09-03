package pro.themed.manager.utils

import android.content.Intent
import androidx.core.content.ContextCompat
import pro.themed.manager.MainActivity.Companion.appContext

fun shareStackTrace(stackTrace: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, stackTrace)
    }
    ContextCompat.startActivity(appContext, Intent.createChooser(intent, "Share stack trace"), null)
}
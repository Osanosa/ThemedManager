package pro.themed.filesizes

import android.content.*
import androidx.core.content.*
import java.io.*

fun openFile(
    item: Pair<String, ItemState>,
    context: Context,
    mimeType: String?,
) {
    val file = File(item.first)
    val uri = FileProvider.getUriForFile(context, "pro.themed.filesizes.provider", file)
    if (mimeType == "application/vnd.android.package-archive") {
        val intent = Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(intent)

    }
    else {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)

    }
}
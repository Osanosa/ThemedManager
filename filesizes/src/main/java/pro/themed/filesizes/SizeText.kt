package pro.themed.filesizes

import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun SizeText(size: Int) {
    Text(
        when (size) {
            in 0..1024 -> "%.2f KB".format((size.toFloat() / 1024))
            in 1025..1048576 -> "%.2f MB".format((size.toFloat() / 1024))
            in 1048577..1073741824 -> "%.2f GB".format((size.toFloat() / 1048576))
            else -> "%.2f TB".format((size.toFloat() / 1073741824))
        }
    )
}

package pro.themed.filesizes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*

@Composable fun ItemIcon(name: String, du: Boolean, mimetype: String? = null) {
    Box {
        if (!du) {
            CircularProgressIndicator()
        }
        //circle background
        val color = when {
            name.endsWith("/") -> Color.hsl(45f, 1f, .5f)
            name.endsWith(".jpg", true) || name.endsWith(".png", true) || name.endsWith(".jpeg", true) || name.endsWith(
                ".gif",
                true) || name.endsWith(".webp", true) -> Color.hsl(15f, 1f, .5f)
            name.endsWith(".mp4", true) || name.endsWith(".mkv", true) || name.endsWith(".avi",
                true) || name.endsWith(".mov", true) -> Color.hsl(90f, 1f, .5f)
            name.endsWith(".mp3", true) || name.endsWith(".wav", true) || name.endsWith(".ogg",
                true) || name.endsWith(".flac", true) -> Color.hsl(270f, 1f, .5f)
            name.endsWith(".pdf", true) -> Color.hsl(0f, 1f, .5f)
            name.endsWith(".zip", true) || name.endsWith(".rar", true) || name.endsWith(".7z",
                true) || name.endsWith(".tar", true) || name.endsWith(".gz", true) || name.endsWith(".xz",
                true) -> Color.hsl(30f, 1f, .5f)
            name.endsWith(".apk", true) -> Color.hsl(75f, 1f, .5f)
            else -> Color.hsl(0f, 0f, .5f)
        }
        Surface(shape = CircleShape, color = color.copy(alpha = 0.1f), contentColor = color) {

            Icon(modifier = androidx.compose.ui.Modifier.padding(8.dp), imageVector = when {
                name.endsWith("/") -> ImageVector.vectorResource(R.drawable.folder_24px)
                name.endsWith(".jpg", true) || name.endsWith(".png", true) || name.endsWith(".jpeg",
                    true) || name.endsWith(".gif", true) || name.endsWith(".webp",
                    true) -> ImageVector.vectorResource(R.drawable.image_24px)
                name.endsWith(".mp4", true) || name.endsWith(".mkv", true) || name.endsWith(".avi",
                    true) || name.endsWith(".mov", true) -> ImageVector.vectorResource(R.drawable.movie_24px)
                name.endsWith(".mp3", true) || name.endsWith(".wav", true) || name.endsWith(".ogg",
                    true) || name.endsWith(".flac", true) -> ImageVector.vectorResource(R.drawable.music_note_24px)
                name.endsWith(".pdf", true) -> ImageVector.vectorResource(R.drawable.picture_as_pdf_24px)
                name.endsWith(".zip", true) || name.endsWith(".rar", true) || name.endsWith(".7z",
                    true) || name.endsWith(".tar", true) || name.endsWith(".gz", true) || name.endsWith(".xz",
                    true) -> ImageVector.vectorResource(R.drawable.folder_zip_24px)
                name.endsWith(".apk", true) -> ImageVector.vectorResource(R.drawable.apk_document_24px)
                else -> ImageVector.vectorResource(R.drawable.draft_24px)
            }, contentDescription = null)
        }
    }
}
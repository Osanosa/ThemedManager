package pro.themed.manager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val backgroundLight = Color(0xfff9f9f9)
val backgroundDark = Color(0xFF0F1012)

val borderLight = Color(0xFFf2f2f2)
val borderDark = Color(0xFF15171A)

val bordercol: Color
    @Composable
    get() = if (isSystemInDarkTheme()) borderDark else borderLight
val cardcol: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) backgroundLight else backgroundDark
val textcol: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) backgroundDark else backgroundLight

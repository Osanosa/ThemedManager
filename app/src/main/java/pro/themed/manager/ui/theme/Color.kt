package pro.themed.manager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val backgroundLight = Color(0xfff9f9f9)
val backgroundDark = Color(0xFF0F1012)

val borderLight = Color(0xFFD9D9D9)
val borderDark = Color(0xFF2C2E33)


val bordercol: Color
    @Composable
    get() = if (isSystemInDarkTheme()) borderLight else borderDark
val cardcol: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) backgroundLight else backgroundDark
val textcol: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) backgroundDark else backgroundLight

package pro.themed.pxlsrtr.ui.theme

import androidx.compose.foundation.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

private val backgroundLight = Color(0xfff9f9f9)
private val backgroundDark = Color(0xFF0F1012)

private val borderLight = Color(0xFFf2f2f2)
private val borderDark = Color(0xFF15171A)

private val cookieBackdropLight = Color(0xFFF0F0F0)
private val cookieBackdropDark = Color(0xFF1c1c1c)

val cookieForegroundLight = Color(0xFFEBEBEB)
val cookieForegroundDark = Color(0xFF282828)

val cookieBackdrop: Color
    @Composable get() = if (!isSystemInDarkTheme()) cookieBackdropLight else cookieBackdropDark

val cookieForeground: Color
    @Composable get() = if (!isSystemInDarkTheme()) cookieForegroundLight else cookieForegroundDark

val bordercol: Color
    @Composable get() = if (isSystemInDarkTheme()) borderDark else borderLight

val background: Color
    @Composable get() = if (!isSystemInDarkTheme()) backgroundLight else backgroundDark

val contentcol: Color
    @Composable get() = if (!isSystemInDarkTheme()) backgroundDark else backgroundLight

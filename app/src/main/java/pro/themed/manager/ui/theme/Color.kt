package pro.themed.manager.ui.theme

import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val Purple = Color(0xFFAA00FF)

val backgroundLight = Color(0xFFFFFFFF)
val backgroundDark = Color(0xFF0F1012)

val borderLight = Color(0xFFD9D9D9)
val borderDark = Color(0xFF2C2E33)


@get:Composable
val Colors.bordercol: Color
    get() = if (isLight) borderLight else borderDark
val Colors.cardcol: Color
    get() = if (isLight) backgroundLight else backgroundDark
val Colors.textcol: Color
    get() = if (isLight) backgroundDark else backgroundLight

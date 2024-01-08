package pro.themed.manager.ui.theme

import android.R
import android.content.res.TypedArray
import android.util.TypedValue
import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import pro.themed.manager.MainActivity

import android.graphics.Color as AndroidColor

private fun fetchAccentColor(): Float {
    val typedValue = TypedValue()
    val a: TypedArray =
        MainActivity.appContext.obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorAccent))
    val color = a.getColor(0, 0)
    a.recycle()

    // Convert the color to its HSV representation
    val hsv = FloatArray(3)
    AndroidColor.colorToHSV(color, hsv)

    // Return the hue
    return hsv[0]
}


val Accent = Color.hsl(fetchAccentColor(), 1f, 0.5f)

val backgroundLight = Color.hsl(fetchAccentColor(), 1f, 0.9f)
val backgroundDark = Color.hsl(fetchAccentColor(), 1f, 0.05f  )


val borderLight = Color(0xFFD9D9D9)
val borderDark = Color(0xFF2C2E33)


@get:Composable
val Colors.bordercol: Color
    get() = if (isLight) borderLight else borderDark
val Colors.cardcol: Color
    get() = if (isLight) backgroundLight else backgroundDark
val Colors.textcol: Color
    get() = if (isLight) backgroundDark else backgroundLight

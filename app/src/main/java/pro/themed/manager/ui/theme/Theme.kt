package pro.themed.manager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    background = backgroundDark,
    surface = backgroundDark
)

val LightColorPalette = lightColors(
    primary = Color.Transparent,
    primaryVariant = Color.Transparent,
    background = backgroundLight,
surface = backgroundLight

    /* Other default colors to override

    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun ThemedManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )

    val systemUiController = rememberSystemUiController()
    if(darkTheme){
        systemUiController.setSystemBarsColor(
            color = backgroundDark
        )
    }else{
        systemUiController.setSystemBarsColor(
            color = backgroundLight
        )
}

}
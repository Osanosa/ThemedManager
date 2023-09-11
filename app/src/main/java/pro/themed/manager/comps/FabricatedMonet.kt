package pro.themed.manager.comps

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.hsl
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getColor
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.R
import pro.themed.manager.buildOverlay
import pro.themed.manager.getOverlayList
import pro.themed.manager.overlayEnable
import pro.themed.manager.ui.theme.cardcol
import pro.themed.manager.utils.GlobalVariables
import pro.themed.manager.utils.showInterstitial
import kotlin.math.roundToInt

annotation class Composable

@RequiresApi(Build.VERSION_CODES.S)
@Preview
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun FabricatedMonet(
) {
    val context = LocalContext.current

    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = sharedPreferences.edit()

    val SN1_10: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral1_10)
    } else {
        Color.Black.toArgb()
    }
    val SN1_50: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral1_50)
    } else {
        Color.Black.toArgb()
    }
    val SN1_100: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral1_100)
    } else {
        Color.Black.toArgb()
    }
    val SN1_200: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral1_200)
    } else {
        Color.Black.toArgb()
    }
    val SN1_300: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral1_300)
    } else {
        Color.Black.toArgb()
    }
    val SN1_400: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral1_400)
    } else {
        Color.Black.toArgb()
    }
    val SN1_500: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral1_500)
    } else {
        Color.Black.toArgb()
    }
    val SN1_600: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral1_600)
    } else {
        Color.Black.toArgb()
    }
    val SN1_700: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral1_700)
    } else {
        Color.Black.toArgb()
    }
    val SN1_800: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral1_800)
    } else {
        Color.Black.toArgb()
    }
    val SN1_900: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral1_900)
    } else {
        Color.Black.toArgb()
    }

    val SN2_10: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral2_10)
    } else {
        Color.Black.toArgb()
    }
    val SN2_50: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral2_50)
    } else {
        Color.Black.toArgb()
    }
    val SN2_100: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral2_100)
    } else {
        Color.Black.toArgb()
    }
    val SN2_200: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral2_200)
    } else {
        Color.Black.toArgb()
    }
    val SN2_300: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral2_300)
    } else {
        Color.Black.toArgb()
    }
    val SN2_400: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral2_400)
    } else {
        Color.Black.toArgb()
    }
    val SN2_500: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral2_500)
    } else {
        Color.Black.toArgb()
    }
    val SN2_600: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral2_600)
    } else {
        Color.Black.toArgb()
    }
    val SN2_700: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral2_700)
    } else {
        Color.Black.toArgb()
    }
    val SN2_800: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral2_800)
    } else {
        Color.Black.toArgb()
    }
    val SN2_900: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_neutral2_900)
    } else {
        Color.Black.toArgb()
    }

    val SA2_10: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent2_10)
    } else {
        Color.Black.toArgb()
    }
    val SA2_50: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent2_50)
    } else {
        Color.Black.toArgb()
    }
    val SA2_100: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent2_100)
    } else {
        Color.Black.toArgb()
    }
    val SA2_200: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent2_200)
    } else {
        Color.Black.toArgb()
    }
    val SA2_300: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent2_300)
    } else {
        Color.Black.toArgb()
    }
    val SA2_400: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent2_400)
    } else {
        Color.Black.toArgb()
    }
    val SA2_500: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent2_500)
    } else {
        Color.Black.toArgb()
    }
    val SA2_600: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent2_600)
    } else {
        Color.Black.toArgb()
    }
    val SA2_700: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent2_700)
    } else {
        Color.Black.toArgb()
    }
    val SA2_800: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent2_800)
    } else {
        Color.Black.toArgb()
    }
    val SA2_900: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent2_900)
    } else {
        Color.Black.toArgb()
    }

    val SA1_10: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent1_10)
    } else {
        Color.Black.toArgb()
    }
    val SA1_50: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent1_50)
    } else {
        Color.Black.toArgb()
    }
    val SA1_100: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent1_100)
    } else {
        Color.Black.toArgb()
    }
    val SA1_200: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent1_200)
    } else {
        Color.Black.toArgb()
    }
    val SA1_300: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent1_300)
    } else {
        Color.Black.toArgb()
    }
    val SA1_400: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent1_400)
    } else {
        Color.Black.toArgb()
    }
    val SA1_500: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent1_500)
    } else {
        Color.Black.toArgb()
    }
    val SA1_600: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent1_600)
    } else {
        Color.Black.toArgb()
    }
    val SA1_700: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent1_700)
    } else {
        Color.Black.toArgb()
    }
    val SA1_800: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent1_800)
    } else {
        Color.Black.toArgb()
    }
    val SA1_900: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent1_900)
    } else {
        Color.Black.toArgb()
    }

    val SA3_10: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent3_10)
    } else {
        Color.Black.toArgb()
    }
    val SA3_50: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent3_50)
    } else {
        Color.Black.toArgb()
    }
    val SA3_100: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent3_100)
    } else {
        Color.Black.toArgb()
    }
    val SA3_200: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent3_200)
    } else {
        Color.Black.toArgb()
    }
    val SA3_300: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent3_300)
    } else {
        Color.Black.toArgb()
    }
    val SA3_400: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent3_400)
    } else {
        Color.Black.toArgb()
    }
    val SA3_500: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent3_500)
    } else {
        Color.Black.toArgb()
    }
    val SA3_600: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent3_600)
    } else {
        Color.Black.toArgb()
    }
    val SA3_700: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent3_700)
    } else {
        Color.Black.toArgb()
    }
    val SA3_800: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent3_800)
    } else {
        Color.Black.toArgb()
    }
    val SA3_900: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getColor(context, android.R.color.system_accent3_900)
    } else {
        Color.Black.toArgb()
    }


    var hue by rememberSaveable { mutableFloatStateOf(0f) }
    var saturation by rememberSaveable { mutableFloatStateOf(100f) }
    var lightness by rememberSaveable { mutableFloatStateOf(0f) }

    if (hue == 360f) {
        hue = 0f
    }
    if (saturation == 0f) {
        hue = 0f
    }


    val C_10 = hsl(hue, saturation / 100, 0.99f + lightness / 100 / 10)
    val C_50 = hsl(hue, saturation / 100, 0.95f + lightness / 100 / 2)
    val C_100 = hsl(hue, saturation / 100, 0.90f + lightness / 100)
    val C_200 = hsl(hue, saturation / 100, 0.80f + lightness / 100)
    val C_300 = hsl(hue, saturation / 100, 0.70f + lightness / 100)
    val C_400 = hsl(hue, saturation / 100, 0.60f + lightness / 100)
    val C_500 = hsl(hue, saturation / 100, 0.496f + lightness / 100)
    val C_600 = hsl(hue, saturation / 100, 0.40f + lightness / 100)
    val C_700 = hsl(hue, saturation / 100, 0.30f + lightness / 100)
    val C_800 = hsl(hue, saturation / 100, 0.20f + lightness / 100)
    val C_900 = hsl(hue, saturation / 100, 0.10f + lightness / 100)

    val isDark = if (sharedPreferences.getBoolean("acccents_dark", false)) "-night" else ""

    Card(
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.cardcol),
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        elevation = (0.dp),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.cardcol
    ) {
        Column(modifier = Modifier) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .padding(start = 8.dp),
                    text = "FakeMonet",
                    fontSize = 24.sp
                )

                IconButton(modifier = Modifier, onClick = {
                    Shell.SU.run("cmd overlay disable themed.fakemonet.generic ; pm uninstall themed.fakemonet.generic")
                }) {
                    Image(
                        painter = painterResource(R.drawable.reset),
                        contentDescription = null,
                    )
                }
            }
            val tilesize = ((LocalConfiguration.current.smallestScreenWidthDp - 16) / 11).dp


            listOf(
                "10", "50", "100", "200", "300", "400", "500", "600", "700", "800", "900"
            )

            Surface {


                Column {


                    @Composable
                    fun M3Tile(color: Int, colorName: String, themedColor: Color) {
                        Surface(
                            modifier = Modifier
                                .height(tilesize)
                                .aspectRatio(1f)
                                .combinedClickable(onClick = {
                                    val fullName = colorName
                                        .replace("SN", "system_neutral")
                                        .replace("SA", "system_accent")


                                    val hex = "%08x".format(themedColor.toArgb())

                                    Shell.SU.run("cd ${GlobalVariables.modulePath}/onDemandCompiler/fakeMonet")

                                    Shell.SU.run(
                                        """sed -i '/$fullName">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#$hex</g' res/values$isDark/colors.xml"""
                                    )
                                }, onLongClick = {}), color = Color(color)
                        ) {
                            Text(
                                text = colorName.substringAfter("_"),
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                            )
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Text(text = "system_neutral1")

                        Row(Modifier.fillMaxWidth()) {
                            M3Tile(color = SN1_10, colorName = "SN1_10", themedColor = C_10)
                            M3Tile(color = SN1_50, colorName = "SN1_50", themedColor = C_50)
                            M3Tile(color = SN1_100, colorName = "SN1_100", themedColor = C_100)
                            M3Tile(color = SN1_200, colorName = "SN1_200", themedColor = C_200)
                            M3Tile(color = SN1_300, colorName = "SN1_300", themedColor = C_300)
                            M3Tile(color = SN1_400, colorName = "SN1_400", themedColor = C_400)
                            M3Tile(color = SN1_500, colorName = "SN1_500", themedColor = C_500)
                            M3Tile(color = SN1_600, colorName = "SN1_600", themedColor = C_600)
                            M3Tile(color = SN1_700, colorName = "SN1_700", themedColor = C_700)
                            M3Tile(color = SN1_800, colorName = "SN1_800", themedColor = C_800)
                            M3Tile(color = SN1_900, colorName = "SN1_900", themedColor = C_900)

                        }
                        Text(text = "system_neutral2")

                        Row(Modifier.fillMaxWidth()) {
                            M3Tile(color = SN2_10, colorName = "SN2_10", themedColor = C_10)
                            M3Tile(color = SN2_50, colorName = "SN2_50", themedColor = C_50)
                            M3Tile(color = SN2_100, colorName = "SN2_100", themedColor = C_100)
                            M3Tile(color = SN2_200, colorName = "SN2_200", themedColor = C_200)
                            M3Tile(color = SN2_300, colorName = "SN2_300", themedColor = C_300)
                            M3Tile(color = SN2_400, colorName = "SN2_400", themedColor = C_400)
                            M3Tile(color = SN2_500, colorName = "SN2_500", themedColor = C_500)
                            M3Tile(color = SN2_600, colorName = "SN2_600", themedColor = C_600)
                            M3Tile(color = SN2_700, colorName = "SN2_700", themedColor = C_700)
                            M3Tile(color = SN2_800, colorName = "SN2_800", themedColor = C_800)
                            M3Tile(color = SN2_900, colorName = "SN2_900", themedColor = C_900)

                        }
                        Text(text = "system_accent1")

                        Row(Modifier.fillMaxWidth()) {
                            M3Tile(color = SA1_10, colorName = "SA1_10", themedColor = C_10)
                            M3Tile(color = SA1_50, colorName = "SA1_50", themedColor = C_50)
                            M3Tile(color = SA1_100, colorName = "SA1_100", themedColor = C_100)
                            M3Tile(color = SA1_200, colorName = "SA1_200", themedColor = C_200)
                            M3Tile(color = SA1_300, colorName = "SA1_300", themedColor = C_300)
                            M3Tile(color = SA1_400, colorName = "SA1_400", themedColor = C_400)
                            M3Tile(color = SA1_500, colorName = "SA1_500", themedColor = C_500)
                            M3Tile(color = SA1_600, colorName = "SA1_600", themedColor = C_600)
                            M3Tile(color = SA1_700, colorName = "SA1_700", themedColor = C_700)
                            M3Tile(color = SA1_800, colorName = "SA1_800", themedColor = C_800)
                            M3Tile(color = SA1_900, colorName = "SA1_900", themedColor = C_900)

                        }
                        Text(text = "system_accent2")

                        Row(Modifier.fillMaxWidth()) {
                            M3Tile(color = SA2_10, colorName = "SA2_10", themedColor = C_10)
                            M3Tile(color = SA2_50, colorName = "SA2_50", themedColor = C_50)
                            M3Tile(color = SA2_100, colorName = "SA2_100", themedColor = C_100)
                            M3Tile(color = SA2_200, colorName = "SA2_200", themedColor = C_200)
                            M3Tile(color = SA2_300, colorName = "SA2_300", themedColor = C_300)
                            M3Tile(color = SA2_400, colorName = "SA2_400", themedColor = C_400)
                            M3Tile(color = SA2_500, colorName = "SA2_500", themedColor = C_500)
                            M3Tile(color = SA2_600, colorName = "SA2_600", themedColor = C_600)
                            M3Tile(color = SA2_700, colorName = "SA2_700", themedColor = C_700)
                            M3Tile(color = SA2_800, colorName = "SA2_800", themedColor = C_800)
                            M3Tile(color = SA2_900, colorName = "SA2_900", themedColor = C_900)

                        }
                        Text(text = "system_accent3")

                        Row(Modifier.fillMaxWidth()) {
                            M3Tile(color = SA3_10, colorName = "SA3_10", themedColor = C_10)
                            M3Tile(color = SA3_50, colorName = "SA3_50", themedColor = C_50)
                            M3Tile(color = SA3_100, colorName = "SA3_100", themedColor = C_100)
                            M3Tile(color = SA3_200, colorName = "SA3_200", themedColor = C_200)
                            M3Tile(color = SA3_300, colorName = "SA3_300", themedColor = C_300)
                            M3Tile(color = SA3_400, colorName = "SA3_400", themedColor = C_400)
                            M3Tile(color = SA3_500, colorName = "SA3_500", themedColor = C_500)
                            M3Tile(color = SA3_600, colorName = "SA3_600", themedColor = C_600)
                            M3Tile(color = SA3_700, colorName = "SA3_700", themedColor = C_700)
                            M3Tile(color = SA3_800, colorName = "SA3_800", themedColor = C_800)
                            M3Tile(color = SA3_900, colorName = "SA3_900", themedColor = C_900)

                        }

                    }
                    Text(text = "Current color palette", fontWeight = FontWeight.Bold)
                    Row(Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .height(tilesize)
                                .aspectRatio(1f)
                                .combinedClickable(onClick = {}), color = C_10
                        ) {
                            Text(
                                text = "10",
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.align(CenterVertically)
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .height(tilesize)
                                .aspectRatio(1f)
                                .combinedClickable(onClick = {}), color = C_50
                        ) {
                            Text(
                                text = "50",
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.align(CenterVertically)
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .height(tilesize)
                                .aspectRatio(1f)
                                .combinedClickable(onClick = {}), color = C_100
                        ) {
                            Text(
                                text = "100",
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.align(CenterVertically)
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .height(tilesize)
                                .aspectRatio(1f)
                                .combinedClickable(onClick = {}), color = C_200
                        ) {
                            Text(
                                text = "200",
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.align(CenterVertically)
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .height(tilesize)
                                .aspectRatio(1f)
                                .combinedClickable(onClick = {}), color = C_300
                        ) {
                            Text(
                                text = "300",
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.align(CenterVertically)
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .height(tilesize)
                                .aspectRatio(1f)
                                .combinedClickable(onClick = {}), color = C_400
                        ) {
                            Text(
                                text = "400",
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.align(CenterVertically)
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .height(tilesize)
                                .aspectRatio(1f)
                                .combinedClickable(onClick = {}), color = C_500
                        ) {
                            Text(
                                text = "500",
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.align(CenterVertically)
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .height(tilesize)
                                .aspectRatio(1f)
                                .combinedClickable(onClick = {}), color = C_600
                        ) {
                            Text(
                                text = "600",
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.align(CenterVertically)
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .height(tilesize)
                                .aspectRatio(1f)
                                .combinedClickable(onClick = {}), color = C_700
                        ) {
                            Text(
                                text = "700",
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.align(CenterVertically)
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .height(tilesize)
                                .aspectRatio(1f)
                                .combinedClickable(onClick = {}), color = C_800
                        ) {
                            Text(
                                text = "800",
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.align(CenterVertically)
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .height(tilesize)
                                .aspectRatio(1f)
                                .combinedClickable(onClick = {}), color = C_900
                        ) {
                            Text(
                                text = "900",
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.align(CenterVertically)
                            )
                        }

                    }


                    Text(text = "hue is ${hue.toInt()}°")
                    androidx.compose.material3.Slider(modifier = Modifier
                        .height(16.dp)
                        .padding(0.dp),
                        value = hue,
                        onValueChange = {
                            hue = it.roundToInt().toFloat()
                        },
                        valueRange = 0f..360f,
                        onValueChangeFinished = {},
                        steps = 0,
                        thumb = {
                            Image(
                                painter = painterResource(R.drawable.fiber_manual_record_48px),
                                contentDescription = null,
                            )

                        })
                    Text(text = "saturation is ${saturation.toInt()}%")
                    androidx.compose.material3.Slider(modifier = Modifier
                        .height(16.dp)
                        .padding(0.dp),
                        value = saturation,
                        onValueChange = {
                            saturation = it.roundToInt().toFloat()
                        },
                        valueRange = 0f..100f,
                        onValueChangeFinished = {},
                        steps = 0,
                        thumb = {
                            Image(
                                painter = painterResource(R.drawable.fiber_manual_record_48px),
                                contentDescription = null,
                            )
                        })
                    Text(text = "Lightness is $lightness")
                    androidx.compose.material3.Slider(modifier = Modifier
                        .height(16.dp)
                        .padding(0.dp),
                        value = lightness,
                        onValueChange = {
                            lightness = it.roundToInt().toFloat()
                        },
                        valueRange = -10f..10f,
                        onValueChangeFinished = {},
                        steps = 19,
                        thumb = {
                            Image(
                                painter = painterResource(R.drawable.fiber_manual_record_48px),
                                contentDescription = null,
                            )
                        })


                    Text(text = "Apply to")

                    Row(modifier = Modifier.padding(horizontal = 1.dp)) {
                        Button(modifier = Modifier.weight(1f), shape = CircleShape, onClick = {
                            Shell.SU.run("cd ${GlobalVariables.modulePath}/onDemandCompiler/fakeMonet")
                            Shell.SU.run(
                                """sed -i '/system_neutral1_10">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_10.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral1_50">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_50.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral1_100">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_100.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral1_200">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_200.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral1_300">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_300.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral1_400">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_400.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral1_500">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_500.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral1_600">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_600.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral1_700">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_700.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral1_800">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_800.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral1_900">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_900.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                        }) {
                            Text(text = "N1")
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        Button(modifier = Modifier.weight(1f), shape = CircleShape, onClick = {
                            Shell.SU.run("cd ${GlobalVariables.modulePath}/onDemandCompiler/fakeMonet")
                            Shell.SU.run(
                                """sed -i '/system_neutral2_10">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_10.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral2_50">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_50.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral2_100">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_100.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral2_200">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_200.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral2_300">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_300.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral2_400">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_400.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral2_500">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_500.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral2_600">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_600.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral2_700">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_700.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral2_800">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_800.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_neutral2_900">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_900.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                        }) {
                            Text(text = "N2")
                        }
                        Spacer(modifier = Modifier.width(8.dp))


                        Button(modifier = Modifier.weight(1f), shape = CircleShape, onClick = {
                            Shell.SU.run("cd ${GlobalVariables.modulePath}/onDemandCompiler/fakeMonet")
                            Shell.SU.run(
                                """sed -i '/system_accent1_10">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_10.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent1_50">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_50.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent1_100">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_100.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent1_200">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_200.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent1_300">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_300.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent1_400">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_400.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent1_500">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_500.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent1_600">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_600.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent1_700">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_700.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent1_800">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_800.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent1_900">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_900.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                        }) {
                            Text(text = "A1")
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        Button(modifier = Modifier.weight(1f), shape = CircleShape, onClick = {
                            Shell.SU.run("cd ${GlobalVariables.modulePath}/onDemandCompiler/fakeMonet")
                            Shell.SU.run(
                                """sed -i '/system_accent2_10">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_10.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent2_50">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_50.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent2_100">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_100.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent2_200">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_200.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent2_300">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_300.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent2_400">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_400.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent2_500">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_500.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent2_600">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_600.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent2_700">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_700.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent2_800">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_800.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent2_900">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_900.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                        }) {
                            Text(text = "A2")
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        Button(modifier = Modifier.weight(1f), shape = CircleShape, onClick = {
                            Shell.SU.run("cd ${GlobalVariables.modulePath}/onDemandCompiler/fakeMonet")
                            Shell.SU.run(
                                """sed -i '/system_accent3_10">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_10.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent3_50">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_50.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent3_100">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_100.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent3_200">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_200.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent3_300">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_300.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent3_400">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_400.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent3_500">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_500.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent3_600">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_600.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent3_700">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_700.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent3_800">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_800.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                            Shell.SU.run(
                                """sed -i '/system_accent3_900">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                    "%08x".format(
                                        C_900.toArgb()
                                    )
                                }</g' res/values$isDark/colors.xml"""
                            )
                        }) {
                            Text(text = "A3")
                        }

                    }
                    Text(text = stringResource(R.string.FakeMonetUpdate))
                    HeaderRow(
                        header = "Disable Monet",
                        subHeader = "Ye you need to enable this first, duh",
                        isChecked = getOverlayList().enabledOverlays.any { it.contains("flagmonet") },
                        onCheckedChange = {
                            if (it) {
                                Shell.SH.run("su -c cmd overlay disable com.android.systemui:accent")
                                Shell.SH.run("su -c cmd overlay disable com.android.systemui:neutral")
                                overlayEnable("misc.flagmonet")

                            } else {
                                Shell.SH.run("su -c cmd overlay enable com.android.systemui:accent")
                                Shell.SH.run("su -c cmd overlay enable com.android.systemui:neutral")
                                Shell.SH.run("su -c cmd overlay disable themed.misc.flagmonet")

                            }
                        },
                        showSwitch = true
                    )

                    HeaderRow(
                        header = "Override colors for dark theme",
                        showSwitch = true,
                        isChecked = sharedPreferences.getBoolean("acccents_dark", false),
                        onCheckedChange = {
                            if (it) {
                                editor.putBoolean("acccents_dark", true)
                                editor.apply()
                            } else {
                                editor.putBoolean("acccents_dark", false)
                                editor.apply()

                            }
                        },
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(), onClick = {
                            Shell.SU.run("cd ${GlobalVariables.modulePath}/onDemandCompiler/fakeMonet")
                            buildOverlay()
                            Shell.SU.run("""cmd overlay enable themed.fakemonet.generic""")
                            showInterstitial(context) {}


                        }, colors = ButtonDefaults.buttonColors(
                            backgroundColor = C_500, contentColor = if (lightness > 50f) {
                                Color.Black
                            } else {
                                Color.White
                            }
                        ), shape = CircleShape
                    ) {
                        Row(verticalAlignment = CenterVertically) {

                            Text(text = "Build and update")
                            Icon(
                                modifier = Modifier.height(24.dp),
                                painter = painterResource(id = R.drawable.arrow_right_alt_48px),
                                contentDescription = ""
                            )

                        }
                    }

                }
            }
        }
    }
}
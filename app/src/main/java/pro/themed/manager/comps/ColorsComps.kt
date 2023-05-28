@file:OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalFoundationApi::class
) @file:Suppress("LocalVariableName")

package pro.themed.manager.comps

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.hsl
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.Layout
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
import androidx.core.graphics.toColorInt
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.CardFace
import pro.themed.manager.FlipCard
import pro.themed.manager.R
import pro.themed.manager.RotationAxis
import pro.themed.manager.bordercol
import pro.themed.manager.cardcol
import pro.themed.manager.getOverlay
import pro.themed.manager.getOverlayList
import pro.themed.manager.overlayEnable
import kotlin.math.roundToInt


@ExperimentalMaterialApi
@Composable
fun ColorTile(name: String, color: String, modifier: Modifier = Modifier) {


    Surface(modifier = modifier,
        color = Color(color.toColorInt()),
        onClick = { overlayEnable("$name.${color.removePrefix("#")}") }) {

        if (getOverlayList().enabledOverlays.any { it.contains(name + "." + color.removePrefix("#")) }) {

            Icon(
                painter = painterResource(id = R.drawable.check_small_48px),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(0.9f)
            )
        }
    }
}


@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun FakeMonet(
) {
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
                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'themed.fakemonet' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
                }) {
                    Image(
                        painter = painterResource(R.drawable.reset),
                        contentDescription = null,
                    )
                }
            }
            val tilesize = ((LocalConfiguration.current.smallestScreenWidthDp - 16) / 11).dp

            var hue by rememberSaveable { mutableFloatStateOf(0f) }
            var saturation by rememberSaveable { mutableFloatStateOf(100f) }

            if (hue == 360f) {
                hue = 0f
            }
            if (saturation == 0f) {
                hue = 0f
            }

            val brightnessValues =
                listOf(0.99f, 0.95f, 0.90f, 0.80f, 0.70f, 0.60f, 0.496f, 0.40f, 0.30f, 0.20f, 0.10f)
            val labels =
                listOf("10", "50", "100", "200", "300", "400", "500", "600", "700", "800", "900")

            Surface {
                val context = LocalContext.current



                Column {

                    Text(text = "Current color palette")

                    Row(Modifier.fillMaxWidth()) {
                        brightnessValues.zip(labels).forEach { (brightness, label) ->
                            Surface(
                                modifier = Modifier
                                    .height(tilesize)
                                    //  .weight(1f)
                                    .aspectRatio(1f)
                                    .combinedClickable(onClick = {
                                        val color = hsl(hue, saturation / 100, brightness)
                                        val hex =
                                            String.format("%06X", (0xFFFFFF and color.toArgb()))
                                        val clipboard =
                                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Color", hex)
                                        clipboard.setPrimaryClip(clip)
                                        Toast
                                            .makeText(
                                                context,
                                                "c$label ($hex) copied to clipboard",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }), color = hsl(hue, saturation / 100, brightness)
                            ) {
                                Text(
                                    text = label,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.align(CenterVertically)
                                )
                            }
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
                        steps = 8,
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
                        steps = 4,
                        thumb = {
                            Image(
                                painter = painterResource(R.drawable.fiber_manual_record_48px),
                                contentDescription = null,
                            )
                        })

                    // Text(text = "fakemonet.n1h${hue.toInt()}s${saturation.toInt()}")

                    /*HeaderRowWithSwitch(header = "Override dark theme", onCheckedChange = {
                        if (it) {
                            isDark = ".dark"
                        } else {
                            isDark = ""
                        }
                    })*/
                    // Text(text = "Apply to")

                    Row {
                        Button(modifier = Modifier.weight(1f), onClick = {
                            overlayEnable("fakemonet.n1h${hue.toInt()}s${saturation.toInt()}")
                        }) {
                            Text(text = "N1")

                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        if (!getOverlayList().unsupportedOverlays.contains("n2")) {
                            Button(modifier = Modifier.weight(1f), onClick = {
                                overlayEnable("fakemonet.n2h${hue.toInt()}s${saturation.toInt()}")
                            }) {
                                Text(text = "N2")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Button(modifier = Modifier.weight(1f), onClick = {
                            overlayEnable("fakemonet.a1h${hue.toInt()}s${saturation.toInt()}")
                        }) {
                            Text(text = "A1")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        if (!getOverlayList().unsupportedOverlays.contains("a2") || !getOverlayList().unsupportedOverlays.contains(
                                "a3"
                            )
                        ) {
                            Button(modifier = Modifier.weight(1f), onClick = {
                                overlayEnable("fakemonet.a2h${hue.toInt()}s${saturation.toInt()}")
                            }) {
                                Text(text = "A2")
                            }
                            Spacer(modifier = Modifier.width(8.dp))

                            Button(modifier = Modifier.weight(1f), onClick = {
                                overlayEnable("fakemonet.a3h${hue.toInt()}s${saturation.toInt()}")
                            }) {
                                Text(text = "A3")
                            }
                        }
                    }

                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun FabricatedMonet(
) {
    val context = LocalContext.current

    val SN1_10 = getColor(context, android.R.color.system_neutral1_10)
    val SN1_50 = getColor(context, android.R.color.system_neutral1_50)
    val SN1_100 = getColor(context, android.R.color.system_neutral1_100)
    val SN1_200 = getColor(context, android.R.color.system_neutral1_200)
    val SN1_300 = getColor(context, android.R.color.system_neutral1_300)
    val SN1_400 = getColor(context, android.R.color.system_neutral1_400)
    val SN1_500 = getColor(context, android.R.color.system_neutral1_500)
    val SN1_600 = getColor(context, android.R.color.system_neutral1_600)
    val SN1_700 = getColor(context, android.R.color.system_neutral1_700)
    val SN1_800 = getColor(context, android.R.color.system_neutral1_800)
    val SN1_900 = getColor(context, android.R.color.system_neutral1_900)

    val SN2_10 = getColor(context, android.R.color.system_neutral2_10)
    val SN2_50 = getColor(context, android.R.color.system_neutral2_50)
    val SN2_100 = getColor(context, android.R.color.system_neutral2_100)
    val SN2_200 = getColor(context, android.R.color.system_neutral2_200)
    val SN2_300 = getColor(context, android.R.color.system_neutral2_300)
    val SN2_400 = getColor(context, android.R.color.system_neutral2_400)
    val SN2_500 = getColor(context, android.R.color.system_neutral2_500)
    val SN2_600 = getColor(context, android.R.color.system_neutral2_600)
    val SN2_700 = getColor(context, android.R.color.system_neutral2_700)
    val SN2_800 = getColor(context, android.R.color.system_neutral2_800)
    val SN2_900 = getColor(context, android.R.color.system_neutral2_900)

    val SA2_10 = getColor(context, android.R.color.system_accent2_10)
    val SA2_50 = getColor(context, android.R.color.system_accent2_50)
    val SA2_100 = getColor(context, android.R.color.system_accent2_100)
    val SA2_200 = getColor(context, android.R.color.system_accent2_200)
    val SA2_300 = getColor(context, android.R.color.system_accent2_300)
    val SA2_400 = getColor(context, android.R.color.system_accent2_400)
    val SA2_500 = getColor(context, android.R.color.system_accent2_500)
    val SA2_600 = getColor(context, android.R.color.system_accent2_600)
    val SA2_700 = getColor(context, android.R.color.system_accent2_700)
    val SA2_800 = getColor(context, android.R.color.system_accent2_800)
    val SA2_900 = getColor(context, android.R.color.system_accent2_900)

    val SA1_10 = getColor(context, android.R.color.system_accent1_10)
    val SA1_50 = getColor(context, android.R.color.system_accent1_50)
    val SA1_100 = getColor(context, android.R.color.system_accent1_100)
    val SA1_200 = getColor(context, android.R.color.system_accent1_200)
    val SA1_300 = getColor(context, android.R.color.system_accent1_300)
    val SA1_400 = getColor(context, android.R.color.system_accent1_400)
    val SA1_500 = getColor(context, android.R.color.system_accent1_500)
    val SA1_600 = getColor(context, android.R.color.system_accent1_600)
    val SA1_700 = getColor(context, android.R.color.system_accent1_700)
    val SA1_800 = getColor(context, android.R.color.system_accent1_800)
    val SA1_900 = getColor(context, android.R.color.system_accent1_900)

    val SA3_10 = getColor(context, android.R.color.system_accent3_10)
    val SA3_50 = getColor(context, android.R.color.system_accent3_50)
    val SA3_100 = getColor(context, android.R.color.system_accent3_100)
    val SA3_200 = getColor(context, android.R.color.system_accent3_200)
    val SA3_300 = getColor(context, android.R.color.system_accent3_300)
    val SA3_400 = getColor(context, android.R.color.system_accent3_400)
    val SA3_500 = getColor(context, android.R.color.system_accent3_500)
    val SA3_600 = getColor(context, android.R.color.system_accent3_600)
    val SA3_700 = getColor(context, android.R.color.system_accent3_700)
    val SA3_800 = getColor(context, android.R.color.system_accent3_800)
    val SA3_900 = getColor(context, android.R.color.system_accent3_900)


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
                    text = "FabricatedMonet",
                    fontSize = 24.sp
                )

                IconButton(modifier = Modifier, onClick = {
                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'ThemedMonet' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
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
                                    Shell.SH.run(
                                        "su -c cmd overlay fabricate --target android --name ThemedMonet$colorName color/$fullName 0x1c ${
                                            "0x%08X".format(themedColor.toArgb())
                                        }"
                                    )
                                    Shell.SH.run("su -c cmd overlay enable com.android.shell:ThemedMonet$colorName")

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
                        valueRange = -10f..+10f,
                        onValueChangeFinished = {},
                        steps = 19,
                        thumb = {
                            Image(
                                painter = painterResource(R.drawable.fiber_manual_record_48px),
                                contentDescription = null,
                            )
                        })


                    Text(text = "Apply to")

                    Row {
                        Button(modifier = Modifier.weight(1f), onClick = {

                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSN1_10 color/system_neutral1_10 0x1c ${
                                    "0x%08X".format(C_10.toArgb())
                                }"
                            )

                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSN1_50 color/system_neutral1_50 0x1c ${
                                    "0x%08X".format(C_50.toArgb())
                                }"
                            )

                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSN1_100 color/system_neutral1_100 0x1c ${
                                    "0x%08X".format(C_100.toArgb())
                                }"
                            )


                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSN1_200 color/system_neutral1_200 0x1c ${
                                    "0x%08X".format(C_200.toArgb())
                                }"
                            )


                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSN1_300 color/system_neutral1_300 0x1c ${
                                    "0x%08X".format(C_300.toArgb())
                                }"
                            )

                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSN1_400 color/system_neutral1_400 0x1c ${
                                    "0x%08X".format(C_400.toArgb())
                                }"
                            )

                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSN1_500 color/system_neutral1_500 0x1c ${
                                    "0x%08X".format(C_500.toArgb())
                                }"
                            )

                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSN1_600 color/system_neutral1_600 0x1c ${
                                    "0x%08X".format(C_600.toArgb())
                                }"
                            )

                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSN1_700 color/system_neutral1_700 0x1c ${
                                    "0x%08X".format(C_700.toArgb())
                                }"
                            )

                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSN1_800 color/system_neutral1_800 0x1c ${
                                    "0x%08X".format(C_800.toArgb())
                                }"
                            )

                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSN1_900 color/system_neutral1_900 0x1c ${
                                    "0x%08X".format(C_900.toArgb())
                                }"
                            )



                            Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'ThemedMonetSN1'  | sed -E 's/^....//'); do cmd overlay enable \"\$ol\"; done")

                        }) {
                            Text(text = "N1")

                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        if (!getOverlayList().unsupportedOverlays.contains("n2")) {
                            Button(modifier = Modifier.weight(1f), onClick = {

                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSN2_10 color/system_neutral2_10 0x1c ${
                                        "0x%08X".format(C_10.toArgb())
                                    }"
                                )

                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSN2_50 color/system_neutral2_50 0x1c ${
                                        "0x%08X".format(C_50.toArgb())
                                    }"
                                )

                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSN2_100 color/system_neutral2_100 0x1c ${
                                        "0x%08X".format(C_100.toArgb())
                                    }"
                                )


                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSN2_200 color/system_neutral2_200 0x1c ${
                                        "0x%08X".format(C_200.toArgb())
                                    }"
                                )


                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSN2_300 color/system_neutral2_300 0x1c ${
                                        "0x%08X".format(C_300.toArgb())
                                    }"
                                )

                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSN2_400 color/system_neutral2_400 0x1c ${
                                        "0x%08X".format(C_400.toArgb())
                                    }"
                                )

                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSN2_500 color/system_neutral2_500 0x1c ${
                                        "0x%08X".format(C_500.toArgb())
                                    }"
                                )

                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSN2_600 color/system_neutral2_600 0x1c ${
                                        "0x%08X".format(C_600.toArgb())
                                    }"
                                )

                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSN2_700 color/system_neutral2_700 0x1c ${
                                        "0x%08X".format(C_700.toArgb())
                                    }"
                                )

                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSN2_800 color/system_neutral2_800 0x1c ${
                                        "0x%08X".format(C_800.toArgb())
                                    }"
                                )

                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSN2_900 color/system_neutral2_900 0x1c ${
                                        "0x%08X".format(C_900.toArgb())
                                    }"
                                )



                                Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'ThemedMonetSN2'  | sed -E 's/^....//'); do cmd overlay enable \"\$ol\"; done")

                            }) {
                                Text(text = "N2")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Button(modifier = Modifier.weight(1f), onClick = {

                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSA1_10 color/system_accent1_10 0x1c ${
                                    "0x%08X".format(C_10.toArgb())
                                }"
                            )

                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSA1_50 color/system_accent1_50 0x1c ${
                                    "0x%08X".format(C_50.toArgb())
                                }"
                            )

                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSA1_100 color/system_accent1_100 0x1c ${
                                    "0x%08X".format(C_100.toArgb())
                                }"
                            )


                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSA1_200 color/system_accent1_200 0x1c ${
                                    "0x%08X".format(C_200.toArgb())
                                }"
                            )


                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSA1_300 color/system_accent1_300 0x1c ${
                                    "0x%08X".format(C_300.toArgb())
                                }"
                            )

                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSA1_400 color/system_accent1_400 0x1c ${
                                    "0x%08X".format(C_400.toArgb())
                                }"
                            )

                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSA1_500 color/system_accent1_500 0x1c ${
                                    "0x%08X".format(C_500.toArgb())
                                }"
                            )

                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSA1_600 color/system_accent1_600 0x1c ${
                                    "0x%08X".format(C_600.toArgb())
                                }"
                            )

                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSA1_700 color/system_accent1_700 0x1c ${
                                    "0x%08X".format(C_700.toArgb())
                                }"
                            )

                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSA1_800 color/system_accent1_800 0x1c ${
                                    "0x%08X".format(C_800.toArgb())
                                }"
                            )

                            Shell.SH.run(
                                "su -c cmd overlay fabricate --target android --name ThemedMonetSA1_900 color/system_accent1_900 0x1c ${
                                    "0x%08X".format(C_900.toArgb())
                                }"
                            )



                            Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'ThemedMonetSA1'  | sed -E 's/^....//'); do cmd overlay enable \"\$ol\"; done")
                        }) {
                            Text(text = "A1")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                       
                            Button(modifier = Modifier.weight(1f), onClick = {
                                

                                    Shell.SH.run(
                                        "su -c cmd overlay fabricate --target android --name ThemedMonetSA2_10 color/system_accent2_10 0x1c ${
                                            "0x%08X".format(C_10.toArgb())
                                        }"
                                    )

                                    Shell.SH.run(
                                        "su -c cmd overlay fabricate --target android --name ThemedMonetSA2_50 color/system_accent2_50 0x1c ${
                                            "0x%08X".format(C_50.toArgb())
                                        }"
                                    )

                                    Shell.SH.run(
                                        "su -c cmd overlay fabricate --target android --name ThemedMonetSA2_100 color/system_accent2_100 0x1c ${
                                            "0x%08X".format(C_100.toArgb())
                                        }"
                                    )


                                    Shell.SH.run(
                                        "su -c cmd overlay fabricate --target android --name ThemedMonetSA2_200 color/system_accent2_200 0x1c ${
                                            "0x%08X".format(C_200.toArgb())
                                        }"
                                    )


                                    Shell.SH.run(
                                        "su -c cmd overlay fabricate --target android --name ThemedMonetSA2_300 color/system_accent2_300 0x1c ${
                                            "0x%08X".format(C_300.toArgb())
                                        }"
                                    )

                                    Shell.SH.run(
                                        "su -c cmd overlay fabricate --target android --name ThemedMonetSA2_400 color/system_accent2_400 0x1c ${
                                            "0x%08X".format(C_400.toArgb())
                                        }"
                                    )

                                    Shell.SH.run(
                                        "su -c cmd overlay fabricate --target android --name ThemedMonetSA2_500 color/system_accent2_500 0x1c ${
                                            "0x%08X".format(C_500.toArgb())
                                        }"
                                    )

                                    Shell.SH.run(
                                        "su -c cmd overlay fabricate --target android --name ThemedMonetSA2_600 color/system_accent2_600 0x1c ${
                                            "0x%08X".format(C_600.toArgb())
                                        }"
                                    )

                                    Shell.SH.run(
                                        "su -c cmd overlay fabricate --target android --name ThemedMonetSA2_700 color/system_accent2_700 0x1c ${
                                            "0x%08X".format(C_700.toArgb())
                                        }"
                                    )

                                    Shell.SH.run(
                                        "su -c cmd overlay fabricate --target android --name ThemedMonetSA2_800 color/system_accent2_800 0x1c ${
                                            "0x%08X".format(C_800.toArgb())
                                        }"
                                    )

                                    Shell.SH.run(
                                        "su -c cmd overlay fabricate --target android --name ThemedMonetSA2_900 color/system_accent2_900 0x1c ${
                                            "0x%08X".format(C_900.toArgb())
                                        }"
                                    )



                                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'ThemedMonetSA2'  | sed -E 's/^....//'); do cmd overlay enable \"\$ol\"; done")
                                
                            }) {
                                Text(text = "A2")
                            }
                            Spacer(modifier = Modifier.width(8.dp))

                            Button(modifier = Modifier.weight(1f), onClick = {
                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSA3_10 color/system_accent3_10 0x1c ${
                                        "0x%08X".format(C_10.toArgb())
                                    }"
                                )

                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSA3_50 color/system_accent3_50 0x1c ${
                                        "0x%08X".format(C_50.toArgb())
                                    }"
                                )

                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSA3_100 color/system_accent3_100 0x1c ${
                                        "0x%08X".format(C_100.toArgb())
                                    }"
                                )


                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSA3_200 color/system_accent3_200 0x1c ${
                                        "0x%08X".format(C_200.toArgb())
                                    }"
                                )


                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSA3_300 color/system_accent3_300 0x1c ${
                                        "0x%08X".format(C_300.toArgb())
                                    }"
                                )

                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSA3_400 color/system_accent3_400 0x1c ${
                                        "0x%08X".format(C_400.toArgb())
                                    }"
                                )

                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSA3_500 color/system_accent3_500 0x1c ${
                                        "0x%08X".format(C_500.toArgb())
                                    }"
                                )

                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSA3_600 color/system_accent3_600 0x1c ${
                                        "0x%08X".format(C_600.toArgb())
                                    }"
                                )

                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSA3_700 color/system_accent3_700 0x1c ${
                                        "0x%08X".format(C_700.toArgb())
                                    }"
                                )

                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSA3_800 color/system_accent3_800 0x1c ${
                                        "0x%08X".format(C_800.toArgb())
                                    }"
                                )

                                Shell.SH.run(
                                    "su -c cmd overlay fabricate --target android --name ThemedMonetSA3_900 color/system_accent3_900 0x1c ${
                                        "0x%08X".format(C_900.toArgb())
                                    }"
                                )



                                Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'ThemedMonetSA3'  | sed -E 's/^....//'); do cmd overlay enable \"\$ol\"; done")
                            }) {
                                Text(text = "A3")
                            }
                        
                    }

                }
            }
        }
    }
}

@Composable
fun VerticalGrid(
    modifier: Modifier = Modifier, columns: Int = 2, content: @Composable () -> Unit
) {
    Layout(
        content = content, modifier = modifier
    ) { measurables, constraints ->
        val itemWidth = constraints.maxWidth / columns

        val itemConstraints = constraints.copy(
            minWidth = itemWidth, maxWidth = itemWidth
        )

        val placeables = measurables.map { it.measure(itemConstraints) }

        val columnHeights = Array(columns) { 0 }
        placeables.forEachIndexed { index, placeable ->
            val column = index % columns
            columnHeights[column] += placeable.height
        }
        val height = (columnHeights.maxOrNull() ?: constraints.minHeight).coerceAtMost(
            constraints.maxHeight
        )
        layout(
            width = constraints.maxWidth, height = height
        ) {
            val columnY = Array(columns) { 0 }
            placeables.forEachIndexed { index, placeable ->
                val column = index % columns
                placeable.placeRelative(
                    x = column * itemWidth, y = columnY[column]
                )
                columnY[column] += placeable.height
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ColorTilesRow(
    name: String, colors: List<String>

) {
    VerticalGrid(columns = 8) {
        colors.forEach { color ->
            ColorTile(
                name = name, color = color, modifier = Modifier.aspectRatio(1f)
            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AccentsNewTemp(
    name: String, header: String
) {
    var redvisible by rememberSaveable { mutableStateOf(false) }
    var orangevisible by rememberSaveable { mutableStateOf(false) }
    var yellowvisible by rememberSaveable { mutableStateOf(false) }
    var lightgreenvisible by rememberSaveable { mutableStateOf(false) }
    var tealvisible by rememberSaveable { mutableStateOf(false) }
    var lightbluevisible by rememberSaveable { mutableStateOf(false) }
    var indigovisible by rememberSaveable { mutableStateOf(false) }
    var purplevisible by rememberSaveable { mutableStateOf(false) }

    val tilesize = (LocalConfiguration.current.smallestScreenWidthDp - 16) / 8
    Card(
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.bordercol),
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
                    text = header,
                    fontSize = 24.sp
                )
                IconButton(modifier = Modifier, onClick = {
                    redvisible = !redvisible
                    orangevisible = !orangevisible
                    yellowvisible = !yellowvisible
                    lightgreenvisible = !lightgreenvisible
                    tealvisible = !tealvisible
                    lightbluevisible = !lightbluevisible
                    indigovisible = !indigovisible
                    purplevisible = !purplevisible
                }) {
                    Image(
                        painter = painterResource(R.drawable.expand_more_48px),
                        contentDescription = null,
                    )
                }
                IconButton(modifier = Modifier, onClick = {
                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'themed.$name' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
                }) {
                    Image(
                        painter = painterResource(R.drawable.reset),
                        contentDescription = null,
                    )
                }
            }
            Column {
                //this annoying piece of shit
                Row {


                    Surface(modifier = Modifier.size(tilesize.dp),
                        color = Color(0xFFF44336),
                        onClick = {
                            redvisible = !redvisible
                            orangevisible = false
                            yellowvisible = false
                            lightgreenvisible = false
                            tealvisible = false
                            lightbluevisible = false
                            indigovisible = false
                            purplevisible = false
                        }) {
                        AnimatedVisibility(
                            visible = redvisible, enter = fadeIn(), exit = fadeOut()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.expand_less_48px),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(0.9f)
                            )
                        }


                    }

                    Surface(modifier = Modifier.size(tilesize.dp),
                        color = Color(0xFFFF9800),
                        onClick = {
                            redvisible = false
                            orangevisible = !orangevisible
                            yellowvisible = false
                            lightgreenvisible = false
                            tealvisible = false
                            lightbluevisible = false
                            indigovisible = false
                            purplevisible = false
                        }) {
                        AnimatedVisibility(
                            visible = orangevisible, enter = fadeIn(), exit = fadeOut()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.expand_less_48px),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(0.9f)
                            )
                        }


                    }
                    Surface(modifier = Modifier.size(tilesize.dp),
                        color = Color(0xFFFFC107),
                        onClick = {
                            redvisible = false
                            orangevisible = false
                            yellowvisible = !yellowvisible
                            lightgreenvisible = false
                            tealvisible = false
                            lightbluevisible = false
                            indigovisible = false
                            purplevisible = false
                        }) {
                        AnimatedVisibility(
                            visible = yellowvisible, enter = fadeIn(), exit = fadeOut()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.expand_less_48px),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(0.9f)
                            )
                        }


                    }
                    Surface(modifier = Modifier.size(tilesize.dp),
                        color = Color(0xFF8BC34A),
                        onClick = {
                            redvisible = false
                            orangevisible = false
                            yellowvisible = false
                            lightgreenvisible = !lightgreenvisible
                            tealvisible = false
                            lightbluevisible = false
                            indigovisible = false
                            purplevisible = false
                        }) {
                        AnimatedVisibility(
                            visible = lightgreenvisible, enter = fadeIn(), exit = fadeOut()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.expand_less_48px),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(0.9f)
                            )
                        }


                    }
                    Surface(modifier = Modifier.size(tilesize.dp),
                        color = Color(0xFF009688),
                        onClick = {
                            redvisible = false
                            orangevisible = false
                            yellowvisible = false
                            lightgreenvisible = false
                            tealvisible = !tealvisible
                            lightbluevisible = false
                            indigovisible = false
                            purplevisible = false
                        }) {
                        AnimatedVisibility(
                            visible = tealvisible, enter = fadeIn(), exit = fadeOut()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.expand_less_48px),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(0.9f)
                            )
                        }


                    }
                    Surface(modifier = Modifier.size(tilesize.dp),
                        color = Color(0xFF03A9F4),
                        onClick = {
                            redvisible = false
                            orangevisible = false
                            yellowvisible = false
                            lightgreenvisible = false
                            tealvisible = false
                            lightbluevisible = !lightbluevisible
                            indigovisible = false
                            purplevisible = false
                        }) {
                        AnimatedVisibility(
                            visible = lightbluevisible, enter = fadeIn(), exit = fadeOut()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.expand_less_48px),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(0.9f)
                            )
                        }


                    }
                    Surface(modifier = Modifier.size(tilesize.dp),
                        color = Color(0xFF3F51B5),
                        onClick = {
                            redvisible = false
                            orangevisible = false
                            yellowvisible = false
                            lightgreenvisible = false
                            tealvisible = false
                            lightbluevisible = false
                            indigovisible = !indigovisible
                            purplevisible = false
                        }) {
                        AnimatedVisibility(
                            visible = indigovisible, enter = fadeIn(), exit = fadeOut()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.expand_less_48px),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(0.9f)
                            )
                        }


                    }
                    Surface(modifier = Modifier.size(tilesize.dp),
                        color = Color(0xFF9C27B0),
                        onClick = {
                            redvisible = false
                            orangevisible = false
                            yellowvisible = false
                            lightgreenvisible = false
                            tealvisible = false
                            lightbluevisible = false
                            indigovisible = false
                            purplevisible = !purplevisible
                        }) {
                        AnimatedVisibility(
                            visible = purplevisible, enter = fadeIn(), exit = fadeOut()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.expand_less_48px),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(0.9f)
                            )
                        }


                    }
                }

                AnimatedVisibility(redvisible) {
                    ColorTilesRow(
                        name, listOf(
                            "#FFC51162", "#FFE91E63", "#FFE91E63", "#FFF50057",

                            "#FFD50000", "#FFF44336", "#FFFF5252", "#FFFF1744"
                        )
                    )
                }

                AnimatedVisibility(orangevisible) {
                    ColorTilesRow(
                        name, listOf(

                            "#FFDD2C00", "#FFDD2C00", "#FFFF6E40", "#FFFF3D00",

                            "#FFFF6D00", "#FFFF9800", "#FFFFAB40", "#FFFF9100"
                        )
                    )
                }
                AnimatedVisibility(yellowvisible) {
                    ColorTilesRow(
                        name, listOf(


                            "#FFFFAB00", "#FFFFC107", "#FFFFD740", "#FFFFC400",

                            "#FFFFD600", "#FFFFEB3B", "#FFFFFF00", "#FFFFEA00"
                        )
                    )


                }
                AnimatedVisibility(lightgreenvisible) {
                    ColorTilesRow(
                        name, listOf(

                            "#FFAEEA00", "#FFCDDC39", "#FFEEFF41", "#FFC6FF00",

                            "#FF64DD17", "#FF8BC34A", "#FFB2FF59", "#FF76FF03"

                        )
                    )
                }
                AnimatedVisibility(tealvisible) {
                    ColorTilesRow(
                        name, listOf(

                            "#FF00C853", "#FF4CAF50", "#FF69F0AE", "#FF00E676",

                            "#FF00BFA5", "#FF009688", "#FF64FFDA", "#FF1DE9B6"

                        )
                    )
                }
                AnimatedVisibility(lightbluevisible) {
                    ColorTilesRow(
                        name, listOf(

                            "#FF0091EA", "#FF00BCD4", "#FF18FFFF", "#FF00E5FF",

                            "#FF0091EA", "#FF03A9F4", "#FF40C4FF", "#FF00B0FF"

                        )
                    )
                }
                AnimatedVisibility(indigovisible) {
                    ColorTilesRow(
                        name, listOf(

                            "#FF2962FF", "#FF2196F3", "#FF448AFF", "#FF2979FF",

                            "#FF304FFE", "#FF3F51B5", "#FF536DFE", "#FF3D5AFE"

                        )
                    )
                }
                AnimatedVisibility(purplevisible) {
                    ColorTilesRow(
                        name, listOf(

                            "#FF6200EA", "#FF673AB7", "#FF7C4DFF", "#FF651FFF",

                            "#FFAA00FF", "#FF9C27B0", "#FFE040FB", "#FFD500F9"


                        )
                    )
                }


            }
        }
    }
}


//@Preview
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ColorsTab() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp),
        color = MaterialTheme.colors.cardcol
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {


            var state by rememberSaveable { mutableStateOf(CardFace.Front) }
            FlipCard(cardFace = state, onClick = {
                state = it.next
            }, axis = RotationAxis.AxisY, back = {
                if (!getOverlayList().overlayList.any { it.contains("accents.dark") }) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp), text = ""
                    )

                } else {
                    if (getOverlayList().unsupportedOverlays.any { it.contains("accents.dark") }) {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp), text = ""
                        )
                    } else {
                        AccentsNewTemp(
                            "accents.dark", stringResource(R.string.accents_dark)
                        )

                    }
                }


            }, front = {

                if (!getOverlayList().overlayList.any { it.contains("accents.F") }) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp), text = ""
                    )

                } else {
                    if (getOverlayList().unsupportedOverlays.any { it.contains("accents.F") }) {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp), text = ""
                        )
                    } else {
                        AccentsNewTemp(
                            "accents", stringResource(R.string.accents)
                        )

                    }
                }

            })

            Spacer(modifier = Modifier.height(8.dp))


            FakeMonet()
            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(8.dp))


            if (getOverlay().contains("fabricate")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    FabricatedMonet()
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HeaderRowWithSwitch(header = "Disable Monet",
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
                })


        }

    }


}

@file:OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalFoundationApi::class
)

package pro.themed.manager.comps

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.CardFace
import pro.themed.manager.FlipCard
import pro.themed.manager.R
import pro.themed.manager.RotationAxis
import pro.themed.manager.bordercol
import pro.themed.manager.cardcol
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


/*@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ColorTilesRow(name: String, colors: List<String>) {
    Row(   verticalAlignment = CenterVertically) {
        colors.forEach { color ->
                ColorTile(name, color,  modifier = Modifier
                    .aspectRatio(1f)
                    .weight(1f))

        }
    }
}*/


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FakeMonet(

) {
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

            var hue by rememberSaveable { mutableStateOf(0f) }
            var saturation by rememberSaveable { mutableStateOf(100f) }

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

                    var isDark by rememberSaveable { mutableStateOf("") }

                    // Text(text = "fakemonet.n1h${hue.toInt()}s${saturation.toInt()}$isDark")

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
                            overlayEnable("fakemonet.n1h${hue.toInt()}s${saturation.toInt()}$isDark")
                        }) {
                            Text(text = "N1")

                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        if (!getOverlayList().unsupportedOverlays.contains("n2")) {
                            Button(modifier = Modifier.weight(1f), onClick = {
                                overlayEnable("fakemonet.n2h${hue.toInt()}s${saturation.toInt()}$isDark")
                            }) {
                                Text(text = "N2")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Button(modifier = Modifier.weight(1f), onClick = {
                            overlayEnable("fakemonet.a1h${hue.toInt()}s${saturation.toInt()}$isDark")
                        }) {
                            Text(text = "A1")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        if (!getOverlayList().unsupportedOverlays.contains("a2") || !getOverlayList().unsupportedOverlays.contains(
                                "a3"
                            )
                        ) {
                            Button(modifier = Modifier.weight(1f), onClick = {
                                overlayEnable("fakemonet.a2h${hue.toInt()}s${saturation.toInt()}$isDark")
                            }) {
                                Text(text = "A2")
                            }
                            Spacer(modifier = Modifier.width(8.dp))

                            Button(modifier = Modifier.weight(1f), onClick = {
                                overlayEnable("fakemonet.a3h${hue.toInt()}s${saturation.toInt()}$isDark")
                            }) {
                                Text(text = "A3")
                            }
                        }
                    }
                    HeaderRowWithSwitch(
                        header = "Disable Monet",
                        subHeader = "Ye you need to enable this first, duh",
                        isChecked =  getOverlayList().enabledOverlays.any { it.contains("flagmonet") },
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
                        }
                    )

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

        }

    }


}

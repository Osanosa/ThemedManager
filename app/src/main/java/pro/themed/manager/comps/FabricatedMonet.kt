package pro.themed.manager.comps

import android.content.res.*
import android.widget.*
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.hsl
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.core.graphics.*
import com.jaredrummler.ktsh.*
import com.jaredrummler.ktsh.Shell.*
import kotlinx.coroutines.*
import pro.themed.manager.*
import pro.themed.manager.MainActivity.Companion.isDark
import pro.themed.manager.R.*
import pro.themed.manager.components.*
import pro.themed.manager.ui.theme.*
import pro.themed.manager.utils.*
import kotlin.math.*

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class,
)
@Composable fun FabricatedMonet(scroll: ScrollState) {
    val context = LocalContext.current
    var selectedColorReference by remember { mutableStateOf("") }
    var selectedMonetColor by remember { mutableStateOf("system_accent1_500") }
    var isGridExpanded by remember { mutableStateOf(false) }
    var expanded by rememberSaveable { mutableStateOf(true) }

    var isColorReferenceDropdownExpanded by remember { mutableStateOf(false) }
    LaunchedEffect(isColorReferenceDropdownExpanded) {
        if (isColorReferenceDropdownExpanded) {
            scroll.animateScrollTo(Int.MAX_VALUE)
        }
    }

    val colorsPath = """${GlobalVariables.modulePath}/onDemandCompiler/fakeMonet"""
    val colorsShell = Shell("su")
    colorsShell.addOnStderrLineListener(object : OnLineListener {
        override fun onLine(line: String) {
            CoroutineScope(Dispatchers.Main).launch {
                line.log("FM ERROR")
            }
        }
    })

    colorsShell.addOnCommandResultListener(object : OnCommandResultListener {
        override fun onResult(result: Command.Result) {
            CoroutineScope(Dispatchers.Main).launch {
                result.log("FM RESULT")
            }
        }
    })


    colorsShell.run("cd $colorsPath")

    var colorsXmlContent by remember {
        mutableStateOf(colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout)
    }

    fun resetColorsXmlContent() {
        colorsXmlContent =
            colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout
    }
    LaunchedEffect(isDark) {
        colorsXmlContent =
            colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout
    }

    LaunchedEffect(selectedColorReference) {
        val colorValue =
            colorsXmlContent.find { it.contains("<color name=\"$selectedColorReference\">") }?.substringAfter("@color/")
                ?.substringBefore("</color>") ?: ""
        if (selectedColorReference.isNotBlank()) selectedMonetColor = colorValue
    }

    LaunchedEffect(selectedMonetColor) {
        val sedCommand = """
        sed -i 's|<color name="$selectedColorReference">@color/[^<]*</color>|<color name="$selectedColorReference">@color/$selectedMonetColor</color>|' /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml
    """.trimIndent()
        if (selectedMonetColor.isNotBlank() && selectedColorReference.isNotBlank()) {
            colorsShell.run(sedCommand)
        }
        resetColorsXmlContent()
    }
    fun getColorValue(colorName: String): Int {
        val colorLine = colorsXmlContent.find { it.contains("<color name=\"$colorName\">") }
        return if (colorLine?.contains("@color/") == true) { // If the color line references another color, recursively resolve the reference
            val referencedColorName = colorLine.substringAfter("@color/").substringBefore("</color>")
            getColorValue(referencedColorName)
        }
        else { // If the color line contains an actual color value, parse and return it
            colorLine?.substringAfter("#")?.substringBefore("</color>")
                ?.let { android.graphics.Color.parseColor("#$it") }
                ?: android.graphics.Color.WHITE // Default color is white
        }
    }

    fun updateColor(colorName: String, colorValue: Int?) {
        colorsShell.run("cd $colorsPath")
        colorsShell.run("""sed -i -r '/$colorName">/ s/>#[0-9A-Fa-f]{6,8}</>#${
            "%08x".format(colorValue ?: 0)
        }</g' res/values$isDark/colors.xml""")

    }

    var hue by rememberSaveable { mutableFloatStateOf(0.5f) }
    var saturation by rememberSaveable { mutableFloatStateOf(100f) }
    var lightness by rememberSaveable { mutableFloatStateOf(0f) }

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

    fun updateBatchColors(name: String) {
        updateColor(name + "_10", C_10.toArgb())
        updateColor(name + "_50", C_50.toArgb())
        updateColor(name + "_100", C_100.toArgb())
        updateColor(name + "_200", C_200.toArgb())
        updateColor(name + "_300", C_300.toArgb())
        updateColor(name + "_400", C_400.toArgb())
        updateColor(name + "_500", C_500.toArgb())
        updateColor(name + "_600", C_600.toArgb())
        updateColor(name + "_700", C_700.toArgb())
        updateColor(name + "_800", C_800.toArgb())
        updateColor(name + "_900", C_900.toArgb())
        resetColorsXmlContent()
    }

    @Composable fun ThemedColor(colorName: String) = when {
        colorName.endsWith("10")  -> C_10
        colorName.endsWith("50")  -> C_50
        colorName.endsWith("100") -> C_100
        colorName.endsWith("200") -> C_200
        colorName.endsWith("300") -> C_300
        colorName.endsWith("400") -> C_400
        colorName.endsWith("500") -> C_500
        colorName.endsWith("600") -> C_600
        colorName.endsWith("700") -> C_700
        colorName.endsWith("800") -> C_800
        colorName.endsWith("900") -> C_900
        else                      -> White
    }


    CookieCard(onClick = { expanded = !expanded }) {

        Column(Modifier.animateContentSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = CenterVertically,

                ) {
                Text(modifier = Modifier
                    .padding(8.dp)
                    .padding(start = 8.dp), text = "Monet", fontSize = 24.sp)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    isColorReferenceDropdownExpanded = !isColorReferenceDropdownExpanded
                    isGridExpanded = false

                }) {
                    Icon(modifier = Modifier,
                        painter = painterResource(id = drawable.tactic_24px),
                        contentDescription = "Expand")
                }
                IconButton(onClick = {
                    isGridExpanded = !isGridExpanded; isColorReferenceDropdownExpanded = false
                }) {
                    Icon(modifier = Modifier,
                        painter = painterResource(id = drawable.background_grid_small_24px),
                        contentDescription = "Expand")
                }
                IconButton(onClick = {
                    expanded = !expanded
                }, colors = IconButtonDefaults.iconButtonColors(containerColor = if (!expanded) {
                    Color.Gray
                }
                else {
                    Transparent
                })) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null)
                }
            }
            val configuration = LocalConfiguration.current
            val divisor = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                5
            }
            else {
                12
            }
            val tilesize = (((configuration.screenWidthDp - 16 - 64 - 16) / divisor)).dp
            HorizontalDivider()

            Box {
                androidx.compose.animation.AnimatedVisibility(visible = !expanded,
                    modifier = Modifier.padding(8.dp),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()) { //Tutorial text
                    Text(
                        text = stringResource(string.monet_tutorial),
                    )
                }

                androidx.compose.animation.AnimatedVisibility(visible = expanded,
                    modifier = Modifier.wrapContentHeight(),
                    enter = fadeIn() + expandVertically(initialHeight = { it / 2 }),
                    exit = fadeOut() + shrinkVertically(targetHeight = { it / 2 })) {
                    Column(horizontalAlignment = CenterHorizontally,
                        modifier = Modifier.imePadding() // .background(cardcol)
                    ) {
                        @Stable @Composable fun M3Tile(
                            color: Int?,
                            colorName: String,
                            themedColor: Color,
                            topStart: Float = 0f,
                            topEnd: Float = 0f,
                            bottomStart: Float = 0f,
                            bottomEnd: Float = 0f,
                        ) {
                            color?.let { Color(it) }?.let {
                                Surface(shape = RoundedCornerShape(topStart = topStart,
                                    topEnd = topEnd,
                                    bottomStart = bottomStart,
                                    bottomEnd = bottomEnd), modifier = Modifier
                                    .width(tilesize)
                                    .aspectRatio(2f)

                                    .combinedClickable(onClick = {

                                        val hex = "%08x".format(themedColor.toArgb())
                                        colorsShell.run("cd $colorsPath")
                                        colorsShell.run("""sed -i -r '/$colorName">/ s/>#[0-9A-Fa-f]{6,8}</>#$hex</g' res/values$isDark/colors.xml""")
                                        resetColorsXmlContent()

                                    }, onLongClick = {
                                        Toast.makeText(MainActivity.appContext, "", Toast.LENGTH_SHORT).show()
                                    }), color = it) {
                                    val textColor = getContrastColor(color)

                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = colorName.substringAfter("_").substringAfter("_"),
                                            color = textColor,
                                            fontSize = 14.sp)
                                    }
                                }
                            }
                        }

                        val values = listOf(10, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900)

                        @Composable fun tiles(name: String) {
                            listOf(name) + values.map { "$name$it" }.forEach { colorName ->
                                M3Tile(color = getColorValue(colorName),
                                    colorName = colorName,
                                    themedColor = ThemedColor(colorName))
                            }

                        }

                        val configuration = LocalConfiguration.current
                        AnimatedVisibility(visible = isGridExpanded) {
                            if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                                Row(Modifier
                                    .wrapContentWidth(unbounded = true)
                                    .clip(RoundedCornerShape(12.dp))) {
                                    listOf(
                                        "system_neutral1_" to "N1",
                                        "system_neutral2_" to "N2",
                                        "system_accent1_" to "A1",
                                        "system_accent2_" to "A2",
                                        "system_accent3_" to "A3",
                                    ).forEach { (colorName, colorReference) ->
                                        Column(horizontalAlignment = CenterHorizontally) {
                                            Text(text = colorReference,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(4.dp))
                                            tiles(colorName)
                                        }
                                    }

                                }

                            }
                            else {
                                Column(Modifier
                                    .wrapContentWidth(unbounded = true)
                                    .clip(RoundedCornerShape(12.dp))) {
                                    listOf("system_neutral1",
                                        "system_neutral2",
                                        "system_accent1",
                                        "system_accent2",
                                        "system_accent3").forEach { colorName ->
                                        Row {
                                            tiles(colorName + "_")
                                        }

                                    }

                                }
                            }
                        }
                        AnimatedVisibility(visible = !isColorReferenceDropdownExpanded) {
                            Column {
                                Row {
                                    mapOf("system_neutral1" to "N1",
                                        "system_neutral2" to "N2",
                                        "system_accent1" to "A1",
                                        "system_accent2" to "A2",
                                        "system_accent3" to "A3").forEach {
                                        Button(modifier = Modifier
                                            .weight(1f)
                                            .padding(2.dp),
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(getColorValue(it.key + "_500")),
                                            ),
                                            contentPadding = PaddingValues(0.dp),
                                            onClick = {
                                                updateBatchColors(it.key)
                                            }) {
                                            Text(text = it.value)
                                        }
                                    }

                                }
                                Column(modifier = Modifier.padding(8.dp)) {
                                    val colors = mutableListOf<Color>()
                                    for (h in 0..360 step 60) {
                                        colors.add(hsl(h.toFloat(), 1f, 0.5f))
                                    }
                                    Text(text = "hue is ${hue.toInt()}°")
                                    val sliderColors = SliderDefaults.colors(activeTrackColor = Transparent,
                                        inactiveTrackColor = Transparent,
                                        thumbColor = White,
                                        activeTickColor = White,
                                        inactiveTickColor = White)

                                    Slider(colors = sliderColors,
                                        modifier = Modifier
                                            .height(16.dp)
                                            .background(Brush.horizontalGradient(colors = colors), shape = CircleShape)
                                            .padding(0.dp),
                                        value = hue,
                                        onValueChange = {
                                            hue = it.roundToInt().toFloat()
                                        },
                                        valueRange = 0f..360f,
                                        onValueChangeFinished = {},
                                        steps = 71)
                                    Text(text = "saturation is ${saturation.toInt()}%")
                                    Slider(
                                        colors = sliderColors,
                                        modifier = Modifier
                                            .background(Brush.horizontalGradient(colors = (0..10).map {
                                                hsl(hue, it / 10f, 0.5f)
                                            }), shape = CircleShape)
                                            .height(16.dp)
                                            .padding(0.dp),
                                        value = saturation,
                                        onValueChange = {
                                            saturation = it.roundToInt().toFloat()
                                        },
                                        valueRange = 0f..100f,
                                        onValueChangeFinished = {},
                                        steps = 19,
                                    )
                                    Text(text = "Lightness is +/-${lightness.toInt()}")
                                    Slider(
                                        colors = sliderColors,
                                        modifier = Modifier
                                            .background(Brush.horizontalGradient(colors = listOf(hsl(hue,
                                                saturation / 100f,
                                                0.4f),
                                                hsl(hue, saturation / 100f, 0.5f),
                                                hsl(hue, saturation / 100f, 0.6f))), shape = CircleShape)

                                            .height(16.dp)
                                            .padding(0.dp),
                                        value = lightness,
                                        onValueChange = {
                                            lightness = it.roundToInt().toFloat()
                                        },
                                        valueRange = -10f..10f,
                                        onValueChangeFinished = {},
                                        steps = 19,
                                    )

                                }
                            }

                        }

                        AnimatedVisibility(visible = isColorReferenceDropdownExpanded) {
                            @Composable fun ReferenceItem(
                                colorName: String, colorReference: String, colorValue: Int,
                            ) {
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp,
                                        vertical = if (colorName == selectedColorReference) 8.dp else 0.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (colorName == selectedColorReference) {
                                        contentcol.copy(alpha = 0.32f)
                                    }
                                    else {
                                        contentcol.copy(alpha = 0.12f)
                                    })
                                    .clickable(onClick = {
                                        selectedColorReference = colorName
                                        selectedMonetColor = colorReference

                                    }), verticalAlignment = CenterVertically) {
                                    Text(text = colorName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(8.dp))
                                    Text(text = colorReference.replace("system_", "S").replace("neutral", "N")
                                        .replace("accent", "A"),
                                        style = MaterialTheme.typography.bodyMedium.copy(shadow = Shadow(color = if (colorValue
                                                .toColor().luminance() > 0.5f) {
                                            White
                                        }
                                        else {
                                            Black
                                        }, blurRadius = 16f)),
                                        color = if (colorValue.toColor().luminance() > 0.5f) {
                                            Black
                                        }
                                        else {
                                            White
                                        },
                                        modifier = Modifier
                                            .wrapContentWidth(End)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(color = Color(colorValue))
                                            .padding(8.dp)

                                    )
                                }
                            }

                            val colorReferences =
                                colorsXmlContent.map { it.trim() }.filter { it.contains("@color/") }.map {

                                    val colorName = it.substringAfter("<color name=\"").substringBefore("\">@color/")

                                    val colorReference = it.substringAfter("@color/").substringBefore("</color>")
                                        .trim() // trim to remove leading and trailing spaces

                                    val colorValue = getColorValue(colorReference)

                                    Triple(colorName, colorReference, colorValue)
                                }
                            Column {
                                LazyColumn(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    colorReferences.forEach { (colorName, colorReference, colorValue) ->
                                        item {
                                            ReferenceItem(colorName, colorReference, colorValue)
                                        }
                                    }
                                }
                                if (selectedColorReference.isBlank()) Text("Pick a color first ↑↑↑")
                                Row {
                                    listOf("neutral1", "neutral2", "accent1", "accent2", "accent3").forEach { accent ->
                                        val containerColor =
                                            Color(getColorValue("system_$accent" + "_" + selectedMonetColor
                                                .substringAfter("_").substringAfter("_")))
                                        Button(modifier = Modifier
                                            .weight(1f)
                                            .padding(2.dp),
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(containerColor = containerColor,
                                                contentColor = if (containerColor.luminance() > 0.4f) {
                                                    Black
                                                }
                                                else {
                                                    White
                                                }),
                                            contentPadding = PaddingValues(0.dp),
                                            onClick = {
                                                if (selectedColorReference.isBlank()){
                                                    Toast.makeText(context, "Pick a color first", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    selectedMonetColor =
                                                        selectedMonetColor.replaceBeforeLast("_", "system_$accent")
                                                    resetColorsXmlContent()
                                                }
                                            }) {
                                            Text(text = accent.first().uppercase() + accent.last(),
                                                color = if (containerColor.luminance() > 0.4f) {
                                                    Black
                                                }
                                                else {
                                                    White
                                                })
                                        }
                                    }
                                }
                                val nums =
                                    listOf("10", "50", "100", "200", "300", "400", "500", "600", "700", "800", "900")
                                FlowRow(horizontalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier.fillMaxWidth()) {
                                    nums.forEach { num ->
                                        val containerColor =
                                            Color(getColorValue(selectedMonetColor.substringBeforeLast("_") + "_$num"))
                                        Button(contentPadding = PaddingValues(0.dp),
                                            onClick = {
                                                selectedMonetColor =
                                                    selectedMonetColor.replace("_\\d{1,4}".toRegex(), "_$num")
                                                resetColorsXmlContent()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = containerColor,
                                                contentColor = if (containerColor.luminance() > 0.4f) {
                                                    Black
                                                }
                                                else {
                                                    White
                                                })) {
                                            Text(text = num, color = if (containerColor.luminance() > 0.4f) {
                                                Black
                                            }
                                            else {
                                                White
                                            })
                                        }
                                    }
                                }
                            }

                        }

                        Row {
                            Button(onClick = {
                                Shell.SH.run("su -c cmd overlay disable themed.fakemonet.generic")
                                Shell.SH.run("su -c cmd overlay enable com.android.systemui:accent")
                                Shell.SH.run("su -c cmd overlay enable com.android.systemui:neutral")
                                Shell.SH.run("su -c cmd overlay enable com.android.systemui:dynamic")
                                Shell.SH.run("su -c cmd overlay disable themed.misc.flagmonet")
                            }) {
                                Text(text = "Reset")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    buildOverlay(colorsPath)
                                    Shell.SH.run("su -c cmd overlay disable com.android.systemui:accent")
                                    Shell.SH.run("su -c cmd overlay disable com.android.systemui:neutral")
                                    Shell.SH.run("su -c cmd overlay disable com.android.systemui:dynamic")
                                    overlayEnable("misc.flagmonet")

                                    colorsShell.run("""cmd overlay enable themed.fakemonet.generic""")
                                    showInterstitial(context) {}

                                },
                                colors = ButtonDefaults.buttonColors(containerColor = C_500,
                                    contentColor = if ((lightness) > 50f) {
                                        Black
                                    }
                                    else {
                                        White
                                    }),
                                shape = CircleShape) {
                                Row(verticalAlignment = CenterVertically) {
                                    Text(text = "Build and update")
                                    Icon(modifier = Modifier.height(24.dp),
                                        imageVector = ImageVector.vectorResource(id = drawable.arrow_right_alt_48px),
                                        contentDescription = "")

                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                    }
                }
            }
        }
    }
}

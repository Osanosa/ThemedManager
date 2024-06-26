package pro.themed.manager.comps

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.hsl
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColor
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.MainActivity
import pro.themed.manager.MainActivity.Companion.isDark
import pro.themed.manager.R
import pro.themed.manager.buildOverlay
import pro.themed.manager.getContrastColor
import pro.themed.manager.log
import pro.themed.manager.overlayEnable
import pro.themed.manager.ui.theme.contentcol
import pro.themed.manager.utils.GlobalVariables
import pro.themed.manager.utils.showInterstitial
import kotlin.math.roundToInt

annotation class Composable

@Preview
@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
)
@Composable
fun FabricatedMonet(
) {
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

    var selectedColorReference by remember { mutableStateOf("") }
    var selectedMonetColor by remember { mutableStateOf("") }
    var isGridExpanded by remember { mutableStateOf(false) }
    var expanded by rememberSaveable { mutableStateOf(true) }

    var isColorReferenceDropdownExpanded by remember { mutableStateOf(false) }

    val colorsPath = """${GlobalVariables.modulePath}/onDemandCompiler/fakeMonet"""
    val colorsShell = Shell("su")
    colorsShell.run("cd $colorsPath")

    var colorsXmlContent by remember { mutableStateOf(colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout) }

    fun resetColorsXmlContent() {
        colorsXmlContent =
            colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout
    }
    LaunchedEffect(isDark) {
        colorsXmlContent =
            colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout.also { it.log() }
    }

    LaunchedEffect(selectedColorReference) {
        val colorValue =
            colorsXmlContent.find { it.contains("<color name=\"$selectedColorReference\">") }
                ?.substringAfter("@color/")?.substringBefore("</color>") ?: ""
        selectedMonetColor = colorValue
    }

    LaunchedEffect(selectedMonetColor) {
        val sedCommand = """
        sed -i 's|<color name="$selectedColorReference">@color/[^<]*</color>|<color name="$selectedColorReference">@color/$selectedMonetColor</color>|' /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml
    """.trimIndent()
        if (selectedMonetColor.isNotBlank()) {
            colorsShell.run(sedCommand).log()
        }
        resetColorsXmlContent()
    }
    fun getColorValue(colorName: String): Int {
        val colorLine = colorsXmlContent.find { it.contains("<color name=\"$colorName\">") }
        return if (colorLine?.contains("@color/") == true) {
            // If the color line references another color, recursively resolve the reference
            val referencedColorName =
                colorLine.substringAfter("@color/").substringBefore("</color>")
            getColorValue(referencedColorName)
        } else {
            // If the color line contains an actual color value, parse and return it
            colorLine?.substringAfter("#")?.substringBefore("</color>")
                ?.let { android.graphics.Color.parseColor("#$it") }
                ?: android.graphics.Color.WHITE // Default color is white
        }
    }

    fun updateColor(colorName: String, colorValue: Int?) {
        colorsShell.run("cd $colorsPath")
        colorsShell.run(
            """sed -i '/$colorName">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                "%08x".format(
                    colorValue ?: 0
                )
            }</g' res/values$isDark/colors.xml"""
        ).log()

    }

    val sn1_10: Int = getColorValue("system_neutral1_10")
    val sn1_50: Int = getColorValue("system_neutral1_50")
    val sn1_100: Int = getColorValue("system_neutral1_100")
    val sn1_200: Int = getColorValue("system_neutral1_200")
    val sn1_300: Int = getColorValue("system_neutral1_300")
    val sn1_400: Int = getColorValue("system_neutral1_400")
    val sn1_500: Int = getColorValue("system_neutral1_500")
    val sn1_600: Int = getColorValue("system_neutral1_600")
    val sn1_700: Int = getColorValue("system_neutral1_700")
    val sn1_800: Int = getColorValue("system_neutral1_800")
    val sn1_900: Int = getColorValue("system_neutral1_900")

    val sn2_10: Int = getColorValue("system_neutral2_10")
    val sn2_50: Int = getColorValue("system_neutral2_50")
    val sn2_100: Int = getColorValue("system_neutral2_100")
    val sn2_200: Int = getColorValue("system_neutral2_200")
    val sn2_300: Int = getColorValue("system_neutral2_300")
    val sn2_400: Int = getColorValue("system_neutral2_400")
    val sn2_500: Int = getColorValue("system_neutral2_500")
    val sn2_600: Int = getColorValue("system_neutral2_600")
    val sn2_700: Int = getColorValue("system_neutral2_700")
    val sn2_800: Int = getColorValue("system_neutral2_800")
    val sn2_900: Int = getColorValue("system_neutral2_900")

    val sa1_10: Int = getColorValue("system_accent1_10")
    val sa1_50: Int = getColorValue("system_accent1_50")
    val sa1_100: Int = getColorValue("system_accent1_100")
    val sa1_200: Int = getColorValue("system_accent1_200")
    val sa1_300: Int = getColorValue("system_accent1_300")
    val sa1_400: Int = getColorValue("system_accent1_400")
    val sa1_500: Int = getColorValue("system_accent1_500")
    val sa1_600: Int = getColorValue("system_accent1_600")
    val sa1_700: Int = getColorValue("system_accent1_700")
    val sa1_800: Int = getColorValue("system_accent1_800")
    val sa1_900: Int = getColorValue("system_accent1_900")
    val sa1_1000: Int = getColorValue("system_accent1_1000")

    val sa2_10: Int = getColorValue("system_accent2_10")
    val sa2_50: Int = getColorValue("system_accent2_50")
    val sa2_100: Int = getColorValue("system_accent2_100")
    val sa2_200: Int = getColorValue("system_accent2_200")
    val sa2_300: Int = getColorValue("system_accent2_300")
    val sa2_400: Int = getColorValue("system_accent2_400")
    val sa2_500: Int = getColorValue("system_accent2_500")
    val sa2_600: Int = getColorValue("system_accent2_600")
    val sa2_700: Int = getColorValue("system_accent2_700")
    val sa2_800: Int = getColorValue("system_accent2_800")
    val sa2_900: Int = getColorValue("system_accent2_900")

    val sa3_10: Int = getColorValue("system_accent3_10")
    val sa3_50: Int = getColorValue("system_accent3_50")
    val sa3_100: Int = getColorValue("system_accent3_100")
    val sa3_200: Int = getColorValue("system_accent3_200")
    val sa3_300: Int = getColorValue("system_accent3_300")
    val sa3_400: Int = getColorValue("system_accent3_400")
    val sa3_500: Int = getColorValue("system_accent3_500")
    val sa3_600: Int = getColorValue("system_accent3_600")
    val sa3_700: Int = getColorValue("system_accent3_700")
    val sa3_800: Int = getColorValue("system_accent3_800")
    val sa3_900: Int = getColorValue("system_accent3_900")

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

    Card(
        onClick = { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = contentcol.copy(alpha = 0.05f),
            contentColor = contentcol
        ),
        modifier = Modifier.animateContentSize()
    ) {
        Card(
            onClick = { /*TODO*/ },
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = contentcol.copy(alpha = 0.05f), contentColor = contentcol
            ),
            modifier = Modifier
                .animateContentSize()
                .padding(8.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = CenterVertically,

                    ) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp)
                            .padding(start = 8.dp),
                        text = "Monet",
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        isColorReferenceDropdownExpanded =
                            !isColorReferenceDropdownExpanded; isGridExpanded = false
                    }) {
                        Icon(
                            modifier = Modifier,
                            painter = painterResource(id = R.drawable.tactic_24px),
                            contentDescription = "Expand"
                        )
                    }
                    IconButton(onClick = {
                        isGridExpanded = !isGridExpanded; isColorReferenceDropdownExpanded = false
                    }) {
                        Icon(
                            modifier = Modifier,
                            painter = painterResource(id = R.drawable.background_grid_small_24px),
                            contentDescription = "Expand"
                        )
                    }
                    IconButton(
                        onClick = {
                            expanded = !expanded
                        }, colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (!expanded) {
                                Color.Gray
                            } else {
                                Color.Transparent
                            }
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info, contentDescription = null
                        )
                    }
                }
                val configuration = LocalConfiguration.current
                val divisor = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    5
                } else {
                    12
                }
                val tilesize = (((configuration.screenWidthDp - 16 - 64 -16) / divisor)).dp
                tilesize.log()
                HorizontalDivider()

                Box {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !expanded,
                        modifier = Modifier.padding(8.dp),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        //Tutorial text
                        Text(
                            text = stringResource(R.string.monet_tutorial),
                        )
                    }

                    androidx.compose.animation.AnimatedVisibility(
                        visible = expanded,
                        modifier = Modifier.wrapContentHeight(),
                        enter = fadeIn() + expandVertically(initialHeight = { it / 2 }),
                        exit = fadeOut() + shrinkVertically(targetHeight = { it / 2 })
                    ) {
                        Column(
                            horizontalAlignment = CenterHorizontally,
                            modifier = Modifier.imePadding()
                            // .background(cardcol)
                        ) {
                            @Stable
                            @Composable
                            fun M3Tile(
                                color: Int?,
                                colorName: String,
                                themedColor: Color,
                                topStart: Float = 0f,
                                topEnd: Float = 0f,
                                bottomStart: Float = 0f,
                                bottomEnd: Float = 0f
                            ) {
                                color?.let { Color(it) }?.let {
                                    Surface(
                                        shape = RoundedCornerShape(
                                            topStart = topStart,
                                            topEnd = topEnd,
                                            bottomStart = bottomStart,
                                            bottomEnd = bottomEnd
                                        ), modifier = Modifier
                                            .width(tilesize)
                                            .aspectRatio(2f)

                                            .combinedClickable(onClick = {
                                                val fullName = colorName
                                                    .replace("sn", "system_neutral")
                                                    .replace("sa", "system_accent")

                                                val hex = "%08x".format(themedColor.toArgb())

                                                colorsShell.run("cd $colorsPath")

                                                colorsShell.run(
                                                    """sed -i '/$fullName">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#$hex</g' res/values$isDark/colors.xml"""
                                                )
                                                resetColorsXmlContent()

                                            }, onLongClick = {
                                                Toast
                                                    .makeText(
                                                        MainActivity.appContext,
                                                        "",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            }), color = it
                                    ) {
                                        val textColor = getContrastColor(color)

                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = colorName.substringAfter("_"),
                                                color = textColor,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }

                            val configuration = LocalConfiguration.current
                            AnimatedVisibility(visible = isGridExpanded) {
                                if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                                    Row(
                                        Modifier
                                            .wrapContentWidth(unbounded = true)
                                            .clip(RoundedCornerShape(12.dp))
                                    ) {
                                        Column(horizontalAlignment = CenterHorizontally) {
                                            Text(
                                                text = "N1",
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(4.dp)
                                            )
                                            M3Tile(
                                                color = sn1_10,
                                                colorName = "sn1_10",
                                                themedColor = C_10,
                                                topStart = 25f
                                            )
                                            M3Tile(
                                                color = sn1_50,
                                                colorName = "sn1_50",
                                                themedColor = C_50
                                            )
                                            M3Tile(
                                                color = sn1_100,
                                                colorName = "sn1_100",
                                                themedColor = C_100
                                            )
                                            M3Tile(
                                                color = sn1_200,
                                                colorName = "sn1_200",
                                                themedColor = C_200
                                            )
                                            M3Tile(
                                                color = sn1_300,
                                                colorName = "sn1_300",
                                                themedColor = C_300
                                            )
                                            M3Tile(
                                                color = sn1_400,
                                                colorName = "sn1_400",
                                                themedColor = C_400
                                            )
                                            M3Tile(
                                                color = sn1_500,
                                                colorName = "sn1_500",
                                                themedColor = C_500
                                            )
                                            M3Tile(
                                                color = sn1_600,
                                                colorName = "sn1_600",
                                                themedColor = C_600
                                            )
                                            M3Tile(
                                                color = sn1_700,
                                                colorName = "sn1_700",
                                                themedColor = C_700
                                            )
                                            M3Tile(
                                                color = sn1_800,
                                                colorName = "sn1_800",
                                                themedColor = C_800
                                            )
                                            M3Tile(
                                                color = sn1_900,
                                                colorName = "sn1_900",
                                                themedColor = C_900,
                                                bottomStart = 25f
                                            )
                                        }

                                        Column(horizontalAlignment = CenterHorizontally) {
                                            Text(
                                                text = "N2",
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(4.dp)
                                            )
                                            M3Tile(
                                                color = sn2_10,
                                                colorName = "sn2_10",
                                                themedColor = C_10
                                            )
                                            M3Tile(
                                                color = sn2_50,
                                                colorName = "sn2_50",
                                                themedColor = C_50
                                            )
                                            M3Tile(
                                                color = sn2_100,
                                                colorName = "sn2_100",
                                                themedColor = C_100
                                            )
                                            M3Tile(
                                                color = sn2_200,
                                                colorName = "sn2_200",
                                                themedColor = C_200
                                            )
                                            M3Tile(
                                                color = sn2_300,
                                                colorName = "sn2_300",
                                                themedColor = C_300
                                            )
                                            M3Tile(
                                                color = sn2_400,
                                                colorName = "sn2_400",
                                                themedColor = C_400
                                            )
                                            M3Tile(
                                                color = sn2_500,
                                                colorName = "sn2_500",
                                                themedColor = C_500
                                            )
                                            M3Tile(
                                                color = sn2_600,
                                                colorName = "sn2_600",
                                                themedColor = C_600
                                            )
                                            M3Tile(
                                                color = sn2_700,
                                                colorName = "sn2_700",
                                                themedColor = C_700
                                            )
                                            M3Tile(
                                                color = sn2_800,
                                                colorName = "sn2_800",
                                                themedColor = C_800
                                            )
                                            M3Tile(
                                                color = sn2_900,
                                                colorName = "sn2_900",
                                                themedColor = C_900
                                            )

                                        }

                                        Column(horizontalAlignment = CenterHorizontally) {
                                            Text(
                                                text = "A1",
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(4.dp)
                                            )
                                            M3Tile(
                                                color = sa1_10,
                                                colorName = "sa1_10",
                                                themedColor = C_10
                                            )
                                            M3Tile(
                                                color = sa1_50,
                                                colorName = "sa1_50",
                                                themedColor = C_50
                                            )
                                            M3Tile(
                                                color = sa1_100,
                                                colorName = "sa1_100",
                                                themedColor = C_100
                                            )
                                            M3Tile(
                                                color = sa1_200,
                                                colorName = "sa1_200",
                                                themedColor = C_200
                                            )
                                            M3Tile(
                                                color = sa1_300,
                                                colorName = "sa1_300",
                                                themedColor = C_300
                                            )
                                            M3Tile(
                                                color = sa1_400,
                                                colorName = "sa1_400",
                                                themedColor = C_400
                                            )
                                            M3Tile(
                                                color = sa1_500,
                                                colorName = "sa1_500",
                                                themedColor = C_500
                                            )
                                            M3Tile(
                                                color = sa1_600,
                                                colorName = "sa1_600",
                                                themedColor = C_600
                                            )
                                            M3Tile(
                                                color = sa1_700,
                                                colorName = "sa1_700",
                                                themedColor = C_700
                                            )
                                            M3Tile(
                                                color = sa1_800,
                                                colorName = "sa1_800",
                                                themedColor = C_800
                                            )
                                            M3Tile(
                                                color = sa1_900,
                                                colorName = "sa1_900",
                                                themedColor = C_900
                                            )

                                        }

                                        Column(horizontalAlignment = CenterHorizontally) {
                                            Text(
                                                text = "A2",
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(4.dp)
                                            )
                                            M3Tile(
                                                color = sa2_10,
                                                colorName = "sa2_10",
                                                themedColor = C_10
                                            )
                                            M3Tile(
                                                color = sa2_50,
                                                colorName = "sa2_50",
                                                themedColor = C_50
                                            )
                                            M3Tile(
                                                color = sa2_100,
                                                colorName = "sa2_100",
                                                themedColor = C_100
                                            )
                                            M3Tile(
                                                color = sa2_200,
                                                colorName = "sa2_200",
                                                themedColor = C_200
                                            )
                                            M3Tile(
                                                color = sa2_300,
                                                colorName = "sa2_300",
                                                themedColor = C_300
                                            )
                                            M3Tile(
                                                color = sa2_400,
                                                colorName = "sa2_400",
                                                themedColor = C_400
                                            )
                                            M3Tile(
                                                color = sa2_500,
                                                colorName = "sa2_500",
                                                themedColor = C_500
                                            )
                                            M3Tile(
                                                color = sa2_600,
                                                colorName = "sa2_600",
                                                themedColor = C_600
                                            )
                                            M3Tile(
                                                color = sa2_700,
                                                colorName = "sa2_700",
                                                themedColor = C_700
                                            )
                                            M3Tile(
                                                color = sa2_800,
                                                colorName = "sa2_800",
                                                themedColor = C_800
                                            )
                                            M3Tile(
                                                color = sa2_900,
                                                colorName = "sa2_900",
                                                themedColor = C_900
                                            )

                                        }

                                        Column(horizontalAlignment = CenterHorizontally) {
                                            Text(
                                                text = "A3",
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(4.dp)
                                            )
                                            M3Tile(
                                                color = sa3_10,
                                                colorName = "sa3_10",
                                                themedColor = C_10,
                                                topEnd = 25f
                                            )
                                            M3Tile(
                                                color = sa3_50,
                                                colorName = "sa3_50",
                                                themedColor = C_50
                                            )
                                            M3Tile(
                                                color = sa3_100,
                                                colorName = "sa3_100",
                                                themedColor = C_100
                                            )
                                            M3Tile(
                                                color = sa3_200,
                                                colorName = "sa3_200",
                                                themedColor = C_200
                                            )
                                            M3Tile(
                                                color = sa3_300,
                                                colorName = "sa3_300",
                                                themedColor = C_300
                                            )
                                            M3Tile(
                                                color = sa3_400,
                                                colorName = "sa3_400",
                                                themedColor = C_400
                                            )
                                            M3Tile(
                                                color = sa3_500,
                                                colorName = "sa3_500",
                                                themedColor = C_500
                                            )
                                            M3Tile(
                                                color = sa3_600,
                                                colorName = "sa3_600",
                                                themedColor = C_600
                                            )
                                            M3Tile(
                                                color = sa3_700,
                                                colorName = "sa3_700",
                                                themedColor = C_700
                                            )
                                            M3Tile(
                                                color = sa3_800,
                                                colorName = "sa3_800",
                                                themedColor = C_800
                                            )
                                            M3Tile(
                                                color = sa3_900,
                                                colorName = "sa3_900",
                                                themedColor = C_900,
                                                bottomEnd = 25f
                                            )

                                        }

                                    }

                                } else {
                                    Column(
                                        Modifier
                                            .wrapContentWidth(unbounded = true)
                                            .clip(RoundedCornerShape(12.dp))
                                    ) {
                                        Row {
                                            M3Tile(
                                                color = sn1_10,
                                                colorName = "sn1_10",
                                                themedColor = C_10
                                            )
                                            M3Tile(
                                                color = sn1_50,
                                                colorName = "sn1_50",
                                                themedColor = C_50
                                            )
                                            M3Tile(
                                                color = sn1_100,
                                                colorName = "sn1_100",
                                                themedColor = C_100
                                            )
                                            M3Tile(
                                                color = sn1_200,
                                                colorName = "sn1_200",
                                                themedColor = C_200
                                            )
                                            M3Tile(
                                                color = sn1_300,
                                                colorName = "sn1_300",
                                                themedColor = C_300
                                            )
                                            M3Tile(
                                                color = sn1_400,
                                                colorName = "sn1_400",
                                                themedColor = C_400
                                            )
                                            M3Tile(
                                                color = sn1_500,
                                                colorName = "sn1_500",
                                                themedColor = C_500
                                            )
                                            M3Tile(
                                                color = sn1_600,
                                                colorName = "sn1_600",
                                                themedColor = C_600
                                            )
                                            M3Tile(
                                                color = sn1_700,
                                                colorName = "sn1_700",
                                                themedColor = C_700
                                            )
                                            M3Tile(
                                                color = sn1_800,
                                                colorName = "sn1_800",
                                                themedColor = C_800
                                            )
                                            M3Tile(
                                                color = sn1_900,
                                                colorName = "sn1_900",
                                                themedColor = C_900
                                            )

                                        }

                                        Row {
                                            M3Tile(
                                                color = sn2_10,
                                                colorName = "sn2_10",
                                                themedColor = C_10
                                            )
                                            M3Tile(
                                                color = sn2_50,
                                                colorName = "sn2_50",
                                                themedColor = C_50
                                            )
                                            M3Tile(
                                                color = sn2_100,
                                                colorName = "sn2_100",
                                                themedColor = C_100
                                            )
                                            M3Tile(
                                                color = sn2_200,
                                                colorName = "sn2_200",
                                                themedColor = C_200
                                            )
                                            M3Tile(
                                                color = sn2_300,
                                                colorName = "sn2_300",
                                                themedColor = C_300
                                            )
                                            M3Tile(
                                                color = sn2_400,
                                                colorName = "sn2_400",
                                                themedColor = C_400
                                            )
                                            M3Tile(
                                                color = sn2_500,
                                                colorName = "sn2_500",
                                                themedColor = C_500
                                            )
                                            M3Tile(
                                                color = sn2_600,
                                                colorName = "sn2_600",
                                                themedColor = C_600
                                            )
                                            M3Tile(
                                                color = sn2_700,
                                                colorName = "sn2_700",
                                                themedColor = C_700
                                            )
                                            M3Tile(
                                                color = sn2_800,
                                                colorName = "sn2_800",
                                                themedColor = C_800
                                            )
                                            M3Tile(
                                                color = sn2_900,
                                                colorName = "sn2_900",
                                                themedColor = C_900
                                            )
                                        }

                                        Row {
                                            M3Tile(
                                                color = sa1_10,
                                                colorName = "sa1_10",
                                                themedColor = C_10
                                            )
                                            M3Tile(
                                                color = sa1_50,
                                                colorName = "sa1_50",
                                                themedColor = C_50
                                            )
                                            M3Tile(
                                                color = sa1_100,
                                                colorName = "sa1_100",
                                                themedColor = C_100
                                            )
                                            M3Tile(
                                                color = sa1_200,
                                                colorName = "sa1_200",
                                                themedColor = C_200
                                            )
                                            M3Tile(
                                                color = sa1_300,
                                                colorName = "sa1_300",
                                                themedColor = C_300
                                            )
                                            M3Tile(
                                                color = sa1_400,
                                                colorName = "sa1_400",
                                                themedColor = C_400
                                            )
                                            M3Tile(
                                                color = sa1_500,
                                                colorName = "sa1_500",
                                                themedColor = C_500
                                            )
                                            M3Tile(
                                                color = sa1_600,
                                                colorName = "sa1_600",
                                                themedColor = C_600
                                            )
                                            M3Tile(
                                                color = sa1_700,
                                                colorName = "sa1_700",
                                                themedColor = C_700
                                            )
                                            M3Tile(
                                                color = sa1_800,
                                                colorName = "sa1_800",
                                                themedColor = C_800
                                            )
                                            M3Tile(
                                                color = sa1_900,
                                                colorName = "sa1_900",
                                                themedColor = C_900
                                            )
                                        }

                                        Row {
                                            M3Tile(
                                                color = sa2_10,
                                                colorName = "sa2_10",
                                                themedColor = C_10
                                            )
                                            M3Tile(
                                                color = sa2_50,
                                                colorName = "sa2_50",
                                                themedColor = C_50
                                            )
                                            M3Tile(
                                                color = sa2_100,
                                                colorName = "sa2_100",
                                                themedColor = C_100
                                            )
                                            M3Tile(
                                                color = sa2_200,
                                                colorName = "sa2_200",
                                                themedColor = C_200
                                            )
                                            M3Tile(
                                                color = sa2_300,
                                                colorName = "sa2_300",
                                                themedColor = C_300
                                            )
                                            M3Tile(
                                                color = sa2_400,
                                                colorName = "sa2_400",
                                                themedColor = C_400
                                            )
                                            M3Tile(
                                                color = sa2_500,
                                                colorName = "sa2_500",
                                                themedColor = C_500
                                            )
                                            M3Tile(
                                                color = sa2_600,
                                                colorName = "sa2_600",
                                                themedColor = C_600
                                            )
                                            M3Tile(
                                                color = sa2_700,
                                                colorName = "sa2_700",
                                                themedColor = C_700
                                            )
                                            M3Tile(
                                                color = sa2_800,
                                                colorName = "sa2_800",
                                                themedColor = C_800
                                            )
                                            M3Tile(
                                                color = sa2_900,
                                                colorName = "sa2_900",
                                                themedColor = C_900
                                            )
                                        }

                                        Row {
                                            M3Tile(
                                                color = sa3_10,
                                                colorName = "sa3_10",
                                                themedColor = C_10
                                            )
                                            M3Tile(
                                                color = sa3_50,
                                                colorName = "sa3_50",
                                                themedColor = C_50
                                            )
                                            M3Tile(
                                                color = sa3_100,
                                                colorName = "sa3_100",
                                                themedColor = C_100
                                            )
                                            M3Tile(
                                                color = sa3_200,
                                                colorName = "sa3_200",
                                                themedColor = C_200
                                            )
                                            M3Tile(
                                                color = sa3_300,
                                                colorName = "sa3_300",
                                                themedColor = C_300
                                            )
                                            M3Tile(
                                                color = sa3_400,
                                                colorName = "sa3_400",
                                                themedColor = C_400
                                            )
                                            M3Tile(
                                                color = sa3_500,
                                                colorName = "sa3_500",
                                                themedColor = C_500
                                            )
                                            M3Tile(
                                                color = sa3_600,
                                                colorName = "sa3_600",
                                                themedColor = C_600
                                            )
                                            M3Tile(
                                                color = sa3_700,
                                                colorName = "sa3_700",
                                                themedColor = C_700
                                            )
                                            M3Tile(
                                                color = sa3_800,
                                                colorName = "sa3_800",
                                                themedColor = C_800
                                            )
                                            M3Tile(
                                                color = sa3_900,
                                                colorName = "sa3_900",
                                                themedColor = C_900
                                            )
                                        }
                                    }
                                }
                            }
                            AnimatedVisibility(visible = !isColorReferenceDropdownExpanded) {
                                Column {
                                    Row {
                                        Button(modifier = Modifier
                                            .weight(1f)
                                            .padding(2.dp),
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sn1_500.toInt())
                                            ),
                                            contentPadding = PaddingValues(0.dp),
                                            onClick = {
                                                updateColor("system_neutral1_10", C_10.toArgb())
                                                updateColor("system_neutral1_50", C_50.toArgb())
                                                updateColor(
                                                    "system_neutral1_100", C_100.toArgb()
                                                )
                                                updateColor(
                                                    "system_neutral1_200", C_200.toArgb()
                                                )
                                                updateColor(
                                                    "system_neutral1_300", C_300.toArgb()
                                                )
                                                updateColor(
                                                    "system_neutral1_400", C_400.toArgb()
                                                )
                                                updateColor(
                                                    "system_neutral1_500", C_500.toArgb()
                                                )
                                                updateColor(
                                                    "system_neutral1_600", C_600.toArgb()
                                                )
                                                updateColor(
                                                    "system_neutral1_700", C_700.toArgb()
                                                )
                                                updateColor(
                                                    "system_neutral1_800", C_800.toArgb()
                                                )
                                                updateColor(
                                                    "system_neutral1_900", C_900.toArgb()
                                                )
                                                resetColorsXmlContent()
                                            }) {
                                            Text(text = "N1")
                                        }

                                        Button(modifier = Modifier
                                            .weight(1f)
                                            .padding(2.dp),
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sn2_500.toInt())
                                            ),
                                            contentPadding = PaddingValues(0.dp),
                                            onClick = {
                                                updateColor("system_neutral2_10", C_10.toArgb())
                                                updateColor("system_neutral2_50", C_50.toArgb())
                                                updateColor(
                                                    "system_neutral2_100", C_100.toArgb()
                                                )
                                                updateColor(
                                                    "system_neutral2_200", C_200.toArgb()
                                                )
                                                updateColor(
                                                    "system_neutral2_300", C_300.toArgb()
                                                )
                                                updateColor(
                                                    "system_neutral2_400", C_400.toArgb()
                                                )
                                                updateColor(
                                                    "system_neutral2_500", C_500.toArgb()
                                                )
                                                updateColor(
                                                    "system_neutral2_600", C_600.toArgb()
                                                )
                                                updateColor(
                                                    "system_neutral2_700", C_700.toArgb()
                                                )
                                                updateColor(
                                                    "system_neutral2_800", C_800.toArgb()
                                                )
                                                updateColor(
                                                    "system_neutral2_900", C_900.toArgb()
                                                )
                                                resetColorsXmlContent()

                                            }) {
                                            Text(text = "N2")
                                        }
                                        Button(modifier = Modifier
                                            .weight(1f)
                                            .padding(2.dp),
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sa1_500.toInt())
                                            ),
                                            contentPadding = PaddingValues(0.dp),
                                            onClick = {
                                                updateColor("system_accent1_10", C_10.toArgb())
                                                updateColor("system_accent1_50", C_50.toArgb())
                                                updateColor(
                                                    "system_accent1_100", C_100.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent1_200", C_200.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent1_300", C_300.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent1_400", C_400.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent1_500", C_500.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent1_600", C_600.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent1_700", C_700.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent1_800", C_800.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent1_900", C_900.toArgb()
                                                )
                                                resetColorsXmlContent()

                                            }) {
                                            Text(text = "A1")
                                        }
                                        Button(modifier = Modifier
                                            .weight(1f)
                                            .padding(2.dp),
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sa2_500.toInt())
                                            ),
                                            contentPadding = PaddingValues(0.dp),
                                            onClick = {
                                                updateColor("system_accent2_10", C_10.toArgb())
                                                updateColor("system_accent2_50", C_50.toArgb())
                                                updateColor(
                                                    "system_accent2_100", C_100.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent2_200", C_200.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent2_300", C_300.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent2_400", C_400.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent2_500", C_500.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent2_600", C_600.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent2_700", C_700.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent2_800", C_800.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent2_900", C_900.toArgb()
                                                )
                                                resetColorsXmlContent()

                                            }) {
                                            Text(text = "A2")
                                        }
                                        Button(modifier = Modifier
                                            .weight(1f)
                                            .padding(2.dp),
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sa3_500.toInt())
                                            ),
                                            contentPadding = PaddingValues(0.dp),
                                            onClick = {
                                                updateColor("system_accent3_10", C_10.toArgb())
                                                updateColor("system_accent3_50", C_50.toArgb())
                                                updateColor(
                                                    "system_accent3_100", C_100.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent3_200", C_200.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent3_300", C_300.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent3_400", C_400.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent3_500", C_500.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent3_600", C_600.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent3_700", C_700.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent3_800", C_800.toArgb()
                                                )
                                                updateColor(
                                                    "system_accent3_900", C_900.toArgb()
                                                )
                                                resetColorsXmlContent()

                                            }) {
                                            Text(text = "A3")
                                        }
                                    }
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        val colors = mutableListOf<Color>()
                                        for (h in 0..360 step 60) {
                                            colors.add(hsl(h.toFloat(), 1f, 0.5f))
                                        }
                                        Text(text = "hue is ${hue.toInt()}°")
                                        Slider(modifier = Modifier
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = colors
                                                ), shape = CircleShape
                                            )
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
                                                    imageVector = ImageVector.vectorResource(R.drawable.fiber_manual_record_48px),
                                                    contentDescription = null,
                                                )

                                            })
                                        Text(text = "saturation is ${saturation.toInt()}%")
                                        Slider(modifier = Modifier
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        hsl(hue, 0f, 0.5f),
                                                        hsl(hue, 0.1f, 0.5f),
                                                        hsl(hue, 0.2f, 0.5f),
                                                        hsl(hue, 0.3f, 0.5f),
                                                        hsl(hue, 0.4f, 0.5f),
                                                        hsl(hue, 0.5f, 0.5f),
                                                        hsl(hue, 0.6f, 0.5f),
                                                        hsl(hue, 0.7f, 0.5f),
                                                        hsl(hue, 0.8f, 0.5f),
                                                        hsl(hue, 0.9f, 0.5f),
                                                        hsl(hue, 1f, 0.5f)
                                                    )
                                                ), shape = CircleShape
                                            )
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
                                                    imageVector = ImageVector.vectorResource(R.drawable.fiber_manual_record_48px),
                                                    contentDescription = null,
                                                )
                                            })
                                        Text(text = "Lightness is +/-${lightness.toInt()}")
                                        Slider(modifier = Modifier
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        hsl(hue, saturation / 100f, 0.4f),
                                                        hsl(hue, saturation / 100f, 0.5f),
                                                        hsl(hue, saturation / 100f, 0.6f)
                                                    )
                                                ), shape = CircleShape
                                            )

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
                                                    imageVector = ImageVector.vectorResource(R.drawable.fiber_manual_record_48px),
                                                    contentDescription = null,
                                                )
                                            })

                                    }
                                }

                            }

                            AnimatedVisibility(visible = isColorReferenceDropdownExpanded) {
                                @Composable
                                fun ReferenceItem(
                                    colorName: String, colorReference: String, colorValue: Int
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                horizontal = 8.dp,
                                                vertical = if (colorName == selectedColorReference) 8.dp else 0.dp
                                            )
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (colorName == selectedColorReference) {
                                                    contentcol.copy(alpha = 0.32f)
                                                } else {
                                                    contentcol.copy(alpha = 0.12f)
                                                }
                                            )
                                            .clickable(onClick = {
                                                selectedColorReference = colorName
                                                selectedMonetColor = colorReference

                                            }), verticalAlignment = CenterVertically
                                    ) {
                                        Text(
                                            text = colorName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(8.dp)
                                        )
                                        Text(
                                            text = colorReference.replace("system_", "S")
                                                .replace("neutral", "N").replace("accent", "A"),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                shadow = Shadow(
                                                    color = if (colorValue.toColor()
                                                            .luminance() > 0.5f
                                                    ) {
                                                        White
                                                    } else {
                                                        Black
                                                    }, blurRadius = 16f
                                                )
                                            ),
                                            color = if (colorValue.toColor().luminance() > 0.5f) {
                                                Black
                                            } else {
                                                White
                                            },
                                            modifier = Modifier
                                                .wrapContentWidth(End)
                                                .clip(
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .background(
                                                    color = Color(
                                                        colorValue
                                                    )
                                                )
                                                .padding(8.dp)

                                        )
                                    }
                                }

                                val colorReferences = colorsXmlContent.map { it.trim() }
                                    .filter { it.contains("@color/") }.map {
                                        it.log()
                                        val colorName = it.substringAfter("<color name=\"")
                                            .substringBefore("\">@color/")
                                        colorName.log()
                                        val colorReference =
                                            it.substringAfter("@color/").substringBefore("</color>")
                                                .trim() // trim to remove leading and trailing spaces
                                        colorReference.log()
                                        val colorValue = getColorValue(colorReference)
                                        colorValue.log()
                                        Triple(colorName, colorReference, colorValue)
                                    }
                                Column {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        colorReferences.forEach { (colorName, colorReference, colorValue) ->
                                            item {
                                                ReferenceItem(
                                                    colorName, colorReference, colorValue
                                                )
                                            }
                                        }
                                    }

                                    Row {
                                        Button(modifier = Modifier
                                            .weight(1f)
                                            .padding(2.dp),
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sn1_500.toInt())
                                            ),
                                            contentPadding = PaddingValues(0.dp),
                                            onClick = {
                                                selectedMonetColor = selectedMonetColor.replace(
                                                    "neutral2", "neutral1"
                                                ).replace("accent1", "neutral1")
                                                    .replace("accent2", "neutral1")
                                                    .replace("accent3", "neutral1")
                                                resetColorsXmlContent()

                                            }) {
                                            Text(text = "N1")
                                        }

                                        Button(modifier = Modifier
                                            .weight(1f)
                                            .padding(2.dp),
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sn2_500.toInt())
                                            ),
                                            contentPadding = PaddingValues(0.dp),
                                            onClick = {
                                                selectedMonetColor = selectedMonetColor.replace(
                                                    "neutral1", "neutral2"
                                                ).replace("accent1", "neutral2")
                                                    .replace("accent2", "neutral2")
                                                    .replace("accent3", "neutral2")
                                                resetColorsXmlContent()
                                            }) {
                                            Text(text = "N2")
                                        }
                                        Button(modifier = Modifier
                                            .weight(1f)
                                            .padding(2.dp),
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sa1_500.toInt())
                                            ),
                                            contentPadding = PaddingValues(0.dp),
                                            onClick = {
                                                selectedMonetColor = selectedMonetColor.replace(
                                                    "neutral1", "accent1"
                                                ).replace("neutral2", "accent1")
                                                    .replace("accent2", "accent1")
                                                    .replace("accent3", "accent1")
                                                resetColorsXmlContent()
                                            }) {
                                            Text(text = "A1")
                                        }
                                        Button(modifier = Modifier
                                            .weight(1f)
                                            .padding(2.dp),
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sa2_500.toInt())
                                            ),
                                            contentPadding = PaddingValues(0.dp),
                                            onClick = {
                                                selectedMonetColor = selectedMonetColor.replace(
                                                    "neutral1", "accent2"
                                                ).replace("neutral2", "accent2")
                                                    .replace("accent1", "accent2")
                                                    .replace("accent3", "accent2")
                                                resetColorsXmlContent()
                                            }) {
                                            Text(text = "A2")
                                        }
                                        Button(modifier = Modifier
                                            .weight(1f)
                                            .padding(2.dp),
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sa3_500.toInt())
                                            ),
                                            contentPadding = PaddingValues(0.dp),
                                            onClick = {
                                                selectedMonetColor = selectedMonetColor.replace(
                                                    "neutral1", "accent3"
                                                ).replace("neutral2", "accent3")
                                                    .replace("accent1", "accent3")
                                                    .replace("accent2", "accent3")
                                                resetColorsXmlContent()
                                            }) {
                                            Text(text = "A3")
                                        }
                                    }
                                    FlowRow {
                                        Button(
                                            contentPadding = PaddingValues(0.dp), onClick = {
                                                selectedMonetColor = selectedMonetColor.replace(
                                                    "_\\d{1,4}".toRegex(), "_10"
                                                )
                                                resetColorsXmlContent()
                                            }, colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sa1_10.toInt())
                                            )
                                        ) {
                                            Text(text = "10")
                                        }
                                        Button(
                                            contentPadding = PaddingValues(0.dp), onClick = {
                                                selectedMonetColor = selectedMonetColor.replace(
                                                    "_\\d{1,4}".toRegex(), "_50"
                                                )
                                                resetColorsXmlContent()
                                            }, colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sa1_50.toInt())
                                            )
                                        ) {
                                            Text(text = "50")
                                        }
                                        Button(
                                            contentPadding = PaddingValues(0.dp), onClick = {
                                                selectedMonetColor = selectedMonetColor.replace(
                                                    "_\\d{1,4}".toRegex(), "_100"
                                                )
                                                resetColorsXmlContent()
                                            }, colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sa1_100.toInt())
                                            )
                                        ) {
                                            Text(text = "100")
                                        }
                                        Button(
                                            contentPadding = PaddingValues(0.dp), onClick = {
                                                selectedMonetColor = selectedMonetColor.replace(
                                                    "_\\d{1,4}".toRegex(), "_200"
                                                )
                                                resetColorsXmlContent()
                                            }, colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sa1_200.toInt())
                                            )
                                        ) {
                                            Text(text = "200")
                                        }
                                        Button(
                                            contentPadding = PaddingValues(0.dp), onClick = {
                                                selectedMonetColor = selectedMonetColor.replace(
                                                    "_\\d{1,4}".toRegex(), "_300"
                                                )
                                                resetColorsXmlContent()
                                            }, colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sa1_300.toInt())
                                            )
                                        ) {
                                            Text(text = "300")
                                        }
                                        Button(
                                            contentPadding = PaddingValues(0.dp), onClick = {
                                                selectedMonetColor = selectedMonetColor.replace(
                                                    "_\\d{1,4}".toRegex(), "_400"
                                                )
                                                resetColorsXmlContent()
                                            }, colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sa1_400.toInt())
                                            )
                                        ) {
                                            Text(text = "400")
                                        }
                                        Button(
                                            contentPadding = PaddingValues(0.dp), onClick = {
                                                selectedMonetColor = selectedMonetColor.replace(
                                                    "_\\d{1,4}".toRegex(), "_500"
                                                )
                                                resetColorsXmlContent()
                                            }, colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sa1_500.toInt())
                                            )
                                        ) {
                                            Text(text = "500")
                                        }
                                        Button(
                                            contentPadding = PaddingValues(0.dp), onClick = {
                                                selectedMonetColor = selectedMonetColor.replace(
                                                    "_\\d{1,4}".toRegex(), "_600"
                                                )
                                                resetColorsXmlContent()
                                            }, colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sa1_600.toInt())
                                            )
                                        ) {
                                            Text(text = "600")
                                        }
                                        Button(
                                            contentPadding = PaddingValues(0.dp), onClick = {
                                                selectedMonetColor = selectedMonetColor.replace(
                                                    "_\\d{1,4}".toRegex(), "_700"
                                                )
                                                resetColorsXmlContent()
                                            }, colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sa1_700.toInt())
                                            )
                                        ) {
                                            Text(text = "700")
                                        }
                                        Button(
                                            contentPadding = PaddingValues(0.dp), onClick = {
                                                selectedMonetColor = selectedMonetColor.replace(
                                                    "_\\d{1,4}".toRegex(), "_800"
                                                )
                                                resetColorsXmlContent()
                                            }, colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sa1_800.toInt())
                                            )
                                        ) {
                                            Text(text = "800")
                                        }
                                        Button(
                                            contentPadding = PaddingValues(0.dp), onClick = {
                                                selectedMonetColor = selectedMonetColor.replace(
                                                    "_\\d{1,4}".toRegex(), "_900"
                                                )
                                                resetColorsXmlContent()
                                            }, colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sa1_900.toInt())
                                            )
                                        ) {
                                            Text(text = "900")
                                        }
                                        Button(
                                            contentPadding = PaddingValues(0.dp), onClick = {
                                                selectedMonetColor = selectedMonetColor.replace(
                                                    "_\\d{1,4}".toRegex(), "_1000"
                                                )
                                                resetColorsXmlContent()
                                            }, colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(sa1_1000.toInt())
                                            )
                                        ) {
                                            Text(text = "1000")
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
                                Button(
                                    modifier = Modifier.fillMaxWidth(), onClick = {
                                        buildOverlay(colorsPath)
                                        Shell.SH.run("su -c cmd overlay disable com.android.systemui:accent")
                                        Shell.SH.run("su -c cmd overlay disable com.android.systemui:neutral")
                                        Shell.SH.run("su -c cmd overlay disable com.android.systemui:dynamic")
                                        overlayEnable("misc.flagmonet")

                                        colorsShell.run("""cmd overlay enable themed.fakemonet.generic""")
                                        showInterstitial(context) {}

                                    }, colors = ButtonDefaults.buttonColors(
                                        containerColor = C_500,
                                        contentColor = if ((lightness) > 50f) {
                                            Black
                                        } else {
                                            White
                                        }
                                    ), shape = CircleShape
                                ) {
                                    Row(verticalAlignment = CenterVertically) {
                                        Text(text = "Build and update")
                                        Icon(
                                            modifier = Modifier.height(24.dp),
                                            imageVector = ImageVector.vectorResource(id = R.drawable.arrow_right_alt_48px),
                                            contentDescription = ""
                                        )

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
}

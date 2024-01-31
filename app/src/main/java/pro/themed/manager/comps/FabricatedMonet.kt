package pro.themed.manager.comps

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.hsl
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.MainActivity
import pro.themed.manager.R
import pro.themed.manager.buildOverlay
import pro.themed.manager.getContrastColor
import pro.themed.manager.getOverlayList
import pro.themed.manager.log
import pro.themed.manager.overlayEnable
import pro.themed.manager.ui.theme.cardcol
import pro.themed.manager.utils.GlobalVariables
import pro.themed.manager.utils.showInterstitial

annotation class Composable

@Preview
@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
)
@Composable
fun FabricatedMonet(
) {
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = sharedPreferences.edit()

    var selectedColorReference by remember { mutableStateOf("") }
    var selectedMonetColor by remember { mutableStateOf("") }
    var isDark by remember { mutableStateOf(sharedPreferences.getString("isDark", "")) }
    var isColorReferenceDropdownExpanded by remember { mutableStateOf(false) }
    var isMonetDropdownExpanded by remember { mutableStateOf(false) }

    val colorsPath = """${GlobalVariables.modulePath}/onDemandCompiler/fakeMonet"""
    val colorsShell = Shell("su")
    colorsShell.run("cd $colorsPath")

    val scope = rememberCoroutineScope()
    var colorsXmlContent by remember { mutableStateOf(colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout) }

    LaunchedEffect(isDark) {
        colorsXmlContent =
            colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout
    }

    val atReferences =
        colorsXmlContent.filter { it.contains("<color name=") && it.contains("@") }.mapNotNull {
            val matchResult = Regex("""<color name="([^"]+)">([^<]+)</color>""").find(it)
            matchResult?.groupValues?.get(1)
        }

    val hashtagReferences =
        colorsXmlContent.filter { it.contains("<color name=") && it.contains("#") }.mapNotNull {
            val matchResult = Regex("""<color name="([^"]+)">([^<]+)</color>""").find(it)
            matchResult?.groupValues?.get(1)
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
    }

    fun getColorValue(colorName: String): Int? {
        return colorsXmlContent.find { it.contains("<color name=\"$colorName\">") }
            ?.substringAfter("#")?.substringBefore("</color>")
            ?.let { android.graphics.Color.parseColor("#$it") }
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

    val sn1_10: Int? = getColorValue("system_neutral1_10")
    val sn1_50: Int? = getColorValue("system_neutral1_50")
    val sn1_100: Int? = getColorValue("system_neutral1_100")
    val sn1_200: Int? = getColorValue("system_neutral1_200")
    val sn1_300: Int? = getColorValue("system_neutral1_300")
    val sn1_400: Int? = getColorValue("system_neutral1_400")
    val sn1_500: Int? = getColorValue("system_neutral1_500")
    val sn1_600: Int? = getColorValue("system_neutral1_600")
    val sn1_700: Int? = getColorValue("system_neutral1_700")
    val sn1_800: Int? = getColorValue("system_neutral1_800")
    val sn1_900: Int? = getColorValue("system_neutral1_900")

    val sn2_10: Int? = getColorValue("system_neutral2_10")
    val sn2_50: Int? = getColorValue("system_neutral2_50")
    val sn2_100: Int? = getColorValue("system_neutral2_100")
    val sn2_200: Int? = getColorValue("system_neutral2_200")
    val sn2_300: Int? = getColorValue("system_neutral2_300")
    val sn2_400: Int? = getColorValue("system_neutral2_400")
    val sn2_500: Int? = getColorValue("system_neutral2_500")
    val sn2_600: Int? = getColorValue("system_neutral2_600")
    val sn2_700: Int? = getColorValue("system_neutral2_700")
    val sn2_800: Int? = getColorValue("system_neutral2_800")
    val sn2_900: Int? = getColorValue("system_neutral2_900")

    val sa1_10: Int? = getColorValue("system_accent1_10")
    val sa1_50: Int? = getColorValue("system_accent1_50")
    val sa1_100: Int? = getColorValue("system_accent1_100")
    val sa1_200: Int? = getColorValue("system_accent1_200")
    val sa1_300: Int? = getColorValue("system_accent1_300")
    val sa1_400: Int? = getColorValue("system_accent1_400")
    val sa1_500: Int? = getColorValue("system_accent1_500")
    val sa1_600: Int? = getColorValue("system_accent1_600")
    val sa1_700: Int? = getColorValue("system_accent1_700")
    val sa1_800: Int? = getColorValue("system_accent1_800")
    val sa1_900: Int? = getColorValue("system_accent1_900")

    val sa2_10: Int? = getColorValue("system_accent2_10")
    val sa2_50: Int? = getColorValue("system_accent2_50")
    val sa2_100: Int? = getColorValue("system_accent2_100")
    val sa2_200: Int? = getColorValue("system_accent2_200")
    val sa2_300: Int? = getColorValue("system_accent2_300")
    val sa2_400: Int? = getColorValue("system_accent2_400")
    val sa2_500: Int? = getColorValue("system_accent2_500")
    val sa2_600: Int? = getColorValue("system_accent2_600")
    val sa2_700: Int? = getColorValue("system_accent2_700")
    val sa2_800: Int? = getColorValue("system_accent2_800")
    val sa2_900: Int? = getColorValue("system_accent2_900")

    val sa3_10: Int? = getColorValue("system_accent3_10")
    val sa3_50: Int? = getColorValue("system_accent3_50")
    val sa3_100: Int? = getColorValue("system_accent3_100")
    val sa3_200: Int? = getColorValue("system_accent3_200")
    val sa3_300: Int? = getColorValue("system_accent3_300")
    val sa3_400: Int? = getColorValue("system_accent3_400")
    val sa3_500: Int? = getColorValue("system_accent3_500")
    val sa3_600: Int? = getColorValue("system_accent3_600")
    val sa3_700: Int? = getColorValue("system_accent3_700")
    val sa3_800: Int? = getColorValue("system_accent3_800")
    val sa3_900: Int? = getColorValue("system_accent3_900")


    var stringhue by remember {
        mutableStateOf(
            sharedPreferences.getString(
                "hue", "0"
            ).toString()
        )
    }
    val hue = stringhue.toFloatOrNull()?.let {
        if (it >= 360) {
            0f.also { newValue ->
                stringhue = newValue.toString()
                editor.putString("hue", newValue.toString()).apply()
            }
        } else it
    } ?: 0f
    var stringsaturation by rememberSaveable {
        mutableStateOf(
            sharedPreferences.getString(
                "saturation", "100"
            ).toString()
        )
    }
    val saturation = stringsaturation.toFloatOrNull()?.let {
        if (it > 100) {
            100f.also { newValue ->
                stringsaturation = newValue.toString()
                editor.putString("saturation", newValue.toString()).apply()
            }
        } else it
    } ?: 100f
    var stringlightness by rememberSaveable {
        mutableStateOf(
            sharedPreferences.getString(
                "lightness", "0"
            ).toString()
        )
    }
    val lightness = stringlightness.toFloatOrNull()?.let {
        if (it >= 10) {
            10f.also { newValue ->
                stringlightness = newValue.toString()
                editor.putString("lightness", newValue.toString()).apply()
            }
        } else if (it <= -10) {
            (-10f).also { newValue ->
                stringlightness = newValue.toString()
                editor.putString("lightness", newValue.toString()).apply()
            }
        } else it
    } ?: 0f


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



    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 8.dp)
            .background(cardcol)
    ) {
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
                colorsShell.run("cmd overlay disable themed.fakemonet.generic ; pm uninstall themed.fakemonet.generic")
            }) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.reset),
                    contentDescription = null,
                )
            }
        }
        val configuration = LocalConfiguration.current
        val divisor = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            5
        } else {
            12
        }
        val tilesize = (((configuration.screenWidthDp - 8 - 64) / divisor)).dp
        tilesize.log()



        Surface(color = cardcol) {


            Column(
                modifier = Modifier
                    .imePadding()
                    .background(cardcol)
            ) {

                @Stable
                @Composable
                fun M3Tile(color: Int?, colorName: String, themedColor: Color) {
                    color?.let { Color(it) }?.let {
                        Surface(
                            modifier = Modifier
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
                                    colorsXmlContent =
                                        colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout


                                }, onLongClick = {
                                    Toast
                                        .makeText(
                                            MainActivity.appContext, "", Toast.LENGTH_SHORT
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
                            M3Tile(color = sn1_10, colorName = "sn1_10", themedColor = C_10)
                            M3Tile(color = sn1_50, colorName = "sn1_50", themedColor = C_50)
                            M3Tile(color = sn1_100, colorName = "sn1_100", themedColor = C_100)
                            M3Tile(color = sn1_200, colorName = "sn1_200", themedColor = C_200)
                            M3Tile(color = sn1_300, colorName = "sn1_300", themedColor = C_300)
                            M3Tile(color = sn1_400, colorName = "sn1_400", themedColor = C_400)
                            M3Tile(color = sn1_500, colorName = "sn1_500", themedColor = C_500)
                            M3Tile(color = sn1_600, colorName = "sn1_600", themedColor = C_600)
                            M3Tile(color = sn1_700, colorName = "sn1_700", themedColor = C_700)
                            M3Tile(color = sn1_800, colorName = "sn1_800", themedColor = C_800)
                            M3Tile(color = sn1_900, colorName = "sn1_900", themedColor = C_900)
                            Button(modifier = Modifier
                                .width(tilesize)
                                .padding(2.dp),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp),
                                onClick = {
                                    updateColor("system_neutral1_10", C_10.toArgb())
                                    updateColor("system_neutral1_50", C_50.toArgb())
                                    updateColor("system_neutral1_100", C_100.toArgb())
                                    updateColor("system_neutral1_200", C_200.toArgb())
                                    updateColor("system_neutral1_300", C_300.toArgb())
                                    updateColor("system_neutral1_400", C_400.toArgb())
                                    updateColor("system_neutral1_500", C_500.toArgb())
                                    updateColor("system_neutral1_600", C_600.toArgb())
                                    updateColor("system_neutral1_700", C_700.toArgb())
                                    updateColor("system_neutral1_800", C_800.toArgb())
                                    updateColor("system_neutral1_900", C_900.toArgb())
                                    colorsXmlContent =
                                        colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout
                                }) {
                                Text(text = "N1")
                            }
                        }

                        Column(horizontalAlignment = CenterHorizontally) {
                            Text(
                                text = "N2",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(4.dp)
                            )
                            M3Tile(color = sn2_10, colorName = "sn2_10", themedColor = C_10)
                            M3Tile(color = sn2_50, colorName = "sn2_50", themedColor = C_50)
                            M3Tile(color = sn2_100, colorName = "sn2_100", themedColor = C_100)
                            M3Tile(color = sn2_200, colorName = "sn2_200", themedColor = C_200)
                            M3Tile(color = sn2_300, colorName = "sn2_300", themedColor = C_300)
                            M3Tile(color = sn2_400, colorName = "sn2_400", themedColor = C_400)
                            M3Tile(color = sn2_500, colorName = "sn2_500", themedColor = C_500)
                            M3Tile(color = sn2_600, colorName = "sn2_600", themedColor = C_600)
                            M3Tile(color = sn2_700, colorName = "sn2_700", themedColor = C_700)
                            M3Tile(color = sn2_800, colorName = "sn2_800", themedColor = C_800)
                            M3Tile(color = sn2_900, colorName = "sn2_900", themedColor = C_900)
                            Button(modifier = Modifier
                                .width(tilesize)
                                .padding(2.dp),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp),
                                onClick = {
                                    updateColor("system_neutral2_10", C_10.toArgb())
                                    updateColor("system_neutral2_50", C_50.toArgb())
                                    updateColor("system_neutral2_100", C_100.toArgb())
                                    updateColor("system_neutral2_200", C_200.toArgb())
                                    updateColor("system_neutral2_300", C_300.toArgb())
                                    updateColor("system_neutral2_400", C_400.toArgb())
                                    updateColor("system_neutral2_500", C_500.toArgb())
                                    updateColor("system_neutral2_600", C_600.toArgb())
                                    updateColor("system_neutral2_700", C_700.toArgb())
                                    updateColor("system_neutral2_800", C_800.toArgb())
                                    updateColor("system_neutral2_900", C_900.toArgb())
                                    colorsXmlContent =
                                        colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout

                                }) {
                                Text(text = "N2")
                            }
                        }

                        Column(horizontalAlignment = CenterHorizontally) {
                            Text(
                                text = "A1",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(4.dp)
                            )
                            M3Tile(color = sa1_10, colorName = "sa1_10", themedColor = C_10)
                            M3Tile(color = sa1_50, colorName = "sa1_50", themedColor = C_50)
                            M3Tile(color = sa1_100, colorName = "sa1_100", themedColor = C_100)
                            M3Tile(color = sa1_200, colorName = "sa1_200", themedColor = C_200)
                            M3Tile(color = sa1_300, colorName = "sa1_300", themedColor = C_300)
                            M3Tile(color = sa1_400, colorName = "sa1_400", themedColor = C_400)
                            M3Tile(color = sa1_500, colorName = "sa1_500", themedColor = C_500)
                            M3Tile(color = sa1_600, colorName = "sa1_600", themedColor = C_600)
                            M3Tile(color = sa1_700, colorName = "sa1_700", themedColor = C_700)
                            M3Tile(color = sa1_800, colorName = "sa1_800", themedColor = C_800)
                            M3Tile(color = sa1_900, colorName = "sa1_900", themedColor = C_900)
                            Button(modifier = Modifier
                                .width(tilesize)
                                .padding(2.dp),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp),
                                onClick = {
                                    updateColor("system_accent1_10", C_10.toArgb())
                                    updateColor("system_accent1_50", C_50.toArgb())
                                    updateColor("system_accent1_100", C_100.toArgb())
                                    updateColor("system_accent1_200", C_200.toArgb())
                                    updateColor("system_accent1_300", C_300.toArgb())
                                    updateColor("system_accent1_400", C_400.toArgb())
                                    updateColor("system_accent1_500", C_500.toArgb())
                                    updateColor("system_accent1_600", C_600.toArgb())
                                    updateColor("system_accent1_700", C_700.toArgb())
                                    updateColor("system_accent1_800", C_800.toArgb())
                                    updateColor("system_accent1_900", C_900.toArgb())
                                    colorsXmlContent =
                                        colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout

                                }) {
                                Text(text = "A1")
                            }
                        }

                        Column(horizontalAlignment = CenterHorizontally) {
                            Text(
                                text = "A2",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(4.dp)
                            )
                            M3Tile(color = sa2_10, colorName = "sa2_10", themedColor = C_10)
                            M3Tile(color = sa2_50, colorName = "sa2_50", themedColor = C_50)
                            M3Tile(color = sa2_100, colorName = "sa2_100", themedColor = C_100)
                            M3Tile(color = sa2_200, colorName = "sa2_200", themedColor = C_200)
                            M3Tile(color = sa2_300, colorName = "sa2_300", themedColor = C_300)
                            M3Tile(color = sa2_400, colorName = "sa2_400", themedColor = C_400)
                            M3Tile(color = sa2_500, colorName = "sa2_500", themedColor = C_500)
                            M3Tile(color = sa2_600, colorName = "sa2_600", themedColor = C_600)
                            M3Tile(color = sa2_700, colorName = "sa2_700", themedColor = C_700)
                            M3Tile(color = sa2_800, colorName = "sa2_800", themedColor = C_800)
                            M3Tile(color = sa2_900, colorName = "sa2_900", themedColor = C_900)
                            Button(modifier = Modifier
                                .width(tilesize)
                                .padding(2.dp),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp),
                                onClick = {
                                    updateColor("system_accent2_10", C_10.toArgb())
                                    updateColor("system_accent2_50", C_50.toArgb())
                                    updateColor("system_accent2_100", C_100.toArgb())
                                    updateColor("system_accent2_200", C_200.toArgb())
                                    updateColor("system_accent2_300", C_300.toArgb())
                                    updateColor("system_accent2_400", C_400.toArgb())
                                    updateColor("system_accent2_500", C_500.toArgb())
                                    updateColor("system_accent2_600", C_600.toArgb())
                                    updateColor("system_accent2_700", C_700.toArgb())
                                    updateColor("system_accent2_800", C_800.toArgb())
                                    updateColor("system_accent2_900", C_900.toArgb())
                                    colorsXmlContent =
                                        colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout

                                }) {
                                Text(text = "A2")
                            }
                        }

                        Column(horizontalAlignment = CenterHorizontally) {
                            Text(
                                text = "A3",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(4.dp)
                            )
                            M3Tile(color = sa3_10, colorName = "sa3_10", themedColor = C_10)
                            M3Tile(color = sa3_50, colorName = "sa3_50", themedColor = C_50)
                            M3Tile(color = sa3_100, colorName = "sa3_100", themedColor = C_100)
                            M3Tile(color = sa3_200, colorName = "sa3_200", themedColor = C_200)
                            M3Tile(color = sa3_300, colorName = "sa3_300", themedColor = C_300)
                            M3Tile(color = sa3_400, colorName = "sa3_400", themedColor = C_400)
                            M3Tile(color = sa3_500, colorName = "sa3_500", themedColor = C_500)
                            M3Tile(color = sa3_600, colorName = "sa3_600", themedColor = C_600)
                            M3Tile(color = sa3_700, colorName = "sa3_700", themedColor = C_700)
                            M3Tile(color = sa3_800, colorName = "sa3_800", themedColor = C_800)
                            M3Tile(color = sa3_900, colorName = "sa3_900", themedColor = C_900)
                            Button(modifier = Modifier
                                .width(tilesize)
                                .padding(2.dp),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp),
                                onClick = {
                                    updateColor("system_accent3_10", C_10.toArgb())
                                    updateColor("system_accent3_50", C_50.toArgb())
                                    updateColor("system_accent3_100", C_100.toArgb())
                                    updateColor("system_accent3_200", C_200.toArgb())
                                    updateColor("system_accent3_300", C_300.toArgb())
                                    updateColor("system_accent3_400", C_400.toArgb())
                                    updateColor("system_accent3_500", C_500.toArgb())
                                    updateColor("system_accent3_600", C_600.toArgb())
                                    updateColor("system_accent3_700", C_700.toArgb())
                                    updateColor("system_accent3_800", C_800.toArgb())
                                    updateColor("system_accent3_900", C_900.toArgb())
                                    colorsXmlContent =
                                        colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout

                                }) {
                                Text(text = "A3")
                            }
                        }

                    }

                } else {
                    Column(
                        Modifier
                            .wrapContentWidth(unbounded = true)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Row {

                            M3Tile(color = sn1_10, colorName = "sn1_10", themedColor = C_10)
                            M3Tile(color = sn1_50, colorName = "sn1_50", themedColor = C_50)
                            M3Tile(color = sn1_100, colorName = "sn1_100", themedColor = C_100)
                            M3Tile(color = sn1_200, colorName = "sn1_200", themedColor = C_200)
                            M3Tile(color = sn1_300, colorName = "sn1_300", themedColor = C_300)
                            M3Tile(color = sn1_400, colorName = "sn1_400", themedColor = C_400)
                            M3Tile(color = sn1_500, colorName = "sn1_500", themedColor = C_500)
                            M3Tile(color = sn1_600, colorName = "sn1_600", themedColor = C_600)
                            M3Tile(color = sn1_700, colorName = "sn1_700", themedColor = C_700)
                            M3Tile(color = sn1_800, colorName = "sn1_800", themedColor = C_800)
                            M3Tile(color = sn1_900, colorName = "sn1_900", themedColor = C_900)
                            Text(textAlign = TextAlign.Center,
                                text = "N1",
                                modifier = Modifier
                                    .width(tilesize)
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(C_500)
                                    .clickable {
                                        updateColor("system_neutral1_10", C_10.toArgb())
                                        updateColor("system_neutral1_50", C_50.toArgb())
                                        updateColor("system_neutral1_100", C_100.toArgb())
                                        updateColor("system_neutral1_200", C_200.toArgb())
                                        updateColor("system_neutral1_300", C_300.toArgb())
                                        updateColor("system_neutral1_400", C_400.toArgb())
                                        updateColor("system_neutral1_500", C_500.toArgb())
                                        updateColor("system_neutral1_600", C_600.toArgb())
                                        updateColor("system_neutral1_700", C_700.toArgb())
                                        updateColor("system_neutral1_800", C_800.toArgb())
                                        updateColor("system_neutral1_900", C_900.toArgb())
                                        colorsXmlContent =
                                            colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout
                                    })


                        }

                        Row {

                            M3Tile(color = sn2_10, colorName = "sn2_10", themedColor = C_10)
                            M3Tile(color = sn2_50, colorName = "sn2_50", themedColor = C_50)
                            M3Tile(color = sn2_100, colorName = "sn2_100", themedColor = C_100)
                            M3Tile(color = sn2_200, colorName = "sn2_200", themedColor = C_200)
                            M3Tile(color = sn2_300, colorName = "sn2_300", themedColor = C_300)
                            M3Tile(color = sn2_400, colorName = "sn2_400", themedColor = C_400)
                            M3Tile(color = sn2_500, colorName = "sn2_500", themedColor = C_500)
                            M3Tile(color = sn2_600, colorName = "sn2_600", themedColor = C_600)
                            M3Tile(color = sn2_700, colorName = "sn2_700", themedColor = C_700)
                            M3Tile(color = sn2_800, colorName = "sn2_800", themedColor = C_800)
                            M3Tile(color = sn2_900, colorName = "sn2_900", themedColor = C_900)
                            Text(textAlign = TextAlign.Center,
                                text = "N2",
                                modifier = Modifier
                                    .width(tilesize)
                                    .padding(2.dp)
                                    .clickable {
                                        updateColor("system_neutral2_10", C_10.toArgb())
                                        updateColor("system_neutral2_50", C_50.toArgb())
                                        updateColor("system_neutral2_100", C_100.toArgb())
                                        updateColor("system_neutral2_200", C_200.toArgb())
                                        updateColor("system_neutral2_300", C_300.toArgb())
                                        updateColor("system_neutral2_400", C_400.toArgb())
                                        updateColor("system_neutral2_500", C_500.toArgb())
                                        updateColor("system_neutral2_600", C_600.toArgb())
                                        updateColor("system_neutral2_700", C_700.toArgb())
                                        updateColor("system_neutral2_800", C_800.toArgb())
                                        updateColor("system_neutral2_900", C_900.toArgb())
                                        colorsXmlContent =
                                            colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout
                                    })
                        }

                        Row {

                            M3Tile(color = sa1_10, colorName = "sa1_10", themedColor = C_10)
                            M3Tile(color = sa1_50, colorName = "sa1_50", themedColor = C_50)
                            M3Tile(color = sa1_100, colorName = "sa1_100", themedColor = C_100)
                            M3Tile(color = sa1_200, colorName = "sa1_200", themedColor = C_200)
                            M3Tile(color = sa1_300, colorName = "sa1_300", themedColor = C_300)
                            M3Tile(color = sa1_400, colorName = "sa1_400", themedColor = C_400)
                            M3Tile(color = sa1_500, colorName = "sa1_500", themedColor = C_500)
                            M3Tile(color = sa1_600, colorName = "sa1_600", themedColor = C_600)
                            M3Tile(color = sa1_700, colorName = "sa1_700", themedColor = C_700)
                            M3Tile(color = sa1_800, colorName = "sa1_800", themedColor = C_800)
                            M3Tile(color = sa1_900, colorName = "sa1_900", themedColor = C_900)
                            Text(textAlign = TextAlign.Center,
                                text = "A1",
                                modifier = Modifier
                                    .width(tilesize)
                                    .padding(2.dp)
                                    .clickable {
                                        updateColor("system_accent1_10", C_10.toArgb())
                                        updateColor("system_accent1_50", C_50.toArgb())
                                        updateColor("system_accent1_100", C_100.toArgb())
                                        updateColor("system_accent1_200", C_200.toArgb())
                                        updateColor("system_accent1_300", C_300.toArgb())
                                        updateColor("system_accent1_400", C_400.toArgb())
                                        updateColor("system_accent1_500", C_500.toArgb())
                                        updateColor("system_accent1_600", C_600.toArgb())
                                        updateColor("system_accent1_700", C_700.toArgb())
                                        updateColor("system_accent1_800", C_800.toArgb())
                                        updateColor("system_accent1_900", C_900.toArgb())
                                        colorsXmlContent =
                                            colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout
                                    })
                        }

                        Row {

                            M3Tile(color = sa2_10, colorName = "sa2_10", themedColor = C_10)
                            M3Tile(color = sa2_50, colorName = "sa2_50", themedColor = C_50)
                            M3Tile(color = sa2_100, colorName = "sa2_100", themedColor = C_100)
                            M3Tile(color = sa2_200, colorName = "sa2_200", themedColor = C_200)
                            M3Tile(color = sa2_300, colorName = "sa2_300", themedColor = C_300)
                            M3Tile(color = sa2_400, colorName = "sa2_400", themedColor = C_400)
                            M3Tile(color = sa2_500, colorName = "sa2_500", themedColor = C_500)
                            M3Tile(color = sa2_600, colorName = "sa2_600", themedColor = C_600)
                            M3Tile(color = sa2_700, colorName = "sa2_700", themedColor = C_700)
                            M3Tile(color = sa2_800, colorName = "sa2_800", themedColor = C_800)
                            M3Tile(color = sa2_900, colorName = "sa2_900", themedColor = C_900)
                            Text(textAlign = TextAlign.Center,
                                text = "A2",
                                modifier = Modifier
                                    .width(tilesize)
                                    .padding(2.dp)
                                    .clickable {
                                        updateColor("system_accent2_10", C_10.toArgb())
                                        updateColor("system_accent2_50", C_50.toArgb())
                                        updateColor("system_accent2_100", C_100.toArgb())
                                        updateColor("system_accent2_200", C_200.toArgb())
                                        updateColor("system_accent2_300", C_300.toArgb())
                                        updateColor("system_accent2_400", C_400.toArgb())
                                        updateColor("system_accent2_500", C_500.toArgb())
                                        updateColor("system_accent2_600", C_600.toArgb())
                                        updateColor("system_accent2_700", C_700.toArgb())
                                        updateColor("system_accent2_800", C_800.toArgb())
                                        updateColor("system_accent2_900", C_900.toArgb())
                                        colorsXmlContent =
                                            colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout
                                    })
                        }

                        Row {

                            M3Tile(color = sa3_10, colorName = "sa3_10", themedColor = C_10)
                            M3Tile(color = sa3_50, colorName = "sa3_50", themedColor = C_50)
                            M3Tile(color = sa3_100, colorName = "sa3_100", themedColor = C_100)
                            M3Tile(color = sa3_200, colorName = "sa3_200", themedColor = C_200)
                            M3Tile(color = sa3_300, colorName = "sa3_300", themedColor = C_300)
                            M3Tile(color = sa3_400, colorName = "sa3_400", themedColor = C_400)
                            M3Tile(color = sa3_500, colorName = "sa3_500", themedColor = C_500)
                            M3Tile(color = sa3_600, colorName = "sa3_600", themedColor = C_600)
                            M3Tile(color = sa3_700, colorName = "sa3_700", themedColor = C_700)
                            M3Tile(color = sa3_800, colorName = "sa3_800", themedColor = C_800)
                            M3Tile(color = sa3_900, colorName = "sa3_900", themedColor = C_900)
                            Text(textAlign = TextAlign.Center,
                                text = "A3",
                                modifier = Modifier
                                    .width(tilesize)
                                    .padding(2.dp)
                                    .clickable {
                                        updateColor("system_accent3_10", C_10.toArgb())
                                        updateColor("system_accent3_50", C_50.toArgb())
                                        updateColor("system_accent3_100", C_100.toArgb())
                                        updateColor("system_accent3_200", C_200.toArgb())
                                        updateColor("system_accent3_300", C_300.toArgb())
                                        updateColor("system_accent3_400", C_400.toArgb())
                                        updateColor("system_accent3_500", C_500.toArgb())
                                        updateColor("system_accent3_600", C_600.toArgb())
                                        updateColor("system_accent3_700", C_700.toArgb())
                                        updateColor("system_accent3_800", C_800.toArgb())
                                        updateColor("system_accent3_900", C_900.toArgb())
                                        colorsXmlContent =
                                            colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout
                                    })
                        }
                    }
                }



                Row {
                    OutlinedTextField(modifier = Modifier.weight(1f),
                        value = stringhue,
                        singleLine = true,
                        onValueChange = {
                            stringhue = it; editor.putString("hue", it).apply()

                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("H") })
                    Spacer(Modifier.width(8.dp))

                    OutlinedTextField(modifier = Modifier.weight(1f),
                        value = stringsaturation,
                        singleLine = true,
                        onValueChange = {
                            stringsaturation = it; editor.putString("saturation", it).apply()
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("S") })
                    Spacer(Modifier.width(8.dp))

                    OutlinedTextField(modifier = Modifier.weight(1f),
                        value = stringlightness,
                        singleLine = true,
                        onValueChange = {
                            stringlightness = it; editor.putString("lightness", it).apply()
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("L") })

                }


                Column(
                    modifier = Modifier.padding(0.dp)
                ) {
                    // ExposedDropdownMenu for selecting color reference
                    ExposedDropdownMenuBox(
                        expanded = isColorReferenceDropdownExpanded,
                        onExpandedChange = {
                            isColorReferenceDropdownExpanded = !isColorReferenceDropdownExpanded
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .pointerInput(Unit) { detectTapGestures {} },
                            value = selectedColorReference,
                            onValueChange = { selectedColorReference = it },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.None),
                            label = { Text("Select Color Reference") },
                            singleLine = true
                        )
                        ExposedDropdownMenu(
                            expanded = isColorReferenceDropdownExpanded,
                            onDismissRequest = {
                                // Dismiss the menu if needed
                                isColorReferenceDropdownExpanded = false
                            }) {
                            atReferences.forEach { reference ->
                                DropdownMenuItem(text = {
                                    Text(text = reference)
                                }, onClick = {
                                    selectedColorReference = reference
                                    isColorReferenceDropdownExpanded = false

                                })
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = isMonetDropdownExpanded,
                        onExpandedChange = {
                            isMonetDropdownExpanded = !isMonetDropdownExpanded
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .pointerInput(Unit) { detectTapGestures {} },
                            value = selectedMonetColor,
                            onValueChange = { selectedMonetColor = it },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.None),
                            label = { Text("Select Monet Color") },
                            singleLine = true
                        )
                        ExposedDropdownMenu(expanded = isMonetDropdownExpanded, onDismissRequest = {
                            // Dismiss the menu if needed
                            isMonetDropdownExpanded = false
                        }) {
                            hashtagReferences.forEach { color ->
                                DropdownMenuItem(text = {
                                    Text(text = color)
                                }, onClick = {
                                    selectedMonetColor = color
                                    isMonetDropdownExpanded = false

                                })
                            }
                        }
                    }
                }


                HeaderRow(
                    header = "Disable Monet",
                    subHeader = "Ye you need to enable this first, duh",
                    isChecked = getOverlayList().enabledOverlays.any { it.contains("flagmonet") },
                    onCheckedChange = {
                        if (it) {
                            Shell.SH.run("su -c cmd overlay disable com.android.systemui:accent")
                            Shell.SH.run("su -c cmd overlay disable com.android.systemui:neutral")
                            Shell.SH.run("su -c cmd overlay disable com.android.systemui:dynamic")
                            overlayEnable("misc.flagmonet")

                        } else {
                            Shell.SH.run("su -c cmd overlay enable com.android.systemui:accent")
                            Shell.SH.run("su -c cmd overlay enable com.android.systemui:neutral")
                            Shell.SH.run("su -c cmd overlay enable com.android.systemui:dynamic")
                            Shell.SH.run("su -c cmd overlay disable themed.misc.flagmonet")

                        }
                    },
                    showSwitch = true
                )

                HeaderRow(
                    header = "Override colors for dark theme",
                    showSwitch = true,
                    isChecked = sharedPreferences.getString("isDark", "") == "-night",
                    onCheckedChange = {
                        if (it) {
                            editor.putString("isDark", "-night").apply()

                        } else {
                            editor.putString("isDark", "").apply()
                        }
                        colorsXmlContent =
                            colorsShell.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout
                        isDark = sharedPreferences.getString("isDark", "")
                    },
                )

                Button(
                    modifier = Modifier.fillMaxWidth(), onClick = {
                        buildOverlay(colorsPath)
                        colorsShell.run("""cmd overlay enable themed.fakemonet.generic""")
                        showInterstitial(context) {}


                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = C_500, contentColor = if (lightness > 50f) {
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
                            imageVector = ImageVector.vectorResource(id = R.drawable.arrow_right_alt_48px),
                            contentDescription = ""
                        )

                    }
                }
                Spacer(modifier = Modifier.height(8.dp))


            }
        }
    }
}

package pro.themed.manager.comps

import android.content.*
import android.widget.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.material.Button
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color.Companion.hsl
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.jaredrummler.ktsh.*
import log
import pro.themed.manager.*
import pro.themed.manager.R
import pro.themed.manager.ui.theme.*
import pro.themed.manager.utils.*

annotation class Composable

@Preview
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class
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

    var isDark by rememberSaveable {
        mutableStateOf("")
    }

    var isColorReferenceDropdownExpanded by remember { mutableStateOf(false) }
    var isMonetDropdownExpanded by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    var colorsXmlContent by remember { mutableStateOf(Shell.SU.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout) }


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


// First launched effect for selectedColorReference
    LaunchedEffect(selectedColorReference) {
        val colorValue =
            colorsXmlContent.find { it.contains("<color name=\"$selectedColorReference\">") }
                ?.substringAfter("@color/")?.substringBefore("</color>") ?: ""

        selectedMonetColor = colorValue
    }

// Second launched effect for selectedMonetColor
    LaunchedEffect(selectedMonetColor) {
        val sedCommand = """
    sed -i 's|<color name="$selectedColorReference">@color/[^<]*</color>|<color name="$selectedColorReference">@color/$selectedMonetColor</color>|' /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml
""".trimIndent()

        Shell.SU.run(sedCommand).log()
    }




    fun getColorValue(colorName: String): Int? {
        return colorsXmlContent.find { it.contains("<color name=\"$colorName\">") }
            ?.substringAfter("#")?.substringBefore("</color>")
            ?.let { android.graphics.Color.parseColor("#$it") }
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
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp)
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
                    Shell.SU.run("cmd overlay disable themed.fakemonet.generic ; pm uninstall themed.fakemonet.generic")
                }) {
                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.reset),
                        contentDescription = null,
                    )
                }
            }
            val tilesize = (((LocalConfiguration.current.smallestScreenWidthDp - 16 - 64) / 5)).dp


            listOf(
                "10", "50", "100", "200", "300", "400", "500", "600", "700", "800", "900"
            )

            Surface {


                Column {


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

                                        Shell.SU.run("cd ${GlobalVariables.modulePath}/onDemandCompiler/fakeMonet")

                                        Shell.SU.run(
                                            """sed -i '/$fullName">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#$hex</g' res/values$isDark/colors.xml"""
                                        )
                                        colorsXmlContent =
                                            Shell.SU.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout


                                    }, onLongClick = {
                                        Toast
                                            .makeText(
                                                MyApplication.appContext, "", Toast.LENGTH_SHORT
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

                        }

                    }

                    fun updateColor(colorName: String, colorValue: Int?) {
                        Shell.SU.run("cd ${GlobalVariables.modulePath}/onDemandCompiler/fakeMonet")
                        Shell.SU.run(
                            """sed -i '/$colorName">/ s/>#\([0-9A-Fa-f]\{8\}\)</>#${
                                "%08x".format(
                                    colorValue ?: 0
                                )
                            }</g' res/values$isDark/colors.xml"""
                        )


                    }

                    Row(modifier = Modifier.padding(horizontal = 1.dp)) {
                        OutlinedButton(modifier = Modifier.weight(1f),
                            shape = CircleShape,
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
                                    Shell.SU.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout
                            }) {
                            Text(text = "N1")

                        }
                        Spacer(modifier = Modifier.width(2.dp))

                        Button(modifier = Modifier.weight(1f), shape = CircleShape, onClick = {
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
                                Shell.SU.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout

                        }) {
                            Text(text = "N2")
                        }
                        Spacer(modifier = Modifier.width(2.dp))


                        Button(modifier = Modifier.weight(1f), shape = CircleShape, onClick = {
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
                                Shell.SU.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout

                        }) {
                            Text(text = "A1")
                        }
                        Spacer(modifier = Modifier.width(2.dp))

                        Button(modifier = Modifier.weight(1f), shape = CircleShape, onClick = {
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
                                Shell.SU.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout

                        }) {
                            Text(text = "A2")
                        }
                        Spacer(modifier = Modifier.width(2.dp))

                        Button(modifier = Modifier.weight(1f), shape = CircleShape, onClick = {
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
                                Shell.SU.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout

                        }) {
                            Text(text = "A3")
                        }

                    }
                    Row {
                        OutlinedTextField(modifier = Modifier.weight(1f),
                            value = hue.toString(),
                            singleLine = true,
                            onValueChange = {
                                val parsedValue = it.toFloatOrNull()
                                if (parsedValue != null) {
                                    // Clamp the value within the range [0, 360]
                                    hue = parsedValue.coerceIn(0f, 360f)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("H") })
                        Spacer(Modifier.width(8.dp))

                        OutlinedTextField(modifier = Modifier.weight(1f),
                            value = saturation.toString(),
                            singleLine = true,
                            onValueChange = {
                                val parsedValue = it.toFloatOrNull()
                                if (parsedValue != null) {
                                    // Clamp the value within the range [0, 360]
                                    saturation = parsedValue.coerceIn(0f, 100f)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("S") })
                        Spacer(Modifier.width(8.dp))

                        OutlinedTextField(modifier = Modifier.weight(1f),
                            value = lightness.toString(),
                            singleLine = true,
                            onValueChange = {
                                val parsedValue = it.toFloatOrNull()
                                if (parsedValue != null) {
                                    // Clamp the value within the range [0, 360]
                                    lightness = parsedValue.coerceIn(-10f, 10f)
                                }
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
                                modifier = Modifier.fillMaxWidth(),
                                value = selectedColorReference,
                                onValueChange = { selectedColorReference = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                label = { Text("Select Color Reference") },
                                singleLine = true
                            )
                            ExposedDropdownMenu(expanded = isColorReferenceDropdownExpanded,
                                onDismissRequest = {
                                    // Dismiss the menu if needed
                                    isColorReferenceDropdownExpanded = false
                                }) {
                                atReferences.forEach { reference ->
                                    DropdownMenuItem(onClick = {
                                        selectedColorReference = reference
                                        isColorReferenceDropdownExpanded = false

                                    }) {
                                        Text(text = reference)
                                    }
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
                                modifier = Modifier.fillMaxWidth(),
                                value = selectedMonetColor,
                                onValueChange = { selectedMonetColor = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                label = { Text("Select Monet Color") },
                                singleLine = true
                            )
                            ExposedDropdownMenu(expanded = isMonetDropdownExpanded,
                                onDismissRequest = {
                                    // Dismiss the menu if needed
                                    isMonetDropdownExpanded = false
                                }) {
                                hashtagReferences.forEach { color ->
                                    DropdownMenuItem(onClick = {
                                        selectedMonetColor = color
                                        isMonetDropdownExpanded = false

                                    }) {
                                        Text(text = color)
                                    }
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
                        isChecked = sharedPreferences.getBoolean("accents_dark", false),
                        onCheckedChange = {
                            if (it) {
                                isDark = "-night"
                            } else {
                                isDark = ""
                            }
                            colorsXmlContent =
                                Shell.SU.run("cat /data/adb/modules/ThemedProject/onDemandCompiler/fakeMonet/res/values$isDark/colors.xml").stdout

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
}
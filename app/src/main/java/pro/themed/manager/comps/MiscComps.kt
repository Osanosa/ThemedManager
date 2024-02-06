package pro.themed.manager.comps

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pro.themed.manager.AdmobBanner
import pro.themed.manager.R
import pro.themed.manager.buildOverlay
import pro.themed.manager.getOverlayList
import pro.themed.manager.log
import pro.themed.manager.overlayEnable
import pro.themed.manager.ui.theme.cardcol
import pro.themed.manager.utils.GlobalVariables
import kotlin.math.roundToInt

@Stable
@ExperimentalMaterial3Api
@Composable
fun Slideritem(
    drawable: Int,
    header: String,
    sliderSteps: Int,
    sliderStepValue: Int,
    minSliderValue: Float,
    maxSliderValue: Float,
    overlayName: String
) {
    val context = LocalContext.current
    var sliderPosition by rememberSaveable { mutableFloatStateOf(0f) }
    var intvalue by rememberSaveable { mutableIntStateOf(sliderPosition.roundToInt()) }


    sliderPosition = sliderPosition.coerceIn(minSliderValue, maxSliderValue)
    intvalue = intvalue.coerceIn(minSliderValue.toInt(), maxSliderValue.toInt())
    if (getOverlayList().overlayList.any { it.contains(overlayName) } && !getOverlayList().unsupportedOverlays.any {
            it.contains(
                overlayName
            )
        }) {
        Surface {


            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    // horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(id = drawable),
                        contentDescription = "",
                        Modifier
                            .padding(horizontal = 16.dp)
                            .size(24.dp)
                    )
                    Column {
                        Text(
                            text = header, fontWeight = FontWeight.SemiBold, fontSize = 16.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Text(text = "Value: $intvalue")

                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(modifier = Modifier, onClick = {
                        intvalue = 0; sliderPosition = intvalue.toFloat()
                        Shell("su").run("for ol in \$(cmd overlay list | grep -E 'themed.$overlayName' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
                    }) {
                        Image(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .size(24.dp),
                            imageVector = ImageVector.vectorResource(id = R.drawable.reset),
                            contentDescription = null,
                        )
                    }

                }
                Row(
                    Modifier, verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable {
                                intvalue -= sliderStepValue; sliderPosition = intvalue.toFloat()
                                Toast
                                    .makeText(context, "$intvalue", Toast.LENGTH_SHORT)
                                    .show()
                                overlayEnable("$overlayName$intvalue")

                            },
                        imageVector = ImageVector.vectorResource(id = R.drawable.remove_48px),
                        contentDescription = null,
                    )

                    Slider(modifier = Modifier
                        .height(16.dp)
                        .weight(1f)
                        .padding(0.dp),
                        value = sliderPosition,
                        onValueChange = { sliderPosition = it; intvalue = it.roundToInt() },
                        valueRange = minSliderValue..maxSliderValue,
                        onValueChangeFinished = {
                            Toast.makeText(context, "$intvalue", Toast.LENGTH_SHORT).show()
                            overlayEnable("$overlayName$intvalue")
                        },
                        steps = sliderSteps,
                        thumb = {
                            Image(
                                imageVector = ImageVector.vectorResource(id = R.drawable.fiber_manual_record_48px),
                                contentDescription = null,
                            )

                        })

                    Image(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable {
                                intvalue += sliderStepValue; sliderPosition = intvalue.toFloat()
                                Toast
                                    .makeText(context, "$intvalue", Toast.LENGTH_SHORT)
                                    .show()
                                overlayEnable("$overlayName$intvalue")
                            },
                        imageVector = ImageVector.vectorResource(id = R.drawable.add_48px),
                        contentDescription = null,
                    )

                }
            }


        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Stable

@Composable
fun HeaderRow(
    header: String = "",
    subHeader: String = "",
    button1text: String = "",
    button1onClick: () -> Unit = {},
    button1weight: Float = 1f,
    button2text: String = "",
    button2onClick: () -> Unit = {},
    button2weight: Float = 1f,
    button3text: String = "",
    button3onClick: () -> Unit = {},
    button3weight: Float = 1f,
    button4text: String = "",
    button4onClick: () -> Unit = {},
    button4weight: Float = 1f,
    switchDescription: String = "",
    onCheckedChange: (Boolean) -> Unit = {},
    isChecked: Boolean = false,
    showSwitch: Boolean = false,
) {
    val scope = rememberCoroutineScope()
    var checkedState by remember { mutableStateOf(isChecked) }

    LaunchedEffect(isChecked) {
        checkedState = isChecked
    }
    val context = LocalContext.current
    Surface {
        Column(
            modifier = Modifier.fillMaxWidth(),
            // verticalAlignment = Alignment.CenterVertically,
            // horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (showSwitch && switchDescription.isEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = header,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (subHeader.isNotEmpty()) {
                            Text(
                                text = subHeader,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    Switch(
                        checked = checkedState, onCheckedChange = {
                            checkedState = it
                            onCheckedChange(it)
                        }, modifier = Modifier
                    )
                }
            } else if (showSwitch && switchDescription.isNotEmpty()) {
                Column(Modifier) {
                    Text(
                        text = header,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = subHeader,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = switchDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = checkedState, onCheckedChange = {
                                checkedState = it
                                onCheckedChange(it)
                            }, modifier = Modifier
                        )
                    }
                }
            } else {
                Text(
                    text = header,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = subHeader,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }


            Row {
                if (button1text.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { scope.launch { withContext(Dispatchers.IO) { button1onClick() } } },
                        modifier = Modifier.weight(button1weight),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) { Text(text = button1text, modifier = Modifier.basicMarquee()) }
                }

                if (button2text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { scope.launch { withContext(Dispatchers.IO) { button2onClick() } } },
                        modifier = Modifier.weight(button2weight),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) { Text(text = button2text, modifier = Modifier.basicMarquee()) }
                }
                if (button3text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { scope.launch { withContext(Dispatchers.IO) { button3onClick() } } },
                        modifier = Modifier.weight(button3weight),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) { Text(text = button3text, modifier = Modifier.basicMarquee()) }
                }
                if (button4text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { scope.launch { withContext(Dispatchers.IO) { button4onClick() } } },
                        modifier = Modifier.weight(button4weight),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) { Text(text = button4text, modifier = Modifier.basicMarquee()) }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

    }
}


@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
//@Preview
@Composable
fun MiscTab() {
    Surface(
        modifier = Modifier.fillMaxSize(), color = cardcol
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            AdmobBanner()

            var rounded_corner_radius by remember { mutableStateOf("0") }
            var config_qs_columns_landscape by remember { mutableStateOf("0") }
            var config_qs_columns_portrait by remember { mutableStateOf("0") }
            val cornersPath = "${GlobalVariables.modulePath}/onDemandCompiler/corners"
            val qsGridGenericPath = "${GlobalVariables.modulePath}/onDemandCompiler/qsGrid"



            NoiseTest()

            LaunchedEffect(Unit) {
                rounded_corner_radius =
                    Shell("su").run("""awk -F'[<>]' '/<dimen name="rounded_corner_radius">/ {print $3}' $cornersPath/res/values/dimens.xml | sed 's/dip//g'""")
                        .stdout()
                config_qs_columns_landscape =
                    Shell("su").run("""awk -F'[<>]' '/<integer name="config_qs_columns_landscape">/ {print $3}' ${qsGridGenericPath}ColumnsLandscapeGeneric/res/values/integers.xml""")
                        .stdout()
                config_qs_columns_portrait =
                    Shell("su").run("""awk -F'[<>]' '/<integer name="config_qs_columns_portrait">/ {print $3}' ${qsGridGenericPath}ColumnsPortraitGeneric/res/values/integers.xml""")
                        .stdout()
            }



            Row {
                OutlinedTextField(modifier = Modifier.weight(1f),
                    value = rounded_corner_radius,
                    singleLine = true,
                    onValueChange = {
                        Shell("su").run(
                            """sed -i 's/<dimen name="rounded_corner_radius">[^<]*/<dimen name="rounded_corner_radius">${it}dip/g' $cornersPath/res/values/dimens.xml"""
                        ).log(); rounded_corner_radius = it
                    },
                    placeholder = { Text("Enter your value", Modifier.basicMarquee()) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.rounded_corner_48px),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            buildOverlay(cornersPath)
                            Shell("su").run("""cmd overlay enable themed.corners.generic""")

                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.move_up_24px),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = { Text("rounded_corner_radius", Modifier.basicMarquee()) })

            }
            Row {
                OutlinedTextField(modifier = Modifier.weight(1f),
                    value = config_qs_columns_landscape,
                    singleLine = true,
                    onValueChange = {
                        Shell("su").run(
                            """sed -i 's/<integer name="config_qs_columns_landscape">[^<]*/<integer name="config_qs_columns_landscape">${it}/g' ${qsGridGenericPath}ColumnsLandscapeGeneric/res/values/integers.xml)"""
                        ); config_qs_columns_landscape = it
                    },
                    placeholder = { Text("Enter your value", Modifier.basicMarquee()) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = {
                        IconButton(onClick = {
                            buildOverlay(cornersPath)
                            Shell("su").run("""cmd overlay enable themed.columnslandscape.generic""")
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.move_up_24px),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = { Text("config_qs_columns_landscape", Modifier.basicMarquee()) })
                OutlinedTextField(modifier = Modifier.weight(1f),
                    value = config_qs_columns_landscape,
                    singleLine = true,
                    onValueChange = {
                        Shell("su").run(
                            """sed -i 's/<integer name="config_qs_columns_landscape">[^<]*/<integer name="config_qs_columns_landscape">${it}/g' ${qsGridGenericPath}ColumnsLandscapeGeneric/res/values/integers.xml)"""
                        ); config_qs_columns_landscape = it
                    },
                    placeholder = { Text("Enter your value", Modifier.basicMarquee()) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = {
                        IconButton(onClick = {
                            buildOverlay(cornersPath)
                            Shell("su").run("""cmd overlay enable themed.columnslandscape.generic""")
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.move_up_24px),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = { Text("config_qs_columns_landscape", Modifier.basicMarquee()) })

            }



            Slideritem(
                drawable = R.drawable.table_rows_48px,
                header = stringResource(R.string.qsquicktilesize),
                sliderSteps = 1,
                sliderStepValue = 20,
                minSliderValue = 60f,
                maxSliderValue = 80f,
                overlayName = "qsquicktilesize"
            )



            Slideritem(
                drawable = R.drawable.table_rows_48px,
                header = stringResource(R.string.qstileheight),
                sliderSteps = 1,
                sliderStepValue = 20,
                minSliderValue = 60f,
                maxSliderValue = 80f,
                overlayName = "qstileheight"
            )
            HeaderRow(
                header = "RoundIconMask",
                subHeader = "Makes app icons masks a perfect circle",
                showSwitch = true,
                onCheckedChange = {
                    if (it) {
                        Shell.SH.run("su -c cmd overlay enable themed.misc.roundiconmask")
                    } else {
                        Shell.SH.run("su -c cmd overlay disable themed.misc.roundiconmask")
                    }
                },
                isChecked = getOverlayList().enabledOverlays.any { it.contains("roundiconmask") },
            )
            HeaderRow(
                header = "Borderless",
                subHeader = "Removes black line hiding display cutout",
                showSwitch = true,
                onCheckedChange = {
                    if (it) {
                        Shell.SH.run("su -c cmd overlay enable themed.misc.borderless")
                    } else {
                        Shell.SH.run("su -c cmd overlay disable themed.misc.borderless")
                    }
                },
                isChecked = getOverlayList().enabledOverlays.any { it.contains("borderless") },
            )


        }
    }

}

@Preview
@Composable
fun NoiseTest() {

    val imageList = listOf(
        ImageBitmap.imageResource(R.drawable.colornoice_00000),
        ImageBitmap.imageResource(R.drawable.colornoice_00001),
        ImageBitmap.imageResource(R.drawable.colornoice_00002),
        ImageBitmap.imageResource(R.drawable.colornoice_00003),
        ImageBitmap.imageResource(R.drawable.colornoice_00004),
        ImageBitmap.imageResource(R.drawable.colornoice_00005),
        ImageBitmap.imageResource(R.drawable.colornoice_00006),
        ImageBitmap.imageResource(R.drawable.colornoice_00007),
        ImageBitmap.imageResource(R.drawable.colornoice_00008),
        ImageBitmap.imageResource(R.drawable.colornoice_00009),
        ImageBitmap.imageResource(R.drawable.colornoice_00010),
        ImageBitmap.imageResource(R.drawable.colornoice_00011),
        ImageBitmap.imageResource(R.drawable.colornoice_00012),
        ImageBitmap.imageResource(R.drawable.colornoice_00013),
        ImageBitmap.imageResource(R.drawable.colornoice_00014),
        ImageBitmap.imageResource(R.drawable.colornoice_00015),
        ImageBitmap.imageResource(R.drawable.colornoice_00016),
        ImageBitmap.imageResource(R.drawable.colornoice_00017),
        ImageBitmap.imageResource(R.drawable.colornoice_00018),
        ImageBitmap.imageResource(R.drawable.colornoice_00019),
        ImageBitmap.imageResource(R.drawable.colornoice_00020),
        ImageBitmap.imageResource(R.drawable.colornoice_00021),
        ImageBitmap.imageResource(R.drawable.colornoice_00022),
        ImageBitmap.imageResource(R.drawable.colornoice_00023),
        ImageBitmap.imageResource(R.drawable.colornoice_00024),
        ImageBitmap.imageResource(R.drawable.colornoice_00025),
        ImageBitmap.imageResource(R.drawable.colornoice_00026),
        ImageBitmap.imageResource(R.drawable.colornoice_00027),
        ImageBitmap.imageResource(R.drawable.colornoice_00028),
        ImageBitmap.imageResource(R.drawable.colornoice_00029),
        ImageBitmap.imageResource(R.drawable.colornoice_00030),
        ImageBitmap.imageResource(R.drawable.colornoice_00031),
        ImageBitmap.imageResource(R.drawable.colornoice_00032),
        ImageBitmap.imageResource(R.drawable.colornoice_00033),
        ImageBitmap.imageResource(R.drawable.colornoice_00034),
        ImageBitmap.imageResource(R.drawable.colornoice_00035),
        ImageBitmap.imageResource(R.drawable.colornoice_00036),
        ImageBitmap.imageResource(R.drawable.colornoice_00037),
        ImageBitmap.imageResource(R.drawable.colornoice_00038),
        ImageBitmap.imageResource(R.drawable.colornoice_00039),
        ImageBitmap.imageResource(R.drawable.colornoice_00040),
        ImageBitmap.imageResource(R.drawable.colornoice_00041),
        ImageBitmap.imageResource(R.drawable.colornoice_00042),
        ImageBitmap.imageResource(R.drawable.colornoice_00043),
        ImageBitmap.imageResource(R.drawable.colornoice_00044),
        ImageBitmap.imageResource(R.drawable.colornoice_00045),
        ImageBitmap.imageResource(R.drawable.colornoice_00046),
        ImageBitmap.imageResource(R.drawable.colornoice_00047),
        ImageBitmap.imageResource(R.drawable.colornoice_00048),
        ImageBitmap.imageResource(R.drawable.colornoice_00049),
        ImageBitmap.imageResource(R.drawable.colornoice_00050),
        ImageBitmap.imageResource(R.drawable.colornoice_00051),
        ImageBitmap.imageResource(R.drawable.colornoice_00052),
        ImageBitmap.imageResource(R.drawable.colornoice_00053),
        ImageBitmap.imageResource(R.drawable.colornoice_00054),
        ImageBitmap.imageResource(R.drawable.colornoice_00055),
        ImageBitmap.imageResource(R.drawable.colornoice_00056),
        ImageBitmap.imageResource(R.drawable.colornoice_00057),
        ImageBitmap.imageResource(R.drawable.colornoice_00058),
        ImageBitmap.imageResource(R.drawable.colornoice_00059),
        ImageBitmap.imageResource(R.drawable.colornoice_00060),
        ImageBitmap.imageResource(R.drawable.colornoice_00061),
        ImageBitmap.imageResource(R.drawable.colornoice_00062),
        ImageBitmap.imageResource(R.drawable.colornoice_00063),
        ImageBitmap.imageResource(R.drawable.colornoice_00064),
        ImageBitmap.imageResource(R.drawable.colornoice_00065),
        ImageBitmap.imageResource(R.drawable.colornoice_00066),
        ImageBitmap.imageResource(R.drawable.colornoice_00067),
        ImageBitmap.imageResource(R.drawable.colornoice_00068),
        ImageBitmap.imageResource(R.drawable.colornoice_00069),
        ImageBitmap.imageResource(R.drawable.colornoice_00070),
        ImageBitmap.imageResource(R.drawable.colornoice_00071),
        ImageBitmap.imageResource(R.drawable.colornoice_00072),
        ImageBitmap.imageResource(R.drawable.colornoice_00073),
        ImageBitmap.imageResource(R.drawable.colornoice_00074),
        ImageBitmap.imageResource(R.drawable.colornoice_00075),
        ImageBitmap.imageResource(R.drawable.colornoice_00076),
        ImageBitmap.imageResource(R.drawable.colornoice_00077),
        ImageBitmap.imageResource(R.drawable.colornoice_00078),
        ImageBitmap.imageResource(R.drawable.colornoice_00079),
        ImageBitmap.imageResource(R.drawable.colornoice_00080),
        ImageBitmap.imageResource(R.drawable.colornoice_00081),
        ImageBitmap.imageResource(R.drawable.colornoice_00082),
        ImageBitmap.imageResource(R.drawable.colornoice_00083),
        ImageBitmap.imageResource(R.drawable.colornoice_00084),
        ImageBitmap.imageResource(R.drawable.colornoice_00085),
        ImageBitmap.imageResource(R.drawable.colornoice_00086),
        ImageBitmap.imageResource(R.drawable.colornoice_00087),
        ImageBitmap.imageResource(R.drawable.colornoice_00088),
        ImageBitmap.imageResource(R.drawable.colornoice_00089),
        ImageBitmap.imageResource(R.drawable.colornoice_00090),
        ImageBitmap.imageResource(R.drawable.colornoice_00091),
        ImageBitmap.imageResource(R.drawable.colornoice_00092),
        ImageBitmap.imageResource(R.drawable.colornoice_00093),
        ImageBitmap.imageResource(R.drawable.colornoice_00094),
        ImageBitmap.imageResource(R.drawable.colornoice_00095),
        ImageBitmap.imageResource(R.drawable.colornoice_00096),
        ImageBitmap.imageResource(R.drawable.colornoice_00097),
        ImageBitmap.imageResource(R.drawable.colornoice_00098),
        ImageBitmap.imageResource(R.drawable.colornoice_00099),
        ImageBitmap.imageResource(R.drawable.colornoice_00100),
        ImageBitmap.imageResource(R.drawable.colornoice_00101),
        ImageBitmap.imageResource(R.drawable.colornoice_00102),
        ImageBitmap.imageResource(R.drawable.colornoice_00103),
        ImageBitmap.imageResource(R.drawable.colornoice_00104),
        ImageBitmap.imageResource(R.drawable.colornoice_00105),
        ImageBitmap.imageResource(R.drawable.colornoice_00106),
        ImageBitmap.imageResource(R.drawable.colornoice_00107),
        ImageBitmap.imageResource(R.drawable.colornoice_00108),
        ImageBitmap.imageResource(R.drawable.colornoice_00109),
        ImageBitmap.imageResource(R.drawable.colornoice_00110),
        ImageBitmap.imageResource(R.drawable.colornoice_00111),
        ImageBitmap.imageResource(R.drawable.colornoice_00112),
        ImageBitmap.imageResource(R.drawable.colornoice_00113),
        ImageBitmap.imageResource(R.drawable.colornoice_00114),
        ImageBitmap.imageResource(R.drawable.colornoice_00115),
        ImageBitmap.imageResource(R.drawable.colornoice_00116),
        ImageBitmap.imageResource(R.drawable.colornoice_00117),
        ImageBitmap.imageResource(R.drawable.colornoice_00118),
        ImageBitmap.imageResource(R.drawable.colornoice_00119),
        ImageBitmap.imageResource(R.drawable.colornoice_00120),
        ImageBitmap.imageResource(R.drawable.colornoice_00121),
        ImageBitmap.imageResource(R.drawable.colornoice_00122),
        ImageBitmap.imageResource(R.drawable.colornoice_00123),
        ImageBitmap.imageResource(R.drawable.colornoice_00124)
    )

    var currentFrame by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            currentFrame = (currentFrame + 1) % imageList.size
            delay(10L)  // Adjust delay as needed
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {

        val paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            shader = ImageShader(imageList[currentFrame], TileMode.Repeated, TileMode.Repeated)
        }

        drawIntoCanvas {
            it.nativeCanvas.drawPaint(paint)
        }
        paint.reset()
    }
}
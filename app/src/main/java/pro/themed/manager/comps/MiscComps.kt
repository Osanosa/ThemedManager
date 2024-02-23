package pro.themed.manager.comps

import android.widget.Toast
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pro.themed.manager.AdmobBanner
import pro.themed.manager.MainActivity.Companion.overlayList
import pro.themed.manager.R
import pro.themed.manager.buildOverlay
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
    if (overlayList.overlayList.any { it.contains(overlayName) } && !overlayList.unsupportedOverlays.any {
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
            @Composable
            fun MiscTextField(
                input: String,
                path: String,
                resource: String,
                file: String,
                overlay: String,
                modifier: Modifier, drawable: Int
            ) {
                var input by remember { mutableStateOf(input) }
                OutlinedTextField(modifier = modifier,
                    value = input,
                    singleLine = true,
                    onValueChange = {
                        Shell("su").run(
                            """sed -i 's/<${file.removeSuffix("s")} name="$resource">[^<]*/<dimen name="$resource">${it}dip/g' $path/res/values/$file.xml"""
                        ).log(); input = it
                    },
                    placeholder = { Text("Enter your value", Modifier.basicMarquee()) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = drawable),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            buildOverlay(path)
                            Shell("su").run("""cmd overlay enable $overlay""")

                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.move_up_24px),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = { Text(resource, Modifier.basicMarquee()) })
            }
            val cornersPath = "${GlobalVariables.modulePath}/onDemandCompiler/corners"
            val qsGridGenericPath = "${GlobalVariables.modulePath}/onDemandCompiler/qsGrid"

            var rounded_corner_radius by remember { mutableStateOf( Shell("su").run("""awk -F'[<>]' '/<dimen name="rounded_corner_radius">/ {print $3}' $cornersPath/res/values/dimens.xml | sed 's/dip//g'""")
                .stdout()) }
            var config_qs_columns_landscape by remember { mutableStateOf(Shell("su").run("""awk -F'[<>]' '/<integer name="config_qs_columns_landscape">/ {print $3}' ${qsGridGenericPath}ColumnsLandscapeGeneric/res/values/integers.xml""")
                .stdout()) }
            var config_qs_columns_portrait by remember { mutableStateOf(  Shell("su").run("""awk -F'[<>]' '/<integer name="config_qs_columns_portrait">/ {print $3}' ${qsGridGenericPath}ColumnsPortraitGeneric/res/values/integers.xml""")
                .stdout()) }
            var config_qs_rows_landscape by remember { mutableStateOf(Shell("su").run("""awk -F'[<>]' '/<integer name="config_qs_rows_landscape">/ {print $3}' ${qsGridGenericPath}RowsLandscapeGeneric/res/values/integers.xml""")
                .stdout()) }
            var config_qs_rows_portrait by remember { mutableStateOf(Shell("su").run("""awk -F'[<>]' '/<integer name="config_qs_rows_landscape">/ {print $3}' ${qsGridGenericPath}RowsLandscapeGeneric/res/values/integers.xml""")
                .stdout()) }

            LaunchedEffect(Unit) {

            }



            Row {
                MiscTextField(
                    input = rounded_corner_radius,
                    path = cornersPath,
                    resource = "rounded_corner_radius",
                    file = """dimens""",
                    overlay = """themed.corners.generic""",
                    modifier = Modifier.weight(1f),
                    drawable = R.drawable.rounded_corner_48px
                )

            }
            Row {
                //columns portrait
                MiscTextField(
                    input = config_qs_columns_portrait,
                    path = qsGridGenericPath + "ColumnsPortraitGeneric/",
                    resource = "config_qs_columns_portrait",
                    file = "integers",
                    overlay = "themed.columnsportrait.generic",
                    modifier = Modifier.weight(1f),
                    drawable = R.drawable.view_week_48px
                )

                //columns landscape
                MiscTextField(
                    input = config_qs_columns_landscape,
                    path = qsGridGenericPath + "ColumnsLandscapeGeneric/",
                    resource = "config_qs_columns_landscape",
                    file = "integers",
                    overlay = "themed.columnslandscape.generic",
                    modifier = Modifier.weight(1f),
                    drawable = R.drawable.view_week_48px
                )


            }
            Row {
    //rows portrait
                MiscTextField(
                    input = config_qs_rows_portrait,
                    path = qsGridGenericPath + "RowsPortraitGeneric/",
                    resource = "config_qs_rows_portrait",
                    file = "integers",
                    overlay = "themed.rowsportrait.generic",
                    modifier = Modifier.weight(1f),
                    drawable = R.drawable.table_rows_48px
                )
                //rows landscape
                MiscTextField(
                    input = config_qs_rows_landscape,
                    path = qsGridGenericPath    + "RowsLandscapeGeneric/",
                    resource = "config_qs_rows_landscape",
                    file = "integers",
                    overlay = "themed.rowslandscape.generic",
                    modifier = Modifier.weight(1f),
                    drawable = R.drawable.table_rows_48px
                )
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
                isChecked = overlayList.enabledOverlays.any { it.contains("roundiconmask") },
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
                isChecked = overlayList.enabledOverlays.any { it.contains("borderless") },
            )


        }
    }

}


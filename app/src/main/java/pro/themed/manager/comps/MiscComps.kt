package pro.themed.manager.comps

import android.widget.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.jaredrummler.ktsh.*
import kotlinx.coroutines.*
import pro.themed.manager.*
import pro.themed.manager.R
import pro.themed.manager.ui.theme.*
import kotlin.math.*
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
                        Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'themed.$overlayName' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
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

                    androidx.compose.material3.Slider(modifier = Modifier
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

@Stable
@Preview
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 4.dp),
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
                            style = MaterialTheme.typography.subtitle1
                        )
                        if (subHeader.isNotEmpty()) {
                            Text(
                                text = subHeader,
                                style = MaterialTheme.typography.body1,
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
                        style = MaterialTheme.typography.subtitle1
                    )
                    Text(
                        text = subHeader,
                        style = MaterialTheme.typography.body1,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = switchDescription,
                            style = MaterialTheme.typography.body1,
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
                    style = MaterialTheme.typography.subtitle1
                )
                Text(
                    text = subHeader,
                    style = MaterialTheme.typography.body1,
                )
            }


            Row {
                if (button1text.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { scope.launch { withContext(Dispatchers.IO) { button1onClick() } } },
                        modifier = Modifier.weight(button1weight),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
                    ) { Text(text = button1text) }
                }

                if (button2text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { scope.launch { withContext(Dispatchers.IO) { button2onClick() } } },
                        modifier = Modifier.weight(button2weight),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
                    ) { Text(text = button2text) }
                }
                if (button3text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { scope.launch { withContext(Dispatchers.IO) { button3onClick() } } },
                        modifier = Modifier.weight(button3weight),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
                    ) { Text(text = button3text) }
                }
                if (button4text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { scope.launch { withContext(Dispatchers.IO) { button4onClick() } } },
                        modifier = Modifier.weight(button4weight),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
                    ) { Text(text = button4text) }
                }
            }
        }

    }
}


@ExperimentalMaterial3Api
//@Preview
@Composable
fun MiscTab() {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colors.cardcol
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            AdmobBanner()

            Row(Modifier.padding(8.dp)) {
                Icon(Icons.Default.Info, contentDescription = "")
                Text(text = "Only supported options are being shown")
            }
            Slideritem(
                drawable = R.drawable.rounded_corner_48px,
                header = stringResource(R.string.rounded_corners_header),
                sliderSteps = 17,
                sliderStepValue = 2,
                minSliderValue = 0f,
                maxSliderValue = 36f,
                overlayName = "roundedcorners"
            )


            Divider()


            Slideritem(
                drawable = R.drawable.view_week_48px,
                header = stringResource(R.string.columns_portrait),
                sliderSteps = 8,
                sliderStepValue = 1,
                minSliderValue = 1f,
                maxSliderValue = 10f,
                overlayName = "qsgrid.columnsportrait"
            )




            Slideritem(
                drawable = R.drawable.table_rows_48px,
                header = stringResource(R.string.rows_portrait),
                sliderSteps = 8,
                sliderStepValue = 1,
                minSliderValue = 1f,
                maxSliderValue = 10f,
                overlayName = "qsgrid.rowsportrait"
            )



            Slideritem(
                drawable = R.drawable.view_week_48px,
                header = stringResource(R.string.columns_landscape),
                sliderSteps = 8,
                sliderStepValue = 1,
                minSliderValue = 1f,
                maxSliderValue = 10f,
                overlayName = "qsgrid.columnslandscape"
            )



            Slideritem(
                drawable = R.drawable.table_rows_48px,
                header = stringResource(R.string.rows_landscape),
                sliderSteps = 8,
                sliderStepValue = 1,
                minSliderValue = 1f,
                maxSliderValue = 10f,
                overlayName = "qsgrid.rowslandscape"
            )

            Divider()


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

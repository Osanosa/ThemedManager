@file:OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class
)

package pro.themed.manager.comps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.*
import pro.themed.manager.R
import pro.themed.manager.ui.theme.*


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AccentsNewTemp(
    name: String, header: String
) {
    var redvisible by remember { mutableStateOf(false) }
    var orangevisible by remember { mutableStateOf(false) }
    var yellowvisible by remember { mutableStateOf(false) }
    var lightgreenvisible by remember { mutableStateOf(false) }
    var tealvisible by remember { mutableStateOf(false) }
    var lightbluevisible by remember { mutableStateOf(false) }
    var indigovisible by remember { mutableStateOf(false) }
    var purplevisible by remember { mutableStateOf(false) }

    val tilesize = (LocalConfiguration.current.smallestScreenWidthDp - 16) / 8
    Card(
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.bordercol),
        modifier = Modifier
            .fillMaxWidth()
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
                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'themed.$name.M' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
                }) {
                    Image(
                        painter = painterResource(R.drawable.reset),
                        contentDescription = null,
                    )
                }
            }
            Column {
                Row {
                    Surface(
                        modifier = Modifier.size(tilesize.dp),
                        color = MaterialRed500,
                        onClick = {
                            redvisible = !redvisible
                            orangevisible = false
                            yellowvisible = false
                            lightgreenvisible = false
                            tealvisible = false
                            lightbluevisible = false
                            indigovisible = false
                            purplevisible = false
                        }) {}
                    Surface(modifier = Modifier.size(tilesize.dp),
                        color = MaterialOrange500,
                        onClick = {
                            redvisible = false
                            orangevisible = !orangevisible
                            yellowvisible = false
                            lightgreenvisible = false
                            tealvisible = false
                            lightbluevisible = false
                            indigovisible = false
                            purplevisible = false
                        }) {}
                    Surface(modifier = Modifier.size(tilesize.dp),
                        color = MaterialAmber500,
                        onClick = {
                            redvisible = false
                            orangevisible = false
                            yellowvisible = !yellowvisible
                            lightgreenvisible = false
                            tealvisible = false
                            lightbluevisible = false
                            indigovisible = false
                            purplevisible = false
                        }) { }
                    Surface(modifier = Modifier.size(tilesize.dp),
                        color = MaterialLightGreen500,
                        onClick = {
                            redvisible = false
                            orangevisible = false
                            yellowvisible = false
                            lightgreenvisible = !lightgreenvisible
                            tealvisible = false
                            lightbluevisible = false
                            indigovisible = false
                            purplevisible = false
                        }) { }
                    Surface(modifier = Modifier.size(tilesize.dp),
                        color = MaterialTeal500,
                        onClick = {
                            redvisible = false
                            orangevisible = false
                            yellowvisible = false
                            lightgreenvisible = false
                            tealvisible = !tealvisible
                            lightbluevisible = false
                            indigovisible = false
                            purplevisible = false
                        }) { }
                    Surface(modifier = Modifier.size(tilesize.dp),
                        color = MaterialLightBlue500,
                        onClick = {
                            redvisible = false
                            orangevisible = false
                            yellowvisible = false
                            lightgreenvisible = false
                            tealvisible = false
                            lightbluevisible = !lightbluevisible
                            indigovisible = false
                            purplevisible = false
                        }) { }
                    Surface(modifier = Modifier.size(tilesize.dp),
                        color = MaterialIndigo500,
                        onClick = {
                            redvisible = false
                            orangevisible = false
                            yellowvisible = false
                            lightgreenvisible = false
                            tealvisible = false
                            lightbluevisible = false
                            indigovisible = !indigovisible
                            purplevisible = false
                        }) { }
                    Surface(modifier = Modifier.size(tilesize.dp),
                        color = MaterialPurple500,
                        onClick = {
                            redvisible = false
                            orangevisible = false
                            yellowvisible = false
                            lightgreenvisible = false
                            tealvisible = false
                            lightbluevisible = false
                            indigovisible = false
                            purplevisible = !purplevisible
                        }) { }
                }
                AnimatedVisibility(redvisible) {
                    Row {
                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialPinkA700,
                            onClick = { overlayEnable("$name.MaterialPinkA700") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialPink500,
                            onClick = { overlayEnable("$name.MaterialPink500") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialPinkA200,
                            onClick = { overlayEnable("$name.MaterialPinkA200") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialPinkA400,
                            onClick = { overlayEnable("$name.MaterialPinkA400") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialRedA700,
                            onClick = { overlayEnable("$name.MaterialRedA700") }) { }

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialRed500,
                            onClick = { overlayEnable("$name.MaterialRed500") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialRedA200,
                            onClick = { overlayEnable("$name.MaterialRedA200") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialRedA400,
                            onClick = { overlayEnable("$name.MaterialRedA400") }) {}
                    }

                }
                AnimatedVisibility(orangevisible) {
                    Row {
                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = Color(android.graphics.Color.rgb(221, 44, 0)),
                            onClick = { overlayEnable("$name.MaterialDeepOrangeA700") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = Color(android.graphics.Color.rgb(255, 87, 34)),
                            onClick = { overlayEnable("$name.MaterialDeepOrange500") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = Color(android.graphics.Color.rgb(255, 110, 64)),
                            onClick = { overlayEnable("$name.MaterialDeepOrangeA200") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = Color(android.graphics.Color.rgb(255, 61, 0)),
                            onClick = { overlayEnable("$name.MaterialDeepOrangeA400") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = Color(android.graphics.Color.rgb(255, 109, 0)),
                            onClick = { overlayEnable("$name.MaterialOrangeA700") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = Color(android.graphics.Color.rgb(255, 152, 0)),
                            onClick = { overlayEnable("$name.MaterialOrange500") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = Color(android.graphics.Color.rgb(255, 171, 64)),
                            onClick = { overlayEnable("$name.MaterialOrangeA200") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = Color(android.graphics.Color.rgb(255, 145, 0)),
                            onClick = { overlayEnable("$name.MaterialOrangeA400") }) {}
                    }

                }
                AnimatedVisibility(yellowvisible) {
                    Row {
                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialAmberA700,
                            onClick = { overlayEnable("$name.MaterialAmberA700") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialAmber500,
                            onClick = { overlayEnable("$name.MaterialAmber500") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialAmberA200,
                            onClick = { overlayEnable("$name.MaterialAmberA200") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialAmberA400,
                            onClick = { overlayEnable("$name.MaterialAmberA400") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialYellowA700,
                            onClick = { overlayEnable("$name.MaterialYellowA700") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialYellow500,
                            onClick = { overlayEnable("$name.MaterialYellow500") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialYellowA200,
                            onClick = { overlayEnable("$name.MaterialYellowA200") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialYellowA400,
                            onClick = { overlayEnable("$name.MaterialYellowA400") }) {}

                    }

                }
                AnimatedVisibility(lightgreenvisible) {
                    Row {
                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialLimeA700,
                            onClick = { overlayEnable("$name.MaterialLimeA700") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialLime500,
                            onClick = { overlayEnable("$name.MaterialLime500") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialLimeA200,
                            onClick = { overlayEnable("$name.MaterialLimeA200") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialLimeA400,
                            onClick = { overlayEnable("$name.MaterialLimeA400") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialLightGreenA700,
                            onClick = { overlayEnable("$name.MaterialLightGreenA700") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialLightGreen500,
                            onClick = { overlayEnable("$name.MaterialLightGreen500") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialLightGreenA200,
                            onClick = { overlayEnable("$name.MaterialLightGreenA200") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialLightGreenA400,
                            onClick = { overlayEnable("$name.MaterialLightGreenA400") }) {}


                    }
                }
                AnimatedVisibility(tealvisible) {
                    Row {
                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialGreenA700,
                            onClick = { overlayEnable("$name.MaterialGreenA700") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialGreen500,
                            onClick = { overlayEnable("$name.MaterialGreen500") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialGreenA200,
                            onClick = { overlayEnable("$name.MaterialGreenA200") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialGreenA400,
                            onClick = { overlayEnable("$name.MaterialGreenA400") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialTealA700,
                            onClick = { overlayEnable("$name.MaterialTealA700") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialTeal500,
                            onClick = { overlayEnable("$name.MaterialTeal500") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialTealA200,
                            onClick = { overlayEnable("$name.MaterialTealA200") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialTealA400,
                            onClick = { overlayEnable("$name.MaterialTealA400") }) {}

                    }
                }
                AnimatedVisibility(lightbluevisible) {
                    Row {
                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialCyanA700,
                            onClick = { overlayEnable("$name.MaterialCyanA700") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialCyan500,
                            onClick = { overlayEnable("$name.MaterialCyan500") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialCyanA200,
                            onClick = { overlayEnable("$name.") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialCyanA400,
                            onClick = { overlayEnable("$name.MaterialCyanA400") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialLightBlueA700,
                            onClick = { overlayEnable("$name.MaterialLightBlueA700") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialLightBlue500,
                            onClick = { overlayEnable("$name.MaterialLightBlue500") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialLightBlueA200,
                            onClick = { overlayEnable("$name.MaterialLightBlueA200") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialLightBlueA400,
                            onClick = { overlayEnable("$name.MaterialLightBlueA400") }) {}
                    }
                }
                AnimatedVisibility(indigovisible) {
                    Row {
                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialBlueA700,
                            onClick = { overlayEnable("$name.MaterialBlueA700") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialBlue500,
                            onClick = { overlayEnable("$name.MaterialBlue500") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialBlueA200,
                            onClick = { overlayEnable("$name.MaterialBlueA200") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialBlueA400,
                            onClick = { overlayEnable("$name.MaterialBlueA400") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialIndigoA700,
                            onClick = { overlayEnable("$name.") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialIndigo500,
                            onClick = { overlayEnable("$name.MaterialIndigo500") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialIndigoA200,
                            onClick = { overlayEnable("$name.MaterialIndigoA200") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialIndigoA400,
                            onClick = { overlayEnable("$name.MaterialIndigoA400") }) {}
                    }
                }
                AnimatedVisibility(purplevisible) {
                    Row {
                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialDeepPurpleA700,
                            onClick = { overlayEnable("$name.MaterialDeepPurpleA700") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialDeepPurple500,
                            onClick = { overlayEnable("$name.MaterialDeepPurple500") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialDeepPurpleA200,
                            onClick = { overlayEnable("$name.MaterialDeepPurpleA200") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialDeepPurpleA400,
                            onClick = { overlayEnable("$name.MaterialDeepPurpleA400") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialPurpleA700,
                            onClick = { overlayEnable("$name.MaterialPurpleA700") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialPurple500,
                            onClick = { overlayEnable("$name.MaterialPurple500") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialPurpleA200,
                            onClick = { overlayEnable("$name.MaterialPurpleA200") }) {}

                        Surface(modifier = Modifier.size(tilesize.dp),
                            color = MaterialPurpleA400,
                            onClick = { overlayEnable("$name.MaterialPurpleA400") }) {}
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UIBGDark() {

    val tilesize = (LocalConfiguration.current.smallestScreenWidthDp - 16) / 8
    Card(
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.bordercol),
        modifier = Modifier
            .fillMaxWidth()
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
                    text = "UIBG Dark",
                    fontSize = 24.sp
                )
                IconButton(modifier = Modifier, onClick = {
                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'themed.uibg.d' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
                }) {
                    Image(
                        painter = painterResource(R.drawable.reset),
                        contentDescription = null,
                    )
                }
            }
            Column {
                Row {
                    Surface(modifier = Modifier.size(tilesize.dp),
                        color = uibgAmoled,
                        onClick = { overlayEnable("uibg.dark.amoled") }) {}

                    Surface(modifier = Modifier.size(tilesize.dp),
                        color = uibgCharcoal,
                        onClick = { overlayEnable("uibg.dark.charcoal") }) {}

                    Surface(modifier = Modifier.size(tilesize.dp),
                        color = uibgCharcoal,
                        onClick = { overlayEnable("uibg.dark.charcoalf2") }) {
                        Text(text = "f2")
                    }


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

            var state by remember { mutableStateOf(CardFace.Front) }
            FlipCard(cardFace = state, onClick = {
                state = it.next
            }, axis = RotationAxis.AxisY, back = {
                if (!overlayList.any { it.contains("accents.dark") }) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = ""
                    )

                } else {
                    if (unsupportedOverlays.any { it.contains("accents.dark") }) {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = ""
                        )
                    } else {
                        AccentsNewTemp("accents.dark", stringResource(R.string.accents_dark))

                    }
                }


            }, front = {

                if (!overlayList.any { it.contains("accents.M") }) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "" )

                } else {
                    if (unsupportedOverlays.any { it.contains("accents.M") }) {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = ""
                        )
                    } else {
                        AccentsNewTemp("accents", stringResource(R.string.accents))

                    }
                }

            })
            Spacer(modifier = Modifier.height(8.dp))
            if (!overlayList.any { it.contains("uibg") }) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = ""
                )

            } else {
                if (unsupportedOverlays.any { it.contains("uibg") }) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = ""
                    )
                } else {
                    UIBGDark()

                }
            }


            Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier.height(8.dp))
            InfoCard()

        }

    }


}

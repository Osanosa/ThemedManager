package pro.themed.manager.comps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.R
import pro.themed.manager.ui.theme.bordercol
import pro.themed.manager.ui.theme.cardcol

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Accents(
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
                verticalAlignment = Alignment.CenterVertically,
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
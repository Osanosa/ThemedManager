package pro.themed.manager.comps

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.R
import pro.themed.manager.buildOverlay
import pro.themed.manager.ui.theme.Purple
import pro.themed.manager.ui.theme.cardcol
import pro.themed.manager.utils.GlobalVariables
import pro.themed.manager.utils.showInterstitial
import kotlin.math.roundToInt

@OptIn(
    ExperimentalLayoutApi::class, ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class
)
@Preview
@Composable
fun QSTileCard() {
    val context = LocalContext.current
    val testdp = (LocalConfiguration.current.screenWidthDp - 16) / 6

    @Composable
    fun MyIconButton(overlayname: String, contentdescription: String, iconname: Int) {


        IconButton(
            onClick = {
                Shell.SU.run("cd ${GlobalVariables.modulePath}/onDemandCompiler/QsPanel")
                Shell.SU.run("""sed -i 's/@drawable\/[^"]*/@drawable\/$overlayname/g' "res/drawable/ic_qs_circle.xml"""")
            }, modifier = Modifier
                .size(testdp.dp)
                .background(color = MaterialTheme.colors.cardcol)
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                painter = painterResource(iconname),
                contentDescription = contentdescription,
            )
        }

    }


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
                text = stringResource(R.string.qspanels_header),
                fontSize = 24.sp
            )
            IconButton(onClick = {
                Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'themed.qspanel' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"$" + "ol\"; done")
            }) {
                Image(
                    painter = painterResource(R.drawable.reset), contentDescription = null
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(text = "Style:")
            Spacer(modifier = Modifier.width(8.dp))

            var style by remember { mutableStateOf("default") }
            LaunchedEffect(style) {
                Shell.SU.run("cd ${GlobalVariables.modulePath}/onDemandCompiler/QsPanel")
                Shell.SU.run("""sed -i 's/@drawable\/[^"]*/@drawable\/bg_$style/g' "res/drawable/themed_qspanel.xml"""")

                Shell.SU.run("cmd vibrator_manager synced -f -d dumpstate oneshot 50")


            }

            FilterChip(colors = ChipDefaults.filterChipColors(selectedBackgroundColor = Purple),
                shape = CircleShape,
                selected = style.contains("default"),
                onClick = { style = "default" },
                content = { Text("Default") },
                leadingIcon = if (style.contains("default")) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Localized Description",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                })
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(colors = ChipDefaults.filterChipColors(selectedBackgroundColor = Purple),
                shape = CircleShape,
                selected = style.contains("clear"),
                onClick = { style = "clear" },
                content = { Text("Clear") },
                leadingIcon = if (style.contains("clear")) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Localized Description",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                })
        }

        Spacer(modifier = Modifier.height(8.dp))



        Surface {
            Column {
                FlowRow {
                    MyIconButton(
                        overlayname = "dualtonecircle",
                        contentdescription = "Circle with Dual Tone",
                        iconname = R.drawable.qscirclewithdualtone
                    )
                    MyIconButton(
                        overlayname = "circlegradient",
                        contentdescription = "Circle with Gradient",
                        iconname = R.drawable.qscirclewithgradient
                    )

                    MyIconButton(
                        overlayname = "circletrim",
                        contentdescription = "Circle with Trim",
                        iconname = R.drawable.qscirclewithtrim
                    )
                    MyIconButton(
                        overlayname = "cookie",
                        contentdescription = "Cookie",
                        iconname = R.drawable.qscookie
                    )
                    MyIconButton(
                        overlayname = "cosmos",
                        contentdescription = "Cosmos",
                        iconname = R.drawable.qscosmos
                    )
                    MyIconButton(
                        overlayname = "defaultcircle",
                        contentdescription = "Default",
                        iconname = R.drawable.qsdefault
                    )
                    MyIconButton(
                        overlayname = "dividedcircle",
                        contentdescription = "Divided Circle",
                        iconname = R.drawable.qsdividedcircle
                    )
                    MyIconButton(
                        overlayname = "dottedcircle",
                        contentdescription = "Dotted Circle",
                        iconname = R.drawable.qsdottedcircle
                    )
                    MyIconButton(
                        overlayname = "dualtonecircletrim",
                        contentdescription = "DualTone Circle with Trim",
                        iconname = R.drawable.qsdualtonecircletrim
                    )
                    MyIconButton(
                        overlayname = "ink", contentdescription = "Ink", iconname = R.drawable.qsink
                    )
                    MyIconButton(
                        overlayname = "inkdrop",
                        contentdescription = "Inkdrop",
                        iconname = R.drawable.qsinkdrop
                    )
                    MyIconButton(
                        overlayname = "justicons",
                        contentdescription = "Just Icons",
                        iconname = R.drawable.qsjusticons
                    )
                    MyIconButton(
                        overlayname = "mountain",
                        contentdescription = "Mountain",
                        iconname = R.drawable.qsmountain
                    )
                    MyIconButton(
                        overlayname = "neonlike",
                        contentdescription = "NeonLike",
                        iconname = R.drawable.qsneonlike
                    )
                    MyIconButton(
                        overlayname = "ninja",
                        contentdescription = "Ninja",
                        iconname = R.drawable.qsninja
                    )
                    MyIconButton(
                        overlayname = "oreocircletrim",
                        contentdescription = "Oreo (Circle Trim)",
                        iconname = R.drawable.qsoreocircletrim
                    )
                    MyIconButton(
                        overlayname = "oreosquircletrim",
                        contentdescription = "Oreo (Squircle Trim)",
                        iconname = R.drawable.qsoreosquircletrim
                    )
                    MyIconButton(
                        overlayname = "pokesign",
                        contentdescription = "Pokesign",
                        iconname = R.drawable.qspokesign
                    )
                    MyIconButton(
                        overlayname = "squaremedo",
                        contentdescription = "Squaremedo",
                        iconname = R.drawable.qssquaremedo
                    )
                    MyIconButton(
                        overlayname = "squircle",
                        contentdescription = "Squircle",
                        iconname = R.drawable.qssquircle
                    )
                    MyIconButton(
                        overlayname = "squircletrim",
                        contentdescription = "Squircle with trim",
                        iconname = R.drawable.qssquircletrim
                    )
                    MyIconButton(
                        overlayname = "teardrop",
                        contentdescription = "TearDrop",
                        iconname = R.drawable.qsteardrop
                    )
                    MyIconButton(
                        overlayname = "triangle",
                        contentdescription = "Triangle",
                        iconname = R.drawable.qstriangle
                    )
                    MyIconButton(
                        overlayname = "wavey",
                        contentdescription = "Wavey",
                        iconname = R.drawable.qswavey
                    )

                }
            }
        }

        var sliderPosition by rememberSaveable { mutableFloatStateOf(0f) }
        var intvalue by rememberSaveable { mutableIntStateOf(sliderPosition.roundToInt()) }
        var minSliderValue by rememberSaveable { mutableFloatStateOf(0f) }
        var maxSliderValue by rememberSaveable { mutableFloatStateOf(60f) }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Rounded corners: $intvalue")
        Spacer(modifier = Modifier.height(8.dp))

        androidx.compose.material3.Slider(modifier = Modifier
            .height(16.dp)
            .weight(1f)
            .padding(0.dp),
            value = sliderPosition,
            onValueChange = { sliderPosition = it ; intvalue = it.roundToInt()},
            valueRange = minSliderValue..maxSliderValue,
            onValueChangeFinished = {
                Toast.makeText(context, "$intvalue", Toast.LENGTH_SHORT).show()
                Shell.SU.run("cd ${GlobalVariables.modulePath}/onDemandCompiler/QsPanel")
                Shell.SU.run("""sed -i '/corners/s/[0-9][0-9]*\.0/$intvalue.0/g' "res/drawable/bg_default.xml"""")
            },
            steps = 29,
            thumb = {
                Image(
                    painter = painterResource(R.drawable.fiber_manual_record_48px),
                    contentDescription = null,
                )

            })
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(), onClick = {
                Shell.SU.run("cd ${GlobalVariables.modulePath}/onDemandCompiler/QsPanel")
                buildOverlay()
                Shell.SU.run("""cmd overlay enable themed.qspanel.generic""")
                showInterstitial(context) {}


            }, shape = CircleShape
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(text = "Build and update")
                Icon(
                    modifier = Modifier.height(24.dp),
                    painter = painterResource(id = R.drawable.arrow_right_alt_48px),
                    contentDescription = ""
                )

            }
        }

    }
}

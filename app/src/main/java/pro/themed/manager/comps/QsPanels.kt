package pro.themed.manager.comps

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.R
import pro.themed.manager.buildOverlay
import pro.themed.manager.log
import pro.themed.manager.ui.theme.Purple
import pro.themed.manager.ui.theme.cardcol
import pro.themed.manager.utils.GlobalVariables
import pro.themed.manager.utils.showInterstitial

@Composable
fun QsPanel() {
    Column(
        modifier = Modifier
            .padding(start = 8.dp)
            .verticalScroll(ScrollState(0))
    ) {
        QSTileCard()
    }
}

@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Preview
@Composable
fun QSTileCard() {
    val context = LocalContext.current
    val testdp = (LocalConfiguration.current.smallestScreenWidthDp - 64 - 16) / 5
    val qsPath = "${GlobalVariables.modulePath}/onDemandCompiler/QsPanel"
    val qsShell = Shell("su")
    qsShell.run("cd $qsPath")
    @Stable
    @Composable
    fun MyIconButton(overlayname: String, contentdescription: String, iconname: Int) {


        IconButton(
            onClick = {
                qsShell.run("sed -i 's/@drawable\\/[^\"]*/@drawable\\/$overlayname/g' \"res/drawable/ic_qs_circle.xml\"")
                qsShell.run("cmd vibrator_manager synced -f -d dumpstate oneshot 50")

            }, modifier = Modifier
                .size(testdp.dp)
                .background(color = MaterialTheme.colors.cardcol)
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
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
                qsShell.run("for ol in \$(cmd overlay list | grep -E 'themed.qspanel' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"$" + "ol\"; done")
            }) {
                Image(
                    painter = painterResource(R.drawable.reset), contentDescription = null
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(text = "Style:")
            Spacer(modifier = Modifier.width(8.dp))

            var style by remember { mutableStateOf("") }
            LaunchedEffect(Unit) {
                style =
                    qsShell.run("""awk -F'bg_' '/<item android:drawable="@drawable\/bg_/ {print $2}' $qsPath/res/drawable/themed_qspanel.xml | sed 's/\" \/>//g'""")
                        .stdout()
            }
            LaunchedEffect(style) {
                qsShell.run("""sed -i 's/@drawable\/[^"]*/@drawable\/bg_$style/g' "$qsPath/res/drawable/themed_qspanel.xml"""")
                    .log()

                qsShell.run("cmd vibrator_manager synced -f -d dumpstate oneshot 50")


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
        var qs_label_container_margin by remember { mutableStateOf("") }
        var qs_tile_start_padding by remember { mutableStateOf("") }
        var columnsPortrait by remember { mutableStateOf("") }
        var rowsPortrait by remember { mutableStateOf("") }
        var columnsLandscape by remember { mutableStateOf("") }
        var rowsLandscape by remember { mutableStateOf("") }
        var qsQuickTileSize by remember { mutableStateOf("") }
        var qsTileHeight by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            qs_label_container_margin =
                qsShell.run("""awk -F'[<>]' '/<dimen name="qs_label_container_margin">/ {print $3}' $qsPath/res/values/dimens.xml | sed 's/dip//g'""")
                    .stdout()
            qs_tile_start_padding =
                qsShell.run("""awk -F'[<>]' '/<dimen name="qs_tile_start_padding">/ {print $3}' $qsPath/res/values/dimens.xml | sed 's/dip//g'""")
                    .stdout()
            columnsPortrait =
                qsShell.run("""awk -F'[<>]' '/<integer name="quick_settings_num_columns">/ {print $3}' $qsPath/res/values/integers.xml""")
                    .stdout()
            rowsPortrait =
                qsShell.run("""awk -F'[<>]' '/<integer name="quick_settings_max_rows">/ {print $3}' $qsPath/res/values/integers.xml""")
                    .stdout()
            columnsLandscape =
                qsShell.run("""awk -F'[<>]' '/<integer name="config_qs_columns_landscape">/ {print $3}' $qsPath/res/values/integers.xml""")
                    .stdout()
            rowsLandscape =
                qsShell.run("""awk -F'[<>]' '/<integer name="config_qs_rows_landscape">/ {print $3}' $qsPath/res/values/integers.xml""")
                    .stdout()
            qsQuickTileSize =
                qsShell.run("""awk -F'[<>]' '/<dimen name="qs_quick_tile_size">/ {print $3}' $qsPath/res/values/dimens.xml | sed 's/dip//g'""")
                    .stdout()
            qsTileHeight =
                qsShell.run("""awk -F'[<>]' '/<dimen name="qs_tile_height">/ {print $3}' $qsPath/res/values/dimens.xml | sed 's/dip//g'""")
                    .stdout()

        }
        Row {
            androidx.compose.material.OutlinedTextField(modifier = Modifier.weight(1f),
                value = qs_label_container_margin,
                singleLine = true,
                onValueChange = {
                    qs_label_container_margin = it
                    qsShell.run("""sed -i 's/<dimen name="qs_label_container_margin">[^<]*/<dimen name="qs_label_container_margin">${it}dip/g' $qsPath/res/values/dimens.xml""")

                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = {
                    Text(
                        text = "qs_label_container_margin", Modifier.basicMarquee()
                    )
                })

            Spacer(modifier = Modifier.width(8.dp))

            androidx.compose.material.OutlinedTextField(modifier = Modifier.weight(1f),
                value = qs_tile_start_padding,
                singleLine = true,
                onValueChange = {
                    qs_tile_start_padding = it
                    qsShell.run("""sed -i 's/<dimen name="qs_tile_start_padding">[^<]*/<dimen name="qs_tile_start_padding">${it}dip/g' $qsPath/res/values/dimens.xml""")

                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("qs_tile_start_padding", Modifier.basicMarquee()) })

        }


        Row {
            androidx.compose.material.OutlinedTextField(modifier = Modifier.weight(1f),
                value = columnsPortrait,
                singleLine = true,
                onValueChange = {
                    columnsPortrait = it
                    qsShell.run("""sed -i 's/<integer name="quick_settings_num_columns">[^<]*/<integer name="quick_settings_num_columns">${it}/g' $qsPath/res/values/integers.xml""")
                    qsShell.run("""sed -i 's/<integer name="config_qs_columns_portrait">[^<]*/<integer name="config_qs_columns_portrait">${it}/g' $qsPath/res/values/integers.xml""")

                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = {
                    Text(
                        text = "columnsPortrait", Modifier.basicMarquee()
                    )
                })

            Spacer(modifier = Modifier.width(8.dp))

            androidx.compose.material.OutlinedTextField(modifier = Modifier.weight(1f),
                value = rowsPortrait,
                singleLine = true,
                onValueChange = {
                    rowsPortrait = it
                    qsShell.run("""sed -i 's/<integer name="config_qs_rows_portrait">[^<]*/<integer name="config_qs_rows_portrait">${it}/g' $qsPath/res/values/integers.xml""")
                    qsShell.run("""sed -i 's/<integer name="quick_settings_max_rows">[^<]*/<integer name="quick_settings_max_rows">${it}/g' $qsPath/res/values/integers.xml""")

                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("rowsPortrait", Modifier.basicMarquee()) })

        }


        Row {
            androidx.compose.material.OutlinedTextField(modifier = Modifier.weight(1f),
                value = columnsLandscape,
                singleLine = true,
                onValueChange = {
                    columnsLandscape = it
                    qsShell.run("""sed -i 's/<integer name="config_qs_columns_landscape">[^<]*/<integer name="config_qs_columns_landscape">${it}/g' $qsPath/res/values/integers.xml""")
                    qsShell.run("""sed -i 's/<integer name="quick_settings_num_columns">[^<]*/<integer name="quick_settings_num_columns">${it}/g' $qsPath/res/values-land/integers.xml""")

                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = {
                    Text(
                        text = "columnsLandscape", Modifier.basicMarquee()
                    )
                })

            Spacer(modifier = Modifier.width(8.dp))

            androidx.compose.material.OutlinedTextField(modifier = Modifier.weight(1f),
                value = rowsLandscape,
                singleLine = true,
                onValueChange = {
                    rowsLandscape = it
                    qsShell.run("""sed -i 's/<integer name="config_qs_rows_landscape">[^<]*/<integer name="config_qs_rows_landscape">${it}/g' $qsPath/res/values/integers.xml""")
                    qsShell.run("""sed -i 's/<integer name="quick_settings_max_rows">[^<]*/<integer name="quick_settings_max_rows">${it}/g' $qsPath/res/values-land/integers.xml""")

                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("rowsLandscape", Modifier.basicMarquee()) })

        }

        Row {
            androidx.compose.material.OutlinedTextField(modifier = Modifier.weight(1f),
                value = qsQuickTileSize,
                singleLine = true,
                onValueChange = {
                    qsQuickTileSize = it
                    qsShell.run("""sed -i 's/<dimen name="qs_quick_tile_size">[^<]*/<dimen name="qs_quick_tile_size">${it}dip/g' $qsPath/res/values/dimens.xml""")

                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = {
                    Text(
                        text = "qsQuickTileSize", Modifier.basicMarquee()
                    )
                })

            Spacer(modifier = Modifier.width(8.dp))

            androidx.compose.material.OutlinedTextField(modifier = Modifier.weight(1f),
                value = qsTileHeight,
                singleLine = true,
                onValueChange = {
                    qsTileHeight = it
                    qsShell.run("""sed -i 's/<dimen name="qs_tile_height">[^<]*/<dimen name="qs_tile_height">${it}dip/g' $qsPath/res/values/dimens.xml""")

                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("qsTileHeight", Modifier.basicMarquee()) })
        }



        Button(
            modifier = Modifier.fillMaxWidth(), onClick = {
                buildOverlay(qsPath)
                qsShell.run("""cmd overlay enable themed.qspanel.generic""")
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


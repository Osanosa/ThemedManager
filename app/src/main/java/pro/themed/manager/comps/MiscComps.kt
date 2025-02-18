package pro.themed.manager.comps

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.MainActivity.Companion.overlayList
import pro.themed.manager.R
import pro.themed.manager.components.AdmobBanner
import pro.themed.manager.components.CookieCard
import pro.themed.manager.components.HeaderRow
import pro.themed.manager.components.MiscTextField
import pro.themed.manager.components.Slideritem
import pro.themed.manager.ui.theme.background
import pro.themed.manager.utils.GlobalVariables

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
// @Preview
@Composable
fun MiscTab() {
    Surface(modifier = Modifier.fillMaxSize(), color = background) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Spacer(Modifier.height(32.dp))

            AdmobBanner()

            val cornersPath = "${GlobalVariables.modulePath}/onDemandCompiler/corners"
            val qsGridGenericPath = "${GlobalVariables.modulePath}/onDemandCompiler/qsGrid"

            var rounded_corner_radius by remember {
                mutableStateOf(
                    Shell("su")
                        .run(
                            """awk -F'[<>]' '/<dimen name="rounded_corner_radius">/ {print $3}' $cornersPath/res/values/dimens.xml | sed 's/dip//g'"""
                        )
                        .stdout()
                )
            }
            var config_qs_columns_landscape by remember {
                mutableStateOf(
                    Shell("su")
                        .run(
                            """awk -F'[<>]' '/<integer name="config_qs_columns_landscape">/ {print $3}' ${qsGridGenericPath}ColumnsLandscapeGeneric/res/values/integers.xml"""
                        )
                        .stdout()
                )
            }
            var config_qs_columns_portrait by remember {
                mutableStateOf(
                    Shell("su")
                        .run(
                            """awk -F'[<>]' '/<integer name="config_qs_columns_portrait">/ {print $3}' ${qsGridGenericPath}ColumnsPortraitGeneric/res/values/integers.xml"""
                        )
                        .stdout()
                )
            }
            var config_qs_rows_landscape by remember {
                mutableStateOf(
                    Shell("su")
                        .run(
                            """awk -F'[<>]' '/<integer name="config_qs_rows_landscape">/ {print $3}' ${qsGridGenericPath}RowsLandscapeGeneric/res/values/integers.xml"""
                        )
                        .stdout()
                )
            }
            var config_qs_rows_portrait by remember {
                mutableStateOf(
                    Shell("su")
                        .run(
                            """awk -F'[<>]' '/<integer name="config_qs_rows_landscape">/ {print $3}' ${qsGridGenericPath}RowsLandscapeGeneric/res/values/integers.xml"""
                        )
                        .stdout()
                )
            }

            LaunchedEffect(Unit) {}

            CookieCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.rounded_corner_48px),
                        null,
                        Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    MiscTextField(
                        input = rounded_corner_radius,
                        path = cornersPath,
                        resource = "rounded_corner_radius",
                        file = """dimens""",
                        overlay = """themed.corners.generic""",
                        modifier = Modifier.weight(1f),
                        label = "Rounded Corner Radius",
                    )
                }
            }
            CookieCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.view_week_48px),
                        null,
                        Modifier.padding(4.dp).size(24.dp),
                    )
                    Text("Qs Grid Columns", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.reset),
                        null,
                        Modifier.padding(4.dp).clip(CircleShape).size(24.dp).clickable {
                            Shell("su")
                                .run("cmd overlay disable themed.qsgrid.columnsportrait.generic")
                            Shell("su")
                                .run("cmd overlay disable themed.qsgrid.columnslandscape.generic")
                        },
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp),
                ) {

                    // columns portrait
                    MiscTextField(
                        input = config_qs_columns_portrait,
                        path = qsGridGenericPath + "ColumnsPortraitGeneric/",
                        resource = "config_qs_columns_portrait",
                        file = "integers",
                        overlay = "themed.qsgrid.columnsportrait.generic",
                        modifier = Modifier.weight(1f),
                        label = "Portrait",
                    )
                    Spacer(Modifier.width(8.dp))

                    // columns landscape
                    MiscTextField(
                        input = config_qs_columns_landscape,
                        path = qsGridGenericPath + "ColumnsLandscapeGeneric/",
                        resource = "config_qs_columns_landscape",
                        file = "integers",
                        overlay = "themed.qsgrid.columnslandscape.generic",
                        modifier = Modifier.weight(1f),
                        label = "Landscape",
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
            CookieCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.table_rows_48px),
                        null,
                        Modifier.padding(4.dp).size(24.dp),
                    )
                    Text("Qs Grid Rows", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.reset),
                        null,
                        Modifier.padding(4.dp).clip(CircleShape).size(24.dp).clickable {
                            Shell("su")
                                .run("cmd overlay disable themed.qsgrid.rowslandscape.generic")
                            Shell("su")
                                .run("cmd overlay disable themed.qsgrid.rowsportrait.generic")
                        },
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp),
                ) {

                    // rows portrait
                    MiscTextField(
                        input = config_qs_rows_portrait,
                        path = qsGridGenericPath + "RowsPortraitGeneric/",
                        resource = "config_qs_rows_portrait",
                        file = "integers",
                        overlay = "themed.qsgrid.rowsportrait.generic",
                        modifier = Modifier.weight(1f),
                        label = "Portrait",
                    )
                    Spacer(Modifier.width(8.dp))

                    // rows landscape
                    MiscTextField(
                        input = config_qs_rows_landscape,
                        path = qsGridGenericPath + "RowsLandscapeGeneric/",
                        resource = "config_qs_rows_landscape",
                        file = "integers",
                        overlay = "themed.qsgrid.rowslandscape.generic",
                        modifier = Modifier.weight(1f),
                        label = "Landscape",
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            CookieCard {
                Slideritem(
                    drawable = R.drawable.table_rows_48px,
                    header = stringResource(R.string.qsquicktilesize),
                    sliderSteps = 1,
                    sliderStepValue = 20,
                    minSliderValue = 60f,
                    maxSliderValue = 80f,
                    overlayName = "qsquicktilesize",
                )
                Slideritem(
                    drawable = R.drawable.table_rows_48px,
                    header = stringResource(R.string.qstileheight),
                    sliderSteps = 1,
                    sliderStepValue = 20,
                    minSliderValue = 60f,
                    maxSliderValue = 80f,
                    overlayName = "qstileheight",
                )
            }

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

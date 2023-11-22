@file:OptIn(
    ExperimentalMaterialApi::class
)

package pro.themed.manager.comps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.AdmobBanner
import pro.themed.manager.R
import pro.themed.manager.getOverlayList
import pro.themed.manager.overlayEnable
import pro.themed.manager.ui.theme.bordercol
import pro.themed.manager.ui.theme.cardcol

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun IconsTab() {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colors.cardcol
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 8.dp)) {
            AdmobBanner()

            NavbarCard()
            IconPackCard()

        }
    }

}


//@Preview
@Composable
fun NavbarCard() {

    Card(
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.bordercol),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        elevation = (0.dp),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.cardcol
    ) {
        val testdp = (LocalConfiguration.current.screenWidthDp - 16) / 8

        var expanded by remember { mutableStateOf(true) }
        Column(modifier = Modifier.clickable { expanded = !expanded }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .padding(start = 8.dp),
                    text = "Navbars",
                    fontSize = 24.sp
                )
                IconButton(onClick = {
                    Shell("su").run("for ol in \$(cmd overlay list | grep -E 'themed.navbar' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"$" + "ol\"; done")
                }) {
                    Image(
                          imageVector = ImageVector.vectorResource(id = R.drawable.reset), contentDescription = null
                    )
                }
            }

            Divider(thickness = 1.dp, color = MaterialTheme.colors.bordercol)
            AnimatedVisibility(expanded) {
                Surface {
                    Column {
                        Navbar(
                            testdp,
                            back = R.drawable.navbar_android_back,
                            home = R.drawable.navbar_android_home,
                            R.drawable.navbar_android_recent,
                            "android"
                        )
                        Navbar(
                            testdp,
                            back = R.drawable.navbar_asus_back,
                            home = R.drawable.navbar_asus_home,
                            R.drawable.navbar_asus_recent,
                            "asus"
                        )
                        Navbar(
                            testdp,
                            back = R.drawable.navbar_dora_back,
                            home = R.drawable.navbar_dora_home,
                            R.drawable.navbar_dora_recent,
                            "dora"
                        )
                        Navbar(
                            testdp,
                            back = R.drawable.navbar_moto_back,
                            home = R.drawable.navbar_moto_home,
                            R.drawable.navbar_moto_recent,
                            "moto"
                        )
                        Navbar(
                            testdp,
                            back = R.drawable.navbar_nexus_back,
                            home = R.drawable.navbar_nexus_home,
                            R.drawable.navbar_nexus_recent,
                            "nexus"
                        )
                        Navbar(
                            testdp,
                            back = R.drawable.navbar_old_back,
                            home = R.drawable.navbar_old_home,
                            R.drawable.navbar_old_recent,
                            "old"
                        )
                        Navbar(
                            testdp,
                            back = R.drawable.navbar_oneplus_back,
                            home = R.drawable.navbar_oneplus_home,
                            R.drawable.navbar_oneplus_recent,
                            "oneplus"
                        )
                        Navbar(
                            testdp,
                            back = R.drawable.navbar_sammy_back,
                            home = R.drawable.navbar_sammy_home,
                            R.drawable.navbar_sammy_recent,
                            "sammy"
                        )
                        Navbar(
                            testdp,
                            back = R.drawable.navbar_tecnocamon_back,
                            home = R.drawable.navbar_tecnocamon_home,
                            R.drawable.navbar_tecnocamon_recent,
                            "tecno"
                        )


                    }
                }
            }
        }
    }
}
@Stable
@Composable
private fun Navbar(testdp: Int, back: Int, home: Int, recent: Int, name: String) {
    val context = LocalContext.current

    if (getOverlayList().overlayList.any { it.contains(name) }) {
        Row(
            Modifier
                .clickable {
                    overlayEnable("navbar.$name")
                }
                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Image(
                imageVector = ImageVector.vectorResource(id = back), contentDescription = null, Modifier.size(testdp.dp)
            )
            Image(
                imageVector = ImageVector.vectorResource(id = home), contentDescription = null, Modifier.size(testdp.dp)
            )
            Image(
                imageVector = ImageVector.vectorResource(id = recent),
                contentDescription = null,
                Modifier.size(testdp.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun IconPackCard() {
    val context = LocalContext.current

    Card(
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.bordercol),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        elevation = (0.dp),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.cardcol
    ) {
        val testdp = (LocalConfiguration.current.screenWidthDp - 16) / 12

        var expanded by remember { mutableStateOf(true) }
        Column(modifier = Modifier.clickable { expanded = !expanded }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .padding(start = 8.dp),
                    text = "IconPacks",
                    fontSize = 24.sp
                )
                IconButton(onClick = {
                    Shell("su").run("for ol in \$(cmd overlay list | grep -E 'themed.iconpack' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"$" + "ol\"; done")
                }) {
                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.reset), contentDescription = null
                    )
                }
            }

            Divider(thickness = 1.dp, color = MaterialTheme.colors.bordercol)

            AnimatedVisibility(expanded) {
                Surface {
                    Column {
                        Surface(modifier = Modifier
                            .fillMaxWidth()
                            .height(testdp.dp + 8.dp)
                            .padding(2.dp), color = MaterialTheme.colors.cardcol, onClick = {
                            overlayEnable("iconpack.acherus.android")
                            overlayEnable("iconpack.acherus.systemui")
                        }) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Archeous", fontSize = 18.sp)
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_archerus_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_archerus_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_archerus_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_archerus_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_archerus_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_archerus_airplane),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                            }
                        }

                        Surface(modifier = Modifier
                            .fillMaxWidth()
                            .height(testdp.dp + 8.dp)
                            .padding(2.dp), color = MaterialTheme.colors.cardcol, onClick = {
                            overlayEnable("iconpack.circular.android")
                            overlayEnable("iconpack.circular.launcher")
                            overlayEnable("iconpack.circular.settings")
                            overlayEnable("iconpack.circular.systemui")
                            overlayEnable("iconpack.circular.themepicker")
                        }) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Circular   ", fontSize = 18.sp)
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_circular_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_circular_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_circular_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_circular_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_circular_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_circular_airplane),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                            }
                        }

                        Surface(modifier = Modifier
                            .fillMaxWidth()
                            .height(testdp.dp + 8.dp)
                            .padding(2.dp), color = MaterialTheme.colors.cardcol, onClick = {
                            overlayEnable("iconpack.filled.android")
                            overlayEnable("iconpack.filled.launcher")
                            overlayEnable("iconpack.filled.settings")
                            overlayEnable("iconpack.filled.systemui")
                            overlayEnable("iconpack.filled.themepicker")
                        }) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Filled       ", fontSize = 18.sp)
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_filled_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_filled_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_filled_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_filled_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_filled_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_filled_airplane),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(modifier = Modifier
                            .fillMaxWidth()
                            .height(testdp.dp + 8.dp)
                            .padding(2.dp), color = MaterialTheme.colors.cardcol, onClick = {
                            overlayEnable("iconpack.kai.android")
                            overlayEnable("iconpack.kai.launcher")
                            overlayEnable("iconpack.kai.settings")
                            overlayEnable("iconpack.kai.systemui")
                            overlayEnable("iconpack.kai.themepicker")
                        }) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Kai           ", fontSize = 18.sp)
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_kai_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_kai_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_kai_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_kai_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_kai_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_kai_airplane),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(modifier = Modifier
                            .fillMaxWidth()
                            .height(testdp.dp + 8.dp)
                            .padding(2.dp), color = MaterialTheme.colors.cardcol, onClick = {
                            overlayEnable("iconpack.outline.android")
                            overlayEnable("iconpack.outline.launcher")
                            overlayEnable("iconpack.outline.settings")
                            overlayEnable("iconpack.outline.systemui")
                            overlayEnable("iconpack.outline.themepicker")
                        }) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Outline    ", fontSize = 18.sp)
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_outline_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_outline_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_outline_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_outline_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_outline_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_outline_airplane),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(modifier = Modifier
                            .fillMaxWidth()
                            .height(testdp.dp + 8.dp)
                            .padding(2.dp), color = MaterialTheme.colors.cardcol, onClick = {
                            overlayEnable("iconpack.oos.android")
                            overlayEnable("iconpack.oos.launcher")
                            overlayEnable("iconpack.oos.settings")
                            overlayEnable("iconpack.oos.systemui")
                            overlayEnable("iconpack.oos.themepicker")
                        }) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "OOS         ", fontSize = 18.sp)
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_oos_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_oos_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_oos_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_oos_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_oos_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_oos_airplane),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(modifier = Modifier
                            .fillMaxWidth()
                            .height(testdp.dp + 8.dp)
                            .padding(2.dp), color = MaterialTheme.colors.cardcol, onClick = {
                            overlayEnable("iconpack.pui.android")
                            overlayEnable("iconpack.pui.launcher")
                            overlayEnable("iconpack.pui.settings")
                            overlayEnable("iconpack.pui.systemui")
                            overlayEnable("iconpack.pui.themepicker")
                        }) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "PUI           ", fontSize = 18.sp)
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_pui_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_pui_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_pui_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_pui_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_pui_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_pui_airplane),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                            }
                        }


                        Surface(modifier = Modifier
                            .fillMaxWidth()
                            .height(testdp.dp + 8.dp)
                            .padding(2.dp), color = MaterialTheme.colors.cardcol, onClick = {
                            overlayEnable("iconpack.rounded.android")
                            overlayEnable("iconpack.rounded.launcher")
                            overlayEnable("iconpack.rounded.settings")
                            overlayEnable("iconpack.rounded.systemui")
                            overlayEnable("iconpack.rounded.themepicker")
                        }) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Rounded ", fontSize = 18.sp)
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_rounded_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_rounded_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_rounded_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_rounded_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_rounded_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_rounded_airplane),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                            }
                        }


                        Surface(modifier = Modifier
                            .fillMaxWidth()
                            .height(testdp.dp + 8.dp)
                            .padding(2.dp), color = MaterialTheme.colors.cardcol, onClick = {
                            overlayEnable("iconpack.sam.android")
                            overlayEnable("iconpack.sam.launcher")
                            overlayEnable("iconpack.sam.settings")
                            overlayEnable("iconpack.sam.systemui")
                            overlayEnable("iconpack.sam.themepicker")
                        }) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Sam         ", fontSize = 18.sp)
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_sam_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_sam_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_sam_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_sam_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_sam_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_sam_airplane),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(modifier = Modifier
                            .fillMaxWidth()
                            .height(testdp.dp + 8.dp)
                            .padding(2.dp), color = MaterialTheme.colors.cardcol, onClick = {
                            overlayEnable("iconpack.victor.android")
                            overlayEnable("iconpack.victor.launcher")
                            overlayEnable("iconpack.victor.settings")
                            overlayEnable("iconpack.victor.systemui")
                            overlayEnable("iconpack.victor.themepicker")
                        }) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Victor      ", fontSize = 18.sp)
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_victor_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_victor_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_victor_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_victor_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_victor_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                      imageVector = ImageVector.vectorResource(id = R.drawable.iconpack_victor_airplane),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}



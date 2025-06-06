@file:OptIn()

package pro.themed.manager.comps

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.jaredrummler.ktsh.*
import pro.themed.manager.*
import pro.themed.manager.R
import pro.themed.manager.components.*
import pro.themed.manager.ui.theme.*
import pro.themed.manager.utils.*

@Composable
fun IconsTab() {
    Surface(modifier = Modifier.fillMaxSize(), color = background) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Spacer(Modifier.height(16.dp))
            AdmobBanner()

            CookieCard { NavbarCard() }

            IconPackCard()
        }
    }
}

// @Preview
@Composable
fun NavbarCard() {

    val testdp = (LocalConfiguration.current.screenWidthDp - 16) / 8

    var expanded by remember { mutableStateOf(true) }
    Column(modifier = Modifier.clickable { expanded = !expanded }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier.padding(8.dp).padding(start = 8.dp),
                text = "Navbars",
                fontSize = 24.sp,
            )
            IconButton(
                onClick = {
                    Shell("su")
                        .run(
                            "for ol in \$(cmd overlay list | grep -E 'themed.navbar' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"$" +
                                "ol\"; done"
                        )
                }
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.reset),
                    contentDescription = null,
                )
            }
        }

        HorizontalDivider(thickness = 1.dp, color = bordercol)
        AnimatedVisibility(expanded) {
            Column {
                Navbar(
                    testdp,
                    back = R.drawable.navbar_android_back,
                    home = R.drawable.navbar_android_home,
                    R.drawable.navbar_android_recent,
                    "android",
                )
                Navbar(
                    testdp,
                    back = R.drawable.navbar_asus_back,
                    home = R.drawable.navbar_asus_home,
                    R.drawable.navbar_asus_recent,
                    "asus",
                )
                Navbar(
                    testdp,
                    back = R.drawable.navbar_dora_back,
                    home = R.drawable.navbar_dora_home,
                    R.drawable.navbar_dora_recent,
                    "dora",
                )
                Navbar(
                    testdp,
                    back = R.drawable.navbar_moto_back,
                    home = R.drawable.navbar_moto_home,
                    R.drawable.navbar_moto_recent,
                    "moto",
                )
                Navbar(
                    testdp,
                    back = R.drawable.navbar_nexus_back,
                    home = R.drawable.navbar_nexus_home,
                    R.drawable.navbar_nexus_recent,
                    "nexus",
                )
                Navbar(
                    testdp,
                    back = R.drawable.navbar_old_back,
                    home = R.drawable.navbar_old_home,
                    R.drawable.navbar_old_recent,
                    "old",
                )
                Navbar(
                    testdp,
                    back = R.drawable.navbar_oneplus_back,
                    home = R.drawable.navbar_oneplus_home,
                    R.drawable.navbar_oneplus_recent,
                    "oneplus",
                )
                Navbar(
                    testdp,
                    back = R.drawable.navbar_sammy_back,
                    home = R.drawable.navbar_sammy_home,
                    R.drawable.navbar_sammy_recent,
                    "sammy",
                )
                Navbar(
                    testdp,
                    back = R.drawable.navbar_tecnocamon_back,
                    home = R.drawable.navbar_tecnocamon_home,
                    R.drawable.navbar_tecnocamon_recent,
                    "tecno",
                )
            }
        }
    }
}

@Stable
@Composable
private fun Navbar(testdp: Int, back: Int, home: Int, recent: Int, name: String) {
    val context = LocalContext.current

    if (MainActivity.overlayList.overlayList.any { it.contains(name) }) {
        Row(
            Modifier.clickable { overlayEnable("navbar.$name") }.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id = back),
                contentDescription = null,
                Modifier.size(testdp.dp),
            )
            Image(
                imageVector = ImageVector.vectorResource(id = home),
                contentDescription = null,
                Modifier.size(testdp.dp),
            )
            Image(
                imageVector = ImageVector.vectorResource(id = recent),
                contentDescription = null,
                Modifier.size(testdp.dp),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IconPackCard() {
    val context = LocalContext.current

    val testdp = (LocalConfiguration.current.screenWidthDp - 16) / 12

    var expanded by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier.clickable { expanded = !expanded },
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier.padding(8.dp).padding(start = 8.dp),
                text = "IconPacks",
                fontSize = 24.sp,
            )
            IconButton(
                onClick = {
                    Shell("su")
                        .run(
                            "for ol in \$(cmd overlay list | grep -E 'themed.iconpack' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"$" +
                                "ol\"; done"
                        )
                }
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.reset),
                    contentDescription = null,
                )
            }
        }

        HorizontalDivider(thickness = 1.dp, color = bordercol)

        AnimatedVisibility(expanded) {
            @Composable
            fun IconPackRow(label: String, overlayCommands: List<String>, iconResIds: List<Int>) {
                CookieCard(onClick = { overlayCommands.forEach { overlayEnable(it) } }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                        )
                        HorizontalDivider(
                            Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 2.dp)
                        )
                        FlowRow(
                            maxItemsInEachRow = 3,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        ) {
                            iconResIds.forEach { resId ->
                                Image(
                                    imageVector = ImageVector.vectorResource(id = resId),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp),
                                )
                            }
                        }
                    }
                }
            }

            val iconPacks =
                listOf(
                    Triple(
                        "Archeous",
                        listOf("iconpack.acherus.android", "iconpack.acherus.systemui"),
                        listOf(
                            R.drawable.iconpack_archerus_wifi_signal_3,
                            R.drawable.iconpack_archerus_bluetooth_transient_animation,
                            R.drawable.iconpack_archerus_dnd,
                            R.drawable.iconpack_archerus_flashlight,
                            R.drawable.iconpack_archerus_auto_rotate,
                            R.drawable.iconpack_archerus_airplane,
                        ),
                    ),
                    Triple(
                        "Circular",
                        listOf(
                            "iconpack.circular.android",
                            "iconpack.circular.launcher",
                            "iconpack.circular.settings",
                            "iconpack.circular.systemui",
                            "iconpack.circular.themepicker",
                        ),
                        listOf(
                            R.drawable.iconpack_circular_wifi_signal_3,
                            R.drawable.iconpack_circular_bluetooth_transient_animation,
                            R.drawable.iconpack_circular_dnd,
                            R.drawable.iconpack_circular_flashlight,
                            R.drawable.iconpack_circular_auto_rotate,
                            R.drawable.iconpack_circular_airplane,
                        ),
                    ),
                    Triple(
                        "Filled",
                        listOf(
                            "iconpack.filled.android",
                            "iconpack.filled.launcher",
                            "iconpack.filled.settings",
                            "iconpack.filled.systemui",
                            "iconpack.filled.themepicker",
                        ),
                        listOf(
                            R.drawable.iconpack_filled_wifi_signal_3,
                            R.drawable.iconpack_filled_bluetooth_transient_animation,
                            R.drawable.iconpack_filled_dnd,
                            R.drawable.iconpack_filled_flashlight,
                            R.drawable.iconpack_filled_auto_rotate,
                            R.drawable.iconpack_filled_airplane,
                        ),
                    ),
                    Triple(
                        "Kai",
                        listOf(
                            "iconpack.kai.android",
                            "iconpack.kai.launcher",
                            "iconpack.kai.settings",
                            "iconpack.kai.systemui",
                            "iconpack.kai.themepicker",
                        ),
                        listOf(
                            R.drawable.iconpack_kai_wifi_signal_3,
                            R.drawable.iconpack_kai_bluetooth_transient_animation,
                            R.drawable.iconpack_kai_dnd,
                            R.drawable.iconpack_kai_flashlight,
                            R.drawable.iconpack_kai_auto_rotate,
                            R.drawable.iconpack_kai_airplane,
                        ),
                    ),
                    Triple(
                        "Outline",
                        listOf(
                            "iconpack.outline.android",
                            "iconpack.outline.launcher",
                            "iconpack.outline.settings",
                            "iconpack.outline.systemui",
                            "iconpack.outline.themepicker",
                        ),
                        listOf(
                            R.drawable.iconpack_outline_wifi_signal_3,
                            R.drawable.iconpack_outline_bluetooth_transient_animation,
                            R.drawable.iconpack_outline_dnd,
                            R.drawable.iconpack_outline_flashlight,
                            R.drawable.iconpack_outline_auto_rotate,
                            R.drawable.iconpack_outline_airplane,
                        ),
                    ),
                    Triple(
                        "OOS",
                        listOf(
                            "iconpack.oos.android",
                            "iconpack.oos.launcher",
                            "iconpack.oos.settings",
                            "iconpack.oos.systemui",
                            "iconpack.oos.themepicker",
                        ),
                        listOf(
                            R.drawable.iconpack_oos_wifi_signal_3,
                            R.drawable.iconpack_oos_bluetooth_transient_animation,
                            R.drawable.iconpack_oos_dnd,
                            R.drawable.iconpack_oos_flashlight,
                            R.drawable.iconpack_oos_auto_rotate,
                            R.drawable.iconpack_oos_airplane,
                        ),
                    ),
                    Triple(
                        "PUI",
                        listOf(
                            "iconpack.pui.android",
                            "iconpack.pui.launcher",
                            "iconpack.pui.settings",
                            "iconpack.pui.systemui",
                            "iconpack.pui.themepicker",
                        ),
                        listOf(
                            R.drawable.iconpack_pui_wifi_signal_3,
                            R.drawable.iconpack_pui_bluetooth_transient_animation,
                            R.drawable.iconpack_pui_dnd,
                            R.drawable.iconpack_pui_flashlight,
                            R.drawable.iconpack_pui_auto_rotate,
                            R.drawable.iconpack_pui_airplane,
                        ),
                    ),
                    Triple(
                        "Rounded ",
                        listOf(
                            "iconpack.rounded.android",
                            "iconpack.rounded.launcher",
                            "iconpack.rounded.settings",
                            "iconpack.rounded.systemui",
                            "iconpack.rounded.themepicker",
                        ),
                        listOf(
                            R.drawable.iconpack_rounded_wifi_signal_3,
                            R.drawable.iconpack_rounded_bluetooth_transient_animation,
                            R.drawable.iconpack_rounded_dnd,
                            R.drawable.iconpack_rounded_flashlight,
                            R.drawable.iconpack_rounded_auto_rotate,
                            R.drawable.iconpack_rounded_airplane,
                        ),
                    ),
                    Triple(
                        "Sam",
                        listOf(
                            "iconpack.sam.android",
                            "iconpack.sam.launcher",
                            "iconpack.sam.settings",
                            "iconpack.sam.systemui",
                            "iconpack.sam.themepicker",
                        ),
                        listOf(
                            R.drawable.iconpack_sam_wifi_signal_3,
                            R.drawable.iconpack_sam_bluetooth_transient_animation,
                            R.drawable.iconpack_sam_dnd,
                            R.drawable.iconpack_sam_flashlight,
                            R.drawable.iconpack_sam_auto_rotate,
                            R.drawable.iconpack_sam_airplane,
                        ),
                    ),
                    Triple(
                        "Victor",
                        listOf(
                            "iconpack.victor.android",
                            "iconpack.victor.launcher",
                            "iconpack.victor.settings",
                            "iconpack.victor.systemui",
                            "iconpack.victor.themepicker",
                        ),
                        listOf(
                            R.drawable.iconpack_victor_wifi_signal_3,
                            R.drawable.iconpack_victor_bluetooth_transient_animation,
                            R.drawable.iconpack_victor_dnd,
                            R.drawable.iconpack_victor_flashlight,
                            R.drawable.iconpack_victor_auto_rotate,
                            R.drawable.iconpack_victor_airplane,
                        ),
                    ),
                )
            Row {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    iconPacks.forEachIndexed { index, (label, commands, icons) ->
                        if (index % 2 == 0) IconPackRow(label, commands, icons)
                    }
                }
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    iconPacks.forEachIndexed { index, (label, commands, icons) ->
                        if (index % 2 == 1) IconPackRow(label, commands, icons)
                    }
                }
            }
        }
    }
}

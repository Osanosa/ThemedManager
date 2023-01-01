@file:Suppress("OPT_IN_IS_NOT_ENABLED") @file:OptIn(
    ExperimentalMaterialApi::class, ExperimentalPagerApi::class
)
package pro.themed.manager.comps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.*
import pro.themed.manager.R

@Composable
fun IconsTab() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp),
        color = MaterialTheme.colors.cardcol
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            QSTileCard()
            NavbarCard()
            IconPackCard()
            InfoCard()

        }
    }

}


@Preview
@Composable
fun QsPanel() {
    HorizontalPager(count = 10) {

    }

}
@Composable
fun QSTileCard() {

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
        val testdp = (LocalConfiguration.current.screenWidthDp - 16) / 6

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
                    text = "QSTiles",
                    fontSize = 24.sp
                )
                IconButton(onClick = {
                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'themed.qstile' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"$" + "ol\"; done")
                }) {
                    Image(
                        painter = painterResource(R.drawable.reset),
                        contentDescription = null
                    )
                }
            }

            Divider(thickness = 1.dp, color = MaterialTheme.colors.bordercol)
            AnimatedVisibility(expanded) {
                Surface {
                    Column {
                        Row {
                            MyIconButton(
                                overlayname = "qstile.dualtonecircle",
                                sizedp = testdp,
                                contentdescription = "Circle with Dual Tone",
                                iconname = R.drawable.qscirclewithdualtone
                            )
                            MyIconButton(
                                overlayname = "qstile.circlegradient",
                                sizedp = testdp,
                                contentdescription = "Circle with Gradient",
                                iconname = R.drawable.qscirclewithgradient
                            )

                            MyIconButton(
                                overlayname = "qstile.circletrim",
                                sizedp = testdp,
                                contentdescription = "Circle with Trim",
                                iconname = R.drawable.qscirclewithtrim
                            )
                            MyIconButton(
                                overlayname = "qstile.cookie",
                                sizedp = testdp,
                                contentdescription = "Cookie",
                                iconname = R.drawable.qscookie
                            )
                            MyIconButton(
                                overlayname = "qstile.cosmos",
                                sizedp = testdp,
                                contentdescription = "Cosmos",
                                iconname = R.drawable.qscosmos
                            )
                            MyIconButton(
                                overlayname = "qstile.default",
                                sizedp = testdp,
                                contentdescription = "Default",
                                iconname = R.drawable.qsdefault
                            )

                        }
                        Row {
                            MyIconButton(
                                overlayname = "qstile.dividedcircle",
                                sizedp = testdp,
                                contentdescription = "Divided Circle",
                                iconname = R.drawable.qsdividedcircle
                            )
                            MyIconButton(
                                overlayname = "qstile.dottedcircle",
                                sizedp = testdp,
                                contentdescription = "Dotted Circle",
                                iconname = R.drawable.qsdottedcircle
                            )
                            MyIconButton(
                                overlayname = "qstile.dualtonecircletrim",
                                sizedp = testdp,
                                contentdescription = "DualTone Circle with Trim",
                                iconname = R.drawable.qsdualtonecircletrim
                            )
                            MyIconButton(
                                overlayname = "qstile.ink",
                                sizedp = testdp,
                                contentdescription = "Ink",
                                iconname = R.drawable.qsink
                            )
                            MyIconButton(
                                overlayname = "qstile.inkdrop",
                                sizedp = testdp,
                                contentdescription = "Inkdrop",
                                iconname = R.drawable.qsinkdrop
                            )
                            MyIconButton(
                                overlayname = "qstile.justicons",
                                sizedp = testdp,
                                contentdescription = "Just Icons",
                                iconname = R.drawable.qsjusticons
                            )


                        }
                        Row {
                            MyIconButton(
                                overlayname = "qstile.mountain",
                                sizedp = testdp,
                                contentdescription = "Mountain",
                                iconname = R.drawable.qsmountain
                            )
                            MyIconButton(
                                overlayname = "qstile.neonlike",
                                sizedp = testdp,
                                contentdescription = "NeonLike",
                                iconname = R.drawable.qsneonlike
                            )
                            MyIconButton(
                                overlayname = "qstile.ninja",
                                sizedp = testdp,
                                contentdescription = "Ninja",
                                iconname = R.drawable.qsninja
                            )
                            MyIconButton(
                                overlayname = "qstile.oreocircletrim",
                                sizedp = testdp,
                                contentdescription = "Oreo (Circle Trim)",
                                iconname = R.drawable.qsoreocircletrim
                            )
                            MyIconButton(
                                overlayname = "qstile.oreosquircletrim",
                                sizedp = testdp,
                                contentdescription = "Oreo (Squircle Trim)",
                                iconname = R.drawable.qsoreosquircletrim
                            )
                            MyIconButton(
                                overlayname = "qstile.pokesign",
                                sizedp = testdp,
                                contentdescription = "Pokesign",
                                iconname = R.drawable.qspokesign
                            )

                        }
                        Row {
                            IconButton(
                                onClick = { overlayEnable("qstile.squaremedo") },
                                modifier = Modifier
                                    .size(testdp.dp)
                                    .background(color = MaterialTheme.colors.cardcol)
                            ) {
                                Image(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    painter = painterResource(R.drawable.qssquaremedo),
                                    contentDescription = "Squaremedo"
                                )
                            }
                            IconButton(
                                onClick = { overlayEnable("qstile.squircle") },
                                modifier = Modifier
                                    .size(testdp.dp)
                                    .background(color = MaterialTheme.colors.cardcol)
                            ) {
                                Image(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    painter = painterResource(R.drawable.qssquircle),
                                    contentDescription = "Squircle"
                                )
                            }
                            IconButton(
                                onClick = { overlayEnable("qstile.squircletrim") },
                                modifier = Modifier
                                    .size(testdp.dp)
                                    .background(color = MaterialTheme.colors.cardcol)
                            ) {
                                Image(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    painter = painterResource(R.drawable.qssquircletrim),
                                    contentDescription = "Squircle with trim",
                                )
                            }
                            IconButton(
                                onClick = { overlayEnable("qstile.teardrop") },
                                modifier = Modifier
                                    .size(testdp.dp)
                                    .background(color = MaterialTheme.colors.cardcol)
                            ) {
                                Image(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    painter = painterResource(R.drawable.qsteardrop),
                                    contentDescription = "TearDrop",
                                )
                            }
                            IconButton(
                                onClick = { overlayEnable("qstile.triangle") },
                                modifier = Modifier
                                    .size(testdp.dp)
                                    .background(color = MaterialTheme.colors.cardcol)
                            ) {
                                Image(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    painter = painterResource(R.drawable.qstriangle),
                                    contentDescription = "Triangle",
                                )
                            }
                            IconButton(
                                onClick = { overlayEnable("qstile.wavey") },
                                modifier = Modifier
                                    .size(testdp.dp)
                                    .background(color = MaterialTheme.colors.cardcol)
                            ) {
                                Image(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    painter = painterResource(R.drawable.qswavey),
                                    contentDescription = "Wavey",
                                )
                            }

                        }
                    }
                }
            }
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
                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'themed.navbar' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"$" + "ol\"; done")
                }) {
                    Image(
                        painter = painterResource(R.drawable.reset),
                        contentDescription = null
                    )
                }
            }

            Divider(thickness = 1.dp, color = MaterialTheme.colors.bordercol)
            AnimatedVisibility(expanded) {
                Surface {
                    Column {
                        Surface(modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
                                overlayEnable("navbar.android")
                            }) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Image(
                                    painter = painterResource(R.drawable.navbar_android_back),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_android_home),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_android_recent),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
                                overlayEnable("navbar.asus")
                            }) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Image(
                                    painter = painterResource(R.drawable.navbar_asus_back),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_asus_home),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_asus_recent),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
                                overlayEnable("navbar.dora")
                            }) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Image(
                                    painter = painterResource(R.drawable.navbar_dora_back),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_dora_home),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_dora_recent),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                            }
                        }

                        Surface(modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
                                overlayEnable("navbar.moto")
                            }) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Image(
                                    painter = painterResource(R.drawable.navbar_moto_back),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_moto_home),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_moto_recent),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
                                overlayEnable("navbar.nexus")
                            }) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Image(
                                    painter = painterResource(R.drawable.navbar_nexus_back),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_nexus_home),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_nexus_recent),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
                                overlayEnable("navbar.old")
                            }) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Image(
                                    painter = painterResource(R.drawable.navbar_old_back),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_old_home),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_old_recent),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
                                overlayEnable("navbar.oneplus")
                            }) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Image(
                                    painter = painterResource(R.drawable.navbar_oneplus_back),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_oneplus_home),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_oneplus_recent),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
                                overlayEnable("navbar.sammy")
                            }) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Image(
                                    painter = painterResource(R.drawable.navbar_sammy_back),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_sammy_home),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_sammy_recent),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
                                overlayEnable("navbar.tecno")
                            }) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Image(
                                    painter = painterResource(R.drawable.navbar_tecnocamon_back),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_tecnocamon_home),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_tecnocamon_recent),
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

//@Preview
@Composable
fun IconPackCard() {

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
                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'themed.iconpack' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"$" + "ol\"; done")
                }) {
                    Image(
                        painter = painterResource(R.drawable.reset),
                        contentDescription = null
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
                                    painter = painterResource(R.drawable.iconpack_archerus_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_archerus_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_archerus_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_archerus_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_archerus_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_archerus_airplane),
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
                                    painter = painterResource(R.drawable.iconpack_circular_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_circular_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_circular_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_circular_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_circular_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_circular_airplane),
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
                                    painter = painterResource(R.drawable.iconpack_filled_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_filled_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_filled_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_filled_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_filled_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_filled_airplane),
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
                                    painter = painterResource(R.drawable.iconpack_kai_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_kai_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_kai_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_kai_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_kai_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_kai_airplane),
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
                                    painter = painterResource(R.drawable.iconpack_outline_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_outline_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_outline_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_outline_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_outline_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_outline_airplane),
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
                                    painter = painterResource(R.drawable.iconpack_oos_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_oos_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_oos_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_oos_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_oos_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_oos_airplane),
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
                                    painter = painterResource(R.drawable.iconpack_pui_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_pui_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_pui_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_pui_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_pui_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_pui_airplane),
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
                                    painter = painterResource(R.drawable.iconpack_rounded_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_rounded_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_rounded_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_rounded_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_rounded_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_rounded_airplane),
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
                                    painter = painterResource(R.drawable.iconpack_sam_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_sam_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_sam_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_sam_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_sam_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_sam_airplane),
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
                                    painter = painterResource(R.drawable.iconpack_victor_wifi_signal_3),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_victor_bluetooth_transient_animation),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_victor_dnd),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_victor_flashlight),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_victor_auto_rotate),
                                    contentDescription = null,
                                    Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_victor_airplane),
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

@file:Suppress("OPT_IN_IS_NOT_ENABLED") @file:OptIn(
    ExperimentalMaterialApi::class, ExperimentalMaterialApi::class,

    )

package pro.themed.manager

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color.rgb
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jaredrummler.ktsh.Shell.Companion.SU
import pro.themed.manager.ui.theme.*
import kotlin.math.roundToInt

@get:Composable
val Colors.bordercol: Color
    get() = if (isLight) borderLight else borderDark
val Colors.cardcol: Color
    get() = if (isLight) backgroundLight else backgroundDark
val Colors.textcol: Color
    get() = if (isLight) backgroundDark else backgroundLight


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemedManagerTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.cardcol
                ) {
                    Main()
                }
            }
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")

    //@Preview
    @Composable
    fun Main() {
        Column() {


            val navController = rememberNavController()

            Scaffold(backgroundColor = MaterialTheme.colors.cardcol,
                topBar = { TopAppBar() },
                bottomBar = { BottomNavigationBar(navController) }) {
                Box {
                    PaddingValues(bottom = 200.dp)
                    Navigation(navController)
                }
            }
            //ColorsTab()
        }
    }
}

private operator fun Navigation.invoke() {

}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItems.ColorsTab, NavigationItems.IconsTab, NavigationItems.MiscTab
    )
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.cardcol,
        contentColor = MaterialTheme.colors.textcol,
        elevation = 0.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { items ->
            BottomNavigationItem(icon = {
                Icon(
                    painter = painterResource(id = items.icon), contentDescription = items.title
                )
            },
                label = { Text(text = items.title) },
                selectedContentColor = MaterialTheme.colors.textcol,
                unselectedContentColor = MaterialTheme.colors.textcol.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == items.route,
                onClick = {
                    navController.navigate(items.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route = route) {
                                saveState = true
                            }
                        }

                        launchSingleTop = true
                        restoreState = true
                    }

                })
        }
    }

}

@Composable
fun Navigation(navController: NavHostController) {

    NavHost(navController, startDestination = NavigationItems.ColorsTab.route) {

        composable(NavigationItems.ColorsTab.route) {
            ColorsTab()
        }

        composable(NavigationItems.IconsTab.route) {
            IconsTab()
        }
        composable(NavigationItems.MiscTab.route){
            MiscTab()
        }
    }

}

//@Preview
@Composable
fun InfoCard() {
    Card(
        border = BorderStroke(
            width = 1.dp, color = MaterialTheme.colors.bordercol
        ),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
            .padding(top = 0.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = (0.dp),
        backgroundColor = MaterialTheme.colors.cardcol
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = stringResource(R.string.infocard),
            fontSize = 14.sp
        )
    }
}

//@Preview()
@Composable
fun TopAppBar() {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    TopAppBar(elevation = 0.dp,
        title = { Text("Themed Manager") },
        backgroundColor = MaterialTheme.colors.cardcol,
        actions = {
            IconButton(onClick = {
                context.startActivity(Intent(context, SettingsActivity::class.java))

            }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }

            IconButton(onClick = {
                context.startActivity(Intent(context, AboutActivity::class.java))
            }) {
                Icon(Icons.Default.Info, contentDescription = "list")
            }

            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                val webIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.t.me/ThemedSupport"))
                val webIntent1 = Intent(
                    Intent.ACTION_VIEW, Uri.parse("https://www.github.com/Osanosa/ThemedProject/")
                )
                val webIntent2 = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themed.pro/"))

                DropdownMenuItem(onClick = { context.startActivity(webIntent) }) {
                    Image(
                        painter = painterResource(R.drawable.telegram_svgrepo_com),
                        contentDescription = "Telegram support group"
                    )
                }

                DropdownMenuItem(onClick = { context.startActivity(webIntent1) }) {
                    Row {
                        Image(
                            painter = painterResource(R.drawable.iconmonstr_github_1),
                            contentDescription = null
                        )
                        Text(text = "GitHub")
                    }
                }


                DropdownMenuItem(onClick = { context.startActivity(webIntent2) }) {
                    Image(
                        painter = painterResource(R.drawable.baseline_language_24),
                        contentDescription = null
                    )
                }
            }
        })
}


fun resetAccents() {
    SU.run("cmd overlay disable themed.accents.MaterialPinkA700")
    SU.run("cmd overlay disable themed.accents.MaterialPink500")
    SU.run("cmd overlay disable themed.accents.MaterialPinkA200")
    SU.run("cmd overlay disable themed.accents.MaterialPinkA400")
    SU.run("cmd overlay disable themed.accents.MaterialRedA700")
    SU.run("cmd overlay disable themed.accents.MaterialRed500")
    SU.run("cmd overlay disable themed.accents.MaterialRedA200")
    SU.run("cmd overlay disable themed.accents.MaterialRedA400")
    SU.run("cmd overlay disable themed.accents.MaterialDeepOrangeA700")
    SU.run("cmd overlay disable themed.accents.MaterialDeepOrange500")
    SU.run("cmd overlay disable themed.accents.MaterialDeepOrangeA200")
    SU.run("cmd overlay disable themed.accents.MaterialDeepOrangeA400")
    SU.run("cmd overlay disable themed.accents.MaterialOrangeA700")
    SU.run("cmd overlay disable themed.accents.MaterialOrange500")
    SU.run("cmd overlay disable themed.accents.MaterialOrangeA200")
    SU.run("cmd overlay disable themed.accents.MaterialOrangeA400")
    SU.run("cmd overlay disable themed.accents.MaterialAmberA700")
    SU.run("cmd overlay disable themed.accents.MaterialAmber500")
    SU.run("cmd overlay disable themed.accents.MaterialAmberA200")
    SU.run("cmd overlay disable themed.accents.MaterialAmberA400")
    SU.run("cmd overlay disable themed.accents.MaterialYellowA700")
    SU.run("cmd overlay disable themed.accents.MaterialYellow500")
    SU.run("cmd overlay disable themed.accents.MaterialYellowA200")
    SU.run("cmd overlay disable themed.accents.MaterialYellowA400")
    SU.run("cmd overlay disable themed.accents.MaterialLimeA700")
    SU.run("cmd overlay disable themed.accents.MaterialLime500")
    SU.run("cmd overlay disable themed.accents.MaterialLimeA200")
    SU.run("cmd overlay disable themed.accents.MaterialLimeA400")
    SU.run("cmd overlay disable themed.accents.MaterialLightGreenA700")
    SU.run("cmd overlay disable themed.accents.MaterialLightGreen500")
    SU.run("cmd overlay disable themed.accents.MaterialLightGreenA200")
    SU.run("cmd overlay disable themed.accents.MaterialLightGreenA400")
    SU.run("cmd overlay disable themed.accents.MaterialGreenA700")
    SU.run("cmd overlay disable themed.accents.MaterialGreen500")
    SU.run("cmd overlay disable themed.accents.MaterialGreenA200")
    SU.run("cmd overlay disable themed.accents.MaterialGreenA400")
    SU.run("cmd overlay disable themed.accents.MaterialTealA700")
    SU.run("cmd overlay disable themed.accents.MaterialTeal500")
    SU.run("cmd overlay disable themed.accents.MaterialTealA200")
    SU.run("cmd overlay disable themed.accents.MaterialTealA400")
    SU.run("cmd overlay disable themed.accents.MaterialCyanA700")
    SU.run("cmd overlay disable themed.accents.MaterialCyan500")
    SU.run("cmd overlay disable themed.accents.MaterialCyanA200")
    SU.run("cmd overlay disable themed.accents.MaterialCyanA400")
    SU.run("cmd overlay disable themed.accents.MaterialLightBlueA700")
    SU.run("cmd overlay disable themed.accents.MaterialLightBlue500")
    SU.run("cmd overlay disable themed.accents.MaterialLightBlueA200")
    SU.run("cmd overlay disable themed.accents.MaterialLightBlueA400")
    SU.run("cmd overlay disable themed.accents.MaterialBlueA700")
    SU.run("cmd overlay disable themed.accents.MaterialBlue500")
    SU.run("cmd overlay disable themed.accents.MaterialBlueA200")
    SU.run("cmd overlay disable themed.accents.MaterialBlueA400")
    SU.run("cmd overlay disable themed.accents.MaterialIndigoA700")
    SU.run("cmd overlay disable themed.accents.MaterialIndigo500")
    SU.run("cmd overlay disable themed.accents.MaterialIndigoA200")
    SU.run("cmd overlay disable themed.accents.MaterialIndigoA400")
    SU.run("cmd overlay disable themed.accents.MaterialDeepPurpleA700")
    SU.run("cmd overlay disable themed.accents.MaterialDeepPurple500")
    SU.run("cmd overlay disable themed.accents.MaterialDeepPurpleA200")
    SU.run("cmd overlay disable themed.accents.MaterialDeepPurpleA400")
    SU.run("cmd overlay disable themed.accents.MaterialPurpleA700")
    SU.run("cmd overlay disable themed.accents.MaterialPurple500")
    SU.run("cmd overlay disable themed.accents.MaterialPurpleA200")
    SU.run("cmd overlay disable themed.accents.MaterialPurpleA400")

}

fun resetAccentsDark() {

    SU.run("cmd overlay disable themed.accents.dark.MaterialPinkA700")
    SU.run("cmd overlay disable themed.accents.dark.MaterialPink500")
    SU.run("cmd overlay disable themed.accents.dark.MaterialPinkA200")
    SU.run("cmd overlay disable themed.accents.dark.MaterialPinkA400")
    SU.run("cmd overlay disable themed.accents.dark.MaterialRedA700")
    SU.run("cmd overlay disable themed.accents.dark.MaterialRed500")
    SU.run("cmd overlay disable themed.accents.dark.MaterialRedA200")
    SU.run("cmd overlay disable themed.accents.dark.MaterialRedA400")
    SU.run("cmd overlay disable themed.accents.dark.MaterialDeepOrangeA700")
    SU.run("cmd overlay disable themed.accents.dark.MaterialDeepOrange500")
    SU.run("cmd overlay disable themed.accents.dark.MaterialDeepOrangeA200")
    SU.run("cmd overlay disable themed.accents.dark.MaterialDeepOrangeA400")
    SU.run("cmd overlay disable themed.accents.dark.MaterialOrangeA700")
    SU.run("cmd overlay disable themed.accents.dark.MaterialOrange500")
    SU.run("cmd overlay disable themed.accents.dark.MaterialOrangeA200")
    SU.run("cmd overlay disable themed.accents.dark.MaterialOrangeA400")
    SU.run("cmd overlay disable themed.accents.dark.MaterialAmberA700")
    SU.run("cmd overlay disable themed.accents.dark.MaterialAmber500")
    SU.run("cmd overlay disable themed.accents.dark.MaterialAmberA200")
    SU.run("cmd overlay disable themed.accents.dark.MaterialAmberA400")
    SU.run("cmd overlay disable themed.accents.dark.MaterialYellowA700")
    SU.run("cmd overlay disable themed.accents.dark.MaterialYellow500")
    SU.run("cmd overlay disable themed.accents.dark.MaterialYellowA200")
    SU.run("cmd overlay disable themed.accents.dark.MaterialYellowA400")
    SU.run("cmd overlay disable themed.accents.dark.MaterialLimeA700")
    SU.run("cmd overlay disable themed.accents.dark.MaterialLime500")
    SU.run("cmd overlay disable themed.accents.dark.MaterialLimeA200")
    SU.run("cmd overlay disable themed.accents.dark.MaterialLimeA400")
    SU.run("cmd overlay disable themed.accents.dark.MaterialLightGreenA700")
    SU.run("cmd overlay disable themed.accents.dark.MaterialLightGreen500")
    SU.run("cmd overlay disable themed.accents.dark.MaterialLightGreenA200")
    SU.run("cmd overlay disable themed.accents.dark.MaterialLightGreenA400")
    SU.run("cmd overlay disable themed.accents.dark.MaterialGreenA700")
    SU.run("cmd overlay disable themed.accents.dark.MaterialGreen500")
    SU.run("cmd overlay disable themed.accents.dark.MaterialGreenA200")
    SU.run("cmd overlay disable themed.accents.dark.MaterialGreenA400")
    SU.run("cmd overlay disable themed.accents.dark.MaterialTealA700")
    SU.run("cmd overlay disable themed.accents.dark.MaterialTeal500")
    SU.run("cmd overlay disable themed.accents.dark.MaterialTealA200")
    SU.run("cmd overlay disable themed.accents.dark.MaterialTealA400")
    SU.run("cmd overlay disable themed.accents.dark.MaterialCyanA700")
    SU.run("cmd overlay disable themed.accents.dark.MaterialCyan500")
    SU.run("cmd overlay disable themed.accents.dark.MaterialCyanA200")
    SU.run("cmd overlay disable themed.accents.dark.MaterialCyanA400")
    SU.run("cmd overlay disable themed.accents.dark.MaterialLightBlueA700")
    SU.run("cmd overlay disable themed.accents.dark.MaterialLightBlue500")
    SU.run("cmd overlay disable themed.accents.dark.MaterialLightBlueA200")
    SU.run("cmd overlay disable themed.accents.dark.MaterialLightBlueA400")
    SU.run("cmd overlay disable themed.accents.dark.MaterialBlueA700")
    SU.run("cmd overlay disable themed.accents.dark.MaterialBlue500")
    SU.run("cmd overlay disable themed.accents.dark.MaterialBlueA200")
    SU.run("cmd overlay disable themed.accents.dark.MaterialBlueA400")
    SU.run("cmd overlay disable themed.accents.dark.MaterialIndigoA700")
    SU.run("cmd overlay disable themed.accents.dark.MaterialIndigo500")
    SU.run("cmd overlay disable themed.accents.dark.MaterialIndigoA200")
    SU.run("cmd overlay disable themed.accents.dark.MaterialIndigoA400")
    SU.run("cmd overlay disable themed.accents.dark.MaterialDeepPurpleA700")
    SU.run("cmd overlay disable themed.accents.dark.MaterialDeepPurple500")
    SU.run("cmd overlay disable themed.accents.dark.MaterialDeepPurpleA200")
    SU.run("cmd overlay disable themed.accents.dark.MaterialDeepPurpleA400")
    SU.run("cmd overlay disable themed.accents.dark.MaterialPurpleA700")
    SU.run("cmd overlay disable themed.accents.dark.MaterialPurple500")
    SU.run("cmd overlay disable themed.accents.dark.MaterialPurpleA200")
    SU.run("cmd overlay disable themed.accents.dark.MaterialPurpleA400")

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AccentsCard() {

    Card(
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.bordercol),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 0.dp),
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
                    text = "Accents",
                    fontSize = 24.sp
                )
                IconButton(onClick = {
                    resetAccents()
                }) {
                    Image(
                        painter = painterResource(R.drawable.restart_alt_48px),
                        contentDescription = null
                    )
                }
            }

            Divider(thickness = 1.dp, color = MaterialTheme.colors.bordercol)
            AnimatedVisibility(expanded) {
                Surface {
                    Column {
                        Row {

                            Text(
                                modifier = Modifier.width(testdp.dp),
                                text = "A700",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                modifier = Modifier.width(testdp.dp),
                                text = "500",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                modifier = Modifier.width(testdp.dp),
                                text = "A200",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                modifier = Modifier.width(testdp.dp),
                                text = "A400",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                modifier = Modifier.width(testdp.dp),
                                text = "A700",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                modifier = Modifier.width(testdp.dp),
                                text = "500",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                modifier = Modifier.width(testdp.dp),
                                text = "A200",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                modifier = Modifier.width(testdp.dp),
                                text = "A400",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(197, 17, 98)),
                                onClick = { overlayEnable("accents.MaterialPinkA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(233, 30, 99)),
                                onClick = { overlayEnable("accents.MaterialPink500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 64, 129)),
                                onClick = { overlayEnable("accents.MaterialPinkA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(245, 0, 87)),
                                onClick = { overlayEnable("accents.MaterialPinkA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(213, 0, 0)),
                                onClick = { overlayEnable("accents.MaterialRedA700") }) { }

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(244, 67, 54)),
                                onClick = { overlayEnable("accents.MaterialRed500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 82, 82)),
                                onClick = { overlayEnable("accents.MaterialRedA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 23, 68)),
                                onClick = { overlayEnable("accents.MaterialRedA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(221, 44, 0)),
                                onClick = { overlayEnable("accents.MaterialDeepOrangeA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 87, 34)),
                                onClick = { overlayEnable("accents.MaterialDeepOrange500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 110, 64)),
                                onClick = { overlayEnable("accents.MaterialDeepOrangeA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 61, 0)),
                                onClick = { overlayEnable("accents.MaterialDeepOrangeA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 109, 0)),
                                onClick = { overlayEnable("accents.MaterialOrangeA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 152, 0)),
                                onClick = { overlayEnable("accents.MaterialOrange500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 171, 64)),
                                onClick = { overlayEnable("accents.MaterialOrangeA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 145, 0)),
                                onClick = { overlayEnable("accents.MaterialOrangeA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 171, 0)),
                                onClick = { overlayEnable("accents.MaterialAmberA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 193, 7)),
                                onClick = { overlayEnable("accents.MaterialAmber500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 215, 64)),
                                onClick = { overlayEnable("accents.MaterialAmberA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 196, 0)),
                                onClick = { overlayEnable("accents.MaterialAmberA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 214, 0)),
                                onClick = { overlayEnable("accents.MaterialYellowA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 235, 59)),
                                onClick = { overlayEnable("accents.MaterialYellow500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 255, 0)),
                                onClick = { overlayEnable("accents.MaterialYellowA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 234, 0)),
                                onClick = { overlayEnable("accents.MaterialYellowA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(174, 234, 0)),
                                onClick = { overlayEnable("accents.MaterialLimeA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(205, 220, 57)),
                                onClick = { overlayEnable("accents.MaterialLime500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(238, 255, 65)),
                                onClick = { overlayEnable("accents.MaterialLimeA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(198, 255, 0)),
                                onClick = { overlayEnable("accents.MaterialLimeA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(100, 221, 23)),
                                onClick = { overlayEnable("accents.MaterialLightGreenA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(139, 195, 74)),
                                onClick = { overlayEnable("accents.MaterialLightGreen500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(178, 255, 89)),
                                onClick = { overlayEnable("accents.MaterialLightGreenA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(118, 255, 3)),
                                onClick = { overlayEnable("accents.MaterialLightGreenA400") }) {}
                        }
                        Row {

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 200, 83)),
                                onClick = { overlayEnable("accents.MaterialGreenA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(76, 175, 80)),
                                onClick = { overlayEnable("accents.MaterialGreen500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(105, 240, 174)),
                                onClick = { overlayEnable("accents.MaterialGreenA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 230, 118)),
                                onClick = { overlayEnable("accents.MaterialGreenA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 191, 165)),
                                onClick = { overlayEnable("accents.MaterialTealA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 150, 136)),
                                onClick = { overlayEnable("accents.MaterialTeal500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(100, 255, 218)),
                                onClick = { overlayEnable("accents.MaterialTealA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(29, 233, 182)),
                                onClick = { overlayEnable("accents.MaterialTealA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 184, 212)),
                                onClick = { overlayEnable("accents.MaterialCyanA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 188, 212)),
                                onClick = { overlayEnable("accents.MaterialCyan500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(24, 255, 255)),
                                onClick = { overlayEnable("accents.MaterialCyanA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 229, 255)),
                                onClick = { overlayEnable("accents.MaterialCyanA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 145, 234)),
                                onClick = { overlayEnable("accents.MaterialLightBlueA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(3, 169, 244)),
                                onClick = { overlayEnable("accents.MaterialLightBlue500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(64, 196, 255)),
                                onClick = { overlayEnable("accents.MaterialLightBlueA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 176, 255)),
                                onClick = { overlayEnable("accents.MaterialLightBlueA400") }) {}
                        }
                        Row {

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(41, 98, 255)),
                                onClick = { overlayEnable("accents.MaterialBlueA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(33, 150, 243)),
                                onClick = { overlayEnable("accents.MaterialBlue500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(68, 138, 255)),
                                onClick = { overlayEnable("accents.MaterialBlueA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(41, 121, 255)),
                                onClick = { overlayEnable("accents.MaterialBlueA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(48, 79, 254)),
                                onClick = { overlayEnable("accents.MaterialIndigoA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(63, 81, 181)),
                                onClick = { overlayEnable("accents.MaterialIndigo500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(83, 109, 254)),
                                onClick = { overlayEnable("accents.MaterialIndigoA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(61, 90, 254)),
                                onClick = { overlayEnable("accents.MaterialIndigoA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(98, 0, 234)),
                                onClick = { overlayEnable("accents.MaterialDeepPurpleA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(103, 58, 183)),
                                onClick = { overlayEnable("accents.MaterialDeepPurple500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(124, 77, 255)),
                                onClick = { overlayEnable("accents.MaterialDeepPurpleA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(101, 31, 255)),
                                onClick = { overlayEnable("accents.MaterialDeepPurpleA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(170, 0, 255)),
                                onClick = { overlayEnable("accents.MaterialPurpleA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(156, 39, 176)),
                                onClick = { overlayEnable("accents.MaterialPurple500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(224, 64, 251)),
                                onClick = { overlayEnable("accents.MaterialPurpleA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(213, 0, 249)),
                                onClick = { overlayEnable("accents.MaterialPurpleA400") }) {}
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AccentsDarkCard() {
    Card(
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.bordercol),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 0.dp),
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
                    text = "Accents Dark",
                    fontSize = 24.sp
                )
                IconButton(onClick = { resetAccentsDark() }) {
                    Image(
                        painter = painterResource(R.drawable.restart_alt_48px),
                        contentDescription = null
                    )
                }
            }

            Divider(thickness = 1.dp, color = MaterialTheme.colors.bordercol)
            AnimatedVisibility(expanded) {
                Surface {
                    Column {
                        Row {

                            Text(
                                modifier = Modifier.width(testdp.dp),
                                text = "A700",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                modifier = Modifier.width(testdp.dp),
                                text = "500",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                modifier = Modifier.width(testdp.dp),
                                text = "A200",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                modifier = Modifier.width(testdp.dp),
                                text = "A400",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                modifier = Modifier.width(testdp.dp),
                                text = "A700",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                modifier = Modifier.width(testdp.dp),
                                text = "500",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                modifier = Modifier.width(testdp.dp),
                                text = "A200",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                modifier = Modifier.width(testdp.dp),
                                text = "A400",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(197, 17, 98)),
                                onClick = { overlayEnable("accents.dark.MaterialPinkA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(233, 30, 99)),
                                onClick = { overlayEnable("accents.dark.MaterialPink500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 64, 129)),
                                onClick = { overlayEnable("accents.dark.MaterialPinkA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(245, 0, 87)),
                                onClick = { overlayEnable("accents.dark.MaterialPinkA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(213, 0, 0)),
                                onClick = { overlayEnable("accents.dark.MaterialRedA700") }) { }

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(244, 67, 54)),
                                onClick = { overlayEnable("accents.dark.MaterialRed500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 82, 82)),
                                onClick = { overlayEnable("accents.dark.MaterialRedA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 23, 68)),
                                onClick = { overlayEnable("accents.dark.MaterialRedA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(221, 44, 0)),
                                onClick = { overlayEnable("accents.dark.MaterialDeepOrangeA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 87, 34)),
                                onClick = { overlayEnable("accents.dark.MaterialDeepOrange500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 110, 64)),
                                onClick = { overlayEnable("accents.dark.MaterialDeepOrangeA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 61, 0)),
                                onClick = { overlayEnable("accents.dark.MaterialDeepOrangeA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 109, 0)),
                                onClick = { overlayEnable("accents.dark.MaterialOrangeA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 152, 0)),
                                onClick = { overlayEnable("accents.dark.MaterialOrange500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 171, 64)),
                                onClick = { overlayEnable("accents.dark.MaterialOrangeA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 145, 0)),
                                onClick = { overlayEnable("accents.dark.MaterialOrangeA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 171, 0)),
                                onClick = { overlayEnable("accents.dark.MaterialAmberA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 193, 7)),
                                onClick = { overlayEnable("accents.dark.MaterialAmber500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 215, 64)),
                                onClick = { overlayEnable("accents.dark.MaterialAmberA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 196, 0)),
                                onClick = { overlayEnable("accents.dark.MaterialAmberA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 214, 0)),
                                onClick = { overlayEnable("accents.dark.MaterialYellowA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 235, 59)),
                                onClick = { overlayEnable("accents.dark.MaterialYellow500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 255, 0)),
                                onClick = { overlayEnable("accents.dark.MaterialYellowA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 234, 0)),
                                onClick = { overlayEnable("accents.dark.MaterialYellowA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(174, 234, 0)),
                                onClick = { overlayEnable("accents.dark.MaterialLimeA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(205, 220, 57)),
                                onClick = { overlayEnable("accents.dark.MaterialLime500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(238, 255, 65)),
                                onClick = { overlayEnable("accents.dark.MaterialLimeA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(198, 255, 0)),
                                onClick = { overlayEnable("accents.dark.MaterialLimeA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(100, 221, 23)),
                                onClick = { overlayEnable("accents.dark.MaterialLightGreenA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(139, 195, 74)),
                                onClick = { overlayEnable("accents.dark.MaterialLightGreen500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(178, 255, 89)),
                                onClick = { overlayEnable("accents.dark.MaterialLightGreenA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(118, 255, 3)),
                                onClick = { overlayEnable("accents.dark.MaterialLightGreenA400") }) {}
                        }
                        Row {

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 200, 83)),
                                onClick = { overlayEnable("accents.dark.MaterialGreenA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(76, 175, 80)),
                                onClick = { overlayEnable("accents.dark.MaterialGreen500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(105, 240, 174)),
                                onClick = { overlayEnable("accents.dark.MaterialGreenA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 230, 118)),
                                onClick = { overlayEnable("accents.dark.MaterialGreenA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 191, 165)),
                                onClick = { overlayEnable("accents.dark.MaterialTealA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 150, 136)),
                                onClick = { overlayEnable("accents.dark.MaterialTeal500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(100, 255, 218)),
                                onClick = { overlayEnable("accents.dark.MaterialTealA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(29, 233, 182)),
                                onClick = { overlayEnable("accents.dark.MaterialTealA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 184, 212)),
                                onClick = { overlayEnable("accents.dark.MaterialCyanA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 188, 212)),
                                onClick = { overlayEnable("accents.dark.MaterialCyan500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(24, 255, 255)),
                                onClick = { overlayEnable("accents.dark.MaterialCyanA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 229, 255)),
                                onClick = { overlayEnable("accents.dark.MaterialCyanA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 145, 234)),
                                onClick = { overlayEnable("accents.dark.MaterialLightBlueA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(3, 169, 244)),
                                onClick = { overlayEnable("accents.dark.MaterialLightBlue500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(64, 196, 255)),
                                onClick = { overlayEnable("accents.dark.MaterialLightBlueA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 176, 255)),
                                onClick = { overlayEnable("accents.dark.MaterialLightBlueA400") }) {}
                        }
                        Row {

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(41, 98, 255)),
                                onClick = { overlayEnable("accents.dark.MaterialBlueA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(33, 150, 243)),
                                onClick = { overlayEnable("accents.dark.MaterialBlue500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(68, 138, 255)),
                                onClick = { overlayEnable("accents.dark.MaterialBlueA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(41, 121, 255)),
                                onClick = { overlayEnable("accents.dark.MaterialBlueA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(48, 79, 254)),
                                onClick = { overlayEnable("accents.dark.MaterialIndigoA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(63, 81, 181)),
                                onClick = { overlayEnable("accents.dark.MaterialIndigo500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(83, 109, 254)),
                                onClick = { overlayEnable("accents.dark.MaterialIndigoA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(61, 90, 254)),
                                onClick = { overlayEnable("accents.dark.MaterialIndigoA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(98, 0, 234)),
                                onClick = { overlayEnable("accents.dark.MaterialDeepPurpleA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(103, 58, 183)),
                                onClick = { overlayEnable("accents.dark.MaterialDeepPurple500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(124, 77, 255)),
                                onClick = { overlayEnable("accents.dark.MaterialDeepPurpleA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(101, 31, 255)),
                                onClick = { overlayEnable("accents.dark.MaterialDeepPurpleA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(170, 0, 255)),
                                onClick = { overlayEnable("accents.dark.MaterialPurpleA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(156, 39, 176)),
                                onClick = { overlayEnable("accents.dark.MaterialPurple500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(224, 64, 251)),
                                onClick = { overlayEnable("accents.dark.MaterialPurpleA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(213, 0, 249)),
                                onClick = { overlayEnable("accents.dark.MaterialPurpleA400") }) {}
                        }
                    }
                }
            }
        }
    }
}


//@Preview
@Composable
fun ColorsTab() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp),
        color = MaterialTheme.colors.cardcol
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            AccentsCard()
            AccentsDarkCard()
            InfoCard()
        }

    }


}

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
fun MiscTab() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp),
        color = MaterialTheme.colors.cardcol
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
           RoundedCornersCard()

        }
    }

}

//@Preview
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
                    resetQSTiles()
                }) {
                    Image(
                        painter = painterResource(R.drawable.restart_alt_48px),
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
                            IconButton(
                                onClick = { overlayEnable("qstile.mountain") },
                                modifier = Modifier
                                    .size(testdp.dp)
                                    .background(color = MaterialTheme.colors.cardcol)
                            ) {
                                Image(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    painter = painterResource(R.drawable.qsmountain),
                                    contentDescription = "Mountain"
                                )
                            }
                            IconButton(
                                onClick = { overlayEnable("qstile.neonlike") },
                                modifier = Modifier
                                    .size(testdp.dp)
                                    .background(color = MaterialTheme.colors.cardcol)
                            ) {
                                Image(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    painter = painterResource(R.drawable.qsneonlike),
                                    contentDescription = "NeonLike"
                                )
                            }
                            IconButton(
                                onClick = { overlayEnable("qstile.ninja") },
                                modifier = Modifier
                                    .size(testdp.dp)
                                    .background(color = MaterialTheme.colors.cardcol)
                            ) {
                                Image(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    painter = painterResource(R.drawable.qsninja),
                                    contentDescription = "Ninja",
                                )
                            }
                            IconButton(
                                onClick = { overlayEnable("qstile.oreocircletrim") },
                                modifier = Modifier
                                    .size(testdp.dp)
                                    .background(color = MaterialTheme.colors.cardcol)
                            ) {
                                Image(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    painter = painterResource(R.drawable.qsoreocircletrim),
                                    contentDescription = "Oreo (Circle Trim)",
                                )
                            }
                            IconButton(
                                onClick = { overlayEnable("qstile.oreosquircletrim") },
                                modifier = Modifier
                                    .size(testdp.dp)
                                    .background(color = MaterialTheme.colors.cardcol)
                            ) {
                                Image(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    painter = painterResource(R.drawable.qsoreosquircletrim),
                                    contentDescription = "Oreo (Squircle Trim)",
                                )
                            }
                            IconButton(
                                onClick = { overlayEnable("qstile.pokesign") },
                                modifier = Modifier
                                    .size(testdp.dp)
                                    .background(color = MaterialTheme.colors.cardcol)
                            ) {
                                Image(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    painter = painterResource(R.drawable.qspokesign),
                                    contentDescription = "Pokesign",
                                )
                            }

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
                    resetNavbars()
                }) {
                    Image(
                        painter = painterResource(R.drawable.restart_alt_48px),
                        contentDescription = null
                    )
                }
            }

            Divider(thickness = 1.dp, color = MaterialTheme.colors.bordercol)
            AnimatedVisibility(expanded) {
                Surface {
                    Column {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
                                overlayEnable("navbar.android")
                            }) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Image(
                                    painter = painterResource(R.drawable.navbar_android_back),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_android_home),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_android_recent),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
                                overlayEnable("navbar.asus")
                            }) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Image(
                                    painter = painterResource(R.drawable.navbar_asus_back),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_asus_home),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_asus_recent),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
                                overlayEnable("navbar.dora")
                            }) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Image(
                                    painter = painterResource(R.drawable.navbar_dora_back),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_dora_home),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_dora_recent),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
                                overlayEnable("navbar.moto")
                            }) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Image(
                                    painter = painterResource(R.drawable.navbar_moto_back),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_moto_home),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_moto_recent),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
                                overlayEnable("navbar.nexus")
                            }) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Image(
                                    painter = painterResource(R.drawable.navbar_nexus_back),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_nexus_home),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_nexus_recent),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
                                overlayEnable("navbar.old")
                            }) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Image(
                                    painter = painterResource(R.drawable.navbar_old_back),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_old_home),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_old_recent),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
                                overlayEnable("navbar.oneplus")
                            }) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Image(
                                    painter = painterResource(R.drawable.navbar_oneplus_back),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_oneplus_home),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_oneplus_recent),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
                                overlayEnable("navbar.sammy")
                            }) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Image(
                                    painter = painterResource(R.drawable.navbar_sammy_back),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_sammy_home),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_sammy_recent),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
                                overlayEnable("navbar.tecno")
                            }) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                Image(
                                    painter = painterResource(R.drawable.navbar_tecnocamon_back),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_tecnocamon_home),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.navbar_tecnocamon_recent),
                                    contentDescription = null, Modifier.size(testdp.dp)
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
                    resetIconPacks()
                }) {
                    Image(
                        painter = painterResource(R.drawable.restart_alt_48px),
                        contentDescription = null
                    )
                }
            }

            Divider(thickness = 1.dp, color = MaterialTheme.colors.bordercol)

            AnimatedVisibility(expanded) {
                Surface {
                    Column {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(testdp.dp + 8.dp)
                                .padding(2.dp),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
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
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_archerus_bluetooth_transient_animation),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_archerus_dnd),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_archerus_flashlight),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_archerus_auto_rotate),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_archerus_airplane),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(testdp.dp + 8.dp)
                                .padding(2.dp),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
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
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_circular_bluetooth_transient_animation),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_circular_dnd),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_circular_flashlight),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_circular_auto_rotate),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_circular_airplane),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(testdp.dp + 8.dp)
                                .padding(2.dp),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
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
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_filled_bluetooth_transient_animation),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_filled_dnd),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_filled_flashlight),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_filled_auto_rotate),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_filled_airplane),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(testdp.dp + 8.dp)
                                .padding(2.dp),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
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
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_kai_bluetooth_transient_animation),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_kai_dnd),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_kai_flashlight),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_kai_auto_rotate),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_kai_airplane),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(testdp.dp + 8.dp)
                                .padding(2.dp),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
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
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_outline_bluetooth_transient_animation),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_outline_dnd),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_outline_flashlight),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_outline_auto_rotate),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_outline_airplane),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(testdp.dp + 8.dp)
                                .padding(2.dp),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
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
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_oos_bluetooth_transient_animation),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_oos_dnd),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_oos_flashlight),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_oos_auto_rotate),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_oos_airplane),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(testdp.dp + 8.dp)
                                .padding(2.dp),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
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
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_pui_bluetooth_transient_animation),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_pui_dnd),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_pui_flashlight),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_pui_auto_rotate),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_pui_airplane),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                            }
                        }


                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(testdp.dp + 8.dp)
                                .padding(2.dp),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
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
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_rounded_bluetooth_transient_animation),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_rounded_dnd),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_rounded_flashlight),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_rounded_auto_rotate),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_rounded_airplane),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                            }
                        }


                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(testdp.dp + 8.dp)
                                .padding(2.dp),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
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
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_sam_bluetooth_transient_animation),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_sam_dnd),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_sam_flashlight),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_sam_auto_rotate),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_sam_airplane),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(testdp.dp + 8.dp)
                                .padding(2.dp),
                            color = MaterialTheme.colors.cardcol,
                            onClick = {
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
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_victor_bluetooth_transient_animation),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_victor_dnd),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_victor_flashlight),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_victor_auto_rotate),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.iconpack_victor_airplane),
                                    contentDescription = null, Modifier.size(testdp.dp)
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}

fun resetIconPacks() {
    SU.run("cmd overlay disable themed.iconpack.archerus.android")
    SU.run("cmd overlay disable themed.iconpack.archerus.launcher")
    SU.run("cmd overlay disable themed.iconpack.archerus.settings")
    SU.run("cmd overlay disable themed.iconpack.archerus.systemui")
    SU.run("cmd overlay disable themed.iconpack.archerus.themepicker")
    SU.run("cmd overlay disable themed.iconpack.circular.android")
    SU.run("cmd overlay disable themed.iconpack.circular.launcher")
    SU.run("cmd overlay disable themed.iconpack.circular.settings")
    SU.run("cmd overlay disable themed.iconpack.circular.systemui")
    SU.run("cmd overlay disable themed.iconpack.circular.themepicker")
    SU.run("cmd overlay disable themed.iconpack.filled.android")
    SU.run("cmd overlay disable themed.iconpack.filled.launcher")
    SU.run("cmd overlay disable themed.iconpack.filled.settings")
    SU.run("cmd overlay disable themed.iconpack.filled.systemui")
    SU.run("cmd overlay disable themed.iconpack.filled.themepicker")
    SU.run("cmd overlay disable themed.iconpack.kai.android")
    SU.run("cmd overlay disable themed.iconpack.kai.launcher")
    SU.run("cmd overlay disable themed.iconpack.kai.settings")
    SU.run("cmd overlay disable themed.iconpack.kai.systemui")
    SU.run("cmd overlay disable themed.iconpack.kai.themepicker")
    SU.run("cmd overlay disable themed.iconpack.oos.android")
    SU.run("cmd overlay disable themed.iconpack.oos.launcher")
    SU.run("cmd overlay disable themed.iconpack.oos.settings")
    SU.run("cmd overlay disable themed.iconpack.oos.systemui")
    SU.run("cmd overlay disable themed.iconpack.oos.themepicker")
    SU.run("cmd overlay disable themed.iconpack.outline.android")
    SU.run("cmd overlay disable themed.iconpack.outline.launcher")
    SU.run("cmd overlay disable themed.iconpack.outline.settings")
    SU.run("cmd overlay disable themed.iconpack.outline.systemui")
    SU.run("cmd overlay disable themed.iconpack.outline.themepicker")
    SU.run("cmd overlay disable themed.iconpack.pui.android")
    SU.run("cmd overlay disable themed.iconpack.pui.launcher")
    SU.run("cmd overlay disable themed.iconpack.pui.settings")
    SU.run("cmd overlay disable themed.iconpack.pui.systemui")
    SU.run("cmd overlay disable themed.iconpack.pui.themepicker")
    SU.run("cmd overlay disable themed.iconpack.rounded.android")
    SU.run("cmd overlay disable themed.iconpack.rounded.launcher")
    SU.run("cmd overlay disable themed.iconpack.rounded.settings")
    SU.run("cmd overlay disable themed.iconpack.rounded.systemui")
    SU.run("cmd overlay disable themed.iconpack.rounded.themepicker")
    SU.run("cmd overlay disable themed.iconpack.sam.android")
    SU.run("cmd overlay disable themed.iconpack.sam.launcher")
    SU.run("cmd overlay disable themed.iconpack.sam.settings")
    SU.run("cmd overlay disable themed.iconpack.sam.systemui")
    SU.run("cmd overlay disable themed.iconpack.sam.themepicker")
    SU.run("cmd overlay disable themed.iconpack.victor.android")
    SU.run("cmd overlay disable themed.iconpack.victor.launcher")
    SU.run("cmd overlay disable themed.iconpack.victor.settings")
    SU.run("cmd overlay disable themed.iconpack.victor.systemui")
    SU.run("cmd overlay disable themed.iconpack.victor.themepicker")

}


fun resetNavbars() {
    SU.run("cmd overlay disable themed.navbar.android")
    SU.run("cmd overlay disable themed.navbar.asus")
    SU.run("cmd overlay disable themed.navbar.dora")
    SU.run("cmd overlay disable themed.navbar.moto")
    SU.run("cmd overlay disable themed.navbar.nexus")
    SU.run("cmd overlay disable themed.navbar.old")
    SU.run("cmd overlay disable themed.navbar.oneplus")
    SU.run("cmd overlay disable themed.navbar.oneui")
    SU.run("cmd overlay disable themed.navbar.sammy")
    SU.run("cmd overlay disable themed.navbar.tecno")
}

fun overlayEnable(overlayname: String) {
    SU.run("cmd overlay enable-exclusive --category themed.$overlayname")
}

@Composable
fun MyIconButton(overlayname: String, sizedp: Int, contentdescription: String, iconname: Int) {
    IconButton(
        onClick = { overlayEnable(overlayname) },
        modifier = Modifier
            .size(sizedp.dp)
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

fun resetQSTiles() {
    SU.run("cmd overlay disable themed.qstile.dualtonecircle")
    SU.run("cmd overlay disable themed.qstile.circlegradient")
    SU.run("cmd overlay disable themed.qstile.circletrim")
    SU.run("cmd overlay disable themed.qstile.cookie")
    SU.run("cmd overlay disable themed.qstile.cosmos")
    SU.run("cmd overlay disable themed.qstile.default")
    SU.run("cmd overlay disable themed.qstile.dividedcircle")
    SU.run("cmd overlay disable themed.qstile.dottedcircle")
    SU.run("cmd overlay disable themed.qstile.dualtonecircletrim")
    SU.run("cmd overlay disable themed.qstile.ink")
    SU.run("cmd overlay disable themed.qstile.inkdrop")
    SU.run("cmd overlay disable themed.qstile.justicons")
    SU.run("cmd overlay disable themed.qstile.mountain")
    SU.run("cmd overlay disable themed.qstile.neonlike")
    SU.run("cmd overlay disable themed.qstile.ninja")
    SU.run("cmd overlay disable themed.qstile.oreocircletrim")
    SU.run("cmd overlay disable themed.qstile.oreosquircletrim")
    SU.run("cmd overlay disable themed.qstile.pokesign")
    SU.run("cmd overlay disable themed.qstile.squaremedo")
    SU.run("cmd overlay disable themed.qstile.squircle")
    SU.run("cmd overlay disable themed.qstile.squircletrim")
    SU.run("cmd overlay disable themed.qstile.teardrop")
    SU.run("cmd overlay disable themed.qstile.triangle")
    SU.run("cmd overlay disable themed.qstile.wavey")
}

//@Preview
@Composable
fun RoundedCornersCard() {


    var sliderPosition by remember { mutableStateOf(0f) }
var rounddp = sliderPosition.roundToInt()
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
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .padding(start = 8.dp),
                    text = "Corners",
                    fontSize = 24.sp
                )
                IconButton(onClick = {
                    resetQSTiles()
                }) {
                    Image(
                        painter = painterResource(R.drawable.restart_alt_48px),
                        contentDescription = null
                    )
                }
            }

            Divider(thickness = 1.dp, color = MaterialTheme.colors.bordercol)
           Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = stringResource(R.string.CornersTip), modifier = Modifier.padding(4.dp))

            Text(text = rounddp.toString())
        }
            Slider(modifier = Modifier.padding(horizontal = 4.dp),
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = 0f..36f,
                onValueChangeFinished = {
                    overlayEnable("roundedcorners$rounddp")
                },
                steps = 8,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colors.cardcol,
                    activeTrackColor = MaterialTheme.colors.bordercol
                )
            )
            Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
            ) {

            Text(text = " 0")
            Text(text = " 4")
            Text(text = " 8")
            Text(text = "12")
            Text(text = "16")
            Text(text = "20")
            Text(text = "24")
            Text(text = "28")
            Text(text = "32")
            Text(text = "36")

        }
        }
    }
}

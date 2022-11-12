@file:Suppress("OPT_IN_IS_NOT_ENABLED") @file:OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class
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
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.ui.theme.*


@get:Composable
val Colors.bordercol: Color
    get() = if (isLight) borderLight else borderDark
val Colors.cardcol: Color
    get() = if (isLight) backgroundLight else backgroundDark


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

   // @Preview
    @Composable
    fun Main() {
        Column(
        ) {


            val navController = rememberNavController()

            Scaffold(
                topBar = { TopAppBar()},
                bottomBar = { BottomNavigationBar(navController) }
            ) {
                Navigation(navController)
            }
            ColorsTab()
        }
    }
}

private operator fun Navigation.invoke(navController: NavHostController) {

}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItems.ColorsTab,
        NavigationItems.IconsTab)
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.background,
        contentColor = Color.Black
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { items ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = items.icon),
                        contentDescription = items.title
                    )
                },
                label = { Text(text = items.title) },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Black.copy(0.4f),
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

                }
            )
        }
    }

}

@Composable
fun Navigation(navController: NavHostController){

    NavHost(navController, startDestination = NavigationItems.ColorsTab.route){

        composable(NavigationItems.ColorsTab.route){
            ColorsTab()
        }

        composable(NavigationItems.IconsTab.route){
            IconsTab()
        }
    }

}


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
        elevation = (0.dp),
        shape = RoundedCornerShape(8.dp),
                backgroundColor = MaterialTheme.colors.cardcol

    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = "Please note that on older devices updating systemUI can take up to 30s. It is recommended that you'd install bootloop protector module. Please report testing to telegram support group.",
            fontSize = 14.sp
        )
    }
}

@Composable
fun TopAppBar() {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    TopAppBar(
        title = { Text("Themed Manager") },
        backgroundColor = MaterialTheme.colors.cardcol,
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "list")
            }

            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(Icons.Default.MoreVert, contentDescription = "list")
            }

            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                val webIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.t.me/ThemedSupport"))
                val webIntent1 = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.github.com/Osanosa/ThemedProject/")
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
    Shell.SU.run("cmd overlay disable themed.accents.MaterialPinkA700")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialPink500")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialPinkA200")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialPinkA400")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialRedA700")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialRed500")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialRedA200")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialRedA400")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialDeepOrangeA700")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialDeepOrange500")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialDeepOrangeA200")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialDeepOrangeA400")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialOrangeA700")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialOrange500")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialOrangeA200")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialOrangeA400")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialAmberA700")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialAmber500")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialAmberA200")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialAmberA400")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialYellowA700")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialYellow500")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialYellowA200")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialYellowA400")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialLimeA700")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialLime500")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialLimeA200")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialLimeA400")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialLightGreenA700")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialLightGreen500")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialLightGreenA200")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialLightGreenA400")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialGreenA700")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialGreen500")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialGreenA200")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialGreenA400")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialTealA700")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialTeal500")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialTealA200")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialTealA400")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialCyanA700")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialCyan500")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialCyanA200")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialCyanA400")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialLightBlueA700")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialLightBlue500")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialLightBlueA200")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialLightBlueA400")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialBlueA700")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialBlue500")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialBlueA200")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialBlueA400")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialIndigoA700")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialIndigo500")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialIndigoA200")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialIndigoA400")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialDeepPurpleA700")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialDeepPurple500")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialDeepPurpleA200")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialDeepPurpleA400")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialPurpleA700")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialPurple500")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialPurpleA200")
    Shell.SU.run("cmd overlay disable themed.accents.MaterialPurpleA400")

}

fun resetAccentsDark() {

    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialPinkA700")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialPink500")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialPinkA200")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialPinkA400")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialRedA700")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialRed500")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialRedA200")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialRedA400")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialDeepOrangeA700")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialDeepOrange500")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialDeepOrangeA200")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialDeepOrangeA400")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialOrangeA700")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialOrange500")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialOrangeA200")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialOrangeA400")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialAmberA700")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialAmber500")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialAmberA200")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialAmberA400")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialYellowA700")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialYellow500")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialYellowA200")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialYellowA400")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialLimeA700")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialLime500")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialLimeA200")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialLimeA400")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialLightGreenA700")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialLightGreen500")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialLightGreenA200")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialLightGreenA400")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialGreenA700")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialGreen500")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialGreenA200")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialGreenA400")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialTealA700")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialTeal500")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialTealA200")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialTealA400")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialCyanA700")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialCyan500")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialCyanA200")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialCyanA400")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialLightBlueA700")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialLightBlue500")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialLightBlueA200")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialLightBlueA400")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialBlueA700")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialBlue500")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialBlueA200")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialBlueA400")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialIndigoA700")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialIndigo500")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialIndigoA200")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialIndigoA400")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialDeepPurpleA700")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialDeepPurple500")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialDeepPurpleA200")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialDeepPurpleA400")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialPurpleA700")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialPurple500")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialPurpleA200")
    Shell.SU.run("cmd overlay disable themed.accents.dark.MaterialPurpleA400")

}


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

            Divider(thickness = 1.dp)
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
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialPinkA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(233, 30, 99)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialPink500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 64, 129)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialPinkA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(245, 0, 87)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialPinkA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(213, 0, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialRedA700") }) { }

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(244, 67, 54)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialRed500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 82, 82)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialRedA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 23, 68)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialRedA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(221, 44, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialDeepOrangeA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 87, 34)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialDeepOrange500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 110, 64)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialDeepOrangeA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 61, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialDeepOrangeA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 109, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialOrangeA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 152, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialOrange500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 171, 64)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialOrangeA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 145, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialOrangeA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 171, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialAmberA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 193, 7)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialAmber500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 215, 64)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialAmberA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 196, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialAmberA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 214, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialYellowA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 235, 59)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialYellow500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 255, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialYellowA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 234, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialYellowA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(174, 234, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLimeA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(205, 220, 57)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLime500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(238, 255, 65)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLimeA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(198, 255, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLimeA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(100, 221, 23)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLightGreenA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(139, 195, 74)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLightGreen500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(178, 255, 89)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLightGreenA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(118, 255, 3)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLightGreenA400") }) {}
                        }
                        Row {

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 200, 83)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialGreenA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(76, 175, 80)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialGreen500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(105, 240, 174)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialGreenA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 230, 118)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialGreenA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 191, 165)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialTealA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 150, 136)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialTeal500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(100, 255, 218)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialTealA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(29, 233, 182)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialTealA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 184, 212)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialCyanA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 188, 212)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialCyan500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(24, 255, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialCyanA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 229, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialCyanA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 145, 234)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLightBlueA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(3, 169, 244)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLightBlue500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(64, 196, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLightBlueA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 176, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLightBlueA400") }) {}
                        }
                        Row {

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(41, 98, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialBlueA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(33, 150, 243)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialBlue500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(68, 138, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialBlueA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(41, 121, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialBlueA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(48, 79, 254)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialIndigoA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(63, 81, 181)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialIndigo500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(83, 109, 254)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialIndigoA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(61, 90, 254)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialIndigoA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(98, 0, 234)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialDeepPurpleA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(103, 58, 183)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialDeepPurple500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(124, 77, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialDeepPurpleA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(101, 31, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialDeepPurpleA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(170, 0, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialPurpleA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(156, 39, 176)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialPurple500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(224, 64, 251)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialPurpleA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(213, 0, 249)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialPurpleA400") }) {}
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

            Divider(thickness = 1.dp)
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
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialPinkA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(233, 30, 99)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialPink500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 64, 129)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialPinkA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(245, 0, 87)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialPinkA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(213, 0, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialRedA700") }) { }

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(244, 67, 54)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialRed500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 82, 82)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialRedA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 23, 68)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialRedA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(221, 44, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialDeepOrangeA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 87, 34)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialDeepOrange500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 110, 64)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialDeepOrangeA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 61, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialDeepOrangeA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 109, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialOrangeA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 152, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialOrange500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 171, 64)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialOrangeA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 145, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialOrangeA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 171, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialAmberA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 193, 7)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialAmber500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 215, 64)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialAmberA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 196, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialAmberA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 214, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialYellowA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 235, 59)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialYellow500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 255, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialYellowA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(255, 234, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialYellowA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(174, 234, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialLimeA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(205, 220, 57)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialLime500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(238, 255, 65)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialLimeA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(198, 255, 0)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialLimeA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(100, 221, 23)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialLightGreenA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(139, 195, 74)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialLightGreen500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(178, 255, 89)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialLightGreenA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(118, 255, 3)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialLightGreenA400") }) {}
                        }
                        Row {

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 200, 83)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialGreenA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(76, 175, 80)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialGreen500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(105, 240, 174)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialGreenA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 230, 118)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialGreenA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 191, 165)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialTealA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 150, 136)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialTeal500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(100, 255, 218)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialTealA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(29, 233, 182)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialTealA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 184, 212)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialCyanA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 188, 212)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialCyan500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(24, 255, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialCyanA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 229, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialCyanA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 145, 234)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialLightBlueA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(3, 169, 244)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialLightBlue500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(64, 196, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialLightBlueA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(0, 176, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialLightBlueA400") }) {}
                        }
                        Row {

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(41, 98, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialBlueA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(33, 150, 243)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialBlue500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(68, 138, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialBlueA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(41, 121, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialBlueA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(48, 79, 254)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialIndigoA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(63, 81, 181)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialIndigo500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(83, 109, 254)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialIndigoA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(61, 90, 254)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialIndigoA400") }) {}
                        }
                        Row {
                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(98, 0, 234)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialDeepPurpleA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(103, 58, 183)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialDeepPurple500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(124, 77, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialDeepPurpleA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(101, 31, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialDeepPurpleA400") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(170, 0, 255)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialPurpleA700") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(156, 39, 176)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialPurple500") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(224, 64, 251)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialPurpleA200") }) {}

                            Surface(modifier = Modifier.size(testdp.dp),
                                color = Color(rgb(213, 0, 249)),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.dark.MaterialPurpleA400") }) {}
                        }
                    }
                }
            }
        }
    }
}


@SuppressLint("VisibleForTests")
@Composable
fun AdvertView(modifier: Modifier = Modifier) {
    val isInEditMode = LocalInspectionMode.current
    if (isInEditMode) {
        Text(
            modifier = modifier
                .fillMaxWidth()
                .background(Red)
                .padding(horizontal = 2.dp, vertical = 6.dp),
            textAlign = TextAlign.Center,
            color = White,
            text = "Advert Here",
        )
    } else {
        AndroidView(modifier = modifier.fillMaxWidth(), factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = context.getString(R.string.ad_id_banner)
                loadAd(AdRequest.Builder().build())
            }
        })
    }
}


@Composable
fun ColorsTab() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.cardcol) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            AdvertView()
            AccentsCard()
            AccentsDarkCard()
            InfoCard()
        }

    }


}
@Composable
fun IconsTab() {
    Surface( color = MaterialTheme.colors.cardcol) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            AdvertView()
            QSTileCard()
            InfoCard()

        }
    }

}
@Preview
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

                Divider(thickness = 1.dp)
                AnimatedVisibility(expanded) {
                    Surface {
                        Column {
                            Row() {
                                IconButton(onClick = { /*TODO*/ }, modifier = Modifier.size(testdp.dp)) {
                                    Image(painter = , contentDescription = )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

fun resetQSTiles() {
    TODO("Not yet implemented")
}



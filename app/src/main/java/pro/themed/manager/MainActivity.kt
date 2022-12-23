@file:Suppress("OPT_IN_IS_NOT_ENABLED") @file:OptIn(
    ExperimentalMaterialApi::class, ExperimentalMaterialApi::class, ExperimentalMaterialApi::class,

    )

package pro.themed.manager

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color.rgb
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

enum class CardFace(val angle: Float) {
    Front(0f) {
        override val next: CardFace
            get() = Back
    },
    Back(180f) {
        override val next: CardFace
            get() = Front
    };

    abstract val next: CardFace
}

enum class RotationAxis {
    AxisX, AxisY,
}

@Stable

@ExperimentalMaterialApi
@Composable
fun FlipCard(
    cardFace: CardFace,
    onClick: (CardFace) -> Unit,
    modifier: Modifier = Modifier,
    axis: RotationAxis = RotationAxis.AxisY,
    back: @Composable () -> Unit = {},
    front: @Composable () -> Unit = {},
) {
    val rotation = animateFloatAsState(
        targetValue = cardFace.angle, animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing,
        )
    )
    Card(
        elevation = 0.dp,
        onClick = { onClick(cardFace) },
        modifier = modifier
            .wrapContentSize()
            .graphicsLayer {
                if (axis == RotationAxis.AxisX) {
                    rotationX = rotation.value
                } else {
                    rotationY = rotation.value
                }
                cameraDistance = 12f * density
            },
    ) {
        if (rotation.value <= 90f) {
            Box(
                Modifier.wrapContentSize()
            ) {
                front()
            }
        } else {
            Box(
                Modifier
                    .wrapContentSize()
                    .graphicsLayer {
                        if (axis == RotationAxis.AxisX) {
                            rotationX = 180f
                        } else {
                            rotationY = 180f
                        }
                    },
            ) {
                back()
            }
        }
    }
}

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
        Column {


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
        composable(NavigationItems.MiscTab.route) {
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

//@Preview
@Composable
fun MagiskInfoCard() {
    Card(
        border = BorderStroke(
            width = 1.dp, color = Color.Red
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
            text = "This app requires root access and installed Themed Project modules. It won't work other way.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold

        )
    }
}

//@Preview
@Composable
fun AdrodInfoCard() {
    Card(
        border = BorderStroke(
            width = 1.dp, color = Color.Red
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
            text = "A12+ does not support landscape grid",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold

        )
    }
}

//@Preview()
@Composable
fun TopAppBar() {
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


            })

}


//@Preview
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
                    text = "Accents",
                    fontSize = 24.sp
                )
                IconButton(modifier = Modifier, onClick = {
                    SU.run("for ol in \$(cmd overlay list | grep -E 'themed.accents.M' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
                }) {
                    Image(
                        painter = painterResource(R.drawable.restart_alt_48px),
                        contentDescription = null,
                    )
                }
            }

            Column {
                Divider(thickness = 1.dp, color = MaterialTheme.colors.bordercol)

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
                    text = "Accents Dark",
                    fontSize = 24.sp
                )
                IconButton(onClick = {

                    SU.run("for ol in \$(cmd overlay list | grep -E 'themed.accents.dark' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"$" + "ol\"; done")
                }) {
                    Image(
                        painter = painterResource(R.drawable.restart_alt_48px),
                        contentDescription = null
                    )
                }
            }

            Divider(thickness = 1.dp, color = MaterialTheme.colors.bordercol)

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

            var state by remember { mutableStateOf(CardFace.Front) }
            FlipCard(cardFace = state, onClick = {
                state = it.next
            }, axis = RotationAxis.AxisY, back = {
                AccentsDarkCard()
            }, front = {
                AccentsCard()
            })
           MagiskInfoCard()
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

//@Preview
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
            QsGridCard()
            AdrodInfoCard()
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
                    SU.run("for ol in \$(cmd overlay list | grep -E 'themed.qstile' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"$" + "ol\"; done")
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
                    SU.run("for ol in \$(cmd overlay list | grep -E 'themed.navbar' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"$" + "ol\"; done")
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
                    SU.run("for ol in \$(cmd overlay list | grep -E 'themed.iconpack' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"$" + "ol\"; done")
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


//@Preview
@Composable
fun RoundedCornersCard() {
    var sliderPosition by remember { mutableStateOf(0f) }

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
                    SU.run("for ol in \$(cmd overlay list | grep -E 'themed.rounded' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"$" + "ol\"; done")
                }) {
                    Image(
                        painter = painterResource(R.drawable.restart_alt_48px),
                        contentDescription = null
                    )
                }
            }

            Divider(thickness = 1.dp, color = MaterialTheme.colors.bordercol)
            Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                var rounddp = sliderPosition.roundToInt()
                Text(text = rounddp.toString())
                val context = LocalContext.current
                Slider(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it; rounddp = it.roundToInt() },
                    valueRange = 0f..36f,
                    onValueChangeFinished = {
                        Toast.makeText(context, "$rounddp", Toast.LENGTH_SHORT).show()
                        overlayEnable("roundedcorners$rounddp")
                    },
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colors.cardcol,
                        activeTrackColor = MaterialTheme.colors.bordercol
                    )
                )
            }
        }
    }
}

//@Preview
@Composable
fun QsGridCard() {
    var rowspositionportrait by remember { mutableStateOf(1f) }
    var columnspositionportrait by remember { mutableStateOf(1f) }
    var rowspositionlandscape by remember { mutableStateOf(1f) }
    var columnspositionlandscape by remember { mutableStateOf(1f) }

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
                    text = "QsGrid",
                    fontSize = 24.sp
                )
                IconButton(onClick = {
                    SU.run("for ol in \$(cmd overlay list | grep -E 'themed.qsgrid' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"$" + "ol\"; done")
                }) {
                    Image(
                        painter = painterResource(R.drawable.restart_alt_48px),
                        contentDescription = null
                    )
                }
            }

            Divider(thickness = 1.dp, color = MaterialTheme.colors.bordercol)
            Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                val context = LocalContext.current
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    var rowsportrait = rowspositionportrait.roundToInt()
                    var columnsportrait = columnspositionportrait.roundToInt()
                    Text(text = "Portrait $rowsportrait × $columnsportrait", fontSize = 20.sp)
                    Slider(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        value = rowspositionportrait,
                        onValueChange = { rowspositionportrait = it; rowsportrait = it.roundToInt() },
                        valueRange = 1f..10f,
                        onValueChangeFinished = {
                              overlayEnable("qsgrid.rowsportrait$rowsportrait")
                        },
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colors.cardcol,
                            activeTrackColor = MaterialTheme.colors.bordercol
                        )
                    )
                    Slider(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            value = columnspositionportrait,
                            onValueChange = { columnspositionportrait = it; columnsportrait = it.roundToInt() },
                            valueRange = 1f..10f,
                            onValueChangeFinished = {
                                overlayEnable("qsgrid.columnsportrait$columnsportrait")
                            },
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colors.cardcol,
                                activeTrackColor = MaterialTheme.colors.bordercol
                            )
                        )
                    var rowslandscape = rowspositionlandscape.roundToInt()
                    var columnslandscape = columnspositionlandscape.roundToInt()
                    Text(text = "Landscape $rowslandscape × $columnslandscape", fontSize = 20.sp)
                    Slider(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        value = rowspositionlandscape,
                        onValueChange = { rowspositionlandscape = it; rowslandscape = it.roundToInt() },
                        valueRange = 1f..10f,
                        onValueChangeFinished = {
                            overlayEnable("qsgrid.rowslandscape$rowslandscape")
                        },
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colors.cardcol,
                            activeTrackColor = MaterialTheme.colors.bordercol
                        )
                    )
                    Slider(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            value = columnspositionlandscape,
                            onValueChange = { columnspositionlandscape = it; columnslandscape = it.roundToInt() },
                            valueRange = 1f..10f,
                            onValueChangeFinished = {
                                overlayEnable("qsgrid.columnslandscape$columnslandscape")
                            },
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colors.cardcol,
                                activeTrackColor = MaterialTheme.colors.bordercol
                            )
                        )


                }

            }
        }
    }
}

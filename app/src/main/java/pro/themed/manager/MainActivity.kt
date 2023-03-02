@file:OptIn(
    ExperimentalMaterialApi::class, ExperimentalMaterialApi::class, ExperimentalMaterialApi::class,

    )

package pro.themed.manager

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.jaredrummler.ktsh.Shell.Companion.SU
import pro.themed.manager.comps.AppsTab
import pro.themed.manager.comps.ColorsTab
import pro.themed.manager.comps.IconsTab
import pro.themed.manager.comps.MiscTab
import pro.themed.manager.ui.theme.*


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
                val screenwidth = (LocalConfiguration.current.screenWidthDp - 16) / 8
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.cardcol
                ) {
                    Main()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
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
        NavigationItems.ColorsTab,
        NavigationItems.IconsTab,
        NavigationItems.FontsTab,
        NavigationItems.MiscTab
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
                    painter = painterResource(id = items.icon),
                    contentDescription = items.title,
                    modifier = Modifier.size(24.dp)
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

@ExperimentalMaterial3Api
@Composable
fun Navigation(navController: NavHostController) {

    NavHost(navController, startDestination = NavigationItems.ColorsTab.route) {

        composable(NavigationItems.ColorsTab.route) {
            ColorsTab()
        }
        composable(NavigationItems.IconsTab.route) {
            IconsTab()
        }
        composable(NavigationItems.FontsTab.route) {
            AppsTab()
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
            .wrapContentHeight(),
        shape = RoundedCornerShape(8.dp),
        elevation = (0.dp),
        backgroundColor = MaterialTheme.colors.cardcol
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
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
            .wrapContentHeight(),
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
        border = BorderStroke(width = 1.dp, color = Color.Red),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
            .padding(top = 0.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = (0.dp),
        backgroundColor = Color.Red.copy(0.4f)
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
                context.startActivity(Intent(context, ToolboxActivity::class.java))

            }) {
                Icon(painterResource(id = R.drawable.toolbox), contentDescription = "Settings")
            }

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


fun overlayEnable(overlayname: String) {
    SU.run("cmd overlay enable-exclusive --category themed.$overlayname")
    SU.run("cmd overlay enable themed.$overlayname")

    Firebase.analytics.logEvent("Overlay_Selected") {
        param ("Overlay_Name" , overlayname)
    }



}



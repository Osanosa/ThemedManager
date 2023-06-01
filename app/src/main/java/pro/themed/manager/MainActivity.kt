@file:OptIn(
    ExperimentalMaterialApi::class, ExperimentalMaterialApi::class, ExperimentalMaterialApi::class,

    )

package pro.themed.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.jaredrummler.ktsh.Shell.Companion.SH
import com.jaredrummler.ktsh.Shell.Companion.SU
import pro.themed.manager.comps.ColorsTab
import pro.themed.manager.comps.IconsTab
import pro.themed.manager.comps.MiscTab
import pro.themed.manager.ui.theme.*


data class OverlayListData(
    val overlayList: List<String>,
    val unsupportedOverlays: List<String>,
    val enabledOverlays: List<String>,
    val disabledOverlays: List<String>,
)

@Composable
fun getOverlayList(): OverlayListData {
    val overlayList by remember { mutableStateOf(fetchOverlayList()) }

    return overlayList
}

private fun fetchOverlayList(): OverlayListData {
    val result = SU.run("cmd overlay list").stdout()
    val overlayList = result.lines().filter { it.contains("themed") }.sorted()

    val unsupportedOverlays = overlayList.filter { it.contains("---") }
    val enabledOverlays = overlayList.filter { it.contains("[x]") }
    val disabledOverlays = overlayList.filter { it.contains("[ ]") }

    return OverlayListData(overlayList, unsupportedOverlays, enabledOverlays, disabledOverlays)
}


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
        ), label = ""
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

object SharedPreferencesManager {
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    }

    fun getSharedPreferences(): SharedPreferences {
        return sharedPreferences
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemedManagerTheme {

                SharedPreferencesManager.initialize(applicationContext)

                val root by rememberSaveable {
                    mutableStateOf(SH.run("su -c whoami").stdout())
                }

                if ("root" !in root) {
                    Toast.makeText(
                        LocalContext.current, getString(R.string.no_root_access), Toast.LENGTH_SHORT
                    ).show()
                } else {
                }


                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.cardcol
                ) {
                    Main()
                }
            }
        }
    }
}

@Composable
fun getOverlay(): String {
    val overlay = rememberSaveable {
        mutableStateOf(SU.run("su -c cmd overlay").stdout())
    }

    return overlay.value
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


private operator fun Navigation.invoke() {

}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItems.ColorsTab, NavigationItems.IconsTab,
        //  NavigationItems.FontsTab,
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
        }/*composable(NavigationItems.FontsTab.route) {
            AppsTab()
        }*/
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
            modifier = Modifier.padding(16.dp), text = "", fontSize = 14.sp
        )
    }
}


//@Preview()
@Composable
fun TopAppBar() {
    val context = LocalContext.current

    TopAppBar(elevation = 0.dp,
        title = { Text(stringResource(R.string.app_name)) },
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

    val overlay = SU.run("su -c cmd overlay").stdout()

    if ("exclusive" in overlay) {
        SH.run("su -c cmd overlay enable-exclusive --category themed.$overlayname")

    } else {
        SU.run("su -c cmd overlay enable themed.$overlayname")
    }


    val sharedPreferences = SharedPreferencesManager.getSharedPreferences()
    val restart_system_ui: Boolean = sharedPreferences.getBoolean("restart_system_ui", false)

    if (restart_system_ui) {
        SU.run("su -c killall com.android.systemui")
    }

    Firebase.analytics.logEvent("Overlay_Selected") {
        param("Overlay_Name", overlayname)
    }


}



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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import kotlinx.coroutines.launch
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
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {

            ThemedManagerTheme {

                SharedPreferencesManager.initialize(applicationContext)
                val context = LocalContext.current

                val sharedPreferences = SharedPreferencesManager.getSharedPreferences()
                val onBoardingCompleted: Boolean =
                    sharedPreferences.getBoolean("onBoardingCompleted", false)

                if (onBoardingCompleted) {
                    splashScreen.setKeepOnScreenCondition { true }


                    val root by rememberSaveable {
                        mutableStateOf(SH.run("su -c whoami").stdout())
                    }

                    if ("root" !in root) {
                        Toast.makeText(
                            LocalContext.current,
                            getString(R.string.no_root_access),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    getOverlayList()
                    getOverlay()
                    Main()
                    LaunchedEffect(Unit) {
                        //  delay(1000)
                        splashScreen.setKeepOnScreenCondition { false }
                    }
                } else {
                    splashScreen.setKeepOnScreenCondition { false }

                    Box(
                        Modifier.fillMaxSize(),
                       contentAlignment = Alignment.Center
//                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        val pageCount = 4
                        val pagerState = rememberPagerState {
                            pageCount
                        }
                        HorizontalPager(
                            state = pagerState,
                            Modifier.fillMaxHeight(0.8f),
                            userScrollEnabled = false
                        ) {

                                index ->

                            when (index) {
                                0 -> {
                                    onBoarding(
                                        image = R.drawable.main_logo_circle_mask00000,
                                        text = "This app uses RROs (resource runtime overlays) to overlay colors, icons, booleans, etc. Full compatibility with OEM ROMs cannot be guaranteed as new resources cannot be added"
                                    )

                                }
                                1 -> {
                                    onBoarding(
                                        image = R.drawable.magisk_logo,
                                        text = "This app requires Root access in order to apply overlays"
                                    )

                                }
                                2 -> {
                                    onBoarding(
                                        image = R.drawable.dead_android,
                                        text = "\n" + "If your system fails to boot after applying an overlay, click vol+ during the boot animation, and all themed overlays will be disabled"
                                    )

                                }
                                3 -> {
                                    onBoarding(
                                        image = R.drawable.telegram_logo,
                                        text = "You can get support and request compatibility fixes in the Telegram group"
                                    )

                                }
                            }
                        }
                        val coroutineScope = rememberCoroutineScope()

                         Row(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding( 32.dp),
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Button(
                                onClick = {
                                    coroutineScope.launch {

                                        if (pagerState.currentPage == 0) {
                                            sharedPreferences.edit()
                                                .putBoolean("onBoardingCompleted", true).apply()
                                            val intent =
                                                Intent(this@MainActivity, MainActivity::class.java)
                                            finish()
                                            startActivity(intent)
                                        } else {
                                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                        }
                                    }
                                }
                            ) {
                                when (pagerState.currentPage) {
                                    0 -> {
                                        androidx.compose.material3.Text(text = "Skip")

                                    }

                                    else -> {
                                        androidx.compose.material3.Text(text = "Back")
                                    }
                                }
                            }
                           // Spacer(modifier = Modifier.fillMaxWidth())
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        if (pagerState.currentPage == 1) {

                                            val root = SH.run("su -c whoami").stdout()


                                            if ("root" !in root) {
                                                Toast.makeText(
                                                    context,
                                                    getString(R.string.no_root_access),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                        }
                                        if (pagerState.currentPage == pageCount - 1) {
                                            sharedPreferences.edit()
                                                .putBoolean("onBoardingCompleted", true).apply()
                                            val intent =
                                                Intent(this@MainActivity, MainActivity::class.java)
                                            finish()
                                            startActivity(intent)
                                        }
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            ) {
                                when (pagerState.currentPage) {
                                    pageCount - 1 -> {
                                        androidx.compose.material3.Text(text = "Get started")

                                    }
                                    1 -> {
                                        androidx.compose.material3.Text(text = "Grant access")

                                    }
                                    else -> {
                                        androidx.compose.material3.Text(text = "Next")
                                    }
                                }
                            }

                        }
                    }
                }

            }
        }
    }
}

@Composable
fun onBoarding(image: Int, text: String) {


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,

    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 80.dp),
            contentScale = ContentScale.FillWidth
        )
        Spacer(modifier = Modifier.height(60.dp))

        androidx.compose.material3.Text(
            text = text, Modifier.padding(horizontal = 30.dp), textAlign = TextAlign.Center,
            // fontWeight = FontWeight.Bold,
            fontSize = 16.sp, color = MaterialTheme.colors.textcol
        )

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



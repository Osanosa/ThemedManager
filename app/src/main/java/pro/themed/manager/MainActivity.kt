@file:OptIn(ExperimentalMaterialApi::class)

package pro.themed.manager

import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.util.*
import android.widget.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.*
import androidx.core.graphics.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.*
import androidx.navigation.compose.*
import com.android.apksigner.*
import com.google.android.gms.ads.*
import com.google.firebase.analytics.ktx.*
import com.google.firebase.crashlytics.ktx.*
import com.google.firebase.database.*
import com.google.firebase.ktx.*
import com.jaredrummler.ktsh.*
import com.jaredrummler.ktsh.Shell.Companion.SH
import com.jaredrummler.ktsh.Shell.Companion.SU
import kotlinx.coroutines.*
import log
import pro.themed.manager.comps.*
import pro.themed.manager.ui.theme.*
import pro.themed.manager.utils.*
import pro.themed.manager.utils.GlobalVariables.magiskVersion
import pro.themed.manager.utils.GlobalVariables.themedId
import pro.themed.manager.utils.GlobalVariables.whoami


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext // Initialize the appContext property

        // Initialize any application-wide resources or settings here
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}

data class OverlayListData(
    val overlayList: List<String>,
    val unsupportedOverlays: List<String>,
    val enabledOverlays: List<String>,
    val disabledOverlays: List<String>,
)


@Composable
fun AdmobBanner(modifier: Modifier = Modifier) {
    if (!MyApplication.appContext.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            .getBoolean("isContributor", false)
    ) {
        val isAdLoaded = remember { mutableStateOf(false) }

        AndroidView(modifier = Modifier.fillMaxWidth(), factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.LARGE_BANNER)
                adUnitId = "ca-app-pub-5920419856758740/9976311451"
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        isAdLoaded.value = true
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        isAdLoaded.value = false
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        })

        if (isAdLoaded.value) {
            Spacer(modifier = Modifier.height(8.dp))
        }

    }
}

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


        SharedPreferencesManager.initialize(applicationContext)
        Firebase.crashlytics.setCustomKey("magisk version", magiskVersion)
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        if (!SharedPreferencesManager.getSharedPreferences().getBoolean("isContributor", false)) {
            loadInterstitial(this)
        }
        fun foregroundServiceRunning(): Boolean {
            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
                if (MyForegroundService::class.java.name == service.service.className) {
                    return true

                }
            }
            return false
        }

        if (foregroundServiceRunning()) {
            Log.d("service", "attempting to stop")
            SU.run("am stop-service pro.themed.manager/pro.themed.manager.utils.MyForegroundService")
            SU.run("killall pro.themed.manager")

        }
        setContent {

            ThemedManagerTheme {
                val context = MyApplication.appContext
                val sharedPreferences = SharedPreferencesManager.getSharedPreferences()
                if ("root" !in whoami) {
                    Toast.makeText(
                        context, getString(R.string.no_root_access), Toast.LENGTH_LONG
                    ).show()
                }
                if (sharedPreferences.getBoolean("onBoardingCompleted", false)) {
                    splashScreen.setKeepOnScreenCondition { true }
                    getOverlayList()
                    Main()
                    LaunchedEffect(Unit) {
                        splashScreen.setKeepOnScreenCondition { false }

                        // Initialize Firebase Database reference
                        val database =
                            FirebaseDatabase.getInstance("https://themed-manager-default-rtdb.europe-west1.firebasedatabase.app")
                        val reference = database.getReference("Contributors/$themedId")

                        var isSubkeyPresent: Boolean

                        // Add a ValueEventListener to check for the subkey just once
                        reference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                // Check if the subkey exists
                                isSubkeyPresent = dataSnapshot.exists()
                                Log.d("DATABASE", "THEMED ID IS $themedId")

                                // If the subkey doesn't exist, set isSubkeyPresent to false
                                if (isSubkeyPresent) {
                                    sharedPreferences.edit().putBoolean("isContributor", true)
                                        .apply()
                                    sharedPreferences.edit().putString(
                                        "isContributorDate",
                                        "${dataSnapshot.getValue(String::class.java)}"
                                    ).apply()

                                    Log.d("DATABASE", "ENTRY FOUND")
                                } else {
                                    sharedPreferences.edit().putBoolean("isContributor", false)
                                        .apply()
                                    sharedPreferences.edit().putString("isContributorDate", "null")
                                        .apply()

                                    Log.d("DATABASE", "ENTRY NOT FOUND")

                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Handle any errors here
                                sharedPreferences.edit().putBoolean("isContributor", false).apply()
                                Log.d("DATABASE", "ENTRY SEARCH FAILED")
                            }
                        })
                    }
                } else {
                    splashScreen.setKeepOnScreenCondition { false }
                    OnBoardingPage(sharedPreferences, context)
                }

            }
        }
    }

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    private fun OnBoardingPage(
        sharedPreferences: SharedPreferences, context: Context
    ) {
        Box(
            Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            //                        verticalArrangement = Arrangement.SpaceBetween,
        ) {
            val pageCount = 5
            val pagerState = rememberPagerState {
                pageCount
            }
            HorizontalPager(
                state = pagerState, Modifier.fillMaxHeight(0.8f),
            ) {

                    index ->

                when (index) {
                    0 -> {
                        OnBoarding(
                            image = R.drawable.main_logo_circle_mask00000,
                            text = stringResource(R.string.onboarding0)
                        )

                    }

                    1 -> {
                        OnBoarding(
                            image = R.drawable.magisk_logo,
                            text = stringResource(R.string.onboarding1)
                        )

                    }

                    2 -> {
                        OnBoarding(
                            image = R.drawable.dead_android,
                            text = stringResource(R.string.onboarding2)
                        )

                    }

                    3 -> {
                        OnBoarding(
                            image = R.drawable.telegram_logo,
                            text = stringResource(R.string.onboarding3)
                        )

                    }

                    4 -> {
                        OnBoarding(
                            image = R.drawable.localazy_logo,
                            text = stringResource(R.string.onboarding4)
                        )

                    }
                }
            }
            val coroutineScope = rememberCoroutineScope()

            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                OutlinedButton(shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface),
                    onClick = {
                        coroutineScope.launch {

                            if (pagerState.currentPage == 0) {
                                SH.run("su")
                                sharedPreferences.edit().putBoolean("onBoardingCompleted", true)
                                    .apply()
                                val intent = Intent(this@MainActivity, MainActivity::class.java)
                                finish()
                                startActivity(intent)
                            } else {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    }) {
                    when (pagerState.currentPage) {
                        0 -> {
                            androidx.compose.material3.Text(
                                text = "Skip", color = MaterialTheme.colors.textcol
                            )

                        }

                        else -> {
                            androidx.compose.material3.Text(
                                text = "Back", color = MaterialTheme.colors.textcol
                            )
                        }
                    }
                }
                // Spacer(modifier = Modifier.fillMaxWidth())
                OutlinedButton(shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface),
                    onClick = {
                        coroutineScope.launch {
                            if (pagerState.currentPage == 1) {


                                if ("root" !in whoami) {
                                    Toast.makeText(
                                        context,
                                        getString(R.string.no_root_access),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                            if (pagerState.currentPage == pageCount - 1) {
                                sharedPreferences.edit().putBoolean("onBoardingCompleted", true)
                                    .apply()
                                val intent = Intent(this@MainActivity, MainActivity::class.java)
                                finish()
                                startActivity(intent)
                            }
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }) {
                    when (pagerState.currentPage) {
                        pageCount - 1 -> {
                            androidx.compose.material3.Text(
                                text = "Get started", color = MaterialTheme.colors.textcol
                            )

                        }

                        1 -> {
                            androidx.compose.material3.Text(
                                text = "Grant access", color = MaterialTheme.colors.textcol
                            )

                        }

                        else -> {
                            androidx.compose.material3.Text(
                                text = "Next", color = MaterialTheme.colors.textcol
                            )
                        }
                    }
                }

            }
        }
    }

    override fun onDestroy() {
        removeInterstitial()
        super.onDestroy()
    }
}

@Composable
fun OnBoarding(image: Int, text: String) {


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


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")

//@Preview
@Composable
fun Main() {
    Column {

        val navController = rememberNavController()

        Scaffold(
            backgroundColor = MaterialTheme.colors.cardcol,
            //topBar = { TopAppBar() },

        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (getOverlayList().overlayList.isEmpty()) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = "Themed overlays are missing\nTry installing module from about screen"
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                           Navigation(navController)
                        }
                        NavigationRailSample(navController)}
                }
            }
        }
    }
}
@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }


@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }
fun getContrastColor(background: Int): Color {
    // Determine a threshold value for choosing white or black text
    val threshold = 129

    val luminance = (0.299 * background.red + 0.587 * background.green + 0.114 * background.blue)
    return if (luminance > threshold) Color.Black else Color.White
}

@Preview
@Preview
@Composable
fun ColorReferencesEditor() {

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
                      imageVector = ImageVector.vectorResource(id = items.icon),
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
    CoroutineScope(Dispatchers.IO).launch {
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

}

fun buildOverlay() {
    CoroutineScope(Dispatchers.IO).launch {
ApkSignerTool.main(arrayOf("help",))
        var path = Shell.SU.run("pwd").stdout().log()
        SU.run("""aapt p -f -v -M AndroidManifest.xml -I /system/framework/framework-res.apk -S res -F unsigned.apk --min-sdk-version 26 --target-sdk-version 29""")
        SU.run("""zipsigner unsigned.apk signed.apk""")
        SU.run("""pm install signed.apk""")

    }
}


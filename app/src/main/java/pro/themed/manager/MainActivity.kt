package pro.themed.manager

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.jaredrummler.ktsh.Shell.Companion.SH
import com.jaredrummler.ktsh.Shell.Companion.SU
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pro.themed.manager.ui.theme.ThemedManagerTheme
import pro.themed.manager.ui.theme.cardcol
import pro.themed.manager.ui.theme.textcol
import pro.themed.manager.utils.GlobalVariables.magiskVersion
import pro.themed.manager.utils.GlobalVariables.themedId
import pro.themed.manager.utils.GlobalVariables.whoami
import pro.themed.manager.utils.MyForegroundService
import pro.themed.manager.utils.NavigationItems
import pro.themed.manager.utils.loadInterstitial
import pro.themed.manager.utils.removeInterstitial


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

@Preview
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

        Scaffold(backgroundColor = MaterialTheme.colors.cardcol,
            topBar = { TopAppBar() },
            bottomBar = {
                if (getOverlayList().overlayList.isNotEmpty()) {
                    BottomNavigationBar(navController)
                }
            }) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (getOverlayList().overlayList.isEmpty()) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = "Themed overlays are missing\nTry installing module from about screen"
                    )

                } else {
                    PaddingValues(bottom = 200.dp)
                    pro.themed.manager.utils.Navigation(navController)
                }
            }
        }
        //ColorsTab()
    }
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
        SU.run("""aapt p -f -v -M AndroidManifest.xml -I /system/framework/framework-res.apk -S res -F unsigned.apk --min-sdk-version 26 --target-sdk-version 29""")
        SU.run("""zipsigner unsigned.apk signed.apk""")
        SU.run("""pm install signed.apk""")

    }
}


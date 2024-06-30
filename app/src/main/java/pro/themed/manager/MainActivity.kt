package pro.themed.manager

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.jaredrummler.ktsh.Shell
import com.jaredrummler.ktsh.Shell.Companion.SH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pro.themed.manager.components.OnBoarding
import pro.themed.manager.comps.NavigationRailSample
import pro.themed.manager.ui.theme.ThemedManagerTheme
import pro.themed.manager.ui.theme.contentcol
import pro.themed.manager.utils.GlobalVariables.magiskVersion
import pro.themed.manager.utils.GlobalVariables.themedId
import pro.themed.manager.utils.GlobalVariables.whoami
import pro.themed.manager.utils.MyForegroundService
import pro.themed.manager.utils.Navigation
import pro.themed.manager.utils.loadInterstitial
import pro.themed.manager.utils.loadRewarded
import pro.themed.manager.utils.removeInterstitial
import java.io.IOException

data class OverlayListData(
    val overlayList: List<String>,
    val unsupportedOverlays: List<String>,
    val enabledOverlays: List<String>,
    val disabledOverlays: List<String>,
)


fun fetchOverlayList(): OverlayListData {
    return try {
        val result = Shell("su").run("cmd overlay list").stdout()
        val overlayList = result.lines().filter { it.contains("themed") }.sorted()
        val unsupportedOverlays = overlayList.filter { it.contains("---") }
        val enabledOverlays = overlayList.filter { it.contains("[x]") }
        val disabledOverlays = overlayList.filter { it.contains("[ ]") }
        "overlay list".log()
        OverlayListData(overlayList, unsupportedOverlays, enabledOverlays, disabledOverlays)
    } catch (e: IOException) {
        OverlayListData(emptyList(), emptyList(), emptyList(), emptyList()).also {
            Toast.makeText(
                MainActivity.appContext,
                "Error while reading overlay list. \n check su access",
                Toast.LENGTH_SHORT
            ).show()
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

/*fun requestStoragePermission(context: Context) {
    val intent = Intent()
    intent.setAction(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
    intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null))
    (context as Activity).startActivityForResult(intent, 0)
    ActivityCompat.requestPermissions(
        context, arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
        ), 0
    )
}*/
class MainActivity : ComponentActivity() {
    companion object {
        lateinit var appContext: Context
            private set
        var overlayList: OverlayListData by mutableStateOf(fetchOverlayList())
        var isDark by mutableStateOf("")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SharedPreferencesManager.initialize(applicationContext)
        Firebase.crashlytics.setCustomKey("magisk version", magiskVersion)
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        appContext = applicationContext // Initialize the appContext property
        // Set the custom UncaughtExceptionHandler
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val stackTrace = Log.getStackTraceString(throwable)
            shareStackTrace(stackTrace)
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
            Shell("su").run("killall pro.themed.manager")
            Log.d("service", "attempting to stop")

        }
        setContent {
            enableEdgeToEdge()
            LaunchedEffect(Unit) {
                if (!SharedPreferencesManager.getSharedPreferences()
                        .getBoolean("isContributor", false)
                ) {
                    loadInterstitial(appContext)
                    loadRewarded(appContext)
                }
            }

            ThemedManagerTheme {
                val context = appContext
                val sharedPreferences = SharedPreferencesManager.getSharedPreferences()

                if (sharedPreferences.getBoolean("onBoardingCompleted", false)) {
                    splashScreen.setKeepOnScreenCondition { true }
                    if ("root" !in whoami) {
                        Toast.makeText(
                            context, getString(R.string.no_root_access), Toast.LENGTH_LONG
                        ).show()
                    }
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

    private fun shareStackTrace(stackTrace: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, stackTrace)
        }
        startActivity(Intent.createChooser(intent, "Share stack trace"))
    }

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    private fun OnBoardingPage(
        sharedPreferences: SharedPreferences, context: Context,
    ) {
        Column(
            Modifier.fillMaxSize(),
            //                        verticalArrangement = Arrangement.SpaceBetween,
        ) {
            val pageCount = 5
            val pagerState = rememberPagerState {
                pageCount
            }
            HorizontalPager(
                state = pagerState, Modifier.weight(1f),
            ) { index ->

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
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
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
                            Text(
                                text = "Skip", color = contentcol
                            )

                        }

                        else -> {
                            Text(
                                text = "Back", color = contentcol
                            )
                        }
                    }
                }
                // Spacer(modifier = Modifier.fillMaxWidth())
                OutlinedButton(shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
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
                            Text(
                                text = "Get started", color = contentcol
                            )

                        }

                        1 -> {
                            Text(
                                text = "Grant access", color = contentcol
                            )

                        }

                        else -> {
                            Text(
                                text = "Next", color = contentcol
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


//@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main() {
    Column {
        val navController = rememberNavController()

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Navigation(navController)
                }
                Box(
                    modifier = Modifier
                        .zIndex(10f)
                        .shadow(8.dp)

                ) {
                    NavigationRailSample(navController)
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

fun overlayEnable(overlayname: String) {
    CoroutineScope(Dispatchers.IO).launch {
        val overlay = Shell("su").run("su -c cmd overlay").stdout()

        if ("exclusive" in overlay) {
            SH.run("su -c cmd overlay enable-exclusive --category themed.$overlayname")

        } else {
            Shell("su").run("su -c cmd overlay enable themed.$overlayname")
        }

        val sharedPreferences = SharedPreferencesManager.getSharedPreferences()
        val restart_system_ui: Boolean = sharedPreferences.getBoolean("restart_system_ui", false)

        if (restart_system_ui) {
            Shell("su").run("su -c killall com.android.systemui")
        }

        Firebase.analytics.logEvent("Overlay_Selected") {
            param("Overlay_Name", overlayname)
        }
        MainActivity.overlayList = fetchOverlayList()
    }

}

fun buildOverlay(path: String = "") {
    CoroutineScope(Dispatchers.IO).launch {
        val compileShell = Shell("su")
        compileShell.addOnStderrLineListener(object : Shell.OnLineListener {
            override fun onLine(line: String) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(MainActivity.appContext, line, Toast.LENGTH_SHORT).show()
                    line.log()
                }
            }
        })
        compileShell.addOnCommandResultListener(object : Shell.OnCommandResultListener {
            override fun onResult(result: Shell.Command.Result) {
                result.log()
            }

        })
        compileShell.run("cd $path")
        compileShell.run("pwd")
        compileShell.run("""aapt p -f -v -M AndroidManifest.xml -I /system/framework/framework-res.apk -S res -F unsigned.apk --min-sdk-version 26 --target-sdk-version 29""").stderr.log()
        compileShell.run("""zipsigner unsigned.apk signed.apk""").log()
        compileShell.run("""pm install signed.apk""").log()
        MainActivity.overlayList = fetchOverlayList()
    }
}
package pro.themed.manager

import android.app.ActivityManager
import android.content.*
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.*
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pro.themed.manager.components.OnBoardingPage
import pro.themed.manager.comps.NavigationRailSample
import pro.themed.manager.ui.theme.ThemedManagerTheme
import pro.themed.manager.utils.*
import pro.themed.manager.utils.GlobalVariables.suVersion
import pro.themed.manager.utils.GlobalVariables.whoami

class MainActivity : ComponentActivity() {

    companion object {
        lateinit var appContext: Context
            private set
        var overlayList: OverlayListData by mutableStateOf(fetchOverlayList())
        var isDark by mutableStateOf("")

    }
    fun shareStackTrace(stackTrace: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, stackTrace)
        }.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ContextCompat.startActivity(this@MainActivity, Intent.createChooser(intent, "Share stack trace"), null)
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        SharedPreferencesManager.initialize(applicationContext)
        Firebase.crashlytics.setCustomKey("su version", suVersion)
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        appContext = applicationContext // Initialize the appContext property
        // Set the custom UncaughtExceptionHandler
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val stackTrace = Log.getStackTraceString(throwable)
            stackTrace.logError()
            shareStackTrace(stackTrace)
        }

        fun foregroundServiceRunning(): Boolean {
            val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
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
                    CoroutineScope(Dispatchers.IO).launch {
                        if ("root" !in whoami) {
                            Toast.makeText(
                                context, getString(R.string.no_root_access), Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    Main()
                    LaunchedEffect(Unit) {
                        splashScreen.setKeepOnScreenCondition { false }
                        // Initialize Firebase Database reference
                        FirebaseIsContributor(sharedPreferences)
                    }
                } else {
                    splashScreen.setKeepOnScreenCondition { false }
                    OnBoardingPage()
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
                        .windowInsetsPadding(WindowInsets.safeDrawing)

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


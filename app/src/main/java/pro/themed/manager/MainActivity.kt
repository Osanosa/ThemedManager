package pro.themed.manager

import android.app.*
import android.content.*
import android.os.*
import android.util.*
import android.widget.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import androidx.core.content.*
import androidx.core.graphics.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.*
import com.google.firebase.crashlytics.ktx.*
import com.google.firebase.ktx.*
import com.jaredrummler.ktsh.*
import kotlinx.coroutines.*
import pro.themed.manager.components.*
import pro.themed.manager.comps.*
import pro.themed.manager.ui.theme.*
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

        appContext = applicationContext

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
                if (!SharedPreferencesManager.getSharedPreferences().getBoolean("isContributor", false)) {
                    loadInterstitial(appContext)
                    loadRewarded(appContext)
                }
            }

            ThemedManagerTheme {
                val context = appContext
                val sharedPreferences = SharedPreferencesManager.getSharedPreferences()

                if (sharedPreferences.getBoolean("onBoardingCompleted", false)) {
                    splashScreen.setKeepOnScreenCondition { true }
                    LaunchedEffect(Unit) {
                        CoroutineScope(Dispatchers.IO).launch {
                            if ("root" !in whoami) {
                                Toast.makeText(context, getString(R.string.no_root_access), Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    Main()
                    LaunchedEffect(Unit) {
                        splashScreen.setKeepOnScreenCondition { false }
                        // Initialize Firebase Database reference
                        FirebaseIsContributor(sharedPreferences)
                    }
                }
                else {
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
@OptIn(ExperimentalMaterial3Api::class) @Composable fun Main() {
    Column {
        val navController = rememberNavController()

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier
                    .weight(1f)
                    .windowInsetsPadding(WindowInsets.safeDrawing)

                ) {
                    Navigation(navController)
                }
                Box(modifier = Modifier
                    .zIndex(10f)
                    .shadow(8.dp)

                ) {
                    NavigationRailSample(navController)
                }
            }

        }

    }
}

@Composable fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Composable fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

fun getContrastColor(background: Int): Color {
    // Determine a threshold value for choosing white or black text
    val threshold = 129

    val luminance = (0.299 * background.red + 0.587 * background.green + 0.114 * background.blue)
    return if (luminance > threshold) Color.Black else Color.White
}


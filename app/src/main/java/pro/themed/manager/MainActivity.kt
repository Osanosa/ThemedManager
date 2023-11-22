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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import com.jaredrummler.ktsh.Shell
import com.jaredrummler.ktsh.Shell.Companion.SH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pro.themed.manager.comps.NavigationRailSample
import pro.themed.manager.ui.theme.ThemedManagerTheme
import pro.themed.manager.ui.theme.cardcol
import pro.themed.manager.ui.theme.textcol
import pro.themed.manager.utils.GlobalVariables.magiskVersion
import pro.themed.manager.utils.GlobalVariables.themedId
import pro.themed.manager.utils.GlobalVariables.whoami
import pro.themed.manager.utils.MyForegroundService
import pro.themed.manager.utils.Navigation
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


@Composable
fun AdmobBanner() {
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

    val result = Shell("su").run("cmd overlay list").stdout()
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
            Shell("su").run("killall pro.themed.manager")
            Log.d("service", "attempting to stop")

        }
        setContent {

            ThemedManagerTheme {
                val context = MyApplication.appContext
                val sharedPreferences = SharedPreferencesManager.getSharedPreferences()

                if (sharedPreferences.getBoolean("onBoardingCompleted", false)) {
                    splashScreen.setKeepOnScreenCondition { true }
                    if ("root" !in whoami) {
                        Toast.makeText(
                            context, getString(R.string.no_root_access), Toast.LENGTH_LONG
                        ).show()
                    }
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

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Navigation(navController)
                    }
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
    }

}

fun buildOverlay(path: String = "") {
    CoroutineScope(Dispatchers.IO).launch {

    /*    val signerConfig = ApkSigner.SignerConfig.Builder("overlay", privateKey, certs).build()
        val signerConfigs: MutableList<ApkSigner.SignerConfig> = ArrayList()
        signerConfigs.add(signerConfig)
        val source = "$modulePath/onDemandCompiler/unsigned.apk"
        val signedOverlayAPKPath = "$modulePath/onDemandCompiler/signed.apk"

        ApkSigner.Builder(signerConfigs)
            .setV1SigningEnabled(false)
            .setV2SigningEnabled(true)
            .setInputApk(File(source))
            .setOutputApk(File(signedOverlayAPKPath))
            .setMinSdkVersion(Build.VERSION.SDK_INT)
            .build()
            .sign()

        com.android.apksigner.ApkSignerTool.main(
            arrayOf(
                "sign",
                "--key",
                "$modulePath/onDemandCompiler/testkey.pk8",
                "--cert",
                "$modulePath/onDemandCompiler/testkey.x509.pem",
                "--out",
                "signed.apk",
                "unsigned.apk"
            )
        )
*/
val compileShell = Shell("su")
        compileShell.run("cd $path")
        compileShell.run("pwd")
        compileShell.run("""aapt p -f -v -M AndroidManifest.xml -I /system/framework/framework-res.apk -S res -F unsigned.apk --min-sdk-version 26 --target-sdk-version 29""").stderr.log()
        compileShell.run("""zipsigner unsigned.apk signed.apk""").log()
        compileShell.run("""pm install signed.apk""").log()

    }
}


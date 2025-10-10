package pro.themed.perappdownscale

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun AdmobBanner(context: Context) {
    val sharedPrefs = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    var isContributor by remember { mutableStateOf(sharedPrefs.getBoolean("isContributor", false)) }
    

        val listener = { prefs: android.content.SharedPreferences, key: String? ->
            when (key) {
                "isContributor" -> isContributor = prefs.getBoolean(key, false)
            }
        }
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
        


    
    if (!isContributor) {
        var isAdLoaded by remember { mutableStateOf(false) }
        var aderror: LoadAdError? by remember { mutableStateOf(null) }

            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    AdView(context).apply {
                        setAdSize(AdSize.SMART_BANNER)
                        adUnitId = "ca-app-pub-5920419856758740/9803021836"
                        adListener =
                            object : AdListener() {
                                override fun onAdLoaded() {
                                    super.onAdLoaded()
                                    Log.e("AdmobBanner", "Ad loaded")
                                    isAdLoaded = true
                                }

                                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                    super.onAdFailedToLoad(loadAdError)
                                    aderror = loadAdError
                                    Log.e(
                                        "AdmobBanner",
                                        "Ad failed to load: ${loadAdError.message}",
                                    )
                                    isAdLoaded = false
                                }
                            }
                        loadAd(AdRequest.Builder().build())
                    }
                },
            )
        }
}

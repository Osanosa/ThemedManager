package pro.themed.perappdownscale

import android.content.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.viewinterop.*
import com.google.android.gms.ads.*

@Composable
fun AdmobBanner(context: Context) {
    if (!context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            .getBoolean("isContributor", false)
    ) {
        val isAdLoaded = remember { mutableStateOf(false) }

        AndroidView(modifier = Modifier.fillMaxWidth(), factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.FULL_BANNER)
                adUnitId = "ca-app-pub-5920419856758740/9803021836"
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



    }
}

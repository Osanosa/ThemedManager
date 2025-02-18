package pro.themed.autorefreshrate

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

var mInterstitialAd: InterstitialAd? = null

fun loadInterstitial(context: Context) {
    if (
        !context
            .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            .getBoolean("isContributor", false)
    ) {
        InterstitialAd.load(
            context,
            "ca-app-pub-5920419856758740/2958268690", // Change this with your own AdUnitID!
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            }
        )
    }
}

fun showInterstitial(context: Context, onAdDismissed: () -> Unit = {}) {
    val activity = context.findActivity()

    if (
        mInterstitialAd != null &&
            activity != null &&
            !context
                .getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                .getBoolean("isContributor", false)
    ) {
        mInterstitialAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdFailedToShowFullScreenContent(
                    e: com.google.android.gms.ads.AdError
                ) {
                    mInterstitialAd = null
                }

                override fun onAdDismissedFullScreenContent() {
                    mInterstitialAd = null

                    loadInterstitial(context)
                    onAdDismissed()
                }
            }
        mInterstitialAd?.show(activity)
    }
}

fun removeInterstitial() {
    mInterstitialAd?.fullScreenContentCallback = null
    mInterstitialAd = null
}

fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

package pro.themed.pxlsrtr

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback


var mRewardedAd: RewardedAd? = null

fun loadRewarded(context: Context) {
    RewardedAd.load(context,
        "ca-app-pub-5920419856758740/1030574665",
        AdRequest.Builder().build(),
        object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mRewardedAd = null
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                mRewardedAd = rewardedAd
            }
        })
}

fun showRewarded(context: Context, onAdDismissed: () -> Unit) {


    if (mRewardedAd != null
    ) {
        mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(e: com.google.android.gms.ads.AdError) {
                mRewardedAd = null
            }

            override fun onAdDismissedFullScreenContent() {
                mRewardedAd = null

                loadRewarded(context)
                onAdDismissed()
            }
        }
        mRewardedAd?.show(PXLSRTRactivity()) {
            // Handle the reward
        }
    }
}

fun removeRewarded() {
    mRewardedAd?.fullScreenContentCallback = null
    mRewardedAd = null
}
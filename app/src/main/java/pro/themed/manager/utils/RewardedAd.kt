package pro.themed.manager.utils

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import pro.themed.manager.SharedPreferencesManager


var mRewardedAd: RewardedAd? = null

fun loadRewarded(context: Context) {
    if (!SharedPreferencesManager.getSharedPreferences().getBoolean("isContributor", false)) {
        RewardedAd.load(context,
            "ca-app-pub-5920419856758740/1939112099", //Change this with your own AdUnitID!
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
}

fun showRewarded(context: Context, onAdDismissed: () -> Unit) {
    val activity = context.findActivity()

    if (mRewardedAd != null && activity != null && !SharedPreferencesManager.getSharedPreferences()
            .getBoolean("isContributor", false)
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
        mRewardedAd?.show(activity) {
            // Handle the reward
        }

    }
}

fun removeRewarded() {
    mRewardedAd?.fullScreenContentCallback = null
    mRewardedAd = null
}


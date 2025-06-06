package pro.themed.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import pro.themed.manager.utils.GlobalVariables

class DownloadBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == "android.intent.action.DOWNLOAD_COMPLETE") {
            GlobalVariables.isdownloaded = true
        }
    }
}

package pro.themed.autorefreshrate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

class AutoRefreshRateBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && sharedPreferences.getBoolean(
                "autoRateOnBoot",
                false
            )
        ) {
            val serviceIntent = Intent(context, AutoRefreshRateForegroundService::class.java)
            context.startForegroundService(serviceIntent)
            Toast.makeText(context, "Starting autorefreshrate service", Toast.LENGTH_SHORT).show()
        }
    }
}
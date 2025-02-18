package pro.themed.autorefreshrate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import com.jaredrummler.ktsh.Shell

class AutoRefreshRateBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

        if (
            intent.action == Intent.ACTION_BOOT_COMPLETED &&
                sharedPreferences.getBoolean("autoRateOnBoot", false)
        ) {
            val shell = Shell.SH
            shell.run("su")
            shell.run(
                "am start-foreground-service pro.themed.manager.autorefreshrate/pro.themed.autorefreshrate.AutoRefreshRateForegroundService"
            )
            Toast.makeText(context, "Starting autorefreshrate service", Toast.LENGTH_SHORT).show()
        }
    }
}

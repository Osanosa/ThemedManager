package pro.themed.autorefreshrate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pro.themed.manager.autorefreshrate.R
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class AutoRefreshRateForegroundService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private lateinit var sharedPreferences: SharedPreferences
    var countdown = 3 // Initial countdown value
    var countdownReset by Delegates.notNull<Int>()
    private var isMaxRate = false // Flag to track the rate state
    private val shell1 = Shell("su")
    private val shell2 = Shell("su")
    private var isCountdownRunning = false // Flag to ensure countdown runs only once

    override fun onCreate() {
        super.onCreate()
        sharedPreferences =
            this.applicationContext.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        startForegroundService()
    }

    private fun startForegroundService() {
        val notificationManager =
            getSystemService(NotificationManager::class.java) as NotificationManager

        val notificationChannelId = "ForegroundServiceChannel"
        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
            .setSmallIcon(R.drawable.autofps_select_24px).setContentTitle("AutoRefreshRate")
            .setContentText("Service is running...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationChannel = NotificationChannel(
            notificationChannelId, notificationChannelId, NotificationManager.IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(notificationChannel)

        startForeground(1001, notificationBuilder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        countdownReset = sharedPreferences.getInt("countdown", 3)
        val actualIntent = intent ?: Intent() // Use an empty intent if it's null
        val minRate = sharedPreferences.getString(
            "minRate", "0"
        ).toString()
        val maxRate = sharedPreferences.getString(
            "maxRate", "0"
        ).toString()
        Thread {}.start()
        shell1.run("getevent") {
            onStdOut = {
                countdown = countdownReset // Reset countdown to the initial value
                serviceScope.launch {
                    if (!isCountdownRunning) {
                        isCountdownRunning = true // Set flag to indicate countdown is running
                        if (!isMaxRate) {
                            isMaxRate = true

                            shell2.run(
                                "service call SurfaceFlinger 1035 i32 $maxRate"
                            )
                        }
                        while (countdown > 0) {
                            delay(1000)
                            countdown -= 1
                        }
                        if (countdown == 0 && isMaxRate) {
                            shell2.run(
                                "service call SurfaceFlinger 1035 i32 $minRate"
                            )
                            isMaxRate = false // Toggle the rate state
                        }
                        isCountdownRunning = false // Reset the countdown flag
                    }
                }
            }
            onStdErr = { line: String ->
                Firebase.crashlytics.log("StdErr: $line")
            }
            timeout = Shell.Timeout(1, TimeUnit.SECONDS)
        }

        startForegroundService()
        return super.onStartCommand(actualIntent, flags, startId)
    }

    override fun onDestroy() {
        //MyTileService().qsTile.updateTile()
        serviceScope.cancel()
        super.onDestroy()
        stopSelf()
        stopService(Intent(this, AutoRefreshRateForegroundService::class.java))
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}

package pro.themed.manager.utils

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
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pro.themed.manager.R

class MyForegroundService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private var countdown = 3 // Initial countdown value
    private var isMaxRate = false // Flag to track the rate state
    private val shell1 = Shell("su")
    private val shell2 = Shell("su")
    private var isCountdownRunning = false // Flag to ensure countdown runs only once
    private lateinit var sharedPreferences: SharedPreferences

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
        val notificationBuilder =
            NotificationCompat.Builder(this, notificationChannelId)
                .setSmallIcon(R.drawable.palette_48px)
                .setContentTitle("AutoRefreshRate")
                .setContentText("Service is running...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationChannel =
            NotificationChannel(
                notificationChannelId,
                notificationChannelId,
                NotificationManager.IMPORTANCE_LOW,
            )

        notificationManager.createNotificationChannel(notificationChannel)

        startForeground(1001, notificationBuilder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val actualIntent = intent ?: Intent() // Use an empty intent if it's null

        Thread {}.start()
        shell1.run("getevent") {
            onStdOut = {
                countdown = 3 // Reset countdown to the initial value
                serviceScope.launch {
                    if (!isCountdownRunning) {
                        isCountdownRunning = true // Set flag to indicate countdown is running
                        if (!isMaxRate) {
                            isMaxRate = true
                            shell2.run(
                                "service call SurfaceFlinger 1035 i32 ${
                                    sharedPreferences.getString(
                                        "maxRate",
                                        "0",
                                    ).toString()
                                }"
                            )
                        }
                        while (countdown > 0) {
                            delay(1000)
                            countdown -= 1
                        }
                        if (countdown == 0 && isMaxRate) {
                            shell2.run(
                                "service call SurfaceFlinger 1035 i32 ${
                                    sharedPreferences.getString(
                                        "minRate",
                                        "0",
                                    ).toString()
                                }"
                            )
                            isMaxRate = false // Toggle the rate state
                        }
                        isCountdownRunning = false // Reset the countdown flag
                    }
                }
            }
            onStdErr = { line: String -> Firebase.crashlytics.log("StdErr: $line") }
            timeout = Shell.Timeout(1, TimeUnit.SECONDS)
        }

        startForegroundService()
        return super.onStartCommand(actualIntent, flags, startId)
    }

    override fun onDestroy() {
        MyTileService().qsTile.updateTile()
        serviceScope.cancel()
        super.onDestroy()
        stopSelf()
        stopService(Intent(this, MyForegroundService::class.java))
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}

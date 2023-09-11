package pro.themed.manager.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pro.themed.manager.R
import pro.themed.manager.utils.GlobalVariables.maxRate
import pro.themed.manager.utils.GlobalVariables.minRate
import java.util.concurrent.TimeUnit

class MyForegroundService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private var countdown = 3 // Initial countdown value
    private var isMaxRate = false // Flag to track the rate state
    private val shell2 = Shell.SU
    private var isCountdownRunning = false // Flag to ensure countdown runs only once


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Thread {}.start()
        Shell.SH.run("su -c getevent | grep 0003") {
            onStdOut = {
                countdown = 3 // Reset countdown to the initial value
                CoroutineScope(Dispatchers.IO).launch {


                    if (!isCountdownRunning) {
                        isCountdownRunning = true // Set flag to indicate countdown is running
                        if (!isMaxRate) {
                            isMaxRate = true
                            shell2.run("service call SurfaceFlinger 1035 i32 $maxRate")
                            Log.d("test", "setting 120")
                        }
                        while (countdown > 0) {
                            delay(1000)
                            countdown -= 1
                            Log.d("countdown", countdown.toString())
                        }
                        if (countdown == 0 && isMaxRate) {
                            shell2.run("service call SurfaceFlinger 1035 i32 $minRate")
                            Log.d("test", "setting 60")
                            isMaxRate = false // Toggle the rate state
                        }
                        isCountdownRunning = false // Reset the countdown flag
                    }
                }
            }
            onStdErr = { line: String ->
                Log.d("ShellCommand", "StdErr: $line")
            }
            timeout = Shell.Timeout(1, TimeUnit.SECONDS)
        }

        val notificationManager =
            getSystemService(NotificationManager::class.java) as NotificationManager

        val notificationChannelId = "ForegroundServiceChannel"
        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
            .setSmallIcon(R.drawable.palette_48px).setContentTitle("").setContentText("")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationChannel = NotificationChannel(
            notificationChannelId, notificationChannelId, NotificationManager.IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(notificationChannel)

        startForeground(1001, notificationBuilder.build())
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
        stopSelf()
        stopService(Intent(this, MyForegroundService::class.java))
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}

package pro.themed.autorefreshrate

<<<<<<< Updated upstream
import android.app.*
import android.content.*
import android.os.*
=======
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
>>>>>>> Stashed changes
import androidx.core.app.NotificationCompat
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pro.themed.manager.autorefreshrate.R
import java.util.concurrent.TimeUnit

class AutoRefreshRateForegroundService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private lateinit var sharedPreferences: SharedPreferences
    private var countdownReset = 3
    private var isMaxRate = false
    private val shell1 = Shell("su")
    private val shell2 = Shell("su")
<<<<<<< Updated upstream
    private var isCountdownRunning = false
=======
    private var debounceJob: Job? = null
    private var countdownJob: Job? = null
    private val debounceTimeMillis = 5L // Debounce time in milliseconds

    companion object {
        private const val TAG = "AutoRefreshRateService"
    }
>>>>>>> Stashed changes

    override fun onCreate() {
        super.onCreate()
        sharedPreferences =
            applicationContext.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        startForegroundService()
    }

    private fun startForegroundService() {
        val notificationChannelId = "ForegroundServiceChannel"
        val notification =
            NotificationCompat.Builder(this, notificationChannelId)
                .setSmallIcon(R.drawable.autofps_select_24px)
                .setContentTitle("AutoRefreshRate")
                .setContentText("Service is running...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

        val nm = getSystemService(NotificationManager::class.java)
        nm?.createNotificationChannel(
            NotificationChannel(
                notificationChannelId,
                notificationChannelId,
                NotificationManager.IMPORTANCE_LOW
            )
        )

        startForeground(1001, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        countdownReset = sharedPreferences.getInt("countdown", 3)
        var minRate = sharedPreferences.getString("minRate", "0") ?: "0"
        var maxRate = sharedPreferences.getString("maxRate", "0") ?: "0"
        sharedPreferences.registerOnSharedPreferenceChangeListener { prefs, key ->
            when (key) {
                "countdown" -> countdownReset = prefs.getInt(key, 3)
                "minRate" -> sharedPreferences.getString(key, "0")?.let { minRate = it }
                "maxRate" -> sharedPreferences.getString(key, "0")?.let { maxRate = it }
            }
        }
        shell1.run("getevent") {
            onStdOut = {
                serviceScope.launch {
                    // Reset countdown each time output is received
                    var countdown = countdownReset
                    if (!isCountdownRunning) {
                        isCountdownRunning = true
                        if (!isMaxRate) {
                            isMaxRate = true
                            shell2.run("service call SurfaceFlinger 1035 i32 $maxRate")
                        }
                        while (countdown-- > 0) delay(1000)
                        if (isMaxRate) {
                            shell2.run("service call SurfaceFlinger 1035 i32 $minRate")
                            isMaxRate = false
                        }
                        isCountdownRunning = false
                    }
                }
            }
            onStdErr = { line -> Firebase.crashlytics.log("StdErr: $line") }
            timeout = Shell.Timeout(1, TimeUnit.SECONDS)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? = null
}

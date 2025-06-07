package pro.themed.autorefreshrate

import android.app.*
import android.content.*
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.jaredrummler.ktsh.Shell
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.*
import pro.themed.manager.autorefreshrate.R

class AutoRefreshRateForegroundService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private lateinit var sharedPreferences: SharedPreferences
    private var countdownReset = 3
    private var isMaxRate = false
    private val shell1 = Shell("su")
    private val shell2 = Shell("su")
    private var debounceJob: Job? = null
    private var countdownJob: Job? = null
    private val debounceTimeMillis = 100L // Debounce time in milliseconds

    companion object {
        private const val TAG = "AutoRefreshRateService"
    }

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
        val minRate = sharedPreferences.getString("minRate", "0") ?: "0"
        val maxRate = sharedPreferences.getString("maxRate", "0") ?: "0"

        shell1.run("getevent") {
            onStdOut = {
                debounceJob?.cancel() // Cancel any existing debounce job
                debounceJob = serviceScope.launch {
                    delay(debounceTimeMillis) // Wait for the debounce period

                    countdownJob?.cancel() // Cancel existing countdown job
                    countdownJob = serviceScope.launch {
                        // Set to max rate if not already set
                        if (!isMaxRate) {
                            isMaxRate = true
                            val command = "service call SurfaceFlinger 1035 i32 $maxRate"
                            Log.d(TAG, "Executing: $command")
                            val result = shell2.run(command)
                            Log.d(TAG, "Command stdout: ${result.stdout()}")
                            if (result.stderr().isNotBlank()) {
                                Log.e(TAG, "Command stderr: ${result.stderr()}")
                            }
                            Log.d(TAG, "Command success: ${result.isSuccess}")
                        }

                        // Start countdown
                        var countdown = countdownReset
                        while (countdown-- > 0) {
                            delay(1000)
                        }

                        // If countdown finishes (not cancelled), set to min rate
                        if (isMaxRate) { // Check ensures it was not cancelled and reset by another event
                            val command = "service call SurfaceFlinger 1035 i32 $minRate"
                            Log.d(TAG, "Executing: $command")
                            val result = shell2.run(command)
                            Log.d(TAG, "Command stdout: ${result.stdout()}")
                            if (result.stderr().isNotBlank()) {
                                Log.e(TAG, "Command stderr: ${result.stderr()}")
                            }
                            Log.d(TAG, "Command success: ${result.isSuccess}")
                            isMaxRate = false
                        }
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

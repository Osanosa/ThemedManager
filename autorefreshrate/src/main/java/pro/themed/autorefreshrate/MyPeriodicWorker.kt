package pro.themed.autorefreshrate

import android.app.*
import android.content.*
import android.widget.*
import androidx.work.*
import com.jaredrummler.ktsh.*

class MyPeriodicWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
            val shell = Shell.SH
            shell.run("su")
            shell.run("am start-foreground-service pro.themed.manager.autorefreshrate/pro.themed.autorefreshrate.AutoRefreshRateForegroundService")
            Toast.makeText(applicationContext, "Restarting autorefreshrate service", Toast.LENGTH_SHORT).show()
        shell.interrupt()
        return Result.success()
    }

    }

// To schedule the periodic task
fun schedulePeriodicTask(context: Context) {
    val workManager = WorkManager.getInstance(context)
    val request = PeriodicWorkRequest.Builder(MyPeriodicWorker::class.java, 15, java.util.concurrent.TimeUnit.MINUTES)
        .build()
    workManager.enqueueUniquePeriodicWork("my_periodic_task", ExistingPeriodicWorkPolicy.UPDATE, request)
    Toast.makeText(context, "Scheduled periodic task", Toast.LENGTH_SHORT).show()
}
fun removeScheduledTask(context: Context, taskName: String) {
    val workManager = WorkManager.getInstance(context)
    workManager.cancelUniqueWork(taskName)
}
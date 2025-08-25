package pro.themed.autorefreshrate

import android.content.Context
import android.content.pm.PackageManager
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Helper class for executing privileged commands using either root or Shizuku
 */
class PrivilegedCommandHelper(private val context: Context) {

    enum class ExecutionMode {
        ROOT,
        SHIZUKU,
        NONE
    }

    data class AvailabilityStatus(
        val rootAvailable: Boolean,
        val rootVersion: String?,
        val shizukuAvailable: Boolean,
        val shizukuPermissionGranted: Boolean
    )

    data class CommandResult(
        val success: Boolean,
        val output: String,
        val error: String,
        val exitCode: Int
    )

    /**
     * Check availability of root and Shizuku
     */
    suspend fun checkAvailability(): AvailabilityStatus = withContext(Dispatchers.IO) {
        // Check root
        val rootResult = Shell.SH.run("su -v")
        val rootAvailable = rootResult.isSuccess && !rootResult.stderr.any {
            it.contains("su: inaccessible or not found", ignoreCase = true) ||
            it.contains("permission denied", ignoreCase = true)
        }
        val rootVersion = if (rootAvailable) rootResult.stdout.firstOrNull() else null

        // Check Shizuku
        val shizukuAvailable = try {
            Shizuku.pingBinder()
        } catch (e: Exception) {
            false
        }

        val shizukuPermissionGranted = if (shizukuAvailable) {
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }

        AvailabilityStatus(
            rootAvailable = rootAvailable,
            rootVersion = rootVersion,
            shizukuAvailable = shizukuAvailable,
            shizukuPermissionGranted = shizukuPermissionGranted
        )
    }

    /**
     * Request Shizuku permission
     */
    fun requestShizukuPermission(requestCode: Int) {
        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            Shizuku.requestPermission(requestCode)
        }
    }

    /**
     * Execute a command using the specified mode
     */
    suspend fun executeCommand(
        command: String,
        mode: ExecutionMode
    ): CommandResult = withContext(Dispatchers.IO) {
        when (mode) {
            ExecutionMode.ROOT -> executeWithRoot(command)
            ExecutionMode.SHIZUKU -> executeWithShizuku(command)
            ExecutionMode.NONE -> CommandResult(
                success = false,
                output = "",
                error = "No execution mode available",
                exitCode = -1
            )
        }
    }

    /**
     * Execute command with best available method
     */
    suspend fun executeCommand(command: String): CommandResult {
        val availability = checkAvailability()

        val mode = when {
            availability.shizukuAvailable && availability.shizukuPermissionGranted -> ExecutionMode.SHIZUKU
            availability.rootAvailable -> ExecutionMode.ROOT
            else -> ExecutionMode.NONE
        }

        return executeCommand(command, mode)
    }

    private suspend fun executeWithRoot(command: String): CommandResult = withContext(Dispatchers.IO) {
        try {
            val result = Shell.SH.run("su -c '$command'")
            CommandResult(
                success = result.isSuccess,
                output = result.stdout(),
                error = result.stderr(),
                exitCode = result.exitCode
            )
        } catch (e: Exception) {
            CommandResult(
                success = false,
                output = "",
                error = e.message ?: "Unknown error",
                exitCode = -1
            )
        }
    }

    private suspend fun executeWithShizuku(command: String): CommandResult = withContext(Dispatchers.IO) {
        try {
            val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
            
            val outputReader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            while (outputReader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }

            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            val errorOutput = StringBuilder()
            while (errorReader.readLine().also { line = it } != null) {
                errorOutput.append(line).append("\n")
            }

            val exitCode = process.waitFor()
            
            CommandResult(
                success = exitCode == 0,
                output = output.toString().trimEnd(),
                error = errorOutput.toString().trimEnd(),
                exitCode = exitCode
            )
        } catch (e: Exception) {
            CommandResult(
                success = false,
                output = "",
                error = e.message ?: "Unknown error",
                exitCode = -1
            )
        }
    }
}
package pro.themed.perappdownscale

import android.content.Context
import android.content.pm.PackageManager
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku

/** Helper class for executing privileged commands using either Root or Shizuku */
class PrivilegedCommandHelper {

    enum class ExecutionMode {
        ROOT,
        SHIZUKU,
        NONE,
    }

    data class AvailabilityStatus(
        val rootAvailable: Boolean,
        val rootVersion: String? = null,
        val rootPermissionDenied: Boolean,
        val shizukuInstalled: Boolean,
        val shizukuAvailable: Boolean,
        val shizukuPermissionGranted: Boolean,
    )

    var currentMode: ExecutionMode = ExecutionMode.NONE
    private var lastRootPermissionDenied: Boolean = false

    /** Check availability of both root and Shizuku */
    suspend fun checkAvailability(
        context: Context,
        skipRootIfDenied: Boolean = false,
    ): AvailabilityStatus =
        withContext(Dispatchers.IO) {
            // Check root availability - first check if su binary exists without triggering
            // permission dialog
            val (rootAvailable, rootPermissionDenied, rootVersion) =
                if (skipRootIfDenied && lastRootPermissionDenied) {
                    // Skip root check if previously denied and this is a periodic check

                    Triple(false, true, null)
                } else {
                    try {

                        // First check if su binary exists by checking version (doesn't require root
                        // permissions)
                        val versionResult = Shell.SH.run("su -v")

                        if (
                            versionResult.exitCode == 0 &&
                                versionResult.stdout().trim().isNotEmpty()
                        ) {
                            val version = versionResult.stdout().trim()

                            // Su binary exists, but we don't know about permissions yet
                            // Mark as available but permission status unknown (will be checked when
                            // needed)
                            Triple(true, false, version)
                        } else {

                            Triple(false, false, null)
                        }
                    } catch (e: Exception) {

                        Triple(false, false, null)
                    }
                }

            // Update the last known state
            lastRootPermissionDenied = rootPermissionDenied

            // Check if Shizuku app is installed
            val shizukuInstalled =
                try {
                    context.packageManager.getPackageInfo("moe.shizuku.privileged.api", 0)
                    true
                } catch (e: Exception) {
                    false
                }

            // Check Shizuku service availability
            val shizukuAvailable =
                if (shizukuInstalled) {
                    try {
                        Shizuku.pingBinder()
                    } catch (e: Exception) {
                        false
                    }
                } else false

            val shizukuPermissionGranted =
                if (shizukuAvailable) {
                    try {
                        Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
                    } catch (e: Exception) {
                        false
                    }
                } else false

            val status =
                AvailabilityStatus(
                    rootAvailable = rootAvailable,
                    rootVersion = rootVersion,
                    rootPermissionDenied = rootPermissionDenied,
                    shizukuInstalled = shizukuInstalled,
                    shizukuAvailable = shizukuAvailable,
                    shizukuPermissionGranted = shizukuPermissionGranted,
                )

            status
        }

    /** Test root permissions (only called when user actually wants to use root) */
    suspend fun testRootPermissions(): Boolean =
        withContext(Dispatchers.IO) {
            try {

                val testResult = Shell.SH.run("su -c whoami")

                val success = testResult.stdout().contains("root")
                if (!success) {
                    // Update the denial state for future checks
                    val stderr = testResult.stderr().lowercase()
                    lastRootPermissionDenied =
                        stderr.contains("permission denied") ||
                            stderr.contains("not permitted") ||
                            stderr.contains("access denied") ||
                            stderr.contains("operation not permitted") ||
                            testResult.exitCode == 1
                } else {
                    // Reset denial state if permission was granted
                    lastRootPermissionDenied = false
                }

                success
            } catch (e: Exception) {

                // Check if exception indicates permission denial
                val exceptionMessage = e.message?.lowercase() ?: ""
                lastRootPermissionDenied =
                    exceptionMessage.contains("permission denied") ||
                        exceptionMessage.contains("access denied") ||
                        exceptionMessage.contains("not permitted")
                false
            }
        }

    /** Set the execution mode */
    fun setExecutionMode(mode: ExecutionMode) {
        currentMode = mode
    }

    /** Request Shizuku permission */
    fun requestShizukuPermission(requestCode: Int) {
        try {
            Shizuku.requestPermission(requestCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** Execute a command using the selected mode */
    suspend fun executeCommand(command: String): CommandResult =
        withContext(Dispatchers.IO) {
            when (currentMode) {
                ExecutionMode.ROOT -> executeRootCommand(command)
                ExecutionMode.SHIZUKU -> executeShizukuCommand(command)
                ExecutionMode.NONE -> CommandResult(false, "No execution mode selected", "")
            }
        }

    /** Execute command using root */
    private fun executeRootCommand(command: String): CommandResult {
        return try {
            // Use Shell.SU directly for root commands to avoid listener conflicts
            val result = Shell.SU.run(command)
            CommandResult(
                success = result.exitCode == 0,
                output = result.stdout(),
                error = result.stderr(),
            )
        } catch (e: Exception) {
            CommandResult(false, "", "Root execution failed: ${e.message}")
        }
    }

    /** Execute command using Shizuku */
    private fun executeShizukuCommand(command: String): CommandResult {
        return try {
            if (!Shizuku.pingBinder()) {
                return CommandResult(false, "", "Shizuku service not available")
            }

            if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                return CommandResult(false, "", "Shizuku permission not granted")
            }

            // For simple implementation, we'll use Shizuku's process API
            val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
            process.waitFor()

            val output = process.inputStream.bufferedReader().readText()
            val error = process.errorStream.bufferedReader().readText()

            CommandResult(success = process.exitValue() == 0, output = output, error = error)
        } catch (e: Exception) {
            CommandResult(false, "", "Shizuku execution failed: ${e.message}")
        }
    }

    /** Get current execution mode */
}

/** Result of command execution */
data class CommandResult(val success: Boolean, val output: String, val error: String)

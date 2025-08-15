package pro.themed.mtkbpfpatcher

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.firebase.FirebaseApp
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.flow.update
import java.io.File

enum class Screen { HOME, PATCHING, RESULT }

enum class FileType { BOOT_IMAGE, KERNEL_GZ, KERNEL_BIN }

data class AppState(
    val currentScreen: Screen = Screen.HOME,
    val logs: List<LogEntry> = emptyList(),
    val inputFile: String? = null,
    val outputFile: String? = null,
    val patchSuccess: Boolean = false,
    val currentBootSlot: String? = null,
    val commandOutput: String = "",
    val magiskbootAvailable: Boolean = true
)

data class LogEntry(
    val message: String, val type: LogType = LogType.INFO, val timestamp: Long = System.currentTimeMillis()
)

enum class LogType { INFO, ERROR, SUCCESS, WARNING, COMMAND, DEBUG }

/**
 * Data class to store boot image header information
 */
data class BootHeaderInfo(
    val headerVersion: String = "0",
    val kernelSize: String = "0",
    val ramdiskSize: String = "0",
    val secondSize: String = "0",
    val dtbSize: String = "0",
    val osVersion: String = "0.0.0",
    val osPatchLevel: String = "0000-00",
    val pageSize: String = "0",
    val name: String = "",
    val cmdLine: String = "",
    val kernelFormat: String = "unknown",
    val ramdiskFormat: String = "unknown"
) {
    val deviceName: String
        get() = if (name.isNotEmpty()) name else "UNKNOWN"
    
    /**
     * Generate a meaningful filename for the patched boot image
     */
    fun generateOutputFilename(): String {
        val dateFormat = java.text.SimpleDateFormat("dd-MM-yy_HH-mm", java.util.Locale.US)
        val currentDate = dateFormat.format(java.util.Date())
        
        // Extract Android version (remove decimal points)
        val androidVersion = osVersion.replace(".", "")
        
        // Create filename: DEVICE_pdPATCH-DATE_aANDROID-VER_CURRENT-DATE
        return "${deviceName}_pd${osPatchLevel}_a${osVersion}_${currentDate}"
    }
}

/**
 * Core binary patcher class that handles the pattern matching and patching
 */
class BinaryPatcher {

    // Direct byte patterns from the Python script
    private val patterns = mapOf(
        // 4.14 kernel pattern
        hexStringToByteArray("0a2450292801080a087d0a1b28010035") to hexStringToByteArray("0a2450292801080a087d0a1b1f2003d5"),

        // 4.19 kernel pattern
        hexStringToByteArray("48010035822240b980420491e10313aa") to hexStringToByteArray("1f2003d5822240b980420491e10313aa"),
        hexStringToByteArray("0A0041B92801080A087D0A1B28010035") to hexStringToByteArray("0A0041B92801080a087d0a1b1f2003d5"),
    )

    /**
     * Patch binary data by looking for known patterns and replacing them
     * @param proceedWithoutPatching If true, will repackage even if no pattern is found
     * @return Tuple of (patched bytes, pattern found, more details, was modified)
     */
    fun patchBinary(bytes: ByteArray, proceedWithoutPatching: Boolean = false): PatchResult {
        val details = StringBuilder()
        details.append("Kernel size: ${bytes.size} bytes\n")

        // Add sample bytes from the kernel for debugging
        details.append("Sample bytes from kernel:\n")
        val samples = getSampleBytes(bytes)
        details.append(samples)

        // Log the patterns we're looking for
        details.append("\nSearching for known patterns:\n")
        for ((pattern, replacement) in patterns) {
            val patternHex = bytesToHex(pattern)
            val replacementHex = bytesToHex(replacement)
            details.append("Pattern: $patternHex\n")
            details.append("Replace: $replacementHex\n")
        }

        // Try to find exact patterns
        for ((pattern, replacement) in patterns) {
            val index = indexOf(bytes, pattern)
            if (index >= 0) {
                val patternHex = bytesToHex(pattern)
                details.append("\nFound matching pattern at offset 0x${index.toString(16)}\n")
                return PatchResult(
                    bytes = applyPatch(bytes, pattern, replacement, index),
                    patternFound = patternHex,
                    details = details.toString(),
                    wasModified = true
                )
            }
        }
        var foundPartial = false
        // Check for similar patterns with small differences
        details.append("\nSearching for partial pattern matches...\n")
        for ((pattern, _) in patterns) {
            val partialMatch = findPartialMatch(bytes, pattern)
            if (partialMatch.first >= 0) {
                foundPartial = true
                details.append("Found partial match of pattern at offset 0x${partialMatch.first.toString(16)}\n")
                details.append("Matched ${partialMatch.second} of ${pattern.size} bytes\n")
                details.append("Bytes at match location: ${bytesToHex(bytes.sliceArray(partialMatch.first until partialMatch.first + pattern.size))}\n")
            }

        }

        details.append("\nNo matching pattern found in kernel\n")
        if (proceedWithoutPatching) {
            details.append("Proceeding without patching (verification mode)\n")
            return PatchResult(
                bytes = bytes, patternFound = null, details = details.toString(), wasModified = false
            )
        } else {
            details.append("This device may not need the MTK BPF patch or has a different kernel version\n")
            return PatchResult(
                bytes = bytes, patternFound = null, details = details.toString(), wasModified = false, foundPartial
            )
        }
    }

    /**
     * Get sample bytes from various locations in the kernel
     */
    private fun getSampleBytes(bytes: ByteArray): String {
        val result = StringBuilder()

        // Sample from the beginning
        result.append("First 32 bytes: ${bytesToHex(bytes.sliceArray(0 until minOf(32, bytes.size)))}\n")

        // Sample from different offsets
        val sampleOffsets = listOf(0x1000, 0x5000, 0x10000, 0x20000, 0x50000)
        for (offset in sampleOffsets) {
            if (offset < bytes.size - 32) {
                result.append("Bytes at 0x${offset.toString(16)}: ${bytesToHex(bytes.sliceArray(offset until offset + 32))}\n")
            }
        }

        // Look for some known strings that might help identify kernel patterns
        val searchStrings = listOf("\$P)(", "array_map_update_elem", "ubsan", "bpf")
        for (str in searchStrings) {
            val strBytes = str.toByteArray()
            val index = indexOf(bytes, strBytes)
            if (index >= 0) {
                result.append("Found string '$str' at offset 0x${index.toString(16)}\n")
                if (index < bytes.size - 32) {
                    result.append("Context: ${bytesToHex(bytes.sliceArray(index until index + 32))}\n")
                }
            }
        }

        return result.toString()
    }

    /**
     * Find partial match of a pattern with most bytes matching
     * Returns (offset, match count) or (-1, 0) if no good match
     */
    private fun findPartialMatch(data: ByteArray, pattern: ByteArray): Pair<Int, Int> {
        var bestOffset = -1
        var bestMatches = 0

        outer@ for (i in 0..data.size - pattern.size) {
            var matches = 0
            for (j in pattern.indices) {
                if (data[i + j] == pattern[j]) {
                    matches++
                }
            }



            // If we match at least 50% of bytes, consider it a partial match
            if (matches > pattern.size * 0.50 && matches > bestMatches) {
                bestMatches = matches
                bestOffset = i
            }
        }

        return Pair(bestOffset, bestMatches)
    }

    /**
     * Find pattern in byte array
     */
    private fun indexOf(data: ByteArray, pattern: ByteArray): Int {
        outer@ for (i in 0..data.size - pattern.size) {
            for (j in pattern.indices) {
                if (data[i + j] != pattern[j]) {
                    continue@outer
                }
            }
            return i
        }
        return -1
    }

    /**
     * Apply patch at specific position
     */
    private fun applyPatch(data: ByteArray, pattern: ByteArray, replacement: ByteArray, index: Int): ByteArray {
        val result = data.copyOf()
        System.arraycopy(replacement, 0, result, index, replacement.size)
        return result
    }

    /**
     * Convert hex string to byte array
     */
    private fun hexStringToByteArray(hex: String): ByteArray {
        val len = hex.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hex[i], 16) shl 4) + Character.digit(hex[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    /**
     * Convert byte array to hex string
     */
    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = HEX_ARRAY[v ushr 4]
            hexChars[i * 2 + 1] = HEX_ARRAY[v and 0x0F]
        }
        return String(hexChars)
    }

    companion object {

        private val HEX_ARRAY = "0123456789ABCDEF".toCharArray()
    }

    /**
     * Result of a patch operation
     */
    data class PatchResult(
        val bytes: ByteArray, val patternFound: String?, val details: String, val wasModified: Boolean, val foundPartial: Boolean = false
    ) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as PatchResult

            if (!bytes.contentEquals(other.bytes)) return false
            if (patternFound != other.patternFound) return false
            if (details != other.details) return false
            if (wasModified != other.wasModified) return false

            return true
        }

        override fun hashCode(): Int {
            var result = bytes.contentHashCode()
            result = 31 * result + (patternFound?.hashCode() ?: 0)
            result = 31 * result + details.hashCode()
            result = 31 * result + wasModified.hashCode()
            return result
        }
    }
}

/**
 * MagiskBoot helper for boot image operations
 */
class MagiskBootHelper(private val shell: Shell, var logger: (String, LogType) -> Unit) {

    private val shellHelper = ShellHelper(shell, logger)
    private var magiskbootPath: String? = null
    
    // Common patterns for MTK BPF patching
    private val patchPatterns = listOf(
        // 4.14 kernel pattern
        "0a2450292801080a087d0a1b28010035" to "0a2450292801080a087d0a1b1f2003d5",
        
        // 4.19 kernel pattern
        "48010035822240b980420491e10313aa" to "1f2003d5822240b980420491e10313aa",
        "0A0041B92801080A087D0A1B28010035" to "0A0041B92801080a087d0a1b1f2003d5",
    )

    /**
     * Get the current magiskboot path
     */
    fun getMagiskbootPath(): String? {
        return magiskbootPath
    }
    
    /**
     * Set the path to magiskboot binary
     * @param path The path to the magiskboot binary
     */
    fun setMagiskbootPath(path: String) {
        logger("Setting magiskboot path to: $path", LogType.DEBUG)
        magiskbootPath = path
    }

    /**
     * Extract header information from boot image
     * @return BootHeaderInfo object with extracted information
     */
    suspend fun extractBootHeaderInfo(bootImage: File, context: Context): BootHeaderInfo? {
        // Verify magiskboot is available first
        if (!verifyMagiskbootAvailable()) {
            logger("ERROR: Magiskboot is not available. Cannot extract header information.", LogType.ERROR)
            return null
        }
        // Create a temporary directory for extraction in app cache
        val tempDir = File(context.cacheDir, "header_extract_${System.currentTimeMillis()}").apply {
            if (exists()) deleteRecursively()
            mkdirs()
        }
        
        try {
            // Run magiskboot unpack command with header-only option
            logger("Extracting boot image header information", LogType.INFO)
            
            // Create the unpack command with -h flag to output header info
            val headerCommand = "cd ${tempDir.absolutePath} && $magiskbootPath unpack -h ${bootImage.absolutePath}"
            val result = shellHelper.runAndLog(headerCommand, "Extracting boot header info")
            
            // Header info is in the stdout/stderr output
            val output = result.stdout() + result.stderr()
            
            // Create boot header info with defaults
            val headerInfo = BootHeaderInfo()
            
            // Use reflection to set properties based on parsed values
            val headerMap = mutableMapOf<String, String>()
            
            // Parse header information using regex
            val regex = "([A-Z_]+)\\s*\\[([^\\]]*)]".toRegex()
            val matches = regex.findAll(output)
            
            for (match in matches) {
                val key = match.groupValues[1]
                val value = match.groupValues[2]
                headerMap[key] = value
            }
            
            // Now map the extracted values to the BootHeaderInfo object
            return BootHeaderInfo(
                headerVersion = headerMap["HEADER_VER"] ?: "0",
                kernelSize = headerMap["KERNEL_SZ"] ?: "0",
                ramdiskSize = headerMap["RAMDISK_SZ"] ?: "0",
                secondSize = headerMap["SECOND_SZ"] ?: "0",
                dtbSize = headerMap["DTB_SZ"] ?: "0",
                osVersion = headerMap["OS_VERSION"] ?: "0.0.0",
                osPatchLevel = headerMap["OS_PATCH_LEVEL"] ?: "0000-00",
                pageSize = headerMap["PAGESIZE"] ?: "0",
                name = headerMap["NAME"] ?: "",
                cmdLine = headerMap["CMDLINE"] ?: "",
                kernelFormat = headerMap["KERNEL_FMT"] ?: "unknown",
                ramdiskFormat = headerMap["RAMDISK_FMT"] ?: "unknown"
            )
        } finally {
            // Clean up temporary directory
            tempDir.deleteRecursively()
        }
    }

    /**
     * Extract magiskboot binary from jnilibs and make it executable
     */
    private suspend fun extractMagiskBootBinary(context: Context): Boolean {
        logger("Using jniLibs for magiskboot binary", LogType.INFO)

        // Create a log file to capture details
        val logFile = File(context.filesDir, "magiskboot_extraction.log")
        logFile.writeText("=== Magiskboot Extraction Log (jniLibs) ===\n")

        fun logBoth(message: String, type: LogType) {
            logger(message, type)
            try {
                logFile.appendText("[$type] $message\n")
            } catch (e: Exception) {
                logger("Failed to write to log file: ${e.message}", LogType.ERROR)
            }
        }

        try {
            // Locate nativeLibraryDir and magiskboot inside it
            val libDir = context.applicationInfo.nativeLibraryDir
            val outputFile = File(libDir, "libmagiskboot.so")
            logBoth("Library directory: $libDir", LogType.INFO)

            // Verify file exists
            if (!outputFile.exists()) {
                logBoth("ERROR: magiskboot not found in $libDir", LogType.ERROR)
                return false
            }
            logBoth("Found magiskboot: ${outputFile.absolutePath}", LogType.INFO)
            logBoth("Binary size: ${outputFile.length()} bytes", LogType.INFO)

            // Ensure executable permission
            if (!outputFile.canExecute()) {
                val chmodResult = shellHelper.runAndLog("chmod 0755 ${outputFile.absolutePath}", "Set exec perm")
                logBoth("chmod exit code: ${chmodResult.exitCode}", LogType.INFO)
                if (!chmodResult.isSuccess) {
                    logBoth("Failed to chmod: ${chmodResult.stderr()}", LogType.ERROR)
                    return false
                }
                logBoth("Chmod successful", LogType.INFO)
            } else {
                logBoth("Binary already executable", LogType.INFO)
            }

            // Set the magiskboot path
            setMagiskbootPath(outputFile.absolutePath)
            logBoth("Set magiskboot path: $magiskbootPath", LogType.SUCCESS)
            if (magiskbootPath.isNullOrEmpty()) {
                logBoth("ERROR: magiskbootPath is empty", LogType.ERROR)
                return false
            }

            // Test the binary
            val testResult = shell.run("$magiskbootPath --help")
            logBoth("Test exit code: ${testResult.exitCode}", LogType.INFO)
            // Don't log help output as it's verbose and not useful in logs

            val helpOk = testResult.isSuccess ||
                    testResult.stdout().contains("MagiskBoot") ||
                    testResult.stderr().contains("MagiskBoot")
            if (!helpOk) {
                logBoth("Magiskboot test failed: exit code ${testResult.exitCode}", LogType.ERROR)
                return false
            }

            logBoth("Magiskboot test successful", LogType.SUCCESS)
            logBoth("=== Extraction completed successfully ===", LogType.SUCCESS)
            return true
        } catch (e: Exception) {
            logger("Magiskboot extraction failed: ${e.message}", LogType.ERROR)
            logger("Stack trace: ${e.stackTraceToString()}", LogType.DEBUG)
            return false
        }
    }
    
    /**
     * Determine the device's CPU architecture
     */
    private fun getDeviceArchitecture(): String {
        val arch = Build.SUPPORTED_ABIS[0]
        return when {
            arch.contains("arm64") -> "arm64-v8a"
            arch.contains("armeabi") -> "armeabi-v7a"
            arch.contains("x86_64") -> "x86_64"
            arch.contains("x86") -> "x86"
            else -> "arm64-v8a" // Default to arm64 if unknown
        }
    }

    /**
     * Check if magiskboot is available and find its path
     */
    suspend fun checkMagiskBoot(context: Context): Boolean {
        logger("Setting up bundled magiskboot...", LogType.INFO)
        
        // If we already have a valid path, check if it still exists and works
        if (magiskbootPath != null) {
            val file = File(magiskbootPath!!)
            if (file.exists() && file.canExecute()) {
                logger("Found existing magiskboot at: $magiskbootPath", LogType.INFO)
                
                // Test if it actually works
                if (verifyMagiskbootAvailable()) {
                    logger("Existing magiskboot is working properly", LogType.SUCCESS)
                    return true
                } else {
                    logger("Existing magiskboot doesn't work, will re-extract", LogType.WARNING)
                }
            } else {
                logger("Existing magiskboot path is invalid, will re-extract", LogType.WARNING)
            }
        }
        
        // Extract and use the bundled magiskboot binary
        val extractSuccess = extractMagiskBootBinary(context)
        if (!extractSuccess) {
            logger("Failed to extract bundled magiskboot binary", LogType.ERROR)
            return false
        }
        
        // Make sure we have a valid path after extraction
        if (magiskbootPath == null) {
            logger("Magiskboot path is null after extraction", LogType.ERROR)
            return false
        }
        
        // Verify the binary works by checking help text
        // No need for root here since we're running our bundled binary
        val testCommand = "$magiskbootPath --help"
        val testResult = shell.run(testCommand)
        val helpTextPresent = testResult.stderr().contains("MagiskBoot") || 
                              testResult.stdout().contains("MagiskBoot")
        
        return if (testResult.isSuccess || helpTextPresent) {
            logger("Successfully set up bundled magiskboot", LogType.SUCCESS)
            true
        } else {
            logger("Failed to verify magiskboot functionality", LogType.ERROR)
            false
        }
    }

    /**
     * Verify that magiskboot is available and working before using it
     * @return True if magiskboot is available and working
     */
    fun verifyMagiskbootAvailable(): Boolean {
        if (magiskbootPath == null) {
            logger("Magiskboot path is null, cannot proceed", LogType.ERROR)
            return false
        }
        
        // Verify the file exists
        val magiskbootFile = File(magiskbootPath!!)
        if (!magiskbootFile.exists()) {
            logger("Magiskboot binary not found at path: $magiskbootPath", LogType.ERROR)
            return false
        }
        
        // Try a simple help command to verify the binary works
        val testResult = shell.run("$magiskbootPath --help")
        val helpTextPresent = testResult.stderr().contains("MagiskBoot") || 
                              testResult.stdout().contains("MagiskBoot")
        
        if (!testResult.isSuccess && !helpTextPresent) {
            logger("Magiskboot test failed: binary exists but doesn't work", LogType.ERROR)
            return false
        }
        
        return true
    }

    /**
     * Unpack boot image using magiskboot
     */
    suspend fun unpackBootImage(bootImage: File, extractDir: File): Boolean {
        // Verify magiskboot is available first
        if (!verifyMagiskbootAvailable()) {
            logger("ERROR: Magiskboot is not available. Cannot unpack boot image.", LogType.ERROR)
            return false
        }

        // Log the magiskboot path to verify it's correct
        logger("Using magiskboot path: $magiskbootPath", LogType.INFO)

        // Run magiskboot unpack command
        logger("Unpacking boot image using magiskboot", LogType.INFO)
        logger("Boot image: ${bootImage.absolutePath}", LogType.DEBUG)
        logger("Extract directory: ${extractDir.absolutePath}", LogType.DEBUG)

        val unpackCommand = "cd ${extractDir.absolutePath} && $magiskbootPath unpack -h ${bootImage.absolutePath}"
        logger("Running command: $unpackCommand", LogType.DEBUG)
        
        val unpackResult = shellHelper.runAndLog(unpackCommand, "Unpacking boot image with magiskboot")

        // Check output for compression format issues first
        val outputText = unpackResult.stderr() + unpackResult.stdout()
        
        // Check for specific compression format errors
        val unsupportedFormat = when {
            outputText.contains("KERNEL_FMT") && 
            (outputText.contains("decode failed") || outputText.contains("Decompression error")) -> {
                // Extract the format from "KERNEL_FMT      [format]"
                val formatRegex = "KERNEL_FMT\\s*\\[([^\\]]+)\\]".toRegex()
                val formatMatch = formatRegex.find(outputText)
                val format = formatMatch?.groupValues?.getOrNull(1) ?: "unknown"
                
                // List of formats known to be supported by magiskboot
                val supportedFormats = listOf("gzip", "lz4", "lzop", "bzip2")
                
                if (format.isNotEmpty() && !supportedFormats.contains(format.lowercase())) {
                    logger("ERROR: Unsupported kernel compression format: $format", LogType.ERROR)
                    if (format.lowercase() == "xz" || format.lowercase() == "lzma") {
                        logger("This boot image uses $format compression which is not supported.", LogType.ERROR)
                        logger("This specific kernel can't be decompressed in current version.", LogType.ERROR)
                    } else {
                        logger("This boot image uses a compression format that magiskboot cannot decompress.", LogType.ERROR)
                    }
                    logger("Please report this to the support group with your device information.", LogType.ERROR)
                    true
                } else {
                    logger("ERROR: Failed to decompress kernel in $format format", LogType.ERROR)
                    logger("Decompression failed but the format should be supported.", LogType.ERROR)
                    logger("Please report this to the support group with your device information.", LogType.ERROR)
                    true
                }
            }
            else -> false
        }

        if (!unpackResult.isSuccess && !unsupportedFormat) {
            logger("Failed to unpack boot image", LogType.ERROR)
            return false
        }

        // Continue with the rest of the method...

        // Check output for errors first, so the user can see all error information
        var criticalErrorDetected = false

        // Check stderr output for common error patterns
        val stderrOutput = unpackResult.stderr().trim()
        val stdoutOutput = unpackResult.stdout().trim()

        // Check for decompression failures and retry with alternative methods
        if (stderrOutput.contains("decode failed") || stdoutOutput.contains("decode failed")) {
            logger("WARNING: Kernel decompression failed with standard method", LogType.WARNING)
            logger("Error details: $stderrOutput", LogType.DEBUG)
            logger("Attempting alternative approaches...", LogType.WARNING)
            
            // Get format information from magiskboot output
            var kernelFormat: String? = null
            
            // Look for KERNEL_FMT in the stderr output from magiskboot
            val kernelFmtRegex = "KERNEL_FMT\\s*\\[([^\\]]+)\\]".toRegex()
            val kernelFmtMatch = kernelFmtRegex.find(stderrOutput)
            if (kernelFmtMatch != null && kernelFmtMatch.groupValues.size > 1) {
                kernelFormat = kernelFmtMatch.groupValues[1].trim().lowercase()
                logger("Detected kernel format: $kernelFormat", LogType.INFO)
            }
            
            // Now extract with -n to get the compressed kernel
            logger("Trying unpack with -n flag (skip decompression)", LogType.INFO)
            val noDecompressResult = shellHelper.runAndLog("cd ${extractDir.absolutePath} && $magiskbootPath unpack -n ${bootImage.absolutePath}", "Unpacking boot image without decompression")
            shellHelper.logShellResult(noDecompressResult, "Unpacking boot image without decompression")
            
            // Check if the kernel was extracted (still compressed)
            val compressedKernel = File(extractDir, "kernel")
            if (compressedKernel.exists() && compressedKernel.length() > 0L) {
                // Report the detected format if available
                if (kernelFormat != null) {
                    logger("Kernel is compressed using $kernelFormat format", LogType.INFO)
                }
                
                // Try manual decompression with magiskboot
                logger("Attempting manual decompression", LogType.INFO)
                val decompressedKernel = File(extractDir, "kernel_decompressed")
                val decompressResult = shellHelper.runAndLog(
                    "cd ${extractDir.absolutePath} && $magiskbootPath decompress ${compressedKernel.absolutePath} ${decompressedKernel.absolutePath}",
                    "Manually decompressing kernel"
                )
                shellHelper.logShellResult(decompressResult, "Manually decompressing kernel")
                
                if (decompressResult.isSuccess && decompressedKernel.exists() && decompressedKernel.length() > 0) {
                    logger("Successfully decompressed kernel manually (${decompressedKernel.length()} bytes)", LogType.SUCCESS)
                    // Replace the original kernel with the decompressed one
                    val moveResult = shellHelper.runAndLog("mv ${decompressedKernel.absolutePath} ${compressedKernel.absolutePath}", "Replacing compressed kernel")
                    if (!moveResult.isSuccess) {
                        logger("CRITICAL ERROR: Failed to replace compressed kernel with decompressed version", LogType.ERROR)
                        criticalErrorDetected = true
                    }
                } else {
                    logger("Failed to decompress kernel", LogType.WARNING)
                    
                    if (kernelFormat != null) {
                        logger("Your device uses the $kernelFormat compression format which isn't fully supported", LogType.ERROR)
                        logger("Please report this to the support group", LogType.ERROR)
                    } else {
                        logger("Unknown compression format. Please report this to the support group", LogType.ERROR)
                    }
                    
                    criticalErrorDetected = true
                }
            } else {
                logger("CRITICAL ERROR: Kernel extraction failed even with -n flag", LogType.ERROR)
                criticalErrorDetected = true
            }
        }

        if (stderrOutput.contains("unexpected ASN.1 DER tag") || stdoutOutput.contains("unexpected ASN.1 DER tag")) {
            logger("WARNING: ASN.1 DER tag error detected", LogType.WARNING)
            logger("This might affect signature verification but may not prevent patching", LogType.WARNING)
        }

        // Verify kernel was extracted
        val kernelFile = File(extractDir, "kernel")
        if (!kernelFile.exists()) {
            logger("CRITICAL ERROR: Kernel file not found after unpacking", LogType.ERROR)
            criticalErrorDetected = true
        } else {
            // Check if kernel file is empty (0 bytes)
            if (kernelFile.length() == 0L) {
                logger("CRITICAL ERROR: EXTRACTED KERNEL FILE IS EMPTY (0 BYTES)", LogType.ERROR)
                logger("The unpacking process may have failed or the kernel data is corrupt", LogType.ERROR)
                criticalErrorDetected = true
            } else {
                logger("Extracted kernel size: ${kernelFile.length()} bytes", LogType.INFO)
            }
        }

        if (criticalErrorDetected) {
            logger("Boot image unpacking FAILED due to critical errors", LogType.ERROR)
            return false
        }

        logger("Boot image unpacked successfully", LogType.SUCCESS)
        logger("Extracted kernel size: ${kernelFile.length()} bytes", LogType.INFO)
        return true
    }

    /**
     * Repack boot image using magiskboot
     */
    suspend fun repackBootImage(originalBootImage: File, extractDir: File, outputBootImage: File): Boolean {
        // Verify magiskboot is available first
        if (!verifyMagiskbootAvailable()) {
            logger("ERROR: Magiskboot is not available. Cannot repack boot image.", LogType.ERROR)
            return false
        }

        // Log the magiskboot path to verify it's correct
        logger("Using magiskboot path: $magiskbootPath", LogType.INFO)

        // Run magiskboot repack command
        logger("Repacking boot image using magiskboot", LogType.INFO)
        logger("Original boot image: ${originalBootImage.absolutePath}", LogType.DEBUG)
        logger("Output boot image: ${outputBootImage.absolutePath}", LogType.DEBUG)

        val repackCommand = "cd ${extractDir.absolutePath} && $magiskbootPath repack ${originalBootImage.absolutePath} ${outputBootImage.absolutePath}"
        val repackResult = shellHelper.runAndLog(repackCommand, "Repacking boot image with magiskboot")

        if (!repackResult.isSuccess) {
            logger("Failed to repack boot image", LogType.ERROR)
            return false
        }

        // Verify output file exists
        if (!outputBootImage.exists()) {
            logger("Output boot image not found after repacking", LogType.ERROR)
            return false
        }

        logger("Boot image repacked successfully", LogType.SUCCESS)
        logger("Output boot image size: ${outputBootImage.length()} bytes", LogType.INFO)
        return true
    }

    /**
     * Patch kernel using magiskboot hexpatch
     * @return True if any pattern was found and patched, false otherwise
     */
    suspend fun patchKernelWithHexpatch(kernelFile: File): Boolean {
        // Verify magiskboot is available first
        if (!verifyMagiskbootAvailable()) {
            logger("ERROR: Magiskboot is not available. Cannot patch kernel.", LogType.ERROR)
            return false
        }

        // Log the magiskboot path to verify it's correct
        logger("Using magiskboot path: $magiskbootPath", LogType.INFO)

        logger("Attempting to patch kernel using magiskboot hexpatch", LogType.INFO)

        var patchedAny = false
        for ((pattern, replacement) in patchPatterns) {
            logger("Trying pattern: $pattern → $replacement", LogType.DEBUG)

            val hexpatchCommand = "$magiskbootPath hexpatch ${kernelFile.absolutePath} $pattern $replacement"
            val hexpatchResult = shellHelper.runAndLog(hexpatchCommand, "magiskboot hexpatch")

            if (hexpatchResult.isSuccess) {
                val output = hexpatchResult.stdout().trim()
                if (output.contains("Pattern found")) {
                    logger("Successfully patched pattern: $pattern", LogType.SUCCESS)
                    patchedAny = true
                } else if (!output.contains("Pattern not found")) {
                    // If the output doesn't contain 'Pattern not found', but also doesn't contain an error message
                    // We consider it patched, as some magiskboot versions don't print detailed info
                    logger("Pattern likely patched: $pattern", LogType.SUCCESS)
                    patchedAny = true
                }
            }
        }

        if (patchedAny) {
            logger("Kernel successfully patched using magiskboot hexpatch", LogType.SUCCESS)
        } else {
            logger("No matching patterns found for patching", LogType.WARNING)
        }

        return patchedAny
    }

    /**
     * Check if root access is available
     * @return True if root access is available
     */
    private  fun checkRootAccess(): Boolean {
        logger("Checking for root access...", LogType.DEBUG)
        
        try {
            // Execute a simple su command to check for root
            val suResult = Shell.SH.run("su -c id")
            
            // Check for error messages that indicate root is not available
            if (suResult.stderr().contains("not found", ignoreCase = true) || 
                suResult.stderr().contains("permission denied", ignoreCase = true)) {
                logger("Root access denied or not available", LogType.DEBUG)
                return false
            } else if (suResult.isSuccess && suResult.stdout().contains("uid=0")) {
                logger("Root access is available", LogType.DEBUG)
                return true
            }
            
            return false
        } catch (e: Exception) {
            logger("Error checking root access: ${e.message}", LogType.DEBUG)
            return false
        }
    }
    
    /**
     * Execute a command with root privileges if available and needed
     * @param command The command to execute
     * @param operation Description of the operation for logging
     * @param requireRoot If true, will return failure if root is not available
     * @return The command result
     */
    private  fun executeWithRoot(command: String, operation: String, requireRoot: Boolean = false): Shell.Command.Result {
        // For magiskboot operations, we typically don't need root since we use our bundled binary
        // Only use root if specifically required AND available
        
        val hasRoot = if (requireRoot) checkRootAccess() else false
        
        if (requireRoot && !hasRoot) {
            logger("ERROR: Root access is required for: $operation", LogType.ERROR)
            // Instead of directly constructing a Result object, run a harmless command that fails
            // This ensures we get a properly constructed Result object
            return Shell.SH.run("false && echo \"Root access required but not available\"")
        }
        
        // Execute with su if we have root and it's required, otherwise just use regular shell
        val fullCommand = if (requireRoot && hasRoot) "su -c \"$command\"" else command
        return shellHelper.runAndLog(fullCommand, operation)
    }
}

/**
 * Helper class for shell command operations
 */
class ShellHelper(private val shell: Shell, private val logger: (String, LogType) -> Unit) {

    /**
     * Log shell command result with detailed output
     */
    fun logShellResult(result: Shell.Command.Result, operation: String) {
        logger("Shell command for '$operation' exited with code ${result.exitCode}", LogType.DEBUG)

        if (result.stdout().isNotEmpty()) {
            logger("stdout: ${result.stdout()}", LogType.DEBUG)
        }

        if (result.stderr().isNotEmpty()) {
            logger("stderr: ${result.stderr()}", LogType.DEBUG)
        }
    }
    
    /**
     * Run a shell command and log its result
     * @return The command result
     */
    fun runAndLog(command: String, operation: String): Shell.Command.Result {
        logger("Running command: $command", LogType.DEBUG)
        val result = shell.run(command)
        logShellResult(result, operation)
        return result
    }
    
    /**
     * Convert byte array to hex string
     */
    fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = "0123456789ABCDEF"[v ushr 4]
            hexChars[i * 2 + 1] = "0123456789ABCDEF"[v and 0x0F]
        }
        return String(hexChars)
    }
}

@Composable
fun KernelPatcherApp() {
    val viewModel: KernelPatcherViewModel = viewModel()
    callback = { viewModel.setScreen(Screen.HOME) }
    clear = { viewModel._state.update { it.copy(logs = mutableListOf()) } }
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Initialize the ViewModel with context - ensure this happens very early
    LaunchedEffect(key1 = Unit) {
        log("Initializing ViewModel with context", LogType.INFO)
        viewModel.initialize(context)
    }

    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val path = getFilePathFromUri(context, uri)
            viewModel.setInputFile(path)
        }
    }

    val saveFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("*/*")
    ) { uri ->
        uri?.let {
            val outputPath = getOutputPath(context, uri)
            viewModel.setOutputFile(outputPath)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Home Screen
            AnimatedVisibility(
                visible = state.currentScreen == Screen.HOME,
                enter = fadeIn() + slideInHorizontally(),
                exit = fadeOut() + slideOutHorizontally()
            ) {
                HomeScreen(
                    currentBootSlot = state.currentBootSlot,
                    magiskBootAvailable = state.magiskbootAvailable,
                    onPickFile = { pickFileLauncher.launch("*/*") },
                    onSelectOutputLocation = { saveFileLauncher.launch("patched_kernel.img") },
                    onPatchCurrentBoot = { viewModel.patchCurrentBoot(it) },
                    onPatchFile = { inputPath, outputPath, verifyOnly ->
                        viewModel.patchFile(inputPath, outputPath, verifyOnly)
                    },
                    inputFile = state.inputFile,
                    outputFile = state.outputFile ?: "/sdcard/patched_kernel.img"
                )
            }

            // Patching Screen (Log Output)
            AnimatedVisibility(
                visible = state.currentScreen == Screen.PATCHING || state.currentScreen == Screen.RESULT,
                enter = fadeIn() + slideInHorizontally(),
                exit = fadeOut() + slideOutHorizontally()
            ) {
                LogScreen(
                    logs = state.logs,
                    patchingFinished = state.currentScreen == Screen.RESULT,
                    patchSuccess = state.patchSuccess,
                    onGoHome = { viewModel.setScreen(Screen.HOME) },
                    onFlashBoot = { outputPath -> viewModel.flashPatchedBoot(outputPath) },
                    outputFile = state.outputFile,
                    onCopyLogs = {
                        val logs = viewModel.getFormattedLogs()
                        copyToClipboard(context, "MTK BPF Patcher Logs", logs)
                        Toast.makeText(context, "Logs copied to clipboard", Toast.LENGTH_SHORT).show()
                    })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LogScreen(
    logs: List<LogEntry>,
    patchingFinished: Boolean,
    patchSuccess: Boolean,
    onGoHome: () -> Unit,
    onFlashBoot: (String) -> Unit,
    outputFile: String?,
    onCopyLogs: () -> Unit
) {
    val viewModel: KernelPatcherViewModel = viewModel()
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current
    var showExtractionLog by remember { mutableStateOf(false) }
    var extractionLog by remember { mutableStateOf("") }
    
    // Automatically check for magiskboot failure
    val magiskbootAvailable = viewModel.state.collectAsState().value.magiskbootAvailable
    
    // Check for logs indicating magiskboot initialization failure or compression format issues
    val hasMagiskbootError = logs.any { 
        it.type == LogType.ERROR && 
        it.message.contains("Magiskboot is not available") 
    }
    
    val hasCompressionFormatError = logs.any {
        it.type == LogType.ERROR &&
        it.message.contains("Unsupported kernel compression format")
    }
    
    // Show extraction log automatically when patching finishes with magiskboot error
    LaunchedEffect(patchingFinished, hasMagiskbootError, hasCompressionFormatError) {
        if (patchingFinished && hasMagiskbootError) {
            extractionLog = viewModel.getMagiskbootExtractionLog()
            showExtractionLog = true
        }
    }

    // Auto-scroll to bottom when new logs are added
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            lazyListState.animateScrollToItem(logs.size - 1)
        }
    }

    if (showExtractionLog) {
        AlertDialog(
            onDismissRequest = { showExtractionLog = false },
            title = { Text("Magiskboot Extraction Log") },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(extractionLog.split("\n")) { line ->
                        Text(
                            text = line,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showExtractionLog = false }) {
                    Text("Close")
                }
            },
            dismissButton = {
                Button(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Extraction Log", extractionLog)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Log copied to clipboard", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Copy")
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!patchingFinished) LoadingIndicator()

            // Header
            Text(
                text = if (patchingFinished) {
                    if (patchSuccess) "Operation Completed!" else "Operation Failed"
                } else {
                    "Operation in progress..."
                },
                style = MaterialTheme.typography.headlineSmall,
                )
        }

        // Log output
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFF121212))
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            items(logs) { log ->
                val color = when (log.type) {
                    LogType.INFO -> Color.White
                    LogType.ERROR -> Color.Red
                    LogType.SUCCESS -> Color.Green
                    LogType.WARNING -> Color.Yellow
                    LogType.COMMAND -> Color(0xFF00BCD4) // Cyan for commands
                    LogType.DEBUG -> Color(0xFFAAAAAA) // Gray for debug logs
                }

                Text(
                    text = log.message,
                    color = color,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(vertical = 2.dp),
                    maxLines = if (log.type == LogType.COMMAND || log.type == LogType.DEBUG) 1 else Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Bottom buttons
        if (patchingFinished) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Show specific message for compression format errors
                if (hasCompressionFormatError) {
                    // Find the specific format if possible
                    val formatRegex = "format: ([a-zA-Z0-9]+)".toRegex()
                    val formatMessage = logs.find { it.message.contains("Unsupported kernel compression format:") }?.message ?: ""
                    
                    // Try to extract format from the message like "Unsupported kernel compression format: xz"
                    val formatMatch = if (formatMessage.isNotEmpty()) {
                        val extractFormatRegex = "compression format:\\s*([a-zA-Z0-9]+)".toRegex()
                        extractFormatRegex.find(formatMessage)?.groupValues?.getOrNull(1)
                    } else null
                    
                    val isXzOrLzma = formatMatch?.lowercase() == "xz" || formatMatch?.lowercase() == "lzma"
                    
                    Text(
                        text = if (isXzOrLzma) 
                            "This boot image uses $formatMatch compression which is only partially supported." 
                        else 
                            "This boot image uses an unsupported compression format.",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    if (isXzOrLzma) {
                        Text(
                            text = "This specific kernel can't be properly decompressed with our current tools.",
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    Text(
                        text = "Please report this to the support group for assistance.",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = onGoHome) {
                    Text("Back")
                }

                    if (!patchSuccess) {
                        Button(
                            onClick = onCopyLogs, 
                            colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Copy Logs")
                        }
                }

                if (patchSuccess && outputFile != null) {
                    Button(
                            onClick = { onFlashBoot(outputFile) }, 
                            colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text("Flash Boot Image")
                        }
                    }
                }
            }
        }
    }
}

fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}

fun getFilePathFromUri(context: Context, uri: Uri): String {
    // For the file:// scheme
    if (uri.scheme == "file") {
        return uri.path ?: ""
    }

    // For content:// scheme, we need to use the actual path
    val path = getContentFilePath(context, uri)
    if (path != null) {
        return path
    }

    // If we can't get the actual path, use a temp file
    val inputStream = context.contentResolver.openInputStream(uri) ?: return ""
    val tempFile = File.createTempFile("boot_", ".bin", context.cacheDir)
    tempFile.deleteOnExit()

    inputStream.use { input ->
        tempFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    return tempFile.absolutePath
}

fun getContentFilePath(context: Context, uri: Uri): String? {
    // Try several methods to get the actual file path
    val projection = arrayOf("_data")

    context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow("_data")
            return cursor.getString(columnIndex)
        }
    }

    return null
}

fun getOutputPath(context: Context, uri: Uri): String {
    // For document provider, we need to use the actual URI
    return uri.toString()
}

var callback = {}
var clear = {}

// Global logging function for debugging
fun log(message: String, type: LogType) {
    // Log to Android logcat with a distinct tag for filtering
    android.util.Log.d("MTK-BPF-PATCHER", "[$type] $message")
}

/**
 * CookieCard component for consistent card styling
 */
@Composable
fun CookieCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .border(
                width = 8.dp,
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            ),
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .padding(8.dp)
        ) {
            content()
        }
    }
}

/**
 * App information dialog explaining what the app does
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppInfoDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // SharedPreferences for first launch
    val sharedPrefs = remember {
        context.getSharedPreferences("mtk_bpf_patcher_prefs", Context.MODE_PRIVATE)
    }
    AnimatedVisibility(
        visible = showDialog,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Dialog(onDismissRequest = { if (!sharedPrefs.getBoolean("has_seen_info_dialog", false)) onDismiss}) {
            CookieCard {
                Column(
                    modifier = Modifier.heightIn(max = 500.dp)
                ) {

                    
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {item{Text(
                        text = "FAQ",
                        style = MaterialTheme.typography.headlineLargeEmphasized,
                        modifier = Modifier.padding( 16.dp)
                    )}
                        item {
                            InfoSection(
                                title = "What does this app do?",
                                content = "This app patches MediaTek kernel images to fix connectivity issues on Android 12+ custom ROMs.\n\nMediaTek added a commit to fix memory errors but it accidentally broke network functionality.\n\nThis app reverts that problematic commit by replacing the bad code with NOPs."
                            )
                        }

                        item {
                            InfoSection(
                                title = "What is BPF?",
                                content = "BPF stands for Berkeley Packet Filter.\n\nIt acts like a firewall system in the Linux kernel that controls network packet filtering and processing.\n\nBPF decides which network packets are allowed through and which are blocked.\n\nWhen BPF breaks, your device can connect to WiFi or mobile networks but all internet requests get blocked because the firewall system is not working correctly."
                            )
                        }

                        item {
                            InfoSection(
                                title = "What is the issue?",
                                content = "MediaTek tried to fix ubsan errors in their BPF arraymap code but the fix they implemented broke memcpy calls.\n\nThis causes the BPF firewall system to malfunction.\n\nYour device shows connected to WiFi or mobile data but no network requests work - websites don't load, apps can't connect to internet, downloads fail.\n\nThe device appears connected but all traffic is silently dropped."
                            )
                        }

                        item {
                            InfoSection(
                                title = "How the fix works",
                                content = "The app looks for specific byte patterns in your kernel that match the problematic MediaTek commit.\n\nWhen found, it replaces those bytes with NOP instructions.\n\nNOP means \"no operation\" - it tells the processor to skip that function entirely.\n\nThis allows the original working memcpy function to run instead of the broken MediaTek fix."
                            )
                        }

                        item {
                            InfoSection(
                                title = "When will this help?",
                                content = "You have a MediaTek device running Android 12+ custom ROM based on LineageOS (most commonly) and you are experiencing network connectivity issues where you can connect to networks but cannot access internet.\n\nYour device kernel version is 4.14.x or 4.19.x.\n\nThe problems became much more common with Android 15 based ROMs but can occur on Android 12+ in some cases."
                            )
                        }

                        item {
                            InfoSection(
                                title = "When won't this help?",
                                content = "Your device is not MediaTek.\n\nYou are running stock ROM.\n\nYou are on Android 11 or older.\n\nYour network issues are caused by something else like DNS problems or other firewall blocking.\n\nYour kernel version is not 4.14.x or 4.19.x.\n\nYou cannot connect to networks at all or your SIM card is not detected.\n\nYour connectivity issues are different from the BPF arraymap bug."
                            )
                        }
                        
                        item {
                            InfoSection(
                                title = "Important notes",
                                content = "This tool has only been tested with 4.14.x and 4.19.x kernels.\n\nOther kernel versions may use different instruction patterns.\n\nEven if you have 4.14 or 4.19, your kernel may have a different implementation which app won't know about, or use (at the moment) incompatible compression algorithm for kernel.\n\nYou need root access to flash the patched boot image back to your device if you wanna do it directly from the app (still requires testing but presumably works but if it doesnt just use twrp/fastboot/spft/whatever the fuck your device needs).\n\nIf boot was succesufully patched but you still have the issue try patching stock boot if you were trying it on rooted one.\n\nAlways backup your original boot image before flashing.\n\nThe issue became dramatically more frequent starting with Android 15 based custom ROMs."
                            )
                        }
                        
                        item {
                            Button(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                            ) {
                                Text("Close")
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Helper composable for info sections
 */
@Composable
private fun InfoSection(
    title: String,
    content: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onBackPressed() {
        callback()
        if (false) super.onBackPressed()
    }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            val context = LocalContext.current

            val readPermissionState = rememberMultiplePermissionsState(
                listOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
            LaunchedEffect(Unit) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    readPermissionState.launchMultiplePermissionRequest()
                    if (!readPermissionState.allPermissionsGranted) {
                        Toast.makeText(
                            context, "No permission, change in settings", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context, "Permission granted", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    if (!Environment.isExternalStorageManager()) {
                        Toast.makeText(
                            context, "No permission, change in settings", Toast.LENGTH_SHORT
                        ).show()
                        context.startActivity(
                            Intent(
                                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                "package:pro.themed.mtkbpfpatcher".toUri()
                            )
                        )
                    }
                }
            }

            MaterialTheme {
                KernelPatcherApp()
            }
        }
    }
}
package pro.themed.mtkbpfpatcher

import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Files helper for handling boot image extraction and other operations
 */
class FileHelper(private val shell: Shell, private val logger: (String, LogType) -> Unit) {

    private val shellHelper = ShellHelper(shell, logger)
    
    /**
     * Check if root access is available
     * @return True if root access is available
     */
    private suspend fun checkRootAccess(): Boolean {
        logger("Checking for root access...", LogType.DEBUG)
        
        try {
            // Execute a simple su command to check for root
            val suResult = Shell.SH.run("su -c id")
            
            // Check for error messages that indicate root is not available
            if (suResult.stderr().contains("not found", ignoreCase = true) || 
                suResult.stderr().contains("permission denied", ignoreCase = true)) {
                logger("Root access denied or not available", LogType.ERROR)
                return false
            } else if (suResult.isSuccess && suResult.stdout().contains("uid=0")) {
                logger("Root access is available", LogType.SUCCESS)
                return true
            }
            
            logger("Root check failed without clear denial", LogType.WARNING)
            return false
        } catch (e: Exception) {
            logger("Error checking root access: ${e.message}", LogType.ERROR)
            return false
        }
    }
    
    /**
     * Execute a command with root privileges if available
     * @param command The command to execute
     * @param operation Description of the operation for logging
     * @param requireRoot If true, will return failure if root is not available
     * @return The command result
     */
    private suspend fun executeWithRoot(command: String, operation: String, requireRoot: Boolean = true): Shell.Command.Result {
        // Check if we have root access
        val hasRoot = checkRootAccess()
        
        if (requireRoot && !hasRoot) {
            logger("ERROR: Root access is required for: $operation", LogType.ERROR)
            // Instead of constructing a Result object directly, run a harmless command that fails
            // This ensures we get a properly constructed Result object
            return Shell.SH.run("false && echo \"Root access required but not available\"")
        }
        
        // Execute with su if we have root, otherwise just use regular shell
        val fullCommand = if (hasRoot) "su -c \"$command\"" else command
        return shellHelper.runAndLog(fullCommand, operation)
    }

    /**
     * Detect file type by checking for magic bytes
     */
    fun detectFileType(file: File): FileType {
        try {
            logger("Examining file header of ${file.absolutePath}", LogType.DEBUG)
            FileInputStream(file).use { stream ->
                val header = ByteArray(8)
                val read = stream.read(header)

                if (read >= 8) {
                    logger("First 8 bytes: ${shellHelper.bytesToHex(header)}", LogType.DEBUG)

                    // Check for ANDROID! magic (boot image)
                    if (isAndroidBootImage(header)) {
                        logger("Detected ANDROID! magic, file is a boot image", LogType.INFO)
                        return FileType.BOOT_IMAGE
                    }

                    // Check for gzip magic
                    if (read >= 2 && isGzipCompressed(header)) {
                        logger("Detected GZIP magic, file is compressed", LogType.INFO)
                        return FileType.KERNEL_GZ
                    }
                }
            }
        } catch (e: Exception) {
            logger("Error detecting file type: ${e.message}", LogType.ERROR)
            logger("Stack trace: ${e.stackTraceToString()}", LogType.DEBUG)
        }

        logger("No special headers detected, assuming raw kernel binary", LogType.INFO)
        return FileType.KERNEL_BIN
    }

    /**
     * Check if byte array starts with ANDROID! magic
     */
    private fun isAndroidBootImage(header: ByteArray): Boolean {
        val magic = "ANDROID!"
        if (header.size < magic.length) return false

        for (i in magic.indices) {
            if (header[i] != magic[i].toByte()) return false
        }
        return true
    }

    /**
     * Check if byte array has gzip magic
     */
    private fun isGzipCompressed(header: ByteArray): Boolean {
        return header.size >= 2 && header[0] == 0x1F.toByte() && header[1] == 0x8B.toByte()
    }

    /**
     * Extract kernel from boot image using root shell
     */
    suspend fun extractKernelFromBoot(bootFile: File, outputFile: File): Boolean {
        logger("Extracting kernel from boot image", LogType.INFO)
        logger("Boot image path: ${bootFile.absolutePath}", LogType.DEBUG)
        logger("Output kernel path: ${outputFile.absolutePath}", LogType.DEBUG)

        val extractCmd = "dd if=${bootFile.absolutePath} bs=4096 skip=1 of=${outputFile.absolutePath}"
        val result = shellHelper.runAndLog(extractCmd, "Extracting kernel from boot image")

        if (!result.isSuccess) {
            logger("Failed to extract kernel from boot image", LogType.ERROR)
            return false
        }

        // Verify kernel was extracted
        if (!outputFile.exists() || outputFile.length() == 0L) {
            logger("Kernel extraction failed: Output file does not exist or is empty", LogType.ERROR)
            return false
        }

        logger("Kernel extracted successfully (${outputFile.length()} bytes)", LogType.SUCCESS)

        // Check if the extracted kernel is compressed
        if (isGzipped(outputFile)) {
            logger("Extracted kernel is gzipped, decompressing", LogType.INFO)
            val gzippedFile = File("${outputFile.absolutePath}.gz")
            val renamed = outputFile.renameTo(gzippedFile)

            if (!renamed) {
                logger("Failed to rename kernel file for decompression", LogType.ERROR)
                return false
            }

            return decompressGzip(gzippedFile, outputFile)
        }

        return true
    }

    /**
     * Check if file is gzipped
     */
    private fun isGzipped(file: File): Boolean {
        try {
            FileInputStream(file).use { stream ->
                val header = ByteArray(2)
                if (stream.read(header) == 2) {
                    logger("Checking gzip magic: ${shellHelper.bytesToHex(header)}", LogType.DEBUG)
                    return header[0] == 0x1F.toByte() && header[1] == 0x8B.toByte()
                }
                return false
            }
        } catch (e: Exception) {
            logger("Error checking if file is gzipped: ${e.message}", LogType.ERROR)
            return false
        }
    }

    /**
     * Decompress gzip file
     */
    suspend fun decompressGzip(inputFile: File, outputFile: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                logger("Decompressing gzip file: ${inputFile.absolutePath}", LogType.DEBUG)
                logger("Output file: ${outputFile.absolutePath}", LogType.DEBUG)

                FileInputStream(inputFile).use { fileIn ->
                    GZIPInputStream(fileIn).use { gzipIn ->
                        FileOutputStream(outputFile).use { fileOut ->
                            val buffer = ByteArray(8192)
                            var bytesRead: Int
                            var totalBytes = 0L

                            while (gzipIn.read(buffer).also { bytesRead = it } != -1) {
                                fileOut.write(buffer, 0, bytesRead)
                                totalBytes += bytesRead
                            }

                            logger("Decompressed $totalBytes bytes", LogType.DEBUG)
                        }
                    }
                }

                if (!outputFile.exists() || outputFile.length() == 0L) {
                    logger("Decompression failed: Output file does not exist or is empty", LogType.ERROR)
                    return@withContext false
                }

                logger("Successfully decompressed file (${outputFile.length()} bytes)", LogType.SUCCESS)
                return@withContext true
            } catch (e: Exception) {
                logger("Failed to decompress: ${e.message}", LogType.ERROR)
                logger("Stack trace: ${e.stackTraceToString()}", LogType.DEBUG)
                return@withContext false
            }
        }
    }

    /**
     * Compress file with gzip
     */
    suspend fun compressGzip(inputFile: File, outputFile: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                logger("Compressing file: ${inputFile.absolutePath}", LogType.DEBUG)
                logger("Output file: ${outputFile.absolutePath}", LogType.DEBUG)

                FileInputStream(inputFile).use { fileIn ->
                    FileOutputStream(outputFile).use { fileOut ->
                        GZIPOutputStream(fileOut).use { gzipOut ->
                            val buffer = ByteArray(8192)
                            var bytesRead: Int
                            var totalBytes = 0L

                            while (fileIn.read(buffer).also { bytesRead = it } != -1) {
                                gzipOut.write(buffer, 0, bytesRead)
                                totalBytes += bytesRead
                            }

                            logger("Compressed $totalBytes bytes", LogType.DEBUG)
                        }
                    }
                }

                if (!outputFile.exists() || outputFile.length() == 0L) {
                    logger("Compression failed: Output file does not exist or is empty", LogType.ERROR)
                    return@withContext false
                }

                logger("Successfully compressed file (${outputFile.length()} bytes)", LogType.SUCCESS)
                return@withContext true
            } catch (e: Exception) {
                logger("Failed to compress: ${e.message}", LogType.ERROR)
                logger("Stack trace: ${e.stackTraceToString()}", LogType.DEBUG)
                return@withContext false
            }
        }
    }

    /**
     * Extract boot header (first 4096 bytes)
     */
    suspend fun extractBootHeader(bootFile: File, outputFile: File): Boolean {
        logger("Extracting boot header", LogType.INFO)
        logger("Boot image path: ${bootFile.absolutePath}", LogType.DEBUG)
        logger("Output header path: ${outputFile.absolutePath}", LogType.DEBUG)

        val headerCmd = "dd if=${bootFile.absolutePath} bs=4096 count=1 of=${outputFile.absolutePath}"
        val result = shellHelper.runAndLog(headerCmd, "Extracting boot header")

        if (!result.isSuccess) {
            logger("Failed to extract boot header", LogType.ERROR)
            return false
        }

        // Verify header was extracted
        if (!outputFile.exists() || outputFile.length() == 0L) {
            logger("Header extraction failed: Output file does not exist or is empty", LogType.ERROR)
            return false
        }

        logger("Boot header extracted successfully (${outputFile.length()} bytes)", LogType.SUCCESS)
        return true
    }

    /**
     * Append file to another file
     */
    suspend fun appendFile(sourceFile: File, targetFile: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                logger("Appending file: ${sourceFile.absolutePath}", LogType.DEBUG)
                logger("To file: ${targetFile.absolutePath}", LogType.DEBUG)

                val sourceSize = sourceFile.length()
                val targetSizeBefore = targetFile.length()

                FileInputStream(sourceFile).use { input ->
                    FileOutputStream(targetFile, true).use { output ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        var totalBytes = 0L

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalBytes += bytesRead
                        }

                        logger("Appended $totalBytes bytes", LogType.DEBUG)
                    }
                }

                val targetSizeAfter = targetFile.length()
                if (targetSizeAfter != targetSizeBefore + sourceSize) {
                    logger(
                        "Warning: Append size mismatch. Expected ${targetSizeBefore + sourceSize} bytes, got $targetSizeAfter",
                        LogType.WARNING
                    )
                }

                logger("Successfully appended file", LogType.SUCCESS)
                return@withContext true
            } catch (e: Exception) {
                logger("Failed to append file: ${e.message}", LogType.ERROR)
                logger("Stack trace: ${e.stackTraceToString()}", LogType.DEBUG)
                return@withContext false
            }
        }
    }

    /**
     * Copy file
     */
    suspend fun copyFile(sourceFile: File, targetFile: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                logger("Copying file: ${sourceFile.absolutePath}", LogType.DEBUG)
                logger("To file: ${targetFile.absolutePath}", LogType.DEBUG)

                FileInputStream(sourceFile).use { input ->
                    FileOutputStream(targetFile).use { output ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        var totalBytes = 0L

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalBytes += bytesRead
                        }

                        logger("Copied $totalBytes bytes", LogType.DEBUG)
                    }
                }

                if (!targetFile.exists() || targetFile.length() != sourceFile.length()) {
                    logger(
                        "Copy failed: Output file size (${targetFile.length()}) doesn't match input (${sourceFile.length()})",
                        LogType.ERROR
                    )
                    return@withContext false
                }

                logger("Successfully copied file", LogType.SUCCESS)
                return@withContext true
            } catch (e: Exception) {
                logger("Failed to copy file: ${e.message}", LogType.ERROR)
                logger("Stack trace: ${e.stackTraceToString()}", LogType.DEBUG)
                return@withContext false
            }
        }
    }

    /**
     * Get current boot slot using root shell
     */
    suspend fun getCurrentBootSlot(): String? {
        logger("Detecting current boot slot", LogType.DEBUG)
        
        // Check for root access first - this is required for boot slot detection
        if (!checkRootAccess()) {
            logger("Root access is required to detect boot slot", LogType.WARNING)
            // Return null but don't show an error, as this is non-critical
            return null
        }

        val bootctlCmd = "bootctl get-suffix $(bootctl get-current-slot)"
        val result = executeWithRoot(bootctlCmd, "Detecting boot slot with bootctl", false)

        if (result.isSuccess && result.stdout().isNotEmpty()) {
            val slot = result.stdout().trim()
            logger("Detected boot slot: $slot", LogType.SUCCESS)
            return slot
        }

        // Try getprop as fallback
        logger("bootctl failed, trying getprop fallback", LogType.DEBUG)
        val propCmd = "getprop ro.boot.slot_suffix"
        val propResult = executeWithRoot(propCmd, "Detecting boot slot with getprop", false)

        if (propResult.isSuccess && propResult.stdout().isNotEmpty()) {
            val slot = propResult.stdout().trim()
            logger("Detected boot slot: $slot", LogType.SUCCESS)
            return slot
        }

        logger("Could not detect boot slot", LogType.WARNING)
        return null
    }

    /**
     * Flash patched boot image
     */
    suspend fun flashBootImage(bootFile: File, bootSlot: String?): Boolean {
        // Check for root access first - this is required for flashing
        if (!checkRootAccess()) {
            logger("Root access is required to flash boot image", LogType.ERROR)
            return false
        }
        
        val bootDevice = if (bootSlot != null && bootSlot.isNotEmpty()) {
            "/dev/block/by-name/boot$bootSlot"
        } else {
            "/dev/block/by-name/boot"
        }

        logger("Flashing to $bootDevice", LogType.WARNING)
        logger("Boot image path: ${bootFile.absolutePath}", LogType.DEBUG)
        logger("Boot image size: ${bootFile.length()} bytes", LogType.DEBUG)

        // Verify boot partition exists
        val checkCmd = "ls -la $bootDevice"
        val checkResult = executeWithRoot(checkCmd, "Verifying boot partition")

        if (!checkResult.isSuccess) {
            logger("Boot partition $bootDevice not found", LogType.ERROR)
            return false
        }

        // —————————————
        // Flash the boot image
        // —————————————

        // 1) Enable writes on the partition
        val rwCmd = "blockdev --setrw $bootDevice"
        executeWithRoot(rwCmd, "Enable write on boot partition")

        // 2) Write & fsync
        val flashCmd = "dd if=${bootFile.absolutePath} of=$bootDevice bs=4K conv=fsync"
        val result = executeWithRoot(flashCmd, "Flashing boot image")
        
        if (!result.isSuccess) {
            logger("Failed to flash boot image", LogType.ERROR)
            return false
        }

        // 3) Restore read-only & sync
        val roCmd = "blockdev --setro $bootDevice"
        executeWithRoot(roCmd, "Set boot partition read-only")
        
        val syncCmd = "sync"
        executeWithRoot(syncCmd, "Sync filesystem")

        // Success
        logger("Boot image flashed successfully", LogType.SUCCESS)
        return true
    }
}
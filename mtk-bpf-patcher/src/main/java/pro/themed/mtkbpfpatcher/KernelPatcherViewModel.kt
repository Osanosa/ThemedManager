package pro.themed.mtkbpfpatcher

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Date

class KernelPatcherViewModel : ViewModel() {

    val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()

    // Use non-root shell by default
    private val shell = Shell.Companion.SH
    private val shellHelper = ShellHelper(shell) { message, type -> log(message, type) }
    private val patcher = BinaryPatcher()
    private val fileHelper = FileHelper(shell) { message, type -> log(message, type) }
    private val magiskBootHelper = MagiskBootHelper(shell) { message, type -> log(message, type) }
    
    // App context will be set from the UI
    private var appContext: Context? = null
    
    // Option to proceed without pattern matching
    private var forcePatching = false
    
    // Track root availability
    private var rootAvailable = false

    init {
        detectBootSlot()
    }
    
    /**
     * Check if root access is available
     * @return True if root access is available
     */
    private suspend fun checkRootAccess(): Boolean {
        if (rootAvailable) return true
        
        log("Checking for root access...", LogType.INFO)
        
        var rootCheckResult = false
        var rootDenied = false
        
        try {
            // Execute a simple su command to check for root
            val suResult = Shell.SH.run("su -c id")
            
            // Check for error messages that indicate root is not available
            if (suResult.stderr().contains("not found", ignoreCase = true) || 
                suResult.stderr().contains("permission denied", ignoreCase = true)) {
                rootDenied = true
            } else if (suResult.isSuccess && suResult.stdout().contains("uid=0")) {
                rootCheckResult = true
            }
            
            if (rootCheckResult) {
                log("Root access is available", LogType.SUCCESS)
                rootAvailable = true
            } else {
                if (rootDenied) {
                    log("Root access denied or not available", LogType.ERROR)
                } else {
                    log("Root check failed but without clear denial", LogType.WARNING)
                }
            }
        } catch (e: Exception) {
            log("Error checking root access: ${e.message}", LogType.ERROR)
        }
        
        return rootCheckResult
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
            log("ERROR: Root access is required for: $operation", LogType.ERROR)
            // Instead of constructing a Result object directly, run a harmless command that fails
            // This ensures we get a properly constructed Result object
            return Shell.SH.run("false && echo \"Root access required but not available\"")
        }
        
        // Execute with su if we have root, otherwise just use regular shell
        val fullCommand = if (hasRoot) "su -c \"$command\"" else command
        return shellHelper.runAndLog(fullCommand, operation)
    }
    
    fun initialize(context: Context) {
        log("ViewModel initialization started", LogType.INFO)
        appContext = context.applicationContext
        
        // First detect boot slot
        detectBootSlot()
        
        // Then check for magiskboot
        checkMagiskBoot()
        
        // Check for root access in the background
        viewModelScope.launch(Dispatchers.IO) {
            val hasRoot = checkRootAccess()
            if (!hasRoot) {
                log("WARNING: Root access not available. Some features will be limited.", LogType.WARNING)
                // Show a toast on the main thread
                withContext(Dispatchers.Main) {
                    appContext?.let {
                        Toast.makeText(it, "Root access not available. Some features will be limited.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        
        // Log our final initialization status
        log("ViewModel initialization completed", LogType.INFO)
    }

    fun setForcePatching(force: Boolean) {
        forcePatching = force
        log("Force patching without pattern matching: $force", LogType.INFO)
    }

    private fun checkMagiskBoot() {
        viewModelScope.launch(Dispatchers.IO) {
            val context = appContext ?: return@launch
            log("Initializing magiskboot...", LogType.INFO)
            
            // Initialize with more detailed logging to diagnose issues
            log("Checking if magiskboot is already initialized...", LogType.DEBUG)
            val currentPath = magiskBootHelper.getMagiskbootPath()
            if (currentPath != null) {
                log("Magiskboot already initialized at: $currentPath", LogType.DEBUG)
            } else {
                log("Magiskboot not initialized yet, setting up...", LogType.DEBUG)
            }
            
            val isAvailable = magiskBootHelper.checkMagiskBoot(context)
            log("Magiskboot initialization result: $isAvailable", LogType.INFO)
            _state.update { it.copy(magiskbootAvailable = isAvailable) }
            
            // Verify path again after initialization
            val newPath = magiskBootHelper.getMagiskbootPath()
            log("Magiskboot path after initialization: $newPath", LogType.DEBUG)
            
            if (state.value.magiskbootAvailable) {
                log("Magiskboot is ready to use", LogType.SUCCESS)
                // Add a quick sanity check to verify the path
                log("Magiskboot path is set to: ${magiskBootHelper.getMagiskbootPath()}", LogType.DEBUG)
            } else {
                log("WARNING: Magiskboot is not available - some functionality will be limited", LogType.WARNING)
            }
        }
    }

    private fun detectBootSlot() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val slot = fileHelper.getCurrentBootSlot()
                if (slot != null) {
                    _state.update { it.copy(currentBootSlot = slot) }
                    log("Current boot slot: $slot", LogType.INFO)
                }
            } catch (e: Exception) {
                log("Failed to detect boot slot: ${e.message}", LogType.ERROR)
                }
        }
    }

    fun log(message: String, type: LogType = LogType.INFO) {
        _state.update {
            it.copy(logs = it.logs + LogEntry(message, type))
        }
    }

    fun appendCommandOutput(output: String) {
        _state.update {
            it.copy(commandOutput = it.commandOutput + output)
        }
    }

    fun setScreen(screen: Screen) {
        _state.update { it.copy(currentScreen = screen) }
    }

    fun setInputFile(path: String) {
        _state.update { it.copy(inputFile = path) }
        log("Selected input file: $path", LogType.INFO)
    }

    fun setOutputFile(path: String) {
        _state.update { it.copy(outputFile = path) }
        log("Selected output file: $path", LogType.INFO)
    }

    fun patchCurrentBoot(verifyOnly: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            setScreen(Screen.PATCHING)
            
            // Check for root access first
            if (!checkRootAccess()) {
                log("ERROR: Root access is required to patch the current boot partition", LogType.ERROR)
                log("Please grant root access and try again", LogType.ERROR)
                _state.update { it.copy(patchSuccess = false) }
                setScreen(Screen.RESULT)
                return@launch
            }
            
            log(
                "Starting to patch current boot partition..." + if (verifyOnly) " (Verification Mode)" else "",
                LogType.INFO
            )

            val slot = state.value.currentBootSlot ?: ""
            val bootDevice = "/dev/block/by-name/boot$slot"

            // Create temp directory
            val tempDir = createTempDir("mtk_bpf_patch")
            log("Created temporary directory: ${tempDir.absolutePath}", LogType.DEBUG)

            try {
                // Extract boot image
                val bootImage = File(tempDir, "boot.img")
                log("Extracting boot image from $bootDevice", LogType.INFO)

                val extractResult = executeWithRoot(
                    "dd if=$bootDevice of=${bootImage.absolutePath} bs=4K",
                    "Extracting boot image"
                )
                if (!extractResult.isSuccess) {
                    throw Exception("Failed to extract boot image: ${extractResult.stderr()}")
                }

                log("Boot image extracted (${bootImage.length()} bytes)", LogType.SUCCESS)

                // Try to extract boot header information
                var outputFilename = "patched_boot_${System.currentTimeMillis()}.img"
                try {
                    appContext?.let { context ->
                        val headerInfo = magiskBootHelper.extractBootHeaderInfo(bootImage, context)
                        if (headerInfo != null) {
                            outputFilename = headerInfo.generateOutputFilename() + ".img"
                            log("Generated output filename from boot header: $outputFilename", LogType.INFO)
                        }
                    }
                } catch (e: Exception) {
                    log("Failed to extract boot header info: ${e.message}", LogType.WARNING)
                    log("Using default output filename", LogType.INFO)
                }

                // Create output file path in /sdcard for accessibility
                val outputPath = "/sdcard/$outputFilename"
                val outputFile = File(outputPath)

                // Patch the boot image
                if (patchBootImage(bootImage, outputFile, verifyOnly)) {
                    _state.update {
                        it.copy(
                            patchSuccess = true, outputFile = outputFile.absolutePath
                        )
                    }
                    log("Boot image ${if (verifyOnly) "verified" else "patched"} successfully!", LogType.SUCCESS)
                    log("Output saved to ${outputFile.absolutePath}", LogType.SUCCESS)
                } else {
                    throw Exception("Failed to ${if (verifyOnly) "verify" else "patch"} boot image")
                }
            } catch (e: Exception) {
                log("Error: ${e.message}", LogType.ERROR)
                _state.update { it.copy(patchSuccess = false) }
            } finally {
                // Clean up
                log("Cleaning up temporary files", LogType.DEBUG)
                tempDir.deleteRecursively()
                setScreen(Screen.RESULT)
            }
        }
    }

    fun patchFile(inputPath: String, outputPath: String, verifyOnly: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            setScreen(Screen.PATCHING)
            log("Starting kernel patching process..." + if (verifyOnly) " (Verification Mode)" else "", LogType.INFO)
            
            val tempDir = createTempDir("mtk_bpf_patch")
            log("Created temporary directory: ${tempDir.absolutePath}", LogType.DEBUG)

            try {
                val inputFile = File(inputPath)
                
                // Check if user has provided a specific output path
                var finalOutputPath = outputPath
                
                log("Input file size: ${inputFile.length()} bytes", LogType.DEBUG)

                // Detect file type
                val fileType = fileHelper.detectFileType(inputFile)
                log("Detected file type: $fileType", LogType.INFO)

                // If the output path is a directory or the default value, generate a smarter filename
                if (outputPath.endsWith("/") || outputPath == "/sdcard/patched_kernel.img") {
                    var outputFilename = "patched_${inputFile.nameWithoutExtension}_${System.currentTimeMillis()}.img"
                    
                    // For boot images, try to extract header info
                    if (fileType == FileType.BOOT_IMAGE) {
                        try {
                            appContext?.let { context ->
                                val headerInfo = magiskBootHelper.extractBootHeaderInfo(inputFile, context)
                                if (headerInfo != null) {
                                    outputFilename = headerInfo.generateOutputFilename() + ".img"
                                    log("Generated output filename from boot header: $outputFilename", LogType.INFO)
                                }
                            }
                        } catch (e: Exception) {
                            log("Failed to extract boot header info: ${e.message}", LogType.WARNING)
                            log("Using default output filename", LogType.INFO)
                        }
                    }
                    
                    // Determine the directory path
                    val dirPath = if (outputPath.endsWith("/")) outputPath else "/sdcard/"
                    finalOutputPath = dirPath + outputFilename
                }
                
                val outputFile = File(finalOutputPath)

                if (!inputFile.exists()) {
                    throw Exception("Input file not found: $inputPath")
                }

                when (fileType) {
                    FileType.BOOT_IMAGE -> {
                        // Patch boot image
                        if (patchBootImage(inputFile, outputFile, verifyOnly)) {
                            _state.update {
                                it.copy(
                                    patchSuccess = true, outputFile = outputFile.absolutePath
                                )
                            }
                            log(
                                "Boot image ${if (verifyOnly) "verified" else "patched"} successfully!",
                                LogType.SUCCESS
                            )
                            log("Output saved to ${outputFile.absolutePath}", LogType.SUCCESS)
                        } else {
                            throw Exception("Failed to ${if (verifyOnly) "verify" else "patch"} boot image")
                        }
                    }

                    FileType.KERNEL_GZ -> {
                        // Decompress, patch and recompress
                        val kernelFile = File(tempDir, "kernel.img")
                        if (!fileHelper.decompressGzip(inputFile, kernelFile)) {
                            throw Exception("Failed to decompress gzip file")
                        }

                        if (patchKernelFile(kernelFile, outputFile, true, verifyOnly)) {
                            _state.update {
                                it.copy(
                                    patchSuccess = true, outputFile = outputFile.absolutePath
                                )
                            }
                            log(
                                "Compressed kernel ${if (verifyOnly) "verified" else "patched"} successfully!",
                                LogType.SUCCESS
                            )
                            log("Output saved to ${outputFile.absolutePath}", LogType.SUCCESS)
                        } else {
                            throw Exception("Failed to ${if (verifyOnly) "verify" else "patch"} compressed kernel")
                        }
                    }

                    FileType.KERNEL_BIN -> {
                        // Patch raw kernel
                        if (patchKernelFile(inputFile, outputFile, false, verifyOnly)) {
                            _state.update {
                                it.copy(
                                    patchSuccess = true, outputFile = outputFile.absolutePath
                                )
                            }
                            log("Kernel ${if (verifyOnly) "verified" else "patched"} successfully!", LogType.SUCCESS)
                            log("Output saved to ${outputFile.absolutePath}", LogType.SUCCESS)
                        } else {
                            throw Exception("Failed to ${if (verifyOnly) "verify" else "patch"} kernel")
                        }
                    }
                }
            } catch (e: Exception) {
                log("Error: ${e.message}", LogType.ERROR)
                    _state.update { it.copy(patchSuccess = false) }
            } finally {
                // Clean up
                log("Cleaning up temporary files", LogType.DEBUG)
                tempDir.deleteRecursively()
                setScreen(Screen.RESULT)
            }
        }
    }

    /**
     * Patch a boot image
     */
    private suspend fun patchBootImage(bootFile: File, outputFile: File, verifyOnly: Boolean = false): Boolean {
        // If magiskboot is available, use it
        if (state.value.magiskbootAvailable) {
            return patchBootImageWithMagiskboot(bootFile, outputFile, verifyOnly, forcePatching)
        }

        // Otherwise use the fallback method
        val tempDir = createTempDir("mtk_bpf_patch")
        log("Created temporary directory for boot image patching: ${tempDir.absolutePath}", LogType.DEBUG)

        try {
            // Extract kernel from boot image
            val kernelFile = File(tempDir, "kernel.img")
            if (!fileHelper.extractKernelFromBoot(bootFile, kernelFile)) {
                throw Exception("Failed to extract kernel from boot image")
            }

            // Extract boot header
            val headerFile = File(tempDir, "header.img")
            if (!fileHelper.extractBootHeader(bootFile, headerFile)) {
                throw Exception("Failed to extract boot header")
            }

            // Patch the kernel
            val patchedKernel = File(tempDir, "patched_kernel.img")
            val patchResult = patchKernelFile(kernelFile, patchedKernel, false, verifyOnly)

            if (!patchResult) {
                if (verifyOnly) {
                    log("Verification mode: Continuing without patching", LogType.INFO)
                } else {
                    return false
                }
            }

            // Copy header to output file
            if (!fileHelper.copyFile(headerFile, outputFile)) {
                throw Exception("Failed to copy boot header")
            }

            // Check if original kernel was compressed
            val isCompressed = fileHelper.detectFileType(kernelFile) == FileType.KERNEL_GZ

            if (isCompressed) {
                // Compress patched kernel
                val compressedKernel = File(tempDir, "patched_kernel.img.gz")
                if (!fileHelper.compressGzip(patchedKernel, compressedKernel)) {
                    throw Exception("Failed to compress patched kernel")
                }

                // Append compressed kernel to output
                if (!fileHelper.appendFile(compressedKernel, outputFile)) {
                    throw Exception("Failed to append compressed kernel")
                }
            } else {
                // Append patched kernel to output
                if (!fileHelper.appendFile(patchedKernel, outputFile)) {
                    throw Exception("Failed to append patched kernel")
                }
            }

            log(
                "Successfully created ${if (verifyOnly) "verified" else "patched"} boot image (${outputFile.length()} bytes)",
                LogType.SUCCESS
            )
            return true
        } catch (e: Exception) {
            log("Error ${if (verifyOnly) "verifying" else "patching"} boot image: ${e.message}", LogType.ERROR)
            return false
        } finally {
            tempDir.deleteRecursively()
        }
    }

    /**
     * Patch a boot image using magiskboot
     */
    private suspend fun patchBootImageWithMagiskboot(
        bootFile: File, outputFile: File, verifyOnly: Boolean = false, forcePatching: Boolean
    ): Boolean {
        val tempDir = createTempDir("mtk_bpf_patch")
        log("Created temporary directory for magiskboot: ${tempDir.absolutePath}", LogType.DEBUG)
        
        // Track if we encounter a compression format error
        var compressionFormatError = false
        
        // Add an error tracking function that wraps the original logger
        val errorTracker: (String, LogType) -> Unit = { message, type ->
            // Forward to the normal logger
            log(message, type)
            
            // Check for compression format error
            if (type == LogType.ERROR && message.contains("Unsupported kernel compression format")) {
                compressionFormatError = true
            }
        }
        
        // Keep track of the original logging function
        val originalLogger = magiskBootHelper.logger
        
        // Temporarily replace the logger to track errors
        magiskBootHelper.logger = errorTracker

        try {
            // Unpack the boot image using magiskboot
            log("Using magiskboot to unpack boot image", LogType.INFO)
            log("Boot image path: ${bootFile.absolutePath}", LogType.DEBUG)
            log("Temp directory: ${tempDir.absolutePath}", LogType.DEBUG)
            
            if (!magiskBootHelper.unpackBootImage(bootFile, tempDir)) {
                // Check if we detected a compression format error
                if (compressionFormatError) {
                    // This is a compression format issue, not a magiskboot availability issue
                    log("Failed to patch boot image due to unsupported compression format", LogType.ERROR)
                    log("The app cannot proceed with patching this specific boot image", LogType.ERROR)
                    return false
                } else {
                    // This is likely a magiskboot binary issue
                    log("ERROR: Failed to unpack boot image with magiskboot", LogType.ERROR)
                    log("This app requires a working magiskboot binary to patch boot images.", LogType.ERROR)
                    log("Check the extraction log for details on why magiskboot failed.", LogType.ERROR)
                    return false
                }
            }

            // Get the extracted kernel
            val kernelFile = File(tempDir, "kernel")
            if (!kernelFile.exists()) {
                throw Exception("Kernel not found after unpacking boot image")
            }
            
            log("Kernel extracted, size: ${kernelFile.length()} bytes", LogType.INFO)

            // Patch the kernel (if not in verify mode)
            val patchedKernelFile = File(tempDir, "kernel_patched")
            if (verifyOnly) {
                // In verify mode, just copy the kernel
                log("Verification mode: Skipping patching", LogType.INFO)
                if (!fileHelper.copyFile(kernelFile, patchedKernelFile)) {
                    throw Exception("Failed to copy kernel file")
                }
            } else {
                // Try the hexpatch method first
                log("Attempting to patch the kernel using magiskboot hexpatch", LogType.INFO)
                val hexpatchSucceeded = magiskBootHelper.patchKernelWithHexpatch(kernelFile)
                
                if (hexpatchSucceeded) {
                    log("Successfully patched kernel using magiskboot hexpatch", LogType.SUCCESS)
                } else {
                    // If hexpatch fails, fall back to the original binary patching method
                    log("Hexpatch failed, falling back to binary pattern matching", LogType.WARNING)
                    
                    // Read kernel data
                    log("Reading kernel file: ${kernelFile.absolutePath}", LogType.DEBUG)
                    val kernelData = FileInputStream(kernelFile).use { it.readBytes() }
                    log("Read ${kernelData.size} bytes from kernel file", LogType.INFO)

                    // Patch the kernel data
                    val patchResult = patcher.patchBinary(kernelData, verifyOnly)

                    // Log the detailed diagnostic info
                    log("--- Diagnostic Information ---", LogType.INFO)
                    patchResult.details.split("\n").forEach { line ->
                        if (line.isNotEmpty()) {
                            log(line, LogType.INFO)
                        }
                    }

                    if (patchResult.patternFound == null && !verifyOnly) {
                        log("No matching pattern found in kernel", LogType.ERROR)
                        log("READ THIS CAREFULLY. COMPARING AGAINST KNOWN PATTERNS NONE OF THEM MATCHED. THERE'S NOTHING FOR ME TO DO ABOUT IT,", LogType.ERROR)
                        log("but", LogType.INFO)
                        log("YOU CAN JOIN SUPPORT GROUP AND PAPACU WILL TRY TO HELP YOU IF HE FEELS LIKE IT", LogType.WARNING)
                        log("You can use Verification Mode to repackage without patching to verify that app repacks the kernel correctly.\nTHIS IS NOT THE SAME AS PATCHING", LogType.INFO)
                        return false
                    }

                    if (patchResult.patternFound != null) {
                        log("Found matching pattern: ${patchResult.patternFound}", LogType.SUCCESS)

                        // Write the patched kernel
                        FileOutputStream(patchedKernelFile).use { it.write(patchResult.bytes) }
                        log("Patched kernel saved (${patchedKernelFile.length()} bytes)", LogType.SUCCESS)

                        // Replace the original kernel file with the patched one
                        val moveResult = shell.run("mv ${patchedKernelFile.absolutePath} ${kernelFile.absolutePath}")
                        if (!moveResult.isSuccess) {
                            throw Exception("Failed to replace kernel with patched version")
                        }
                        log("Replaced original kernel with patched version", LogType.SUCCESS)
                    } else {
                        if (patchResult.foundPartial) log("READ THIS CAREFULLY. THERE WAS NO EXACT MATCHING PATTERNS FOUND BUT THERE'S A SIMILAR ONE. PROVIDE IT TO SUPPORT GROUP AND YOU'LL GET A TEST VERSION",
                            LogType.WARNING)
                        log("No matching pattern found, proceeding without patching", LogType.WARNING)
                    }
                }
            }

            // Repack the boot image using magiskboot
            log("Repacking boot image with magiskboot", LogType.INFO)
            log("Original boot image: ${bootFile.absolutePath}", LogType.DEBUG)
            log("Output boot image: ${outputFile.absolutePath}", LogType.DEBUG)
            
            if (!magiskBootHelper.repackBootImage(bootFile, tempDir, outputFile)) {
                // This will fail if magiskboot is not available
                log("ERROR: Failed to repack boot image with magiskboot", LogType.ERROR)
                log("This app requires a working magiskboot binary to patch boot images.", LogType.ERROR)
                log("Check the extraction log for details on why magiskboot failed.", LogType.ERROR)
                return false
            }

            log(
                "Successfully ${if (verifyOnly) "verified" else "patched"} boot image with magiskboot (${outputFile.length()} bytes)",
                LogType.SUCCESS
            )
           if (!verifyOnly) log(
            "IF THIS HELPED YOU, CONSIDER MAKING A DONATION TO SHOW YOUR APPRECIATION FOR THE WORK I PUT INTO THIS APP",
            LogType.WARNING
            )
            return true
        } catch (e: Exception) {
            log(
                "Error ${if (verifyOnly) "verifying" else "patching"} boot image with magiskboot: ${e.message}",
                LogType.ERROR
            )

             return false

        } finally {
            tempDir.deleteRecursively()
            magiskBootHelper.logger = originalLogger
        }
    }


    /**
     * Patch a kernel file
     */
    private suspend fun patchKernelFile(
        kernelFile: File, outputFile: File, isCompressed: Boolean, verifyOnly: Boolean = false
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Read kernel data
                log("Reading kernel file: ${kernelFile.absolutePath}", LogType.DEBUG)
                val kernelData = FileInputStream(kernelFile).use { it.readBytes() }
                log("Read ${kernelData.size} bytes from kernel file", LogType.INFO)

                // Patch the kernel data
                val patchResult = patcher.patchBinary(kernelData, verifyOnly)

                // Log the detailed diagnostic info
                log("--- Diagnostic Information ---", LogType.INFO)
                patchResult.details.split("\n").forEach { line ->
                    if (line.isNotEmpty()) {
                        log(line, LogType.INFO)
                    }
                }

                if (patchResult.patternFound == null && !verifyOnly) {
                    log("No matching pattern found in kernel", LogType.ERROR)
                    log("You can use Verification Mode to repackage without patching", LogType.INFO)
                    return@withContext false
                }

                if (patchResult.patternFound != null) {
                    log("Found matching pattern: ${patchResult.patternFound}", LogType.SUCCESS)
                } else if (verifyOnly) {
                    log("Verification mode: Continuing without patching", LogType.INFO)
                }

                // Generate a message based on what happened
                val action = when {
                    patchResult.wasModified -> "patched"
                    verifyOnly -> "packaged (verification mode)"
                    else -> "no action taken"
                }
                log("Kernel $action", LogType.SUCCESS)

                // Write the kernel
                val outputBytes = patchResult.bytes
                if (isCompressed) {
                    // First write to a temp file
                    val tempFile = File.createTempFile("kernel", ".img")
                    FileOutputStream(tempFile).use { it.write(outputBytes) }

                    // Then compress to the output file
                    val result = fileHelper.compressGzip(tempFile, outputFile)
                    tempFile.delete()

                    if (!result) {
                        return@withContext false
                    }
                } else {
                    // Write directly to output file
                    FileOutputStream(outputFile).use { it.write(outputBytes) }
                }

                log("Kernel saved to ${outputFile.absolutePath} (${outputFile.length()} bytes)", LogType.SUCCESS)
                return@withContext true
            } catch (e: Exception) {
                log("Error ${if (verifyOnly) "verifying" else "patching"} kernel: ${e.message}", LogType.ERROR)
                    return@withContext false
            }
        }
    }

    fun flashPatchedBoot(patchedBootPath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            log("Preparing to flash patched boot image...", LogType.WARNING)
            log("THIS IS DANGEROUS! Make sure you have a backup!", LogType.WARNING)
            
            // Check for root access first
            if (!checkRootAccess()) {
                log("ERROR: Root access is required to flash boot image", LogType.ERROR)
                log("Please grant root access and try again", LogType.ERROR)
                return@launch
            }

            try {
                val bootFile = File(patchedBootPath)
                if (!bootFile.exists()) {
                    throw Exception("Boot image file not found")
                }

                if (fileHelper.flashBootImage(bootFile, state.value.currentBootSlot)) {
                    log("Boot image flashed successfully!", LogType.SUCCESS)
                    log("Please reboot your device to apply changes", LogType.INFO)
                } else {
                    log("Failed to flash boot image", LogType.ERROR)
                }
            } catch (e: Exception) {
                log("Error flashing boot image: ${e.message}", LogType.ERROR)
                }
        }
    }

    fun getFormattedLogs(): String {
        val sb = StringBuilder()
        sb.appendLine("=== MTK BPF Patcher Logs ===")
        sb.appendLine("Generated: ${Date()}")
        sb.appendLine("Boot slot: ${state.value.currentBootSlot ?: "unknown"}")
        sb.appendLine("Input file: ${state.value.inputFile ?: "none"}")
        sb.appendLine("Output file: ${state.value.outputFile ?: "none"}")
        sb.appendLine("Patch result: ${if (state.value.patchSuccess) "SUCCESS" else "FAILED"}")
        sb.appendLine("MagiskBoot available: ${state.value.magiskbootAvailable}")
        sb.appendLine("=== Logs ===")

        state.value.logs.forEach { entry ->
            val prefix = when (entry.type) {
                LogType.INFO -> "INFO"
                LogType.ERROR -> "ERROR"
                LogType.SUCCESS -> "SUCCESS"
                LogType.WARNING -> "WARNING"
                LogType.COMMAND -> "CMD"
                LogType.DEBUG -> "DEBUG"
            }
            sb.appendLine("[$prefix] ${entry.message}")
        }

        sb.appendLine("\n=== Command Output ===")
        sb.appendLine(state.value.commandOutput)

        return sb.toString()
    }

    /**
     * Get the contents of the magiskboot extraction log if it exists
     */
    fun getMagiskbootExtractionLog(): String {
        val context = appContext ?: return "Context not available"
        val logFile = File(context.filesDir, "magiskboot_extraction.log")
        
        return if (logFile.exists()) {
            try {
                logFile.readText()
            } catch (e: Exception) {
                "Error reading log file: ${e.message}"
            }
        } else {
            "Log file does not exist"
        }
    }
}
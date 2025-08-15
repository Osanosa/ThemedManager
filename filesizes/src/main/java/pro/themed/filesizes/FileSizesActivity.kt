package pro.themed.filesizes

import android.content.Context
import android.os.Bundle
import android.webkit.MimeTypeMap
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.AutoMirrored.Filled
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import pro.themed.pxlsrtr.ui.theme.FileSizesTheme
import java.io.File

data class ItemState(val size: Int, val du: Boolean = false)

class FileSizesActivity : ComponentActivity() {
    
    // Single shell instance with proper synchronization
    private val shell = Shell.SH
    private val shellMutex = Mutex()
    private var shellJob: Job? = null
    private var isScanning = false
    
    // Move scan-related state to class level
    private val currentList = mutableStateMapOf<String, ItemState>()
    private var currentPath by mutableStateOf("/sdcard")
    private var isLoading by mutableStateOf(false)
    private var command = "ls -d -1 -s -p -a -L \"\$(pwd -P)/\"*"

    override fun onBackPressed() {
        if (false) super.onBackPressed()
        // Use proper coroutine for shell operations
        shellJob = CoroutineScope(Dispatchers.IO).launch {
            interruptAndStartNewScan("..")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up shell operations
        shellJob?.cancel()
        shell.shutdown()
    }

    // Sequential execution: run du after ls for specific item
    private suspend fun runDuForItem(path: String) = withContext(Dispatchers.IO) {
        shellMutex.withLock {
            shell.run("du -s \"$path\"") {
                onStdOut = { line: String ->
                    line.log()
                    val parts = line.trim().split("\t")
                    if (parts.size >= 2) {
                        val (size, name) = parts
                        try {
                            currentList[name] = ItemState(size.toInt(), true)
                        } catch (e: NumberFormatException) {
                            line.log("DU size parse error")
                        }
                    }
                }
                onStdErr = { line: String ->
                    line.log("DU error")
                }
            }
        }
    }

    // Main function: ls first, then du sequentially for each item
    private suspend fun scanCurrentDirectory(): Unit = withContext(Dispatchers.IO) {
        // Prevent multiple concurrent scans
        if (isScanning) return@withContext
        isScanning = true
        isLoading = true
        currentList.clear()
        
        try {
            // Check for cancellation
            if (!isActive) return@withContext
            
            // Step 1: Get current path
            val path = shellMutex.withLock {
                if (!isActive) return@withContext
                shell.run("pwd -P").stdout().trim()
            }
            currentPath = path

            // Check for cancellation before ls
            if (!isActive) return@withContext

            // Step 2: Run ls command first
            val lsItems = mutableSetOf<String>()
            shellMutex.withLock {
                if (!isActive) return@withContext
                shell.run(command) {
                    onStdOut = { line: String ->
                        line.log("LS")
                        try {
                            val parts = line.trim().split(Regex("\\s+"), limit = 2)
                            if (parts.size >= 2) {
                                val (sizeString, name) = parts
                                val size = sizeString.toInt()
                                val fixedName = name.replace(Regex("^//"), "/")
                                
                                // Add to currentList with ls size and collect for du
                                if (command.contains("-p")) {
                                    currentList[fixedName] = ItemState(size)
                                    if (!fixedName.contains("/proc")) {
                                        lsItems.add(fixedName)
                                    }
                                } else {
                                    if (File(fixedName).isDirectory) {
                                        val dirName = "$fixedName/"
                                        currentList[dirName] = ItemState(size)
                                        if (!fixedName.contains("/proc")) {
                                            lsItems.add(dirName)
                                        }
                                    } else {
                                        currentList[fixedName] = ItemState(size)
                                        if (!fixedName.contains("/proc")) {
                                            lsItems.add(fixedName)
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            line.log("LS parse error: ${e.message}")
                        }
                    }
                    onStdErr = { line: String ->
                        line.log("LS error")
                        // Handle command option errors
                        command = when {
                            line.contains("Unknown option '-1'") -> command.replace("-1", "")
                            line.contains("Unknown option '-p'") -> command.replace("-p", "")
                            line.contains("Unknown option '-L'") -> command.replace("-L", "")
                            else -> command
                        }
                    }
                }
            }

            // Check for cancellation before du operations
            if (!isActive) return@withContext

            // Step 3: Run du sequentially for each item found by ls
            lsItems.forEach { item ->
                if (isActive) { // Check if coroutine is still active
                    runDuForItem(item)
                } else {
                    return@withContext // Exit early if cancelled
                }
            }
        } catch (e: Exception) {
            e.message?.log("Scan error")
        } finally {
            isScanning = false
            isLoading = false
        }
    }

    // Helper function to properly interrupt current scan and start new one
    private suspend fun interruptAndStartNewScan(newPath: String? = null) {
        // Cancel current job
        shellJob?.cancel()
        
        // Interrupt shell operations
        shell.interrupt()
        
        // Wait briefly for interruption to take effect
        delay(50)
        
        // Change directory if specified
        if (newPath != null) {
            shellMutex.withLock {
                shell.run("cd \"$newPath\"")
            }
        }
        
        // Start new scan
        scanCurrentDirectory()
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Setup shell error listener
            shell.addOnStderrLineListener(object : Shell.OnLineListener {
                override fun onLine(line: String) {
                    line.log()
                }
            })

            val context = LocalContext.current
            val sharedPrefs = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val mode = rememberSaveable { mutableIntStateOf(sharedPrefs.getInt("mode", 3)) }
            sharedPrefs.registerOnSharedPreferenceChangeListener { _, _ ->
                mode.intValue = sharedPrefs.getInt("mode", 3)
            }
            if (mode.intValue == 3) {
                ModeDialog()
            }

            val listOfStorageVolume by remember {
                mutableStateOf(context.externalCacheDirs.map {
                    it?.path?.removeSuffix("/Android/data/pro.themed.filesizes/cache") ?: "/sdcard"
                })
            }
            var currentSize by remember { mutableIntStateOf(0) }

            // Initialize shell based on mode
            LaunchedEffect(mode.intValue) {
                if (mode.intValue == 0) {
                    shellMutex.withLock {
                        shell.run("su -t 1")
                    }
                }
                shellMutex.withLock {
                    shell.run("cd -P /sdcard")
                }
            }

            val coroutineScope = rememberCoroutineScope()

            // Shell command result listener
            shell.addOnCommandResultListener(object : Shell.OnCommandResultListener {
                override fun onResult(result: Shell.Command.Result) {
                    result.log()
                    if (result.details.command.startsWith("cd ")) {
                        shellJob = coroutineScope.launch {
                            scanCurrentDirectory()
                        }
                    }
                }
            })

            // Initial scan
            LaunchedEffect(Unit, mode) { 
                scanCurrentDirectory()
            }

            FileSizesTheme {
                Surface(modifier = Modifier.fillMaxSize()) {

                    Column {
                        var expanded by remember { mutableStateOf(false) }
                        TopAppBar(title = {
                            Text(currentPath,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { expanded = !expanded }
                                    .padding(8.dp))
                        }, expandedHeight = 56.dp, navigationIcon = {
                            //go up
                            IconButton(onClick = {
                                shellJob = coroutineScope.launch {
                                    interruptAndStartNewScan("..")
                                }
                            }) {
                                Icon(Filled.ArrowBack, null, Modifier.graphicsLayer(rotationZ = 90f))
                            }
                        })
                        AnimatedVisibility(expanded) {
                            Column {

                                listOfStorageVolume.forEach {
                                    Row(verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                shellJob = coroutineScope.launch {
                                                    interruptAndStartNewScan(it)
                                                }
                                            }) {
                                        Text(it)
                                    }
                                }
                            }
                        }




                        if (isLoading) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        LazyColumn(userScrollEnabled = true,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier) {

                            items(currentList.filter {
                                //contains current path and has no children after /
                                it.key.startsWith(currentPath) && !it.key.removePrefix(currentPath).removePrefix("/")
                                    .removeSuffix("/").contains("/")
                            }.toList().sortedByDescending { it.second.size }, key = { it.first }) { item ->
                                Box(Modifier.animateItem()) {
                                    val mimeTypeMap = MimeTypeMap.getSingleton()
                                    val mimeType =
                                        mimeTypeMap.getMimeTypeFromExtension(item.first.substringAfterLast("."))

                                    Item(item.first.removePrefix(currentPath).removePrefix("/"),
                                        item.second,
                                        mimeType,
                                        onClick = {
                                            if (!item.first.endsWith("/")) {

                                                openFile(item, context, mimeType)

                                            }
                                            else {
                                                shellJob = coroutineScope.launch {
                                                    interruptAndStartNewScan(item.first)
                                                }
                                            }
                                        },
                                        fullPath = item.first,
                                        onDeletion = {
                                            currentList.remove(item.first)
                                        })

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }


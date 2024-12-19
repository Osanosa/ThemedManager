package pro.themed.filesizes

import android.content.*
import android.os.*
import android.webkit.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons.AutoMirrored.Filled
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import com.google.accompanist.permissions.*
import com.jaredrummler.ktsh.*
import kotlinx.coroutines.*
import pro.themed.pxlsrtr.ui.theme.*
import java.io.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

data class ItemState(val size: Int, val du: Boolean = false)

val lsShell = Shell.SH
val duShell = Shell.SH

class FileSizesActivity : ComponentActivity() {

    override fun onBackPressed() {
        if (false) super.onBackPressed()
        lsShell.run("cd ..")
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            lsShell.addOnStderrLineListener(object : Shell.OnLineListener {
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

            val currentList = remember { mutableStateMapOf<String, ItemState>() }
            val listOfStorageVolume by remember {
                mutableStateOf(context.externalCacheDirs.map {
                    it?.path?.removeSuffix("/Android/data/pro.themed.filesizes/cache") ?: "/sdcard"
                })
            }
            var currentPath by rememberSaveable { mutableStateOf("/sdcard") }
            var currentSize by remember { mutableIntStateOf(0) }


            if (mode.intValue == 0) {
                lsShell.run("su -t 1")
                duShell.run("su -t 1")

            }
            lsShell.run("cd -P /sdcard")

            val coroutineScope = rememberCoroutineScope()
            var command = "ls -d -1 -s -p -a -L \"\$(pwd -P)/\"*"
            lsShell.run(command){
                onStdErr = { line: String ->
                    // if there is an error, log it
                    // the error is usually because the command is not supported
                    // in this case, remove the unsupported option from the command
                    line.log()
                    currentList.clear()
                    command = when {
                        line.contains("Unknown option '-1'") -> command.replace("-1", "") // -1 lists one file per line
                        line.contains("Unknown option '-p'") -> command.replace("-p", "") // -p adds a / to the end of directories
                        line.contains("Unknown option '-L'") -> command.replace("-L", "") // -L follows symbolic links to their targets
                        else -> command
                    }

                }

            }

            suspend fun du(path: String) = coroutineScope.launch {
                duShell.run("du -s \"$path\"") {
                    onStdOut = { line: String ->
                        line.log()
                        val (size, name) = line.trim().split("\t")
                        currentList[name] = ItemState(size.toInt(), true)
                    }
                }
            }


            // run ls and du commands in parallel
            // ls is used to get the list of files and directories
            // du is used to get the size of each file and directory
            // the two commands are run in parallel to speed up the process
            suspend fun ls(): Unit =  coroutineScope {
                // run ls command
                // the output is stored in the currentList map
                // the key is the path of the file or directory
                // the value is the size of the file or directory
                // if the file or directory is a symbolic link, the size is 0
                val path = lsShell.run("pwd -P").stdout().trim()
                currentPath = path

                lsShell.run(command) {
                    onStdOut = { line: String ->
                        // process the output of the ls command
                        // split the line into the size and name
                        // if the file or directory is a symbolic link, the name is the target of the symbolic link
                        // add the size and name to the currentList map
                        line.log("LS")

                        val (sizeString, name) = line.trim().split(Regex("\\s+"), limit = 2)
                        val size = sizeString.toInt()
                        val fixedName = name.replace(Regex("^//"), "/")
                        if (command.contains("-p")) {
                            if (currentList[fixedName]?.du != true) currentList[fixedName] = ItemState(size)
                        }
                        else {
                            if (File(fixedName).isDirectory) currentList["$fixedName/"] = ItemState(size)
                            else currentList[fixedName] = ItemState(size)
                        }
                        launch { if (!fixedName.contains("/proc")) du(fixedName) }
                        currentList.keys.filter { it.startsWith(fixedName) }.forEach { it.log("ITEM") }
                    }
                    onStdErr = { line: String ->
                        // if there is an error, log it
                        // the error is usually because the command is not supported
                        // in this case, remove the unsupported option from the command
                        line.log()
                        command = when {
                            line.contains("Unknown option '-1'") -> command.replace("-1", "") // -1 lists one file per line
                            line.contains("Unknown option '-p'") -> command.replace("-p", "") // -p adds a / to the end of directories
                            line.contains("Unknown option '-L'") -> command.replace("-L", "") // -L follows symbolic links to their targets
                            else -> command
                        }
                        launch {
                            // if there is no error, run the command again
                            // if there is an error, do not run the command again
                            if (!line.contains("No such file or directory")) ls()
                        }
                    }
                }
            }
            lsShell.addOnCommandResultListener(object : Shell.OnCommandResultListener {
                override fun onResult(result: Shell.Command.Result) {
                    result.log()
                    if (result.details.command == "cd ..") {
                        CoroutineScope(Dispatchers.Default).launch {
                            lsShell.interrupt()
                            duShell.interrupt()
                            ls()
                        }
                    }
                }
            })

            LaunchedEffect(Unit, mode) { ls() }

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

                                CoroutineScope(Dispatchers.Default).launch {
                                    lsShell.run("cd ..")

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
                                                CoroutineScope(Dispatchers.Default).launch {
                                                    coroutineScope.cancel()
                                                    lsShell.interrupt()
                                                    duShell.interrupt()

                                                    lsShell.run("cd \"${it}\"")
                                                    ls()
                                                }
                                            }) {
                                        Text(it)
                                    }
                                }
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

                                                CoroutineScope(Dispatchers.Default).launch {
                                                    coroutineScope.cancel()
                                                    lsShell.interrupt()
                                                    duShell.interrupt()
                                                    lsShell.run("cd \"${item.first}\"")
                                                    ls()
                                            }}
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



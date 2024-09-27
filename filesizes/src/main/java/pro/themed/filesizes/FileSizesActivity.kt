package pro.themed.filesizes

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
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
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.jaredrummler.ktsh.*
import com.jaredrummler.ktsh.Shell.*
import kotlinx.coroutines.*
import pro.themed.pxlsrtr.ui.theme.*
import java.util.concurrent.TimeUnit.*

class FileSizesActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FileSizesTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var currentList = remember { mutableStateMapOf<String, Int>() }
                    var currentPath by rememberSaveable { mutableStateOf(Environment.getExternalStorageDirectory().path) }
                    var currentSize by remember { mutableIntStateOf(0) }
                    val lsShell = Shell("su -t 1")
                    val duShell = Shell("su -t 1")

                    fun du() {

                        val list = duShell.run("ls -p -1 ").stdout.also { it.log("LIST") }
                        list.forEach { path ->
                            path.log("PATH")
                            if (!path.contains("proc/")) duShell.run("du -d 0 \"${path}\"".replace("//", "/")
                                .also { it.log("CMD") }) {
                                onStdOut = { line1: String ->
                                    if (currentList.containsKey(line1.trim().substringAfter("\t"))) {
                                        currentList[line1.trim().substringAfter("\t")] =
                                            line1.trim().substringBefore("\t").toInt()

                                    }

                                }
                                onStdErr = { line1: String -> line1.log("LINE1 ERROR") }
                                timeout = Timeout(1, SECONDS)
                            }
                            currentSize = currentList.values.sum()
                        }
                    }

                    fun ls() {

                        CoroutineScope(Dispatchers.IO).launch {
                            currentPath = lsShell.run("pwd").stdout().trim()
                            lsShell.run("ls -s -p -1") {

                                onStdOut = { line: String ->
                                    line.log("LS")
                                    if (line.startsWith("total")) {
                                        currentSize = line.substringAfter(" ").toInt()
                                    } else {
                                        val size = line.trim().substringBefore(" ").toInt()
                                        val path = line.trim().substringAfter(" ")
                                        currentList[path] = size

                                    }
                                }
                                onStdErr = { line: String -> line.log() }
                                timeout = Timeout(1, SECONDS)

                            }
                        }
                    }



                    lsShell.run("cd $currentPath")
                    duShell.run("cd $currentPath")
                    LaunchedEffect(Unit) {
                        launch { ls() }
                        launch { du() }

                    }

                    Column {
                        TopAppBar(title = { Text(currentPath) }, expandedHeight = 56.dp, navigationIcon = {
                            //go up
                            IconButton(onClick = {
                                currentList.clear()

                                CoroutineScope(Dispatchers.Default).launch {
                                    lsShell.interrupt()
                                    duShell.interrupt()
                                    lsShell.run("cd ..")
                                    duShell.run("cd ..")
                                    ls()
                                    du()
                                }
                            }) {
                                Icon(Filled.ArrowBack, null)
                            }
                        })
                        LazyColumn(
                            userScrollEnabled = true,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {

                            items(currentList.toList().sortedByDescending { it.second }) { item ->
                                var height by remember { mutableIntStateOf(0) }
                                Box(Modifier
                                    .onSizeChanged {
                                        height = it.height
                                    }
                                    .onGloballyPositioned {
                                        height = it.size.height
                                    }
                                    .animateItem()
                                    .fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                                    CookieCard(onClick = {

                                        currentList.clear()
                                        CoroutineScope(Dispatchers.Default).launch {
                                            lsShell.interrupt()
                                            duShell.interrupt()
                                            lsShell.run("cd \"${item.first}\"")
                                            duShell.run("cd \"${item.first}\"")
                                            ls()
                                            du()
                                        }

                                    }) {

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(8.dp)
                                        ) {
                                            //box with icon
                                            Box {
                                                //circle background
                                                Surface(shape = CircleShape, color = contentcol.copy(alpha = 0.5f)) {

                                                    Icon(
                                                        modifier = Modifier.padding(8.dp), imageVector = when {
                                                            item.first.endsWith("/")                                         -> ImageVector.vectorResource(
                                                                R.drawable.folder_24px
                                                            )
                                                            item.first.endsWith(".jpg") || item.first.endsWith(".png") || item.first.endsWith(
                                                                ".jpeg"
                                                            ) || item.first.endsWith(".gif") || item.first.endsWith(".webp") -> ImageVector.vectorResource(
                                                                R.drawable.image_24px
                                                            )
                                                            item.first.endsWith(".mp4") || item.first.endsWith(".mkv") || item.first.endsWith(
                                                                ".avi"
                                                            ) || item.first.endsWith(".mov")                                 -> ImageVector.vectorResource(
                                                                R.drawable.movie_24px
                                                            )
                                                            item.first.endsWith(".mp3") || item.first.endsWith(".wav") || item.first.endsWith(
                                                                ".ogg"
                                                            ) || item.first.endsWith(".flac")                                -> ImageVector.vectorResource(
                                                                R.drawable.music_note_24px
                                                            )
                                                            item.first.endsWith(".pdf")                                      -> ImageVector.vectorResource(
                                                                R.drawable.picture_as_pdf_24px
                                                            )
                                                            item.first.endsWith(".zip") || item.first.endsWith(".rar") || item.first.endsWith(
                                                                ".7z"
                                                            ) || item.first.endsWith(".tar") || item.first.endsWith(".gz") || item.first.endsWith(
                                                                ".xz"
                                                            )                                                                -> ImageVector.vectorResource(
                                                                R.drawable.folder_zip_24px
                                                            )
                                                            item.first.endsWith(".apk")                                      -> ImageVector.vectorResource(
                                                                R.drawable.apk_document_24px
                                                            )
                                                            else                                                             -> ImageVector.vectorResource(
                                                                R.drawable.draft_24px
                                                            )
                                                        }, contentDescription = null
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                item.first, modifier = Modifier.weight(1f)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                when (item.second) {
                                                    in 0..1024             -> "%.2f KB".format((item.second.toFloat() / 1024))
                                                    in 1025..1048576       -> "%.2f MB".format((item.second.toFloat() / 1024))
                                                    in 1048577..1073741824 -> "%.2f GB".format((item.second.toFloat() / 1048576))
                                                    else                   -> "%.2f TB".format((item.second.toFloat() / 1073741824))
                                                }
                                            )
                                        }
                                    }


                                    Row(
                                        modifier = Modifier.clip(RoundedCornerShape(16.dp, 0.dp, 0.dp, 16.dp))
                                            .background(
                                                Color.Red.copy(alpha = 0.1f)
                                            )
                                            .height(height.pxToDp())
                                            .fillMaxWidth(item.second.toFloat() / currentSize.toFloat())
                                            ,

                                        ) {}
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }
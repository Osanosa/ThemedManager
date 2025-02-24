package pro.themed.audhdlauncher

import android.app.ActivityOptions
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pro.themed.audhdlauncher.database.LauncherDbHelper
import pro.themed.audhdlauncher.database.getSortedAppsByCategory
import pro.themed.audhdlauncher.database.updateDefaultFilters
import pro.themed.audhdlauncher.ui.theme.ThemedManagerTheme
import pro.themed.audhdlauncher.ui.theme.contentcol
import pro.themed.audhdlauncher.ui.theme.cookieForeground

class AuDHDLauncherActivity : ComponentActivity() {

    @OptIn(ExperimentalLayoutApi::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            var isVisible by remember { mutableStateOf(true) }

            val readPermissionState =
                rememberMultiplePermissionsState(
                    listOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    )
                )

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                readPermissionState.launchMultiplePermissionRequest()
                if (!readPermissionState.allPermissionsGranted) {
                    Toast.makeText(context, "No permission, change in settings", Toast.LENGTH_SHORT)
                        .show()
                    isVisible = true
                } else {
                    Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (!Environment.isExternalStorageManager()) {
                    Toast.makeText(context, "No permission, change in settings", Toast.LENGTH_SHORT)
                        .show()
                    isVisible = true
                }
            }
            if (!Environment.isExternalStorageManager()) {
                context.startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        Uri.parse("package:pro.themed.audhdlauncher"),
                    )
                )
            }
            ThemedManagerTheme {
                val context = LocalContext.current
                val dbHelper = LauncherDbHelper(context)

                LaunchedEffect(key1 = Unit) { dbHelper.writableDatabase.updateDefaultFilters() }
                Box(modifier = Modifier.safeDrawingPadding().padding(horizontal = 8.dp)) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp),) {
                        @Composable
                        fun DebugAppList(context: Context, dbHelper: LauncherDbHelper) {
                            val sortedCategories = remember {
                                mutableStateOf(getSortedAppsByCategory(context, dbHelper))
                            }
                            val clipboardManager =
                                LocalContext.current.getSystemService(Context.CLIPBOARD_SERVICE)
                                    as ClipboardManager

                            // Extract Uncategorized and other categories
                            val uncategorizedPair =
                                sortedCategories.value.find { it.first.name == "Uncategorized" }
                            val otherCategories =
                                sortedCategories.value.filter { it.first.name != "Uncategorized" }

                            // For all other categories, display each in its own CookieCard with a
                            // horizontally scrollable LazyHorizontalGrid
                            otherCategories.forEach { (category, apps) ->
                                CookieCard {
                                    /*
                                    Text(
                                        text = "Category: ${category.name}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(8.dp),
                                    */
                                    val rowsInt = 2
                                    LazyHorizontalGrid(
                                        rows = GridCells.Fixed(rowsInt),
                                        contentPadding = PaddingValues(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.height((16+(48*rowsInt)+(8*rowsInt-1)).dp).fillMaxWidth(),
                                    ) {
                                        items(apps, key = { it.activityInfo.packageName }) {
                                            resolveInfo ->
                                            AppIcon(
                                                resolveInfo = resolveInfo,
                                                onClick = {
                                                    val categoryId =
                                                        dbHelper.getCategoryId(category.name)
                                                    dbHelper.incrementAppLaunchCount(
                                                        dbHelper,
                                                        categoryId,
                                                        resolveInfo.activityInfo.packageName,
                                                    )
                                                    sortedCategories.value =
                                                        getSortedAppsByCategory(context, dbHelper)
                                                },
                                                modifier = Modifier.animateItem(),
                                            )
                                        }
                                    }
                                }
                            }
                            // If there's an Uncategorized category, display it in a LazyColumn
                            uncategorizedPair?.let { (category, apps) ->
                                LazyColumn(modifier= Modifier.height(500.dp)) {
                                    item {
                                        Text(
                                            text = "Category: ${category.name}",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(8.dp),
                                        )
                                    }
                                    items(apps, key = { it.activityInfo.packageName }) { resolveInfo
                                        ->
                                        Row(
                                            modifier =
                                                Modifier.fillMaxWidth().padding(2.dp).clickable {
                                                    // Copy package name to clipboard on row click
                                                    val clipData =
                                                        ClipData.newPlainText(
                                                            "Package Name",
                                                            resolveInfo.activityInfo.packageName,
                                                        )
                                                    clipboardManager.setPrimaryClip(clipData)
                                                },
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            AppIcon(
                                                resolveInfo = resolveInfo,
                                                onClick = {
                                                    val categoryId =
                                                        dbHelper.getCategoryId(category.name)
                                                    dbHelper.incrementAppLaunchCount(
                                                        dbHelper,
                                                        categoryId,
                                                        resolveInfo.activityInfo.packageName,
                                                    )
                                                    sortedCategories.value =
                                                        getSortedAppsByCategory(context, dbHelper)
                                                },
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            val name by remember{
                                            mutableStateOf(
                                            resolveInfo
                                                            .loadLabel(context.packageManager)
                                                            .toString())}
                                            Column {
                                                Text(
                                                    text = name
                                                        ,
                                                    fontSize = 16.sp,
                                                )
                                                Text(
                                                    text = resolveInfo.activityInfo.packageName,
                                                    fontSize = 12.sp,
                                                    color = Color.Gray,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        DebugAppList(context, dbHelper)
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun CookieCard(
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CircularProgressIndicator()
            Text(text = "Loading...")
        }
    },
) {
    Surface(
        color = cookieForeground.copy(0.7f),
        contentColor = contentcol,
        modifier =
            Modifier.clip(RoundedCornerShape(16.dp))
                .clickable { onClick() }
                .border(
                    width = 8.dp,
                    shape = RoundedCornerShape(16.0.dp),
                    color = cookieForeground.copy(alpha = 0.5f),
                )
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp)),
    ) {
        Column(modifier = Modifier.clip(RoundedCornerShape(8.dp))) { content() }
    }
}

@Composable
fun AppIcon(resolveInfo: ResolveInfo, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val packageName = remember { resolveInfo.activityInfo.packageName }
    val intent = remember { context.packageManager.getLaunchIntentForPackage(packageName) }
    val icon = remember { mutableStateOf(resolveInfo.loadIcon(context.packageManager)) }
  val scope = CoroutineScope(Dispatchers.Main)
    AndroidView(
        factory = { ctx ->
            ImageView(ctx).apply {
                setImageDrawable(icon.value)
                setOnClickListener {
                    
                    this.post {
                        val centerX = this.width / 2
                        val centerY = this.height / 2
                        val options =
                            ActivityOptions.makeScaleUpAnimation(this, centerX, centerY, 0, 0)
                        ctx.startActivity(intent, options.toBundle())
                    }
                    scope.launch{onClick()}
                }
            }
        },
        modifier = modifier.size(48.dp),
    )
}

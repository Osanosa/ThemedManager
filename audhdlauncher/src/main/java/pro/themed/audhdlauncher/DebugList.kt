package pro.themed.audhdlauncher

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.pm.ResolveInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pro.themed.audhdlauncher.database.CategoryData

@Composable
internal fun DebugList(category: CategoryData, apps: List<ResolveInfo>, context: Context) {
    val clipboardManager =
        LocalContext.current.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    CookieCard(alpha = 1f,) {
        LazyColumn {
            item {
                Text(
                    text = "${category.name}: ${apps.size}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp),
                )
            }
            items(apps, key = { it.activityInfo.packageName }) { resolveInfo ->
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
                    AppIcon(resolveInfo = resolveInfo, onClick = {})

                    Spacer(modifier = Modifier.width(8.dp))
                    val name by remember {
                        mutableStateOf(resolveInfo.loadLabel(context.packageManager).toString())
                    }
                    Column {
                        Text(text = name, fontSize = 16.sp)
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

package pro.themed.audhdlauncher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import pro.themed.audhdlauncher.database.AppDataStoreRepository
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InfoDialog(
    resolveInfo: ResolveInfo,
    showpopup: Boolean,
    onDismiss: () -> Unit,
    context: Context,
    categoryName: String = "",
) {
    AnimatedVisibility(showpopup) {
        Dialog({ onDismiss() }, DialogProperties(usePlatformDefaultWidth = true)) {
            CookieCard(alpha = 0.9f,) {
                Column(
                    modifier = Modifier.Companion.fillMaxWidth().padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Row(
                        modifier = Modifier.Companion.fillMaxWidth().padding(2.dp),
                        verticalAlignment = Alignment.Companion.CenterVertically,
                    ) {
                        AppIcon(resolveInfo = resolveInfo, onClick = {})

                        Spacer(modifier = Modifier.Companion.width(8.dp))
                        val name by remember {
                            mutableStateOf(resolveInfo.loadLabel(context.packageManager).toString())
                        }
                        Column(Modifier.Companion.fillMaxWidth().weight(1f)) {
                            Text(text = name, fontSize = 16.sp)
                            Text(
                                text = resolveInfo.activityInfo.packageName,
                                fontSize = 12.sp,
                                color = Color.Companion.Gray,
                            )
                        }

                        Column(modifier = Modifier.Companion.width(24.dp)) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                modifier =
                                    Modifier.Companion.size(24.dp).clip(CircleShape).clickable {
                                        val intent = Intent()
                                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                        intent.data =
                                            Uri.fromParts(
                                                "package",
                                                resolveInfo.activityInfo.packageName,
                                                null,
                                            )
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(intent)
                                    },
                            )
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null,
                                modifier =
                                    Modifier.Companion.size(24.dp).clip(CircleShape).clickable {
                                        val intent = Intent(Intent.ACTION_DELETE)
                                        intent.setData(
                                            Uri.fromParts(
                                                "package",
                                                resolveInfo.activityInfo.packageName,
                                                null,
                                            )
                                        )
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(intent)
                                    },
                            )
                        }
                    }

                    val versionName by remember {
                        mutableStateOf(
                            try {
                                context.packageManager
                                    .getPackageInfo(resolveInfo.activityInfo.packageName, 0)
                                    .versionName
                            } catch (e: PackageManager.NameNotFoundException) {
                                ""
                            }
                        )
                    }
                    Text(
                        text = "Version: $versionName",
                        fontSize = 12.sp,
                        color = Color.Companion.Gray,
                    )
                    val lastUpdateTime by remember {
                        mutableStateOf(
                            try {
                                context.packageManager
                                    .getPackageInfo(resolveInfo.activityInfo.packageName, 0)
                                    .lastUpdateTime
                            } catch (e: PackageManager.NameNotFoundException) {
                                0
                            }
                        )
                    }
                    Text(
                        text =
                            "Last Update: ${
                                SimpleDateFormat(
                                    "HH:mm dd MMM yyyy", Locale.getDefault(),
                                ).format(Date(lastUpdateTime))
                            }",
                        fontSize = 12.sp,
                        color = Color.Companion.Gray,
                    )
                    val packageSignatures by remember {
                        mutableStateOf(
                            try {
                                context.packageManager
                                    .getPackageInfo(
                                        resolveInfo.activityInfo.packageName,
                                        PackageManager.GET_SIGNATURES,
                                    )
                                    .signatures
                            } catch (e: PackageManager.NameNotFoundException) {
                                emptyArray()
                            }
                        )
                    }
                    val sha1 =
                        if (packageSignatures?.isNotEmpty() == true) {
                            MessageDigest.getInstance("SHA")
                                .digest(packageSignatures!![0].toByteArray())
                                .joinToString("") { "%02x".format(it) }
                        } else {
                            ""
                        }
                    Text(
                        text = "Signing SHA1: $sha1",
                        fontSize = 12.sp,
                        color = Color.Companion.Gray,
                    )

                    // Launch count display and clear functionality
                    if (categoryName.isNotEmpty()) {
                        val scope = rememberCoroutineScope()
                        var launchCount by remember { mutableStateOf(0) }
                        val repository: AppDataStoreRepository = koinInject()

                        // Load launch count for this app in this category using injected repository
                        LaunchedEffect(resolveInfo.activityInfo.packageName, categoryName) {
                            repository.getAllLaunchCounts().collect { launchCounts ->
                                launchCount =
                                    launchCounts["${resolveInfo.activityInfo.packageName}_$categoryName"] ?: 0
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Launch count: $launchCount",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = "Clear",
                                modifier = Modifier
                                    .height(20.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        scope.launch {
                                            repository.clearAppLaunchCount(
                                                categoryName,
                                                resolveInfo.activityInfo.packageName
                                            )
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

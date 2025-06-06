package pro.themed.audhdlauncher

import android.content.Context
import android.content.pm.ResolveInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import pro.themed.audhdlauncher.database.CategoryData
import pro.themed.audhdlauncher.database.LauncherViewModel

@Composable
fun CategoryCard(category: CategoryData, apps: List<ResolveInfo>, context: Context) {
    var showdebugpopup by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    val viewModel: LauncherViewModel = viewModel()
    
    // Local state for the slider
    var sliderPosition by remember(category.rows) { 
        mutableStateOf(category.rows.toFloat() - 1) // Convert to 0-4 range for slider
    }
    
    // Show settings dialog on long click
    LaunchedEffect(showSettingsDialog) {
        if (showSettingsDialog) {
            // Reset slider position when dialog is shown
            sliderPosition = category.rows.toFloat() - 1
        }
    }
    
    CookieCard(
        modifier =
            Modifier.combinedClickable(
                onClick = {},
                onLongClick = { showSettingsDialog = true }
            )
    ) {
        val rowsInt = category.rows
        if (true) {
            Row(
                modifier =
                    Modifier.horizontalScroll(rememberScrollState())
                        .height((16 + (48 * rowsInt) + (8 * (rowsInt - 1))).dp)
                        .fillMaxWidth()
                        .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                // Split apps into columns of 2 items each
                apps.chunked(category.rows).forEach { columnApps ->
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                        // Add debug button for first column if uncategorized
//                        if (
//                            category.name == "Uncategorized" &&
//                                columnApps == apps.chunked(category.rows).first()
//                        ) {
//                            IconButton(
//                                onClick = { showdebugpopup = true },
//                                colors =
//                                    IconButtonDefaults.iconButtonColors(
//                                        contentColor = Red,
//                                        containerColor = White,
//                                    ),
//                            ) {
//                                Icon(Icons.Default.Info, "Debug")
//                            }
//                            AnimatedVisibility(showdebugpopup) {
//                                Dialog({ showdebugpopup = false }) {
//                                    DebugList(category, apps, context)
//                                }
//                            }
//                        }

                        // Add the actual app icons
                        columnApps.forEach { resolveInfo ->
                            LaunchIcon(resolveInfo, category.name, context, Modifier)
                        }
                    }
                }
            }
        } else {
            LazyHorizontalGrid(
                rows = GridCells.Fixed(rowsInt),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier =
                    Modifier.Companion.height((16 + (48 * rowsInt) + (8 * rowsInt - 1)).dp)
                        .fillMaxWidth(),
            ) {
                if (category.name == "Uncategorized")
                    item {
                        IconButton(
                            onClick = { showdebugpopup = true },
                            colors =
                                IconButtonDefaults.iconButtonColors(
                                    contentColor = Red,
                                    containerColor = White,
                                ),
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = "Debug")
                        }
                        AnimatedVisibility(showdebugpopup) {
                            Dialog({ showdebugpopup = false }) {
                                DebugList(category, apps, context)
                            }
                        }
                    }
                items(apps, key = { it.activityInfo.packageName }) { resolveInfo ->
                    Box(
                        modifier =
                            Modifier.animateItem(
                                fadeInSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                placementSpec =
                                    spring(
                                        stiffness = Spring.StiffnessVeryLow,
                                        visibilityThreshold = IntOffset(1, 1),
                                    ),
                                fadeOutSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            )
                    ) {
                        LaunchIcon(resolveInfo, category.name, context, Modifier)
                    }
                }
            }
        }
    }
    
    // Category Settings Dialog
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { 
                Text(
                    "${category.name} Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ) 
            },
            text = {
                Column {
                    Text("Number of Rows: ${(sliderPosition + 1).toInt()}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = sliderPosition,
                        onValueChange = { sliderPosition = it },
                        valueRange = 0f..4f, // 1-5 rows (0-4 for 0-based)
                        steps = 3, // Steps at 1, 2, 3, 4, 5
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    // Add more settings here as needed
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newRows = (sliderPosition + 1).toInt()
                        if (newRows != category.rows) {
                            viewModel.updateCategory(category.copy(rows = newRows))
                        }
                        showSettingsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6200EE)
                    )
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSettingsDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

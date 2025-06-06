package pro.themed.manager.utils

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.MainActivity
import pro.themed.manager.MainActivity.Companion.isDark
import pro.themed.manager.R.drawable
import pro.themed.manager.comps.AboutPage
import pro.themed.manager.comps.AccentsAAPT
import pro.themed.manager.comps.FabricatedMonet
import pro.themed.manager.comps.FontsTab
import pro.themed.manager.comps.IconsTab
import pro.themed.manager.comps.MiscTab
import pro.themed.manager.comps.QsPanel
import pro.themed.manager.comps.SettingsPage
import pro.themed.manager.comps.ToolboxPage
import pro.themed.manager.ui.theme.contentcol

@ExperimentalMaterial3Api
@Composable
fun Navigation(navController: NavHostController) {
    val routes = listOf(
        NavigationItems.About.route,
        NavigationItems.Settings.route,
        NavigationItems.Toolbox.route,
        NavigationItems.ColorsTab.route,
        NavigationItems.QsPanel.route,
        NavigationItems.FontsTab.route,
        NavigationItems.IconsTab.route,
        NavigationItems.MiscTab.route,
    )

    NavHost(navController, startDestination = NavigationItems.ColorsTab.route) {
        routes.forEachIndexed { index, route ->
            composable(
                route,
                enterTransition = {
                    if (index > routes.indexOf(navController.previousBackStackEntry?.destination?.route)) {
                        slideInVertically(initialOffsetY = { it / 2 }) + fadeIn()
                    } else {
                        slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn()
                    }
                },
                exitTransition = {
                    if (index < routes.indexOf(navController.currentBackStackEntry?.destination?.route)) {
                        slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut()
                    } else {
                        slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
                    }
                },
                popEnterTransition = {
                    if (index < routes.indexOf(navController.currentBackStackEntry?.destination?.route)) {
                        slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn()
                    } else {
                        slideInVertically(initialOffsetY = { it / 2 }) + fadeIn()
                    }
                },
                popExitTransition = {
                    if (index > routes.indexOf(navController.previousBackStackEntry?.destination?.route)) {
                        slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
                    } else {
                        slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut()
                    }
                },
            ) {
                when (route) {
                    NavigationItems.About.route -> AboutPage()
                    NavigationItems.Settings.route -> SettingsPage()
                    NavigationItems.Toolbox.route -> ToolboxPage()
                    else -> getComposableForRoute(route)()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun getComposableForRoute(route: String): @Composable () -> Unit {
    val overlays = MainActivity.overlayList.overlayList
    val moduleTemp by remember { mutableStateOf(Shell.SH.run("su -c ls /data/adb/modules_update").output()) }
    return when {
        moduleTemp.contains("ThemedProject") && overlays.isEmpty() -> {
            {
                Box(
                    contentAlignment = androidx.compose.ui.Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                    //    .padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = drawable.goodboy),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Column {

                        OutlinedButton(
                            onClick = { Shell("su").run("reboot") },

                            ) {
                            Text(
                                textAlign = TextAlign.Center,
                                text = "Now reboot",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    shadow = Shadow(
                                        color = Color.Black,
                                        offset = Offset(4f, 4f),
                                        blurRadius = 8f,
                                    ),
                                ),
                            )
                        }
                    }
                }

            }
        }

        overlays.isEmpty() -> {
            {
                Box(
                    contentAlignment = androidx.compose.ui.Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                    //    .padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = drawable.ohnocringememe),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Text(
                        textAlign = TextAlign.Center,
                        text = "Themed overlays\nare missing\n\n\n\nTry installing module from about screen",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(4f, 4f),
                                blurRadius = 8f,
                            ),
                        ),
                    )
                }
            }
        }

        else -> {
            when (route) {
                NavigationItems.ColorsTab.route -> {
                    {
                        val scroll = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .verticalScroll(scroll)
                                .padding(horizontal = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .background(contentcol.copy(alpha = 0.05f), shape = CircleShape)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDark == "") contentcol.copy(alpha = 0.05f)
                                            else Color.Transparent,
                                            shape = CircleShape,
                                        )
                                        .clickable { isDark = "" }
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Icon(
                                        painter = painterResource(id = drawable.light_mode_24px),
                                        contentDescription = "Light",
                                        tint = contentcol,
                                    )
                                    Text(text = "Light")
                                }

                                Icon(
                                    painter = painterResource(id = drawable.developer_mode_24px),
                                    contentDescription = null,
                                    modifier = Modifier.padding(16.dp),
                                    tint = contentcol,
                                )

                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDark == "-night") contentcol.copy(alpha = 0.05f)
                                            else Color.Transparent
                                        )
                                        .clickable { isDark = "-night" }
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Icon(
                                        painter = painterResource(id = drawable.dark_mode_24px),
                                        contentDescription = "Dark",
                                        tint = contentcol,
                                    )
                                    Text(text = "Dark")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            AccentsAAPT()
                            Spacer(modifier = Modifier.height(8.dp))
                            FabricatedMonet(scroll)
                            Spacer(modifier = Modifier.height(8.dp))
DebugSystemColors()
                        }
                    }
                }

                NavigationItems.QsPanel.route -> {
                    { QsPanel() }
                }

                NavigationItems.FontsTab.route -> {
                    { FontsTab() }
                }

                NavigationItems.IconsTab.route -> {
                    { IconsTab() }
                }

                NavigationItems.MiscTab.route -> {
                    { MiscTab() }
                }

                else -> {
                    {}
                }
            }
        }
    }
}


@Composable
fun DebugSystemColors() {
    val context = MainActivity.appContext
    val colorNames = listOf(
        // Light theme colors
        "system_primary_container_light",
        "system_on_primary_container_light",
        "system_primary_light",
        "system_on_primary_light",
        "system_secondary_container_light",
        "system_on_secondary_container_light",
        "system_secondary_light",
        "system_on_secondary_light",
        "system_tertiary_container_light",
        "system_on_tertiary_container_light",
        "system_tertiary_light",
        "system_on_tertiary_light",
        "system_background_light",
        "system_on_background_light",
        "system_surface_light",
        "system_on_surface_light",
        "system_surface_container_low_light",
        "system_surface_container_lowest_light",
        "system_surface_container_light",
        "system_surface_container_high_light",
        "system_surface_container_highest_light",
        "system_surface_bright_light",
        "system_surface_dim_light",
        "system_surface_variant_light",
        "system_on_surface_variant_light",
        "system_outline_light",
        "system_outline_variant_light",
        "system_error_light",
        "system_on_error_light",
        "system_error_container_light",
        "system_on_error_container_light",
        "system_control_activated_light",
        "system_control_normal_light",
        "system_control_highlight_light",
        "system_text_primary_inverse_light",
        "system_text_secondary_and_tertiary_inverse_light",
        "system_text_primary_inverse_disable_only_light",
        "system_text_secondary_and_tertiary_inverse_disabled_light",
        "system_text_hint_inverse_light",
        "system_palette_key_color_primary_light",
        "system_palette_key_color_secondary_light",
        "system_palette_key_color_tertiary_light",
        "system_palette_key_color_neutral_light",
        "system_palette_key_color_neutral_variant_light",

        // Dark theme colors
        "system_primary_container_dark",
        "system_on_primary_container_dark",
        "system_primary_dark",
        "system_on_primary_dark",
        "system_secondary_container_dark",
        "system_on_secondary_container_dark",
        "system_secondary_dark",
        "system_on_secondary_dark",
        "system_tertiary_container_dark",
        "system_on_tertiary_container_dark",
        "system_tertiary_dark",
        "system_on_tertiary_dark",
        "system_background_dark",
        "system_on_background_dark",
        "system_surface_dark",
        "system_on_surface_dark",
        "system_surface_container_low_dark",
        "system_surface_container_lowest_dark",
        "system_surface_container_dark",
        "system_surface_container_high_dark",
        "system_surface_container_highest_dark",
        "system_surface_bright_dark",
        "system_surface_dim_dark",
        "system_surface_variant_dark",
        "system_on_surface_variant_dark",
        "system_outline_dark",
        "system_outline_variant_dark",
        "system_error_dark",
        "system_on_error_dark",
        "system_error_container_dark",
        "system_on_error_container_dark",
        "system_control_activated_dark",
        "system_control_normal_dark",
        "system_control_highlight_dark",
        "system_text_primary_inverse_dark",
        "system_text_secondary_and_tertiary_inverse_dark",
        "system_text_primary_inverse_disable_only_dark",
        "system_text_secondary_and_tertiary_inverse_disabled_dark",
        "system_text_hint_inverse_dark",
        "system_palette_key_color_primary_dark",
        "system_palette_key_color_secondary_dark",
        "system_palette_key_color_tertiary_dark",
        "system_palette_key_color_neutral_dark",
        "system_palette_key_color_neutral_variant_dark",

        // Fixed colors
        "system_primary_fixed",
        "system_primary_fixed_dim",
        "system_on_primary_fixed",
        "system_on_primary_fixed_variant",
        "system_secondary_fixed",
        "system_secondary_fixed_dim",
        "system_on_secondary_fixed",
        "system_on_secondary_fixed_variant",
        "system_tertiary_fixed",
        "system_tertiary_fixed_dim",
        "system_on_tertiary_fixed",
        "system_on_tertiary_fixed_variant"
    )

    LazyColumn(
        modifier = Modifier.height(700.dp)
    ) {
        items (colorNames) { colorName ->
            val colorResId = context.resources.getIdentifier(colorName, "color", "android")
            val color = if (colorResId != 0) {
                Color(context.getColor(colorResId))
            } else {
                Color.Red // Fallback for missing colors
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color)
                    .padding(4.dp),
            ) {
                Text(
                    text = colorName,
                    color = getContrastColor(color),

                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "#%08x".format(color.toArgb()), //color as hex
                    color = getContrastColor(color),
                )
            }
        }
    }
}

private fun getContrastColor(color: Color): Color {
    val red = color.red * 255
    val green = color.green * 255
    val blue = color.blue * 255
    val brightness = (red * 299 + green * 587 + blue * 114) / 1000f
    return if (brightness > 128) Color.Black else Color.White
}
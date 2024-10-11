package pro.themed.manager.utils

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import androidx.navigation.compose.*
import pro.themed.manager.*
import pro.themed.manager.MainActivity.Companion.isDark
import pro.themed.manager.R.*
import pro.themed.manager.comps.*
import pro.themed.manager.ui.theme.*

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
        NavigationItems.MiscTab.route
    )

    NavHost(
        navController, startDestination = NavigationItems.ColorsTab.route
    ) {
        routes.forEachIndexed { index, route ->

            composable(route, enterTransition = {
                if (index > routes.indexOf(navController.previousBackStackEntry?.destination?.route)) {
                    slideInVertically(initialOffsetY = { it / 2 }) + fadeIn()
                } else {
                    slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn()
                }

            }, exitTransition = {
                if (index < routes.indexOf(navController.currentBackStackEntry?.destination?.route)) {
                    slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut()
                } else {
                    slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
                }
            }, popEnterTransition = {
                if (index < routes.indexOf(navController.currentBackStackEntry?.destination?.route)) {
                    slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn()
                } else {
                    slideInVertically(initialOffsetY = { it / 2 }) + fadeIn()
                }
            }, popExitTransition = {
                if (index > routes.indexOf(navController.previousBackStackEntry?.destination?.route)) {
                    slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
                } else {
                    slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut()
                }
            }) {
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
    val overlays = MainActivity.overlayList.overlayList.isEmpty()
    return if (overlays) {
        {
            Box(
                contentAlignment = androidx.compose.ui.Alignment.Center,
                modifier = Modifier.fillMaxSize()
                //    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = drawable.ohnocringememe),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    textAlign = TextAlign.Center,
                    text = "Themed overlays are missing\n\n\n\nTry installing module from about screen",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        shadow = Shadow(
                            color = Color.Black, offset = Offset(4f, 4f), blurRadius = 8f
                        )
                    )
                )
            }
        }
    } else {
        when (route) {
            NavigationItems.ColorsTab.route -> {
                {
                    val scroll = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .verticalScroll(scroll)
                            .padding(horizontal = 8.dp)
                    ) {
                        Spacer(Modifier.height(32.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .background(contentcol.copy(alpha = 0.05f), shape = CircleShape)
                        ) {
                            Row(modifier = Modifier
                                .weight(1f)
                                .clip(CircleShape)
                                .background(
                                    if (isDark == "") contentcol.copy(alpha = 0.05f) else Color.Transparent,
                                    shape = CircleShape
                                )

                                .clickable { isDark = "" }
                                .padding(16.dp),
                                horizontalArrangement = Arrangement.Center) {
                                Icon(
                                    painter = painterResource(id = drawable.light_mode_24px),
                                    contentDescription = "Light",
                                    tint = contentcol
                                )
                                Text(text = "Light")
                            }

                            Icon(
                                painter = painterResource(id = drawable.developer_mode_24px),
                                contentDescription = null,
                                modifier = Modifier.padding(16.dp),
                                tint = contentcol
                            )

                            Row(modifier = Modifier
                                .weight(1f)
                                .clip(CircleShape)
                                .background(if (isDark == "-night") contentcol.copy(alpha = 0.05f) else Color.Transparent)
                                .clickable { isDark = "-night" }

                                .padding(16.dp),
                                horizontalArrangement = Arrangement.Center) {
                                Icon(
                                    painter = painterResource(id = drawable.dark_mode_24px),
                                    contentDescription = "Dark",
                                    tint = contentcol
                                )
                                Text(text = "Dark")
                            }

                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        AccentsAAPT()
                        Spacer(modifier = Modifier.height(8.dp))
                        FabricatedMonet(scroll)
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
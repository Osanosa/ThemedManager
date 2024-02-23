package pro.themed.manager.utils

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pro.themed.manager.MainActivity
import pro.themed.manager.MainActivity.Companion.isDark
import pro.themed.manager.R
import pro.themed.manager.comps.AboutPage
import pro.themed.manager.comps.AccentsAAPT
import pro.themed.manager.comps.FabricatedMonet
import pro.themed.manager.comps.FontsTab
import pro.themed.manager.comps.IconsTab
import pro.themed.manager.comps.MiscTab
import pro.themed.manager.comps.QsPanel
import pro.themed.manager.comps.SettingsPage
import pro.themed.manager.comps.ToolboxPage
import pro.themed.manager.ui.theme.textcol

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
                    painter = painterResource(id = R.drawable.ohnocringememe),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    textAlign = TextAlign.Center,
                    text = "Themed overlays are missing\n\n\n\nTry installing module from about screen",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(
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
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            val isDarkState = remember { mutableStateOf(false) }
                            val backgroundColor by animateColorAsState(
                                targetValue = if (isDarkState.value) textcol.copy(alpha = 0.1f) else Color.Transparent
                            )
                            Row(modifier = Modifier
                                .weight(1f)
                                .clip(CircleShape)
                                .background(if (isDark == "") textcol.copy(alpha = 0.1f) else Color.Transparent)


                                .clickable { isDark = "" }
                                .padding(16.dp),
                                horizontalArrangement = Arrangement.Center) {
                                Icon(
                                    painter = painterResource(id = R.drawable.light_mode_24px),
                                    contentDescription = "Light",
                                    tint = textcol
                                )
                                Text(text = "Light")
                            }

                            Icon(
                                painter = painterResource(id = R.drawable.developer_mode_24px),
                                contentDescription = null,
                                modifier = Modifier.padding(16.dp),
                                tint = textcol
                            )


                            Row(modifier = Modifier
                                .weight(1f)
                                .clip(CircleShape)
                                .background(if (isDark == "-night") textcol.copy(alpha = 0.1f) else Color.Transparent)
                                .clickable { isDark = "-night" }

                                .padding(16.dp),
                                horizontalArrangement = Arrangement.Center) {
                                Icon(
                                    painter = painterResource(id = R.drawable.dark_mode_24px),
                                    contentDescription = "Dark",
                                    tint = textcol
                                )
                                Text(text = "Dark")
                            }

                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        AccentsAAPT()
                        Spacer(modifier = Modifier.height(8.dp))
                        FabricatedMonet()
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
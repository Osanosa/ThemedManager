package pro.themed.manager.utils

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pro.themed.manager.comps.AboutPage
import pro.themed.manager.comps.FabricatedMonet
import pro.themed.manager.comps.FontsTab
import pro.themed.manager.comps.IconsTab
import pro.themed.manager.comps.MiscTab
import pro.themed.manager.comps.QsPanel
import pro.themed.manager.comps.SettingsPage
import pro.themed.manager.comps.ToolboxPage
import pro.themed.manager.getOverlayList

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
        navController,
        startDestination = NavigationItems.ColorsTab.route
    ) {
        routes.forEachIndexed { index, route ->

            composable(
                route,
                enterTransition = {
                    if (index > routes.indexOf(navController.previousBackStackEntry?.destination?.route)) {
                        slideInVertically(initialOffsetY = { it/2 }) + fadeIn()
                    } else {
                        slideInVertically(initialOffsetY  = { -it/2 }) + fadeIn()
                    }

                },
                exitTransition = {
                    if (index < routes.indexOf(navController.currentBackStackEntry?.destination?.route)) {
                        slideOutVertically(targetOffsetY  = { -it/2 }) + fadeOut()
                    } else {
                        slideOutVertically(targetOffsetY = { it/2 }) + fadeOut()
                    }
                }
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
    return if (getOverlayList().overlayList.isEmpty()) {
        {
            Text(
                textAlign = TextAlign.Center,
                text = "Themed overlays are missing\nTry installing module from about screen"
            )
        }
    } else {
        when (route) {
            NavigationItems.ColorsTab.route -> { { FabricatedMonet() } }
            NavigationItems.QsPanel.route -> { { QsPanel() } }
            NavigationItems.FontsTab.route -> { { FontsTab() } }
            NavigationItems.IconsTab.route -> { { IconsTab() } }
            NavigationItems.MiscTab.route -> { { MiscTab() } }
            else -> { {} }
        }
    }
}
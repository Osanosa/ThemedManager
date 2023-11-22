package pro.themed.manager.utils

import android.util.Log
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
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
            Log.d("NavHost", "Index: $index, Route: $route")

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
                    NavigationItems.ColorsTab.route -> FabricatedMonet()
                    NavigationItems.QsPanel.route -> QsPanel()
                    NavigationItems.FontsTab.route -> FontsTab()
                    NavigationItems.IconsTab.route -> IconsTab()
                    NavigationItems.MiscTab.route -> MiscTab()
                }
            }
        }
    }
}
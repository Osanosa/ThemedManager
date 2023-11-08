package pro.themed.manager.utils

import androidx.compose.animation.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.*
import pro.themed.manager.comps.*

@ExperimentalMaterial3Api
@Composable
fun Navigation(navController: NavHostController) {
    val routes = listOf(
        NavigationItems.About.route,
        NavigationItems.Settings.route,
        NavigationItems.Toolbox.route,
        NavigationItems.ColorsTab.route,
        NavigationItems.IconsTab.route,
        NavigationItems.FontsTab.route,
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
                    NavigationItems.ColorsTab.route -> FabricatedMonet()
                    NavigationItems.IconsTab.route -> IconsTab()
                    NavigationItems.FontsTab.route -> FontsTab()
                    NavigationItems.MiscTab.route -> MiscTab()
                }
            }
        }
    }
}
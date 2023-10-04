package pro.themed.manager.utils

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.*
import pro.themed.manager.comps.*

@ExperimentalMaterial3Api
@Composable
fun Navigation(navController: NavHostController) {

    NavHost(navController, startDestination = NavigationItems.ColorsTab.route) {

        composable(NavigationItems.ColorsTab.route) {
            FabricatedMonet()
        }
        composable(NavigationItems.IconsTab.route) {
            IconsTab()
        }
        /*composable(NavigationItems.FontsTab.route) {
            AppsTab()
        }*/
        composable(NavigationItems.MiscTab.route) {
            MiscTab()
        }

        composable(NavigationItems.About.route) {
            AboutPage()
        }
        composable(NavigationItems.Settings.route) {
            SettingsPage()
        }
        composable(NavigationItems.Toolbox.route) {
            ToolboxPage()
        }

    }

}
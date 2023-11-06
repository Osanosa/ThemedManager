package pro.themed.manager.utils

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.*
import androidx.navigation.*
import androidx.navigation.compose.*
import pro.themed.manager.*
import pro.themed.manager.comps.*

@ExperimentalMaterial3Api
@Composable
fun Navigation(navController: NavHostController) {

    NavHost(navController, startDestination = NavigationItems.ColorsTab.route) {

        composable(NavigationItems.ColorsTab.route) {
            if (getOverlayList().overlayList.isEmpty()) {
                androidx.compose.material.Text(
                    textAlign = TextAlign.Center,
                    text = "Themed overlays are missing\nTry installing module from about screen"
                )
            } else {
            FabricatedMonet()}
        }
        composable(NavigationItems.IconsTab.route) {
            if (getOverlayList().overlayList.isEmpty()) {
                androidx.compose.material.Text(
                    textAlign = TextAlign.Center,
                    text = "Themed overlays are missing\nTry installing module from about screen"
                )
            } else {
            IconsTab()}
        }
        /*composable(NavigationItems.FontsTab.route) {
            AppsTab()
        }*/
        composable(NavigationItems.MiscTab.route) {
            if (getOverlayList().overlayList.isEmpty()) {
                androidx.compose.material.Text(
                    textAlign = TextAlign.Center,
                    text = "Themed overlays are missing\nTry installing module from about screen"
                )
            } else {
            MiscTab()}
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
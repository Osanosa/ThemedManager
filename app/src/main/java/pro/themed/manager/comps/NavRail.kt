package pro.themed.manager.comps

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import androidx.navigation.compose.*
import pro.themed.manager.ui.theme.*
import pro.themed.manager.utils.*

@Composable
fun NavigationRailSample(
    navController: NavController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val itemsbottom = listOf(
        NavigationItems.ColorsTab, NavigationItems.IconsTab,
        // NavigationItems.FontsTab,
        NavigationItems.MiscTab
    )
    val itemstop = listOf(
        NavigationItems.About, NavigationItems.Settings, NavigationItems.Toolbox
    )

    androidx.compose.material.NavigationRail(elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.cardcol,
        contentColor = MaterialTheme.colors.textcol,
        modifier = Modifier.width(64.dp),

    ) {



        Column(
            modifier = Modifier
                , verticalArrangement = Arrangement.Top
        ) {
            itemstop.forEachIndexed { _, item ->
                val isSelected = currentRoute == item.route
                androidx.compose.material.NavigationRailItem(
                    selectedContentColor = MaterialTheme.colors.textcol,
                    unselectedContentColor = MaterialTheme.colors.textcol.copy(0.4f),
                    icon = {
                        Icon(
                            painterResource(id = item.icon),
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(item.title) },
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(item.route) {
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route = route) {
                                        saveState = true
                                    }
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
          //  Spacer(Modifier.height(64.dp))
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
               , verticalArrangement = Arrangement.Bottom
        ) {
            itemsbottom.forEachIndexed { _, item ->
                val isSelected = currentRoute == item.route
                androidx.compose.material.NavigationRailItem(
                    selectedContentColor = MaterialTheme.colors.textcol,
                    unselectedContentColor = MaterialTheme.colors.textcol.copy(0.4f),
                    icon = {
                        Icon(
                            painterResource(id = item.icon),
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(item.title) },
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(item.route) {
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route = route) {
                                        saveState = true
                                    }
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
          //  Spacer(Modifier.height(64.dp))
        }
    }
}

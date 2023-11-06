package pro.themed.manager.comps

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.layout.*
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
    var topColumnHeight by remember { mutableIntStateOf(0) }
    var bottomColumnHeight by remember { mutableIntStateOf(0) }
    var railHeight by remember { mutableIntStateOf(0) }
    var contentExceedsAvailableSpace by remember { mutableStateOf(false) }
    contentExceedsAvailableSpace = topColumnHeight /itemstop.size*(itemstop.size+ itemsbottom.size)>railHeight



    NavigationRail(
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.cardcol,
        contentColor = MaterialTheme.colors.textcol,
        modifier = Modifier
            .width(64.dp)
            .onGloballyPositioned { layoutInfo ->
                railHeight = layoutInfo.size.height
            }
            .then(if (contentExceedsAvailableSpace) Modifier.verticalScroll(rememberScrollState()) else Modifier)


    ) {
            Column(modifier = Modifier.onGloballyPositioned { layoutInfo ->
                topColumnHeight = layoutInfo.size.height
            }, verticalArrangement = Arrangement.Top) {
                itemstop.forEachIndexed { _, item ->
                    val isSelected = currentRoute == item.route
                    NavigationRailItem(selectedContentColor = MaterialTheme.colors.textcol,
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
                        })
                }
            }
        Spacer(modifier = Modifier.weight(1f))
            Column( modifier = Modifier
                .wrapContentHeight()
                .onGloballyPositioned { layoutInfo ->
                    bottomColumnHeight = layoutInfo.size.height
                }) {
                itemsbottom.forEachIndexed { _, item ->
                    val isSelected = currentRoute == item.route
                    NavigationRailItem(selectedContentColor = MaterialTheme.colors.textcol,
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
                        })
                }
            }
        }
    }

package pro.themed.manager.comps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import pro.themed.manager.ui.theme.bordercol
import pro.themed.manager.ui.theme.textcol
import pro.themed.manager.utils.NavigationItems

@Stable
@Composable
fun NavigationRailSample(
    navController: NavController,
) {
    val itemsbottom = listOf(
        NavigationItems.ColorsTab,
        NavigationItems.QsPanel,
        NavigationItems.FontsTab,
        NavigationItems.IconsTab,
        NavigationItems.MiscTab
    )
    val itemstop = listOf(
        NavigationItems.About, NavigationItems.Settings, NavigationItems.Toolbox
    )

    var topColumnHeight by rememberSaveable { mutableIntStateOf(0) }
    var bottomColumnHeight by rememberSaveable { mutableIntStateOf(0) }
    var railHeight by rememberSaveable { mutableIntStateOf(0) }
    var contentExceedsAvailableSpace by remember { mutableStateOf(false) }

    NavigationRail(
        containerColor = bordercol,
        contentColor = textcol,

        modifier = Modifier

            .width(64.dp)
            .onGloballyPositioned { layoutInfo ->
                val newRailHeight = layoutInfo.size.height
                if (railHeight != newRailHeight) {
                    railHeight = newRailHeight
                }
            }
            .then(if (contentExceedsAvailableSpace) Modifier.verticalScroll(rememberScrollState()) else Modifier)

    ) {
        Column(modifier = Modifier.onGloballyPositioned { layoutInfo ->
            val newTopColumnHeight = layoutInfo.size.height
            if (topColumnHeight != newTopColumnHeight) {
                topColumnHeight = newTopColumnHeight
            }
        }, verticalArrangement = Arrangement.Top) {
            itemstop.forEachIndexed { _, item ->
                CustomNavigationRailItem(
                 item = item,
                    navController = navController
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(modifier = Modifier
            .wrapContentHeight()
            .onGloballyPositioned { layoutInfo ->
                val newBottomColumnHeight = layoutInfo.size.height
                if (bottomColumnHeight != newBottomColumnHeight) {
                    bottomColumnHeight = newBottomColumnHeight
                }
            }) {
            itemsbottom.forEachIndexed { _, item ->
                CustomNavigationRailItem(
                    item = item,
                    navController = navController
                )
            }
        }
    }

    contentExceedsAvailableSpace =
        topColumnHeight / itemstop.size * (itemstop.size + itemsbottom.size) > railHeight
}

@Stable
@Composable
fun CustomNavigationRailItem(item: NavigationItems, navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isSelected by remember {
        derivedStateOf {
            navBackStackEntry?.destination?.route == item.route
        }
    }
    NavigationRailItem(
        colors = NavigationRailItemDefaults.colors(
            selectedIconColor = textcol,
            selectedTextColor = textcol,
            unselectedTextColor = textcol.copy(0.4f),
            unselectedIconColor = textcol.copy(0.4f),
        ),

        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(id = item.icon),
                contentDescription = item.title,
                modifier = Modifier.size(24.dp)
            )
        },
        label = { Text( item.title) },
        selected = isSelected,
        onClick = {
            navController.navigate(item.route) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}
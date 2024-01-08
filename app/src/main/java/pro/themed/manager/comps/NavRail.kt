package pro.themed.manager.comps

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import pro.themed.manager.log
import pro.themed.manager.ui.theme.cardcol
import pro.themed.manager.ui.theme.textcol
import pro.themed.manager.utils.NavigationItems

@Stable
@Composable
fun NavigationRailSample(
    navController: NavController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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
    var topColumnHeight by remember { mutableIntStateOf(0) }
    var bottomColumnHeight by remember { mutableIntStateOf(0) }
    var railHeight by remember { mutableIntStateOf(0) }
    var contentExceedsAvailableSpace by remember { mutableStateOf(false) }
    contentExceedsAvailableSpace =
        topColumnHeight / itemstop.size * (itemstop.size + itemsbottom.size) > railHeight



    NavigationRail(elevation = if ( isSystemInDarkTheme().also { it.log() })0.1.dp else (-80).dp,
        backgroundColor = MaterialTheme.colors.cardcol,
        contentColor = MaterialTheme.colors.textcol,

        modifier = Modifier
            .shadow(1.dp)
            .zIndex(10f)
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
    CustomNavigationRailItem(
        isSelected = isSelected,
        onClick = {
            if (!isSelected) {
                navController.navigate(item.route)
            }
        },
        iconResource = item.icon,
        itemTitle = item.title
    )
}
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(modifier = Modifier
            .wrapContentHeight()
            .onGloballyPositioned { layoutInfo ->
                bottomColumnHeight = layoutInfo.size.height
            }) {
            itemsbottom.forEachIndexed { _, item ->
                val isSelected = currentRoute == item.route
                CustomNavigationRailItem(
                    isSelected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(item.route)
                        }
                    },
                    iconResource = item.icon,
                    itemTitle = item.title
                )
            }
        }
    }
}
@Stable
@Composable
fun CustomNavigationRailItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    iconResource: Int,
    itemTitle: String
) {
    NavigationRailItem(
        selectedContentColor = MaterialTheme.colors.textcol,
        unselectedContentColor = MaterialTheme.colors.textcol.copy(0.4f),
        icon = {
            Icon(
                painterResource(id = iconResource),
                contentDescription = itemTitle,
                modifier = Modifier.size(24.dp)
            )
        },
        label = { Text(itemTitle) },
        selected = isSelected,
        onClick = onClick
    )
}
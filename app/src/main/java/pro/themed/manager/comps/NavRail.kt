package pro.themed.manager.comps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pro.themed.manager.MyApplication
import pro.themed.manager.R
import pro.themed.manager.ui.theme.cardcol
import pro.themed.manager.ui.theme.textcol


@Preview
@Composable
fun NavigationRailSample() {
    val context = MyApplication.appContext
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Colors", "QsPanel", "Navbar", "Iconpack")
    val icons = listOf(
        R.drawable.format_paint,
        R.drawable.format_paint,
        R.drawable.format_paint,
        R.drawable.format_paint
    )

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
            ) {
ColorsTab()            }
        }

        androidx.compose.material.NavigationRail(
            backgroundColor = MaterialTheme.colors.cardcol,
            contentColor = MaterialTheme.colors.textcol,
            modifier = Modifier.width(64.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(
                        rememberScrollState()
                    ), verticalArrangement = Arrangement.Bottom
            ) {
                items.forEachIndexed { index, item ->
                    androidx.compose.material.NavigationRailItem(
                        selectedContentColor = MaterialTheme.colors.textcol,
                        unselectedContentColor = MaterialTheme.colors.textcol.copy(0.4f),
                        icon = { Icon(painterResource(id = icons[index]), contentDescription = null) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index })
                }
                Spacer(Modifier.height(64.dp))
            }
        }
    }

}

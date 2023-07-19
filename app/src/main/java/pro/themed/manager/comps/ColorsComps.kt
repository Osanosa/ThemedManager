@file:OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalFoundationApi::class
) @file:Suppress("LocalVariableName")

package pro.themed.manager.comps

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.AdmobBanner
import pro.themed.manager.R
import pro.themed.manager.getOverlayList
import pro.themed.manager.overlayEnable
import pro.themed.manager.ui.theme.cardcol


@ExperimentalMaterialApi
@Composable
fun ColorTile(name: String, color: String, modifier: Modifier = Modifier) {

    val context = LocalContext.current

    Surface(modifier = modifier,
        color = Color(color.toColorInt()),
        onClick = { overlayEnable("$name.${color.removePrefix("#")}") }) {

        if (getOverlayList().enabledOverlays.any { it.contains(name + "." + color.removePrefix("#")) }) {

            Icon(
                painter = painterResource(id = R.drawable.check_small_48px),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(0.9f)
            )
        }
    }
}


@Composable
fun VerticalGrid(
    modifier: Modifier = Modifier, columns: Int = 2, content: @Composable () -> Unit
) {
    Layout(
        content = content, modifier = modifier
    ) { measurables, constraints ->
        val itemWidth = constraints.maxWidth / columns

        val itemConstraints = constraints.copy(
            minWidth = itemWidth, maxWidth = itemWidth
        )

        val placeables = measurables.map { it.measure(itemConstraints) }

        val columnHeights = Array(columns) { 0 }
        placeables.forEachIndexed { index, placeable ->
            val column = index % columns
            columnHeights[column] += placeable.height
        }
        val height = (columnHeights.maxOrNull() ?: constraints.minHeight).coerceAtMost(
            constraints.maxHeight
        )
        layout(
            width = constraints.maxWidth, height = height
        ) {
            val columnY = Array(columns) { 0 }
            placeables.forEachIndexed { index, placeable ->
                val column = index % columns
                placeable.placeRelative(
                    x = column * itemWidth, y = columnY[column]
                )
                columnY[column] += placeable.height
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ColorTilesRow(
    name: String, colors: List<String>

) {
    VerticalGrid(columns = 8) {
        colors.forEach { color ->
            ColorTile(
                name = name, color = color, modifier = Modifier.aspectRatio(1f)
            )
        }
    }
}


//@Preview
@SuppressLint("NewApi")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ColorsTab() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp),
        color = MaterialTheme.colors.cardcol
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            val context = LocalContext.current
            AdmobBanner()
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()

            AccentsAAPT()

            FabricatedMonet()

            Spacer(modifier = Modifier.height(8.dp))


        }

    }


}

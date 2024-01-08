package pro.themed.manager.comps

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.R
import pro.themed.manager.ui.theme.Accent


@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun FontsTab() {
    data class FontItemData(
        val imageResourceId: Int,
        val config_bodyFontFamily: String = "@string/config_bodyFontFamily",
        val config_bodyFontFamilyMedium: String = "@string/config_bodyFontFamily",
        val config_headlineFontFamily: String = "@string/config_bodyFontFamily",
        val config_headlineFontFamilyMedium: String = "@string/config_bodyFontFamily",
        val config_lightFontFamily: String = "@string/config_bodyFontFamily",
        val config_regularFontFamily: String = "@string/config_bodyFontFamily"
    )

    val fontItems = listOf(
        FontItemData(R.drawable.font_3d_disometric_black, "3d-isometric-black"),
        FontItemData(R.drawable.font_3d_disometric_bold, "3d-isometric-bold"),
    )
    var font by remember { mutableStateOf("System") }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun FontItem(
        imageResourceId: Int,
        config_bodyFontFamily: String,
        config_bodyFontFamilyMedium: String = "@string/config_bodyFontFamily",
        config_headlineFontFamily: String = "@string/config_bodyFontFamily",
        config_headlineFontFamilyMedium: String = "@string/config_bodyFontFamily",
        config_lightFontFamily: String = "@string/config_bodyFontFamily",
        config_regularFontFamily: String = "@string/config_bodyFontFamily",
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(onClick = { }, onLongClick = { })
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id = imageResourceId),
                contentDescription = null,
                modifier = Modifier
                    .basicMarquee(spacing = MarqueeSpacing(0.dp))
                    .padding(16.dp)
            )

        }
    }
    Column {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(text = "Apply to:")
            Spacer(modifier = Modifier.width(8.dp))

            LaunchedEffect(font) {
            //    Shell("su").run("cd ${GlobalVariables.modulePath}/onDemandCompiler/Font")
              //  Shell("su").run("""sed -i 's/@drawable\/[^"]*/@drawable\/bg_$font/g' "res/drawable/themed_qspanel.xml"""")

                Shell("su").run("cmd vibrator_manager synced -f -d dumpstate oneshot 50")


            }

            FilterChip(colors = ChipDefaults.filterChipColors(selectedBackgroundColor = Accent),
                shape = CircleShape,
                selected = font.contains("System"),
                onClick = { font = "System" },
                content = { Text("System") },
                leadingIcon = if (font.contains("System")) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Localized Description",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                })
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(colors = ChipDefaults.filterChipColors(selectedBackgroundColor = Accent),
                shape = CircleShape,
                selected = font.contains("Clock"),
                onClick = { font = "Clock" },
                content = { Text("Clock") },
                leadingIcon = if (font.contains("Clock")) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Localized Description",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                })
        }
        LazyColumn {
            items(fontItems) { item ->
                FontItem(
                    item.imageResourceId,
                    item.config_bodyFontFamily,
                    item.config_bodyFontFamilyMedium,
                    item.config_headlineFontFamily,
                    item.config_headlineFontFamilyMedium,
                    item.config_lightFontFamily,
                    item.config_regularFontFamily
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Not yet implemented") }
        }
    }
}
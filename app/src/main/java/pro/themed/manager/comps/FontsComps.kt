package pro.themed.manager.comps

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.jaredrummler.ktsh.*
import pro.themed.manager.R
import pro.themed.manager.ui.theme.*


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
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
            //    Shell.SU.run("cd ${GlobalVariables.modulePath}/onDemandCompiler/Font")
              //  Shell.SU.run("""sed -i 's/@drawable\/[^"]*/@drawable\/bg_$font/g' "res/drawable/themed_qspanel.xml"""")

                Shell.SU.run("cmd vibrator_manager synced -f -d dumpstate oneshot 50")


            }

            FilterChip(colors = ChipDefaults.filterChipColors(selectedBackgroundColor = Purple),
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
            FilterChip(colors = ChipDefaults.filterChipColors(selectedBackgroundColor = Purple),
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
        }
    }
}
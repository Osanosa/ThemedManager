@file:OptIn(
    ExperimentalMaterialApi::class, ExperimentalMaterialApi::class, ExperimentalMaterialApi::class
)

package pro.themed.manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import pro.themed.manager.ui.theme.ThemedManagerTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemedManagerTheme {
                Column {


                    Text(text = "Just a test")
                }
            }
        }
    }
}


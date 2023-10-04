package pro.themed.manager

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.navigation.compose.*
import pro.themed.manager.ui.theme.*

class ToolboxActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemedManagerTheme {

            }
        }
    }




    @Composable
    fun TopAppToolbox() {
        val context = LocalContext.current
        val navController = rememberNavController()
        TopAppBar(elevation = 0.dp,
            title = { Text(text = stringResource(R.string.toolbox)) },
            backgroundColor = MaterialTheme.colors.cardcol,
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigateUp()
                    finish()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "backIcon")
                }
            })
    }
}

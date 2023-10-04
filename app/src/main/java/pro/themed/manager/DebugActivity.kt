package pro.themed.manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import pro.themed.manager.comps.DebugPage
import pro.themed.manager.ui.theme.ThemedManagerTheme
import pro.themed.manager.ui.theme.cardcol

class DebugActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemedManagerTheme {
                DebugPage()

            }
        }
    }



    @Composable
    fun TopAppBarDebug() {
        val context = LocalContext.current
        val navController = rememberNavController()
        TopAppBar(
            elevation = 0.dp,
            title = { Text(text = stringResource(R.string.debug)) },
            backgroundColor = MaterialTheme.colors.cardcol,
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigateUp()
                    finish()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "backIcon")
                }
            },
        )
    }
}



package pro.themed.manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import pro.themed.manager.ui.theme.ThemedManagerTheme

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemedManagerTheme {
                AboutPage()

            }
        }
    }

    @Preview
    @Composable
    fun AboutPage() {
        Surface {
            Column {
                TopAppBarAbout()
                Image(
                    painter = painterResource(id = R.drawable.main_logo_circle_mask00000),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .align(alignment = CenterHorizontally)

                )
            }
        }

    }

    @Composable
    fun TopAppBarAbout() {
        val navController = rememberNavController()
        TopAppBar(title = { Text(text = "About") },
            backgroundColor = MaterialTheme.colors.cardcol,
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigateUp()
                    finish()
                }) {
                    Icon(Icons.Filled.ArrowBack, "backIcon")
                }
            })
    }
}



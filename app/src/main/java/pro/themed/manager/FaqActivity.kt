package pro.themed.manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import pro.themed.manager.ui.theme.ThemedManagerTheme

class FaqActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemedManagerTheme {
                FaqPage()

            }
        }
    }


    @Preview
    @Composable
    fun FaqPage() {
        val context = LocalContext.current
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.cardcol
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TopAppBarFaq()
                Column() {
                    Text(text = "To be filled")
                }
            }
        }

    }

    @Composable
    fun TopAppBarFaq() {
        val context = LocalContext.current
        val navController = rememberNavController()
        TopAppBar(
            elevation = 0.dp,
            title = { Text(text = "Faq") },
            backgroundColor = MaterialTheme.colors.cardcol,
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigateUp()
                    finish()
                }) {
                    Icon(Icons.Filled.ArrowBack, "backIcon")
                }
            },
        )
    }
}



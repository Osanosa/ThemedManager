package pro.themed.manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import pro.themed.manager.ui.theme.ThemedManagerTheme

class SettingsActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThemedManagerTheme {
                val context = LocalContext.current
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {


                    Column {
                        /* Button(onClick = {

                            // if [pro.themed.manager.DataStore(context).getAutoRestartSystemUIValue = true] ()

                             CoroutineScope(Dispatchers.IO).launch {
                                 DataStore(context).setAutoRestartSystemUI(false)
                             }
                         }) {
                             Text(text = DataStore(context).getAutoRestartSystemUIValue.collectAsState(
                                 initial = ""
                             ).toString())

                         }*/
                    }
                }
            }
        }
    }
}


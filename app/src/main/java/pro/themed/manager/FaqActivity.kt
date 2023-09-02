package pro.themed.manager

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.delay
import pro.themed.manager.ui.theme.ThemedManagerTheme
import pro.themed.manager.ui.theme.cardcol
import pro.themed.manager.utils.MyBackgroundService
import java.util.concurrent.TimeUnit


class FaqActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemedManagerTheme {
                FaqPage()
                startService(Intent(this, MyBackgroundService::class.java));
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
                Column {
                    Text(text = "To be filled")
                    var test by remember { mutableStateOf("") }
                    val shell1 = Shell.SH
                    val shell2 = Shell.SU
                    Text(text = test)
                    SideEffect {
                        shell1.run("su -c getevent | grep 0003") {
                            onStdOut = { line: String ->
                                test = line
                                //

                            }
                            timeout = Shell.Timeout(100, TimeUnit.MILLISECONDS)
                        }
                    }
                    var countdown by remember(test) { mutableIntStateOf(3) }

                    LaunchedEffect(test ){
                        shell2.run("service call SurfaceFlinger 1035 i32 0")

                        while (countdown > 0) {
                            delay(1000) // 1 second delay
                            countdown -= 1
                        }

                        Toast.makeText(context, "toast", Toast.LENGTH_SHORT).show()

                        shell2.run("service call SurfaceFlinger 1035 i32 2")


                    }


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
            title = { Text(text = stringResource(R.string.faq)) },
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



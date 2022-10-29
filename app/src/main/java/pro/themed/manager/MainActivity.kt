package pro.themed.manager

import android.content.Intent
import android.graphics.Color.rgb
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells.Fixed
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaredrummler.ktsh.Shell


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThemedManagerTheme()
}
}}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun ThemedManagerTheme() {


    Surface(modifier = Modifier.fillMaxSize()) {

        Column {

            TopAppBar(
                title = { Text("Themed Manager") },
                actions = {
                    val context = LocalContext.current
                    val webIntent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.t.me/ThemedSupport"))

                    IconButton(onClick = {context.startActivity(webIntent) }) {
                        Image(painter = painterResource (R.drawable.telegram_svgrepo_com), contentDescription = null) }

                    val context1 = LocalContext.current
                    val webIntent1: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/Osanosa/ThemedProject/"))

                     IconButton(onClick = {context1.startActivity(webIntent1)}) {
                        Image(painter = painterResource (R.drawable.iconmonstr_github_1), contentDescription = null)
                    }
                    
                    val context2 = LocalContext.current
                    val webIntent2: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themed.pro/"))

                     IconButton(onClick = {context2.startActivity(webIntent2)}) {
                         Image(painter = painterResource (R.drawable.baseline_language_24), contentDescription = null)
                    }
                }
            )

            Card(border = BorderStroke(width = 1.dp, color = colorResource(id = R.color.grey_outline)), modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp), elevation = (0.dp),  shape = RoundedCornerShape(8.dp)) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(modifier = Modifier
                            .padding(8.dp)
                            .padding(start = 8.dp),
                            text = "Accents", fontSize = 24.sp)
                       // Switch(checked = false, onCheckedChange = null, modifier = Modifier.align(CenterVertically))
                    }
                    Divider(thickness = 1.dp,)
                    LazyVerticalGrid(
                        modifier = Modifier.padding(0.dp),
                        cells = Fixed(8),
                    ) {
                        item {Text(modifier = Modifier.padding(4.dp), text = "A700", fontSize = 12.sp, textAlign = TextAlign.Center ) }
                        item {Text(modifier = Modifier.padding(4.dp), text = "500", fontSize = 12.sp, textAlign = TextAlign.Center) }
                        item {Text(modifier = Modifier.padding(4.dp), text = "A200", fontSize = 12.sp, textAlign = TextAlign.Center) }
                        item {Text(modifier = Modifier.padding(4.dp), text = "A400", fontSize = 12.sp, textAlign = TextAlign.Center) }
                        item {Text(modifier = Modifier.padding(4.dp), text = "A700", fontSize = 12.sp, textAlign = TextAlign.Center) }
                        item {Text(modifier = Modifier.padding(4.dp), text = "500", fontSize = 12.sp, textAlign = TextAlign.Center) }
                        item {Text(modifier = Modifier.padding(4.dp), text = "A200", fontSize = 12.sp, textAlign = TextAlign.Center) }
                        item {Text(modifier = Modifier.padding(4.dp), text = "A400", fontSize = 12.sp, textAlign = TextAlign.Center) }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(213, 0, 0))
                                ),
                                onClick = {  Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialRedA700") }) { }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(244, 67, 54))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialRed500") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(255, 82, 82))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialRedA200") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(255, 23, 68))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialRedA400") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(221, 44, 0))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialDeepOrangeA700") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(255, 87, 34))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialDeepOrange500") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(255, 110, 64))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialDeepOrangeA200") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(255, 61, 0))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialDeepOrangeA400") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(255, 109, 0))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialOrangeA700") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(255, 152, 0))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialOrange500") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(255, 171, 64))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialOrangeA200") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(255, 145, 0))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialOrangeA400") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(255, 171, 0))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialAmberA700") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(255, 193, 7))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialAmber500") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(255, 215, 64))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialAmberA200") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(255, 196, 0))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialAmberA400") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(255, 214, 0))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialYellowA700") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(255, 235, 59))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialYellow500") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(255, 255, 0))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialYellowA200") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(255, 234, 0))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialYellowA400") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(174, 234, 0))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLimeA700") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(205, 220, 57))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLime500") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(238, 255, 65))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLimeA200") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(198, 255, 0))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLimeA400") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(100, 221, 23))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLightGreenA700") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(139, 195, 74))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLightGreen500") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(178, 255, 89))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLightGreenA200") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(118, 255, 3))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLightGreenA400") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(0, 200, 83))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialGreenA700") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(76, 175, 80))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialGreen500") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(105, 240, 174))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialGreenA200") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(0, 230, 118))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialGreenA400") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(0, 191, 165))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialTealA700") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(0, 150, 136))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialTeal500") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(100, 255, 218))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialTealA200") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(29, 233, 182))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialTealA400") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(0, 184, 212))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialCyanA700") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(0, 188, 212))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialCyan500") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(24, 255, 255))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialCyanA200") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(0, 229, 255))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialCyanA400") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(0, 145, 234))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLightBlueA700") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(3, 169, 244))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLightBlue500") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(64, 196, 255))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLightBlueA200") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(0, 176, 255))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialLightBlueA400") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(41, 98, 255))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialBlueA700") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(33, 150, 243))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialBlue500") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(68, 138, 255))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialBlueA200") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(41, 121, 255))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialBlueA400") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(48, 79, 254))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialIndigoA700") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(63, 81, 181))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialIndigo500") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(83, 109, 254))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialIndigoA200") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(61, 90, 254))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialIndigoA400") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(98, 0, 234))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialDeepPurpleA700") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(103, 58, 183))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialDeepPurple500") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(124, 77, 255))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialDeepPurpleA200") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(101, 31, 255))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialDeepPurpleA400") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(170, 0, 255))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialPurpleA700") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(156, 39, 176))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialPurple500") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(224, 64, 251))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialPurpleA200") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(213, 0, 249))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialPurpleA400") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(197, 17, 98))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialPinkA700") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(233, 30, 99))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialPink500") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(255, 64, 129))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialPinkA200") }) {
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.background(
                                    Color(rgb(245, 0, 87))
                                ),
                                onClick = { Shell.SU.run("cmd overlay enable-exclusive --category themed.accents.MaterialPinkA400") }) {
                            }
                        }

                    }
                }
            }
            Card(border = BorderStroke(width = 1.dp, color = colorResource(id = R.color.grey_outline)), modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp)
                .padding(top = 0.dp), elevation = (0.dp), shape = RoundedCornerShape(8.dp)) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .padding(start = 8.dp), text = "QSTiles", fontSize = 24.sp
                )
            }
            Card(border = BorderStroke(width = 1.dp, color = colorResource(id = R.color.grey_outline)), modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp)
                .padding(top = 0.dp), elevation = (0.dp), shape = RoundedCornerShape(8.dp)) {
                Text(
                    modifier = Modifier
                        .padding(8.dp), text = "Please note that on older devices updating systemUI can take up to 30s. It is recommended that you'd install bootloop protector module. Please report testing to telegram support group.", fontSize = 14.sp
                )
            }
            Card(border = BorderStroke(width = 1.dp, color = colorResource(id = R.color.grey_outline)), modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp)
                .padding(top = 0.dp), elevation = (0.dp), shape = RoundedCornerShape(8.dp)) {
                Text(
                    modifier = Modifier
                        .padding(8.dp), text = "Please note that on older devices updating systemUI can take up to 30s. It is recommended that you'd install bootloop protector module. Please report testing to telegram support group.", fontSize = 14.sp
                )
            }

        }
    }

}



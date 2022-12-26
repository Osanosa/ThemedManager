package pro.themed.manager.comps

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.*
import pro.themed.manager.R
import kotlin.math.roundToInt



@Preview
@Composable
fun MiscTab() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp),
        color = MaterialTheme.colors.cardcol
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            RoundedCornersCard()
            QsGridCard()
            AdrodInfoCard()
        }
    }

}

//@Preview
@Composable
fun RoundedCornersCard() {
    var sliderPosition by remember { mutableStateOf(0f) }

    Card(
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.bordercol),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        elevation = (0.dp),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.cardcol
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .padding(start = 8.dp),
                    text = "Corners",
                    fontSize = 24.sp
                )
                IconButton(onClick = {
                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'themed.rounded' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"$" + "ol\"; done")
                }) {
                    Image(
                        painter = painterResource(R.drawable.reset),
                        contentDescription = null
                    )
                }
            }

            Divider(thickness = 1.dp, color = MaterialTheme.colors.bordercol)
            Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                var rounddp = sliderPosition.roundToInt()
                Text(text = rounddp.toString())
                val context = LocalContext.current
                Slider(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it; rounddp = it.roundToInt() },
                    valueRange = 0f..36f,
                    onValueChangeFinished = {
                        Toast.makeText(context, "$rounddp", Toast.LENGTH_SHORT).show()
                        overlayEnable("roundedcorners$rounddp")
                    },
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colors.cardcol,
                        activeTrackColor = MaterialTheme.colors.bordercol
                    )
                )
            }
        }
    }
}

//@Preview
@Composable
fun QsGridCard() {
    var rowspositionportrait by remember { mutableStateOf(1f) }
    var columnspositionportrait by remember { mutableStateOf(1f) }
    var rowspositionlandscape by remember { mutableStateOf(1f) }
    var columnspositionlandscape by remember { mutableStateOf(1f) }

    Card(
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.bordercol),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        elevation = (0.dp),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.cardcol
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .padding(start = 8.dp),
                    text = "QsGrid",
                    fontSize = 24.sp
                )
                IconButton(onClick = {
                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'themed.qsgrid' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"$" + "ol\"; done")
                }) {
                    Image(
                        painter = painterResource(R.drawable.reset),
                        contentDescription = null
                    )
                }
            }

            Divider(thickness = 1.dp, color = MaterialTheme.colors.bordercol)
            Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                val context = LocalContext.current
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    var rowsportrait = rowspositionportrait.roundToInt()
                    var columnsportrait = columnspositionportrait.roundToInt()
                    Text(text = "Portrait $rowsportrait × $columnsportrait", fontSize = 20.sp)
                    Slider(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        value = rowspositionportrait,
                        onValueChange = { rowspositionportrait = it; rowsportrait = it.roundToInt() },
                        valueRange = 1f..10f,
                        onValueChangeFinished = {
                            overlayEnable("qsgrid.rowsportrait$rowsportrait")
                        },
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colors.cardcol,
                            activeTrackColor = MaterialTheme.colors.bordercol
                        )
                    )
                    Slider(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        value = columnspositionportrait,
                        onValueChange = { columnspositionportrait = it; columnsportrait = it.roundToInt() },
                        valueRange = 1f..10f,
                        onValueChangeFinished = {
                            overlayEnable("qsgrid.columnsportrait$columnsportrait")
                        },
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colors.cardcol,
                            activeTrackColor = MaterialTheme.colors.bordercol
                        )
                    )
                    var rowslandscape = rowspositionlandscape.roundToInt()
                    var columnslandscape = columnspositionlandscape.roundToInt()
                    Text(text = "Landscape $rowslandscape × $columnslandscape", fontSize = 20.sp)
                    Slider(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        value = rowspositionlandscape,
                        onValueChange = { rowspositionlandscape = it; rowslandscape = it.roundToInt() },
                        valueRange = 1f..10f,
                        onValueChangeFinished = {
                            overlayEnable("qsgrid.rowslandscape$rowslandscape")
                        },
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colors.cardcol,
                            activeTrackColor = MaterialTheme.colors.bordercol
                        )
                    )
                    Slider(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        value = columnspositionlandscape,
                        onValueChange = { columnspositionlandscape = it; columnslandscape = it.roundToInt() },
                        valueRange = 1f..10f,
                        onValueChangeFinished = {
                            overlayEnable("qsgrid.columnslandscape$columnslandscape")
                        },
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colors.cardcol,
                            activeTrackColor = MaterialTheme.colors.bordercol
                        )
                    )


                }

            }
        }
    }
}

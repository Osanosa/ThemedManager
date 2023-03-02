package pro.themed.manager.comps

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.R
import pro.themed.manager.cardcol
import pro.themed.manager.overlayEnable
import kotlin.math.roundToInt


@ExperimentalMaterial3Api
@Composable
fun Slideritem(
    drawable: Int,
    header: String,
    sliderSteps: Int,
    sliderStepValue: Int,
    minSliderValue: Float,
    maxSliderValue: Float,
    overlayName: String
) {
    val context = LocalContext.current
    var sliderPosition by remember { mutableStateOf(0f) }
    var intvalue by remember { mutableStateOf(sliderPosition.roundToInt()) }


    sliderPosition = sliderPosition.coerceIn(minSliderValue, maxSliderValue)
    intvalue = intvalue.coerceIn(minSliderValue.toInt(), maxSliderValue.toInt())
    Surface {


        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                // horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(id = drawable),
                    contentDescription = "",
                    Modifier
                        .padding(horizontal = 16.dp)
                        .size(24.dp)
                )
                Column {
                    Text(
                        text = header, fontWeight = FontWeight.SemiBold, fontSize = 16.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Text(text = "Value: $intvalue")

                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(modifier = Modifier, onClick = {
                    intvalue = 0; sliderPosition = intvalue.toFloat()
                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'themed.$overlayName' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
                }) {
                    Image(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .size(24.dp),
                        painter = painterResource(R.drawable.reset),
                        contentDescription = null,
                    )
                }

            }
            Row(
                Modifier, verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable {
                            intvalue -= sliderStepValue; sliderPosition = intvalue.toFloat()
                            Toast.makeText(context, "$intvalue", Toast.LENGTH_SHORT).show()
                            overlayEnable("$overlayName$intvalue")

                        },
                    painter = painterResource(R.drawable.remove_48px),
                    contentDescription = null,
                )

                androidx.compose.material3.Slider(modifier = Modifier
                    .height(16.dp)
                    .weight(1f)
                    .padding(0.dp),
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it; intvalue = it.roundToInt() },
                    valueRange = minSliderValue..maxSliderValue,
                    onValueChangeFinished = {
                        Toast.makeText(context, "$intvalue", Toast.LENGTH_SHORT).show()
                        overlayEnable("$overlayName$intvalue")
                    },
                    steps = sliderSteps,
                    thumb = {
                        Image(
                            painter = painterResource(R.drawable.fiber_manual_record_48px),
                            contentDescription = null,
                        )

                    })

                Image(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable {
                           intvalue += sliderStepValue; sliderPosition = intvalue.toFloat()
                            Toast.makeText(context, "$intvalue", Toast.LENGTH_SHORT).show()
                            overlayEnable("$overlayName$intvalue")
                        },
                    painter = painterResource(R.drawable.add_48px),
                    contentDescription = null,
                )

            }
        }


    }
}

@ExperimentalMaterial3Api
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

            Slideritem(
                drawable = R.drawable.rounded_corner_48px,
                header = "Rounded corners",
                sliderSteps = 17,
                sliderStepValue = 2,
                minSliderValue = 0f,
                maxSliderValue = 36f,
                overlayName = "roundedcorners"
            )
            Divider()
            Slideritem(
                drawable = R.drawable.view_week_48px,
                header = "Columns (portrait)",
                sliderSteps = 8,
                sliderStepValue = 1,
                minSliderValue = 1f,
                maxSliderValue = 10f,
                overlayName = "columnsportrait"
            )
            Slideritem(
                drawable = R.drawable.table_rows_48px,
                header = "Rows (portrait)",
                sliderSteps = 8,
                sliderStepValue = 1,
                minSliderValue = 1f,
                maxSliderValue = 10f,
                overlayName = "rowsportrait"
            )
            Divider()
            Slideritem(
                drawable = R.drawable.view_week_48px,
                header = "Columns (landscape)",
                sliderSteps = 8,
                sliderStepValue = 1,
                minSliderValue = 1f,
                maxSliderValue = 10f,
                overlayName = "columnslandscape"
            )
            Slideritem(
                drawable = R.drawable.table_rows_48px,
                header = "Rows (landscape)",
                sliderSteps = 8,
                sliderStepValue = 1,
                minSliderValue = 1f,
                maxSliderValue = 10f,
                overlayName = "rowslandscape"
            )
            Slideritem(
                drawable = R.drawable.table_rows_48px,
                header = "QsQuickTileSize",
                sliderSteps = 0,
                sliderStepValue = 20,
                minSliderValue = 60f,
                maxSliderValue = 80f,
                overlayName = "rowslandscape"
            )

        }
    }

}

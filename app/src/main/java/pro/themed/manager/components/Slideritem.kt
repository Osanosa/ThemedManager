package pro.themed.manager.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.MainActivity.Companion.overlayList
import pro.themed.manager.R
import pro.themed.manager.utils.overlayEnable
import kotlin.math.roundToInt


@Stable
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
    var sliderPosition by rememberSaveable { mutableFloatStateOf(0f) }
    var intvalue by rememberSaveable { mutableIntStateOf(sliderPosition.roundToInt()) }

    sliderPosition = sliderPosition.coerceIn(minSliderValue, maxSliderValue)
    intvalue = intvalue.coerceIn(minSliderValue.toInt(), maxSliderValue.toInt())
    if (overlayList.overlayList.any { it.contains(overlayName) } && !overlayList.unsupportedOverlays.any {
            it.contains(
                overlayName
            )
        }) {

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
                    Shell("su").run("for ol in \$(cmd overlay list | grep -E 'themed.$overlayName' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
                }) {
                    Image(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .size(24.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.reset),
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
                            Toast
                                .makeText(context, "$intvalue", Toast.LENGTH_SHORT)
                                .show()
                            overlayEnable("$overlayName$intvalue")

                        },
                    imageVector = ImageVector.vectorResource(id = R.drawable.remove_48px),
                    contentDescription = null,
                )

                Slider(modifier = Modifier
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
                            imageVector = ImageVector.vectorResource(id = R.drawable.fiber_manual_record_48px),
                            contentDescription = null,
                        )

                    })

                Image(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable {
                            intvalue += sliderStepValue; sliderPosition = intvalue.toFloat()
                            Toast
                                .makeText(context, "$intvalue", Toast.LENGTH_SHORT)
                                .show()
                            overlayEnable("$overlayName$intvalue")
                        },
                    imageVector = ImageVector.vectorResource(id = R.drawable.add_48px),
                    contentDescription = null,
                )

            }
        }

    }
}

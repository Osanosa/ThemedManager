package pro.themed.manager.comps


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pro.themed.manager.R
import pro.themed.manager.buildOverlay
import pro.themed.manager.log
import pro.themed.manager.utils.showInterstitial
import kotlin.math.roundToInt


@SuppressLint("CommitPrefEdits")
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccentsAAPT() {
    Surface {
        val context = LocalContext.current

        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        Column {
            val shell = Shell("su")
            shell.addOnCommandResultListener(object : Shell.OnCommandResultListener {
                override fun onResult(result: Shell.Command.Result) {
                    CoroutineScope(Dispatchers.Main).launch {

                        // do something
                        Toast.makeText(context, result.toString(), Toast.LENGTH_SHORT).show()
                        result.log()
                    }
                }
            })
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .padding(start = 8.dp),
                    text = "Accent",
                    fontSize = 24.sp
                )

                IconButton(modifier = Modifier, onClick = {
                    shell.run("cmd overlay disable themed.accent.generic ; pm uninstall themed.accent.generic")
                }) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.reset),
                        contentDescription = null,
                    )
                }
            }

            var hue by rememberSaveable { mutableFloatStateOf(0.5f) }
            var saturation by rememberSaveable { mutableFloatStateOf(100f) }
            var lightness by rememberSaveable { mutableFloatStateOf(50f) }

            if (hue == 360f) {
                hue = 0f
            }


            val color = Color.hsl(hue, saturation / 100, lightness / 100)

            Text(text = "hue is ${hue.toInt()}°")
            Slider(modifier = Modifier
                .height(16.dp)
                .padding(0.dp), value = hue, onValueChange = {
                hue = it.roundToInt().toFloat()
            }, valueRange = 0f..360f, onValueChangeFinished = {}, steps = 0, thumb = {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.fiber_manual_record_48px),
                    contentDescription = null,
                )

            })
            Text(text = "saturation is ${saturation.toInt()}%")
            Slider(modifier = Modifier
                .height(16.dp)
                .padding(0.dp),
                value = saturation,
                onValueChange = {
                    saturation = it.roundToInt().toFloat()
                },
                valueRange = 0f..100f,
                onValueChangeFinished = {},
                steps = 0,
                thumb = {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.fiber_manual_record_48px),
                        contentDescription = null,
                    )
                })
            Text(text = "Lightness is ${lightness.toInt()}%")
            Slider(modifier = Modifier
                .height(16.dp)
                .padding(0.dp),
                value = lightness,
                onValueChange = {
                    lightness = it.roundToInt().toFloat()
                },
                valueRange = 0f..100f,
                onValueChangeFinished = {},
                steps = 0,
                thumb = {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.fiber_manual_record_48px),
                        contentDescription = null,
                    )
                })
            val hex = String.format("%08x".format(color.toArgb()))
            var isDark = ""
            HeaderRow(
                header = "Override colors for dark theme",
                showSwitch = true,
                isChecked = false,
                onCheckedChange = {
                    isDark = if (it) {
                        "-night"
                    } else {
                        ""
                    }
                },
            )

            Button(
                modifier = Modifier.fillMaxWidth(), onClick = {
                    shell.run("cd /data/adb/modules/ThemedProject/onDemandCompiler/staticAccent")
                    shell.run("""sed -i 's/>#\([0-9A-Fa-f]\{8\}\)</>#$hex</g' "res/values$isDark/colors.xml"""")
                    buildOverlay("/data/adb/modules/ThemedProject/onDemandCompiler/staticAccent")
                    shell.run("""cmd overlay enable themed.accent.generic""")
                    showInterstitial(context) {}


                }, colors = ButtonDefaults.buttonColors(
                    containerColor = color, contentColor = if (lightness > 50f) {
                        Color.Black
                    } else {
                        Color.White
                    }
                ), shape = CircleShape
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(text = "Build and update, $hex")
                    Icon(
                        modifier = Modifier.height(24.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.arrow_right_alt_48px),
                        contentDescription = ""
                    )

                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
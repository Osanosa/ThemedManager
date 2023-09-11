package pro.themed.manager.comps


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.R
import pro.themed.manager.buildOverlay
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
                    Shell.SU.run("cmd overlay disable themed.accent.generic ; pm uninstall themed.accent.generic")
                }) {
                    Image(
                        painter = painterResource(R.drawable.reset),
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
            androidx.compose.material3.Slider(modifier = Modifier
                .height(16.dp)
                .padding(0.dp),
                value = hue,
                onValueChange = {
                    hue = it.roundToInt().toFloat()
                },
                valueRange = 0f..360f,
                onValueChangeFinished = {},
                steps = 0,
                thumb = {
                    Image(
                        painter = painterResource(R.drawable.fiber_manual_record_48px),
                        contentDescription = null,
                    )

                })
            Text(text = "saturation is ${saturation.toInt()}%")
            androidx.compose.material3.Slider(modifier = Modifier
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
                        painter = painterResource(R.drawable.fiber_manual_record_48px),
                        contentDescription = null,
                    )
                })
            Text(text = "Lightness is ${lightness.toInt()}%")
            androidx.compose.material3.Slider(modifier = Modifier
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
                        painter = painterResource(R.drawable.fiber_manual_record_48px),
                        contentDescription = null,
                    )
                })
            val hex = String.format("%08x".format(color.toArgb()))
            val isDark = if (sharedPreferences.getBoolean("acccents_dark", false)) "-night" else ""
            HeaderRow(
                header = "Override colors for dark theme",
                showSwitch = true,
                isChecked = sharedPreferences.getBoolean("acccents_dark", false),
                onCheckedChange = {
                    if (it) {
                        editor.putBoolean("acccents_dark", true)
                        editor.apply()
                    } else {
                        editor.putBoolean("acccents_dark", false)
                        editor.apply()

                    }
                },
            )

            androidx.compose.material.Button(
                modifier = Modifier.fillMaxWidth(), onClick = {
                    Shell.SU.run("cd /data/adb/modules/ThemedProject/onDemandCompiler/staticAccent")
                    Shell.SU.run("""sed -i 's/>#\([0-9A-Fa-f]\{8\}\)</>#$hex</g' "res/values$isDark/colors.xml"""")
                    buildOverlay()
                    Shell.SU.run("""cmd overlay enable themed.accent.generic""")
                    showInterstitial(context) {}


                }, colors = ButtonDefaults.buttonColors(
                    backgroundColor = color, contentColor = if (lightness > 50f) {
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
                        painter = painterResource(id = R.drawable.arrow_right_alt_48px),
                        contentDescription = ""
                    )

                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
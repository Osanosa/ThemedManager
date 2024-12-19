package pro.themed.manager.comps

import android.widget.*
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.jaredrummler.ktsh.*
import kotlinx.coroutines.*
import pro.themed.manager.MainActivity.Companion.isDark
import pro.themed.manager.R.*
import pro.themed.manager.ui.theme.*
import pro.themed.manager.utils.*
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class) @Composable fun AccentsAAPT() {
    val context = LocalContext.current
    var expanded by rememberSaveable { mutableStateOf(true) }

    Card(onClick = { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = contentcol.copy(alpha = 0.05f), contentColor = contentcol),
        modifier = Modifier.animateContentSize()) {
        Card(shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = contentcol.copy(alpha = 0.05f),
                contentColor = contentcol),
            modifier = Modifier
                .animateContentSize()
                .padding(8.dp)) {
            Column(modifier = Modifier.animateContentSize()) {
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
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(modifier = Modifier

                        .padding(start = 16.dp), text = "Accent", style = MaterialTheme.typography.headlineSmall)

                    IconButton(onClick = {
                        expanded = !expanded
                    }, colors = IconButtonDefaults.iconButtonColors(containerColor = if (!expanded) {
                        Color.Gray
                    }
                    else {
                        Transparent
                    })) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null)
                    }
                }
                HorizontalDivider()
                var hue by rememberSaveable { mutableFloatStateOf(0.5f) }
                var saturation by rememberSaveable { mutableFloatStateOf(100f) }
                var lightness by rememberSaveable { mutableFloatStateOf(50f) }

                if (hue == 360f) {
                    hue = 0f
                }
                Box {
                    androidx.compose.animation.AnimatedVisibility(visible = !expanded,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()) {
                        //Tutorial text
                        Text(text = "This is the accent picker, " + "it is linked via overlay to framework-res, " + "overlaying some of the colors, " + "mainly those that were before A12. " + "This won't change background colors, " + "you can use FakeMonet for that.")
                    }
                    val color = Color.hsl(hue, saturation / 100, lightness / 100)
                    val hex = String.format("%08x".format(color.toArgb()))
                    val sliderColors = SliderDefaults.colors(activeTrackColor = Transparent,
                        inactiveTrackColor = Transparent,
                        thumbColor = White,
                        activeTickColor = White,
                        inactiveTickColor = White)

                    androidx.compose.animation.AnimatedVisibility(visible = expanded,
                        modifier = Modifier.wrapContentHeight(),
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })) {
                        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                            val colors = mutableListOf<Color>()
                            for (h in 0..360 step 60) {
                                colors.add(Color.hsl(h.toFloat(), 1f, 0.5f))
                            }
                            Text(text = "hue is ${hue.toInt()}°")
                            Slider(
                                colors = sliderColors,
                                modifier = Modifier
                                    .background(Brush.horizontalGradient(colors = colors), shape = CircleShape)
                                    .height(16.dp)
                                    .padding(0.dp),
                                value = hue,
                                onValueChange = {
                                    hue = it.roundToInt().toFloat()
                                },
                                valueRange = 0f..360f,
                                onValueChangeFinished = {},
                                steps = 72,
                            )
                            Text(text = "saturation is ${saturation.toInt()}%")
                            Slider(
                                colors = sliderColors,
                                modifier = Modifier
                                    .background(Brush.horizontalGradient(colors = listOf(Color.hsl(hue, 0f, 0.5f),
                                        Color.hsl(hue, 0.1f, 0.5f),
                                        Color.hsl(hue, 0.2f, 0.5f),
                                        Color.hsl(hue, 0.3f, 0.5f),
                                        Color.hsl(hue, 0.4f, 0.5f),
                                        Color.hsl(hue, 0.5f, 0.5f),
                                        Color.hsl(hue, 0.6f, 0.5f),
                                        Color.hsl(hue, 0.7f, 0.5f),
                                        Color.hsl(hue, 0.8f, 0.5f),
                                        Color.hsl(hue, 0.9f, 0.5f),
                                        Color.hsl(hue, 1f, 0.5f))), shape = CircleShape)
                                    .height(16.dp)
                                    .padding(0.dp),
                                value = saturation,
                                onValueChange = {
                                    saturation = it.roundToInt().toFloat()
                                },
                                valueRange = 0f..100f,
                                onValueChangeFinished = {},
                                steps = 19,
                            )
                            Text(text = "Lightness is ${lightness.toInt()}%")
                            Slider(
                                colors = sliderColors,
                                modifier = Modifier
                                    .background(Brush.horizontalGradient(colors = listOf(Color.hsl(hue,
                                        saturation / 100f,
                                        0f),
                                        Color.hsl(hue, saturation / 100f, 0.5f),
                                        Color.hsl(hue, saturation / 100f, 1f))), shape = CircleShape)

                                    .height(16.dp)
                                    .padding(0.dp),
                                value = lightness,
                                onValueChange = {
                                    lightness = it.roundToInt().toFloat()
                                },
                                valueRange = 0f..100f,
                                onValueChangeFinished = {},
                                steps = 19,
                            )

                            Row(Modifier.padding(vertical = 4.dp)) {
                                OutlinedButton (border = BorderStroke(2.dp, contentcol.copy(0.5f)), onClick = {
                                    shell.run("cmd overlay disable themed.accent.generic ; pm uninstall themed.accent.generic")
                                    hue = 45f
                                    saturation = 100f
                                    lightness = 50f
                                }) {
                                    Text(text = "Reset")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        shell.run("cd /data/adb/modules/ThemedProject/onDemandCompiler/staticAccent")
                                        shell.run("""sed -i 's/>#\([0-9A-Fa-f]\{8\}\)</>#$hex</g' "res/values$isDark/colors.xml"""")
                                        buildOverlay("/data/adb/modules/ThemedProject/onDemandCompiler/staticAccent")
                                        shell.run("""cmd overlay enable themed.accent.generic""")
                                        showInterstitial(context) {}

                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = color,
                                        contentColor = if (lightness > 50f) {
                                            Color.Black
                                        }
                                        else {
                                            White
                                        }),
                                    shape = CircleShape,
                                    contentPadding = PaddingValues(0.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "Build, ${hex.removePrefix("ff")}", color = if (lightness > 50f) {
                                            Color.Black
                                        }
                                        else {
                                            White
                                        })
                                        Icon(modifier = Modifier.height(24.dp),
                                            imageVector = ImageVector.vectorResource(id = drawable.arrow_right_alt_48px),
                                            contentDescription = "")

                                    }
                                }
                            }

                        }
                    }
                }

            }
        }
    }
}
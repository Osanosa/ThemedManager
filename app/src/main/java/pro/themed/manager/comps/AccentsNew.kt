package pro.themed.manager.comps

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import pro.themed.manager.MainActivity.Companion.isDark
import pro.themed.manager.R
import pro.themed.manager.buildOverlay
import pro.themed.manager.log
import pro.themed.manager.ui.theme.textcol
import pro.themed.manager.utils.showInterstitial
import kotlin.math.roundToInt

@SuppressLint("CommitPrefEdits")
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccentsAAPT() {
    val context = LocalContext.current
    var expanded by rememberSaveable { mutableStateOf(true) }

    Card(
        onClick = { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = textcol.copy(alpha = 0.05f), contentColor = textcol),
        modifier = Modifier.animateContentSize()
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = textcol.copy(alpha = 0.05f),
                contentColor = textcol
            ),
            modifier = Modifier
                .animateContentSize()
                .padding(8.dp)
        ) {
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

                    IconButton(
                        onClick = {
                            expanded = !expanded
                        }, colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (!expanded) {
                                Color.Gray
                            } else {
                                Color.Transparent
                            }
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info, contentDescription = null
                        )
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
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !expanded,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        //Tutorial text
                        Text(
                            text = "This is the accent picker, " + "it is linked via overlay to framework-res, " + "overlaying some of the colors, " + "mainly those that were before A12. " + "This won't change background colors, " + "you can use FakeMonet for that."
                        )
                    }
                    val color = Color.hsl(hue, saturation / 100, lightness / 100)
                    val hex = String.format("%08x".format(color.toArgb()))

                    androidx.compose.animation.AnimatedVisibility(
                        visible = expanded,
                        modifier = Modifier.wrapContentHeight(),
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                            val colors = mutableListOf<Color>()
                            for (h in 0..360 step 60) {
                                colors.add(Color.hsl(h.toFloat(), 1f, 0.5f))
                            }
                            Text(text = "hue is ${hue.toInt()}°")
                            Slider(modifier = Modifier
                                .background(
                                    Brush.horizontalGradient(
                                        colors = colors
                                    ), shape = CircleShape
                                )
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
                                        imageVector = ImageVector.vectorResource(R.drawable.fiber_manual_record_48px),
                                        contentDescription = null,
                                    )

                                })
                            Text(text = "saturation is ${saturation.toInt()}%")
                            Slider(modifier = Modifier
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.hsl(hue, 0f, 0.5f),
                                            Color.hsl(hue, 0.1f, 0.5f),
                                            Color.hsl(hue, 0.2f, 0.5f),
                                            Color.hsl(hue, 0.3f, 0.5f),
                                            Color.hsl(hue, 0.4f, 0.5f),
                                            Color.hsl(hue, 0.5f, 0.5f),
                                            Color.hsl(hue, 0.6f, 0.5f),
                                            Color.hsl(hue, 0.7f, 0.5f),
                                            Color.hsl(hue, 0.8f, 0.5f),
                                            Color.hsl(hue, 0.9f, 0.5f),
                                            Color.hsl(hue, 1f, 0.5f)
                                        )
                                    ), shape = CircleShape
                                )
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
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.hsl(hue, saturation / 100f, 0f),
                                            Color.hsl(hue, saturation / 100f, 0.5f),
                                            Color.hsl(hue, saturation / 100f, 1f)
                                        )
                                    ), shape = CircleShape
                                )

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

                            Row {
                                Button(onClick = {
                                    shell.run("cmd overlay disable themed.accent.generic ; pm uninstall themed.accent.generic")
                                    hue = 45f
                                    saturation = 100f
                                    lightness = 50f
                                }) {
                                    Text(text = "Reset")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    modifier = Modifier.fillMaxWidth(), onClick = {
                                        shell.run("cd /data/adb/modules/ThemedProject/onDemandCompiler/staticAccent")
                                        shell.run("""sed -i 's/>#\([0-9A-Fa-f]\{8\}\)</>#$hex</g' "res/values$isDark/colors.xml"""")
                                        buildOverlay("/data/adb/modules/ThemedProject/onDemandCompiler/staticAccent")
                                        shell.run("""cmd overlay enable themed.accent.generic""")
                                        showInterstitial(context) {}

                                    }, colors = ButtonDefaults.buttonColors(
                                        containerColor = color,
                                        contentColor = if (lightness > 50f) {
                                            Color.Black
                                        } else {
                                            Color.White
                                        }
                                    ), shape = CircleShape
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Build, $hex", color = if (lightness > 50f) {
                                                Color.Black
                                            } else {
                                                Color.White
                                            }
                                        )
                                        Icon(
                                            modifier = Modifier.height(24.dp),
                                            imageVector = ImageVector.vectorResource(id = R.drawable.arrow_right_alt_48px),
                                            contentDescription = ""
                                        )

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
package pro.themed.manager.comps

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.hsl
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.R
import pro.themed.manager.getOverlayList
import pro.themed.manager.overlayEnable
import pro.themed.manager.ui.theme.cardcol
import kotlin.math.roundToInt

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun FakeMonet(
) {
    Card(
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.cardcol),
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        elevation = (0.dp),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.cardcol
    ) {
        Column(modifier = Modifier) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .padding(start = 8.dp),
                    text = "FakeMonet",
                    fontSize = 24.sp
                )

                IconButton(modifier = Modifier, onClick = {
                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'themed.fakemonet' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
                }) {
                    Image(
                        painter = painterResource(R.drawable.reset),
                        contentDescription = null,
                    )
                }
            }
            val tilesize = ((LocalConfiguration.current.smallestScreenWidthDp - 16) / 11).dp

            var hue by rememberSaveable { mutableFloatStateOf(0f) }
            var saturation by rememberSaveable { mutableFloatStateOf(100f) }

            if (hue == 360f) {
                hue = 0f
            }
            if (saturation == 0f) {
                hue = 0f
            }

            val brightnessValues =
                listOf(0.99f, 0.95f, 0.90f, 0.80f, 0.70f, 0.60f, 0.496f, 0.40f, 0.30f, 0.20f, 0.10f)
            val labels =
                listOf("10", "50", "100", "200", "300", "400", "500", "600", "700", "800", "900")

            Surface {
                val context = LocalContext.current



                Column {

                    Text(text = "Current color palette")

                    Row(Modifier.fillMaxWidth()) {
                        brightnessValues.zip(labels).forEach { (brightness, label) ->
                            Surface(
                                modifier = Modifier
                                    .height(tilesize)
                                    //  .weight(1f)
                                    .aspectRatio(1f)
                                    .combinedClickable(onClick = {
                                        val color = hsl(hue, saturation / 100, brightness)
                                        val hex =
                                            String.format("%06X", (0xFFFFFF and color.toArgb()))
                                        val clipboard =
                                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Color", hex)
                                        clipboard.setPrimaryClip(clip)
                                        Toast
                                            .makeText(
                                                context,
                                                "c$label ($hex) copied to clipboard",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }), color = hsl(hue, saturation / 100, brightness)
                            ) {
                                Text(
                                    text = label,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.align(CenterVertically)
                                )
                            }
                        }
                    }


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
                        steps = 8,
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
                        steps = 4,
                        thumb = {
                            Image(
                                painter = painterResource(R.drawable.fiber_manual_record_48px),
                                contentDescription = null,
                            )
                        })

                    // Text(text = "fakemonet.n1h${hue.toInt()}s${saturation.toInt()}")

                    /*HeaderRowWithSwitch(header = "Override dark theme", onCheckedChange = {
                        if (it) {
                            isDark = ".dark"
                        } else {
                            isDark = ""
                        }
                    })*/
                    // Text(text = "Apply to")

                    Row {
                        Button(modifier = Modifier.weight(1f), onClick = {
                            overlayEnable("fakemonet.n1h${hue.toInt()}s${saturation.toInt()}")
                        }) {
                            Text(text = "N1")

                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        if (!getOverlayList().unsupportedOverlays.contains("n2")) {
                            Button(modifier = Modifier.weight(1f), onClick = {
                                overlayEnable("fakemonet.n2h${hue.toInt()}s${saturation.toInt()}")
                            }) {
                                Text(text = "N2")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Button(modifier = Modifier.weight(1f), onClick = {
                            overlayEnable("fakemonet.a1h${hue.toInt()}s${saturation.toInt()}")
                        }) {
                            Text(text = "A1")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        if (!getOverlayList().unsupportedOverlays.contains("a2") || !getOverlayList().unsupportedOverlays.contains(
                                "a3"
                            )
                        ) {
                            Button(modifier = Modifier.weight(1f), onClick = {
                                overlayEnable("fakemonet.a2h${hue.toInt()}s${saturation.toInt()}")
                            }) {
                                Text(text = "A2")
                            }
                            Spacer(modifier = Modifier.width(8.dp))

                            Button(modifier = Modifier.weight(1f), onClick = {
                                overlayEnable("fakemonet.a3h${hue.toInt()}s${saturation.toInt()}")
                            }) {
                                Text(text = "A3")
                            }
                        }
                    }

                }
            }
        }
    }
}

package pro.themed.manager.widgets

import android.content.Context
import android.os.Handler
import android.widget.Toast
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.action
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.jaredrummler.ktsh.Shell
import java.text.DecimalFormat

/* Import Glance Composables
 In the event there is a name clash with the Compose classes of the same name,
 you may rename the imports per https://kotlinlang.org/docs/packages.html#imports
 using the `as` keyword.

import androidx.glance.Button
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.text.Text
*/
class ClearCacheWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        provideContent {
            // create your AppWidget here
            GlanceTheme {
                Column(
                    modifier = GlanceModifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Clear Cache",
                        modifier = GlanceModifier.padding(12.dp)
                            .background(GlanceTheme.colors.primary)
                            .clickable(onClick = action {
                                val freebefore =
                                    Shell("su").run("df -k /data | awk 'NR==2{print \$4}'\n").stdout.toString()
                                        .replace(Regex("[^0-9]"), "").toLong()
                                Shell("su").run("pm trim-caches 100000g")
                                val freeafter =
                                    Shell("su").run("df -k /data | awk 'NR==2{print \$4}'\n").stdout.toString()
                                        .replace(Regex("[^0-9]"), "").toLong()
                                val difference: Float = freeafter.toFloat() - freebefore.toFloat()
                                val toast = when {
                                    difference > 1024 * 1024 -> "${
                                        DecimalFormat("#.##").format(
                                            difference / 1024 / 1024
                                        )
                                    }Gb"

                                    difference > 1024 -> "${
                                        DecimalFormat("#.##").format(
                                            difference / 1024
                                        )
                                    }Mb"

                                    else -> "${DecimalFormat("#").format(difference)}Kb"
                                }

                                Handler(context.mainLooper).post {
                                    Toast.makeText(
                                        context, "+$toast", Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }),
                        style = TextStyle(
                            color = GlanceTheme.colors.background,
                            textAlign = TextAlign.Center
                        )
                    )

                }
            }
        }
    }

}
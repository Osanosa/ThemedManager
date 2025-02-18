package pro.themed.manager.widgets

import android.content.Context
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
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.comps.downscalebydivisor

/* Import Glance Composables
 In the event there is a name clash with the Compose classes of the same name,
 you may rename the imports per https://kotlinlang.org/docs/packages.html#imports
 using the `as` keyword.

import androidx.glance.Button
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.text.Text
*/
class DownscaleWidget1x4 : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        provideContent {
            // create your AppWidget here
            GlanceTheme {
                Column(
                    modifier = GlanceModifier.wrapContentSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "1/4",
                        modifier =
                            GlanceModifier.padding(12.dp)
                                .background(GlanceTheme.colors.primary)
                                .wrapContentSize()
                                .clickable(
                                    onClick =
                                        action {
                                            if (
                                                Shell("su")
                                                    .run("wm size")
                                                    .stdout()
                                                    .contains("Override")
                                            ) {
                                                Shell("su").run("wm size reset ; wm density reset")
                                                Shell("su").run("killall com.android.systemui")
                                            } else {

                                                downscalebydivisor("4")
                                                Shell("su").run("killall com.android.systemui")
                                            }
                                        }
                                ),
                        style =
                            TextStyle(
                                color = GlanceTheme.colors.background,
                                textAlign = TextAlign.Center,
                            ),
                    )
                }
            }
        }
    }
}

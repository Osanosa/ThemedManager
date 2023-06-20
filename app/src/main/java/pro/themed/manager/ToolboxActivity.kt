package pro.themed.manager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.jaredrummler.ktsh.Shell.Companion.SU
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import pro.themed.manager.ui.theme.ThemedManagerTheme
import java.text.DecimalFormat


class ToolboxActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemedManagerTheme {
                ToolboxPage()

            }
        }
    }


    @Composable
    fun ToolboxPage() {
        val context = LocalContext.current
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.cardcol
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TopAppToolbox()

                Column(Modifier.padding(horizontal = 8.dp)) {

                    DisableOverlaysCard(context)

                    Spacer(Modifier.height(8.dp))
                    SystemUIRestart()
                    Spacer(Modifier.height(8.dp))

                    SystemThemeCard()
                    Spacer(Modifier.height(8.dp))

                    ClearAppCacheCard(context)
                    Spacer(Modifier.height(8.dp))

                    Dex2OatCard(context)
                    Spacer(Modifier.height(8.dp))
                    DownscaleCard(context = context)
                }
            }
        }

    }


    @Composable
    fun DisableOverlaysCard(context: Context) {
        Card(
            border = BorderStroke(
                width = 1.dp, color = MaterialTheme.colors.bordercol
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            elevation = (0.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(
                            horizontal = 16.dp, vertical = 4.dp
                        ),
                        text = stringResource(R.string.disable_overlays),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(R.string.disable_all_overlays_stock_android_themes_or_only_themed_project_s),
                    fontSize = 18.sp
                )

                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    var isDialogShown by remember { mutableStateOf(false) }
                    if (isDialogShown) {
                        AlertDialog(onDismissRequest = { /* Handle the dismissal here */ },
                            title = { Text(stringResource(R.string.disabling_all_overlays_may_cause_some_system_settings_to_reset)) },
                            text = { Text(stringResource(R.string.are_you_sure_you_want_to_proceed)) },
                            buttons = {
                                Row {


                                    Button(onClick = {
                                        MainScope().launch {
                                            async(Dispatchers.Default) {
                                                SU.run("""for ol in $(cmd overlay list | grep -E '[x]' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable "$""" + """ol"; done""")
                                                runOnUiThread {
                                                    Toast.makeText(
                                                        context,
                                                        getString(R.string.done),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                    }) { Text(text = stringResource(R.string.yes)) }
                                    Button(
                                        onClick = {
                                            isDialogShown = false
                                        },
                                    ) { Text(text = stringResource(R.string.no)) }
                                }
                            })
                    }

                    OutlinedButton(
                        onClick = {


                            isDialogShown = true


                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.cardcol, contentColor = Color.Red
                        )
                    ) {
                        Text(text = stringResource(R.string.all))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {

                            MainScope().launch {
                                async(Dispatchers.Default) {
                                    runOnUiThread {
                                        Toast.makeText(
                                            context,
                                            getString(R.string.process_started_now_wait),
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                    val myTrace: Trace = FirebasePerformance.getInstance()
                                        .newTrace("toolbox_overlay_reset_stock")
                                    myTrace.start()
                                    SU.run("""for ol in $(cmd overlay list | grep -E 'com.android.theme' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable "$""" + """ol"; done""")
                                    SU.run("""for ol in $(cmd overlay list | grep -E 'com.android.system' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable "$""" + """ol"; done""")
                                    SU.run("""for ol in $(cmd overlay list | grep -E 'com.accent' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable "$""" + """ol"; done""")
                                    myTrace.stop()

                                    runOnUiThread {

                                        Toast.makeText(
                                            context, getString(R.string.done), Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.cardcol,
                        )

                    ) {
                        Text(text = stringResource(R.string.stock))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {

                            MainScope().launch {
                                async(Dispatchers.Default) {

                                    runOnUiThread {
                                        Toast.makeText(
                                            context,
                                            getString(R.string.process_started_now_wait),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    val myTrace: Trace = FirebasePerformance.getInstance()
                                        .newTrace("toolbox_overlay_reset_themed")
                                    myTrace.start()
                                    SU.run(
                                        """for ol in $(cmd overlay list | grep -E '^.x..themed.'  | sed -E 's/^....//'); do cmd overlay disable "$""" + """ol"; done"""
                                    )
                                    myTrace.stop()

                                    runOnUiThread {
                                        Toast.makeText(
                                            context, getString(R.string.done), Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                }
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.cardcol,
                        )

                    ) {
                        Text(text = stringResource(R.string.themed))
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                }
            }
        }
    }

    @Composable
    fun SystemUIRestart() {
        Card(
            border = BorderStroke(
                width = 1.dp, color = MaterialTheme.colors.bordercol
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            elevation = (0.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(Modifier.padding(vertical = 2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(
                            horizontal = 16.dp, vertical = 8.dp
                        ),
                        text = stringResource(R.string.restart_systemui),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(R.string.restarts_systemui_in_case_if_your_rom_doesn_t_refreshes_it_automatically),
                    fontSize = 18.sp
                )

                Row {
                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = {
                            MainScope().launch {
                                async(Dispatchers.Default) {
                                    val myTrace: Trace = FirebasePerformance.getInstance()
                                        .newTrace("toolbox_restart_systemui")
                                    myTrace.start()
                                    SU.run("su -c killall com.android.systemui")

                                    myTrace.stop()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.cardcol,
                        )

                    ) {
                        Text(text = stringResource(R.string.restart_now))
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                }

            }
        }
    }

    @Composable
    fun SystemThemeCard() {
        Card(
            border = BorderStroke(
                width = 1.dp, color = MaterialTheme.colors.bordercol
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            elevation = (0.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(Modifier.padding(vertical = 2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(
                            horizontal = 16.dp, vertical = 8.dp
                        ),
                        text = stringResource(R.string.change_system_theme),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(R.string.change_a_theme_of_your_device),
                    fontSize = 18.sp
                )

                Row {
                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = {
                            MainScope().launch {
                                async(Dispatchers.Default) {
                                    SU.run("cmd uimode night no")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.cardcol,
                        )
                    ) {
                        Row {
                            Text(text = stringResource(R.string.light))

                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            MainScope().launch {
                                async(Dispatchers.Default) {
                                    SU.run("cmd uimode night yes")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.cardcol,
                        )

                    ) {
                        Text(text = stringResource(R.string.dark))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            MainScope().launch {
                                async(Dispatchers.Default) {
                                    SU.run("cmd uimode night auto")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.cardcol,
                        )

                    ) {
                        Text(text = stringResource(R.string.auto))
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                }
            }
        }
    }

    @Composable
    fun ClearAppCacheCard(context: Context) {
        Card(
            border = BorderStroke(
                width = 1.dp, color = MaterialTheme.colors.bordercol
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            elevation = (0.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(Modifier.padding(vertical = 2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(
                            horizontal = 16.dp, vertical = 8.dp
                        ),
                        text = stringResource(R.string.clear_app_caches),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(R.string.clears_cache_of_all_apps_data_is_safe),
                    fontSize = 18.sp
                )

                Row {
                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = {
                            MainScope().launch {
                                async(Dispatchers.Default) {
                                    val myTrace: Trace = FirebasePerformance.getInstance()
                                        .newTrace("toolbox_cache_clear")
                                    myTrace.start()

                                    val freebefore =
                                        SU.run("df -k /data | awk 'NR==2{print \$4}'\n").stdout.toString()
                                            .replace(Regex("[^0-9]"), "").toLong()
                                    SU.run("pm trim-caches 100000g")
                                    val freeafter =
                                        SU.run("df -k /data | awk 'NR==2{print \$4}'\n").stdout.toString()
                                            .replace(Regex("[^0-9]"), "").toLong()
                                    myTrace.stop()
                                    val difference: Float =
                                        freeafter.toFloat() - freebefore.toFloat()
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
                                    runOnUiThread {
                                        Toast.makeText(
                                            context, "+$toast", Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                }
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.cardcol,
                        )

                    ) {
                        Text(text = stringResource(R.string.clear_all))
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                }
            }
        }
    }

    @Composable
    fun Dex2OatCard(context: Context) {
        Card(
            border = BorderStroke(
                width = 1.dp, color = MaterialTheme.colors.bordercol
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            elevation = (0.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(Modifier.padding(vertical = 2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(
                            horizontal = 16.dp, vertical = 8.dp
                        ),
                        text = stringResource(R.string.dex2oat),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(R.string.pre_compiles_code_of_all_installed_apps_aswell_as_it_s_layouts_to_reduce_lag_ans_stutter),
                    fontSize = 18.sp
                )

                Row(modifier = Modifier) {
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {

                            MainScope().launch {
                                async(Dispatchers.Default) {
                                    runOnUiThread {
                                        Toast.makeText(
                                            context,
                                            getString(R.string.precompiling_dex_files),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    val SpeedTrace: Trace = FirebasePerformance.getInstance()
                                        .newTrace("toolbox_dex2oat_speed")
                                    SpeedTrace.start()
                                    SU.run("cmd package compile -m speed-profile -a")
                                    SU.run("cmd package compile -m speed-profile --secondary-dex -a")
                                    SpeedTrace.stop()
                                    runOnUiThread {
                                        Toast.makeText(
                                            context,
                                            getString(R.string.precompiling_layouts),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    val LayoutsTrace: Trace = FirebasePerformance.getInstance()
                                        .newTrace("toolbox_dex2oat_layouts")
                                    LayoutsTrace.start()
                                    SU.run("cmd package compile --compile-layouts -a")
                                    LayoutsTrace.stop()
                                    runOnUiThread {
                                        Toast.makeText(
                                            context, getString(R.string.done), Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.cardcol,
                        )

                    ) {
                        Text(text = stringResource(R.string.optimize))
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                }
            }
        }
    }

    @Preview
    @Composable
    fun DownscaleCard(context: Context = LocalContext.current) {
        Card(
            border = BorderStroke(
                width = 1.dp, color = MaterialTheme.colors.bordercol
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            elevation = (0.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(
                            horizontal = 16.dp, vertical = 8.dp
                        ),
                        text = stringResource(R.string.downscale),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(modifier = Modifier, onClick = {

                        SU.run("wm size reset ; wm density reset")


                    }) {
                        Image(
                            painter = painterResource(R.drawable.reset),
                            contentDescription = null,
                        )
                    }

                }
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(R.string.changes_resolution_and_density_please_click_custom_first),
                    fontSize = 18.sp
                )
                var customresShown by remember { mutableStateOf(false) }
                var customres by remember { mutableStateOf("") }
                if (customresShown) {
                    AlertDialog(onDismissRequest = { /* Handle the dismissal here */ },
                        title = { Text(stringResource(R.string.enter_your_custom_resolution)) },
                        text = { Text(stringResource(R.string.on_some_roms_such_as_miui_setting_resolution_to_smaller_then_480p_may_cause_issues_test_button_will_reset_size_after_10s)) },
                        buttons = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                TextField(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                    value = customres,
                                    singleLine = true,
                                    onValueChange = { customres = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    label = { Text("Enter custom resolution") })


                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Button(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        onClick = {

                                            downscalebynumber(width = customres)
                                            SU.run("sleep 10 ; wm size reset ; wm density reset")


                                        }) { Text(text = stringResource(R.string.test)) }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        onClick = {

                                            downscalebynumber(width = customres)


                                        },
                                    ) { Text(text = stringResource(R.string.apply)) }
                                }
                                Text(text = stringResource(R.string.close),
                                    modifier = Modifier.clickable { customresShown = false })
                                Spacer(modifier = Modifier.height(8.dp))

                            }
                        })
                }
                val switchState = rememberSaveable { mutableStateOf(true) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = stringResource(R.string.reset_to_defaults_after_10_seconds)
                    )
                    Switch(
                        checked = switchState.value,
                        onCheckedChange = { switchState.value = it })
                }

                Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                    OutlinedButton(
                        onClick = {

                            downscalebydivisor("2")
                            if (switchState.value) {
                                Thread.sleep(10000)
                                SU.run(" wm size reset ; wm density reset")

                            }
                        },
                        modifier = Modifier.wrapContentWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.cardcol,
                        )

                    ) {
                        Text(text = stringResource(R.string._0_5x))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {

                            downscalebydivisor("3")
                            if (switchState.value) {
                                Thread.sleep(10000)
                                SU.run(" wm size reset ; wm density reset")

                            }
                        },
                        modifier = Modifier.wrapContentWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.cardcol,
                        )

                    ) {
                        Text(text = stringResource(R.string._0_33x))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {

                            downscalebydivisor("4")

                            if (switchState.value) {
                                Thread.sleep(10000)
                                SU.run(" wm size reset ; wm density reset")
                            }

                        },
                        modifier = Modifier.wrapContentWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.cardcol,
                        )

                    ) {
                        Text(text = stringResource(R.string._0_25x))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            customresShown = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.cardcol,
                        )

                    ) {
                        Text(text = stringResource(R.string.custom))
                    }

                }
            }
        }
    }

    private fun downscalebydivisor(divisor: String) {

        SU.run(
            command = """
                # Set the number of division
                divisor=$divisor
                
                # Get the current resolution
                resolution=${'$'}(wm size | awk '{if (${'$'}1 == "Physical") {print ${'$'}3}}')
                
                # Extract the width and height values
                width=${'$'}(echo ${'$'}resolution | awk -F 'x' '{print ${'$'}1}')
                height=${'$'}(echo ${'$'}resolution | awk -F 'x' '{print ${'$'}2}')
                
                # Get the current density
                density=${'$'}(wm density | awk '{if (${'$'}1 == "Physical") {print ${'$'}3}}')
                
                # Check if width and height are odd
                if [ ${'$'}((width % ${'$'}divisor)) -eq 1 ]; then
                    width=${'$'}((${'$'}width - 1))
                fi
                
                if [ ${'$'}((height % ${'$'}divisor)) -eq 1 ]; then
                    height=${'$'}((${'$'}height - 1))
                fi
                
                # Divide the width, height and density by divisor
                width=${'$'}((${'$'}width / ${'$'}divisor))
                height=${'$'}((${'$'}height / ${'$'}divisor))
                density=${'$'}((${'$'}density / ${'$'}divisor))
                
                # Set the new resolution and density
                wm size ${'$'}width"x"${'$'}height
                wm density ${'$'}density
                """
        )

    }

    private fun downscalebynumber(width: String) {
        SU.run(
            command = """
                # Get current screen resolution
                resolution=${'$'}(wm size | awk '{if (${'$'}1 == "Physical") {print ${'$'}3}}')
                
                # Extract width and height from resolution
                width=${'$'}(echo ${'$'}resolution | cut -d'x' -f1 | cut -d':' -f2)
                height=${'$'}(echo ${'$'}resolution | cut -d'x' -f2)
                
                # Calculate aspect ratio
                aspect_ratio=${'$'}(echo "scale=2; ${'$'}width/${'$'}height" | bc)
                
                # Calculate new height
                new_height=${'$'}(echo "scale=0; $width/${'$'}aspect_ratio" | bc)
                
                # Get current density
                density=${'$'}(wm density | awk '{if (${'$'}1 == "Physical") {print ${'$'}3}}')
                
                
                
                #Calculate new density
                density_ratio=${'$'}(echo "scale=2; ${'$'}width/$width" | bc)
                new_density=${'$'}(echo "scale=0; ${'$'}density/${'$'}density_ratio" | bc)
                
                # Set new resolution
                wm size $width"x"${'$'}new_height
                
                # Set new density
                wm density ${'$'}(printf "%.0f" ${'$'}new_density)
                
                """
        )
    }


    @Composable
    fun TopAppToolbox() {
        val context = LocalContext.current
        val navController = rememberNavController()
        TopAppBar(elevation = 0.dp,
            title = { Text(text = stringResource(R.string.toolbox)) },
            backgroundColor = MaterialTheme.colors.cardcol,
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigateUp()
                    finish()
                }) {
                    Icon(Icons.Filled.ArrowBack, "backIcon")
                }
            },
            actions = {
                IconButton(onClick = {
                    context.startActivity(Intent(context, DebugActivity::class.java))

                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_bug_report_24),
                        contentDescription = "debug"
                    )
                }

                IconButton(onClick = {
                    context.startActivity(Intent(context, FaqActivity::class.java))
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_help_24),
                        contentDescription = "faq"
                    )
                }
            })
    }
}



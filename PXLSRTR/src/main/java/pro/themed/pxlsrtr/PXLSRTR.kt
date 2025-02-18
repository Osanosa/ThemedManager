package pro.themed.pxlsrtr

import android.hardware.display.*
import android.os.*
import android.os.Build.*
import android.view.*
import android.view.WindowInsets
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.google.firebase.analytics.*
import com.google.firebase.analytics.ktx.*
import com.google.firebase.ktx.*
import kotlin.math.*
import kotlin.system.*
import kotlinx.coroutines.*
import pro.themed.PXLSRTR.R
import pro.themed.pxlsrtr.ui.theme.*

class PXLSRTRactivity : ComponentActivity() {

    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        // Obtain the FirebaseAnalytics instance.
        analytics = Firebase.analytics

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.hide(WindowInsets.Type.systemBars())
        } else {
            @Suppress("DEPRECATION") // Older API support
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_IMMERSIVE or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PXLSRTRTheme {
                val context = applicationContext

                NoiseTest()
                LaunchedEffect(key1 = Unit) { showRewarded(context) {} }
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

@Preview
@Composable
fun NoiseTest() {
    var showDialog by remember { mutableStateOf(true) }

    var currentFrame by remember { mutableIntStateOf(0) }
    val emptyFrame = R.drawable.emptyframe

    var isNoiseChecked by remember { mutableStateOf(false) }
    var isGradientsChecked by remember { mutableStateOf(false) }
    var isBlobsChecked by remember { mutableStateOf(false) }
    var isHorizontalStripesChecked by remember { mutableStateOf(false) }
    var isVerticalStripesChecked by remember { mutableStateOf(false) }
    var isHorizontalLinesChecked by remember { mutableStateOf(false) }
    var isVerticalLinesChecked by remember { mutableStateOf(false) }
    var isHorizontalGradientsChecked by remember { mutableStateOf(false) }
    var isVerticalGradientsChecked by remember { mutableStateOf(false) }
    var isHueChecked by remember { mutableStateOf(false) }
    var isHueStrobeChecked by remember { mutableStateOf(false) }
    var isMonochromeChecked by remember { mutableStateOf(false) }
    var isMonochromeNoiseChecked by remember { mutableStateOf(false) }
    var isMonochromeStrobeChecked by remember { mutableStateOf(false) }

    fun buildList(): List<Int> {
        var list = buildList {
            if (isNoiseChecked) addAll(colorNoiseList)
            if (isGradientsChecked) addAll(colorGradientsList)
            if (isBlobsChecked) addAll(colorBlobsList)
            if (isHorizontalStripesChecked) addAll(horizontalStripesList)
            if (isVerticalStripesChecked) addAll(verticalStripesList)
            if (isHorizontalLinesChecked) addAll(horizontalLinesList)
            if (isVerticalLinesChecked) addAll(verticalLinesList)
            if (isHorizontalGradientsChecked) addAll(horizontalGradientsList)
            if (isVerticalGradientsChecked) addAll(verticalGradientsList)
            if (isHueChecked) addAll(hueList)
            if (isHueStrobeChecked) addAll(hueStrobeList)
            if (isMonochromeChecked) addAll(monochromeList)
            if (isMonochromeNoiseChecked) addAll(monochromeNoiseList)
            if (isMonochromeStrobeChecked) addAll(monochromeStrobeList)
            if (
                !isNoiseChecked &&
                    !isGradientsChecked &&
                    !isBlobsChecked &&
                    !isHorizontalStripesChecked &&
                    !isVerticalStripesChecked &&
                    !isHorizontalLinesChecked &&
                    !isVerticalLinesChecked &&
                    !isHorizontalGradientsChecked &&
                    !isVerticalGradientsChecked &&
                    !isHueChecked &&
                    !isHueStrobeChecked &&
                    !isMonochromeChecked &&
                    !isMonochromeNoiseChecked &&
                    !isMonochromeStrobeChecked
            )
                add(emptyFrame)
        }
        return list
    }

    var currentList by remember { mutableStateOf(buildList()) }
    var delay by remember { mutableLongStateOf(1000 / 60) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            delay =
                (1000 /
                    (context
                        .getSystemService(DisplayManager::class.java)
                        .displays[0]
                        .refreshRate
                        .roundToLong()))
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            currentFrame = (currentFrame + 1) % currentList.size
            delay(delay)
        }
    }
    val verticalScroll = rememberScrollState()
    var minutes by remember { mutableIntStateOf(10) }
    LaunchedEffect(minutes) {
        delay((minutes * 60 * 1000).toLong())
        PXLSRTRactivity().moveTaskToBack(true)
        exitProcess(-1)
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            CookieCard {
                Text(
                    "EPILEPSY WARNING",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
                AdmobBanner(LocalContext.current)
                @Composable
                fun CheckRow(name: String, isChecked: Boolean, onClick: () -> Unit) {
                    Row(
                        Modifier.clickable {
                                onClick()
                                minutes = minutes
                            }
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(isChecked, onCheckedChange = { onClick() })
                        Text(name)
                    }
                }
                Box {
                    Column(Modifier.fillMaxHeight(0.5f).verticalScroll(verticalScroll)) {
                        CheckRow("Noise", isNoiseChecked) {
                            isNoiseChecked = !isNoiseChecked
                            currentList = buildList()
                        }
                        CheckRow("Gradients", isGradientsChecked) {
                            isGradientsChecked = !isGradientsChecked
                            currentList = buildList()
                        }
                        CheckRow("Blobs", isBlobsChecked) {
                            isBlobsChecked = !isBlobsChecked
                            currentList = buildList()
                        }
                        CheckRow("Horizontal Stripes", isHorizontalStripesChecked) {
                            isHorizontalStripesChecked = !isHorizontalStripesChecked
                            currentList = buildList()
                        }
                        CheckRow("Vertical Stripes", isVerticalStripesChecked) {
                            isVerticalStripesChecked = !isVerticalStripesChecked
                            currentList = buildList()
                        }
                        CheckRow("Horizontal Lines", isHorizontalLinesChecked) {
                            isHorizontalLinesChecked = !isHorizontalLinesChecked
                            currentList = buildList()
                        }
                        CheckRow("Vertical Lines", isVerticalLinesChecked) {
                            isVerticalLinesChecked = !isVerticalLinesChecked
                            currentList = buildList()
                        }
                        CheckRow("Horizontal Gradients", isHorizontalGradientsChecked) {
                            isHorizontalGradientsChecked = !isHorizontalGradientsChecked
                            currentList = buildList()
                        }
                        CheckRow("Vertical Gradients", isVerticalGradientsChecked) {
                            isVerticalGradientsChecked = !isVerticalGradientsChecked
                            currentList = buildList()
                        }
                        CheckRow("Hue", isHueChecked) {
                            isHueChecked = !isHueChecked
                            currentList = buildList()
                        }
                        CheckRow("Hue Strobe", isHueStrobeChecked) {
                            isHueStrobeChecked = !isHueStrobeChecked
                            currentList = buildList()
                        }
                        CheckRow("Monochrome", isMonochromeChecked) {
                            isMonochromeChecked = !isMonochromeChecked
                            currentList = buildList()
                        }
                        CheckRow("Monochrome Noise", isMonochromeNoiseChecked) {
                            isMonochromeNoiseChecked = !isMonochromeNoiseChecked
                            currentList = buildList()
                        }
                        CheckRow("Monochrome Strobe", isMonochromeStrobeChecked) {
                            isMonochromeStrobeChecked = !isMonochromeStrobeChecked
                            currentList = buildList()
                        }
                    }
                    Column(
                        modifier =
                            Modifier.align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors =
                                            listOf(
                                                Color.Transparent,
                                                MaterialTheme.colorScheme.surface
                                            ),
                                        startY = 0f,
                                        endY = 1000f,
                                        tileMode = TileMode.Clamp
                                    ),
                                    alpha =
                                        1f -
                                            (verticalScroll.value.toFloat() /
                                                verticalScroll.maxValue.toFloat())
                                )
                    ) {}
                    Column(
                        modifier =
                            Modifier.graphicsLayer { scaleY = -1f }.align(Alignment.TopCenter)
                    ) {
                        Column(
                            modifier =
                                Modifier.fillMaxWidth()
                                    .height(100.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colors =
                                                listOf(
                                                    Color.Transparent,
                                                    MaterialTheme.colorScheme.surface
                                                ),
                                            startY = 0f,
                                            endY = 1000f,
                                            tileMode = TileMode.Clamp
                                        ),
                                        alpha =
                                            (verticalScroll.value.toFloat() /
                                                verticalScroll.maxValue.toFloat())
                                    )
                        ) {}
                    }
                }
                Column(Modifier.padding(8.dp)) {
                    Text("Turn off after: $minutes minutes", fontWeight = FontWeight.Bold)
                    Slider(
                        value = minutes.toFloat(),
                        onValueChange = { minutes = it.toInt() },
                        valueRange = 0f..60f,
                        steps = 59
                    )
                }
                Row(
                    Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = {
                            isNoiseChecked = true
                            isGradientsChecked = true
                            isBlobsChecked = true
                            isHorizontalStripesChecked = true
                            isVerticalStripesChecked = true
                            isHorizontalLinesChecked = true
                            isVerticalLinesChecked = true
                            isHorizontalGradientsChecked = true
                            isVerticalGradientsChecked = true
                            isHueChecked = true
                            isHueStrobeChecked = true
                            isMonochromeChecked = true
                            isMonochromeNoiseChecked = true
                            isMonochromeStrobeChecked = true
                            currentList = buildList()
                            showDialog = false
                            minutes = minutes
                        }
                    ) {
                        Text("Enable all")
                    }
                    Button(
                        onClick = {
                            isNoiseChecked = true
                            isGradientsChecked = true
                            isBlobsChecked = true
                            isHorizontalStripesChecked = true
                            isVerticalStripesChecked = true
                            isHorizontalLinesChecked = true
                            isVerticalLinesChecked = true
                            isHorizontalGradientsChecked = true
                            isVerticalGradientsChecked = true
                            isHueChecked = true
                            isHueStrobeChecked = true
                            isMonochromeChecked = true
                            isMonochromeNoiseChecked = true
                            isMonochromeStrobeChecked = true
                            currentList = buildList().shuffled()
                            showDialog = false
                            minutes = minutes
                        }
                    ) {
                        Text("Randomize!")
                    }
                }
            }
        }
    }
    val imageBitmap =
        ImageBitmap.imageResource(
            currentList[if (currentList.size <= currentFrame) 0 else currentFrame]
        )

    Canvas(modifier = Modifier.fillMaxSize().clickable { showDialog = true }) {
        val paint =
            Paint().asFrameworkPaint().apply {
                isAntiAlias = false
                shader = ImageShader(imageBitmap, TileMode.Mirror, TileMode.Mirror)
            }

        drawIntoCanvas { it.nativeCanvas.drawPaint(paint) }
        paint.reset()
    }
}

package pro.themed.pxlsrtr

import android.os.*
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
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.window.*
import com.google.firebase.analytics.*
import com.google.firebase.analytics.ktx.*
import com.google.firebase.ktx.*
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
                (View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PXLSRTRTheme {
                val context = applicationContext


                NoiseTest()
                LaunchedEffect(key1 = Unit) {
                    showRewarded(context) {}
                }
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

@Preview
@Composable
fun NoiseTest() {
    var showDialog by remember { mutableStateOf(true) }
    val imageList = listOf(
        ImageBitmap.imageResource(R.drawable.colornoice_00000),
        ImageBitmap.imageResource(R.drawable.colornoice_00001),
        ImageBitmap.imageResource(R.drawable.colornoice_00002),
        ImageBitmap.imageResource(R.drawable.colornoice_00003),
        ImageBitmap.imageResource(R.drawable.colornoice_00004),
        ImageBitmap.imageResource(R.drawable.colornoice_00005),
        ImageBitmap.imageResource(R.drawable.colornoice_00006),
        ImageBitmap.imageResource(R.drawable.colornoice_00007),
        ImageBitmap.imageResource(R.drawable.colornoice_00008),
        ImageBitmap.imageResource(R.drawable.colornoice_00009),
        ImageBitmap.imageResource(R.drawable.colornoice_00010),
        ImageBitmap.imageResource(R.drawable.colornoice_00011),
        ImageBitmap.imageResource(R.drawable.colornoice_00012),
        ImageBitmap.imageResource(R.drawable.colornoice_00013),
        ImageBitmap.imageResource(R.drawable.colornoice_00014),
        ImageBitmap.imageResource(R.drawable.colornoice_00015),
        ImageBitmap.imageResource(R.drawable.colornoice_00016),
        ImageBitmap.imageResource(R.drawable.colornoice_00017),
        ImageBitmap.imageResource(R.drawable.colornoice_00018),
        ImageBitmap.imageResource(R.drawable.colornoice_00019),
        ImageBitmap.imageResource(R.drawable.colornoice_00020),
        ImageBitmap.imageResource(R.drawable.colornoice_00021),
        ImageBitmap.imageResource(R.drawable.colornoice_00022),
        ImageBitmap.imageResource(R.drawable.colornoice_00023),
        ImageBitmap.imageResource(R.drawable.colornoice_00024),
        ImageBitmap.imageResource(R.drawable.colornoice_00025),
        ImageBitmap.imageResource(R.drawable.colornoice_00026),
        ImageBitmap.imageResource(R.drawable.colornoice_00027),
        ImageBitmap.imageResource(R.drawable.colornoice_00028),
        ImageBitmap.imageResource(R.drawable.colornoice_00029),
        ImageBitmap.imageResource(R.drawable.colornoice_00030),
        ImageBitmap.imageResource(R.drawable.colornoice_00031),
        ImageBitmap.imageResource(R.drawable.colornoice_00032),
        ImageBitmap.imageResource(R.drawable.colornoice_00033),
        ImageBitmap.imageResource(R.drawable.colornoice_00034),
        ImageBitmap.imageResource(R.drawable.colornoice_00035),
        ImageBitmap.imageResource(R.drawable.colornoice_00036),
        ImageBitmap.imageResource(R.drawable.colornoice_00037),
        ImageBitmap.imageResource(R.drawable.colornoice_00038),
        ImageBitmap.imageResource(R.drawable.colornoice_00039),
        ImageBitmap.imageResource(R.drawable.colornoice_00040),
        ImageBitmap.imageResource(R.drawable.colornoice_00041),
        ImageBitmap.imageResource(R.drawable.colornoice_00042),
        ImageBitmap.imageResource(R.drawable.colornoice_00043),
        ImageBitmap.imageResource(R.drawable.colornoice_00044),
        ImageBitmap.imageResource(R.drawable.colornoice_00045),
        ImageBitmap.imageResource(R.drawable.colornoice_00046),
        ImageBitmap.imageResource(R.drawable.colornoice_00047),
        ImageBitmap.imageResource(R.drawable.colornoice_00048),
        ImageBitmap.imageResource(R.drawable.colornoice_00049),
        ImageBitmap.imageResource(R.drawable.colornoice_00050),
        ImageBitmap.imageResource(R.drawable.colornoice_00051),
        ImageBitmap.imageResource(R.drawable.colornoice_00052),
        ImageBitmap.imageResource(R.drawable.colornoice_00053),
        ImageBitmap.imageResource(R.drawable.colornoice_00054),
        ImageBitmap.imageResource(R.drawable.colornoice_00055),
        ImageBitmap.imageResource(R.drawable.colornoice_00056),
        ImageBitmap.imageResource(R.drawable.colornoice_00057),
        ImageBitmap.imageResource(R.drawable.colornoice_00058),
        ImageBitmap.imageResource(R.drawable.colornoice_00059),
        ImageBitmap.imageResource(R.drawable.colornoice_00060),
        ImageBitmap.imageResource(R.drawable.colornoice_00061),
        ImageBitmap.imageResource(R.drawable.colornoice_00062),
        ImageBitmap.imageResource(R.drawable.colornoice_00063),
        ImageBitmap.imageResource(R.drawable.colornoice_00064),
        ImageBitmap.imageResource(R.drawable.colornoice_00065),
        ImageBitmap.imageResource(R.drawable.colornoice_00066),
        ImageBitmap.imageResource(R.drawable.colornoice_00067),
        ImageBitmap.imageResource(R.drawable.colornoice_00068),
        ImageBitmap.imageResource(R.drawable.colornoice_00069),
        ImageBitmap.imageResource(R.drawable.colornoice_00070),
        ImageBitmap.imageResource(R.drawable.colornoice_00071),
        ImageBitmap.imageResource(R.drawable.colornoice_00072),
        ImageBitmap.imageResource(R.drawable.colornoice_00073),
        ImageBitmap.imageResource(R.drawable.colornoice_00074),
        ImageBitmap.imageResource(R.drawable.colornoice_00075),
        ImageBitmap.imageResource(R.drawable.colornoice_00076),
        ImageBitmap.imageResource(R.drawable.colornoice_00077),
        ImageBitmap.imageResource(R.drawable.colornoice_00078),
        ImageBitmap.imageResource(R.drawable.colornoice_00079),
        ImageBitmap.imageResource(R.drawable.colornoice_00080),
        ImageBitmap.imageResource(R.drawable.colornoice_00081),
        ImageBitmap.imageResource(R.drawable.colornoice_00082),
        ImageBitmap.imageResource(R.drawable.colornoice_00083),
        ImageBitmap.imageResource(R.drawable.colornoice_00084),
        ImageBitmap.imageResource(R.drawable.colornoice_00085),
        ImageBitmap.imageResource(R.drawable.colornoice_00002),
        ImageBitmap.imageResource(R.drawable.colornoice_00087),
        ImageBitmap.imageResource(R.drawable.colornoice_00088),
        ImageBitmap.imageResource(R.drawable.colornoice_00089),
        ImageBitmap.imageResource(R.drawable.colornoice_00090),
        ImageBitmap.imageResource(R.drawable.colornoice_00091),
        ImageBitmap.imageResource(R.drawable.colornoice_00092),
        ImageBitmap.imageResource(R.drawable.colornoice_00093),
        ImageBitmap.imageResource(R.drawable.colornoice_00094),
        ImageBitmap.imageResource(R.drawable.colornoice_00095),
        ImageBitmap.imageResource(R.drawable.colornoice_00096),
        ImageBitmap.imageResource(R.drawable.colornoice_00097),
        ImageBitmap.imageResource(R.drawable.colornoice_00098),
        ImageBitmap.imageResource(R.drawable.colornoice_00099),
        ImageBitmap.imageResource(R.drawable.colornoice_00100),
        ImageBitmap.imageResource(R.drawable.colornoice_00101),
        ImageBitmap.imageResource(R.drawable.colornoice_00102),
        ImageBitmap.imageResource(R.drawable.colornoice_00103),
        ImageBitmap.imageResource(R.drawable.colornoice_00104),
        ImageBitmap.imageResource(R.drawable.colornoice_00105),
        ImageBitmap.imageResource(R.drawable.colornoice_00106),
        ImageBitmap.imageResource(R.drawable.colornoice_00107),
        ImageBitmap.imageResource(R.drawable.colornoice_00108),
        ImageBitmap.imageResource(R.drawable.colornoice_00109),
        ImageBitmap.imageResource(R.drawable.colornoice_00110),
        ImageBitmap.imageResource(R.drawable.colornoice_00111),
        ImageBitmap.imageResource(R.drawable.colornoice_00112),
        ImageBitmap.imageResource(R.drawable.colornoice_00113),
        ImageBitmap.imageResource(R.drawable.colornoice_00114),
        ImageBitmap.imageResource(R.drawable.colornoice_00115),
        ImageBitmap.imageResource(R.drawable.colornoice_00116),
        ImageBitmap.imageResource(R.drawable.colornoice_00117),
        ImageBitmap.imageResource(R.drawable.colornoice_00118),
        ImageBitmap.imageResource(R.drawable.colornoice_00119),
        ImageBitmap.imageResource(R.drawable.colornoice_00120),
        ImageBitmap.imageResource(R.drawable.colornoice_00121),
        ImageBitmap.imageResource(R.drawable.colornoice_00122),
        ImageBitmap.imageResource(R.drawable.colornoice_00123),
        ImageBitmap.imageResource(R.drawable.colornoice_00124)
    )

    var currentFrame by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            currentFrame = (currentFrame + 1) % imageList.size
            delay(10L)  // Adjust delay as needed
        }
    }
    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false },
        ) {
            CookieCard {

            }
        }
    }
    Canvas(modifier = Modifier
        .fillMaxSize()
        .clickable { showDialog = true }) {

        val paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = false
            shader = ImageShader(imageList[currentFrame], TileMode.Repeated, TileMode.Repeated)
        }

        drawIntoCanvas {
            it.nativeCanvas.drawPaint(paint)
        }
        paint.reset()
    }
}
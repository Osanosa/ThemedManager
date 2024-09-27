package pro.themed.pxlsrtr

import android.hardware.display.*
import android.os.Build.*
import android.service.dreams.*
import androidx.annotation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import androidx.lifecycle.*
import androidx.savedstate.*
import kotlinx.coroutines.*
import kotlin.math.*
import kotlin.system.*

class DreamServiceCompat : DreamService(), SavedStateRegistryOwner, ViewModelStoreOwner {

    @Suppress("LeakingThis")
    private val lifecycleRegistry = LifecycleRegistry(this)

    @Suppress("LeakingThis")
    private val savedStateRegistryController = SavedStateRegistryController.create(this).apply {
        performAttach()
    }

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore = ViewModelStore()
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    @CallSuper
    override fun onCreate() {
        super.onCreate()

        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        val view = ComposeView(this)
        // Set composition strategy
        view.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

        // Inject dependencies normally added by appcompat activities
        view.setViewTreeLifecycleOwner(this)
        view.setViewTreeViewModelStoreOwner(this)
        view.setViewTreeSavedStateRegistryOwner(this)

        // Set content composable
        view.setContent({
            Random()
        })

        // Set content view
        setContentView(view)

        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()

        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

}

@Preview
@Composable
fun Random() {

    var currentFrame by remember { mutableIntStateOf(0) }

    val currentList by remember {
        mutableStateOf(
            colorNoiseList + colorGradientsList + colorBlobsList + horizontalStripesList + verticalStripesList + horizontalLinesList + verticalLinesList + hueList + hueStrobeList + monochromeList + monochromeNoiseList + monochromeStrobeList
        )
    }
    var delay by remember { mutableLongStateOf(1000 / 60) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            delay =
                (1000 / (context.getSystemService(_root_ide_package_.android.hardware.display.DisplayManager::class.java).displays[0].refreshRate.roundToLong()))
        }

    }
    LaunchedEffect(Unit) {
        while (true) {
            currentFrame = (currentFrame + 1) % currentList.size
            delay(delay)
        }
    }

    val imageBitmap = ImageBitmap.imageResource(currentList.shuffled()[currentFrame])

    Canvas(
        modifier = _root_ide_package_.androidx.compose.ui.Modifier.fillMaxSize()
    ) {
        val paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = false
            shader = ImageShader(

                imageBitmap, TileMode.Mirror, TileMode.Mirror
            )
        }

        drawIntoCanvas {
            it.nativeCanvas.drawPaint(paint)
        }
        paint.reset()
    }

}


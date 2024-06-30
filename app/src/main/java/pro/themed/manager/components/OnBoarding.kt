package pro.themed.manager.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pro.themed.manager.ui.theme.contentcol

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OnBoarding(image: Int, text: String) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.smallestScreenWidthDp.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = null,
                modifier = Modifier.size(screenWidth)
            )

            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 30.dp),
                textAlign = TextAlign.Start,
                fontSize = 16.sp,
                color = contentcol
            )
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxHeight()
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 80.dp)
                    .aspectRatio(1f)
                    .weight(0.8f)
                    .fillMaxSize()
            )
            Text(
                text = text,
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .weight(0.2f),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = contentcol
            )
        }
    }
}
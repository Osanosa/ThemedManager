@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class
)

package pro.themed.manager.comps

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.R
import pro.themed.manager.cardcol
import pro.themed.manager.overlayEnable

@Preview
@Composable
fun FontsTab() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp),
        color = MaterialTheme.colors.cardcol
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

            Spacer(modifier = Modifier.height(8.dp))
            Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colors.cardcol,
                onClick = { overlayEnable("font.jetbrainsmono") }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.font_jetbrains_mono),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.cardcol,
                onClick = { overlayEnable("font.opensans") }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.font_opensans),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colors.cardcol,
                onClick = { overlayEnable("font.nunito") }

            ) {
                Image(
                    painter = painterResource(id = R.drawable.font_nunito),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.cardcol,
                onClick = {
                    Shell.SU.run("for ol in \$(cmd overlay list | grep -E 'themed.font.' | grep  -E '^.x'  | sed -E 's/^....//'); do cmd overlay disable \"\$ol\"; done")
                }) {
                Image(
                    painter = painterResource(id = R.drawable.reset),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

        }
    }

}

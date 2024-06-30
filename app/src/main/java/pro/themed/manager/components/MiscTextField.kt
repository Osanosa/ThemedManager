package pro.themed.manager.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.R
import pro.themed.manager.buildOverlay
import pro.themed.manager.log

@Composable
fun MiscTextField(
    input: String,
    path: String,
    resource: String,
    label: String = "TEST",
    file: String,
    overlay: String,
    modifier: Modifier,
) {
    var input by remember { mutableStateOf(input) }
    OutlinedTextField(modifier = modifier.padding(bottom = 2.dp),
        value = input,
        singleLine = true,
        onValueChange = {
            if (file == "integers") {
                Shell("su").run(
                    """sed -i 's/<integer name="$resource">[^<]*/<integer name="$resource">${it}/g' $path/res/values/$file.xml"""
                ).log(); input = it
            } else if (file == "dimens") {
                Shell("su").run(
                    """sed -i 's/<dimens name="$resource">[^<]*/<dimen name="$resource">${it}dip/g' $path/res/values/$file.xml"""
                ).log(); input = it
            }
        },
        placeholder = { Text("Enter your value", Modifier.basicMarquee()) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

        trailingIcon = {
            IconButton(onClick = {
                buildOverlay(path)
                Shell("su").run("""cmd overlay enable $overlay""")

            }) {
                Icon(
                    painter = painterResource(id = R.drawable.move_up_24px),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        label = { Text(label, Modifier.basicMarquee()) })
}

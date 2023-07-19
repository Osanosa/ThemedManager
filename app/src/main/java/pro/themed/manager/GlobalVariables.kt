package pro.themed.manager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


object GlobalVariables {
    var myBoolean by mutableStateOf(false)
    var modulePath by mutableStateOf("/data/adb/modules/ThemedProject")

}
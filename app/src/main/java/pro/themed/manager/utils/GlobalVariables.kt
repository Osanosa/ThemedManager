package pro.themed.manager.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


object GlobalVariables {
    var modulePath by mutableStateOf("/data/adb/modules/ThemedProject")
var isdownloaded by mutableStateOf( false)
}
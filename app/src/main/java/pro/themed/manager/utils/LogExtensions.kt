package pro.themed.manager.utils// Create a LogExtensions.kt file or add this code to an existing Kotlin file.

import android.util.Log


fun Any.log(TAG: String = "Simple Log") {
    Log.d(TAG, this.toString())
}

fun Any.logError(TAG: String = "Simple Log") {
    Log.e(TAG, this.toString())
}

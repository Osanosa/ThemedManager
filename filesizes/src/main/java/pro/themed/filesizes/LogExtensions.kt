package pro.themed.filesizes // Create a LogExtensions.kt file or add this code to an existing
                             // Kotlin file.

import android.util.*

fun Any.log(TAG: String = "Simple Log") {
    Log.d(TAG, this.toString())
}

fun Any.logError(TAG: String = "Simple Log") {
    Log.e(TAG, this.toString())
}

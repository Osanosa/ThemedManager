// Create a LogExtensions.kt file or add this code to an existing Kotlin file.

import android.util.Log

private const val TAG = "SimpleLog" // Replace with your desired tag

fun Any.log() {
    Log.d(TAG, this.toString())
}

fun Any.logError() {
    Log.e(TAG, this.toString())
}

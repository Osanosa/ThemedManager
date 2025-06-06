package pro.themed.audhdlauncher.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

fun Context.getActivityOrNull(): Activity? {
    var context = this
    if (context is Activity) return context
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }

    return null
}

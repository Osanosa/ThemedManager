package pro.themed.manager.utils

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pro.themed.manager.MainActivity

fun overlayEnable(overlayname: String) {
    CoroutineScope(Dispatchers.IO).launch {
        val overlay = Shell("su").run("su -c cmd overlay").stdout()

        if ("exclusive" in overlay) {
            Shell.SH.run("su -c cmd overlay enable-exclusive --category themed.$overlayname")
        } else {
            Shell("su").run("su -c cmd overlay enable themed.$overlayname")
        }

        val sharedPreferences = SharedPreferencesManager.getSharedPreferences()
        val restart_system_ui: Boolean = sharedPreferences.getBoolean("restart_system_ui", false)

        if (restart_system_ui) {
            Shell("su").run("su -c killall com.android.systemui")
        }

        Firebase.analytics.logEvent("Overlay_Selected") { param("Overlay_Name", overlayname) }
        MainActivity.overlayList = fetchOverlayList()
    }
}

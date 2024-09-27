package pro.themed.manager.utils

import android.content.*
import com.jaredrummler.ktsh.*

data class OverlayListData(
    val overlayList: List<String>,
    val unsupportedOverlays: List<String>,
    val enabledOverlays: List<String>,
    val disabledOverlays: List<String>,
)

fun fetchOverlayList(): OverlayListData {
    return try {
        val result = Shell("su").run("cmd overlay list").stdout()
        val overlayList = result.lines().filter { it.contains("themed") }.sorted()
        val unsupportedOverlays = overlayList.filter { it.contains("---") }
        val enabledOverlays = overlayList.filter { it.contains("[x]") }
        val disabledOverlays = overlayList.filter { it.contains("[ ]") }
        OverlayListData(overlayList, unsupportedOverlays, enabledOverlays, disabledOverlays)
    } catch (e: Exception) {
        return OverlayListData(emptyList(), emptyList(), emptyList(), emptyList())
    }

}

object SharedPreferencesManager {
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    }

    fun getSharedPreferences(): SharedPreferences {
        return sharedPreferences
    }
}
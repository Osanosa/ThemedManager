package pro.themed.manager

import android.app.Application
import androidx.preference.PreferenceManager

class DataStore : Application() {

    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    val preferenceKey = "my_preference"

    fun setPreference(value: Boolean) {
        sharedPreferences.edit().putBoolean(preferenceKey, value).apply()
    }

    fun getPreference(): Boolean {
        return sharedPreferences.getBoolean(preferenceKey, false)
    }
}

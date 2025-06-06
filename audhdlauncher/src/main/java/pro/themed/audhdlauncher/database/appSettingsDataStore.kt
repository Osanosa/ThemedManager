package pro.themed.audhdlauncher.database

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// Extension property for Settings DataStore
private val Context.appSettingsDataStore by preferencesDataStore(name = "app_settings")

/** DataStore for application settings with generic implementation */
class AppSettingsDataStore(private val context: Context) {

    // Settings definitions
    sealed class Setting<T> {

        abstract val key: Preferences.Key<T>
        abstract val defaultValue: T
    }

    // Boolean settings
    sealed class BoolSetting(
        override val key: Preferences.Key<Boolean>,
        override val defaultValue: Boolean,
    ) : Setting<Boolean>()

    // Int settings
    sealed class IntSetting(
        override val key: Preferences.Key<Int>,
        override val defaultValue: Int,
    ) : Setting<Int>()

    // Float settings
    sealed class FloatSetting(
        override val key: Preferences.Key<Float>,
        override val defaultValue: Float,
    ) : Setting<Float>()

    // String settings
    sealed class StringSetting(
        override val key: Preferences.Key<String>,
        override val defaultValue: String,
    ) : Setting<String>()

    // Available settings
    object Settings {

        // UI Settings
        object DarkMode :
            BoolSetting(key = booleanPreferencesKey("dark_mode"), defaultValue = false)

        object GridColumns : IntSetting(key = intPreferencesKey("grid_columns"), defaultValue = 4)

        object AccentColor :
            StringSetting(key = stringPreferencesKey("accent_color"), defaultValue = "#1428A0")

        // Behavior Settings
        object SortAlphabetically :
            BoolSetting(key = booleanPreferencesKey("sort_alphabetically"), defaultValue = false)

        object DefaultCategory :
            StringSetting(key = stringPreferencesKey("default_category"), defaultValue = "All")

        // Advanced Settings
        object DebugMode :
            BoolSetting(key = booleanPreferencesKey("debug_mode"), defaultValue = false)

        object BackupFrequency :
            IntSetting(key = intPreferencesKey("backup_frequency"), defaultValue = 7)
    }

    // All available settings
    private val allSettings =
        listOf(
            Settings.DarkMode,
            Settings.GridColumns,
            Settings.AccentColor,
            Settings.SortAlphabetically,
            Settings.DefaultCategory,
            Settings.DebugMode,
            Settings.BackupFrequency,
        )

    // Initialize with default values if not set
    init {
        CoroutineScope(Dispatchers.IO).launch {
            // Initialize settings with default values if they don't exist
            context.appSettingsDataStore.edit { preferences ->
                allSettings.forEach { setting ->
                    when (setting) {
                        is BoolSetting ->
                            if (!preferences.contains(setting.key))
                                preferences[setting.key] = setting.defaultValue

                        is IntSetting ->
                            if (!preferences.contains(setting.key))
                                preferences[setting.key] = setting.defaultValue

                        is FloatSetting ->
                            if (!preferences.contains(setting.key))
                                preferences[setting.key] = setting.defaultValue

                        is StringSetting ->
                            if (!preferences.contains(setting.key))
                                preferences[setting.key] = setting.defaultValue
                    }
                }
            }
        }
    }

    // Generic getter for any setting type
    fun <T> getSetting(setting: Setting<T>): Flow<T> {
        return context.appSettingsDataStore.data.map { preferences ->
            preferences[setting.key] ?: setting.defaultValue
        }
    }

    // Generic setter for any setting type
    suspend fun <T> setSetting(setting: Setting<T>, value: T) {
        context.appSettingsDataStore.edit { preferences -> preferences[setting.key] = value }
    }

    // Convenience getters for commonly used settings
    val darkMode: Flow<Boolean> = getSetting(Settings.DarkMode)
    val gridColumns: Flow<Int> = getSetting(Settings.GridColumns)
    val accentColor: Flow<String> = getSetting(Settings.AccentColor)
    val sortAlphabetically: Flow<Boolean> = getSetting(Settings.SortAlphabetically)
    val defaultCategory: Flow<String> = getSetting(Settings.DefaultCategory)
    val debugMode: Flow<Boolean> = getSetting(Settings.DebugMode)
    val backupFrequency: Flow<Int> = getSetting(Settings.BackupFrequency)

    // Convenience setters for commonly used settings
    suspend fun setDarkMode(enabled: Boolean) = setSetting(Settings.DarkMode, enabled)

    suspend fun setGridColumns(columns: Int) = setSetting(Settings.GridColumns, columns)

    suspend fun setAccentColor(color: String) = setSetting(Settings.AccentColor, color)

    suspend fun setSortAlphabetically(enabled: Boolean) =
        setSetting(Settings.SortAlphabetically, enabled)

    suspend fun setDefaultCategory(category: String) =
        setSetting(Settings.DefaultCategory, category)

    suspend fun setDebugMode(enabled: Boolean) = setSetting(Settings.DebugMode, enabled)

    suspend fun setBackupFrequency(days: Int) = setSetting(Settings.BackupFrequency, days)
}

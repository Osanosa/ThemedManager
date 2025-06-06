package pro.themed.audhdlauncher.database

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.appSettingsDataStore by preferencesDataStore(name = "app_settings")

object AppSettingsKeys {
    val DarkMode = booleanPreferencesKey("dark_mode")
    val GridColumns = intPreferencesKey("grid_columns")
    val AccentColor = stringPreferencesKey("accent_color")
    val SortAlphabetically = booleanPreferencesKey("sort_alphabetically")
    val DefaultCategory = stringPreferencesKey("default_category")
    val DebugMode = booleanPreferencesKey("debug_mode")
    val BackupFrequency = intPreferencesKey("backup_frequency")
    val UseVerticalListLayout = booleanPreferencesKey("use_vertical_list_layout")
}

object AppSettingsDefaults {
    const val DarkMode = false
    const val GridColumns = 4
    const val AccentColor = "#1428A0"
    const val SortAlphabetically = false
    const val DefaultCategory = "All"
    const val DebugMode = false
    const val BackupFrequency = 7
    const val UseVerticalListLayout = true
}

class AppSettingsDataStore(private val context: Context) {

    val darkMode: Flow<Boolean> = context.appSettingsDataStore.data.map { prefs ->
        prefs[AppSettingsKeys.DarkMode] ?: AppSettingsDefaults.DarkMode
    }
    suspend fun setDarkMode(enabled: Boolean) {
        context.appSettingsDataStore.edit { prefs -> prefs[AppSettingsKeys.DarkMode] = enabled }
    }

    val gridColumns: Flow<Int> = context.appSettingsDataStore.data.map { prefs ->
        prefs[AppSettingsKeys.GridColumns] ?: AppSettingsDefaults.GridColumns
    }
    suspend fun setGridColumns(columns: Int) {
        context.appSettingsDataStore.edit { prefs -> prefs[AppSettingsKeys.GridColumns] = columns }
    }

    val accentColor: Flow<String> = context.appSettingsDataStore.data.map { prefs ->
        prefs[AppSettingsKeys.AccentColor] ?: AppSettingsDefaults.AccentColor
    }
    suspend fun setAccentColor(color: String) {
        context.appSettingsDataStore.edit { prefs -> prefs[AppSettingsKeys.AccentColor] = color }
    }

    val sortAlphabetically: Flow<Boolean> = context.appSettingsDataStore.data.map { prefs ->
        prefs[AppSettingsKeys.SortAlphabetically] ?: AppSettingsDefaults.SortAlphabetically
    }
    suspend fun setSortAlphabetically(enabled: Boolean) {
        context.appSettingsDataStore.edit { prefs -> prefs[AppSettingsKeys.SortAlphabetically] = enabled }
    }

    val defaultCategory: Flow<String> = context.appSettingsDataStore.data.map { prefs ->
        prefs[AppSettingsKeys.DefaultCategory] ?: AppSettingsDefaults.DefaultCategory
    }
    suspend fun setDefaultCategory(category: String) {
        context.appSettingsDataStore.edit { prefs -> prefs[AppSettingsKeys.DefaultCategory] = category }
    }

    val debugMode: Flow<Boolean> = context.appSettingsDataStore.data.map { prefs ->
        prefs[AppSettingsKeys.DebugMode] ?: AppSettingsDefaults.DebugMode
    }
    suspend fun setDebugMode(enabled: Boolean) {
        context.appSettingsDataStore.edit { prefs -> prefs[AppSettingsKeys.DebugMode] = enabled }
    }

    val backupFrequency: Flow<Int> = context.appSettingsDataStore.data.map { prefs ->
        prefs[AppSettingsKeys.BackupFrequency] ?: AppSettingsDefaults.BackupFrequency
    }
    suspend fun setBackupFrequency(days: Int) {
        context.appSettingsDataStore.edit { prefs -> prefs[AppSettingsKeys.BackupFrequency] = days }
    }

    val useVerticalListLayout: Flow<Boolean> = context.appSettingsDataStore.data.map { prefs ->
        prefs[AppSettingsKeys.UseVerticalListLayout] ?: AppSettingsDefaults.UseVerticalListLayout
    }
    suspend fun setUseVerticalListLayout(enabled: Boolean) {
        context.appSettingsDataStore.edit { prefs -> prefs[AppSettingsKeys.UseVerticalListLayout] = enabled }
    }
}

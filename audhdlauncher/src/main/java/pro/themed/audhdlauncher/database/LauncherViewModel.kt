package pro.themed.audhdlauncher.database

import android.content.pm.ResolveInfo
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LauncherViewModel(
    private val repository: AppDataStoreRepository,
    private val settingsDataStore: AppSettingsDataStore,
) : ViewModel() {

    // UI state for categories and their apps
    val categorizedApps: StateFlow<List<Pair<CategoryData, List<ResolveInfo>>>> =
        repository
            .getSortedAppsByCategory()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )

    // UI state for all categories
    val categories: StateFlow<List<CategoryData>> =
        repository.allCategories.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    // Generic function to create StateFlow for any setting
    private fun <T> createSettingStateFlow(setting: AppSettingsDataStore.Setting<T>): StateFlow<T> =
        settingsDataStore
            .getSetting(setting)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = setting.defaultValue,
            )

    // All settings grouped in a single object
    val settings =
        object {
            val darkMode = createSettingStateFlow(AppSettingsDataStore.Settings.DarkMode)
            val gridColumns = createSettingStateFlow(AppSettingsDataStore.Settings.GridColumns)
            val accentColor = createSettingStateFlow(AppSettingsDataStore.Settings.AccentColor)
            val sortAlphabetically =
                createSettingStateFlow(AppSettingsDataStore.Settings.SortAlphabetically)
            val defaultCategory =
                createSettingStateFlow(AppSettingsDataStore.Settings.DefaultCategory)
            val debugMode = createSettingStateFlow(AppSettingsDataStore.Settings.DebugMode)
            val backupFrequency =
                createSettingStateFlow(AppSettingsDataStore.Settings.BackupFrequency)
        }

    // Record app launch
    fun recordAppLaunch(categoryName: String, packageName: String) {
        viewModelScope.launch { repository.incrementAppLaunchCount(categoryName, packageName) }
    }

    // Category management functions
    fun addCategory(category: CategoryData) {
        viewModelScope.launch { repository.addCategory(category) }
    }

    fun updateCategory(category: CategoryData) {
        viewModelScope.launch { repository.updateCategory(category) }
    }

    fun deleteCategory(categoryName: String) {
        viewModelScope.launch { repository.deleteCategory(categoryName) }
    }

    // Generic setting update function
    fun <T> updateSetting(setting: AppSettingsDataStore.Setting<T>, value: T) {
        viewModelScope.launch { settingsDataStore.setSetting(setting, value) }
    }

    // Settings update functions grouped in one object
    val updateSettings =
        object {
            fun darkMode(enabled: Boolean) =
                updateSetting(AppSettingsDataStore.Settings.DarkMode, enabled)

            fun gridColumns(columns: Int) =
                updateSetting(AppSettingsDataStore.Settings.GridColumns, columns)

            fun accentColor(color: String) =
                updateSetting(AppSettingsDataStore.Settings.AccentColor, color)

            fun sortAlphabetically(enabled: Boolean) =
                updateSetting(AppSettingsDataStore.Settings.SortAlphabetically, enabled)

            fun defaultCategory(category: String) =
                updateSetting(AppSettingsDataStore.Settings.DefaultCategory, category)

            fun debugMode(enabled: Boolean) =
                updateSetting(AppSettingsDataStore.Settings.DebugMode, enabled)

            fun backupFrequency(days: Int) =
                updateSetting(AppSettingsDataStore.Settings.BackupFrequency, days)
        }
}

@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel = viewModel(),
    onAppClick: (ResolveInfo, CategoryData) -> Unit,
) {

    // Your Compose UI implementation goes here
    // This is just a skeleton showing state collection

    // Example of handling app click
    val handleAppClick: (ResolveInfo, CategoryData) -> Unit = { app, category ->
        viewModel.recordAppLaunch(category.name, app.activityInfo.packageName)
        onAppClick(app, category)
    }

    // Example: Render categories and apps
    // CategoriesList(
    //    categorizedApps = categorizedApps,
    //    onAppClick = handleAppClick
    // )
}

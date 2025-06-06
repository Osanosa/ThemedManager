package pro.themed.audhdlauncher.database

import android.content.pm.ResolveInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // Settings StateFlows
    val darkMode: StateFlow<Boolean> = settingsDataStore.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettingsDefaults.DarkMode)
    val gridColumns: StateFlow<Int> = settingsDataStore.gridColumns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettingsDefaults.GridColumns)
    val useVerticalListLayout: StateFlow<Boolean> = settingsDataStore.useVerticalListLayout
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettingsDefaults.UseVerticalListLayout)
    val accentColor: StateFlow<String> = settingsDataStore.accentColor
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettingsDefaults.AccentColor)
    val sortAlphabetically: StateFlow<Boolean> = settingsDataStore.sortAlphabetically
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettingsDefaults.SortAlphabetically)
    val defaultCategory: StateFlow<String> = settingsDataStore.defaultCategory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettingsDefaults.DefaultCategory)
    val debugMode: StateFlow<Boolean> = settingsDataStore.debugMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettingsDefaults.DebugMode)
    val backupFrequency: StateFlow<Int> = settingsDataStore.backupFrequency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettingsDefaults.BackupFrequency)

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

    // Public functions to update settings
    fun updateDarkMode(enabled: Boolean) = viewModelScope.launch { settingsDataStore.setDarkMode(enabled) }
    fun updateGridColumns(columns: Int) = viewModelScope.launch { settingsDataStore.setGridColumns(columns) }
    fun updateUseVerticalListLayout(enabled: Boolean) = viewModelScope.launch { settingsDataStore.setUseVerticalListLayout(enabled) }
    fun updateAccentColor(color: String) = viewModelScope.launch { settingsDataStore.setAccentColor(color) }
    fun updateSortAlphabetically(enabled: Boolean) = viewModelScope.launch { settingsDataStore.setSortAlphabetically(enabled) }
    fun updateDefaultCategory(category: String) = viewModelScope.launch { settingsDataStore.setDefaultCategory(category) }
    fun updateDebugMode(enabled: Boolean) = viewModelScope.launch { settingsDataStore.setDebugMode(enabled) }
    fun updateBackupFrequency(days: Int) = viewModelScope.launch { settingsDataStore.setBackupFrequency(days) }
}

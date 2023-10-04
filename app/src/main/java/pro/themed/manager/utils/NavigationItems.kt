package pro.themed.manager.utils

import pro.themed.manager.*

sealed class NavigationItems(var route: String, var icon: Int, var title: String) {
    data object ColorsTab : NavigationItems("ColorsTab", R.drawable.format_paint, "Colors")
    data object IconsTab : NavigationItems("IconsTab", R.drawable.outline_widgets_24, "Icons")
    data object FontsTab : NavigationItems("FontsTab", R.drawable.round_api_24, "Apps")
    data object MiscTab : NavigationItems("MiscTab", R.drawable.baseline_miscellaneous_services_24, "Misc")
    data object Toolbox : NavigationItems("Toolbox", R.drawable.toolbox, "Toolbox")
    data object Settings : NavigationItems("Settings", R.drawable.settings_24px, "Settings")
    data object About : NavigationItems("About", R.drawable.info_24px, "About")
}

package pro.themed.manager.utils

import pro.themed.manager.R

sealed class NavigationItems(var route: String, var icon: Int, var title: String) {
    data object About : NavigationItems("About", R.drawable.info_24px, "About")

    data object Settings : NavigationItems("Settings", R.drawable.settings_24px, "Settings")

    data object Toolbox : NavigationItems("Toolbox", R.drawable.toolbox, "Toolbox")

    data object ColorsTab : NavigationItems("ColorsTab", R.drawable.format_paint, "Colors")

    data object QsPanel : NavigationItems("QsPanel", R.drawable.outline_widgets_24, "QsPanel")

    data object IconsTab : NavigationItems("IconsTab", R.drawable.outline_widgets_24, "Icons")

    data object FontsTab : NavigationItems("FontsTab", R.drawable.round_api_24, "Fonts")

    data object MiscTab :
        NavigationItems("MiscTab", R.drawable.baseline_miscellaneous_services_24, "Misc")
}

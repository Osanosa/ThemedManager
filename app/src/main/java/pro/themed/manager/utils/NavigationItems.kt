package pro.themed.manager.utils

import pro.themed.manager.R

sealed class NavigationItems(var route: String, var icon: Int, var title: String) {
    object ColorsTab : NavigationItems("ColorsTab", R.drawable.format_paint, "Colors")
    object IconsTab : NavigationItems("IconsTab", R.drawable.outline_widgets_24, "Icons")
    object FontsTab : NavigationItems("FontsTab", R.drawable.round_api_24, "Apps")
    object MiscTab :
        NavigationItems("MiscTab", R.drawable.baseline_miscellaneous_services_24, "Misc")
}
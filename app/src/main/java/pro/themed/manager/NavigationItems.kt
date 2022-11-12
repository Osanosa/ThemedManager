package pro.themed.manager

sealed class NavigationItems(var route: String, var icon: Int, var title: String)
{
object ColorsTab : NavigationItems("ColorsTab", R.drawable.format_paint, "Colors")
object IconsTab : NavigationItems("IconsTab", R.drawable.outline_widgets_24, "Icons")
}
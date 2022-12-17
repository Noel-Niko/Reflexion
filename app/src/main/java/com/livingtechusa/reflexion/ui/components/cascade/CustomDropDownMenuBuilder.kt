//package com.livingtechusa.reflexion.ui.components.cascade
//
//import androidx.compose.ui.graphics.vector.ImageVector
//import com.androidpoet.dropdown.MenuItem
//
//public class CustomDropDownMenuBuilder<T : Any> {
//    public var menu: MenuItem<T> = MenuItem<T>()
//
//    public fun icon(value: ImageVector) {
//        menu.icon = value
//    }
//
//    public fun item(id: T, title: String, init: (CustomDropDownMenuBuilder<T>.() -> Unit)? = null) {
//        val menuBuilder = CustomDropDownMenuBuilder<T>()
//        val child = menuBuilder.menu.apply {
//            this.id = id
//            this.title = title
//        }
//
//
//        init?.let {
//            menuBuilder.init()
//        }
//        menu.children = menu.children ?: mutableListOf()
//        child.parent = menu
//        menu.children!!.add(child)
//
//
//        init?.let {
//            menuBuilder.init()
//        }
//
//    }
//}
//
//public fun <T : Any> customDropDownMenu(init: CustomDropDownMenuBuilder<T>.() -> Unit): MenuItem<T> {
//    val menuBuilder = CustomDropDownMenuBuilder<T>()
//    menuBuilder.init()
//    return menuBuilder.menu
//}
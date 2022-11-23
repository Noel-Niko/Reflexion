package com.livingtechusa.reflexion.navigation

import com.livingtechusa.reflexion.Main_Activity
import com.livingtechusa.reflexion.ui.build.BuildRoute
import com.livingtechusa.reflexion.ui.children.ChildRoute
import com.livingtechusa.reflexion.ui.components.CONFIRM_SAVE
import com.livingtechusa.reflexion.ui.components.PASTE_SAVE
import com.livingtechusa.reflexion.ui.components.VideoScreenRoute


sealed class Screen(val route: String) {
    object BuildItemScreen : Screen(BuildRoute)
    object VideoView : Screen(VideoScreenRoute)
    object ChildrenScreen : Screen(ChildRoute)
    object MainActivity: Screen(Main_Activity)
    object ConfirmSaveScreen: Screen(CONFIRM_SAVE)
    object PasteAndSaveScreen: Screen(PASTE_SAVE)

    fun withArgs(vararg args: String): String {
        return  buildString {
            append(route)
            args.forEach { arg->
                append("/$arg")
            }
        }
    }
}
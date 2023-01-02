package com.livingtechusa.reflexion.navigation

import com.livingtechusa.reflexion.Main_Activity
import com.livingtechusa.reflexion.ui.build.BuildRoute
import com.livingtechusa.reflexion.ui.components.CONFIRM_DELETE
import com.livingtechusa.reflexion.ui.topics.ListRoute
import com.livingtechusa.reflexion.ui.components.CONFIRM_SAVE
import com.livingtechusa.reflexion.ui.components.PASTE_SAVE
import com.livingtechusa.reflexion.ui.components.VideoScreenRoute
import com.livingtechusa.reflexion.ui.customLists.BuildCustomList
import com.livingtechusa.reflexion.ui.home.HOME


sealed class Screen(val route: String) {
    object HomeScreen : Screen(HOME)
    object BuildItemScreen : Screen(BuildRoute)
    object VideoView : Screen(VideoScreenRoute)
    object MainActivity: Screen(Main_Activity)
    object ConfirmSaveScreen: Screen(CONFIRM_SAVE)
    object PasteAndSaveScreen: Screen(PASTE_SAVE)

    object ConfirmDeleteScreen: Screen(CONFIRM_DELETE)
    object ListScreen: Screen(ListRoute)

    object CustomLists: Screen(BuildCustomList)

    fun withArgs(vararg args: String): String {
        return  buildString {
            append(route)
            args.forEach { arg->
                append("/$arg")
            }
        }
    }
}
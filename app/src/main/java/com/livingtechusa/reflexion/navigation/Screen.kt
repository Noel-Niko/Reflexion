package com.livingtechusa.reflexion.navigation

import com.livingtechusa.reflexion.Main_Activity
import com.livingtechusa.reflexion.ui.build.BuildRoute
import com.livingtechusa.reflexion.ui.components.CONFIRM_DELETE_LIST
import com.livingtechusa.reflexion.ui.components.CONFIRM_DELETE_SUBITEM
import com.livingtechusa.reflexion.ui.topics.ListRoute
import com.livingtechusa.reflexion.ui.components.CONFIRM_SAVE
import com.livingtechusa.reflexion.ui.components.PASTE_SAVE
import com.livingtechusa.reflexion.ui.components.VideoCustomListScreenRoute
import com.livingtechusa.reflexion.ui.components.VideoScreenRoute
import com.livingtechusa.reflexion.ui.customListDisplay.CUSTOM_LIST_DISPLAY
import com.livingtechusa.reflexion.ui.customLists.BuildCustomList
import com.livingtechusa.reflexion.ui.home.HOME


sealed class Screen(val route: String) {
    object CustomListDisplay : Screen(CUSTOM_LIST_DISPLAY)

    object HomeScreen : Screen(HOME)
    object BuildItemScreen : Screen(BuildRoute)
    object VideoView : Screen(VideoScreenRoute)

    object VideoViewCustomList : Screen(VideoCustomListScreenRoute)
    object MainActivity: Screen(Main_Activity)
    object ConfirmSaveScreen: Screen(CONFIRM_SAVE)
    object PasteAndSaveScreen: Screen(PASTE_SAVE)
    object ConfirmDeleteSubItemScreen: Screen(CONFIRM_DELETE_SUBITEM)
    object ConfirmDeleteListScreen: Screen(CONFIRM_DELETE_LIST)
    object TopicScreen: Screen(ListRoute)

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
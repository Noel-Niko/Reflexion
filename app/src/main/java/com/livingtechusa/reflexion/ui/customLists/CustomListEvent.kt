package com.livingtechusa.reflexion.ui.customLists

import com.livingtechusa.reflexion.ui.build.BuildEvent

sealed class CustomListEvent {

    data class UpdateListName(
        val index: Int,
        val text: String? = ""
    ) : CustomListEvent() {}

    data class MoveItemUp(
        val index: Int,
    ) : CustomListEvent() {}

    data class MoveItemDown(
        val index: Int,
    ) : CustomListEvent() {}

    data class DeleteItemInList(
        val index: Int
    ) : CustomListEvent()

    object Save : CustomListEvent()

    data class DeleteList(
        val index: Int
    ) : CustomListEvent()

    data class MoveToEdit(
        val index: Int
    ) : CustomListEvent()

    data class GetDisplayList(
        val headNodePk: Long
    ) : CustomListEvent()

    data class GetDisplayListImages(
        val headNodePk: Long
    ) : CustomListEvent()

    object ReSet : CustomListEvent()
    data class Bookmark(val nodePk: Long) : CustomListEvent()

    // For Custom List Display Screen
    object SendText : CustomListEvent()


}
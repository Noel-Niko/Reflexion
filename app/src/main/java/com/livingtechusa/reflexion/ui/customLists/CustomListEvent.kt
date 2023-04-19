package com.livingtechusa.reflexion.ui.customLists

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
    object SendFile : CustomListEvent()

    // For Custom List Display Screen
    object SendText : CustomListEvent()
}
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

    object ReSet : CustomListEvent()

}
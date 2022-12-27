package com.livingtechusa.reflexion.ui.customLists

sealed class CustomListEvent {
    data class GetChildList(
        val pk: Long
    ) : CustomListEvent() {}

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

    data class Delete(
        val index: Int
    ) : CustomListEvent()
}
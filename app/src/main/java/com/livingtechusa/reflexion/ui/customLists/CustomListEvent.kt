package com.livingtechusa.reflexion.ui.customLists

sealed class CustomListEvent {
    data class GetListsForTopic(
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

    object Save: CustomListEvent()
}
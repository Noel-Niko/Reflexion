package com.livingtechusa.reflexion.ui.customLists

import com.livingtechusa.reflexion.ui.list.ListEvent

sealed class CustomListEvent {
    data class GetChildList(
        val pk: Long
    ) : CustomListEvent() {}

    data class UpdateListName(
        val index: Int,
        val text: String? = ""
    ) : CustomListEvent() {}
}
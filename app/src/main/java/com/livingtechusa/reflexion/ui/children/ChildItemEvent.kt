package com.livingtechusa.reflexion.ui.children

import com.livingtechusa.reflexion.data.entities.ReflexionItem

sealed class ChildEvent {

    data class GetChildren(
        val parentPK: Long
    ) : ChildEvent() {}

    data class DisplaySelectedReflexionItem(
        val reflexionItem: ReflexionItem
    ) : ChildEvent() {}

}
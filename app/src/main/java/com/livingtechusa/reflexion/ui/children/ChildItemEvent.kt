package com.livingtechusa.reflexion.ui.children

import com.livingtechusa.reflexion.data.entities.ReflexionItem

sealed class ChildEvent {

    object GetChildren : ChildEvent() {}

    data class DisplaySelectedReflexionItem(
        val reflexionItem: ReflexionItem
    ) : ChildEvent() {}

}
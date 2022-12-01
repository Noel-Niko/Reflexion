package com.livingtechusa.reflexion.ui.children

import com.livingtechusa.reflexion.data.entities.ReflexionItem

sealed class ListEvent {

    data class GetList(
        val pk: Long
    ) : ListEvent() {}

    data class DisplaySelectedReflexionItem(
        val pk: Long
    ) : ListEvent() {}

   data class Search(
       val search: String?,
       val pk: Long
       ): ListEvent() {}

}
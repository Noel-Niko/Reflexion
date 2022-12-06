package com.livingtechusa.reflexion.ui.list

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
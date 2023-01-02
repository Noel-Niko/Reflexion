package com.livingtechusa.reflexion.ui.topics

sealed class ListEvent {

    data class GetList(
        val pk: Long?
    ) : ListEvent() {}

    data class DisplaySelectedReflexionItem(
        val pk: Long
    ) : ListEvent() {}

   data class Search(
       val search: String?,
       val pk: Long
       ): ListEvent() {}

    object  ClearList: ListEvent()

    object UpOneLevel: ListEvent()
}
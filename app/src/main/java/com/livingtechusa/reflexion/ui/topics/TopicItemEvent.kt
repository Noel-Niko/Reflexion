package com.livingtechusa.reflexion.ui.topics

sealed class TopicItemEvent {

    data class GetTopicItem(
        val pk: Long?
    ) : TopicItemEvent() {}

    data class DisplaySelectedReflexionItem(
        val pk: Long
    ) : TopicItemEvent() {}

   data class Search(
       val search: String?,
       ): TopicItemEvent() {}

    object  ClearTopicItem: TopicItemEvent()

    object UpOneLevel: TopicItemEvent()
}
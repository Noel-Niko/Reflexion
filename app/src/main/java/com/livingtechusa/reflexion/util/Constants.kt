package com.livingtechusa.reflexion.util

import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING


object Constants {
    const val DATABASE_NAME: String = "Reflexion_Database"
    const val EMPTY_STRING: String = ""
    const val VIDEO = "video/*"
    const val IMAGE = "image/*"
    const val URI = "Uri"
    const val URL = "Url"
    const val SEARCH_YOUTUBE = "https://www.youtube.com/results?search_query="
    const val SOURCE = "sourceType"
    const val REFLEXION_ITEM_PK = "reflexion_item_pk"
    const val EMPTY_ITEM = "Empty Item"
    const val EMPTY_PK = -1L
    const val EMPTY_PK_STRING = "-1"
    const val DO_NOT_UPDATE = -2L
    const val NO_LISTS = "No lists found for selected topic."
    const val INDEX = "index"
    const val LIST_NAME = "listName"
    const val HEAD_NODE_PK = "headNodePk"
}

object Temporary {
    var use: Boolean = false
    var url = EMPTY_STRING
    var tempReflexionItem: ReflexionItem = ReflexionItem()
}
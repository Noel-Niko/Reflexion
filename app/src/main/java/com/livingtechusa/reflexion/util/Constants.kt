package com.livingtechusa.reflexion.util

import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING


object Constants {
    const val DATABASE_NAME: String = "Reflexion_Database"
    const val EMPTY_STRING: String = ""
    const val VIDEO = "video/*"
    const val URI = "Uri"
    const val URL = "Url"
    const val SAVE_URL = "Save_Url"
    const val SEARCH_YOUTUBE = "https://www.youtube.com/results?search_query="
    const val SOURCE = "sourceType"
    const val REFLEXION_ITEM_PK = "reflexion_item_pk"
}

object Temporary {
    var use: Boolean = false
    var url = EMPTY_STRING
    var tempReflexionItem: ReflexionItem = ReflexionItem()
}
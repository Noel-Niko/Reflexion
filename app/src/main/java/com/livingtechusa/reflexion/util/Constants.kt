package com.livingtechusa.reflexion.util

import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING

object Constants {
    const val DATABASE_NAME: String = "Reflexion_Database"
    const val EMPTY_STRING: String = ""
    const val VIDEO_TYPE = "video/*"
    const val IMAGE_TYPE = "image/*"
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
    const val SUB_ITEM = "subItem"
    // REFLEXION ITEM SUB ITEMS
    const val NAME = "name"
    const val DESCRIPTION = "description"
    const val DETAILED_DESCRIPTION = "detailedDescription"
    const val IMAGE = "image"
    const val VIDEO_URI = "videoUri"
    const val VIDEO_URL = "videoUrl"
    const val PARENT = "parent"

    // APP ICONS
    const val EYE_1 = R.mipmap.ic_launcher
    const val EYE_4 = R.mipmap.ic_launcher_4
    const val EYE_8 = R.mipmap.ic_launcher_8
    const val EYE_9 = R.mipmap.ic_launcher_9
    const val EYE_10 = R.mipmap.ic_launcher_10
    const val EYE_11 = R.mipmap.ic_launcher_11
    const val EYE_19 = R.mipmap.ic_launcher_19
    const val EYE_14 = R.mipmap.ic_launcher_14
    const val EYE_12 = R.mipmap.ic_launcher_12
    const val EYE_17 = R.mipmap.ic_launcher_17
    const val EYE_13 = R.mipmap.ic_launcher_13
    const val EYE_18 = R.mipmap.ic_launcher_18
    const val EYE_15 = R.mipmap.ic_launcher_15
    const val EYE_16 = R.mipmap.ic_launcher_16
    const val EYE_21 = R.mipmap.ic_launcher_21
    const val BUDDA = R.mipmap.ic_launcher_2
    const val BOOKS = R.mipmap.ic_launcher_5
    const val SUN_GLASSES = R.mipmap.ic_launcher_6
    const val PUZZLE = R.mipmap.ic_launcher_7
    val APP_ICON_LIST = listOf<Int>(
        EYE_1, EYE_4, EYE_8, EYE_9, EYE_10, EYE_11, EYE_19, EYE_14, EYE_12, EYE_17, EYE_13, EYE_18, EYE_15,
        EYE_16, EYE_21, BUDDA, BOOKS, SUN_GLASSES, PUZZLE
        )
}

object Temporary {
    var use: Boolean = false
    var url = EMPTY_STRING
    var tempReflexionItem: ReflexionItem = ReflexionItem()
}


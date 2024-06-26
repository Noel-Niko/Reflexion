package com.livingtechusa.reflexion.data.models

import com.livingtechusa.reflexion.util.Constants

data class AbridgedReflexionItem(
    var autogenPk: Long = 0,
    var name: String = Constants.EMPTY_ITEM,
    var parent: Long? = null
)
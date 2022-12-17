package com.livingtechusa.reflexion.util

import com.livingtechusa.reflexion.data.entities.ReflexionItem

class ReflexionArrayItem(itemPK: Long?, itemName: String, children: MutableList<ReflexionArrayItem>?) {
    var reflexionItemPk: Long? =  itemPK
    var reflexionItemName: String = itemName
    var items: MutableList<ReflexionArrayItem>? = children ?: mutableListOf()
}

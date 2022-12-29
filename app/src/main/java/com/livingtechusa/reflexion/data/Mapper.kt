package com.livingtechusa.reflexion.data

import com.livingtechusa.reflexion.data.entities.ListNode
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import com.livingtechusa.reflexion.data.models.AbridgedReflexionItem
import com.livingtechusa.reflexion.util.Constants
import com.livingtechusa.reflexion.util.ReflexionArrayItem
import java.util.concurrent.atomic.AtomicLong


fun ReflexionArrayItem.toListNode(topic: Long?): List<ListNode?> {
    val list = mutableListOf<ListNode>()
    list.add(ListNode(
        nodePk = 0L,
        topic = topic ?: -1L,
        title = reflexionItemName.toString(),
        itemPK = reflexionItemPk ?: -1L,
        parentPk = null,
        childPk = items?.get(0)?.reflexionItemPk
    ) )
    items?.forEach {
        list.add(it.toAListNode( topic = topic, parentPk = reflexionItemPk))
    }
    return list
}

fun ReflexionArrayItem.toAListNode(topic: Long?, parentPk: Long?): ListNode {
    var child: Long? = null
    if(items.isNullOrEmpty().not() && items?.get(0)?.reflexionItemPk != null) {
        child = items?.get(0)?.reflexionItemPk
    }
    return ListNode(
        nodePk = 0L,
        topic = topic ?: -1L,
        title = reflexionItemName.toString(),
        itemPK = reflexionItemPk ?: -1L,
        parentPk = parentPk ?: -1L,  // FK - ITEM.autogenPK
        childPk = child
    )
}

fun AbridgedReflexionItem.toReflexionArrayItem() =
    ReflexionArrayItem(
        itemPK = autogenPK ?: 0L,
        itemName = name ?: Constants.EMPTY_STRING,
        children = mutableListOf()
    )

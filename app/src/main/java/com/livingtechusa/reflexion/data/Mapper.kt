package com.livingtechusa.reflexion.data

import com.livingtechusa.reflexion.data.entities.ListNode
import com.livingtechusa.reflexion.data.models.AbridgedReflexionItem
import com.livingtechusa.reflexion.data.models.ReflexionArrayItem
import com.livingtechusa.reflexion.util.Constants
import com.livingtechusa.reflexion.util.Constants.EMPTY_PK

fun ReflexionArrayItem.toListNode(topic: Long?, headNodePk: Long?): List<ListNode?> {
    val list = mutableListOf<ListNode>()
    var child: Long? = null
    if(children.isEmpty().not()) {
        child = children.get(0).itemPK
    }
    list.add(ListNode(
        nodePk = headNodePk ?: 0L,
        topic = topic ?: EMPTY_PK,
        title = itemName.toString(),
        itemPK = itemPK ?: EMPTY_PK,
        parentPk = null,
        childPk = child
    ) )
    children.forEach {
        list.add(it.toAListNode( topic = topic, parentPk = itemPK))
    }
    return list
}

fun ReflexionArrayItem.toAListNode(topic: Long?, parentPk: Long?): ListNode {
    var child: Long? = null
    if(children.isEmpty().not() && children.get(0).itemPK != null) {
        child = children.get(0).itemPK
    }
    return ListNode(
        nodePk = 0L,
        topic = topic ?: EMPTY_PK,
        title = itemName.toString(),
        itemPK = itemPK ?: EMPTY_PK,
        parentPk = parentPk ?: EMPTY_PK,  // FK - ITEM.autogenPK
        childPk = child
    )
}

fun AbridgedReflexionItem.toReflexionArrayItem() =
    ReflexionArrayItem(
        itemPK = autogenPk,
        itemName = name,
        nodePk = 0L,
        children = mutableListOf()
    )

fun ListNode.toReflexionArrayItem() =
    ReflexionArrayItem(
        itemPK = itemPK,
        itemName = title,
        nodePk = nodePk,
        children = mutableListOf()
    )
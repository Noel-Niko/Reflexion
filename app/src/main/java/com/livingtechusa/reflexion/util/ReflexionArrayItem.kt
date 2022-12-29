package com.livingtechusa.reflexion.util

import com.livingtechusa.reflexion.data.entities.ReflexionItem

class ReflexionArrayItem(itemPK: Long?, itemName: String?, children: MutableList<ReflexionArrayItem>?) {
    var reflexionItemPk: Long? =  itemPK
    var reflexionItemName: String? = itemName
    var items: MutableList<ReflexionArrayItem>? = children ?: mutableListOf()

    companion object {
        fun traverseDepthFirst(
            rootNode: ReflexionArrayItem,
            action: (value: ReflexionArrayItem) -> Unit
        ) {
            val stack = ArrayDeque<ReflexionArrayItem>()
            stack.addFirst(rootNode)

            while(stack.isNotEmpty()) {
                val currentNode = stack.removeFirst()

                action.invoke(currentNode)

                for(index in (currentNode.items?.size?.minus(1) ?: 0) downTo 0) {
                    stack.addFirst(currentNode.items?.get(index) ?: ReflexionArrayItem(null, null, null))
                }
            }
        }
        fun traverseBreadthFirst(
            rootNode: ReflexionArrayItem,
            action: (value: ReflexionArrayItem) -> Unit
        ) {
            val queue = ArrayDeque<ReflexionArrayItem>()
            queue.addFirst(rootNode)

            while(queue.isNotEmpty()) {
                val currentNode = queue.removeLast()

                action.invoke(currentNode)

                currentNode.items?.forEach(){
                    queue.addFirst(it)
                }
            }
        }
    }
}

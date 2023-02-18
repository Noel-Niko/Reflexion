package com.livingtechusa.reflexion.data.models

class ReflexionArrayItem(var itemPK: Long?, var itemName: String?, var nodePk: Long?, var children: MutableList<ReflexionArrayItem> = mutableListOf()) {


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

                for(index in (currentNode.children.size.minus(1) ?: 0) downTo 0) {
                    stack.addFirst(currentNode.children.get(index) ?: ReflexionArrayItem(null, null, null))
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

                currentNode.children.forEach(){
                    queue.addFirst(it)
                }
            }
        }
    }
}

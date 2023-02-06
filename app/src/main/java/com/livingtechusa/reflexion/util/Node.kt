package com.livingtechusa.reflexion.util


class Node<T>(
    var value: T,
    var children: MutableList<Node<T>> = mutableListOf()
) {

    companion object {

        fun <T> traverseBreadthFirst(
            rootNode: Node<T>,
            action: (value: T) -> Unit
        ) {
            val queue = ArrayDeque<Node<T>>()
            queue.addFirst(rootNode)

            while(queue.isNotEmpty()) {
                val currentNode = queue.removeLast()

                action.invoke(currentNode.value)

                for(childNode in currentNode.children) {
                    queue.addFirst(childNode)
                }
            }
        }
    }
}
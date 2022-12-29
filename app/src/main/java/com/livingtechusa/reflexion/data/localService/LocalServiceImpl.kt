package com.livingtechusa.reflexion.data.localService

import android.util.Log
import com.livingtechusa.reflexion.data.dao.KeyWordsDao
import com.livingtechusa.reflexion.data.dao.LinkedListDao
import com.livingtechusa.reflexion.data.dao.ReflexionItemDao
import com.livingtechusa.reflexion.data.entities.KeyWords
import com.livingtechusa.reflexion.data.entities.ListNode
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.models.AbridgedReflexionItem
import com.livingtechusa.reflexion.data.toListNode
import com.livingtechusa.reflexion.data.toReflexionArrayItem
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import com.livingtechusa.reflexion.util.ReflexionArrayItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

class LocalServiceImpl @Inject constructor(
    private val reflexionItemDao: ReflexionItemDao,
    private val keyWordsDao: KeyWordsDao,
    private val linkedListDao: LinkedListDao
) : ILocalService {
    companion object {
        val TAG = "LocalServiceImpl"
    }
    private val scope = CoroutineScope(Dispatchers.IO)
    override suspend fun setItem(item: ReflexionItem) {
        reflexionItemDao.setReflexionItem(item)
    }

    override suspend fun updateReflexionItem(item: ReflexionItem) {
        reflexionItemDao.updateReflexionItem(
            item.autogenPK,
            item.name,
            item.description,
            item.detailedDescription,
            item.image,
            item.videoUri,
            item.videoUrl,
            item.parent
        )
    }

    override suspend fun getAllItems(): List<ReflexionItem?> {
        return reflexionItemDao.getAllReflexionItems()
    }

    override suspend fun getAllTopics(): List<ReflexionItem?> {
        return reflexionItemDao.getReflexionItemTopics()
    }

    override suspend fun getAllTopicsContainingString(
        search: String
    ): List<ReflexionItem?> {
        return reflexionItemDao.getAllTopicsContainingString(search)
    }

    override suspend fun selectChildrenContainingString(
        pk: Long,
        search: String?
    ): List<ReflexionItem?> {
        var list = emptyList<ReflexionItem>()
        if (pk <= 0) {
            list =
                search?.let { reflexionItemDao.getAllTopicsContainingString(it) } as List<ReflexionItem>
        } else {
            reflexionItemDao.selectChildrenContainingString(pk, search)
        }
        return list
    }

    override suspend fun selectChildren(pk: Long): List<ReflexionItem?> {
        return reflexionItemDao.selectChildReflexionItems(pk)
    }

    override suspend fun clearALLReflexionItems() {
        reflexionItemDao.clearALLReflexionItems()
    }

    override suspend fun selectItem(autogenPK: Long): ReflexionItem? {
        return reflexionItemDao.selectReflexionItem(autogenPK)
    }

    override suspend fun deleteReflexionItem(autogenPK: Long, name: String) {
        return reflexionItemDao.deleteReflexionItem(autogenPK, name)
    }

    override suspend fun renameItem(autogenPK: Long, name: String, newName: String) {
        return reflexionItemDao.renameReflexionItem(autogenPK, name, newName)
    }

    override suspend fun setItemParent(autogenPK: Long, name: String, newParent: Long) {
        return reflexionItemDao.setReflexionItemParent(autogenPK, name, newParent)
    }

    override suspend fun selectAbridgedReflexionItemDataByParentPk(pk: Long?): List<AbridgedReflexionItem?> {
        if (pk == null) {
            return reflexionItemDao.getAbridgedReflexionItemTopics()
        }
        return reflexionItemDao.selectAbridgedReflexionItemDataByParentPk(pk)
    }

    override suspend fun selectReflexionArrayItemsByParentPk(pk: Long?): List<ReflexionArrayItem?> {
        if (pk == null) {
            val result: MutableList<ReflexionArrayItem> = mutableListOf()
            reflexionItemDao.getAbridgedReflexionItemTopics().forEach() {
                val Rai = ReflexionArrayItem(
                    itemPK = it?.autogenPK ?: 0L,
                    itemName = it?.name ?: EMPTY_STRING,
                    children = mutableListOf()
                )
                if (Rai != null) {
                    result.add(Rai)
                }
            }
            return result
        } else {
            val result: MutableList<ReflexionArrayItem> = mutableListOf()
            reflexionItemDao.selectAbridgedReflexionItemDataByParentPk(pk).forEach() {
                val Rai: ReflexionArrayItem =
                    ReflexionArrayItem(
                        itemPK = it?.autogenPK ?: 0L,
                        itemName = it?.name ?: EMPTY_STRING,
                        children = mutableListOf()
                    )
                result.add(Rai)
            }
            return result
        }
    }

    override suspend fun selectReflexionArrayItemsByPk(pk: Long): ReflexionArrayItem? {
        val _result: Deferred<AbridgedReflexionItem> = scope.async {
            reflexionItemDao.selectSingleAbridgedReflexionItem(pk)
        }
        return _result.await().toReflexionArrayItem()
    }

    override suspend fun selectParent(pk: Long): Long? {
        return reflexionItemDao.getParent(pk)
    }

    override suspend fun insertNewNodeList(arrayItem: ReflexionArrayItem, topic: Long) {
        val nodeList: List<ListNode?> = arrayItem.toListNode(topic)
        var parent: Long? = null
        nodeList.forEach {
            if (it != null) {
                val updated = it.copy(parentPk = parent)
                parent = linkedListDao.insertNewNode(updated)
            }
        }
    }

    override suspend fun selectSingleAbridgedReflexionItemDataByParentPk(pk: Long): AbridgedReflexionItem {
        return reflexionItemDao.selectSingleAbridgedReflexionItemDataByParentPk(pk)
    }


    override suspend fun selectSiblings(pk: Long, parent: Long): List<ReflexionItem?> {
        return reflexionItemDao.selectSiblings(pk, parent)
    }

    override suspend fun selectAllSiblings(parent: Long): List<ReflexionItem?> {
        val grandparent: Long = reflexionItemDao.getParent(parent) ?: -1L
        if(grandparent == -1L) {
            return reflexionItemDao.getReflexionItemTopics()
        } else {
            return reflexionItemDao.selectAllSiblings(grandparent)
        }
    }


    override suspend fun setKeyWords(keyWords: KeyWords) {
        return keyWordsDao.setKeyWords(keyWords)
    }

    override suspend fun getKeyWords(): KeyWords? {
        return keyWordsDao.getKeyWords()
    }

    override suspend fun clearKeyWords() {
        keyWordsDao.clearKeyWords()
    }

    override suspend fun selectKeyWords(item_pk: String): List<KeyWords?> {
        return keyWordsDao.selectKeyWords(item_pk)
    }

    override suspend fun deleteKeyWord(word: String) {
        keyWordsDao.deleteKeyWord(word)
    }

    override suspend fun renameKeyWord(word: String, newWord: String) {
        keyWordsDao.renameKeyWord(word, newWord)
    }

    override suspend fun deleteAllChildNodes(nodePk: Long) {
        var child = linkedListDao.selectChildNode(nodePk = nodePk)
        do {
            val parent: Long? = child?.nodePk
            if (parent != null) {
                linkedListDao.deleteSelectedNode(parent)
            }
            child = parent?.let { linkedListDao.selectChildNode(nodePk = it) }
        } while (child != null)
    }

    override suspend fun insertNewNode(listNode: ListNode) {
        linkedListDao.insertNewNode(listNode = listNode)
    }

    override suspend fun selectNodeHeadsByTopic(topicPk: Long): List<ListNode?> {
        return linkedListDao.selectNodeHeadsByTopic(topicPk)
    }

    override suspend fun selectNodeListsByTopic(topicPk: Long): List<ReflexionArrayItem> {
        val nList: MutableList<ListNode> = mutableListOf()
        linkedListDao.selectNodeHeadsByTopic(topicPk = topicPk).forEach { listNode ->
            listNode?.nodePk?.let { node -> linkedListDao.selectChildNode(node)}
                ?.let { listNode1 -> nList.add(listNode1) }
        }
        val rList = mutableListOf<ReflexionArrayItem>()
        nList.filter(){
            it.parentPk == null
        }.forEach(){
            rList.add(it.toReflexionArrayItem())
        }
        fun getChild(itemPk: Long): ListNode? {
            return nList.filter { it.parentPk == itemPk }.firstOrNull()
        }
        rList.forEach() { reflexionArrayItem ->
            val children = mutableListOf<ListNode>()
            var child: ListNode? = null
            child = reflexionArrayItem.reflexionItemPk?.let { it1 -> getChild(it1) }
            if (child != null) {
                children.add(child)
            }
            val childrenSorted = children.sortedWith<ListNode>(object : Comparator <ListNode> {
                override fun compare(o1: ListNode, o2: ListNode): Int {
                    try {
                        if (o1.itemPK > o2.parentPk!!) {
                            return 0
                        }
                        if (o1.itemPK == o2.parentPk) {
                            return 1
                        }
                        if (o1.itemPK < o2.parentPk) {
                            return 0
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "ERROR: " + e.message + " WITH CAUSE: " + e.cause)
                    }
                    return 0
                }
            })
            val reflexionArrayItems: MutableList<ReflexionArrayItem> = mutableListOf()
            for (childNode in childrenSorted) {
                reflexionArrayItems.add(childNode.toReflexionArrayItem())
            }
            reflexionArrayItem.items = reflexionArrayItems
        }
        return rList
    }

    override suspend fun selectChildNode(nodePk: Long): ListNode? {
        return linkedListDao.selectChildNode(nodePk = nodePk)
    }

    override suspend fun selectParentNode(parentPk: Long): ListNode? {
        return linkedListDao.selectParentNode(parentPk = parentPk)
    }

    override suspend fun updateListNode(
        nodePk: Long,
        title: String,
        parentPK: Long,
        childPk: Long
    ) {
        linkedListDao.updateListNode(
            nodePk = nodePk,
            title = title,
            parentPK = parentPK,
            childPk = childPk
        )
    }

    override suspend fun getAllLinkedLists(): List<ListNode?> {
        return linkedListDao.getAllLinkedLists()
    }

    override suspend fun deleteAllLinkedLists() {
        linkedListDao.deleteAllLinkedLists()
    }

    override suspend fun deleteSelectedNode(nodePk: Long) {
        linkedListDao.deleteSelectedNode(nodePk = nodePk)
    }

    override suspend fun selectReflexionItemByName(name: String): ReflexionItem {
        return reflexionItemDao.selectReflexionItemByName(name)
    }
}
package com.livingtechusa.reflexion.data.localService

import com.livingtechusa.reflexion.data.dao.KeyWordsDao
import com.livingtechusa.reflexion.data.dao.LinkedListDao
import com.livingtechusa.reflexion.data.dao.ReflexionItemDao
import com.livingtechusa.reflexion.data.entities.KeyWords
import com.livingtechusa.reflexion.data.entities.ListNode
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import javax.inject.Inject

class LocalServiceImpl @Inject constructor(
    private val reflexionItemDao: ReflexionItemDao,
    private val keyWordsDao: KeyWordsDao,
    private val linkedListDao: LinkedListDao
) : ILocalService {
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

    override suspend fun selectChildItems(parent: Long): List<ReflexionItem?> {
        return reflexionItemDao.selectChildReflexionItems(parent)
    }

    override suspend fun selectSiblings(pk: Long, parent: Long): List<ReflexionItem?> {
        return reflexionItemDao.selectSiblings(pk, parent)
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

    override suspend fun selectLinkedList(nodePk: Long): ListNode? {
        return linkedListDao.selectLinkedList(nodePk)
    }

    override suspend fun deleteSelectedNode(nodePk: Long) {
        linkedListDao.deleteSelectedNode(nodePk = nodePk)
    }

    override suspend fun selectReflexionItemByName(name: String): ReflexionItem {
        return reflexionItemDao.selectReflexionItemByName(name)
    }
}
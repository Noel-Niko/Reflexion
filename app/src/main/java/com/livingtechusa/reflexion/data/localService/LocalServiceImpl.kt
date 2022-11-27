package com.livingtechusa.reflexion.data.localService

import com.livingtechusa.reflexion.data.dao.ReflexionItemDao
import com.livingtechusa.reflexion.data.dao.KeyWordsDao
import com.livingtechusa.reflexion.data.dao.LinkedListDao
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.entities.KeyWords
import com.livingtechusa.reflexion.data.entities.LinkedList
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
        reflexionItemDao.updateReflexionItem(item.autogenPK, item.name, item.description, item.detailedDescription, item.image, item.videoUri, item.videoUrl, item.parent, item.hasChildren)
    }

    override suspend fun getAllItems(): List<ReflexionItem?> {
        return reflexionItemDao.getAllReflexionItems()
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

    override suspend fun setLinkedList(linkedList: LinkedList) {
        linkedListDao.setLinkedList(linkedList)
    }

    override suspend fun getAllLinkedLists(): LinkedList? {
        return linkedListDao.getAllLinkedLists()
    }

    override suspend fun clearLinkedList() {
        linkedListDao.clearLinkedList()
    }

    override suspend fun selectLinkedList(title: String, itemPK: Long): LinkedList? {
        return linkedListDao.selectLinkedList(title, itemPK)
    }

    override suspend fun deleteLinkedList(title: String, itemPK: Long) {
        linkedListDao.deleteLinkedList(title, itemPK)
    }

    override suspend fun setLinkedListIndex(title: String, itemPK: Long, index: Int) {
        linkedListDao.setLinkedListIndex(title, itemPK, index)
    }

    override suspend fun renameLinkedList(title: String, itemPK: Long, newTitle: String) {
        linkedListDao.renameLinkedList(title, itemPK, newTitle)
    }

    override suspend fun selectReflexionItemByName(name: String): ReflexionItem {
        return reflexionItemDao.selectReflexionItemByName(name)
    }

    override suspend fun selectChildLinkedLists(parent: Long): List<LinkedList?> {
        return linkedListDao.selectChildLinkedLists(parent)
    }


}
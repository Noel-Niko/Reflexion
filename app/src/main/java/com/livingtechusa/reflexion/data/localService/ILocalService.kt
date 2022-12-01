package com.livingtechusa.reflexion.data.localService

import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.entities.KeyWords
import com.livingtechusa.reflexion.data.entities.LinkedList

interface ILocalService {
    suspend fun setItem(item: ReflexionItem)
    suspend fun updateReflexionItem(item: ReflexionItem)
    suspend fun getAllItems(): List<ReflexionItem?>
    suspend fun clearALLReflexionItems()
    suspend fun selectItem(autogenPK: Long): ReflexionItem?
    suspend fun deleteReflexionItem(autogenPK: Long, name: String)
    suspend fun renameItem(autogenPK: Long, name: String, newName: String)
    suspend fun selectChildItems(parent: Long): List<ReflexionItem?>
    suspend fun setItemParent(autogenPK: Long, name: String, newParent: Long)

    suspend fun setKeyWords(keyWords: KeyWords)
    suspend fun getKeyWords(): KeyWords?
    suspend fun clearKeyWords()
    suspend fun selectKeyWords(item_pk: String): List<KeyWords?>
    suspend fun deleteKeyWord(word: String)
    suspend fun renameKeyWord(word: String, newWord: String)

    suspend fun setLinkedList(linkedList: LinkedList)
    suspend fun getAllLinkedLists(): LinkedList?
    suspend fun clearLinkedList()
    suspend fun selectLinkedList(title: String, itemPK: Long): LinkedList?
    suspend fun deleteLinkedList(title: String, itemPK: Long)
    suspend fun setLinkedListIndex(title: String, itemPK: Long, index: Int)
    suspend fun selectChildLinkedLists(parent: Long): List<LinkedList?>
    suspend fun renameLinkedList(title: String, itemPK: Long, newTitle: String)
    suspend fun selectReflexionItemByName(name: String): ReflexionItem
    suspend fun selectChildren(pk: Long): List<ReflexionItem?>
    suspend fun getAllTopics(): List<ReflexionItem?>
    suspend fun getAllTopicsContainingString(search: String): List<ReflexionItem?>
    suspend fun selectChildrenContainingString(pk: Long, search: String?): List<ReflexionItem?>
}
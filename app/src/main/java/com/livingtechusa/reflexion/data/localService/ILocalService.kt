package com.livingtechusa.reflexion.data.localService

import androidx.room.Query
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.entities.KeyWords
import com.livingtechusa.reflexion.data.entities.ListNode
import org.w3c.dom.NodeList

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


    suspend fun selectReflexionItemByName(name: String): ReflexionItem
    suspend fun selectChildren(pk: Long): List<ReflexionItem?>
    suspend fun getAllTopics(): List<ReflexionItem?>
    suspend fun getAllTopicsContainingString(search: String): List<ReflexionItem?>
    suspend fun selectChildrenContainingString(pk: Long, search: String?): List<ReflexionItem?>
    suspend fun selectSiblings(pk: Long, parent: Long): List<ReflexionItem?>
    suspend fun setKeyWords(keyWords: KeyWords)
    suspend fun getKeyWords(): KeyWords?
    suspend fun clearKeyWords()
    suspend fun selectKeyWords(item_pk: String): List<KeyWords?>
    suspend fun deleteKeyWord(word: String)
    suspend fun renameKeyWord(word: String, newWord: String)


    suspend fun deleteAllChildNodes(nodePk: Long)

    suspend fun insertNewNode(listNode: ListNode)

    suspend fun selectNodeHeadsByTopic(topicPk: Long): List<ListNode?>

    suspend fun selectChildNode(nodePk: Long): ListNode?

    suspend fun selectParentNode(parentPk: Long): ListNode?

    suspend fun updateListNode(nodePk: Long, title: String, parentPK: Long, childPk: Long)

    suspend fun getAllLinkedLists(): List<ListNode?>

    suspend fun deleteAllLinkedLists()

    suspend fun selectLinkedList(nodePk: Long): ListNode?

    suspend fun deleteSelectedNode(nodePk: Long)
}
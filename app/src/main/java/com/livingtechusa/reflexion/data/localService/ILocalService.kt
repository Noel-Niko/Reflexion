package com.livingtechusa.reflexion.data.localService

import android.graphics.Bitmap
import android.net.Uri
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.entities.Bookmarks
import com.livingtechusa.reflexion.data.entities.ListNode
import com.livingtechusa.reflexion.data.models.AbridgedReflexionItem
import com.livingtechusa.reflexion.data.models.ReflexionArrayItem

interface ILocalService {
    // REFLEXION ITEMS
    suspend fun saveNewItem(item: ReflexionItem): Long
    suspend fun updateReflexionItemImage(item: ReflexionItem, priorImagePk: Long?)
    suspend fun updateReflexionItemVideoUri(item: ReflexionItem, newUri: Uri)
    suspend fun getAllItems(): List<ReflexionItem?>
    suspend fun selectReflexionItemByPk(autogenPK: Long?): ReflexionItem?
    suspend fun selectImagePkForItem(autogenPK: Long): Long?
    suspend fun deleteReflexionItem(autogenPK: Long, name: String, imagePk: Long?)
    suspend fun renameItem(autogenPK: Long, name: String, newName: String)
    suspend fun setItemParent(autogenPK: Long, name: String, newParent: Long)
    suspend fun selectReflexionItemByName(name: String): ReflexionItem
    suspend fun selectItemByUri(uri: String): ReflexionItem?
    suspend fun selectChildren(pk: Long): List<ReflexionItem?>
    suspend fun getAllTopics(): List<ReflexionItem?>
    suspend fun getAllItemsContainingString(search: String): List<ReflexionItem?>
    suspend fun getAllTopicsContainingString(search: String): List<ReflexionItem?>
    suspend fun selectChildrenContainingString(pk: Long, search: String?): List<ReflexionItem?>
    suspend fun selectSiblings(pk: Long, parent: Long): List<ReflexionItem?>
    suspend fun selectAllSiblings(parent: Long): List<ReflexionItem?>
    suspend fun selectParent(pk: Long): Long?

    // IMAGES AND ASSOCIATIONS
    suspend fun deleteImageAndAssociation(imagePk: Long, itemPk: Long)

    // ABRIDGED REFLEXION ITEMS
    suspend fun selectAbridgedReflexionItemDataByParentPk(pk: Long?): List<AbridgedReflexionItem?>
    suspend fun selectSingleAbridgedReflexionItemDataByParentPk(pk: Long): AbridgedReflexionItem
    suspend fun selectReflexionArrayItemsByParentPk(pk: Long?): List<ReflexionArrayItem?>
    suspend fun selectReflexionArrayItemByPk(pk: Long): ReflexionArrayItem?
    suspend fun selectImage(imagePk: Long): Bitmap?

    // LINKED LIST NODES
    suspend fun deleteAllChildNodes(nodePk: Long)
    suspend fun insertNewNode(listNode: ListNode): Long
    suspend fun selectListNode(nodePk: Long): ListNode?
    suspend fun selectNodeHeadsByTopic(topicPk: Long): List<ListNode?>
    suspend fun selectNodeTopic(itemPk: Long): Long?
    suspend fun selectNodeListsAsArrayItemsByTopic(topicPk: Long): List<ReflexionArrayItem?>
    suspend fun selectNodeListsAsArrayItemsByHeadNode(topicPk: Long?): ReflexionArrayItem?
    suspend fun selectChildNode(nodePk: Long): ListNode?
    suspend fun selectParentNode(parentPk: Long): ListNode?
    suspend fun updateListNode(nodePk: Long, title: String, topicPk: Long, itemPk: Long, parentPK: Long?, childPk: Long?)
    suspend fun getAllLinkedLists(): List<ListNode?>
    suspend fun deleteAllLinkedLists()
    suspend fun deleteSelectedNode(nodePk: Long)
    suspend fun insertNewOrUpdateNodeList(arrayItem: ReflexionArrayItem, topic: Long): Long?

    suspend fun removeLinkNodesForDeletedLists()

    // BOOKMARKS
    suspend fun setBookMarks(bookMark: Bookmarks)
    suspend fun getBookMarks(): List<Bookmarks?>
    suspend fun clearBookMarks()
    suspend fun selectItemBookMark(item_pk: Long): Bookmarks?
    suspend fun selectListBookMarks(list_pk: Long): Bookmarks?
    suspend fun deleteBookmark(autoGenPk: Long)
    suspend fun renameKeyWord(word: String, newWord: String)
    suspend fun searchBookmarksByTitle(text: String): List<Bookmarks?>
    suspend fun selectLevelBookMarks(): List<Bookmarks?>
    suspend fun selectBookmarkByLevelPK(levelPk: Long?): Bookmarks?
}
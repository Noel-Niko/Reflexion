package com.livingtechusa.reflexion.data.localService

import android.graphics.Bitmap
import com.livingtechusa.reflexion.data.Converters
import com.livingtechusa.reflexion.data.dao.BookMarksDao
import com.livingtechusa.reflexion.data.dao.ImagesDao
import com.livingtechusa.reflexion.data.dao.LinkedListDao
import com.livingtechusa.reflexion.data.dao.ReflexionItemDao
import com.livingtechusa.reflexion.data.entities.Bookmarks
import com.livingtechusa.reflexion.data.entities.Image
import com.livingtechusa.reflexion.data.entities.ItemImageAssociativeData
import com.livingtechusa.reflexion.data.entities.ListNode
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.models.AbridgedReflexionItem
import com.livingtechusa.reflexion.data.models.ReflexionArrayItem
import com.livingtechusa.reflexion.data.toListNode
import com.livingtechusa.reflexion.data.toReflexionArrayItem
import com.livingtechusa.reflexion.util.Constants.EMPTY_PK
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalServiceImpl @Inject constructor(
    private val reflexionItemDao: ReflexionItemDao,
    private val bookMarksDao: BookMarksDao,
    private val linkedListDao: LinkedListDao,
    private val imagesDao: ImagesDao
) : ILocalService {
    companion object {
        val TAG = "LocalServiceImpl"
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    override suspend fun saveNewItem(item: ReflexionItem): Long {
        val itemPk = reflexionItemDao.setReflexionItem(item)
        var newImagePk: Long? = null
        if (item.imagePk == null || (item.image != null && item.image?.isNotEmpty() == true)) {
            val itemWithPk = item.copy(autogenPk = itemPk)
            newImagePk = linkSavedOrAddNewImageAndAssociation(itemWithPk)
        }
        val saveItem = item.copy(autogenPk = itemPk, imagePk = newImagePk ?: item.imagePk, image = null)
        return reflexionItemDao.setReflexionItem(saveItem)
    }

    private suspend fun linkSavedOrAddNewImageAndAssociation(item: ReflexionItem): Long? {
        val _imagePk = CoroutineScope(Dispatchers.IO).async {
            var pkofImage: Long? = null
            if (item.image != null) {
                val existingImage =
                    item.image.let { imagesDao.selectImagePKByByteArray(item.image!!) }
                if (existingImage != null) {
                     imagesDao.insertImageAssociation(
                        ItemImageAssociativeData(
                            itemPk = item.autogenPk,
                            imagePk = existingImage
                        )
                    )
                    pkofImage = existingImage
                } else {
                    if (item.image != null) {
                        // Add to Images
                        val newImage = imagesDao.insertImage(Image(imagePk = 0L, image = item.image!!))
                        // Record Association
                        imagesDao.insertImageAssociation(
                            ItemImageAssociativeData(
                                itemPk = item.autogenPk,
                                imagePk = newImage
                            )
                        )
                        pkofImage = newImage
                    }
                }
                imagesDao.deleteUnusedImages()
                return@async pkofImage
            } else {
                return@async null
            }
        }
        return _imagePk.await()
    }

    override suspend fun updateReflexionItem(item: ReflexionItem, priorImagePk: Long?) {
        // Remove old association data if applicable
        if (priorImagePk != item.imagePk) {
            if (priorImagePk != null) {
                imagesDao.removeImageAssociation(itemPk = priorImagePk)
            }
        }
        // generate a new imagePk if applicable
        var newImagePk: Long? = null
        if (item.imagePk == null && item.image != null && item.image?.isNotEmpty() == true) {
            newImagePk = linkSavedOrAddNewImageAndAssociation(item)
        }

        reflexionItemDao.updateReflexionItem(
            item.autogenPk,
            item.name,
            item.description,
            item.detailedDescription,
            newImagePk ?: item.imagePk,
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

    override suspend fun getAllItemsContainingString(search: String): List<ReflexionItem?> {
        return reflexionItemDao.getAllItemsContainingString(search)
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

    override suspend fun selectItem(autogenPK: Long): ReflexionItem? {
        return reflexionItemDao.selectReflexionItem(autogenPK)
    }

    override suspend fun selectImagePkForItem(autogenPK: Long): Long? {
        return reflexionItemDao.selectImagePkForItem(autogenPK)
    }

    override suspend fun deleteReflexionItem(autogenPK: Long, name: String, imagePk: Long?) {
        if (imagePk != null) {
            imagesDao.removeImageAssociation(itemPk = autogenPK)
        }
        imagesDao.deleteUnusedAssociations()
        val imageUses = imagePk?.let { imagesDao.countImagePkUses(imagePk = it) }
        if(imageUses != null && imageUses <= 1) {
            imagesDao.deleteImage(imagePk = imagePk)
        }
        reflexionItemDao.deleteReflexionItem(autogenPK, name)
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
                    itemPK = it?.autogenPk ?: 0L,
                    itemName = it?.name ?: EMPTY_STRING,
                    nodePk = 0L,
                    children = mutableListOf()
                )
                result.add(Rai)
            }
            return result
        } else {
            val result: MutableList<ReflexionArrayItem> = mutableListOf()
            reflexionItemDao.selectAbridgedReflexionItemDataByParentPk(pk).forEach() {
                val Rai =
                    ReflexionArrayItem(
                        itemPK = it?.autogenPk ?: 0L,
                        itemName = it?.name ?: EMPTY_STRING,
                        nodePk = 0L,
                        children = mutableListOf()
                    )
                result.add(Rai)
            }
            return result
        }
    }

    override suspend fun selectReflexionArrayItemByPk(pk: Long): ReflexionArrayItem? {
        val _result: Deferred<AbridgedReflexionItem?> = scope.async {
            reflexionItemDao.selectSingleAbridgedReflexionItem(pk)
        }
        return _result.await()?.toReflexionArrayItem()
    }

    override suspend fun selectParent(pk: Long): Long? {
        return reflexionItemDao.getParent(pk)
    }

    override suspend fun deleteImageAndAssociation(imagePk: Long, itemPk: Long) {
        imagesDao.removeImageAssociation(itemPk)
        val useCount = imagesDao.getAssociationUseCount(itemPk)
        if(useCount < 1) imagesDao.deleteImage(imagePk = imagePk)
    }

    override suspend fun insertNewOrUpdateNodeList(
        arrayItem: ReflexionArrayItem,
        topic: Long
    ): Long? {
        val nodeList: List<ListNode?> = arrayItem.toListNode(topic, arrayItem.nodePk)
        // Delete prior children
        arrayItem.nodePk?.let { deleteAllChildNodes(it) }
        var parent: Long? = null
        var headNodePk: Long? = null
        var tailNode: Long? = null
        var count = 0
        nodeList.forEach { listNode ->
            if (listNode != null) {
                val updated = listNode.copy(parentPk = parent)
                parent = linkedListDao.insertNewNode(updated)
                if (count == 0) {
                    headNodePk = parent
                }
                tailNode = listNode.nodePk
                count++
            }
        }
        return headNodePk
    }

    override suspend fun selectImage(imagePk: Long): Bitmap? {
        val byteArray: ByteArray? = reflexionItemDao.selectImage(imagePk)
        return if (byteArray != null) {
            Converters().getBitmapFromByteArray(byteArray = byteArray)
        } else {
            null
        }
    }

    override suspend fun setBookMarks(bookMark: Bookmarks) {
        bookMarksDao.setBookMarks(bookMark)
    }

    override suspend fun getBookMarks(): List<Bookmarks?> {
        return bookMarksDao.getBookMarks()
    }

    override suspend fun clearBookMarks() {
        bookMarksDao.clearBookMarks()
    }

    override suspend fun selectItemBookMark(item_pk: Long): Bookmarks? {
        return bookMarksDao.selectItemBookMark(item_pk)
    }

    override suspend fun selectListBookMarks(list_pk: Long): Bookmarks? {
        return bookMarksDao.selectListBookMarks(list_pk)
    }

    override suspend fun selectSingleAbridgedReflexionItemDataByParentPk(pk: Long): AbridgedReflexionItem {
        return reflexionItemDao.selectSingleAbridgedReflexionItemDataByParentPk(pk)
    }


    override suspend fun selectSiblings(pk: Long, parent: Long): List<ReflexionItem?> {
        return reflexionItemDao.selectSiblings(pk, parent)
    }

    override suspend fun selectAllSiblings(parent: Long): List<ReflexionItem?> {
        val grandparent: Long = reflexionItemDao.getParent(parent) ?: EMPTY_PK
        return if (grandparent == EMPTY_PK) {
            reflexionItemDao.getReflexionItemTopics()
        } else {
            reflexionItemDao.selectAllSiblings(grandparent)
        }
    }

    override suspend fun deleteBookmark(autoGenPk: Long) {
        bookMarksDao.deleteBookmark(autoGenPk)
    }

    override suspend fun renameKeyWord(word: String, newWord: String) {
        bookMarksDao.renameKeyWord(word, newWord)
    }

    override suspend fun searchBookmarksByTitle(text: String): List<Bookmarks?> {
        return bookMarksDao.searchBookmarksByTitle(text)
    }

    override suspend fun selectLevelBookMarks(): List<Bookmarks?> {
        return bookMarksDao.selectLevelBookMarks()
    }

    override suspend fun selectBookmarkByLevelPK(levelPk: Long?): Bookmarks? {
        return bookMarksDao.selectBookmarkByLevelPK(levelPk)
    }

    override suspend fun deleteAllChildNodes(nodePk: Long) {
        var child = linkedListDao.selectChildNode(nodePk = nodePk)
        do {
            val grandChild: ListNode? =
                child?.nodePk?.let { linkedListDao.selectChildNode(nodePk = it) }
            // val parent: Long? = child?.nodePk
            if (child?.nodePk != null) {
                linkedListDao.deleteSelectedNode(child.nodePk)
            }
            child = grandChild
        } while (child != null)
    }

    override suspend fun insertNewNode(listNode: ListNode): Long {
        return linkedListDao.insertNewNode(listNode = listNode)
    }

    override suspend fun selectListNode(nodePk: Long): ListNode? {
        return linkedListDao.selectListNode(nodePk)
    }

    override suspend fun selectNodeHeadsByTopic(topicPk: Long): List<ListNode?> {
        return linkedListDao.selectNodeHeadsByTopic(topicPk)
    }

    override suspend fun selectNodeTopic(itemPk: Long): Long? {
        return linkedListDao.selectNodeTopic(itemPk)
    }

    override suspend fun selectNodeListsAsArrayItemsByTopic(topicPk: Long): List<ReflexionArrayItem> {
        // get all of the nodes by topic
        val nodeList: MutableList<ListNode?> =
            linkedListDao.selectAllNodesByTopic(topicPk = topicPk).toMutableList()
        // sort them into lists by parent and child
        val headNodeList = mutableListOf<ListNode>()
        val reflexionArrayItems = mutableListOf<ReflexionArrayItem>()
        val job = CoroutineScope(Dispatchers.IO).launch {
            // identify all the list head nodes
            nodeList.filter() {
                it?.parentPk == null
            }.forEach() { listNode ->
                if (listNode != null) {
                    headNodeList.add(listNode)
                }
            }

            // recursive function
            fun getChild(itemPk: Long): ListNode? {
                return nodeList.filter { it?.parentPk == itemPk }.firstOrNull()
            }
            // get the children for each head node
            headNodeList.forEach() { headNode ->
                val children = mutableListOf<ListNode>()
                var child = getChild(headNode.nodePk)
                // recursively get all the children for each head node
                while (child != null) {
                    children.add(child)
                    child = getChild(child.nodePk)
                }
                val reflexionArrayItem = headNode.toReflexionArrayItem()
                children.forEach { node ->
                    reflexionArrayItem.children.add(node.toReflexionArrayItem())
                }
                reflexionArrayItems.add(reflexionArrayItem)
            }
        }
        job.join()
        return reflexionArrayItems
    }

    override suspend fun selectNodeListsAsArrayItemsByHeadNode(topicPk: Long?): ReflexionArrayItem? {
        // get all of the nodes by topic
        val node: ListNode? =
            topicPk?.let { linkedListDao.selectListNode(it) }

        var reflexionArrayItem: ReflexionArrayItem? = null
        val children = mutableListOf<ListNode>()
        val result = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                var parent: ListNode? = node
                suspend fun getChild(itemPk: Long): ListNode? {
                    val next: ListNode? = linkedListDao.selectChildNode(itemPk)
                    if (next != null) {
                        children.add(next)
                    }
                    parent = next
                    return next
                }
                // recursively get all the children for each head node
                while (parent?.nodePk != null) {
                    getChild(parent?.nodePk!!)
                }
                val nodeRAI = node?.toReflexionArrayItem()
                children.forEach { childNodes ->
                    nodeRAI?.children?.add(childNodes.toReflexionArrayItem())
                }
                reflexionArrayItem = nodeRAI
            }
        }
        result.join()
        return reflexionArrayItem
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

    override suspend fun selectItemByUri(uri: String): ReflexionItem? {
        return reflexionItemDao.selectItemByUri(uri)
    }
}

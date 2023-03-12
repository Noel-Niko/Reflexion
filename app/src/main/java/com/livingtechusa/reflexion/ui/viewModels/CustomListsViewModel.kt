package com.livingtechusa.reflexion.ui.viewModels

import android.content.Context
import android.content.Intent
import android.content.Intent.EXTRA_STREAM
import android.content.Intent.EXTRA_TEXT
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.Converters
import com.livingtechusa.reflexion.data.entities.Bookmarks
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import com.livingtechusa.reflexion.data.models.ReflexionArrayItem
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.ui.customLists.CustomListEvent
import com.livingtechusa.reflexion.util.BaseApplication
import com.livingtechusa.reflexion.util.Constants.EMPTY_PK
import com.livingtechusa.reflexion.util.Constants.EMPTY_PK_STRING
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import com.livingtechusa.reflexion.util.Constants.NULL
import com.livingtechusa.reflexion.util.scopedStorageUtils.FileResource
import com.livingtechusa.reflexion.util.scopedStorageUtils.SafeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class CustomListsViewModel @Inject constructor(
    private val localServiceImpl: LocalServiceImpl
) : ViewModel() {
    companion object {
        private val TAG = this::class.java.simpleName
    }

    private val TAG = "CustomListsViewModel"
    private val context: Context
        get() = BaseApplication.getInstance()

    private val emptyRai = ReflexionArrayItem(
        itemPK = 0L,
        itemName = "New List",
        nodePk = 0L,
        children = mutableListOf<ReflexionArrayItem>()
    )

    private val _customList = MutableStateFlow(
        ReflexionArrayItem(
            null,
            context.getString(R.string.list_title),
            null,
            mutableListOf<ReflexionArrayItem>()
        )
    )
    val customList: StateFlow<ReflexionArrayItem> get() = _customList

    private val _children = MutableStateFlow<List<ReflexionItem>>(emptyList())
    val children: StateFlow<List<ReflexionItem>> get() = _children

    private val _listOfLists = MutableStateFlow<List<ReflexionArrayItem?>>(emptyList())
    val listOfLists: StateFlow<List<ReflexionArrayItem?>> get() = _listOfLists

    private val _listImages = MutableStateFlow<List<Bitmap>>(emptyList())
    val listImages: StateFlow<List<Bitmap?>> get() = _listImages

    private val _childListImages = MutableStateFlow<List<Bitmap>>(emptyList())
    val childListImages: StateFlow<List<Bitmap?>> get() = _childListImages

    private val _childVideoUriResourceList = MutableStateFlow<List<FileResource>>(emptyList())
    val childVideoUriResourceList: StateFlow<List<FileResource?>> get() = _childVideoUriResourceList

    private val _selectedParent = MutableStateFlow(ReflexionItem())
    val selectedParent: StateFlow<ReflexionItem> get() = _selectedParent


    private var newList = true
    private var topic: Long = EMPTY_PK

    private suspend fun getTopic(pk: Long): Long {
        var childPk: Long? = pk
        // If pk is a topic, it's own pk should be returned.
        var parent = pk
        var hasParent = true
        while (hasParent) {
            childPk = childPk?.let { pKey -> localServiceImpl.selectParent(pKey) }
            if (childPk != null) {
                parent = childPk
            } else {
                hasParent = false
            }
        }
        return parent
    }

    private fun getListImages() {
        viewModelScope.launch {
            val bitmaps: MutableList<Bitmap> = mutableListOf()
            val job = async {
                listOfLists.value.forEach { topicItem ->
                    topicItem?.children?.get(0)?.itemPK?.let { itemPk ->
                        localServiceImpl.selectItem(itemPk)?.imagePk?.let { imagePk ->
                            localServiceImpl.selectImage(imagePk)
                                ?.let { bitmap -> bitmaps.add(bitmap) }
                        }
                    }
                }
            }
            job.join()
            _listImages.value = bitmaps
        }
    }

    private fun getChildListImages() {
        viewModelScope.launch {
            val bitmaps: MutableList<Bitmap> = mutableListOf()
            val job = async {
                _children.value.forEach { childReflexionItem ->
                    childReflexionItem.image?.let { bytes ->
                        Converters().getBitmapFromByteArray(bytes)
                            .let { bitmaps.add(it) }
                    }
                }
            }
            job.join()
            _childListImages.value = bitmaps
        }
    }

    private fun getVideoResource() {
        viewModelScope.launch {
            val resources: MutableList<FileResource> = mutableListOf()
            val job = async {
                try {
                    _children.value.forEach { reflexionItem ->
                        reflexionItem.videoUri?.let { s ->
                            Converters().convertStringToUri(s)?.let { uri ->
                                SafeUtils.getResourceByUriPersistently(context = context, uri = uri)
                            }
                        }
                            ?.let { fileResource -> resources.add(fileResource) }
                    }

                } catch (e: java.lang.Exception) {
                    Log.e(TAG, "Unable to get File Resource")
                }
            }
            job.join()
            _childVideoUriResourceList.value = resources
        }
    }

    private val item1 = ReflexionArrayItem(
        itemPK = null,
        itemName = context.getString(R.string.topics),
        0L,
        children = mutableListOf()
    )
    private val _itemTree = MutableStateFlow(item1)
    val itemTree: StateFlow<ReflexionArrayItem> get() = _itemTree

    init {
        viewModelScope.launch {
            _itemTree.value = newLevel(item1, getMore(item1.itemPK))
        }
    }

    private fun newLevel(
        Rai: ReflexionArrayItem,
        list: MutableList<ReflexionArrayItem>?
    ): ReflexionArrayItem {
        if (list != null) {
            Rai.children = list
        }
        return Rai
    }

    private suspend fun getMore(pk: Long?): MutableList<ReflexionArrayItem> {
        val _list = mutableListOf<ReflexionArrayItem>()
        val job = viewModelScope.async {
            localServiceImpl.selectReflexionArrayItemsByParentPk(pk).forEach() {
                val list: MutableList<ReflexionArrayItem> = getMore(it?.itemPK)
                val subLevel = it?.let { it1 -> newLevel(it1, list) }
                if (subLevel != null) {
                    _list.add(subLevel)
                }
            }
        }
        job.join()
        return _list
    }

    fun onTriggerEvent(event: CustomListEvent) {
        try {
            when (event) {

                is CustomListEvent.UpdateListName -> {
                    val newListItem = ReflexionArrayItem(
                        customList.value.itemPK,
                        customList.value.itemName,
                        customList.value.nodePk,
                        customList.value.children
                    )
                    newListItem.itemName = event.text
                    _customList.value = newListItem
                }

                is CustomListEvent.MoveItemUp -> {
                    val newArrayList = ReflexionArrayItem(
                        customList.value.itemPK,
                        customList.value.itemName,
                        customList.value.nodePk,
                        bubbleUp(_customList.value, event.index).toMutableList()
                    )
                    _customList.value = newArrayList
                }

                is CustomListEvent.MoveItemDown -> {
                    val newArrayList = ReflexionArrayItem(
                        customList.value.itemPK,
                        customList.value.itemName,
                        customList.value.nodePk,
                        bubbleDown(_customList.value, event.index).toMutableList()
                    )
                    _customList.value = newArrayList
                }

                is CustomListEvent.DeleteItemInList -> {
                    val items = mutableListOf<ReflexionArrayItem>()
                    for (index in customList.value.children.indices) {
                        if (index != event.index) {
                            items.add(customList.value.children[index])
                        }
                    }
                    val newArrayList = ReflexionArrayItem(
                        customList.value.itemPK,
                        customList.value.itemName,
                        customList.value.nodePk,
                        items
                    )
                    _customList.value = newArrayList
                }

                is CustomListEvent.Save -> {
                    viewModelScope.launch(Dispatchers.IO) {
                        withContext(Dispatchers.IO) {
                            val nodePk: Long? =
                                localServiceImpl.insertNewOrUpdateNodeList(customList.value, topic)
                            if (nodePk != null) {
                                val job = launch { updateCustomList(nodePk) }
                                job.join()
                            }
                            _listOfLists.value =
                                localServiceImpl.selectNodeListsAsArrayItemsByTopic(topic)
                            getListImages()
                        }
                    }
                }

                is CustomListEvent.DeleteList -> {
                    viewModelScope.launch {
                        val reflexionArrayItem = listOfLists.value[event.index]
                        reflexionArrayItem?.nodePk?.let { localServiceImpl.deleteSelectedNode(it) }
                        reflexionArrayItem?.children?.forEach() { rai ->
                            rai.nodePk?.let { localServiceImpl.deleteSelectedNode(it) }
                        }
                        _listOfLists.value =
                            localServiceImpl.selectNodeListsAsArrayItemsByTopic(topic)
                    }
                }

                is CustomListEvent.MoveToEdit -> {
                    val editList = listOfLists.value[event.index]
                    _customList.value = editList ?: emptyRai
                }

                is CustomListEvent.ReSet -> {
                    val newListItem = ReflexionArrayItem(
                        null,
                        context.getString(R.string.list_title),
                        null,
                        mutableListOf<ReflexionArrayItem>()
                    )
                    _customList.value = newListItem
                }

                is CustomListEvent.GetDisplayList -> {
                    viewModelScope.launch {
                        val newListItem =
                            localServiceImpl.selectNodeListsAsArrayItemsByHeadNode(event.headNodePk)
                        if (newListItem != null) {
                            _customList.value = newListItem
                            val newReflexionItemList = mutableListOf<ReflexionItem>()
                            newListItem.children.forEach() { reflexionArrayItem ->
                                reflexionArrayItem.itemPK?.let { pk ->
                                    localServiceImpl.selectItem(pk)
                                        ?.let { reflexionItem ->
                                            newReflexionItemList.add(
                                                reflexionItem
                                            )
                                        }
                                }
                            }
                            _children.value = newReflexionItemList
                            val job = async {
                                val job2 = async { getChildListImages() }
                                job2.join()
                                getVideoResource()
                            }
                            job.join()
                        }
                    }
                }

                is CustomListEvent.Bookmark -> {
                    viewModelScope.launch {
                        val bookMark = customList.value.itemName?.let {
                            Bookmarks(
                                autoGenPk = 0L,
                                ITEM_PK = null,
                                LIST_PK = customList.value.nodePk,
                                LEVEL_PK = null,
                                title = it
                            )
                        }
                        if (bookMark != null) {
                            localServiceImpl.setBookMarks(bookMark)
                        }
                    }
                }

                is CustomListEvent.SendText -> {
                    val shareIntent = Intent()
                    // Make Text
                    val title = customList.value.itemName + "\n"
                    var listItems = EMPTY_STRING
                    _children.value.forEach { reflexionItem ->
                        listItems += context.getString(R.string.title) + reflexionItem.name + "\n" +
                                context.getString(R.string.description) + reflexionItem.description + "\n" + context.getString(
                            R.string.detailedDescription
                        ) + reflexionItem.detailedDescription + "\n" + reflexionItem.videoUrl + "\n"
                    }
                    val sentWith = "\n\n" +
                            context.getString(R.string.sent_with_reflexion_from_the_google_play_store) + "\n" +
                            context.getString(R.string.reflexion_link)
                    val text = title + listItems + sentWith
                    shareIntent.putExtra(EXTRA_TEXT, text)
                    shareIntent.type = "text/*"

                    // If applicable add video
                    // Check for videoUri's
                    var count = 0
                    _children.value.forEach() {
                        if (it.videoUri != null && it.videoUri.equals(EMPTY_STRING).not())
                            count++
                    }
                    if (count > 0) {
                        shareIntent.action = Intent.ACTION_OPEN_DOCUMENT
                        shareIntent.action = Intent.ACTION_SEND_MULTIPLE
                        val uriList = mutableListOf<Uri>()
                        _children.value.forEach { reflexionItem ->
                            reflexionItem.videoUri?.let { uri ->
                                Converters().convertStringToUri(
                                    uri
                                )?.let { videoUri ->
                                    uriList.add(videoUri)
                                }
                            }
                        }
                        val parcelableUriList: ArrayList<Uri> = ArrayList(uriList)
                        shareIntent.putParcelableArrayListExtra(EXTRA_STREAM, parcelableUriList)
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    } else {
                        shareIntent.action = Intent.ACTION_SEND
                    }

                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(context, shareIntent, null)
                }

                else -> {}
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error: " + e.message + " with cause " + e.cause)
        }
    }

    private suspend fun updateCustomList(nodePk: Long) {
        val list = localServiceImpl.selectNodeListsAsArrayItemsByHeadNode(nodePk) ?: emptyRai
        _customList.value = list
    }

    private fun bubbleDown(
        newArrayListItem: ReflexionArrayItem,
        index: Int
    ): List<ReflexionArrayItem> {
        val newArrangement = mutableListOf<ReflexionArrayItem>()
        var temp: ReflexionArrayItem? = null
        for (item in newArrayListItem.children.indices) {
            if (item == index) {
                temp = newArrayListItem.children[index]
            } else {
                newArrangement.add(newArrayListItem.children[item])
                temp?.let {
                    newArrangement.add(it)
                    temp = null
                }
            }
        }
        return newArrangement
    }

    private fun bubbleUp(
        newArrayListItem: ReflexionArrayItem,
        index: Int
    ): List<ReflexionArrayItem> {
        val newArrangement = mutableListOf<ReflexionArrayItem>()
        for (item in newArrayListItem.children.indices) {
            if (item == index) {
                val temp = newArrangement.last()
                newArrangement.removeLast()
                newArrangement.add(newArrayListItem.children[item])
                newArrangement.add(temp)
            } else {
                newArrangement.add(newArrayListItem.children[item])
            }
        }
        return newArrangement
    }

    fun selectItem(itemPk: String?) {
        // Ignore the empty Topic label
        if (itemPk.isNullOrEmpty().not() && itemPk.equals(EMPTY_PK_STRING)
                .not() && itemPk.equals("null").not()
        ) {
            viewModelScope.launch() {
                withContext(Dispatchers.IO) {

                    // First item selected in new topic in new list. Set Topic, and add selection and load topic lists
                    if (topic == EMPTY_PK || newList) {
                        if (itemPk != null) {
                            // Set the topic
                            topic = getTopic(itemPk.toLong())
                            // Add the first list item
                            addItemToList(itemPk)
                            newList = false
                            val newList =
                                async { localServiceImpl.selectNodeListsAsArrayItemsByTopic(topic) }
                            _listOfLists.value = newList.await()
                            getListImages()
                        }

                        // A new topic item has been selected, create new list with selected first item, and load related lists.
                    } else if (itemPk?.toLong()?.let { getTopic(it) } != topic) {
                        topic = itemPk?.toLong()?.let { getTopic(it) } ?: EMPTY_PK
                        // Reset the UI with the new list item
                        withContext(Dispatchers.Main) { _customList.value = emptyRai }
                        addItemToList(itemPk)
                        // Get that topic's lists
                        val newList =
                            async { localServiceImpl.selectNodeListsAsArrayItemsByTopic(topic) }
                        _listOfLists.value = newList.await()
                        getListImages()
                    } else {
                        /* If we are adding to an existing list, only allowing items under the same topic,
                      we copy to list and add the newly selected child.
                       */
                        newList = false
                        addItemToList(itemPk)
                    }
                }
            }
        }
    }

    private suspend fun addItemToList(itemPk: String?) {
        val newListItem = ReflexionArrayItem(
            customList.value.itemPK,
            customList.value.itemName,
            customList.value.nodePk,
            customList.value.children
        )
        if (itemPk != null) {
            if (itemPk.isEmpty().not() && (itemPk == "null").not()) {
                itemPk.toLong().let { pk ->
                    localServiceImpl.selectReflexionArrayItemByPk(pk)
                        ?.let { newListItem.children.add(it) }
                }
            }
        }
        _customList.value = newListItem
    }

    fun selectParentItem(itemPk: String?) {
        // Ignore the empty Topic label
        if (itemPk != null && itemPk != EMPTY_STRING && (itemPk == EMPTY_PK_STRING).not() && (itemPk == NULL).not()
        ) {
            viewModelScope.launch() {
                withContext(Dispatchers.Main) {
                    // First item selected in new topic in new list. Set Topic, and add selection and load topic lists
                    if (topic == EMPTY_PK || newList) {
                        // Set the topic
                        topic = getTopic(itemPk.toLong())
                        // Add the first list item
                        setParent(itemPk)
                        newList = false
                        val newList =
                            async { localServiceImpl.selectNodeListsAsArrayItemsByTopic(topic) }
                        _listOfLists.value = newList.await()

                        // A new topic item has been selected, create new list with selected first item, and load related lists.
                    } else if (getTopic(itemPk.toLong()) != topic) {
                        topic = itemPk.toLong().let { getTopic(it) }
                        // Reset the UI with the new list item
                        withContext(Dispatchers.Main) { _customList.value = emptyRai }
                        setParent(itemPk)
                        // Get that topic's lists
                        val newList =
                            async { localServiceImpl.selectNodeListsAsArrayItemsByTopic(topic) }
                        _listOfLists.value = newList.await()

                    } else {
                        /* If we are adding to an existing list, only allowing items under the same topic,
                        we copy to list and add the newly selected child.
                         */
                        newList = false
                        setParent(itemPk)
                    }
                }
            }
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.an_error_occurred_please_try_again),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setParent(itemPk: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val selectedItem = localServiceImpl.selectItem(itemPk.toLong())
            if (selectedItem != null) {
                _selectedParent.value = selectedItem
                val parent = selectedParent.value.name
                Toast.makeText(
                    context,
                    "Setting $parent as the new parent.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.an_error_occurred_please_try_again),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun sendPKToBuildViewModel(parent: ReflexionItem, buildItemViewModel: BuildItemViewModel) {
        buildItemViewModel.onTriggerEvent(BuildEvent.SetSelectedParent(parent))
    }

}

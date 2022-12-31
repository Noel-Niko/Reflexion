package com.livingtechusa.reflexion.ui.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import com.livingtechusa.reflexion.di.DefaultDispatcher
import com.livingtechusa.reflexion.ui.customLists.CustomListEvent
import com.livingtechusa.reflexion.util.BaseApplication
import com.livingtechusa.reflexion.util.Constants.EMPTY_PK_STRING
import com.livingtechusa.reflexion.util.ReflexionArrayItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class CustomListsViewModel @Inject constructor(
    private val localServiceImpl: LocalServiceImpl,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val TAG = "CustomListsViewModel"
    private val context: Context
        get() = BaseApplication.getInstance()

//    private val _parentList = MutableStateFlow(emptyList<AbridgedReflexionItem?>())
//    val topicsList: StateFlow<List<AbridgedReflexionItem?>> get() = _parentList
//
//    private val _childList = MutableStateFlow(emptyList<AbridgedReflexionItem?>())
//    val childList: StateFlow<List<AbridgedReflexionItem?>> get() = _childList

    private val emptyRai = ReflexionArrayItem(
        itemPK = 0L,
        itemName = "New List",
        nodePk = 0L,
        children = mutableListOf<ReflexionArrayItem>()
    )

    private val _customList = MutableStateFlow(
        ReflexionArrayItem(
            null,
            " List Title",
            null,
            mutableListOf<ReflexionArrayItem>()
        )
    )
    val customList: StateFlow<ReflexionArrayItem> get() = _customList


    private val _listOfLists = MutableStateFlow<List<ReflexionArrayItem?>>(emptyList())
    val listOfLists: StateFlow<List<ReflexionArrayItem?>> get() = _listOfLists

    private var newList = true
    private var topic: Long = -1L

    suspend fun getTopic(pk: Long): Long? {
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

    val item1 = ReflexionArrayItem(
        itemPK = null,
        itemName = "Topics",
        0L,
        children = mutableListOf()
    )
    private val _itemTree = MutableStateFlow(item1)
    val itemTree: StateFlow<ReflexionArrayItem> get() = _itemTree

    suspend fun hasChildren(pk: Long): Boolean {
        return localServiceImpl.selectChildren(pk).isNotEmpty()
    }

    init {
        viewModelScope.launch {
            _itemTree.value = newLevel(item1, getMore(item1.itemPK))
        }
    }

    fun newLevel(
        Rai: ReflexionArrayItem,
        list: MutableList<ReflexionArrayItem>?
    ): ReflexionArrayItem {
        if (list != null) {
            Rai.children = list
        }
        return Rai
    }

    suspend fun getMore(pk: Long?): MutableList<ReflexionArrayItem> {
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
                                updateCustomList(nodePk)
                            }
                            _listOfLists.value =
                                localServiceImpl.selectNodeListsAsArrayItemsByTopic(topic)
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
                    _customList.value = listOfLists.value[event.index] ?: emptyRai
                }

                is CustomListEvent.ReSet -> {
                    newList = true
                    _customList.value.children = mutableListOf()
                    _customList.value = emptyRai
                }

                else -> {}
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}  with cause: ${e.cause}")
        }
    }

    private fun updateCustomList(nodePk: Long) {
        viewModelScope.launch {
            val list = localServiceImpl.selectNodeListsAsArrayItemsByHeadNode(nodePk) ?: emptyRai
            _customList.value = list
        }
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
                    if (topic == -1L || newList) {
                        if (itemPk != null) {
                            // Set the topic
                            topic = getTopic(itemPk.toLong()) ?: itemPk.toLong()
                            // Add the first list item
                            addItemToList(itemPk)
                            newList = false
                            val newList =
                                async { localServiceImpl.selectNodeListsAsArrayItemsByTopic(topic) }
                            _listOfLists.value = newList.await()

                        }

                        // A new topic item has been selected, create new list with selected first item, and load related lists.
                    } else if (itemPk?.toLong()?.let { getTopic(it) } != topic) {
                        topic = itemPk?.toLong()?.let { getTopic(it) } ?: -1L
                        // Reset the UI with the new list item
                        withContext(Dispatchers.Main) { _customList.value = emptyRai }
                        addItemToList(itemPk)
                        // Get that topic's lists
                        val newList =
                            async { localServiceImpl.selectNodeListsAsArrayItemsByTopic(topic) }
                        _listOfLists.value = newList.await()

                        /* If we are adding to an existing list, only allowing items under the same topic,
                        we copy to list and add the newly selected child.
                         */
                    } else {
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
                    localServiceImpl.selectReflexionArrayItemsByPk(pk)
                        ?.let { newListItem.children.add(it) }
                }
            }
        }
        _customList.value = newListItem
    }
}

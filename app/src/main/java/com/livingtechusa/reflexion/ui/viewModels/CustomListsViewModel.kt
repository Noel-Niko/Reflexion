package com.livingtechusa.reflexion.ui.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.data.entities.ReflexionItem
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

    private val _selectedItem = MutableStateFlow(ReflexionItem())
    val selectedItem: StateFlow<ReflexionItem> get() = _selectedItem

    private val emptyRai = ReflexionArrayItem(
        itemPK = 0L,
        itemName = "New List",
        nodePk = 0L,
        children = mutableListOf<ReflexionArrayItem>()
    )

    private val _customList = MutableStateFlow(emptyRai)
    val customList: StateFlow<ReflexionArrayItem> get() = _customList


    private val _listOfLists = MutableStateFlow<List<ReflexionArrayItem?>>(emptyList())
    val listOfLists: StateFlow<List<ReflexionArrayItem?>> get() = _listOfLists

    private var newList = true
    private var topic: Long = -1L

    suspend fun getTopic(pk: Long): Long? {
        var key: Long? = pk
        var parent: Long? = null
        var hasParent = true
        while (hasParent) {
            key = key?.let { it -> localServiceImpl.selectParent(it) }
            if (key != null) {
                parent = key
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
                is CustomListEvent.GetListsForTopic -> {
                    viewModelScope.launch {
                        val lists: List<ReflexionArrayItem> =
                            localServiceImpl.selectNodeListsAsArrayItemsByTopic(topic)
                        _listOfLists.value = lists
                    }
                }

                is CustomListEvent.UpdateListName -> {
                    val newListItem = ReflexionArrayItem(
                        _customList.value.itemPK,
                        _customList.value.itemName,
                        0L,
                        _customList.value.children
                    )
                    newListItem.itemName = event.text
                    _customList.value = newListItem
                }

                is CustomListEvent.MoveItemUp -> {
                    val newArrayList = ReflexionArrayItem(
                        customList.value.itemPK,
                        customList.value.itemName,
                        0L,
                        bubbleUp(_customList.value, event.index).toMutableList()
                    )
                    _customList.value = newArrayList
                }

                is CustomListEvent.MoveItemDown -> {
                    val newArrayList = ReflexionArrayItem(
                        customList.value.itemPK,
                        customList.value.itemName,
                        0L,
                        bubbleDown(_customList.value, event.index).toMutableList()
                    )
                    _customList.value = newArrayList
                }

                is CustomListEvent.Delete -> {
                    val items = mutableListOf<ReflexionArrayItem>()
                    for (index in customList.value.children.indices) {
                        if (index != event.index) {
                            items.add(customList.value.children[index])
                        }
                    }
                    val newArrayList = ReflexionArrayItem(
                        customList.value.itemPK,
                        customList.value.itemName,
                        0L,
                        items
                    )
                    _customList.value = newArrayList
                }

                is CustomListEvent.Save -> {
                    viewModelScope.launch(Dispatchers.IO) {
                        withContext(Dispatchers.IO) {
                            val nodePk: Long? = localServiceImpl.insertNewOrUpdateNodeList(customList.value, topic)
                            if (nodePk != null) {
                                updateCustomList(nodePk)
                            }
                            _listOfLists.value = localServiceImpl.selectNodeListsAsArrayItemsByTopic(topic)
                        }
                    }
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
        if (itemPk.isNullOrEmpty().not() && itemPk.equals(EMPTY_PK_STRING)
                .not() && itemPk.equals("null").not()
        ) {
            viewModelScope.launch() {
                withContext(Dispatchers.IO) {
                    if (newList) {
                        if (itemPk != null) {
                            topic = getTopic(itemPk.toLong()) ?: itemPk.toLong()
                            newList = false
                            _listOfLists.value =
                                localServiceImpl.selectNodeListsAsArrayItemsByTopic(topic)
                        }
                    }
                    if (itemPk?.toLong()?.let { localServiceImpl.selectNodeTopic(it) } != topic) {
                        newList = true
                        topic = itemPk?.toLong()?.let { getTopic(it) } ?: -1L
                        _listOfLists.value =
                            localServiceImpl.selectNodeListsAsArrayItemsByTopic(topic)
                    }
                    val newListItem = ReflexionArrayItem(
                        _customList.value.itemPK,
                        _customList.value.itemName,
                        0L,
                        _customList.value.children
                    )

                    if (itemPk.isNullOrEmpty().not() && itemPk.equals("null").not()) {
                        itemPk?.toLong()?.let { pk ->
                            localServiceImpl.selectReflexionArrayItemsByPk(pk)
                                ?.let { newListItem.children.add(it) }
                        }
                    }
                    _customList.value = newListItem
                }
            }
        }
    }
}

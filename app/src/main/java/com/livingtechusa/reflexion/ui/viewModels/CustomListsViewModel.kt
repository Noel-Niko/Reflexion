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

    private val _customList =
        MutableStateFlow(ReflexionArrayItem(0L, "New List", mutableListOf<ReflexionArrayItem>()))
    val customList: StateFlow<ReflexionArrayItem> get() = _customList


    private val _listOfLists = MutableStateFlow(listOf(customList.value))
    val listOfLists: StateFlow<List<ReflexionArrayItem>> get() = _listOfLists


    val item1 = ReflexionArrayItem(
        itemPK = null,
        itemName = "Topics",
        children = mutableListOf()
    )
    private val _itemTree = MutableStateFlow(item1)
    val itemTree: StateFlow<ReflexionArrayItem> get() = _itemTree

    suspend fun hasChildren(pk: Long): Boolean {
        return localServiceImpl.selectChildren(pk).isNotEmpty()
    }

    init {
        viewModelScope.launch {
            _itemTree.value = newLevel(item1, getMore(item1.reflexionItemPk))
        }
    }

    fun newLevel(
        Rai: ReflexionArrayItem,
        list: MutableList<ReflexionArrayItem>?
    ): ReflexionArrayItem {
        Rai.items = list
        return Rai
    }

    suspend fun getMore(pk: Long?): MutableList<ReflexionArrayItem> {
        val _list = mutableListOf<ReflexionArrayItem>()
        val job = viewModelScope.async {
            localServiceImpl.selectReflexionArrayItemsByParentPk(pk).forEach() {
                val list: MutableList<ReflexionArrayItem> = getMore(it?.reflexionItemPk)
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
                        _customList.value.reflexionItemPk,
                        _customList.value.reflexionItemName,
                        _customList.value.items
                    )
                    newListItem.reflexionItemName = event.text
                    _customList.value = newListItem
                }
                is CustomListEvent.MoveItemUp -> {
                    val newArrayList = ReflexionArrayItem(
                        customList.value.reflexionItemPk,
                        customList.value.reflexionItemName,
                        bubbleUp(_customList.value, event.index).toMutableList()
                    )
                   _customList.value = newArrayList
                }
                is CustomListEvent.MoveItemDown -> {
                    val newArrayList = ReflexionArrayItem(
                        customList.value.reflexionItemPk,
                        customList.value.reflexionItemName,
                        bubbleDown(_customList.value, event.index).toMutableList()
                    )
                    _customList.value = newArrayList
                }
                else -> {}
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}  with cause: ${e.cause}")
        }
    }

    private fun bubbleDown(newArrayListItem: ReflexionArrayItem, index: Int): List<ReflexionArrayItem> {
        val newArrangement = mutableListOf<ReflexionArrayItem>()
        var temp :ReflexionArrayItem?  = null
        for(item in newArrayListItem.items?.indices!!) {
            if (item == index) {
                temp = newArrayListItem.items!![index]
            } else {
                newArrangement.add(newArrayListItem.items!![item])
                temp?.let {
                    newArrangement.add(it)
                    temp = null
                }
            }
        }
        return newArrangement
    }

    private fun bubbleUp(newArrayListItem: ReflexionArrayItem, index: Int): List<ReflexionArrayItem> {
        val newArrangement = mutableListOf<ReflexionArrayItem>()
        for(item in newArrayListItem.items!!.indices) {
            if (item == index) {
                val temp = newArrangement.last()
                newArrangement.removeLast()
                newArrangement.add(newArrayListItem.items!![item])
                newArrangement.add(temp)
            } else {
                newArrangement.add(newArrayListItem.items!![item])
            }
        }
        return newArrangement
    }

    fun selectItem(itemPk: String?) {
        if (itemPk.isNullOrEmpty().not() && itemPk.equals(EMPTY_PK_STRING).not()) {
            viewModelScope.launch {
                val newListItem = ReflexionArrayItem(
                    _customList.value.reflexionItemPk,
                    _customList.value.reflexionItemName,
                    _customList.value.items
                )
                withContext(defaultDispatcher) {
                    if (itemPk.isNullOrEmpty().not()) {
                        itemPk?.toLong()?.let { pk ->
                            localServiceImpl.selectReflexionArrayItemsByPk(pk)
                                ?.let { newListItem.items?.add(it) }
                        }
                    }
                }
                _customList.value = newListItem
            }

        }
    }
}

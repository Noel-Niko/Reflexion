package com.livingtechusa.reflexion.ui.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import com.livingtechusa.reflexion.data.models.AbridgedReflexionItem
import com.livingtechusa.reflexion.ui.customLists.CustomListEvent
import com.livingtechusa.reflexion.util.BaseApplication
import com.livingtechusa.reflexion.util.ReflexionArrayItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CustomListsViewModel @Inject constructor(
    private val localServiceImpl: LocalServiceImpl,
    private val state: SavedStateHandle
) : ViewModel() {

    private val TAG = "CustomListsViewModel"
    private val context: Context
        get() = BaseApplication.getInstance()

    private val _parentList = MutableStateFlow(emptyList<AbridgedReflexionItem?>())
    val topicsList: StateFlow<List<AbridgedReflexionItem?>> get() = _parentList

    private val _childList = MutableStateFlow(emptyList<AbridgedReflexionItem?>())
    val childList: StateFlow<List<AbridgedReflexionItem?>> get() = _childList

    private val _selectedItem = MutableStateFlow(ReflexionItem())
    val selectedItem: StateFlow<ReflexionItem> get() = _selectedItem

    var bigList = mutableListOf<MutableList<MutableList<AbridgedReflexionItem>>>()

    suspend fun hasChildren(pk: Long): Boolean {
        return localServiceImpl.selectChildren(pk).isNotEmpty()
    }

    init {
        viewModelScope.launch {
            _parentList.value = localServiceImpl.selectAbridgedReflexionItemDataByParentPk(null)
            val childList = mutableListOf<AbridgedReflexionItem>()
            topicsList.value.forEach() { abridgedParent ->
                localServiceImpl.selectAbridgedReflexionItemDataByParentPk(abridgedParent?.autogenPK)
                    .forEach() { abridgedChild ->
                        if (abridgedChild != null) {
                            childList.add(abridgedChild)
                        }
                    }
            }
            _childList.value = childList

            val item1 = ReflexionArrayItem(
                itemPK = null,
                itemName = "Topics",
                children = mutableListOf()
            )
            val bigList = newLevel(item1, getMore(item1.reflexionItemPk))
            println(bigList)

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
        viewModelScope.launch {
            try {
                when (event) {
//                    is CustomListEvent.GetChildList -> {
//                        _childList.value =
//                            localServiceImpl.selectChildren(event.pk) as List<ReflexionItem>
//                    }

                    else -> {
                        Toast.makeText(context, "No matching items", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.message}  with cause: ${e.cause}")
            }
        }
    }

    fun selectItem(itemPk: String?) {
        if (itemPk != null) {
            viewModelScope.launch {
                if (hasChildren(itemPk.toLong())) {
                    _parentList.value = childList.value
                    _childList.value =
                        localServiceImpl.selectAbridgedReflexionItemDataByParentPk(itemPk.toLong())
                }
            }
        }
    }
}
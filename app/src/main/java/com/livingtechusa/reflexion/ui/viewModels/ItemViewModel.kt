package com.livingtechusa.reflexion.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.entities.LinkedList
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import com.livingtechusa.reflexion.ui.build.BuildEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

const val STATE_KEY_URL = "com.livingtechusa.reflexion.ui.build.BuildItemScreen.url"

enum class ApiStatus { PRE_INIT, LOADING, ERROR, DONE }


@HiltViewModel
class ItemViewModel @Inject constructor (
    private val localServiceImpl: LocalServiceImpl
    ): ViewModel() {
    private val _displayedItem = MutableStateFlow(ReflexionItem())
    val displayedItem: StateFlow<ReflexionItem> get() = _displayedItem.asStateFlow()


    private val TAG = "ItemViewModel"
    private val _reflexionItem = MutableStateFlow(ReflexionItem())
    val reflexionItem: StateFlow<ReflexionItem> get() = _reflexionItem

    private val _isParent = MutableStateFlow(false)
    val isParent: StateFlow<Boolean> get() = _isParent

//    private val _topic = MutableStateFlow(String())
//    val topic: StateFlow<String?> get() = _topic

    private val _parentName = MutableStateFlow(String())
    val parentName: StateFlow<String?> get() = _parentName

    private val _keyWords = MutableStateFlow(emptyList<String>())
    val keyWords: StateFlow<List<String?>> get() = _keyWords

    private val _linkedLists = MutableStateFlow(emptyList<LinkedList>())
    val linkedLists: StateFlow<List<LinkedList?>> get() = _linkedLists

    private val _siblings = MutableStateFlow(emptyList<ReflexionItem>())
    val siblings: StateFlow<List<ReflexionItem?>> get() = _siblings

    private val _children = MutableStateFlow(emptyList<ReflexionItem>())
    val children: StateFlow<List<ReflexionItem?>> get() = _children

    init {
        _reflexionItem.value.name = "Start here"
    }

    fun onTriggerEvent(event: BuildEvent) {
        viewModelScope.launch {
            try {
                when (event) {
                    is BuildEvent.UpdateReflexionItem -> {
                        _reflexionItem.value = event.reflexionItem
                        localServiceImpl.updateReflexionItem(event.reflexionItem)
                    }
                    is BuildEvent.SaveNew -> {
                        localServiceImpl.setItem(event.reflexionItem)
                        _reflexionItem.value = localServiceImpl.selectReflexionItemByName(event.reflexionItem.name)
                        println("_")
                    }
                    is BuildEvent.Delete -> {
                        localServiceImpl.deleteReflexionItem(_reflexionItem.value.autogenPK, _reflexionItem.value.name)
                        _reflexionItem.value = ReflexionItem()
                    }
                    is BuildEvent.ShowChildren -> {
                        //ItemRecyclerView()
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Exception: ${e.message}  with cause: ${e.cause}"
                )
            }
        }
    }
}
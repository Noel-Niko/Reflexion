package com.livingtechusa.reflexion.ui.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import com.livingtechusa.reflexion.di.DefaultDispatcher
import com.livingtechusa.reflexion.ui.topics.ListEvent
import com.livingtechusa.reflexion.util.BaseApplication
import com.livingtechusa.reflexion.util.Constants.EMPTY_PK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TopicsViewModel @Inject constructor(
    private val localServiceImpl: LocalServiceImpl,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val TAG = "ListViewModel"

    private val context: Context
        get() = BaseApplication.getInstance()

    // Currently displayed list of items in the list
    private val _list = MutableStateFlow(emptyList<ReflexionItem>())
    val list: StateFlow<List<ReflexionItem>> get() = _list

    // Term for query
    private val _search = MutableStateFlow(null as String?)
    val search: StateFlow<String?> = _search

    suspend fun hasNoChildren(pk: Long): Boolean {
        return localServiceImpl.selectChildren(pk).isEmpty()
    }

    fun onTriggerEvent(event: ListEvent) {

        try {
            when (event) {
                is ListEvent.GetList -> {
                    viewModelScope.launch {
                        if (event.pk == null || event.pk == EMPTY_PK) {
                            _list.value = (localServiceImpl.getAllTopics() as? List<ReflexionItem>)
                                ?: emptyList()
                        } else {
                            val newList = withContext(defaultDispatcher) {
                                (localServiceImpl.selectChildren(event.pk) as? List<ReflexionItem>)
                                    ?: emptyList()
                            }
                            _list.value = newList
                        }
                    }
                }

                is ListEvent.Search -> {
                    viewModelScope.launch {
                        // Item keyword search
                        if (event.search.isNullOrEmpty()) {
                            _list.value = localServiceImpl.getAllTopics() as List<ReflexionItem>
                        } else {
                            _list.value =
                                localServiceImpl.getAllItemsContainingString(event.search) as List<ReflexionItem>
                        }
                    }
                }

                is ListEvent.ClearList -> {
                    _list.value = emptyList()
                }

                is ListEvent.UpOneLevel -> {
                    viewModelScope.launch {
                        val parent: Long = list.value?.get(0)?.parent ?: EMPTY_PK
                        if (parent == EMPTY_PK) {
                            val newList = localServiceImpl.getAllTopics() as List<ReflexionItem>
                            _list.value = newList
                        } else {
                            val newList =
                                localServiceImpl.selectAllSiblings(parent) as List<ReflexionItem>
                            _list.value = newList
                        }
                    }
                }

                else -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.no_matching_items),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Exception: ${e.message}  with cause: ${e.cause}"
            )
        }
    }

    fun searchEvent(term: String?) {
        _search.value = term
        onTriggerEvent(ListEvent.Search(term))
    }

    fun onUp() {
        if (list.value.isEmpty() || list.value[0].parent == null || list.value[0].parent == EMPTY_PK) {
            Toast.makeText(context, R.string.no_parent_found, Toast.LENGTH_SHORT).show()
        } else {
            onTriggerEvent(ListEvent.UpOneLevel)
        }
    }
}
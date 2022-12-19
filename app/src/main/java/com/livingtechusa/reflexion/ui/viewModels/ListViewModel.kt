package com.livingtechusa.reflexion.ui.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.asLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import com.livingtechusa.reflexion.di.DefaultDispatcher
import com.livingtechusa.reflexion.ui.list.ListEvent
import com.livingtechusa.reflexion.util.BaseApplication
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
class ListViewModel @Inject constructor(
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
                            //_list.value = emptyList()
                            if (event.pk == null || event.pk == -1L) {
                                _list.value = localServiceImpl.getAllTopics() as List<ReflexionItem>
                            } else {
                                val newList = withContext(defaultDispatcher) {
                                        localServiceImpl.selectChildren(event.pk) as List<ReflexionItem>
                                }
                              _list.value = newList
                            }
                        }
                    }

//                    is ListEvent.Search -> {
//                        if (event.pk == -1L) {
//                            if (event.search.isNullOrEmpty()) {
//                                _list.value = localServiceImpl.getAllTopics() as List<ReflexionItem>
//                            } else {
//                                // Topic key work search
//                                _list.value =
//                                    localServiceImpl.getAllTopicsContainingString(event.search) as List<ReflexionItem>
//                            }
//                        } else {
//                            // Children keyword search
//                            _list.value =
//                                localServiceImpl.selectChildrenContainingString(
//                                    event.pk,
//                                    event.search
//                                ) as List<ReflexionItem>
//                        }
//                    }
//
//                    is ListEvent.ClearList -> {
//                        _list.value = emptyList()
//                    }

                    else -> {
                        Toast.makeText(context, "No matching items", Toast.LENGTH_SHORT).show()
                    }

                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Exception: ${e.message}  with cause: ${e.cause}"
                )
            }

    }

    var listPK = 0L
    fun searchEvent(term: String?) {
        _search.value = term
        if (term.isNullOrEmpty()) {
            onTriggerEvent(ListEvent.GetList(listPK))
        } else {
            //onTriggerEvent(ListEvent.Search(term, reflexionItem.value.autogenPK))
        }
    }

}
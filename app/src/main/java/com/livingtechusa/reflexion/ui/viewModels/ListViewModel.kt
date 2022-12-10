package com.livingtechusa.reflexion.ui.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import com.livingtechusa.reflexion.ui.list.ListEvent
import com.livingtechusa.reflexion.util.BaseApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val localServiceImpl: LocalServiceImpl
) : ViewModel() {
    private val TAG = "ListViewModel"

    private val context: Context
        get() = BaseApplication.getInstance()

    private val _list = MutableStateFlow(emptyList<ReflexionItem>())
    val list: StateFlow<List<ReflexionItem>> get() = _list

    private val _search = MutableStateFlow(null as String?)
    val search: StateFlow<String?> = _search

    private val _reflexionItem = MutableStateFlow(ReflexionItem())
    val reflexionItem: StateFlow<ReflexionItem> get() = _reflexionItem


    fun searchEvent(term: String?) {
        _search.value = term
        if (term.isNullOrEmpty()) {
            onTriggerEvent(ListEvent.GetList(-1L))
        } else {
            onTriggerEvent(ListEvent.Search(term, reflexionItem.value.autogenPK))
        }
    }

    fun onTriggerEvent(event: ListEvent) {
        viewModelScope.launch {
            try {
                when (event) {
                    is ListEvent.GetList -> {
                        if (event.pk == -1L ) {
                            _list.value = localServiceImpl.getAllTopics() as List<ReflexionItem>
                        } else {
                            _list.value =
                                localServiceImpl.selectChildren(event.pk) as List<ReflexionItem>
                        }
                    }

                    is ListEvent.Search -> {
                        if (event.pk == -1L) {
                            if (event.search.isNullOrEmpty()) {
                                _list.value = localServiceImpl.getAllTopics() as List<ReflexionItem>
                            } else {
                                _list.value =
                                    localServiceImpl.getAllTopicsContainingString(event.search) as List<ReflexionItem>
                            }
                        } else {
                            _list.value =
                                localServiceImpl.selectChildrenContainingString(
                                    event.pk,
                                    event.search
                                ) as List<ReflexionItem>
                        }
                    }

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
    }
}
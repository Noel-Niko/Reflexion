package com.livingtechusa.reflexion.ui.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import com.livingtechusa.reflexion.ui.customLists.CustomListEvent
import com.livingtechusa.reflexion.ui.list.ListEvent
import com.livingtechusa.reflexion.util.BaseApplication
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _topicsList = MutableStateFlow(emptyList<ReflexionItem>())
    val topicsList: StateFlow<List<ReflexionItem>> get() = _topicsList

    private val _childList = MutableStateFlow(emptyList<ReflexionItem>())
    val childList: StateFlow<List<ReflexionItem>> get() = _childList

    private val _selectedItem = MutableStateFlow(ReflexionItem())
    val selectedItem: StateFlow<ReflexionItem> get() = _selectedItem


    init {
        viewModelScope.launch {
            _topicsList.value = (localServiceImpl.getAllTopics()
                ?: emptyList<ReflexionItem>()) as List<ReflexionItem>
        }
    }

    fun onTriggerEvent(event: CustomListEvent) {
        viewModelScope.launch {
            try {
                when (event) {
                    is CustomListEvent.GetChildList -> {
                        _childList.value =
                            localServiceImpl.selectChildren(event.pk) as List<ReflexionItem>
                    }

                    else -> {
                        Toast.makeText(context, "No matching items", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.message}  with cause: ${e.cause}")
            }
        }
    }

//    fun selectItem(itemPk: Long?) {
//        if (itemPk != null) {
//            viewModelScope.launch {
//                _selectedItem.value = localServiceImpl.selectItem(itemPk) ?: ReflexionItem()
//            }
//        }
//    }
fun selectItem(itemPk: String?) {
    if (itemPk != null) {
        viewModelScope.launch {
           // _selectedItem.value = localServiceImpl.selectItem(itemPk) ?: ReflexionItem()
        }
    }
}
}
package com.livingtechusa.reflexion.ui.viewModels

import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
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

    private val _topicsList = MutableStateFlow(emptyList<ReflexionItem>())
    val topicsList: StateFlow<List<ReflexionItem>> get() = _topicsList

    init {
        viewModelScope.launch {
            _topicsList.value = (localServiceImpl.getAllTopics()
                ?: emptyList<ReflexionItem>()) as List<ReflexionItem>
        }
    }
}
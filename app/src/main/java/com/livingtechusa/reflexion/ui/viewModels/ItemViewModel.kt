package com.livingtechusa.reflexion.ui.viewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.entities.LinkedList
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.util.BaseApplication
import com.livingtechusa.reflexion.util.Constants
import com.livingtechusa.reflexion.util.MediaStoreUtils
//import com.livingtechusa.reflexion.ui.components.VideoView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val STATE_KEY_URL = "com.livingtechusa.reflexion.ui.build.BuildItemScreen.url"

enum class ApiStatus { PRE_INIT, LOADING, ERROR, DONE }


@HiltViewModel
class ItemViewModel @Inject constructor(
    private val localServiceImpl: LocalServiceImpl
) : ViewModel() {

    private val TAG = "ItemViewModel"

    private val _reflexionItem = MutableStateFlow(ReflexionItem())
    val reflexionItem: StateFlow<ReflexionItem> get() = _reflexionItem

    private val _isParent = MutableStateFlow(false)
    val isParent: StateFlow<Boolean> get() = _isParent

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

    private val context: Context
        get() = BaseApplication.getInstance()

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow: SharedFlow<String> = _errorFlow

    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.i(TAG, "ERROR: $throwable")
        throwable.printStackTrace()
    }

    init {
        _reflexionItem.value.name = Constants.EMPTY_STRING
    }

    fun onTriggerEvent(event: BuildEvent) {
        viewModelScope.launch {
            try {
                when (event) {

                    is BuildEvent.UpdateReflexionItem -> {
                        withContext(Dispatchers.IO) {
                            _reflexionItem.value = event.reflexionItem
                            localServiceImpl.updateReflexionItem(event.reflexionItem)
                        }
                    }

                    is BuildEvent.SaveNew -> {
                        withContext(Dispatchers.IO) {
                            localServiceImpl.setItem(event.reflexionItem)
                            _reflexionItem.value =
                                localServiceImpl.selectReflexionItemByName(event.reflexionItem.name)
                        }
                    }

                    is BuildEvent.Delete -> {
                        withContext(Dispatchers.IO) {
                            localServiceImpl.deleteReflexionItem(
                                _reflexionItem.value.autogenPK,
                                _reflexionItem.value.name
                            )
                            _reflexionItem.value = ReflexionItem()
                        }
                    }

                    is BuildEvent.ShowVideo -> {
                        if (event.uri.isNullOrEmpty().not()) {
                            event.uri?.let {

                            }
                        }
                    }

                    is BuildEvent.ShowChildren -> {
                        //ItemRecyclerView()
                    }

                    else -> {

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

    suspend fun createVideoUri(): Uri? {
        val filename = context.getString(R.string.app_name) + "${System.currentTimeMillis()}.mp4"
        val uri = MediaStoreUtils.createVideoUri(context, filename)
        return if (uri != null) {
            uri
        } else {
            _errorFlow.emit("Could not create a video Uri\n$filename")
            null
        }
    }
}
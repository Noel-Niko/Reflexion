package com.livingtechusa.reflexion.ui.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.data.entities.ListNode
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import com.livingtechusa.reflexion.di.DefaultDispatcher
import com.livingtechusa.reflexion.ui.bookmarks.BookmarksEvent
import com.livingtechusa.reflexion.util.BaseApplication
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val localServiceImpl: LocalServiceImpl,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val TAG = "BookmarksViewModel"
    private val context: Context
        get() = BaseApplication.getInstance()

    private val _listBookmark = MutableStateFlow(emptyList<ListNode>())
    val listBookmark get() = _listBookmark

    private val _itemBookmarks = MutableStateFlow(emptyList<ReflexionItem>())
    val itemBookmarks get() = _itemBookmarks

    private val _search = MutableStateFlow(EMPTY_STRING)
    val search get() = _search


    fun onTriggerEvent(event: BookmarksEvent) {

        try {
            when (event) {

                is BookmarksEvent.Search -> {
                    viewModelScope.launch {
                        // Bookmark keyword search
                        if (event.search.isNullOrEmpty()) {
                            _itemBookmarks.value = emptyList()
                            _listBookmark.value = emptyList()
                        } else {
                            val items = mutableListOf<ReflexionItem>()
                            val lists = mutableListOf<ListNode>()
                            localServiceImpl.searchBookmarksByTitle(event.search)
                                .forEach { bookMarks ->

                                    if (bookMarks != null) {
                                        if (bookMarks.ITEM_PK != null) {
                                            localServiceImpl.selectItem(bookMarks.ITEM_PK)
                                                ?.let { reflexionItem -> items.add(reflexionItem) }
                                        } else if (bookMarks.LIST_PK != null) {
                                            localServiceImpl.selectListNode(bookMarks.LIST_PK)
                                                ?.let { listNode -> lists.add(listNode) }
                                        }
                                    }
                                }
                            _itemBookmarks.value = items
                            _listBookmark.value = lists
                        }
                    }
                }

                is BookmarksEvent.DeleteBookmark -> {
                    viewModelScope.launch {
                        localServiceImpl.deleteBookmark(event.bookmarkPk)
                    }
                }


                else -> {}

            }
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Exception: ${e.message}  with cause: ${e.cause}"
            )
        }
    }

    fun searchEvent(term: String?) {
        _search.value = term ?: EMPTY_STRING
        onTriggerEvent(BookmarksEvent.Search(term))
    }

}
package com.livingtechusa.reflexion.ui.viewModels

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.ListNode
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import com.livingtechusa.reflexion.di.DefaultDispatcher
import com.livingtechusa.reflexion.ui.bookmarks.BookmarksEvent
import com.livingtechusa.reflexion.util.BaseApplication
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _search = MutableStateFlow(null as String?)
    val search get() = _search

    private val _listImages = MutableStateFlow<List<Bitmap>>(emptyList())
    val listImages: StateFlow<List<Bitmap?>> get() = _listImages

    fun getListImages() {
        viewModelScope.launch {
            val bitmaps: MutableList<Bitmap> = mutableListOf()
            val job = async {
                listBookmark.value.forEach { node ->
                    localServiceImpl.selectImage(node.childPk ?: node.topic)
                        ?.let { bitmaps.add(it) }
                }
            }
            job.join()
            _listImages.value = bitmaps
        }
    }
    fun onTriggerEvent(event: BookmarksEvent) {

        try {
            when (event) {

                is BookmarksEvent.Search -> {
                    viewModelScope.launch {
                        // Bookmark keyword search
                        if (event.search.isNullOrEmpty()) {
                            populateContent()
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

                is BookmarksEvent.DeleteItemBookmark -> {
                    viewModelScope.launch {
                        localServiceImpl.selectItemBookMark(event.ITEM_PK)?.autoGenPk?.let { bookmark_PK ->
                            localServiceImpl.deleteBookmark(
                                bookmark_PK
                            )
                        }
                        viewModelScope.launch(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.removed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        populateContent()
                    }
                }

                is BookmarksEvent.DeleteListBookmark -> {
                    viewModelScope.launch {
                        localServiceImpl.selectListBookMarks(event.NODE_PK)?.autoGenPk?.let { bookmark_PK ->
                            localServiceImpl.deleteBookmark(
                                bookmark_PK
                            )
                        }
                        viewModelScope.launch(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.removed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        populateContent()
                    }
                }

                is BookmarksEvent.GetAllBookmarks -> {
                    populateContent()
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

    private fun populateContent() {
        viewModelScope.launch {
            val items = mutableListOf<ReflexionItem>()
            val lists = mutableListOf<ListNode>()
            localServiceImpl.getBookMarks().forEach { bookmarks ->
                if (bookmarks != null) {
                    if (bookmarks.ITEM_PK != null) {
                        localServiceImpl.selectItem(bookmarks.ITEM_PK)
                            ?.let { reflexionItem -> items.add(reflexionItem) }
                    } else if (bookmarks.LIST_PK != null) {
                        localServiceImpl.selectListNode(bookmarks.LIST_PK)
                            ?.let { listNode -> lists.add(listNode) }
                    }
                }
            }
            _itemBookmarks.value = items
            _listBookmark.value = lists
            getListImages()
        }
    }
}
package com.livingtechusa.reflexion.ui.viewModels

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.Bookmarks
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import com.livingtechusa.reflexion.di.DefaultDispatcher
import com.livingtechusa.reflexion.ui.topics.TopicItemEvent
import com.livingtechusa.reflexion.util.BaseApplication
import com.livingtechusa.reflexion.util.Constants.EMPTY_PK
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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

    private val TAG = "TopicsViewModel"

    private val context: Context
        get() = BaseApplication.getInstance()

    // Currently displayed list of items in the list
    private val _list = MutableStateFlow(emptyList<ReflexionItem>())
    val list: StateFlow<List<ReflexionItem>> get() = _list

    private val _bookmarks = MutableStateFlow(emptyList<Bookmarks?>())
    val bookmarks: StateFlow<List<Bookmarks?>> get() = _bookmarks

    private val _bookmarkImages = MutableStateFlow(emptyList<Bitmap?>())
    val bookmarkImages: StateFlow<List<Bitmap?>> get() = _bookmarkImages

    // Term for query
    private val _search = MutableStateFlow(null as String?)
    val search: StateFlow<String?> = _search

    suspend fun hasNoChildren(pk: Long): Boolean {
        return localServiceImpl.selectChildren(pk).isEmpty()
    }

    fun onTriggerEvent(event: TopicItemEvent) {

        try {
            when (event) {
                is TopicItemEvent.GetTopicItem -> {
                    viewModelScope.launch {
                        withContext(defaultDispatcher) {
                            if (event.pk == null || event.pk == EMPTY_PK) {
                                _list.value =
                                    (localServiceImpl.getAllTopics() as? List<ReflexionItem>)
                                        ?: emptyList()
                            } else {
                                val newList = async {
                                    (localServiceImpl.selectChildren(event.pk) as? List<ReflexionItem>)
                                        ?: emptyList()
                                }
                                _list.value = newList.await()
                            }
                            setBookmarks()
                        }
                    }
                }

                is TopicItemEvent.Search -> {
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

                is TopicItemEvent.ClearTopicItem -> {
                    _list.value = emptyList()
                }

                is TopicItemEvent.UpOneLevel -> {
                    viewModelScope.launch {
                        val parent: Long = list.value[0].parent ?: EMPTY_PK
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

    private fun setBookmarks() {
        viewModelScope.launch(Dispatchers.IO) {
            val _bookmarkedLevels: Deferred<List<Bookmarks?>> =
                async { localServiceImpl.selectLevelBookMarks() }
            val bookmarkedLevels = _bookmarkedLevels.await()
            _bookmarks.value = bookmarkedLevels
            val _images: Deferred<MutableList<Bitmap>> = async {
                val bitmaps = mutableListOf<Bitmap>()
                _bookmarks.value?.forEach { bookmark ->
                    bookmark?.LEVEL_PK?.let { levelPk ->
                        localServiceImpl.selectReflexionItemByPk(levelPk)?.imagePk?.let { imagePk ->
                            localServiceImpl.selectImage(imagePk)?.let { bitmaps.add(it) }
                        }
                    }
                }
                return@async bitmaps
            }
            val images = _images.await()
            _bookmarkImages.value = images
        }
    }

    fun searchEvent(term: String?) {
        _search.value = term
        onTriggerEvent(TopicItemEvent.Search(term))
    }

    fun onUp() {
        if (list.value.isEmpty() || list.value[0].parent == null || list.value[0].parent == EMPTY_PK) {
            Toast.makeText(context, R.string.no_parent_found, Toast.LENGTH_SHORT).show()
        } else {
            onTriggerEvent(TopicItemEvent.UpOneLevel)
        }
    }

    fun bookmark() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // if no items exist in the DB yet, disable and provide toast
                if (list.value.isEmpty()) {
                    viewModelScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.no_parent_item_available_to_bookmark_this_level),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val parent = list.value[0].autogenPk.let { localServiceImpl.selectParent(it) }
                    if (parent != null) {
                        val item = parent.let { localServiceImpl.selectReflexionItemByPk(it) }
                        val selectBkMkByLevelPK =
                            localServiceImpl.selectBookmarkByLevelPK(item?.autogenPk)
                        if (selectBkMkByLevelPK == null) {
                            localServiceImpl.setBookMarks(
                                Bookmarks(
                                    autoGenPk = 0L,
                                    ITEM_PK = null,
                                    LIST_PK = null,
                                    LEVEL_PK = item?.autogenPk,
                                    title = item?.name ?: EMPTY_STRING
                                )
                            )
                            viewModelScope.launch(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.bookmarked),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            setBookmarks()
                        } else {
                            viewModelScope.launch(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.already_bookmarked),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        viewModelScope.launch(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.no_parent_item_available_to_bookmark_this_level),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

        fun selectLevel(parentPk: Long) {
            this@TopicsViewModel.onTriggerEvent(TopicItemEvent.GetTopicItem(parentPk))
        }

        fun deleteBookmark(autogenPk: Long) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    localServiceImpl.deleteBookmark(autogenPk)
                    viewModelScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.removed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    setBookmarks()
                }
            }
        }
    }
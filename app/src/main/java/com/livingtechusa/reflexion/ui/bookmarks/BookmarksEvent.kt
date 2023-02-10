package com.livingtechusa.reflexion.ui.bookmarks

sealed class BookmarksEvent {
    data class Search(
        val search: String?,
    ): BookmarksEvent() {}

    data class DeleteItemBookmark(
       val ITEM_PK: Long,
    ): BookmarksEvent() {}

    data class DeleteListBookmark(
        val NODE_PK: Long,
    ) : BookmarksEvent()

    object  ClearList: BookmarksEvent()
    object GetAllBookmarks: BookmarksEvent()
}
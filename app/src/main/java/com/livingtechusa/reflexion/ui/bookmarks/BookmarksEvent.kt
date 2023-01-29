package com.livingtechusa.reflexion.ui.bookmarks

sealed class BookmarksEvent {
    data class Search(
        val search: String?,
    ): BookmarksEvent() {}

    data class DeleteBookmark(
       val bookmarkPk: Long,
    ): BookmarksEvent() {}


    object  ClearList: BookmarksEvent()

}
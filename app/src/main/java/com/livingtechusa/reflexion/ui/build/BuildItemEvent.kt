package com.livingtechusa.reflexion.ui.build

import android.net.Uri
import com.livingtechusa.reflexion.data.entities.ReflexionItem

sealed class BuildEvent {

    object SaveNew : BuildEvent()

    object UpdateReflexionItem : BuildEvent()

    data class DeleteReflexionItemSubItemByName(
        val subItem: String
    ) : BuildEvent()

    data class UpdateDisplayedReflexionItem(
        val subItem: String,
        val newVal: Any
    ) : BuildEvent()

    object Delete : BuildEvent()
    object ClearReflexionItem : BuildEvent()
    data class GetSelectedReflexionItem(
        val pk: Long?
    ): BuildEvent()
    data class SetParent(val parent: Long): BuildEvent()
    data class SetSelectedParent(val parent: ReflexionItem): BuildEvent()
    data class CreateThumbnailImage(val uri: Uri?) : BuildEvent()
    data class Bookmark(val itemPk: Long): BuildEvent()
    object RotateImage: BuildEvent()
    object SendText: BuildEvent()
    object Save: BuildEvent()
    data class SearchUri(val uri: String): BuildEvent ()
    object SendFile: BuildEvent()
    object SaveAndDisplayZipFile: BuildEvent()
    object RemoveListNodesForDeletedItems: BuildEvent()
}
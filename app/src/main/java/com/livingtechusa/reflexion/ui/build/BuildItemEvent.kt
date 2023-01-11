package com.livingtechusa.reflexion.ui.build

import android.net.Uri
import com.livingtechusa.reflexion.data.entities.ReflexionItem

sealed class BuildEvent {

    data class SaveNew(
        val reflexionItem: ReflexionItem
    ) : BuildEvent()

    data class UpdateReflexionItem(
        val reflexionItem: ReflexionItem
    ) : BuildEvent()

    data class DeleteReflexionItemSubItemByName(
        val subItem: String
    ) : BuildEvent()

    data class UpdateDisplayedReflexionItem(
        val reflexionItem: ReflexionItem
    ) : BuildEvent()

    data class ShowVideo(
        val uri: String?,
        val Url: String?
    ) : BuildEvent()

    object Delete : BuildEvent()

    object ClearReflexionItem : BuildEvent()

    data class UpdateVideoURL(
        val videoUrl: String
    ) : BuildEvent()

    object ShowSiblings : BuildEvent()

    data class GetSelectedReflexionItem(
        val pk: Long?
    ): BuildEvent()

    data class SetParent(val parent: Long): BuildEvent()
    data class CreateThumbnailImage(val uri: Uri?) : BuildEvent()
    object RotateImage : BuildEvent()

    object BluetoothSend : BuildEvent()
    object SendText : BuildEvent()

    object SaveFromTopBar : BuildEvent()


}
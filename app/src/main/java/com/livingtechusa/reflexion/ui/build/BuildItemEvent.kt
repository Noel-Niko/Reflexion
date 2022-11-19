package com.livingtechusa.reflexion.ui.build

import android.net.Uri
import com.livingtechusa.reflexion.data.entities.ReflexionItem

sealed class BuildEvent {

    data class UpdateDisplayedItem(
        val reflexionItem: ReflexionItem
    ) : BuildEvent()

    data class SaveNew(
        val reflexionItem: ReflexionItem
    ) : BuildEvent()

    data class UpdateReflexionItem(
        val reflexionItem: ReflexionItem
    ) : BuildEvent()

    data class ShowVideo(
        val uri: String?,
        val Url: String?
    ) : BuildEvent()

    object Delete : BuildEvent()

    object UpdateSavedVideo : BuildEvent() {
    }

    object UpdateVideoURL : BuildEvent() {
    }

    object ShowSiblings : BuildEvent() {
    }

    object ShowChildren : BuildEvent()
}
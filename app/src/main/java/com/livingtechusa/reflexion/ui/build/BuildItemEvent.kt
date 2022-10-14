package com.livingtechusa.reflexion.ui.build

import android.net.Uri
import com.livingtechusa.reflexion.data.entities.ReflexionItem

sealed class BuildEvent {

    object GetImages : BuildEvent()
    object GetNewImage : BuildEvent()
    data class ConvertToYodaSpeak(
        val text: String
    ) : BuildEvent()

    data class UpdateALLFieldText(
        val reflexionItem: ReflexionItem
    ) : BuildEvent()

    object Delete : BuildEvent()

    object UpdateColor : BuildEvent()
    object UpdateSavedVideo : BuildEvent() {

    }

    object UpdateVideoURL : BuildEvent() {

    }

    object ShowSiblings : BuildEvent() {

    }

    object ShowChildren : BuildEvent() {

    }
}
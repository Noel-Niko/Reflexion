package com.livingtechusa.gotjokes.ui.display

sealed class DisplayEvent {
    object GoToBuildScreen : DisplayEvent()
    object GoToSavedScreen : DisplayEvent()
}
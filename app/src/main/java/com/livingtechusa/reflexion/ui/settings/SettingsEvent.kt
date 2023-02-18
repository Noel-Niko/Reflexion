package com.livingtechusa.reflexion.ui.settings

sealed class SettingsEvent {
    object GetIconImages : SettingsEvent()
    data class SetIconSelected (
        val iconSelected: Int
    ) : SettingsEvent()
}
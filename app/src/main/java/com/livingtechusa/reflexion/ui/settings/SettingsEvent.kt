package com.livingtechusa.reflexion.ui.settings

sealed class SettingsEvent {
    object getIconImages : SettingsEvent()
    data class setIconSelected (
        val iconSelected: Int
    ) : SettingsEvent()
}
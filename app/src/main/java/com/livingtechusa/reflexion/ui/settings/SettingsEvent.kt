package com.livingtechusa.reflexion.ui.settings

sealed class SettingsEvent {
    object getIconImages : SettingsEvent()
    data class setMode(
        val isDark: Boolean
    ) : SettingsEvent()
}
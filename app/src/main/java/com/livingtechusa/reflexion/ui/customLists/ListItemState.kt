package com.livingtechusa.reflexion.ui.customLists

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.livingtechusa.reflexion.data.models.ReflexionArrayItem


/*[ListItemState]  represents currentListItem state. */
 class ListItemState(currentItem: ReflexionArrayItem) {
    private var _currentMenu by mutableStateOf(currentItem)

    var currentMenuItem: ReflexionArrayItem
        get() = _currentMenu
        set(value) {
            _currentMenu = value
        }
}
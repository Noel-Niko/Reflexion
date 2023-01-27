package com.livingtechusa.reflexion.ui.components.bars

import androidx.activity.compose.BackHandler
import androidx.compose.material.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.livingtechusa.reflexion.ui.components.icons.BackIcon
import com.livingtechusa.reflexion.ui.components.text.SearchTextField
import com.livingtechusa.reflexion.ui.components.icons.SearchIcon
import com.livingtechusa.reflexion.ui.components.icons.UpIcon

@Composable
fun SearchBar(
    search: String?,
    onSearch: (searchText: String?) -> Unit,
    onUp: () -> Unit
) {
    when (search) {
        null -> MainTopBar {
            SearchIcon { onSearch("") }
            UpIcon {
                onUp()
            }
        }

        else -> TopAppBar(
            title = {
                SearchTextField(search = search, onSearch = onSearch)
            },
            navigationIcon = {
                BackHandler { onSearch(null) }
                BackIcon { onSearch(null) }
            },
            backgroundColor = MaterialTheme.colorScheme.surface
        )
    }
}

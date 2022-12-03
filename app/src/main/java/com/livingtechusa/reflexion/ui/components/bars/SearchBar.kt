package com.livingtechusa.reflexion.ui.components.bars

import androidx.activity.compose.BackHandler
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import com.livingtechusa.reflexion.ui.components.BackIcon
import com.livingtechusa.reflexion.ui.components.text.SearchTextField
import com.raywenderlich.android.words.ui.icons.SearchIcon

@Composable
fun SearchBar(
    search: String?,
    onSearch: (String?) -> Unit
) {
  when (search) {
    null -> MainTopBar(
        actions = { SearchIcon { onSearch("")

        } }
    )
    else -> TopAppBar(
        title = {
          SearchTextField(search, onSearch)
        },
        navigationIcon = {
          BackHandler { onSearch(null) }
          BackIcon { onSearch(null) }
        },
        backgroundColor = MaterialTheme.colors.background
    )
  }
}

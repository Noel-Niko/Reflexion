package com.livingtechusa.reflexion.ui.components.bars

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.ui.components.icons.BackIcon
import com.livingtechusa.reflexion.ui.components.text.SearchTextField
import com.livingtechusa.reflexion.ui.components.icons.SearchIcon
import com.livingtechusa.reflexion.ui.components.icons.UpIcon
import kotlinx.coroutines.launch

@Composable
fun SearchBar(
    search: String?,
    onSearch: (searchText: String?) -> Unit,
    onUp: (() -> Unit)?
) {
    when (search) {
        null -> MainTopBar() {
            SearchIcon { onSearch("") }
            if (onUp != null) {
                UpIcon {
                        onUp()
                    }
            }
        }

        else -> TopAppBar(
            title = {
                SearchTextField(search = search, onSearch = onSearch)
            },
            navigationIcon = {
                if (search.isNotEmpty()) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_clear_24),
                        contentDescription = "Clear search",
                        modifier = Modifier.clickable(onClick = {
                            onSearch(null)
                        }),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    }
}

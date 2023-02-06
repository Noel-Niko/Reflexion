package com.livingtechusa.reflexion.ui.components.bars

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.ui.components.icons.BookmarkIcon
import com.livingtechusa.reflexion.ui.components.text.SearchTextField
import com.livingtechusa.reflexion.ui.components.icons.SearchIcon
import com.livingtechusa.reflexion.ui.components.icons.UpIcon
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
import kotlin.reflect.KFunction2

@Composable
fun SearchBar(
    search: String?,
    onSearch: (searchText: String?) -> Unit,
    onUp: (() -> Unit)?,
    bookmark: (() -> Unit)?,
) {
    val resource = ResourceProviderSingleton
    val context = LocalContext.current
    when (search) {
        null -> TopicTopBar() {
            SearchIcon { onSearch("") }
            if (onUp != null) {
                UpIcon {
                        onUp()
                    }
            }
            if (bookmark != null) {
                BookmarkIcon {
                    bookmark()
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
                        contentDescription = stringResource(R.string.clear_search),
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

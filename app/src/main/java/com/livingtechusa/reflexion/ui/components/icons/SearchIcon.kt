
package com.livingtechusa.reflexion.ui.components.icons

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import com.livingtechusa.reflexion.ui.theme.ReflexionItemsColors

@Composable
fun SearchIcon(onClick: () -> Unit) {
  IconButton(
      onClick = onClick,
      content = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "search",
            tint = MaterialTheme.colors.onBackground,
        )
      },
  )
}
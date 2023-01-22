
package com.livingtechusa.reflexion.ui.components.icons

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun SearchIcon(onClick: () -> Unit) {
  IconButton(
      onClick = onClick,
      content = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "search",
        )
      },
  )
}
package com.livingtechusa.reflexion.ui.components


import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import com.livingtechusa.reflexion.ui.theme.ReflexionItemsColors

@Composable
fun BackIcon(onClick: () -> Unit) {
  IconButton(
      onClick = onClick,
      content = {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "back",
            tint = ReflexionItemsColors.salem,
        )
      },
  )
}


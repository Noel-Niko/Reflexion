package com.livingtechusa.reflexion.ui.components.icons


import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable

@Composable
fun BackIcon(onClick: () -> Unit) {
  IconButton(
      onClick = onClick,
      content = {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "back",
        )
      },
  )
}


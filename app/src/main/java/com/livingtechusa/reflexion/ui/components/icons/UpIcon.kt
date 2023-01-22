package com.livingtechusa.reflexion.ui.components.icons

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.Composable

@Composable
fun UpIcon(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        content = {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = "up",
            )
        },
    )
}
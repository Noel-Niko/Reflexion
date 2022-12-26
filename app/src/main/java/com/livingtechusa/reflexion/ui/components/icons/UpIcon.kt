package com.livingtechusa.reflexion.ui.components.icons

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.Composable
import com.livingtechusa.reflexion.ui.theme.ReflexionItemsColors

@Composable
fun UpIcon(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        content = {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = "up",
                tint = ReflexionItemsColors.salem,
            )
        },
    )
}
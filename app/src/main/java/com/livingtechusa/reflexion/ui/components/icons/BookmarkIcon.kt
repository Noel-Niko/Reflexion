package com.livingtechusa.reflexion.ui.components.icons

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.livingtechusa.reflexion.R

@Composable
fun BookmarkIcon(onClick: () -> Unit) {
    IconButton(
    onClick = onClick,
    content = {
        Icon(
            imageVector = Icons.Default.Bookmark,
            contentDescription = stringResource(R.string.bookmark),
            tint = MaterialTheme.colorScheme.onSurface
        )
    },
    )
}
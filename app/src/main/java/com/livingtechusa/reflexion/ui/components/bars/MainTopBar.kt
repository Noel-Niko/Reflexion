package com.livingtechusa.reflexion.ui.components.bars

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.livingtechusa.reflexion.R


@Composable
fun MainTopBar(
    actions: @Composable RowScope.() -> Unit = {}
) {
  TopAppBar(
      title = {
        Text(text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
      },
      contentColor = MaterialTheme.colorScheme.onSurface,
      backgroundColor = MaterialTheme.colorScheme.surface,
      actions = actions
  )
}
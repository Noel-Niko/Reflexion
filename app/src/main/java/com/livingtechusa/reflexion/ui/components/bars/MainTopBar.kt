package com.livingtechusa.reflexion.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.livingtechusa.reflexion.R


@Composable
fun MainTopBar(
    actions: @Composable RowScope.() -> Unit = {}
) {
  TopAppBar(
      backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
      elevation = 4.dp,
      title = {
        Text(text = stringResource(id = R.string.app_name))
      },
      actions = actions
  )
}
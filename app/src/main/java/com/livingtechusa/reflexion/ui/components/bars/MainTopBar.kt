package com.livingtechusa.reflexion.ui.components.bars

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.livingtechusa.reflexion.R


@Composable
fun MainTopBar(
    actions: @Composable RowScope.() -> Unit = {}
) {
  TopAppBar(
      backgroundColor = MaterialTheme.colors.background,
      elevation = 6.dp,
      title = {
        Text(text = stringResource(id = R.string.app_name), color = androidx.compose.material.MaterialTheme.colors.onBackground)
      },
      actions = actions
  )
}
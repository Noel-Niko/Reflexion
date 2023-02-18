package com.livingtechusa.reflexion.ui.components.icons

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.ui.viewModels.BuildItemViewModel

@Composable
fun BuildIcons(viewModel: BuildItemViewModel){
    Row() {
        IconButton(
            onClick = {},
            content = {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = stringResource(R.string.send),
                )
            },
        )
        IconButton(
            onClick = {},
            content = {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = stringResource(R.string.save),
                )
            },
        )
    }
}
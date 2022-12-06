package com.livingtechusa.reflexion.ui.components.icons

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SendToMobile
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.ui.theme.ReflexionItemsColors
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel

@Composable
fun BuildIcons(viewModel: ItemViewModel){
    Row() {
        IconButton(
            onClick = {},//viewModel.onTriggerEvent(BuildEvent.SendText),
            content = {
                Icon(
                    imageVector = Icons.Default.SendToMobile,
                    contentDescription = "send",
                    tint = ReflexionItemsColors.salem,
                )
            },
        )
        IconButton(
            onClick = {},//viewModel.onTriggerEvent(BuildEvent.SendText),
            content = {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "send",
                    tint = ReflexionItemsColors.salem,
                )
            },
        )
        IconButton(
            onClick = {},//viewModel.onTriggerEvent(BuildEvent.SendText),
            content = {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "send",
                    tint = ReflexionItemsColors.salem,
                )
            },
        )
    }
}
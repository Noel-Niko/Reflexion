package com.livingtechusa.reflexion.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.ui.children.ListEvent
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.Temporary


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReflexionItemListUI(
    reflexionItems: List<ReflexionItem>,
    navController: NavHostController,
    viewModel: ItemViewModel
    ) {
    val context = LocalContext.current
    Scaffold(
        topBar = { MainTopBar() },
        content = {
            ReflexionItemsContent(
                reflexionItems = reflexionItems,
                onSelected = {
                    navController.navigate(route = Screen.BuildItemScreen.route) {// + "/" + it.autogenPK) {
                        launchSingleTop = true
                        restoreState = true
                    }
                    viewModel.onTriggerEvent(BuildEvent.GetSelectedReflexionItem(it.autogenPK))
                }
            )
        }
    )
}

@Composable
private fun ReflexionItemColumnItem(
    reflexionItem: ReflexionItem,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.clickable { onClick() },
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = reflexionItem.name,
        )
    }
}

@Composable
private fun ReflexionItemsContent(
    reflexionItems: List<ReflexionItem>,
    onSelected: (ReflexionItem) -> Unit,
) {
    LazyColumn {
        items(reflexionItems) { reflexionItem ->
            ReflexionItemColumnItem(reflexionItem = reflexionItem) {
                onSelected(reflexionItem)
            }
        }
    }
}


package com.livingtechusa.reflexion.ui.components

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.media3.common.C
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.ui.children.ChildEvent
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.ItemTypeEnum
import com.livingtechusa.reflexion.util.ItemTypeEnum.CHILD
import com.livingtechusa.reflexion.util.Temporary


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReflexionItemListUI(
    reflexionItems: List<ReflexionItem>,
    navController: NavHostController
    ) {
    val context = LocalContext.current
    Scaffold(
        topBar = { MainTopBar() },
        content = {
            ReflexionItemsContent(
                reflexionItems = reflexionItems,
                onSelected = {
                    Temporary.tempReflexionItem = it
                    Temporary.use = true
                    navController.navigate(route = Screen.BuildItemScreen.route)
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


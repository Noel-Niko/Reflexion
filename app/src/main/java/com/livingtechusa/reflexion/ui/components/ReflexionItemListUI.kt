package com.livingtechusa.reflexion.ui.components

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.ui.list.ListEvent
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReflexionItemListUI(
    navController: NavHostController,
    viewModel: ItemViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val list = viewModel.list.collectAsState()
    Scaffold(
        content = {
            ReflexionItemsContent(
                reflexionItems = list.value,
                onDoubleTap = { reflexionItem ->
                    scope.launch {
                        val job = launch {
                            viewModel.onTriggerEvent(
                                BuildEvent.GetSelectedReflexionItem(reflexionItem.autogenPK)
                            )
                        }
                        job.join()
                        navController.navigate(route = Screen.BuildItemScreen.route) {// + "/" + it.autogenPK) {
                            launchSingleTop = true
                        }
                    }
                },
                onLongPress = { reflexionItem ->
                    scope.launch {
                        if (viewModel.hasNoChildren(reflexionItem.autogenPK)) {
                            Toast.makeText(context, "No child items found.", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            viewModel.onTriggerEvent(ListEvent.ClearList)
                            viewModel.onTriggerEvent(ListEvent.GetList(reflexionItem.autogenPK))
                        }
                    }
                }
            )
        }
    )
}

@Composable
private fun ReflexionItemColumnItem(
    reflexionItem: ReflexionItem,
    onDoubleTap: () -> Unit,
    onLongPress: () -> Unit
) {
    Row(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = { onDoubleTap() },
                onLongPress = { onLongPress() }
            )
        }
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
    onDoubleTap: (ReflexionItem) -> Unit,
    onLongPress: (ReflexionItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
//            .verticalScroll(rememberScrollState())
    ) {
        items(reflexionItems) { reflexionItem ->
            ReflexionItemColumnItem(
                reflexionItem = reflexionItem,
                onDoubleTap = { onDoubleTap(reflexionItem) },
                onLongPress = { onLongPress(reflexionItem) }
            )
        }
    }
}


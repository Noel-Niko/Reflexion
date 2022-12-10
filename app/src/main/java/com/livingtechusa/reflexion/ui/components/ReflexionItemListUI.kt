package com.livingtechusa.reflexion.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.livingtechusa.reflexion.ui.viewModels.ListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReflexionItemListUI(
    navController: NavHostController,
    viewModel: ListViewModel
    ) {
    val context = LocalContext.current
    val scope =  rememberCoroutineScope()
    val state = rememberScaffoldState()
    val reflexionItemList by viewModel.list.collectAsState()
    Scaffold(
        scaffoldState = state,
        content = {
            ReflexionItemsContent(
                reflexionItems = reflexionItemList,
                onDoubleTap = { reflexionItem ->
                    //CoroutineScope(Dispatchers.Main).launch {
                       // viewModel.onTriggerEvent(ListEvent.GetList(pk))

                        navController.navigate(route = Screen.BuildItemScreen.route + "/" + reflexionItem.autogenPK) {
                            launchSingleTop = true
                        }
                   // }
                },
                onLongPress = { reflexionItem ->
                    navController.navigate(route = Screen.ListScreen.route +"/" + reflexionItem.autogenPK) {
                        viewModel.onTriggerEvent(ListEvent.GetList(reflexionItem.autogenPK))
                        launchSingleTop = true
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
                onLongPress = { onLongPress ()}
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


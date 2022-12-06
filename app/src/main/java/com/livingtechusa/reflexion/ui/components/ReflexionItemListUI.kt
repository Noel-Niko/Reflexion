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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReflexionItemListUI(
    reflexionItems: List<ReflexionItem>,
    navController: NavHostController,
    viewModel: ItemViewModel
    ) {
    val context = LocalContext.current
    Scaffold(
        content = {
            ReflexionItemsContent(
                reflexionItems = reflexionItems,
                onClick = {
                    navController.navigate(route = Screen.BuildItemScreen.route) {// + "/" + it.autogenPK) {
                        launchSingleTop = true
                        restoreState = true
                    }
                    viewModel.onTriggerEvent(BuildEvent.GetSelectedReflexionItem(it.autogenPK))
                },
                onDoubleTap = {
                    Toast.makeText(context, "LONG PRESSED", Toast.LENGTH_SHORT).show()
                }
            )
        }
    )
}

@Composable
private fun ReflexionItemColumnItem(
    reflexionItem: ReflexionItem,
    onClick: () -> Unit,
    onDoubleTap: () -> Unit
) {
    Row(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onPress = { onClick() },
                onDoubleTap = { onDoubleTap ()}
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
    onClick: (ReflexionItem) -> Unit,
    onDoubleTap: (ReflexionItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
//            .verticalScroll(rememberScrollState())
    ) {
        items(reflexionItems) { reflexionItem ->
            ReflexionItemColumnItem(
                reflexionItem = reflexionItem,
                onClick = { onClick(reflexionItem) },
                onDoubleTap = { onDoubleTap(reflexionItem) }
            )
        }
    }
}


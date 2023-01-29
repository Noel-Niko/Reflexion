package com.livingtechusa.reflexion.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.topics.ListEvent
import com.livingtechusa.reflexion.ui.viewModels.ListViewModel
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReflexionItemListUI(
    navController: NavHostController,
    viewModel: ListViewModel
) {
    val context: Context = LocalContext.current
    val resource = ResourceProviderSingleton
    val reflexionItemList by viewModel.list.collectAsState()
    Scaffold(
        content = {
            ReflexionItemsContent(
                reflexionItems = reflexionItemList,
                onDoubleTap = { reflexionItem ->
                    navController.navigate(route = Screen.BuildItemScreen.route + "/" + reflexionItem.autogenPK) {
                        launchSingleTop = true
                    }
                },
                onLongPress = { reflexionItem ->
                    CoroutineScope(Dispatchers.Main).launch {
                        if (viewModel.hasNoChildren(reflexionItem.autogenPK)) {
                            Toast.makeText(
                                context,
                                resource.getString(R.string.no_child_items_found),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
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
) {
    Row(modifier = Modifier) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = reflexionItem.name,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium
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
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        items(reflexionItems) { reflexionItem ->
            val imagePainter = rememberImagePainter(
                data = reflexionItem.image,
                builder = {
                    allowHardware(false)
                }
            )
            Box {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        //.background(MaterialTheme.colorScheme.surface)
                        .pointerInput(key1 = reflexionItem) {
                            detectTapGestures(
                                onDoubleTap = { onDoubleTap(reflexionItem) },
                                onLongPress = { onLongPress(reflexionItem) }
                            )
                        },
                    elevation = 6.dp,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)) {
                        Image(
                            painter = imagePainter,
                            contentDescription = "Your Image",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .width(60.dp)
                                .height(60.dp)

                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        ReflexionItemColumnItem(
                            reflexionItem = reflexionItem,
                        )
                    }
                }
            }
        }
    }
}


package com.livingtechusa.reflexion.ui.customLists

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.util.Constants.NO_LISTS
import com.livingtechusa.reflexion.util.ReflexionArrayItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

@Composable
fun CustomListContent(
    navController: NavHostController,
    viewModel: CustomListsViewModel = hiltViewModel()
) {
    val customList by viewModel.customList.collectAsState()
    val listOfLists by viewModel.listOfLists.collectAsState()
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        Row() {
            LazyColumn(
                modifier = Modifier
                    .padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(1) { index ->
                    Column(modifier = Modifier.background(Color.Transparent)) {
                        TextField(
                            modifier = Modifier.height(IntrinsicSize.Min),
                            value = customList.itemName.toString(),
                            colors = TextFieldDefaults.textFieldColors(
                                //textColor = MaterialTheme.colors.primary,
                                backgroundColor = Color.Transparent
                            ),
                            onValueChange = {
                                viewModel.onTriggerEvent(
                                    CustomListEvent.UpdateListName(
                                        index = index,
                                        text = it
                                    )
                                )
                            }
                        )
                        EditableHorizontalScrollableRowComponent(
                            viewModel = viewModel,
                            customList = customList
                        )
                    }
                }
            }
        }
        Row(modifier = Modifier.padding(4.dp, 16.dp)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(5F)
                    .padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                items(listOfLists.size) { index ->
//                    val painter = rememberAsyncImagePainter(
//                        CoroutineScope(Dispatchers.IO).launch {
//                            withContext(Dispatchers.IO) {
//                                {
//                                    listOfLists[index]?.itemPK?.let { viewModel.getImage(it) }
//                                }
//                            }
//                                )
//                                val imagePainter = rememberImagePainter(
//                                    data = painter,
//                                    builder = {
//                                        allowHardware(false)
//                                    }
//                                )
                                Box {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(4.dp),
                                        elevation = 10.dp,
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .pointerInput(key1 = index) {
                                                    detectTapGestures(
                                                        onDoubleTap = {
                                                            // Launch dialog
                                                            navController.navigate(Screen.ConfirmDeleteListScreen.route + "/" + index + "/" + listOfLists[index]?.itemName)
                                                        },
                                                        onLongPress = {
                                                            viewModel.onTriggerEvent(
                                                                CustomListEvent.MoveToEdit(
                                                                    index
                                                                )
                                                            )
                                                        },
                                                        onTap = {
                                                            navController.navigate(Screen.CustomListDisplay.route + "/" + listOfLists[index]?.nodePk)
                                                        }
                                                    )
                                                },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
//                                            Image(
//                                                painter = imagePainter,
//                                                contentDescription = "Your Image",
//                                                contentScale = ContentScale.FillBounds,
//                                                modifier = Modifier
//                                                    .width(50.dp)
//                                                    .height(50.dp)
//
//                                            )
//                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = listOfLists[index]?.itemName ?: NO_LISTS,
                                                modifier = Modifier
                                                    .padding(16.dp),
                                                style = MaterialTheme.typography.subtitle2
                                            )
                                            listOfLists[index]?.let {
                                                HorizontalScrollableRowComponent(
                                                    list = it
                                                )
                                            }
                                        }
                                    }
                                }
                            }
//                        }
            }
        }
    }
}


@Composable
fun EditableHorizontalScrollableRowComponent(
    viewModel: CustomListsViewModel,
    customList: ReflexionArrayItem
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .horizontalScroll(state = scrollState),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (customList.children.isNullOrEmpty().not()) {
            customList.children?.forEach { item ->
                var offsetX by remember { mutableStateOf(0f) }
                var offsetY by remember { mutableStateOf(0f) }
                var elevated by remember { mutableStateOf(0) }
                val h2 = MaterialTheme.typography.h2
                val b2 = MaterialTheme.typography.body2
                var textStyle = b2
                Text(
                    text = item.itemName.toString() + ", ",
                    style = textStyle,
                    //color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .pointerInput(Unit) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    offsetY = -50f
                                    elevated = 10
                                    textStyle = h2
                                },
                                onDrag = { change, offset ->
                                    change.consume()
                                    offsetX += offset.x
                                },
                                onDragEnd = {
                                    if (offsetX <= 0) {
                                        customList.children
                                            ?.indexOf(item)
                                            ?.let { index ->
                                                viewModel.onTriggerEvent(
                                                    CustomListEvent.MoveItemUp(
                                                        index
                                                    )
                                                )
                                            }
                                        offsetX = 0F
                                        offsetY = 0f
                                        elevated = 0
                                        textStyle = b2
                                    } else if (offsetX > 0) {
                                        customList.children
                                            ?.indexOf(item)
                                            ?.let { index ->
                                                viewModel.onTriggerEvent(
                                                    CustomListEvent.MoveItemDown(
                                                        index
                                                    )
                                                )
                                            }

                                        offsetX = 0F
                                        offsetY = 0f
                                        elevated = 0
                                        textStyle = b2
                                    }
                                }
                            )
                        }
                        .fillMaxSize()
                        .clickable(
                            enabled = true,
                            onClick = {
                                customList.children
                                    .indexOf(item)
                                    .let { index ->
                                        viewModel.onTriggerEvent(
                                            CustomListEvent.DeleteItemInList(
                                                index
                                            )
                                        )
                                    }
                            }),
                )
            }
        }
    }
}


@Composable
fun HorizontalScrollableRowComponent(
    list: ReflexionArrayItem
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .horizontalScroll(state = scrollState),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (list.children.isEmpty().not()) {
            list.children.forEach { item ->
                Text(
                    text = item.itemName.toString() + ", ",
                    style = MaterialTheme.typography.body1,
                   // color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}

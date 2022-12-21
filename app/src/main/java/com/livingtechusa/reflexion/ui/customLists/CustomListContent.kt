package com.livingtechusa.reflexion.ui.customLists

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.util.Constants.EMPTY_PK
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun CustomListContent(
    navController: NavHostController,
    viewModel: CustomListsViewModel = hiltViewModel()
) {
    val customList by viewModel.customList.collectAsState()
    val listOfLists by viewModel.listOfLists.collectAsState()
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(1) { index -> //customList.items?.size ?:
            Column(modifier = Modifier.background(Color.LightGray)) {
                TextField(
                    value = customList.reflexionItemName.toString(),

                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.Black,
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
                Spacer(
                    modifier = Modifier
                        .height(4.dp)
                        .fillMaxWidth()
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (customList.items.isNullOrEmpty().not()) {
                        val count: Int = customList.items?.size ?: 0
                        items(count) { item ->
                            var offsetX = 0F
                            var fontStyleStandard = MaterialTheme.typography.body2
                            var fontStyleSelected = MaterialTheme.typography.h1
                            var fontStyleImplemented = fontStyleStandard
                            Text(
                                modifier = Modifier
                                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                                    .draggable(
                                        orientation = Orientation.Horizontal,
                                        state = rememberDraggableState { delta ->
                                            offsetX += delta
                                        },
                                        //onDragStarted = { fontStyleImplemented = fontStyleSelected },
                                        onDragStopped = {
                                            if(offsetX.absoluteValue > 10){
                                                viewModel.onTriggerEvent(CustomListEvent.MoveItemUp(customList.items?.get(item)?.reflexionItemPk ?: EMPTY_PK))
                                            } else  if(offsetX.absoluteValue < 10){
                                                viewModel.onTriggerEvent(CustomListEvent.MoveItemDown(customList.items?.get(item)?.reflexionItemPk ?: EMPTY_PK))
                                            }
                                        }
                                    )
                                    .fillMaxSize(),
                                text = customList.items?.get(item)?.reflexionItemName.toString() + ", ",
                               // fontStyle = fontStyleImplemented
                            )
                        }
                    }
                }
            }
        }
    }
}

//
//var offsetX by remember { mutableStateOf(0f) }
//var fontStyleStandard = MaterialTheme.typography.body2
//var fontStyleSelected = MaterialTheme.typography.h1
//var fontStyleImplemented = fontStyleStandard
//Text(
//modifier = Modifier
//.offset { IntOffset(offsetX.roundToInt(), 0) }
//.draggable(
//orientation = Orientation.Horizontal,
//state = rememberDraggableState { delta ->
//    offsetX += delta
//},
//onDragStarted = { fontStyleImplemented = fontStyleSelected },
//onDragStopped = {
//    fontStyleImplemented = fontStyleSelected
//}
//),
//text = customList.items?.get(item)?.reflexionItemName.toString() + ", ",
//// fontStyle = fontStyleImplemented
//)
//}


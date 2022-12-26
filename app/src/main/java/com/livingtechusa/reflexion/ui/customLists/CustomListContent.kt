package com.livingtechusa.reflexion.ui.customLists

import android.graphics.fonts.FontStyle
import android.graphics.fonts.FontStyle.FONT_SLANT_ITALIC
import android.graphics.fonts.FontStyle.FONT_WEIGHT_EXTRA_BOLD
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.util.ReflexionArrayItem
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
                HorizontalScrollableRowComponent(viewModel = viewModel, customList = customList)
            }
        }
    }
}

@Composable
fun HorizontalScrollableRowComponent(
    viewModel: CustomListsViewModel,
    customList: ReflexionArrayItem
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var dragEnabled by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .horizontalScroll(state = scrollState)
            .background(Color.Magenta),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (customList.items.isNullOrEmpty().not()) {
            val count: Int = customList.items?.size ?: 0
            customList.items?.forEach { item ->
                var offsetX by remember { mutableStateOf(0f) }
                var offsetY by remember { mutableStateOf(0f) }
                var elevated by remember { mutableStateOf(0) }
                val h2 = MaterialTheme.typography.h2
                val b2 = MaterialTheme.typography.body2
                var textStyle =  b2
                Text(
                    text = item.reflexionItemName.toString() + ", ",
                    style = textStyle,
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .pointerInput(Unit) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    offsetY = -25f
                                    elevated = 4
                                    textStyle = h2
                                },
                                onDrag = { change, offset ->
                                    change.consume()
                                    offsetX += offset.x
                                },
                                onDragEnd = {
                                    if (offsetX <= 0) {
                                        customList.items
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
                                        customList.items
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
                        .fillMaxSize(),
                )
            }
        }
    }
}
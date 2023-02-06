package com.livingtechusa.reflexion.ui.bookmarks

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.viewModels.BookmarksViewModel
import com.livingtechusa.reflexion.util.Constants
import com.livingtechusa.reflexion.util.ResourceProviderSingleton


@Composable
fun ReflexionItemListUIForBookmarks(
    navController: NavHostController,
    viewModel: BookmarksViewModel
) {
    val context: Context = LocalContext.current
    val resource = ResourceProviderSingleton
    val reflexionItemList by viewModel.itemBookmarks.collectAsState()
    val listOfLists by viewModel.listBookmark.collectAsState()
    val listimages by viewModel.listImages.collectAsState()

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.5f)
            ) {
                Column(

                ) {
                    Text(
                        text = stringResource(R.string.items),
                        modifier = Modifier.padding(start = 16.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface

                    )
                    ReflexionItemsContent(
                        reflexionItems = reflexionItemList,
                        onTap = { reflexionItem ->
                            navController.navigate(route = Screen.BuildItemScreen.route + "/" + reflexionItem.autogenPK) {
                                launchSingleTop = true
                            }
                        },
                        onDoubleTap = { ITEM_PK ->
                            viewModel.onTriggerEvent(BookmarksEvent.DeleteItemBookmark(ITEM_PK))
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1f)
            ) {
                Column(
                    // modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = stringResource(R.string.lists),
                        modifier = Modifier.padding(start = 16.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(5F)
                            .padding(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(listOfLists.size) { index ->
                            Box {
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.surfaceTint)
                                            .pointerInput(key1 = index) {
                                                detectTapGestures(
                                                    onDoubleTap = {
                                                        viewModel.onTriggerEvent(BookmarksEvent.DeleteListBookmark(listOfLists[index].nodePk))
                                                    },
                                                    onLongPress = {
                                                        // Nothing
                                                    },
                                                    onTap = {
                                                        navController.navigate(Screen.CustomListDisplay.route + "/" + listOfLists[index].nodePk)
                                                    }
                                                )
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (listimages.isEmpty()
                                                .not() && listimages.size > index
                                        ) {
                                            val imagePainter = rememberImagePainter(
                                                data = listimages[index],
                                                builder = {
                                                    allowHardware(false)
                                                }
                                            )
                                            Image(
                                                painter = imagePainter,
                                                contentDescription = "Your Image",
                                                contentScale = ContentScale.FillBounds,
                                                modifier = Modifier
                                                    .height(55.dp)
                                                    .width(55.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        androidx.compose.material3.Text(
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            text = listOfLists[index]?.title
                                                ?: Constants.NO_LISTS,
                                            modifier = Modifier
                                                .padding(16.dp)
                                                .fillMaxWidth(),
                                            style = MaterialTheme.typography.titleMedium
                                        )
//                                        listOfLists[index]?.let {
//                                            HorizontalScrollableRowComponent(
//                                                list = it
//                                            )
//                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
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
    onTap: (ReflexionItem) -> Unit,
    onDoubleTap: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
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
                                onTap = { onTap(reflexionItem) },
                                onDoubleTap = { onDoubleTap(reflexionItem.autogenPK) }
                            )
                        },
                    elevation = 6.dp,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
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


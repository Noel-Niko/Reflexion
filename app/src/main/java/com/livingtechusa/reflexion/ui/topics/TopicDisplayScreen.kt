package com.livingtechusa.reflexion.ui.topics

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.Bookmarks
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.viewModels.TopicsViewModel
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReflexionItemListUI(
    navController: NavHostController,
    viewModel: TopicsViewModel
) {
    val context: Context = LocalContext.current
    val resource = ResourceProviderSingleton
    val reflexionItemList by viewModel.list.collectAsState()
    val bookmarksList by viewModel.bookmarks.collectAsState()
    val bookmarkImages by viewModel.bookmarkImages.collectAsState()
    Scaffold(
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(.7f)
                ) {
                    ReflexionItemsContent(
                        reflexionItems = reflexionItemList,
                        onTap = { reflexionItem ->
                            navController.navigate(route = Screen.BuildItemScreen.route + "/" + reflexionItem.autogenPk) {
                                launchSingleTop = true
                            }
                        },
                        onLongPress = { reflexionItem ->
                            CoroutineScope(Dispatchers.Main).launch {
                                if (viewModel.hasNoChildren(reflexionItem.autogenPk)) {
                                    Toast.makeText(
                                        context,
                                        resource.getString(R.string.no_child_items_found),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    viewModel.onTriggerEvent(
                                        TopicItemEvent.GetTopicItem(
                                            reflexionItem.autogenPk
                                        )
                                    )
                                }
                            }
                        }
                    )
                }
                Divider()
                Row(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = resource.getString(R.string.subtopic_bookmarks),
                                modifier = Modifier.padding(start = 16.dp),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            BookmarkItemsContent(
                                viewModel = viewModel,
                                onTap = viewModel::selectLevel,
                                onDoubleTap = viewModel::deleteBookmark
                            )
                        }
                    }
                }
            }
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
    onTap: (ReflexionItem) -> Unit,
    onLongPress: (ReflexionItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        items(reflexionItems) { reflexionItem ->
            val imagePainter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = reflexionItem.image)
                    .listener(
                        onError = { request, result ->
                            Log.e("Image", result.throwable.toString())
                        }
                    )
                    .apply(block = fun ImageRequest.Builder.() {
                        allowHardware(false)
                    }).build()
            )
            Box {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .pointerInput(key1 = reflexionItem) {
                            detectTapGestures(
                                onTap = { onTap(reflexionItem) },
                                onLongPress = { onLongPress(reflexionItem) }
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


@Composable
private fun BookmarkItemsContent(
    viewModel: TopicsViewModel,
    onTap: (Long) -> Unit,
    onDoubleTap: (Long) -> Unit
) {
    val bookmarksList by viewModel.bookmarks.collectAsState()
    val bookmarkImages by viewModel.bookmarkImages.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        items(bookmarksList.size) { bookmark ->
            val imagePainter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .listener(
                        onError = { request, result ->
                            Log.e("Image", result.throwable.toString())
                        }
                    )
                    .data(
                        data =  if (bookmarkImages.size > bookmark && bookmarkImages[bookmark] != null) {
                            bookmarkImages[bookmark]
                        } else {
                            R.mipmap.ic_launcher
                        }
                    )
                    .apply(block = fun ImageRequest.Builder.() {
                        allowHardware(false)
                    }).build()
            )
            Box {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .pointerInput(key1 = bookmark) {
                            detectTapGestures(
                                onTap = { bookmarksList[bookmark]?.LEVEL_PK?.let { pk -> onTap(pk) } },
                                onDoubleTap = {
                                    bookmarksList[bookmark]?.autoGenPk?.let { itemPk ->
                                        onDoubleTap(
                                            itemPk
                                        )
                                    }
                                }
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
                            contentDescription = stringResource(R.string.your_image),
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .width(60.dp)
                                .height(60.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        bookmarksList[bookmark]?.title?.let { title ->
                            Text(
                                modifier = Modifier.padding(16.dp),
                                text = title,
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}


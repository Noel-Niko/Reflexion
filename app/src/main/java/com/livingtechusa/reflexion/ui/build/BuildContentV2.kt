package com.livingtechusa.reflexion.ui.build

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.Constants
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
import com.livingtechusa.reflexion.util.Temporary
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun BuildContentV2(pk: Long, navHostController: NavHostController, viewModel: ItemViewModel, paddingValues: PaddingValues
) {
    val URI = "/Uri"
    val URL = "/Url"
    val context = LocalContext.current
//    val scaffoldState = rememberScaffoldState()

    val itemViewModel: ItemViewModel = viewModel
    val scope = rememberCoroutineScope()
    val reflexionItem by itemViewModel.reflexionItem.collectAsState()
    val saveNow by itemViewModel.saveNow.collectAsState()
    val resource = ResourceProviderSingleton
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    // If `lifecycleOwner` changes, dispose and reset the effect
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer = LifecycleEventObserver { owner, event ->
            if(event == Lifecycle.Event.ON_CREATE) {
                viewModel.onTriggerEvent(BuildEvent.GetSelectedReflexionItem(pk))
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    var targetVideoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val selectVideo =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                val copy = reflexionItem.copy(videoUri = uri.toString())
                itemViewModel.onTriggerEvent(BuildEvent.UpdateDisplayedReflexionItem(copy))
            })
    val takeVideo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { _ ->
        targetVideoUri?.let { uri ->
            targetVideoUri = null
            val copy = reflexionItem.copy(videoUri = uri.toString())
            itemViewModel.onTriggerEvent(BuildEvent.UpdateDisplayedReflexionItem(copy))
        }
    }


    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (saveNow) {
            Toast.makeText(
                context, resource.getString(R.string.changes_saved), Toast.LENGTH_SHORT
            ).show()
            if (reflexionItem.autogenPK != 0L) {
                reflexionItem.name = reflexionItem.name.trim()
                itemViewModel.onTriggerEvent(
                    BuildEvent.UpdateReflexionItem(
                        reflexionItem
                    )
                )
                Temporary.tempReflexionItem = ReflexionItem()
            } else {
                itemViewModel.onTriggerEvent(BuildEvent.SaveNew(reflexionItem))
                Temporary.tempReflexionItem = ReflexionItem()
            }
            itemViewModel.setSaveNow(false)
        }
        Scaffold(
            floatingActionButton = {
                /* SAVE */
                SmallFloatingActionButton(modifier = Modifier
                    .offset {
                        IntOffset(
                            x = offsetX.value.roundToInt(), y = offsetY.value.roundToInt()
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consumeAllChanges()
                            offsetX.value += dragAmount.x
                            offsetY.value += dragAmount.y
                        }
                    },
                    containerColor = MaterialTheme.colors.primary,
                    onClick = {
                        Toast.makeText(
                            context, resource.getString(R.string.changes_saved), Toast.LENGTH_SHORT
                        ).show()
                        if (reflexionItem.autogenPK != 0L) {
                            reflexionItem.autogenPK = reflexionItem.autogenPK
                            reflexionItem.name = reflexionItem.name.trim()
                            itemViewModel.onTriggerEvent(
                                BuildEvent.UpdateReflexionItem(
                                    reflexionItem
                                )
                            )
                            Temporary.tempReflexionItem = ReflexionItem()
                        } else {
                            itemViewModel.onTriggerEvent(BuildEvent.SaveNew(reflexionItem))
                            Temporary.tempReflexionItem = ReflexionItem()
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_save_alt_24),
                        contentDescription = null
                    )
                }
            }) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                item {
                    Spacer(Modifier.height(16.dp))/* TOPIC */
                    if (reflexionItem.parent == null) {
                        /* TOPIC */
                        Row(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Column(
                                Modifier
                                    .align(Alignment.CenterVertically)
                            ) {
                                Text(text = stringResource(R.string.topic))
                            }
                            Column(
                                Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically)
                            ) {
                                TextField(
                                    colors = TextFieldDefaults.textFieldColors(
                                        textColor = Black,
                                        backgroundColor = Transparent
                                    ),
                                    textStyle = MaterialTheme.typography.h6,
                                    modifier = Modifier.fillMaxWidth(),
                                    value = if (reflexionItem.name == Constants.EMPTY_ITEM) {
                                        ""
                                    } else {
                                        reflexionItem.name
                                    },
                                    onValueChange = { name ->
                                        val copy = reflexionItem.copy(name = name)
                                        itemViewModel.onTriggerEvent(
                                            BuildEvent.UpdateDisplayedReflexionItem(
                                                copy
                                            )
                                        )
                                    })
                            }
                        }
                    } else {
                        /* TITLE */
                        Row(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Column(
                                Modifier
                                    .align(Alignment.CenterVertically)
                            ) {
                                Text(text = stringResource(R.string.title))
                            }
                            Column(
                                Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically)
                            ) {
                                TextField(
                                    colors = TextFieldDefaults.textFieldColors(
                                        textColor = Black,
                                        backgroundColor = Transparent
                                    ),
                                    textStyle = MaterialTheme.typography.h6,
                                    value = if (reflexionItem.name == Constants.EMPTY_ITEM) {
                                        ""
                                    } else {
                                        reflexionItem.name
                                    },
                                    onValueChange = { name ->
                                        val copy = reflexionItem.copy(name = name)
                                        itemViewModel.onTriggerEvent(
                                            BuildEvent.UpdateDisplayedReflexionItem(
                                                copy
                                            )
                                        )
                                    })
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    /* DESCRIPTION */
                    Row(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Column(
                            Modifier
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(text = stringResource(R.string.description))
                        }
                        Column(
                            Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            TextField(
                                colors = TextFieldDefaults.textFieldColors(
                                    textColor = Black,
                                    backgroundColor = Transparent
                                ),
                                textStyle = MaterialTheme.typography.h6,
                                modifier = Modifier.fillMaxWidth(),
                                value = reflexionItem.description ?: Constants.EMPTY_STRING,
                                onValueChange = { description ->
                                    val copy = reflexionItem.copy(description = description)
                                    itemViewModel.onTriggerEvent(
                                        BuildEvent.UpdateDisplayedReflexionItem(
                                            copy
                                        )
                                    )
                                })
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    /* DETAILS */
                    Row(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Column(
                            Modifier
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(text = stringResource(R.string.detailedDescription))
                        }
                        Column(
                            Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            TextField(
                                colors = TextFieldDefaults.textFieldColors(
                                    textColor = Black,
                                    backgroundColor = Transparent
                                ),
                                textStyle = MaterialTheme.typography.h6,
                                modifier = Modifier.fillMaxWidth(),
                                value = reflexionItem.detailedDescription ?: Constants.EMPTY_STRING,
                                onValueChange = { detailedDescription ->
                                    val copy =
                                        reflexionItem.copy(detailedDescription = detailedDescription)
                                    itemViewModel.onTriggerEvent(
                                        BuildEvent.UpdateDisplayedReflexionItem(
                                            copy
                                        )
                                    )
                                })
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    /* SAVED VIDEO */
                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column(
                            Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (reflexionItem.videoUri.isNullOrEmpty()) {
                                            Toast
                                                .makeText(
                                                    context,
                                                    resource.getString(R.string.is_saved),
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        } else {
                                            val route: String = Screen.VideoView.route + URI
                                            navHostController.navigate(route)
                                        }
                                    },
                                text = AnnotatedString(stringResource(R.string.saved_video)),
                                color = Blue

                            )
                        }
                        Column(
                            Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            IconButton(onClick = {
                                selectVideo.launch(Constants.VIDEO)
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_video_library_24),
                                    contentDescription = null
                                )
                            }

                            IconButton(onClick = {
                                scope.launch {
                                    viewModel.createVideoUri()?.let { uri ->
                                        targetVideoUri = uri
                                        takeVideo.launch(uri)
                                    }
                                }
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_videocam_24),
                                    contentDescription = null
                                )
                            }
                        }
                        /* VIDEO URL */
                        Column(
                            Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        onClick = {
                                            if (reflexionItem.videoUrl == Constants.EMPTY_STRING) {
                                                navHostController.navigate(Screen.PasteAndSaveScreen.route)
                                            } else {
                                                val route: String = Screen.VideoView.route + URL
                                                navHostController.navigate(route)
                                            }
                                        },
                                    ),
                                text = AnnotatedString(stringResource(R.string.video_link)),
                                color = Blue
                            )
                        }
                        Column(
                            Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            IconButton(onClick = {
                                val query = Constants.SEARCH_YOUTUBE + reflexionItem.name
                                val intent = Intent(
                                    Intent.ACTION_VIEW, Uri.parse(query)
                                )
                                ContextCompat.startActivity(context, intent, null)
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_youtube_searched_for_24),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
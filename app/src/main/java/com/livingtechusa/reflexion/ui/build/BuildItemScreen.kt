package com.livingtechusa.reflexion.ui.build

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.components.MainTopBar
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import com.livingtechusa.reflexion.util.Constants.SEARCH_YOUTUBE
import com.livingtechusa.reflexion.util.Constants.VIDEO
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
import com.livingtechusa.reflexion.util.Temporary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val BuildRoute = "build"

@Composable
fun BuildItemScreen(
    navHostController: NavHostController,
    viewModel: ItemViewModel = hiltViewModel(),
) {
    val URI = "/Uri"
    val URL = "/Url"
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        // BuildItemScreenLandscape(navHostController)
    } else {
        val itemViewModel: ItemViewModel = viewModel
        val scope = rememberCoroutineScope()
        val error by viewModel.errorFlow.collectAsState(null)
        val savedReflexionItem by itemViewModel.reflexionItem.collectAsState()
        val resource = ResourceProviderSingleton
        val reflexionItem =
            ReflexionItem(
                autogenPK = savedReflexionItem.autogenPK,
                name = savedReflexionItem.name,
                description = savedReflexionItem.description,
                detailedDescription = savedReflexionItem.detailedDescription,
                image = savedReflexionItem.image,
                videoUri = savedReflexionItem.videoUri,
                videoUrl = savedReflexionItem.videoUrl,
                parent = savedReflexionItem.parent,
                hasChildren = savedReflexionItem.hasChildren
            )

        var targetVideoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
        val selectVideo = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                val copy = reflexionItem.copy(videoUri = uri.toString())
                itemViewModel.onTriggerEvent(BuildEvent.UpdateDisplayedReflexionItem(copy))
            }
        )

        val takeVideo = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CaptureVideo()
        ) { _ ->
            targetVideoUri?.let { uri ->
                targetVideoUri = null
                val copy = reflexionItem.copy(videoUri = uri.toString())
                itemViewModel.onTriggerEvent(BuildEvent.UpdateDisplayedReflexionItem(copy))
            }
        }

        val scaffoldState = rememberScaffoldState()

        LaunchedEffect(error) {
            error?.let { scaffoldState.snackbarHostState.showSnackbar(it) }
        }

        Scaffold(
            topBar = { MainTopBar() },
            floatingActionButton = {
                /* SAVE */
                FloatingActionButton(onClick = {
                    Toast.makeText(
                        context,
                        resource.getString(R.string.changes_saved),
                        Toast.LENGTH_SHORT
                    ).show()
                    if (savedReflexionItem.autogenPK != 0L) {
                        reflexionItem.autogenPK = savedReflexionItem.autogenPK
                        reflexionItem.name = reflexionItem.name.trim()
                        itemViewModel.onTriggerEvent(BuildEvent.UpdateReflexionItem(reflexionItem))
                        Temporary.tempReflexionItem = ReflexionItem()
                    } else {
                        itemViewModel.onTriggerEvent(BuildEvent.SaveNew(reflexionItem))
                        Temporary.tempReflexionItem = ReflexionItem()
                    }
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_save_alt_24),
                        contentDescription = null
                    )
                }
            }
        ) { paddingValues ->
            paddingValues // padding values?
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    Spacer(Modifier.height(16.dp))
                    /* TOPIC */
                    if (savedReflexionItem.parent == null) {
                        Row(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Column(
                                Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically)
                            ) {
                                Text(text = stringResource(R.string.topic))
                            }
                            Column(
                                Modifier
                                    .weight(3f)
                                    .align(Alignment.CenterVertically)
                            ) {
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = reflexionItem.name,
                                    onValueChange = { name ->
                                        val copy = reflexionItem.copy(name = name)
                                        itemViewModel.onTriggerEvent(
                                            BuildEvent.UpdateDisplayedReflexionItem(
                                                copy
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    } else {
                        /* TITLE */
                        Row(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Column(
                                Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically)
                            ) {
                                Text(text = stringResource(R.string.title))
                            }
                            Column(
                                Modifier
                                    .weight(3f)
                                    .align(Alignment.CenterVertically)
                            ) {
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = reflexionItem.name,
                                    onValueChange = { name ->
                                        val copy = reflexionItem.copy(name = name)
                                        itemViewModel.onTriggerEvent(
                                            BuildEvent.UpdateDisplayedReflexionItem(
                                                copy
                                            )
                                        )
                                    }
                                )
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
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(text = stringResource(R.string.description))
                        }
                        Column(
                            Modifier
                                .weight(3f)
                                .align(Alignment.CenterVertically)
                        ) {
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = reflexionItem.description ?: EMPTY_STRING,
                                onValueChange = { description ->
                                    val copy = reflexionItem.copy(description = description)
                                    itemViewModel.onTriggerEvent(
                                        BuildEvent.UpdateDisplayedReflexionItem(
                                            copy
                                        )
                                    )
                                }
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    /* DETAILS */
                    Row(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Column(
                            Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(text = stringResource(R.string.detailedDescription))
                        }
                        Column(
                            Modifier
                                .weight(3f)
                                .align(Alignment.CenterVertically)
                        ) {
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = reflexionItem.detailedDescription ?: EMPTY_STRING,
                                onValueChange = { detailedDescription ->
                                    val copy =
                                        reflexionItem.copy(detailedDescription = detailedDescription)
                                    itemViewModel.onTriggerEvent(
                                        BuildEvent.UpdateDisplayedReflexionItem(
                                            copy
                                        )
                                    )
                                }
                            )
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
                                color = Color.Blue

                            )
                        }
                        Column(
                            Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            IconButton(
                                onClick = {
                                    selectVideo.launch(VIDEO)
                                }) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_video_library_24),
                                    contentDescription = null
                                )
                            }

                            IconButton(
                                onClick = {
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
                                            if (reflexionItem.videoUrl == EMPTY_STRING) {
                                                navHostController.navigate(Screen.PasteAndSaveScreen.route)
                                            } else {
                                                val route: String = Screen.VideoView.route + URL
                                                navHostController.navigate(route)
                                            }
                                        },
                                    ),
                                text = AnnotatedString(stringResource(R.string.video_link)),
                                color = Color.Blue
                            )
                        }
                        Column(
                            Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            IconButton(
                                onClick = {
                                    val query = SEARCH_YOUTUBE + reflexionItem.name
                                    val intent = Intent(
                                        Intent.ACTION_VIEW, Uri.parse(query)
                                    )
                                    startActivity(context, intent, null)
                                }) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_youtube_searched_for_24),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        /* SIBLINGS */
                        Column(
                            Modifier.weight(1f)
                        ) {
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT)
                                        .show()
                                    itemViewModel.onTriggerEvent(BuildEvent.ShowSiblings)
                                }
                            ) {
                                Text(
                                    stringResource(R.string.siblings)
                                )
                            }
                        }
                        /* CHILDREN */
                        Column(
                            Modifier.weight(1f)
                        ) {
                            Button(onClick = {
                                navHostController.navigate(Screen.ListScreen.route + "/" + reflexionItem.autogenPK)
                            }
                            ) {
                                Text(
                                    stringResource(R.string.children)
                                )
                            }
                        }
                    }
                    /* DELETE ITEM */
                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        if (savedReflexionItem.hasChildren == false) {
                            Column(
                                Modifier.weight(1f)
                            ) {
                                Button(onClick = {
                                    scope.launch {
                                        withContext(Dispatchers.Main) {
                                            if (itemViewModel.hasNoChildren(reflexionItem.autogenPK)) {
                                                itemViewModel.onTriggerEvent(BuildEvent.Delete)
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    resource.getString(R.string.is_parent),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }
                                ) {
                                    Text(stringResource(R.string.delete))
                                }
                            }
                        }
                        Column(
                            Modifier.weight(1f)
                        ) {
                            Button(onClick = {
                                val parent = reflexionItem.autogenPK
                                itemViewModel.onTriggerEvent(BuildEvent.ClearReflexionItem)
                                itemViewModel.onTriggerEvent(BuildEvent.SetParent(parent))
                            }
                            ) {
                                Text(stringResource(R.string.add_child))
                            }
                        }
                    }
                    /* NEW ITEM */
                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column(
                            Modifier.weight(1f)
                        ) {
                            Button(onClick = {
                                itemViewModel.onTriggerEvent(BuildEvent.ClearReflexionItem)
                            }
                            ) {
                                Text(stringResource(R.string.new_item))
                            }
                        }
                    }
                    /* PARENT */
                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column(
                            Modifier.weight(1f)
                        ) {
                            Button(onClick = {
                                val parent = reflexionItem.parent
                                if (parent != null) {
                                    itemViewModel.onTriggerEvent(BuildEvent.ClearReflexionItem)
                                    itemViewModel.onTriggerEvent(
                                        BuildEvent.GetSelectedReflexionItem(
                                            parent
                                        )
                                    )
                                } else {
                                    Toast.makeText(
                                        context,
                                        resource.getString(R.string.no_parent_found),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            ) {
                                Text(stringResource(R.string.go_to_parent))
                            }
                        }
                    }
                }
            }
        }
    }
}


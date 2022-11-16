package com.livingtechusa.reflexion.ui.build

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.ui.children.ChildRoute
import com.livingtechusa.reflexion.ui.components.VideoView
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.livingtechusa.reflexion.ui.children.ChildrenScreen
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import com.livingtechusa.reflexion.util.Constants.VIDEO
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

const val BuildRoute = "build"
@Composable
fun BuildItemScreen(
    viewModel: ItemViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        // BuildItemScreenLandscape(navHostController)
    } else {
        val itemViewModel: ItemViewModel = viewModel
        val savedReflexionItem by itemViewModel.reflexionItem.collectAsState()
        val isParent by itemViewModel.isParent.collectAsState()
        //        val topic by itemViewModel.topic.collectAsState()
        val parentName by itemViewModel.parentName.collectAsState()
        val keyWords by itemViewModel.keyWords.collectAsState()
        val linkedLists by itemViewModel.linkedLists.collectAsState()
        val siblings by itemViewModel.siblings.collectAsState()
        val children by itemViewModel.children.collectAsState()
        val context = LocalContext.current
        val reflexionItem = remember {
            mutableStateOf(
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
            )
        }

        val name = remember { mutableStateOf(savedReflexionItem.name) }
        val description = remember { mutableStateOf(savedReflexionItem.description) }
        val detailedDescription =
            remember { mutableStateOf(savedReflexionItem.detailedDescription) }
        val image = remember { mutableStateOf(savedReflexionItem.image) }
        val videoUri = remember { mutableStateOf(savedReflexionItem.videoUri) }
        val videoUrl = remember { mutableStateOf(savedReflexionItem.videoUrl) }
        val parent = remember { mutableStateOf(savedReflexionItem.parent) }

        var targetVideoUri by rememberSaveable { mutableStateOf<Uri?>(null) }

        val showSavedVideo = remember { mutableStateOf(false) }
        val showOnLineVideo = remember { mutableStateOf(false) }
        val selectVideo = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                reflexionItem.value.videoUri = uri.toString()
            }
        )

        val takeVideo = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CaptureVideo()
        ) { _ ->
            targetVideoUri?.let { uri ->
                targetVideoUri = null
                reflexionItem.value.videoUri = uri.toString()
            }

        }

        val scaffoldState = rememberScaffoldState()
        Scaffold(
            floatingActionButton = {
                /* SAVE */
                FloatingActionButton(onClick = {
                    Toast.makeText(context, "Changes Saved", Toast.LENGTH_SHORT).show()
                    if (savedReflexionItem.autogenPK != 0L) {
                        reflexionItem.value.autogenPK = savedReflexionItem.autogenPK
                        reflexionItem.value.name = reflexionItem.value.name.trim()
                        itemViewModel.onTriggerEvent(BuildEvent.UpdateReflexionItem(reflexionItem.value))
                    } else {
                        itemViewModel.onTriggerEvent(BuildEvent.SaveNew(reflexionItem.value))
                    }
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_save_alt_24),
                        contentDescription = null
                    )
                }
            }
        ) {
            it // padding values?
            if (showSavedVideo.value && reflexionItem.value.videoUri.isNullOrEmpty().not()) {
                VideoView(videoUri = reflexionItem.value.videoUri ?: EMPTY_STRING)
            } else if (showOnLineVideo.value && reflexionItem.value.videoUrl.isNullOrEmpty().not()) {
                VideoView(videoUri = reflexionItem.value.videoUrl ?: EMPTY_STRING)
            }
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
                                    value = name.value,
                                    onValueChange = {
                                        name.value = it
                                        reflexionItem.value.name = it
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
                                    value = name.value,
                                    onValueChange = {
                                        name.value = it
                                        reflexionItem.value.name = it
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
                                value = description.value ?: "",
                                onValueChange = {
                                    description.value = it
                                    reflexionItem.value.description = it
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
                                value = detailedDescription.value ?: "",
                                onValueChange = {
                                    detailedDescription.value = it
                                    reflexionItem.value.detailedDescription = it
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
                                        showSavedVideo.value = true
                                        showOnLineVideo.value = false
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
                                    painter = painterResource(R.drawable.ic_baseline_get_app_24),
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
                                            showOnLineVideo.value = true
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
                                    Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT)
                                        .show()
                                    // todo capture and pass url
                                    itemViewModel.onTriggerEvent(
                                        BuildEvent.UpdateReflexionItem(
                                            savedReflexionItem
                                        )
                                    )
                                }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_baseline_get_app_24),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    /* SIBLINGS */
                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column(
                            Modifier.weight(1f)
                        ) {
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Button Clicked", Toast.LENGTH_LONG)
                                        .show()
                                    itemViewModel.onTriggerEvent(BuildEvent.ShowSiblings)

                                }
                            ) {
                                Text(
                                    stringResource(R.string.siblings)
                                )
                            }
                        }
                        Column(
                            Modifier.weight(1f)
                        ) {
                            Button(onClick = {
                                Toast.makeText(context, "Button Clicked", Toast.LENGTH_LONG).show()
                                itemViewModel.onTriggerEvent(BuildEvent.ShowChildren)
                            }
                            ) {
                                Text(
                                    stringResource(R.string.children)
                                )
                            }
                        }
                    }
                    /* CHILDREN */
                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        if (savedReflexionItem.hasChildren == false) {
                            Column(
                                Modifier.weight(1f)
                            ) {
                                Button(onClick = {
                                    Toast.makeText(context, "Button Clicked", Toast.LENGTH_LONG)
                                        .show()
                                    itemViewModel.onTriggerEvent(BuildEvent.Delete)
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
                                Toast.makeText(context, "Button Clicked", Toast.LENGTH_LONG).show()
                                itemViewModel.onTriggerEvent(BuildEvent.Delete)
                            }
                            ) {
                                Text(stringResource(R.string.add_child))
                            }
                        }
                    }
                }
            }
        }
    }
}

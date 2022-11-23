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
import androidx.compose.runtime.remember
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
import com.livingtechusa.reflexion.ui.components.PasteAndSaveDialog
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.Constants
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import com.livingtechusa.reflexion.util.Constants.SEARCH_YOUTUBE
import com.livingtechusa.reflexion.util.Constants.VIDEO
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
import com.livingtechusa.reflexion.util.SystemBroadcastReceiver
import com.livingtechusa.reflexion.util.Temporary
import kotlinx.coroutines.launch

const val BuildRoute = "build"

@Composable
fun BuildItemScreen(
    navHostController: NavHostController,
    viewModel: ItemViewModel = hiltViewModel(),
    useTempItem: String? = null
) {

    SystemBroadcastReceiver(Intent.ACTION_SEND) { send ->
        val isCharging = /* Get from batteryStatus ... */ true
        /* Do something if the device is charging */
    }


    val URI = "/Uri"
    val URL = "/Url"
    val configuration = LocalConfiguration.current
    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        // BuildItemScreenLandscape(navHostController)
    } else {
        val itemViewModel: ItemViewModel = viewModel
        val scope = rememberCoroutineScope()
        val error by viewModel.errorFlow.collectAsState(null)
        val savedReflexionItem by itemViewModel.reflexionItem.collectAsState()
        val isParent by itemViewModel.isParent.collectAsState()
        //        val topic by itemViewModel.topic.collectAsState()
        val parentName by itemViewModel.parentName.collectAsState()
        val keyWords by itemViewModel.keyWords.collectAsState()
        val linkedLists by itemViewModel.linkedLists.collectAsState()
        val siblings by itemViewModel.siblings.collectAsState()
        val children by itemViewModel.children.collectAsState()
        val resource = ResourceProviderSingleton

        val context = LocalContext.current
        val reflexionItem = rememberSaveable {
            mutableStateOf(
                ReflexionItem(
                    autogenPK = savedReflexionItem.autogenPK,
                    name = savedReflexionItem.name,
                    description = savedReflexionItem.description,
                    detailedDescription = savedReflexionItem.detailedDescription,
                    image = savedReflexionItem.image,
                    videoUri = savedReflexionItem.videoUri,
                    videoUrl =  savedReflexionItem.videoUrl,
                    parent = savedReflexionItem.parent,
                    hasChildren = savedReflexionItem.hasChildren
                )
            )
        }

        fun addUrl(urlString: String) {
            reflexionItem.value.videoUrl = urlString
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
                Temporary.tempReflexionItem.videoUri = uri.toString()
            }
        )

        val takeVideo = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CaptureVideo()
        ) { _ ->
            targetVideoUri?.let { uri ->
                targetVideoUri = null
                reflexionItem.value.videoUri = uri.toString()
                Temporary.tempReflexionItem.videoUrl = uri.toString()
            }
        }

        val scaffoldState = rememberScaffoldState()

        LaunchedEffect(error) {
            error?.let { scaffoldState.snackbarHostState.showSnackbar(it) }
        }

        Scaffold(
            floatingActionButton = {
                /* SAVE */
                FloatingActionButton(onClick = {
                    Toast.makeText(context, resource.getString(R.string.changes_saved), Toast.LENGTH_SHORT).show()
                    if (savedReflexionItem.autogenPK != 0L) {
                        reflexionItem.value.autogenPK = savedReflexionItem.autogenPK
                        reflexionItem.value.name = reflexionItem.value.name.trim()
                        itemViewModel.onTriggerEvent(BuildEvent.UpdateReflexionItem(reflexionItem.value))
                        Temporary.tempReflexionItem = ReflexionItem()
                    } else {
                        itemViewModel.onTriggerEvent(BuildEvent.SaveNew(reflexionItem.value))
                        Temporary.tempReflexionItem = ReflexionItem()
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
                                        if (reflexionItem.value.videoUri.isNullOrEmpty()) {
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
                                            if (reflexionItem.value.videoUrl == EMPTY_STRING) {
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
                                    Temporary.tempReflexionItem = reflexionItem.value
                                    val query = SEARCH_YOUTUBE + reflexionItem.value.name
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
                    /* SIBLINGS */
                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
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
                        Column(
                            Modifier.weight(1f)
                        ) {
                            Button(onClick = {
                                Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show()
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
                                    Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT)
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
                                Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show()
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

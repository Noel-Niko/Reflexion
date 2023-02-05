package com.livingtechusa.reflexion.ui.build

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.components.ImageCard
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.Constants
import com.livingtechusa.reflexion.util.Constants.DESCRIPTION
import com.livingtechusa.reflexion.util.Constants.DETAILED_DESCRIPTION
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import com.livingtechusa.reflexion.util.Constants.IMAGE
import com.livingtechusa.reflexion.util.Constants.NAME
import com.livingtechusa.reflexion.util.Constants.VIDEO_URI
import com.livingtechusa.reflexion.util.Constants.VIDEO_URL
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
import com.livingtechusa.reflexion.util.Temporary
import com.livingtechusa.reflexion.util.scopedStorageUtils.DocumentFilePreviewCard
import com.livingtechusa.reflexion.util.scopedStorageUtils.MediaStoreUtils
import com.livingtechusa.reflexion.util.scopedStorageUtils.videoImagePreviewCard
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun BuildItemContent(
    pk: Long,
    navController: NavHostController,
    viewModel: ItemViewModel,
    paddingValues: PaddingValues
) {
    val URI = "Uri"
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
//    val reflexionItem by itemViewModel.reflexionItem.collectAsState()

    val name by viewModel.name.collectAsState()
    val description by viewModel.description.collectAsState()
    val detailedDescription by viewModel.detailedDescription.collectAsState()
    val image by viewModel.image.collectAsStateWithLifecycle()
    val videoUri by viewModel.videoUri.collectAsState()
    val videoUrl by viewModel.videoUrl.collectAsState()
    val parent by viewModel.parent.collectAsState()
    val autogenPK by viewModel.autogenPK.collectAsState()


    val saveNow by viewModel.saveNowFromTopBar.collectAsState()
    val resource = ResourceProviderSingleton
    val selectedFile by viewModel.selectedFile.collectAsState()
    val colorStops: Array<out Pair<Float, Color>> =
        arrayOf(Pair(10f, Color.Black), Pair(5f, Color.Red))

    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    // If `lifecycleOwner` changes, dispose and reset the effect
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer = LifecycleEventObserver { owner, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                if(Temporary.use) {
                    viewModel.onTriggerEvent(BuildEvent.SearchUri(Temporary.uri))
                } else {
                    viewModel.onTriggerEvent(BuildEvent.GetSelectedReflexionItem(pk))
                }
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

    val selectVideo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            viewModel.onTriggerEvent(
                BuildEvent.UpdateDisplayedReflexionItem(
                    subItem = VIDEO_URI, newVal = uri.toString()
                )
            )
        })

    val takeVideo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { _ ->
        targetVideoUri?.let { uri ->
            targetVideoUri = null
            viewModel.onTriggerEvent(
                BuildEvent.UpdateDisplayedReflexionItem(
                    subItem = VIDEO_URI, newVal = uri.toString()
                )
            )
        }
    }

    var targetImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val selectImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            viewModel.onTriggerEvent(
                BuildEvent.CreateThumbnailImage(uri)
            )
        })

    val takeImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { _ ->
        targetImageUri?.let { uri ->
            targetImageUri = null
            val newVal =
                MediaStoreUtils.uriToByteArray(uri, context = context) ?: emptyArray<Byte>()
            viewModel.onTriggerEvent(
                BuildEvent.UpdateDisplayedReflexionItem(
                    subItem = IMAGE, newVal = newVal
                )
            )
        }
    }
    if (saveNow) {
        Toast.makeText(
            context, resource.getString(R.string.changes_saved), Toast.LENGTH_SHORT
        ).show()
        val trimmedName = name.trim()
        viewModel.onTriggerEvent(
            BuildEvent.UpdateDisplayedReflexionItem(
                subItem = NAME, newVal = trimmedName
            )
        )
        if (autogenPK != 0L) {
            viewModel.onTriggerEvent(BuildEvent.UpdateReflexionItem)
        } else {
            viewModel.onTriggerEvent(BuildEvent.SaveNew)
        }
        viewModel.setSaveNowFromTopBar(false)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(16.dp))
        // Image
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .weight(3f)
                    .align(Alignment.CenterVertically)
            ) {
                Box(
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 32.dp, 0.dp)
                        .border(
                            1.dp,
                            verticalGradient(colorStops = colorStops),
                            TextFieldDefaults.OutlinedTextFieldShape
                        )
                        .align(Alignment.End)
                ) {
                    if (image != null) {
                        showImage(image!!, navController)
                    } else {
                        Text(
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.add_an_image_here),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Column(Modifier.weight(1f)) {
                IconButton(onClick = {
                    selectImage.launch(Constants.IMAGE_TYPE)
                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_video_library_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = {
                    scope.launch {
                        viewModel.createImageUri()?.let { uri ->
                            targetImageUri = uri
                            takeImage.launch(uri)
                        }
                    }
                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_photo_camera_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = {
                    viewModel.onTriggerEvent(BuildEvent.RotateImage)
                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_rotate_90_degrees_ccw_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }/* TOPIC */
        if (parent == null) {/* TOPIC */
            Row(
                modifier = Modifier.padding(12.dp)
            ) {
                Column(
                    Modifier.align(Alignment.Top)
                ) {
                    Text(
                        text = stringResource(R.string.topic),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column(
                    Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    TextField(colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colorScheme.onSurface,
                        backgroundColor = Transparent
                    ),
                        textStyle = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.fillMaxWidth(),
                        value = if (name == Constants.EMPTY_ITEM) {
                            EMPTY_STRING
                        } else {
                            name
                        },
                        onValueChange = { name ->
                            viewModel.onTriggerEvent(
                                BuildEvent.UpdateDisplayedReflexionItem(
                                    subItem = NAME, newVal = name
                                )
                            )
                        })
                }
            }
        } else {/* TITLE */
            Row(
                modifier = Modifier.padding(12.dp)
            ) {
                Column(
                    Modifier.align(Alignment.Top)
                ) {
                    Text(
                        text = stringResource(R.string.title),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column(
                    Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    TextField(colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colorScheme.onSurface,
                        backgroundColor = Transparent
                    ),
                        textStyle = MaterialTheme.typography.labelLarge,
                        value = if (name == Constants.EMPTY_ITEM) {
                            EMPTY_STRING
                        } else {
                            name
                        },
                        onValueChange = { name ->
                            viewModel.onTriggerEvent(
                                BuildEvent.UpdateDisplayedReflexionItem(
                                    subItem = NAME, newVal = name
                                )
                            )
                        })
                }
            }
        }
        Spacer(Modifier.height(16.dp))/* DESCRIPTION */
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                Modifier.align(Alignment.Top)
            ) {
                Text(
                    text = stringResource(R.string.description),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(
                Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Bottom
            ) {
                TextField(colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    backgroundColor = Transparent
                ),
                    textStyle = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.fillMaxWidth(),
                    value = description ?: EMPTY_STRING,
                    onValueChange = { description ->
                        viewModel.onTriggerEvent(
                            BuildEvent.UpdateDisplayedReflexionItem(
                                subItem = DESCRIPTION, newVal = description
                            )
                        )
                    })
            }
        }
        Spacer(Modifier.height(16.dp))/* DETAILS */
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                Modifier.align(Alignment.Top)
            ) {
                Text(
                    text = stringResource(R.string.detailedDescription),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(
                Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Bottom
            ) {
                TextField(
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colorScheme.onSurface,
                        backgroundColor = Transparent
                    ),
                    textStyle = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.fillMaxWidth(),
                    value = detailedDescription ?: EMPTY_STRING,
                    onValueChange = { detailedDescription ->
                        viewModel.onTriggerEvent(
                            BuildEvent.UpdateDisplayedReflexionItem(
                                subItem = DETAILED_DESCRIPTION, newVal = detailedDescription
                            )
                        )
                    }
                )
            }
        }
        Spacer(Modifier.height(16.dp))/* SAVED VIDEO */
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (videoUri.isNullOrEmpty()) {
                                    Toast
                                        .makeText(
                                            context,
                                            resource.getString(R.string.is_saved),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else {
                                    //
                                    val route: String = Screen.VideoView.route + "/" + URI
                                    navController.navigate(route)
                                }
                            },
                        text = AnnotatedString(stringResource(R.string.saved_video)),
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (videoUri.isNullOrEmpty().not()) {
                        viewModel.getSelectedFile()
                        if (selectedFile != null) {
                            DocumentFilePreviewCard(
                                resource = selectedFile!!,
                                navController = navController,
                                VIDEO_URI
                            )
                        }
                    }
                }
            }
            Column(
                Modifier
                    .weight(.5f)
                    .align(Alignment.CenterVertically)
            ) {
                IconButton(onClick = {
                    selectVideo.launch(arrayOf<String>(Constants.VIDEO_TYPE))
                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_video_library_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
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
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }/* VIDEO URL */
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
                                if (videoUrl == EMPTY_STRING) {
                                    navController.navigate(Screen.PasteAndSaveScreen.route)
                                } else {

                                    val intent = Intent(
                                        Intent.ACTION_VIEW, Uri.parse(videoUrl)
                                    )
                                    ContextCompat.startActivity(context, intent, null)
                                }
                            },
                        ),
                    text = AnnotatedString(stringResource(R.string.video_link)),
                    color = MaterialTheme.colorScheme.primary
                )
                if (videoUrl.isNullOrEmpty().not()) {
                    videoImagePreviewCard(
                        urlString = videoUrl, navController = navController, docType = VIDEO_URL
                    )
                }
            }
            Column(
                Modifier
                    .weight(0.5f)
                    .align(Alignment.CenterVertically)
            ) {
                IconButton(onClick = {
                    val query = Constants.SEARCH_YOUTUBE + name
                    val intent = Intent(
                        Intent.ACTION_VIEW, Uri.parse(query)
                    )
                    ContextCompat.startActivity(context, intent, null)
                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_youtube_searched_for_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}


@Composable
fun showImage(image: Bitmap, navController: NavHostController) {
    ImageCard(
        image, navController
    )
}


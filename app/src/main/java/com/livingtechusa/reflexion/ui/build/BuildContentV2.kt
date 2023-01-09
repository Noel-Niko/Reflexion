package com.livingtechusa.reflexion.ui.build

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
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
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.Converters
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.Constants
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
import com.livingtechusa.reflexion.util.Temporary
import com.livingtechusa.reflexion.ui.components.ImageCard
import com.livingtechusa.reflexion.util.scopedStorageUtils.MediaStoreUtils
import com.livingtechusa.reflexion.util.scopedStorageUtils.DocumentFilePreviewCard
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import java.io.InputStream
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.drawable.toAdaptiveIcon
import androidx.core.net.toFile
import coil.size.Size
import com.livingtechusa.reflexion.util.scopedStorageUtils.SafeUtils
import com.livingtechusa.reflexion.util.scopedStorageUtils.SafeUtils.getThumbnail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.ByteArrayInputStream

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun BuildContentV2(
    pk: Long,
    navController: NavHostController,
    viewModel: ItemViewModel,
    paddingValues: PaddingValues
) {
    val URI = "Uri"
    val context = LocalContext.current
    val itemViewModel: ItemViewModel = viewModel
    val scope = rememberCoroutineScope()
    val reflexionItem by itemViewModel.reflexionItem.collectAsState()
    val saveNow by itemViewModel.saveNow.collectAsState()
    val resource = ResourceProviderSingleton
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val selectedFile by viewModel.selectedFile.collectAsState()

//    val selectFile =
//        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
//            uri?.let { viewModel.onFileSelect(it) }
//        }
    // If `lifecycleOwner` changes, dispose and reset the effect
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer = LifecycleEventObserver { owner, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
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
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument(),
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

    var targetImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val selectImage =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->

////                val takeFlags : Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
////                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
////                if (uri != null) {
////                    context.contentResolver.takePersistableUriPermission(uri, takeFlags)
////                }
//                CoroutineScope(Dispatchers.IO).launch {
//                    if (uri != null) {
//                        val bitMap: Bitmap? = SafeUtils.getThumbnail(context, uri)
//                        if(bitMap != null) {
//                            val copy = reflexionItem.copy(image = Converters().convertBitMapToByteArray(bitmap = bitMap))
//                            itemViewModel.onTriggerEvent(BuildEvent.UpdateDisplayedReflexionItem(copy))
//                        }
//                    }
//                }

//                val size = android.util.Size(200, 300)
//                val bitMap: Bitmap? = uri?.let {
//                    context.contentResolver.loadThumbnail(it, size, null)
//                }
//
//                if(bitMap != null) {
//                    val copy = reflexionItem.copy(image = Converters().convertBitMapToByteArray(bitmap = bitMap))
//                    itemViewModel.onTriggerEvent(BuildEvent.UpdateDisplayedReflexionItem(copy))
//                }
                val iStream: InputStream? = context.contentResolver.openInputStream(uri ?: Uri.EMPTY)
//                val takeFlags : Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
//                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//                if (uri != null) {
//                    context.contentResolver.takePersistableUriPermission(uri, takeFlags)
//                }
                if(iStream != null) {
                   //Reduce the image to a thumbnail
                    val bitmap = BitmapFactory.decodeStream(iStream)
                    val thumbNail = ThumbnailUtils.extractThumbnail(bitmap, 200, 300)


//                    val thumb = uri?.toString()
//                        ?.let { ThumbnailUtils.createImageThumbnail(it,  MediaStore.Images.Thumbnails.MINI_KIND) }
                    if (thumbNail != null) {
                        val copy = reflexionItem.copy(image = Converters().convertBitMapToByteArray(thumbNail))
                        itemViewModel.onTriggerEvent(BuildEvent.UpdateDisplayedReflexionItem(copy))
                    }

//                    val copy = reflexionItem.copy(image = Converters().getBytes(inputStream = iStream))
//                    itemViewModel.onTriggerEvent(BuildEvent.UpdateDisplayedReflexionItem(copy))
                }
                iStream?.close()
            })

    val takeImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { _ ->
        targetImageUri?.let { uri ->
            targetImageUri = null
            val copy = reflexionItem.copy(image = MediaStoreUtils.uriToByteArray(uri, context = context))
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
                    Spacer(Modifier.height(16.dp))
                    /* TOPIC */
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
                                            if (reflexionItem.videoUri.isNullOrEmpty()) {
                                                Toast
                                                    .makeText(
                                                        context,
                                                        resource.getString(R.string.is_saved),
                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            } else {
                                                val route: String =
                                                    Screen.VideoView.route + "/" + URI
                                                navController.navigate(route)
                                            }
                                        },
                                    text = AnnotatedString(stringResource(R.string.saved_video)),
                                    color = Blue
                                )
                                if (reflexionItem.videoUri.isNullOrEmpty().not()) {
                                    viewModel.getSelectedFile()
                                    if (selectedFile != null) {
                                        DocumentFilePreviewCard(resource = selectedFile!!, navController = navController)
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
                                                navController.navigate(Screen.PasteAndSaveScreen.route)
                                            } else {
                                                val intent = Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse(reflexionItem.videoUrl)
                                                )
                                                ContextCompat.startActivity(context, intent, null)
                                            }
                                        },
                                    ),
                                text = AnnotatedString(stringResource(R.string.video_link)),
                                color = Blue
                            )
                        }
                        Column(
                            Modifier
                                .weight(0.5f)
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
                    Spacer(Modifier.height(16.dp))
                    // Display Image
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            Modifier
                                .weight(4f)
                                .padding(16.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            if(reflexionItem.image != null) {
                               ImageCard(reflexionItem.image, navController)
                            } else {
                                Text(text = stringResource(R.string.add_an_image_here))
                            }
                        }
                        Column(
                            Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            IconButton(onClick = {
                                selectImage.launch(Constants.IMAGE_TYPE)
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_video_library_24),
                                    contentDescription = null
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
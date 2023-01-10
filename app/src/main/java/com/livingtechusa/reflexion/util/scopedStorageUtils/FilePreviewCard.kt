package com.livingtechusa.reflexion.util.scopedStorageUtils

import android.graphics.Bitmap
import android.text.format.Formatter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.customLists.CustomListEvent
import com.livingtechusa.reflexion.util.Constants.VIDEO_URI
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage


@Composable
fun MediaFilePreviewCard(resource: FileResource) {
    val context = LocalContext.current
    val formattedFileSize = Formatter.formatShortFileSize(context, resource.size)
    val fileMetadata = "${resource.mimeType} - $formattedFileSize"

    Card(
        elevation = 0.dp,
        border = BorderStroke(width = 1.dp, color = compositeBorderColor()),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column {
            CoilImage(
                imageModel = { resource.uri }, // loading a network image or local resource using an URL.
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = resource.filename, style = MaterialTheme.typography.subtitle2)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = fileMetadata, style = MaterialTheme.typography.caption)
                Spacer(modifier = Modifier.height(12.dp))
                resource.path?.let { Text(text = it, style = MaterialTheme.typography.caption) }
            }
        }
    }
}

@Composable
fun DocumentFilePreviewCard(resource: FileResource, navController: NavHostController, docType: String) {
    val context = LocalContext.current
    val formattedFileSize = Formatter.formatShortFileSize(context, resource.size)

     // modify to handle play for both both saved and linked video... to delete on long or double tap...

    val URI = "Uri"
    val route: String =
        Screen.VideoView.route + "/" + URI
    val fileMetadata = "${resource.mimeType} - $formattedFileSize"

    val thumbnail by produceState<Bitmap?>(null, resource.uri) {
        value = SafeUtils.getThumbnail(context, resource.uri)
    }


    Card(
        elevation = 0.dp,
        border = BorderStroke(width = 1.dp, color = compositeBorderColor()),
        modifier = Modifier
            .pointerInput(key1 = resource) {
                detectTapGestures(
                    onLongPress  = {
                        // Launch dialog
                        navController.navigate(Screen.ConfirmDeleteSubItemScreen.route + "/" + docType)
                    },
//                    onDoubleTap = {
//                        viewModel.onTriggerEvent(
//                            CustomListEvent.MoveToEdit(
//                                index
//                            )
//                        )
//                    },
                    onTap = {
                        Screen.VideoView.route + "/" + URI
                        navController.navigate(route)
                    }
                )
            }
            .padding(16.dp)
            .fillMaxWidth()
            .clickable {
                Screen.VideoView.route + "/" + URI
                navController.navigate(route)
            }
    ) {
        Column {
            thumbnail?.let { Image(bitmap = it.asImageBitmap(), contentDescription = null) }

//            Column(modifier = Modifier.padding(16.dp)) {
//                Text(text = resource.filename, style = MaterialTheme.typography.subtitle2)
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = fileMetadata, style = MaterialTheme.typography.caption)
//                Spacer(modifier = Modifier.height(12.dp))
//                resource.path?.let { Text(text = it, style = MaterialTheme.typography.caption) }
//            }
        }
    }
}
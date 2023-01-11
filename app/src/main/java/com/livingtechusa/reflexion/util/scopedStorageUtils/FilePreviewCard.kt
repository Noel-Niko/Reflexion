package com.livingtechusa.reflexion.util.scopedStorageUtils

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.text.format.Formatter
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import java.lang.Exception


    val previewHeight = 210.dp
    val previewWidth = 140.dp

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
   // val formattedFileSize = Formatter.formatShortFileSize(context, resource.size)

     // modify to handle play for both both saved and linked video... to delete on long or double tap...

    val URI = "Uri"
    val route: String =
        Screen.VideoView.route + "/" + URI
    //val fileMetadata = "${resource.mimeType} - $formattedFileSize"

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
            .padding(0.dp)
            .fillMaxWidth()
//            .clickable {
//                Screen.VideoView.route + "/" + URI
//                navController.navigate(route)
//            }
    ) {
        Column {
            val colorStops: Array<out Pair<Float, Color>> =
                arrayOf(Pair(10f, Color.Black), Pair(5f, Color.Red))
            thumbnail?.let { Image(
                modifier = Modifier.height(previewHeight).width(previewWidth)
                    .border(
                        1.dp, Brush.verticalGradient(colorStops = colorStops),
                        TextFieldDefaults.OutlinedTextFieldShape
                    ).padding(2.dp),
                contentScale = ContentScale.Crop,
                bitmap = it.asImageBitmap(), contentDescription = null) }

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

@Composable
fun videoImagePreviewCard(urlString: String?, navController: NavHostController, docType: String) {
    val context = LocalContext.current
val resource = ResourceProviderSingleton
    // get url thumbnail...
    // for Youtube
    var imageUrl = urlString
    try {
        if (imageUrl?.contains("youtu") == true) {
            val urlArray = imageUrl.split("//", "/").map { it.trim() }
            imageUrl =
                resource.getString(R.string.youtube_image_prepend) + urlArray[2] + resource.getString(R.string.youtube_image_postpend)
        } else {
            Toast.makeText(context, resource.getString(R.string.unable_to_obtain_youtube_link_preview), Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, stringResource(R.string.unable_to_preview_non_youtube_videos), Toast.LENGTH_SHORT).show()
    }

    val painter = rememberImagePainter(data = imageUrl)

    Card(
        elevation = 0.dp,
        border = BorderStroke(width = 1.dp, color = compositeBorderColor()),
        modifier = Modifier
            .pointerInput(key1 = urlString) {
                detectTapGestures(
                    onLongPress = {
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
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(urlString)
                        )
                        ContextCompat.startActivity(context, intent, null)
                    }
                )
            }
            .padding(0.dp)
            .fillMaxWidth()
            .background(Color.Transparent)
//            .clickable {
//                val intent = Intent(
//                    Intent.ACTION_VIEW,
//                    Uri.parse(reflexionItem.videoUrl)
//                )
//                ContextCompat.startActivity(context, intent, null)
//            }
    ) {
        Column {
            val colorStops: Array<out Pair<Float, Color>> =
                arrayOf(Pair(10f, Color.Black), Pair(5f, Color.Red))
            Image(
                painter = painter,
                contentDescription = "Linked Video Preview Image",
                modifier = Modifier.height(previewHeight).width(previewWidth)
                    .border(
                        1.dp, Brush.verticalGradient(colorStops = colorStops),
                        TextFieldDefaults.OutlinedTextFieldShape
                    )
                    .background(Color.Transparent),
                contentScale = ContentScale.Crop
            )
        }
    }
}













//
//    // val formattedFileSize = Formatter.formatShortFileSize(context, resource.size)
//
//    // modify to handle play for both both saved and linked video... to delete on long or double tap...
//
//    val URI = "Uri"
//    val route: String =
//        Screen.VideoView.route + "/" + URI
//    //val fileMetadata = "${resource.mimeType} - $formattedFileSize"
//
//    val thumbnail by produceState<Bitmap?>(null, resource.uri) {
//        value = SafeUtils.getThumbnail(context, resource.uri)
//    }
//
//
//    Card(
//        elevation = 0.dp,
//        border = BorderStroke(width = 1.dp, color = compositeBorderColor()),
//        modifier = Modifier
//            .pointerInput(key1 = resource) {
//                detectTapGestures(
//                    onLongPress  = {
//                        // Launch dialog
//                        navController.navigate(Screen.ConfirmDeleteSubItemScreen.route + "/" + docType)
//                    },
////                    onDoubleTap = {
////                        viewModel.onTriggerEvent(
////                            CustomListEvent.MoveToEdit(
////                                index
////                            )
////                        )
////                    },
//                    onTap = {
//                        Screen.VideoView.route + "/" + URI
//                        navController.navigate(route)
//                    }
//                )
//            }
//            .padding(16.dp)
//            .fillMaxWidth()
//            .clickable {
//                Screen.VideoView.route + "/" + URI
//                navController.navigate(route)
//            }
//    ) {
//        Column {
//            val colorStops: Array<out Pair<Float, Color>> =
//                arrayOf(Pair(10f, Color.Black), Pair(5f, Color.Red))
//            Image(
//                modifier = Modifier.height(90.dp).width(60.dp)
//                    .border(
//                        1.dp, Brush.verticalGradient(colorStops = colorStops),
//                        TextFieldDefaults.OutlinedTextFieldShape
//                    ).padding(2.dp),
//                contentScale = ContentScale.FillBounds,
//                bitmap = it.asImageBitmap(), contentDescription = null)
//
////            Column(modifier = Modifier.padding(16.dp)) {
////                Text(text = resource.filename, style = MaterialTheme.typography.subtitle2)
////                Spacer(modifier = Modifier.height(4.dp))
////                Text(text = fileMetadata, style = MaterialTheme.typography.caption)
////                Spacer(modifier = Modifier.height(12.dp))
////                resource.path?.let { Text(text = it, style = MaterialTheme.typography.caption) }
////            }
//        }
//    }
//}
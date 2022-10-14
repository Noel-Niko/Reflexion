//package com.livingtechusa.gotjokes.ui.display
//
//import android.content.res.Configuration
//import android.net.Uri
//import android.util.Log
//import android.widget.Toast
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.gestures.Orientation
//import androidx.compose.foundation.gestures.draggable
//import androidx.compose.foundation.gestures.rememberDraggableState
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material.Button
//import androidx.compose.material.CircularProgressIndicator
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.unit.IntOffset
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.livingtechusa.gotjokes.R
//import com.livingtechusa.gotjokes.ui.build.BuildEvent
//import com.livingtechusa.gotjokes.ui.build.BuildViewModel
//import com.livingtechusa.gotjokes.ui.components.DisplayImgCard
//import com.livingtechusa.gotjokes.util.TakeScreenShot
//import com.livingtechusa.gotjokes.util.findActivity
//import kotlin.math.roundToInt
//
//@Composable
//fun DisplayScreen() {
//    //    val result = remember { mutableStateOf<Bitmap?>(null) }
//    //        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
//    //            result.value = it
//    //        }
//    //
//    ////        Button(onClick = { launcher.launch() }) {
//    ////            Text(text = "Take a picture")
//    ////        }
//    //
//    //        result.value?.let { image ->
//    //            Image(image.asImageBitmap(), null, modifier = Modifier.fillMaxWidth())
//    //        }
//    //
//    //    AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT
//    //AccessibilityService.ScreenshotResult()\
//
//    val activity = findActivity()
//    TakeScreenShot.verifyStoragePermission(
//        activity
//    )
//
//    val configuration = LocalConfiguration.current
//    val height = configuration.screenHeightDp
//    val width = configuration.screenWidthDp
//    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//        DisplayScreenLandscape()
//    } else {
//        val buildViewModel: BuildViewModel = viewModel(BuildViewModel::class.java)
//        val caption by buildViewModel.caption.collectAsState()
//        val image by buildViewModel.imageUrl.collectAsState()
//        val textColor by buildViewModel.color.collectAsState()
//
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//        ) {
//            if (image == null) {
//                item {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(25.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        CircularProgressIndicator()
//                    }
//                }
//            } else {
//                item {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(8.dp)
//                    ) {
//                        Spacer(
//                            Modifier.height((height/5).dp)
//                            )
//                        if (image != null) {
//                            DisplayImgCard(url = image!!)
//                        }
//                        Spacer(modifier = Modifier.height(8.dp))
//                        var offsetY by remember { mutableStateOf(0f) }
//                        Text(
//                            modifier = Modifier
//                                .padding(28.dp, 0.dp, 28.dp, 0.dp)
//                                .align(Alignment.CenterHorizontally)
//                                .offset { IntOffset(0, offsetY.roundToInt()) }
//                                .draggable(
//                                    orientation = Orientation.Vertical,
//                                    state = rememberDraggableState { delta ->
//                                        offsetY += delta
//                                    }
//                                )
//                                .clickable {
//                                    buildViewModel.onTriggerEvent(BuildEvent.UpdateColor)
//                                },
//                            text = caption,
//                            fontSize = 20.sp,
//                            color = textColor
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//
//                        Button(
//                            modifier = Modifier.align(Alignment.CenterHorizontally),
//                            onClick = {
//                                try {
//
//                                    val imageUri = TakeScreenShot.takeScreenShot(
//                                        activity.window.decorView, caption, (height*2).dp, width.dp
//                                    )
//                                    val uri: Uri = imageUri
//
//                                    buildViewModel.onTriggerEvent(BuildEvent.Save(uri))
//                                    Toast.makeText(activity, "Saved", Toast.LENGTH_SHORT).show()
//                                } catch (e: Exception) {
//                                    val TAG = "ScreenShot"
//                                    Log.e(TAG, "Error message: " + e.message + " with cause " + e.cause)
//                                    Toast.makeText(activity, "Unable to save. \n Error: " + e.cause, Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                        ) {
//                            Text(stringResource(R.string.save))
//                        }
//                    }
//                }
//            }
//        }
//    }
//
////    var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }
////
////    val context = LocalContext.current
////    var view = LocalView.current
////
////    Column(
////        modifier = Modifier
////            .padding(top = 8.dp)
////            .height(390.dp)
////            .width(300.dp)
////            .onGloballyPositioned {
////                capturingViewBounds = it.boundsInRoot()
////            },
////        horizontalAlignment = Alignment.CenterHorizontally
////    ) {
////        val bounds = capturingViewBounds
////        var bitmap = Bitmap.createBitmap(
////            bounds?.width?.roundToInt()!!, bounds.height.roundToInt(),
////            Bitmap.Config.ARGB_8888
////        ).applyCanvas {
////            translate(-bounds.left, -bounds.top)
////            view.draw(this)
////        }
////
////    }
//}
//package com.livingtechusa.gotjokes.ui.display
//
//import android.content.res.Configuration
//import android.net.Uri
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.layout.wrapContentWidth
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.Button
//import androidx.compose.material.CircularProgressIndicator
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.livingtechusa.gotjokes.R
//import com.livingtechusa.gotjokes.ui.build.BuildEvent
//import com.livingtechusa.gotjokes.ui.build.BuildViewModel
//import com.livingtechusa.gotjokes.ui.components.DisplayImgCard
//import com.livingtechusa.gotjokes.util.TakeScreenShot
//import com.livingtechusa.gotjokes.util.findActivity
//
//@Composable
//fun DisplayScreenLandscape() {
//    val activity = findActivity()
//    TakeScreenShot.verifyStoragePermission(
//        activity
//    )
//    val configuration = LocalConfiguration.current
//    if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
//        DisplayScreen()
//    } else {
//        val buildViewModel: BuildViewModel = viewModel(BuildViewModel::class.java)
//        val caption by buildViewModel.caption.collectAsState()
//        val image by buildViewModel.imageUrl.collectAsState()
//        val scrollState = rememberScrollState()
//        val height = configuration.screenHeightDp
//        val width = configuration.screenWidthDp
//        LazyRow(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(8.dp)
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
//                    Row(
//                        modifier = Modifier.padding(8.dp)
//                    ) {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxSize()
//                        ) {
//                            //  Spacer(Modifier.height(16.dp))
//                            if (image != null) {
//                                DisplayImgCard(url = image!!)
//                            }
//                        }
//                        Column(
//                            modifier = Modifier
//                                .verticalScroll(scrollState)
//                                .align(alignment = Alignment.CenterVertically)
//
//                        ) {
//                            Text(
//                                modifier = Modifier
//                                    .width(height.dp)
//                                    .wrapContentWidth(),
//                                text = caption
//                            )
//                            Spacer(modifier = Modifier.height(16.dp))
//                            Button(
//                                onClick = {
//                                    val imageUri = TakeScreenShot.takeScreenShot(
//                                        activity.window.decorView.rootView, caption,
//                                        (height*2).dp, width.dp
//                                    )
//                                   val uri: Uri = imageUri
//                                    buildViewModel.onTriggerEvent(BuildEvent.Save(uri))
//                                },
//                                modifier = Modifier
//                                    .align(alignment = Alignment.CenterHorizontally)
//                            ) {
//                                Text(stringResource(R.string.save))
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
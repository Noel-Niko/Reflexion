package com.livingtechusa.reflexion.ui.customListDisplay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowWidthSizeClass
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.ui.customLists.CustomListEvent
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.util.ReflexionArrayItem
import com.livingtechusa.reflexion.util.extensions.findActivity
import kotlinx.coroutines.launch

const val CUSTOM_LIST_DISPLAY = "customListDisplay"

@Composable
fun CustomListDisplayScreen(
    navController: NavHostController,
    windowSize: WindowWidthSizeClass,
    headNodePk: Long
) {

    val context = LocalContext.current
    if (context.findActivity() != null) {
        when (windowSize) {
            WindowWidthSizeClass.COMPACT -> {
                CompactScreen(navController, headNodePk)
            }

            WindowWidthSizeClass.MEDIUM -> {
                Landscape(navController, headNodePk)
            }

//            WindowWidthSizeClass.EXPANDED -> {
//                ExpandedScreen(navHostController, icons)
//                viewModel.navigationType = ReflexionNavigationType.PERMANENT_NAVIGATION_DRAWER
//            }

            else -> CompactScreen(navController, headNodePk = headNodePk)
        }
    }
}

@Composable
fun CustomListDisplayContent(
    paddingValues: PaddingValues?,
    viewModel: CustomListsViewModel = hiltViewModel()
) {

    val selectedList by viewModel.customList.collectAsState()
    val children by viewModel.children.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues ?: PaddingValues(8.dp))
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            /* TITLE */
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .align(Alignment.CenterVertically)
                ) {
                    androidx.compose.material3.Text(text = stringResource(R.string.title))
                }
                Column(
                    Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        color = Color.Black,
                        style = MaterialTheme.typography.h6,
                        text = selectedList.itemName.toString(),
                    )
                }
            }

        }
        Spacer(Modifier.height(16.dp))

        // Row of Cards
        LazyRow(
            modifier = Modifier
                .padding(4.dp).fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(children.size) { childItemIndex ->
                Card(
                    modifier = Modifier
                        .fillParentMaxSize()
                        .padding(4.dp),
                    elevation = 10.dp,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    /* TITLE */
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                color = Color.Black,
                                style = MaterialTheme.typography.h6,
                                text = children[childItemIndex].name,
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        /* DESCRIPTION */
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth()
                        ) {
                            Column(
                                Modifier

                            ) {
                                androidx.compose.material3.Text(text = stringResource(R.string.description))
                            }
                            Column(
                                Modifier
                                    .weight(1f)

                            ) {
                                Text(
                                    color = Color.Black,
                                    style = MaterialTheme.typography.h6,
                                    text = children[childItemIndex].description.toString(),
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        /* DETAILS */
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth()
                        ) {
                            Column(
                                Modifier

                            ) {
                                androidx.compose.material3.Text(text = stringResource(R.string.detailedDescription))
                            }
                            Column(
                                Modifier
                                    .weight(1f)

                            ) {
                                Text(
                                    color = Color.Black,
                                    style = MaterialTheme.typography.h6,
                                    text = children[childItemIndex].detailedDescription.toString(),
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

                            ) {
                                androidx.compose.material3.Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
//                                    if (reflexionItem.videoUri.isNullOrEmpty()) {
//                                        Toast
//                                            .makeText(
//                                                context,
//                                                resource.getString(R.string.is_saved),
//                                                Toast.LENGTH_SHORT
//                                            )
//                                            .show()
//                                    } else {
//                                        val route: String = Screen.VideoView.route + URI
//                                        navHostController.navigate(route)
//                                    }
                                        },
                                    text = AnnotatedString(stringResource(R.string.saved_video)),
                                    color = Color.Blue

                                )
                            }


                            /* VIDEO URL */
                            Column(
                                Modifier
                                    .weight(1f)

                            ) {
                                androidx.compose.material3.Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            onClick = {
//                                        if (reflexionItem.videoUrl == Constants.EMPTY_STRING) {
//                                            navHostController.navigate(Screen.PasteAndSaveScreen.route)
//                                        } else {
//                                            val route: String =
//                                                Screen.VideoView.route + URL
//                                            navHostController.navigate(route)
//                                        }
                                            },
                                        ),
                                    text = AnnotatedString(stringResource(R.string.video_link)),
                                    color = Color.Blue
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

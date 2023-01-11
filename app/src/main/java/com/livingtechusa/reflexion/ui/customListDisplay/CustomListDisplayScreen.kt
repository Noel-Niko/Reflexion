package com.livingtechusa.reflexion.ui.customListDisplay

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
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
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.ui.components.ImageCard
import com.livingtechusa.reflexion.ui.customLists.CustomListEvent
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.util.Constants
import com.livingtechusa.reflexion.util.ReflexionArrayItem
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
import com.livingtechusa.reflexion.util.extensions.findActivity
import kotlinx.coroutines.launch

const val CUSTOM_LIST_DISPLAY = "customListDisplay"

@Composable
fun CustomListDisplayScreen(
    viewModel: CustomListsViewModel,
    navController: NavHostController,
    windowSize: WindowWidthSizeClass,
    headNodePk: Long
) {

    val context = LocalContext.current
    if (context.findActivity() != null) {
        when (windowSize) {
            WindowWidthSizeClass.COMPACT -> {
                CompactScreen(navController = navController, headNodePk =headNodePk, viewModel = viewModel)
            }

            WindowWidthSizeClass.MEDIUM -> {
                Landscape(navController = navController, headNodePk =headNodePk, viewModel = viewModel)
            }

//            WindowWidthSizeClass.EXPANDED -> {
//                ExpandedScreen(navHostController, icons)
//                viewModel.navigationType = ReflexionNavigationType.PERMANENT_NAVIGATION_DRAWER
//            }

            else -> CompactScreen(navController = navController, headNodePk = headNodePk, viewModel = viewModel)
        }
    }
}

@Composable
fun CustomListDisplayContent(
    paddingValues: PaddingValues?,
    viewModel: CustomListsViewModel,
    navController: NavHostController
) {

    val selectedList by viewModel.customList.collectAsState()
    val children by viewModel.children.collectAsState()
    val childImageList by viewModel.childListImages.collectAsState()
    val context = LocalContext.current
    val resource = ResourceProviderSingleton
    val colorStops: Array<out Pair<Float, Color>> =
        arrayOf(Pair(10f, Color.Black), Pair(5f, Color.Red))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues ?: PaddingValues(8.dp))
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            /* TITLE */
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
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
                .padding(4.dp)
                .fillMaxSize(),
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
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                    ) {
                        /* IMAGE */
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
                                            1.dp, Brush.verticalGradient(colorStops = colorStops),
                                            TextFieldDefaults.OutlinedTextFieldShape
                                        )
                                        .align(Alignment.End)
                                ) {
                                    if (childImageList.isNullOrEmpty().not()) {
                                        ImageCard(childImageList[childItemIndex], navController)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        /* TITLE */
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
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()
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
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()
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
                                            if (children[childItemIndex].videoUri.isNullOrEmpty()) {
                                                Toast
                                                    .makeText(
                                                        context,
                                                        resource.getString(R.string.is_saved),
                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            } else {
                                                val route: String =
                                                    Screen.VideoView.route + "/" + children[childItemIndex].videoUri
                                                navController.navigate(route)
                                            }
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
                                                if (children[childItemIndex].videoUrl == Constants.EMPTY_STRING) {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            resource.getString(R.string.did_you_add_a_video_link),
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                } else {
                                                    val route: String =
                                                        Screen.VideoView.route + "/" + children[childItemIndex].videoUrl
                                                    navController.navigate(route)
                                                }
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

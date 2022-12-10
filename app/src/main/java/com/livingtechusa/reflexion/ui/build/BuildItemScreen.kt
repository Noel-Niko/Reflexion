package com.livingtechusa.reflexion.ui.build

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SendToMobile
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.window.core.layout.WindowWidthSizeClass
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.navigation.BarItem
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.navigation.ReflexionNavigationType
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.Constants.EMPTY_ITEM
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import com.livingtechusa.reflexion.util.Constants.SEARCH_YOUTUBE
import com.livingtechusa.reflexion.util.Constants.VIDEO
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
import com.livingtechusa.reflexion.util.Temporary
import com.livingtechusa.reflexion.util.extensions.findActivity
import com.livingtechusa.reflexion.ui.components.icons.BuildIcons
import com.livingtechusa.reflexion.ui.theme.ReflexionItemsColors
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

const val BuildRoute = "build"

@Composable
fun BuildItemScreen(
    navHostController: NavHostController,
    windowSize: WindowWidthSizeClass,
    viewModel: ItemViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val icons = NavBarItems.BuildBarItems
    if (context.findActivity() != null) {
        when (windowSize) {
            WindowWidthSizeClass.COMPACT -> {
                CompactScreen(navHostController, icons, viewModel)
            }

            WindowWidthSizeClass.MEDIUM -> {
                MediumScreen(navHostController, icons, viewModel)
            }

//            WindowWidthSizeClass.EXPANDED -> {
//                ExpandedScreen(navHostController, icons, viewModel)
//                viewModel.navigationType = ReflexionNavigationType.PERMANENT_NAVIGATION_DRAWER
//            }

            else -> CompactScreen(navHostController, icons, viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildContent(
    navHostController: NavHostController, viewModel: ItemViewModel, paddingValues: PaddingValues
) {
    val URI = "/Uri"
    val URL = "/Url"
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()

    val itemViewModel: ItemViewModel = viewModel
    val scope = rememberCoroutineScope()
    val savedReflexionItem by itemViewModel.reflexionItem.collectAsState()
    val saveNow by itemViewModel.saveNow.collectAsState()
    val resource = ResourceProviderSingleton
    val reflexionItem = // remember??? TODO
        ReflexionItem(
            autogenPK = savedReflexionItem.autogenPK,
            name = savedReflexionItem.name,
            description = savedReflexionItem.description,
            detailedDescription = savedReflexionItem.detailedDescription,
            image = savedReflexionItem.image,
            videoUri = savedReflexionItem.videoUri,
            videoUrl = savedReflexionItem.videoUrl,
            parent = savedReflexionItem.parent
        )

    var targetVideoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val selectVideo =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),
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

    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if(saveNow) {
            Toast.makeText(
                context, resource.getString(R.string.changes_saved), Toast.LENGTH_SHORT
            ).show()
            if (savedReflexionItem.autogenPK != 0L) {
                reflexionItem.autogenPK = savedReflexionItem.autogenPK
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
        Scaffold(floatingActionButton = {
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
                }, containerColor = MaterialTheme.colorScheme.primary,
                onClick = {
                Toast.makeText(
                    context, resource.getString(R.string.changes_saved), Toast.LENGTH_SHORT
                ).show()
                if (savedReflexionItem.autogenPK != 0L) {
                    reflexionItem.autogenPK = savedReflexionItem.autogenPK
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
                    Spacer(Modifier.height(16.dp))/* TOPIC */
                    if (savedReflexionItem.parent == null) {
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
                                        textColor = Color.Black,
                                        containerColor = Color.Transparent
                                    ),
                                    textStyle = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.fillMaxWidth(),
                                    value = if (reflexionItem.name == EMPTY_ITEM) {
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
                                        textColor = Color.Black,
                                        containerColor = Color.Transparent,
                                    ),
                                    textStyle = MaterialTheme.typography.headlineSmall,
                                    value = if (reflexionItem.name == EMPTY_ITEM) {
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
                                    textColor = Color.Black,
                                    containerColor = Color.Transparent
                                ),
                                textStyle = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth(),
                                value = reflexionItem.description ?: EMPTY_STRING,
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
                                    textColor = Color.Black,
                                    containerColor = Color.Transparent
                                ),
                                textStyle = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth(),
                                value = reflexionItem.detailedDescription ?: EMPTY_STRING,
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
                                        if (reflexionItem.videoUri.isNullOrEmpty()) {
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
                            IconButton(onClick = {
                                selectVideo.launch(VIDEO)
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
                                            if (reflexionItem.videoUrl == EMPTY_STRING) {
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
                            IconButton(onClick = {
                                val query = SEARCH_YOUTUBE + reflexionItem.name
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

                }
            }
        }
    }
}





//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ExpandedScreen(
//    navController: NavHostController, icons: List<BarItem>, viewModel: ItemViewModel
//) {
//    val backStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = backStackEntry?.destination?.route
//    PermanentNavigationDrawer(drawerContent = {
//        icons.forEach { navItem ->
//            NavigationDrawerItem(icon = {
//                androidx.compose.material3.Icon(
//                    imageVector = navItem.image, contentDescription = navItem.title
//                )
//            },
//                label = { androidx.compose.material3.Text(text = navItem.title) },
//                selected = currentRoute == navItem.route,
//                onClick = {
//                    navController.navigate(navItem.route) {
//                        popUpTo(navController.graph.findStartDestination().id) {
//                            saveState = true
//                        }
//                        launchSingleTop = true
//                        restoreState = true
//                    }
//                })
//        }
//    }, content = {
//        val scope = rememberCoroutineScope()
//        val state = rememberScaffoldState()
//
//        Scaffold(scaffoldState = state, topBar = {
//            TopAppBar(title = {
//                Text(text = stringResource(id = R.string.app_name))
//            }, navigationIcon = {
//                Icon(
//                    painter = painterResource(R.drawable.baseline_menu_24),
//                    contentDescription = "Toggle Drawer",
//                    modifier = Modifier.clickable(onClick = {
//                        scope.launch {
//                            if (state.drawerState.isClosed) state.drawerState.open() else state.drawerState.close()
//                        }
//                    })
//                )
//            })
//        }, drawerContent = {
//            Text("Reflexion", modifier = Modifier.padding(16.dp))
//            Divider()
//            drawerNavContent(
//                navController,
//                viewModel,
//                viewModel.reflexionItem.collectAsState().value
//            )
//        },
//            modifier = Modifier.fillMaxSize()
//        ) { paddingValues ->
//
//                BuildContent(navController, viewModel, it)
//        }
//    })
//}


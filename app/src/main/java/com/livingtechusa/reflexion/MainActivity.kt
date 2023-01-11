package com.livingtechusa.reflexion

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.util.Consumer
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.build.BuildItemScreen
import com.livingtechusa.reflexion.ui.components.ConfirmDeleteListDialog
import com.livingtechusa.reflexion.ui.components.ConfirmDeleteSubItemDialog
import com.livingtechusa.reflexion.ui.topics.ListDisplay
import com.livingtechusa.reflexion.ui.components.ConfirmSaveAlertDialog
import com.livingtechusa.reflexion.ui.components.PasteAndSaveDialog
import com.livingtechusa.reflexion.ui.components.VideoPlayer
import com.livingtechusa.reflexion.ui.components.VideoPlayer2CustomList
import com.livingtechusa.reflexion.ui.customListDisplay.CustomListDisplayScreen
import com.livingtechusa.reflexion.ui.customLists.BuildCustomListsScreen
import com.livingtechusa.reflexion.ui.home.HomeScreen
import com.livingtechusa.reflexion.ui.theme.ReflexionTheme
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.BaseApplication
import com.livingtechusa.reflexion.util.Constants.EMPTY_PK
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import com.livingtechusa.reflexion.util.Constants.HEAD_NODE_PK
import com.livingtechusa.reflexion.util.Constants.INDEX
import com.livingtechusa.reflexion.util.Constants.LIST_NAME
import com.livingtechusa.reflexion.util.Constants.REFLEXION_ITEM_PK
import com.livingtechusa.reflexion.util.Constants.SOURCE
import com.livingtechusa.reflexion.util.Constants.SUB_ITEM
import com.livingtechusa.reflexion.util.Constants.URI
import com.livingtechusa.reflexion.util.MediaUtil
import com.livingtechusa.reflexion.util.Temporary
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val Main_Activity = "Main_Activity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var app: BaseApplication

    lateinit var navigationController: NavHostController

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReflexionTheme {
                val permissionsState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    rememberMultiplePermissionsState(
                        permissions = listOf(
                            android.Manifest.permission.RECORD_AUDIO,
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.INTERNET,
                            android.Manifest.permission.BLUETOOTH_CONNECT,
                            android.Manifest.permission.READ_MEDIA_VIDEO,
                        )
                    )
                } else {
                    rememberMultiplePermissionsState(
                        permissions = listOf(
                            android.Manifest.permission.RECORD_AUDIO,
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.INTERNET
                        )
                    )
                }
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(
                    key1 = lifecycleOwner,
                    effect = {
                        val observer = LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_START) {
                                permissionsState.launchMultiplePermissionRequest()
                                MediaUtil().verifyStoragePermission(this@MainActivity)
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)
                        onDispose {
                            lifecycleOwner.lifecycle.removeObserver(observer)
                        }
                    }
                )
                val navController = rememberNavController()
                navigationController = navController
                val windowSize = calculateWindowSizeClass(this)
                NavHost(
                    navController = navController,
                    startDestination = Screen.HomeScreen.route
                ) {

                    composable(
                        route = Screen.HomeScreen.route,
                    ) {
                        HomeScreen(
                            navHostController = navController,
                            windowSize = windowSize
                        )
                    }

                    composable(
                        route = Screen.BuildItemScreen.route + "/{reflexion_item_pk}",
                        arguments = listOf(
                            navArgument(REFLEXION_ITEM_PK) {
                                type = NavType.LongType
                            }
                        )
                    ) { navBackStackEntry ->
                        val parentEntry = remember(navBackStackEntry) {
                            navController.getBackStackEntry(Screen.HomeScreen.route)
                        }
                        val parentViewModel: ItemViewModel = hiltViewModel(parentEntry)
                        BuildItemScreen(
                            pk = navBackStackEntry.arguments?.getLong(REFLEXION_ITEM_PK) ?: EMPTY_PK,
                            navHostController = navController,
                            windowSize = windowSize,
                            viewModel = parentViewModel
                        )
                    }

                    composable(
                        route = Screen.VideoView.route + "/{sourceType}", // "/{required arg}/{required arg} ?not_required_arg = {arg}"
                        arguments = listOf(
                            navArgument(SOURCE) {
                                type = NavType.StringType
                            }
                        )
                    ) { navBackStackEntry ->
                        val parentEntry = remember(navBackStackEntry) {
                            navController.getBackStackEntry(Screen.HomeScreen.route)
                        }
                        val parentViewModel: ItemViewModel = hiltViewModel(parentEntry)
                        VideoPlayer(
                            viewModel =  parentViewModel
                        )
                    }

                    composable(
                        route = Screen.ConfirmSaveScreen.route,
                    ) { navBackStackEntry ->
                        val parentEntry = remember(navBackStackEntry) {
                            navController.getBackStackEntry(Screen.HomeScreen.route)
                        }
                        val parentViewModel: ItemViewModel = hiltViewModel(parentEntry)
                        ConfirmSaveAlertDialog(
                            navController = navController,
                            viewModel = parentViewModel
                        )
                    }

                    composable(
                        route = Screen.PasteAndSaveScreen.route
                    ) { navBackStackEntry ->
                        val parentEntry = remember(navBackStackEntry) {
                            navController.getBackStackEntry(Screen.HomeScreen.route)
                        }
                        val parentViewModel: ItemViewModel = hiltViewModel(parentEntry)
                        PasteAndSaveDialog(
                            viewModel = parentViewModel,
                            navController = navController
                        )
                    }

                    composable(
                        route = Screen.TopicScreen.route + "/{reflexion_item_pk}",
                        arguments = listOf(
                            navArgument(REFLEXION_ITEM_PK) {
                                type = NavType.LongType
                            }
                        )
                    ) { navBackStackEntry ->
                        ListDisplay(
                            navHostController = navController,
                            pk = navBackStackEntry.arguments?.getLong(REFLEXION_ITEM_PK) ?: EMPTY_PK,
                            windowSize = windowSize,
                        )
                    }

                    composable(
                        route = Screen.CustomLists.route,
                    ) {
                        BuildCustomListsScreen(
                            navController = navController,
                            windowSize = windowSize
                        )
                    }

                    composable(
                        route = Screen.ConfirmDeleteListScreen.route+ "/{index}/{listName}",
                        arguments = listOf(
                            navArgument(INDEX) {
                                type = NavType.IntType
                            },
                            navArgument(LIST_NAME) {
                                type = androidx.navigation.NavType.StringType
                            }
                        )
                    ) { navBackStackEntry ->
                        val parentEntry = remember(navBackStackEntry) {
                            navController.getBackStackEntry(Screen.CustomLists.route)
                        }
                        val parentViewModel: CustomListsViewModel = hiltViewModel(parentEntry)
                        ConfirmDeleteListDialog(
                            viewModel = parentViewModel,
                            navController = navController,
                            index = navBackStackEntry.arguments?.getInt(INDEX),
                            itemToDelete = navBackStackEntry.arguments?.getString(LIST_NAME)
                        )
                    }

                    composable(
                        route = Screen.ConfirmDeleteSubItemScreen.route + "/{subItem}",
                        arguments = listOf(
                            navArgument(SUB_ITEM) {
                                type = NavType.StringType
                            }
                        )
                    ) { navBackStackEntry ->
                        val parentEntry = remember(navBackStackEntry) {
                            navController.getBackStackEntry(Screen.HomeScreen.route)
                        }
                        val parentViewModel: ItemViewModel = hiltViewModel(parentEntry)
                        ConfirmDeleteSubItemDialog(
                            viewModel = parentViewModel,
                            navController = navController,
                            subItem = navBackStackEntry.arguments?.getString(SUB_ITEM) ?: EMPTY_STRING
                        )
                    }

                    composable(
                        route = Screen.CustomListDisplay.route + "/{headNodePk}",
                        arguments = listOf(
                            navArgument(HEAD_NODE_PK) {
                                type = NavType.LongType
                            }
                        )
                    ) { navBackStackEntry ->
                        val parentEntry = remember(navBackStackEntry) {
                            navController.getBackStackEntry(Screen.CustomLists.route)
                        }
                        val parentViewModel: CustomListsViewModel = hiltViewModel(parentEntry)
                        CustomListDisplayScreen(
                            viewModel = parentViewModel,
                            navController = navController,
                            headNodePk = navBackStackEntry.arguments?.getLong(HEAD_NODE_PK) ?: -1,
                            windowSize = windowSize
                        )
                    }

                    composable(
                        route = Screen.VideoViewCustomList.route, // + "/{Index}",
//                        arguments = listOf(
//                            navArgument(INDEX) {
//                                type = NavType.IntType
//                            }
//                        )
                    ) { navBackStackEntry ->
                        val parentEntry = remember(navBackStackEntry) {
                            navController.getBackStackEntry(Screen.HomeScreen.route)
                        }
                        val parentViewModel: CustomListsViewModel = hiltViewModel(parentEntry)
                        VideoPlayer2CustomList(
                            index = 0,   //navBackStackEntry.arguments?.getInt(INDEX),
                            viewModel = parentViewModel,
                        )
                    }
                }

                DisposableEffect(key1 = Intent()) {
                    val listener = Consumer<Intent> { intent ->
                        if (intent.clipData?.getItemAt(0)?.text != null && intent.clipData?.getItemAt(
                                0
                            )?.text != EMPTY_STRING
                        ) {
                            val url = intent.clipData?.getItemAt(0)?.text
                            Temporary.url = url.toString()
                            navigationController.navigate(Screen.ConfirmSaveScreen.route)
                        }
                    }
                    addOnNewIntentListener(listener)
                    onDispose { removeOnNewIntentListener(listener) }
                }
            }
        }
    }
}


@Composable
fun calculateWindowSizeClass(activity: Activity): WindowWidthSizeClass {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    return WindowSizeClass.compute(screenWidth.value, screenHeight.value).windowWidthSizeClass
}
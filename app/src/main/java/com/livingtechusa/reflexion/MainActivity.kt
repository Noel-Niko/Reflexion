package com.livingtechusa.reflexion

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.build.BuildItemScreen
import com.livingtechusa.reflexion.ui.components.ConfirmSaveAlertDialog
import com.livingtechusa.reflexion.ui.components.PasteAndSaveDialog
import com.livingtechusa.reflexion.ui.components.VideoPlayer
import com.livingtechusa.reflexion.ui.theme.ReflexionTheme
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.BaseApplication
import com.livingtechusa.reflexion.util.Constants
import com.livingtechusa.reflexion.util.Constants.REFLEXION_ITEM
import com.livingtechusa.reflexion.util.Constants.SOURCE
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
                val permissionsState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        android.Manifest.permission.RECORD_AUDIO,
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.INTERNET
                    )
                )
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
                NavHost(
                    navController = navController,
                    startDestination = Screen.BuildItemScreen.route, // + "/{reflexion_item}"
                ) {

                    composable(
                        route = Screen.BuildItemScreen.route,
                    ) {
                        BuildItemScreen(
                            navHostController = navController,
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
                            navController.getBackStackEntry(Screen.BuildItemScreen.route)
                        }
                        val parentViewModel: ItemViewModel = hiltViewModel(parentEntry)
                        VideoPlayer(
                            navBackStackEntry.arguments?.getString(SOURCE) ?: Constants.URL,
                            parentViewModel,
                            navController = navController
                        )
                    }

                    composable(
                        route = Screen.ConfirmSaveScreen.route
                    ) { navBackStackEntry ->
                        val parentEntry = remember(navBackStackEntry) {
                            navController.getBackStackEntry(Screen.BuildItemScreen.route)
                        }
                        val parentViewModel: ItemViewModel = hiltViewModel(parentEntry)
                        ConfirmSaveAlertDialog(
                            itemViewModel = parentViewModel,
                            navController = navController
                        )
                    }

                    composable(
                        route = Screen.PasteAndSaveScreen.route
                    ) { navBackStackEntry ->
                        val parentEntry = remember(navBackStackEntry) {
                            navController.getBackStackEntry(Screen.BuildItemScreen.route)
                        }
                        val parentViewModel: ItemViewModel = hiltViewModel(parentEntry)
                        PasteAndSaveDialog(
                            itemViewModel = parentViewModel,
                            navController = navController
                        )
                    }

                }

                DisposableEffect(Unit) {
                    val listener = Consumer<Intent> { intent ->
                        if (intent.clipData?.getItemAt(0)?.text != null && intent.clipData?.getItemAt(0)?.text != Constants.EMPTY_STRING
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

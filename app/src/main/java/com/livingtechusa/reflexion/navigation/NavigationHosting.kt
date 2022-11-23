//package com.livingtechusa.reflexion.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navArgument
//import com.livingtechusa.reflexion.ui.build.BuildItemScreen
//import com.livingtechusa.reflexion.ui.components.CONFIRM_SAVE
//import com.livingtechusa.reflexion.ui.components.ConfirmSaveAlertDialog
//import com.livingtechusa.reflexion.ui.components.VideoPlayer
//import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
//import com.livingtechusa.reflexion.util.Constants.URL
//
//const val SOURCE = "sourceType"
//const val REFLEXION_ITEM = "reflexion_item"
//
//@Composable
//fun NavigationHosting(urlText: String?) {
//    val navController = rememberNavController()
//    NavHost(
//        navController = navController,
//        startDestination = Screen.BuildItemScreen.route
//    ) {
//
//        composable(
//            route = Screen.BuildItemScreen.route,
//            arguments = listOf(
//                navArgument(REFLEXION_ITEM) {
//                    type = NavType.BoolType
//                }
//            )
//        ) {navBackStackEntry ->
//            BuildItemScreen(
//                urlToSave = urlText,
//                navHostController = navController,
//                useTempItem = navBackStackEntry.arguments?.getBoolean(REFLEXION_ITEM) ?: false
//            )
//        }
//
//        composable(
//            route = Screen.VideoView.route + "/{sourceType}", // "/{required arg}/{required arg} ?not_required_arg = {arg}"
//            arguments = listOf(
//                navArgument(SOURCE) {
//                    type = NavType.StringType
//                }
//            )
//        ) { navBackStackEntry ->
//            val parentEntry = remember(navBackStackEntry) {
//                navController.getBackStackEntry(Screen.BuildItemScreen.route)
//            }
//            val parentViewModel: ItemViewModel = hiltViewModel(parentEntry)
//            VideoPlayer(
//                navBackStackEntry.arguments?.getString(SOURCE) ?: URL,
//                parentViewModel
//            )
//        }
//
//        composable(
//            route = Screen.ConfirmSaveScreen.route + "/{Url}"
//            ) { navBackStackEntry ->
//            val parentEntry = remember(navBackStackEntry) {
//                navController.getBackStackEntry(Screen.BuildItemScreen.route)
//            }
//            val parentViewModel: ItemViewModel = hiltViewModel(parentEntry)
//            ConfirmSaveAlertDialog(
//                parentViewModel,
//                navController
//            )
//        }
//
//    }
//}
package com.livingtechusa.reflexion.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.livingtechusa.reflexion.ui.build.BuildItemScreen
import com.livingtechusa.reflexion.ui.children.ChildrenScreen

enum class ReflexionScreen(
    val icon: ImageVector,
    val body: @Composable ((String) -> Unit) -> Unit
) {
    Build(
        icon = Icons.Filled.Build,
        body = { BuildItemScreen() }
    ),
    Children(
        icon = Icons.Filled.Build,
        body = { ChildrenScreen() }
    );

    @Composable
    fun content(onScreenChange: (String) -> Unit) {
        body(onScreenChange)
    }

    companion object {
        fun fromRoute(route: String?): ReflexionScreen =
            when(route?.substringBefore("/")) {
                Build.name -> Build
                Children.name -> Children
                null -> Build
                else -> throw IllegalArgumentException("route $route is not recognied.")
            }
    }
}
//
//enum class ReflexionScreen(
//    @StringRes val title: Int,
//    val icon: ImageVector,
//    val body: @Composable ((String) -> Unit) -> Unit
//) {
//    Start(
//        title = R.string.title,
//        icon = Icons.Filled.Build  ,
//        body = {  BuildItemScreen() }
//    ),
//    Build(
//        title = R.string.BuildScreen
//    ),
//    Display(
//        title = R.string.DisplayScreen
//    ),
//    Children(
//        title = R.string.children
//    ),
//    Siblings(
//        title = R.string.siblings
//    )
//}
//
///**
// * Composable that displays the topBar and displays back button if back navigation is possible.
// */
//@Composable
//fun ReflexionAppBar(
//    currentScreen: ReflexionScreen,
//    canNavigateBack: Boolean,
//    navigateUp: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    TopAppBar(
//        title = { Text(stringResource(currentScreen.title)) },
//        modifier = modifier,
//        navigationIcon = {
//            if (canNavigateBack) {
//                IconButton(onClick = navigateUp) {
//                    Icon(
//                        imageVector = Icons.Filled.ArrowBack,
//                        contentDescription = stringResource(R.string.back_button)
//                    )
//                }
//            }
//        }
//    )
//}
//
//@Composable
//fun ReflexionApp(
//    modifier: Modifier = Modifier,
//    viewModel: ItemViewModel = viewModel(),
//    navController: NavHostController = rememberNavController()
//) {
//    //val itemViewModel:ItemViewModel by viewModel()
//    // Get current back stack entry
//    val backStackEntry by navController.currentBackStackEntryAsState()
//    // Get the name of the current screen
//    val currentScreen = ReflexionScreen.valueOf(
//        backStackEntry?.destination?.route ?: ReflexionScreen.Start.name
//    )
//
//    Scaffold(
//        topBar = {
//            ReflexionAppBar(currentScreen = currentScreen,
//                            canNavigateBack = navController.previousBackStackEntry != null,
//                            navigateUp = { navController.navigateUp() })
//        }
//    ) { innerPadding ->
//        val displayedItem by viewModel.displayedItem.collectAsState()
//
//        NavHost(
//            navController = navController,
//            startDestination = ReflexionScreen.Build.name,
//            modifier = modifier.padding(innerPadding)
//        ) {
//            composable(route = ReflexionScreen.Build.name) {
//                BuildItemScreen(viewModel = viewModel, navHostController = navController)
//            }
//            composable(route = ReflexionScreen.Children.name) {
//                ChildrenScreen( viewModel = viewModel, navController)
//            }
//        }
//    }
//
//}
//
////ReflexionTheme() {
////    val allScreens = ReflexionScreen.values().toList()
////    var currentScreen by rememberSaveable { mutableStateOf(ReflexionScreen.Build) }
////
////    Scaffold(
////        topBar = {
////            Row(
////                modifier = Modifier.fillMaxWidth(),
////                horizontalArrangement = Arrangement.Start
////            ) {
////                Column(
////                    modifier = Modifier
////                        .fillMaxWidth()
////                        .align(Alignment.CenterVertically)
////                ) {
////                    ReflexionTabRow(
////                        allScreens = allScreens,
////                        onTabSelected = { screen -> currentScreen = screen },
////                        currentScreen = currentScreen
////                    )
////                }
////            }
////        }
////    ) { innerPadding ->
////        Box(
////            modifier = Modifier.padding(innerPadding)
////        ) {
////            currentScreen.content(
////                onScreenChange = { screen ->
////                    currentScreen = ReflexionScreen.valueOf(screen)
////                }
////            )
////        }
////
////    }
////}
////}

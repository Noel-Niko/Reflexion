package com.livingtechusa.reflexion.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.ui.build.BuildItemScreen
import com.livingtechusa.reflexion.ui.children.ChildrenScreen
import com.livingtechusa.reflexion.ui.components.ReflexionTabRow
import com.livingtechusa.reflexion.ui.theme.ReflexionTheme
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import java.lang.IllegalArgumentException

//enum class ReflexionScreen(
//    val icon: ImageVector,
//    val body: @Composable ((String) -> Unit) -> Unit
//) {
//    Build(
//        icon = Icons.Filled.Build,
//        body = { BuildItemScreen() }
//    ),
//    Children(
//        icon = Icons.Filled.Build,
//        body = { ChildrenScreen() }
//    );
//
//    @Composable
//    fun content(onScreenChange: (String) -> Unit) {
//        body(onScreenChange)
//    }
//
//    companion object {
//        fun fromRoute(route: String?): ReflexionScreen =
//            when(route?.substringBefore("/")) {
//                Build.name -> Build
//                Children.name -> Children
//                null -> Build
//                else -> throw IllegalArgumentException("route $route is not recognied.")
//            }
//    }
//}

enum class ReflexionScreen(@StringRes val title: Int) {
    Start(title = R.string.title),
    Build(title = R.string.BuildScreen),
    Display(title = R.string.DisplayScreen),
    Children(title = R.string.children),
    Siblings(title = R.string.siblings)
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@Composable
fun ReflexionAppBar(
    currentScreen: ReflexionScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun ReflexionApp(
    modifier: Modifier = Modifier,
    viewModel: ItemViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    //val itemViewModel:ItemViewModel by viewModel()
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = ReflexionScreen.valueOf(
        backStackEntry?.destination?.route ?: ReflexionScreen.Start.name
    )

    Scaffold(
        topBar = {
            ReflexionAppBar(currentScreen = currentScreen,
                            canNavigateBack = navController.previousBackStackEntry != null,
                            navigateUp = { navController.navigateUp() })
        }
    ) { innerPadding ->
        val displayedItem by viewModel.displayedItem.collectAsState()

        NavHost(
            navController = navController,
            startDestination = ReflexionScreen.Start.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = ReflexionScreen.Start.name) {
                BuildItemScreen(
                    viewModel = viewModel,
                    navHostController = navController
//                    onNavigateToChildren = { navController.navigate(ReflexionScreen.Children.name) },
//                    onNavigateToChildren = { navController.navigate(ReflexionScreen.Children.name) },
//                    onNavigateToSiblings = { navController.navigate(ReflexionScreen.Children.name) },
//                    onNavigateToParent = { navController.navigate(ReflexionScreen.Children.name) },
                )
            }
            composable(route = ReflexionScreen.Children.name) {
                ChildrenScreen( viewModel = viewModel, navController)
            }
        }
    }

}

//ReflexionTheme() {
//    val allScreens = ReflexionScreen.values().toList()
//    var currentScreen by rememberSaveable { mutableStateOf(ReflexionScreen.Build) }
//
//    Scaffold(
//        topBar = {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.Start
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .align(Alignment.CenterVertically)
//                ) {
//                    ReflexionTabRow(
//                        allScreens = allScreens,
//                        onTabSelected = { screen -> currentScreen = screen },
//                        currentScreen = currentScreen
//                    )
//                }
//            }
//        }
//    ) { innerPadding ->
//        Box(
//            modifier = Modifier.padding(innerPadding)
//        ) {
//            currentScreen.content(
//                onScreenChange = { screen ->
//                    currentScreen = ReflexionScreen.valueOf(screen)
//                }
//            )
//        }
//
//    }
//}
//}

package com.livingtechusa.reflexion.ui.home

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.window.layout.WindowMetricsCalculator
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.extensions.findActivity

const val HOME = "home"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: ItemViewModel = hiltViewModel()
) {
    val context = LocalContext.current
// This method should be run inside a Composable function.
    if(context.findActivity() != null) {
        val widthSizeClass = calculateWindowSizeClass(context.findActivity()!!)
    }

// You can get the height of the current window by invoking heightSizeClass instead.


    Scaffold(
        topBar = { TopAppBar(title = {Text("Bottom Navigation Demo")}) },
        bottomBar = { CompactScreen(navController = navHostController)}

    ) {
        it
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(32.dp))
            Button(onClick = {
                navHostController.navigate(Screen.BuildItemScreen.route)
            }
            ) {
                Text(stringResource(R.string.BuildScreen))
            }
        }

    }
}

@Composable
fun calculateWindowSizeClass(activity: Activity): WindowSizeClass {
    val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(activity)
    return WindowSizeClass.compute(metrics.bounds.toComposeRect().width, metrics.bounds.toComposeRect().height)
}

//@Composable
//fun MyApp(widthSizeClass: WindowWidthSizeClass) {
//    // Select a navigation element based on window size.
//    when (widthSizeClass) {
//        WindowWidthSizeClass.COMPACT -> { CompactScreen() }
////        WindowWidthSizeClass.MEDIUM -> { MediumScreen() }
////        WindowWidthSizeClass.EXPANDED -> { ExpandedScreen() }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactScreen(navController: NavHostController) {
    val icons = NavBarItems.HomeBarItems

    Scaffold(bottomBar = {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        BottomNavigation {
            icons.forEach { navItem ->
                BottomNavigationItem(
                    selected = currentRoute == navItem.route,
                    onClick = {
                        navController.navigate(navItem.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(imageVector = navItem.image,
                        contentDescription = navItem.title)
                    },
                    label = {
                        Text(text = navItem.title) }
                )
            }
        }
    }
    ) {
        it // Other content
    }
}

//@Composable
//fun MediumScreen() {
//    Row(modifier = Modifier.fillMaxSize()) {
//        NavigationRail {
//            icons.forEach { item ->
//                NavigationRailItem(
//                    selected = isSelected,
//                    onClick = { ... },
//                    icon = { ... })
//            }
//        }
//        // Other content
//    }
//}
//
//@Composable
//fun ExpandedScreen() {
//    PermanentNavigationDrawer(
//        drawerContent = {
//            icons.forEach { item ->
//                NavigationDrawerItem(
//                    icon = { ... },
//                    label = { ... },
//                    selected = isSelected,
//                    onClick = { ... }
//                )
//            }
//        },
//        content = {
//            // Other content
//        }
//    )
//}

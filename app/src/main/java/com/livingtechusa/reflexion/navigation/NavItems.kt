package com.livingtechusa.reflexion.navigation

import android.app.Activity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass


@Composable
fun calculateWindowSizeClass(activity: Activity): WindowWidthSizeClass {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    return WindowSizeClass.compute(screenWidth.value, screenHeight.value).windowWidthSizeClass
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactScreen(navController: NavHostController, icons: List<BarItem>) {

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

@Composable
fun MediumScreen(navController: NavHostController, icons: List<BarItem>) {
    val icons = NavBarItems.HomeBarItems
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    Row(modifier = Modifier.fillMaxSize()) {
        NavigationRail {
            icons.forEach { navItem ->
                Spacer(modifier = Modifier.height(32.dp))
                NavigationRailItem(
                    selected = currentRoute == navItem.route,
                    onClick = { navController.navigate(navItem.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    } },
                    icon = { Icon(imageVector = navItem.image,
                        contentDescription = navItem.title) })
            }
        }
        // Other content
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedScreen(navController: NavHostController, icons: List<BarItem>) {
    val icons = NavBarItems.HomeBarItems
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    PermanentNavigationDrawer(
        drawerContent = {
            icons.forEach { navItem ->
                NavigationDrawerItem(
                    icon = { Icon(imageVector = navItem.image,
                        contentDescription = navItem.title)  },
                    label = { Text(text = navItem.title) },
                    selected = currentRoute == navItem.route,
                    onClick = { navController.navigate(navItem.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    } }
                )
            }
        },
        content = {
            // Other content
//            Scaffold(
//                topBar = { TopAppBar(title = {Text("")}) },
//            ) {
//                it
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize(),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                ) {
//                    Spacer(Modifier.height(32.dp))
//                    Button(onClick = {
//                        navController.navigate(Screen.BuildItemScreen.route)
//                    }
//                    ) {
//                        Text(stringResource(R.string.BuildScreen))
//                    }
//                }
//
//            }
        }
    )
}

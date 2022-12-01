package com.livingtechusa.reflexion.ui.children

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.window.core.layout.WindowWidthSizeClass
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.navigation.BarItem
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.navigation.ReflexionNavigationType
import com.livingtechusa.reflexion.ui.components.ReflexionItemListUI
import com.livingtechusa.reflexion.ui.components.bars.SearchBar
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.extensions.findActivity

const val ListRoute = "list"

@Composable
fun ListDisplay(
    viewModel: ItemViewModel = hiltViewModel(),
    navHostController: NavHostController,
    windowSize: WindowWidthSizeClass,
    pk: Long
) {
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    // If `lifecycleOwner` changes, dispose and reset the effect
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                viewModel.listPK = pk
                viewModel.onTriggerEvent(ListEvent.GetList(pk))
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val context = LocalContext.current
    val icons = NavBarItems.ListBarItems
    val reflexionItemList by viewModel.list.collectAsState()

    val search by viewModel.search.collectAsState()

    if (context.findActivity() != null) {
        when (windowSize) {
            WindowWidthSizeClass.COMPACT -> {
                CompactScreen(navHostController, icons, viewModel, search, reflexionItemList, viewModel::searchEvent)
                viewModel.navigationType = ReflexionNavigationType.BOTTOM_NAVIGATION
            }

//            WindowWidthSizeClass.MEDIUM -> {
//                MediumScreen(navHostController, icons, viewModel, pk)
//                viewModel.navigationType = ReflexionNavigationType.NAVIGATION_RAIL
//            }
//
//            WindowWidthSizeClass.EXPANDED -> {
//                ExpandedScreen(navHostController, icons, viewModel, pk)
//                viewModel.navigationType = ReflexionNavigationType.PERMANENT_NAVIGATION_DRAWER
//            }

            else -> CompactScreen(navHostController, icons, viewModel, search, reflexionItemList, viewModel::searchEvent)
        }
    }
}

@Composable
fun ListContent(
    itemViewModel: ItemViewModel,
    navController: NavHostController,
    reflexionItemList: List<ReflexionItem>
) {
    ReflexionItemListUI(
        reflexionItems = reflexionItemList,
        navController = navController,
        viewModel = itemViewModel
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactScreen(
    navController: NavHostController,
    icons: List<BarItem>,
    viewModel: ItemViewModel,
    search: String?,
    reflexionItemList: List<ReflexionItem>,
    onSearch: (String?) -> Unit
) {
    androidx.compose.material3.Scaffold(
        topBar = {
            SearchBar(search = search, onSearch = onSearch )
        },
        containerColor = Color.LightGray,
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            BottomNavigation(
                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ) {
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
                            Icon(
                                imageVector = navItem.image,
                                contentDescription = navItem.title
                            )
                        },
                        label = {
                            Text(text = navItem.title)
                        }
                    )
                }
            }
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
        ) {
            ListContent(navController = navController, itemViewModel = viewModel, reflexionItemList= reflexionItemList)
        }

    }
}

@Composable
fun MediumScreen(
    navController: NavHostController,
    icons: List<BarItem>,
    viewModel: ItemViewModel
) {
    val icons = NavBarItems.HomeBarItems
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    Row(modifier = Modifier.fillMaxSize()) {
        NavigationRail(
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ) {
            icons.forEach { navItem ->
                Spacer(modifier = Modifier.height(32.dp))
                NavigationRailItem(
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
                        Icon(
                            imageVector = navItem.image,
                            contentDescription = navItem.title
                        )
                    })
            }
        }
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
        ) { paddingValues ->
//            ListContent(
//                navController = navController,
//                itemViewModel = viewModel,
//                reflexionItemList = reflexionItemList
//            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedScreen(
    navController: NavHostController,
    icons: List<BarItem>,
    viewModel: ItemViewModel
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    PermanentNavigationDrawer(
        drawerContent = {
            icons.forEach { navItem ->
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = navItem.image,
                            contentDescription = navItem.title
                        )
                    },
                    label = { Text(text = navItem.title) },
                    selected = currentRoute == navItem.route,
                    onClick = {
                        navController.navigate(navItem.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        content = {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
            ) { paddingValues -> //TODO
//                ListContent(
//                    navController = navController,
//                    itemViewModel = viewModel,
//                    reflexionItemList = reflexionItemList
//                )
            }
        }
    )
}


package com.livingtechusa.reflexion.ui.build

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.BarItem
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import kotlinx.coroutines.launch


@Composable
fun MediumScreen(
    navController: NavHostController, icons: List<BarItem>, viewModel: ItemViewModel
) {
    val icons = NavBarItems.BuildBarItems
    val backStackEntry by navController.currentBackStackEntryAsState()
    val scope = rememberCoroutineScope()
    val state = rememberScaffoldState()

    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        scaffoldState = state,
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                backgroundColor = MaterialTheme.colorScheme.onPrimaryContainer,
                elevation = 4.dp,
                navigationIcon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_menu_24),
                        contentDescription = "Toggle Drawer",
                        modifier = Modifier.clickable(onClick = {
                            scope.launch {
                                if (state.drawerState.isClosed) state.drawerState.open() else state.drawerState.close()
                            }
                        })
                    )
                })
        },
        drawerContent = {
            Text(stringResource(id = R.string.app_name), modifier = Modifier.padding(16.dp))
            Divider()
            drawerNavContent(
                navController,
                viewModel,
                viewModel.reflexionItem.collectAsState().value
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        Scaffold(
            modifier = Modifier.padding(paddingValues)
        ) {
            Column() {
                NavigationRail(
                    modifier = Modifier.fillMaxHeight(),
                    containerColor = MaterialTheme.colorScheme.background,
                ) {
                    icons.forEach { navItem ->
                        Spacer(modifier = Modifier.height(32.dp))
                        NavigationRailItem(selected = currentRoute == navItem.route, onClick = {
                            navController.navigate(navItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
//                                    saveState = true
                                }
                                launchSingleTop = true
//                                restoreState = true
                            }
                        }, icon = {
                            Icon(
                                imageVector = navItem.image, contentDescription = navItem.title
                            )
                        })
                    }
                }
                BuildContent(navController, viewModel, it)
            }
        }

    }
}

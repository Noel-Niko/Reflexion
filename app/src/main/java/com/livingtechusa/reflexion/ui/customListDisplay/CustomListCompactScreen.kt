package com.livingtechusa.reflexion.ui.customListDisplay

import androidx.compose.foundation.layout.Row
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SendToMobile
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.ui.customLists.CustomListEvent
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactScreen(
    navController: NavHostController,
    headNodePk: Long,
    viewModel: CustomListsViewModel = hiltViewModel()
) {
    viewModel.onTriggerEvent(CustomListEvent.GetDisplayList(headNodePk))

    val icons = NavBarItems.CustomListsDisplayBarItems
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.custom_list_display),
                        color = MaterialTheme.colors.onBackground
                    )
                },
                actions = {
                    Row() {
                        IconButton(
                            onClick = {
                                // viewModel.onTriggerEvent(Screen.CustomLists.BluetoothSend)
                            },
                            content = {
                                androidx.compose.material.Icon(
                                    imageVector = Icons.Default.SendToMobile,
                                    contentDescription = "send",
                                    tint = MaterialTheme.colors.onBackground,
                                )
                            },
                        )
                        IconButton(
                            onClick = {
//                                viewModel.onTriggerEvent(Screen.CustomLists.SendText)
                            },
                            content = {
                                androidx.compose.material.Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "send",
                                    tint = MaterialTheme.colors.onBackground,
                                )
                            },
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = 4.dp,
//                navigationIcon = {
//                    androidx.compose.material.Icon(
//                        painter = painterResource(R.drawable.baseline_menu_24),
//                        contentDescription = "Toggle Drawer",
//                        modifier = Modifier.clickable(onClick = {
//                            scope.launch {
//                                if (state.drawerState.isClosed) state.drawerState.open() else state.drawerState.close()
//                            }
//                        }),
//                        tint = MaterialTheme.colors.onBackground
//                    )
//                })
            )
        },
        containerColor = MaterialTheme.colors.background,
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            BottomNavigation(
                backgroundColor = MaterialTheme.colors.background,
            ) {
                icons.forEach { navItem ->
                    BottomNavigationItem(
                        selected = currentRoute == navItem.route,
                        onClick = {
                            navController.navigate(navItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                }
                                launchSingleTop = true
//                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = navItem.image,
                                contentDescription = navItem.title,
                                tint = MaterialTheme.colors.onBackground
                            )
                        },
                        label = {
                            Text(text = navItem.title, color = MaterialTheme.colors.onBackground)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        CustomListDisplayContent(paddingValues = paddingValues)
    }
}


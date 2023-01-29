package com.livingtechusa.reflexion.ui.customListDisplay

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SendToMobile
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.ui.customLists.CustomListEvent
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.util.ResourceProviderSingleton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactScreen(
    navController: NavHostController,
    headNodePk: Long,
    viewModel: CustomListsViewModel
) {
    viewModel.onTriggerEvent(CustomListEvent.GetDisplayList(headNodePk))
    viewModel.onTriggerEvent(CustomListEvent.GetDisplayListImages(headNodePk))
    val context = LocalContext.current
    val resource = ResourceProviderSingleton
    val icons = NavBarItems.CustomListsDisplayBarItems
    val selectedList by viewModel.customList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.custom_list_display),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    Row() {
                        IconButton(
                            onClick = {
                                selectedList.nodePk?.let { nodePk -> CustomListEvent.Bookmark(nodePk = nodePk) }
                                    ?.let { event -> viewModel.onTriggerEvent(event) }
                                Toast.makeText(
                                    context,
                                    resource.getString(R.string.bookmarked),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            content = {
                                androidx.compose.material.Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "bookmark",
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                )
                            },
                        )
                        IconButton(
                            onClick = {
                                // viewModel.onTriggerEvent(Screen.CustomLists.BluetoothSend)
                            },
                            content = {
                                androidx.compose.material.Icon(
                                    imageVector = Icons.Default.SendToMobile,
                                    contentDescription = "send",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            },
                        )
                        IconButton(
                            onClick = {
                                viewModel.onTriggerEvent(Screen.CustomLists.SendText)
                            },
                            content = {
                                androidx.compose.material.Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "send",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                        )
                    }
                },
                backgroundColor = MaterialTheme.colorScheme.surface,
                elevation = 0.dp,
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
        //containerColor = MaterialTheme.colors.background,
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            BottomNavigation(
                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
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
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        },
                        label = {
                            Text(
                                text = navItem.title,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 1
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        CustomListDisplayContent(
            paddingValues = paddingValues,
            navController = navController,
            viewModel = viewModel
        )
    }
}


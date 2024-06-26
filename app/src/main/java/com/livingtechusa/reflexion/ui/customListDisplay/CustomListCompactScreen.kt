package com.livingtechusa.reflexion.ui.customListDisplay

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Send
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.ui.components.animation_utils.LoadingAnimation
import com.livingtechusa.reflexion.ui.customLists.CustomListEvent
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.util.ResourceProviderSingleton

@Composable
fun CustomListDisplayCompactScreen(
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
    val loading by viewModel.loading.collectAsState()

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
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                        )
                        IconButton(
                            onClick = { viewModel.onTriggerEvent(CustomListEvent.SendFile) },
                            content = {
                                androidx.compose.material.Icon(
                                    imageVector = Icons.Default.FileUpload,
                                    contentDescription = "send reflexion item file",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                        )
                        IconButton(
                            onClick = {
                                viewModel.onTriggerEvent(CustomListEvent.SendText)
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
            )
        },
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            BottomNavigation(
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                icons.forEach { navItem ->
                    BottomNavigationItem(
                        selected = currentRoute == navItem.route,
                        onClick = {
                            navController.navigate(navItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                }
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = navItem.image,
                                contentDescription = navItem.title,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        },
                        label = {
                            Text(
                                text = navItem.title,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                maxLines = 1
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        if (loading) {
            LoadingAnimation(paddingValues = paddingValues)
        } else {
            CustomListDisplayContent(
                paddingValues = paddingValues,
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}


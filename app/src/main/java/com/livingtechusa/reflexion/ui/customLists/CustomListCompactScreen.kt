package com.livingtechusa.reflexion.ui.customLists

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.BarItem
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.util.ResourceProviderSingleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomListCompactScreen(
    navController: NavHostController,
    icons: List<BarItem>,
    viewModel: CustomListsViewModel,
) {
    val scope = rememberCoroutineScope()
    val state = rememberScaffoldState()
    val context = LocalContext.current
    val resource = ResourceProviderSingleton
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text =  stringResource(R.string.lists),
                        color = MaterialTheme.colors.onBackground
                    )
                },
                actions = {
                    Row() {
                        IconButton(
                            onClick = {
                                      viewModel.onTriggerEvent(CustomListEvent.Save)
                            },
                            content = {
                                androidx.compose.material.Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = "save",
                                    tint = MaterialTheme.colors.onBackground,
                                )
                            },
                        )
                        IconButton(
                            onClick = {
                                viewModel.onTriggerEvent(CustomListEvent.ReSet)
                            },
                            content = {
                                androidx.compose.material.Icon(
                                    imageVector = Icons.Default.Autorenew,
                                    contentDescription = "reset list",
                                    tint = MaterialTheme.colors.onBackground,
                                )
                            },
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = 4.dp,
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
                               // popUpTo(navController.graph.findStartDestination().id) {}
                                launchSingleTop = true
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
    ) { paddingValue ->
            CustomListsContent(navController = navController, viewModel = viewModel, paddingValues = paddingValue)
    }
}
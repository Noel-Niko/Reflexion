package com.livingtechusa.reflexion.ui.customLists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.BarItem
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomListCompactScreen(
    navController: NavHostController,
    icons: List<BarItem>,
    viewModel: CustomListsViewModel,
) {
    val scope = rememberCoroutineScope()
    val state = rememberScaffoldState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.lists),
                        color = MaterialTheme.colorScheme.onSurface
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
                                    tint = MaterialTheme.colorScheme.onSurface,
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
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                        )
                    }
                },
                backgroundColor = MaterialTheme.colorScheme.surface,
                elevation = 6.dp
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
    ) { paddingValue ->
        CustomListsContent(
            navController = navController,
            viewModel = viewModel,
            paddingValues = paddingValue
        )
    }
}
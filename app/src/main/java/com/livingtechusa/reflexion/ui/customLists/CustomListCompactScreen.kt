package com.livingtechusa.reflexion.ui.customLists

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FormatClear
import androidx.compose.material.icons.filled.LayersClear
import androidx.compose.material.icons.filled.NewLabel
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhonelinkErase
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SendToMobile
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.ListNode
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.navigation.BarItem
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.ui.components.bars.MainTopBar
import com.livingtechusa.reflexion.ui.components.bars.SearchBar
import com.livingtechusa.reflexion.ui.list.ListContent
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
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
                            onClick = {},//viewModel.onTriggerEvent(BuildEvent.SendText),
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
                navigationIcon = {
                    androidx.compose.material.Icon(
                        painter = painterResource(R.drawable.baseline_menu_24),
                        contentDescription = "Toggle Drawer",
                        modifier = Modifier.clickable(onClick = {
                            scope.launch {
                                if (state.drawerState.isClosed) state.drawerState.open() else state.drawerState.close()
                            }
                        }),
                        tint = MaterialTheme.colors.onBackground
                    )
                })
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
//                                    saveState = true
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
    ) { paddingValue ->
            CustomListsContent(navController = navController, viewModel = viewModel, paddingValues = paddingValue)
    }
}
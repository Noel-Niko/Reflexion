package com.livingtechusa.reflexion.ui.build

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SendToMobile
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.BarItem
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactScreen(
    pk: Long, navController: NavHostController, icons: List<BarItem>, viewModel: ItemViewModel
) {
    val scope = rememberCoroutineScope()
    val state = rememberScaffoldState()
    val context = LocalContext.current
    val resource = ResourceProviderSingleton

    Scaffold(
        scaffoldState = state,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        color = MaterialTheme.colors.onBackground
                    )
                },
                actions = {
                    Row() {
                        IconButton(
                            onClick = {
                                viewModel.onTriggerEvent(BuildEvent.BluetoothSend)
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.SendToMobile,
                                    contentDescription = "send",
                                    tint = MaterialTheme.colors.onBackground,
                                )
                            },
                        )
                        IconButton(
                            onClick = { viewModel.onTriggerEvent(BuildEvent.SendText) },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "send",
                                    tint = MaterialTheme.colors.onBackground,
                                )
                            },
                        )
                        IconButton(
                            onClick = {
                                Toast.makeText(
                                    context,
                                    resource.getString(R.string.changes_saved),
                                    Toast.LENGTH_SHORT
                                ).show()
                                viewModel.onTriggerEvent(BuildEvent.SaveFromTopBar)
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = "save",
                                    tint = MaterialTheme.colors.onBackground
                                )
                            },
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = 4.dp,
                navigationIcon = {
                    Icon(
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
        drawerContent = {
            Text("Reflexion", modifier = Modifier.padding(16.dp))
            Divider()
            drawerNavContent(
                navController,
                viewModel,
                viewModel.reflexionItem.collectAsState().value,
                state
            )
        },
        drawerElevation = 4.dp,
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            BottomNavigation(
                backgroundColor = MaterialTheme.colors.background,
            ) {
                icons.forEach { navItem ->
                    BottomNavigationItem(selected = currentRoute == navItem.route, onClick = {
                        navController.navigate(navItem.route) {}
                    }, icon = {
                        Icon(
                            imageVector = navItem.image,
                            contentDescription = navItem.title,
                            tint = MaterialTheme.colors.onBackground
                        )
                    }, label = {
                        Text(text = navItem.title, color = MaterialTheme.colors.onBackground)
                    })
                }
            }
        }) { paddingValues ->
        BuildItemContent(pk, navController, viewModel, paddingValues)
    }
}

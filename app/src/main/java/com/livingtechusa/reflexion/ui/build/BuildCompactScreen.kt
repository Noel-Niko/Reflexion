package com.livingtechusa.reflexion.ui.build

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SendToMobile
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.navigation.BarItem
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.Constants
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
import com.livingtechusa.reflexion.util.Temporary
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactScreen(
    pk: Long, navController: NavHostController, icons: List<BarItem>, viewModel: ItemViewModel
) {
    val scope = rememberCoroutineScope()
    val state = rememberScaffoldState()
    val context = LocalContext.current
    val resource = ResourceProviderSingleton
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }


    Scaffold(
        scaffoldState = state,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        color = MaterialTheme.colorScheme.onSurface
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
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                        )
                        IconButton(
                            onClick = { viewModel.onTriggerEvent(BuildEvent.SendText) },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "send",
                                    tint = MaterialTheme.colorScheme.onSurface,
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
                                viewModel.onTriggerEvent(BuildEvent.Save)
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = "save",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            },
                        )
                    }
                },
                backgroundColor = MaterialTheme.colorScheme.surface,
                elevation = 6.dp,
                navigationIcon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_menu_24),
                        contentDescription = "Toggle Drawer",
                        modifier = Modifier.clickable(onClick = {
                            scope.launch {
                                if (state.drawerState.isClosed) state.drawerState.open() else state.drawerState.close()
                            }
                        }),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                })
        },
        drawerContent = {
            Text("Reflexion", modifier = Modifier.padding(16.dp))
            Divider()
            drawerNavContent(
                navController,
                viewModel,
                viewModel.reflexionItemState.collectAsState().value,
                state
            )
        },
        drawerElevation = 4.dp,
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            BottomNavigation(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            ) {
                icons.forEach { navItem ->
                    BottomNavigationItem(selected = currentRoute == navItem.route, onClick = {
                        navController.navigate(navItem.route) {}
                    }, icon = {
                        Icon(
                            imageVector = navItem.image,
                            contentDescription = navItem.title,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }, label = {
                        Text(text = navItem.title, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    })
                }
            }
        },
        floatingActionButton = {
            /* SAVE */
            SmallFloatingActionButton(modifier = Modifier
                .offset {
                    IntOffset(
                        x = offsetX.value.roundToInt(), y = offsetY.value.roundToInt()
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consumeAllChanges()
                        offsetX.value += dragAmount.x
                        offsetY.value += dragAmount.y
                    }
                }, containerColor = MaterialTheme.colorScheme.primary, onClick = {
                Toast.makeText(
                    context,
                    resource.getString(R.string.changes_saved),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.onTriggerEvent(BuildEvent.Save)
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_save_alt_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    ) { paddingValues ->
            BuildItemContent(pk, navController, viewModel, paddingValues)
    }
}

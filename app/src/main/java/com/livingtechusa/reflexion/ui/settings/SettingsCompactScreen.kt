package com.livingtechusa.reflexion.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SendToMobile
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.BarItem
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.ui.viewModels.SettingsViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactScreen(
    navController: NavHostController,
    icons: List<BarItem>,
    viewModel: SettingsViewModel
) {
    Scaffold(
        topBar = {
            androidx.compose.material.TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.surface,
                elevation = 6.dp,
              )
        },
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            BottomNavigation(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
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
                                tint = MaterialTheme.colorScheme.onSurface
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
    ) {
        settingsContent(paddingValues = it, viewModel = viewModel)
    }
}

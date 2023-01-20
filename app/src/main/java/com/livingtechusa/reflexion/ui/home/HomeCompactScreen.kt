package com.livingtechusa.reflexion.ui.home

import androidx.compose.foundation.layout.Row
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.BarItem
import com.livingtechusa.reflexion.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactScreen(navController: NavHostController, icons: List<BarItem>) {
    val state = rememberScaffoldState()
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
                                navController.navigate(Screen.SettingsScreen.route)
                            },
                            content = {
                                androidx.compose.material.Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "settings",
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
    ) {
        it
        homeContent()
    }
}

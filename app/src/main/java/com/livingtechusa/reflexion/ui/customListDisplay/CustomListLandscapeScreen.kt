package com.livingtechusa.reflexion.ui.customListDisplay

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.livingtechusa.reflexion.navigation.BarItem
import com.livingtechusa.reflexion.navigation.NavBarItems


@Composable
fun Landscape(navController: NavHostController, icons: List<BarItem>) {
    val icons = NavBarItems.CustomListsDisplayBarItems
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    Row(modifier = Modifier.fillMaxSize()) {
        NavigationRail(
            containerColor = MaterialTheme.colors.background,
        ) {
            icons.forEach { navItem ->
                Spacer(modifier = Modifier.height(32.dp))
                NavigationRailItem(
                    selected = currentRoute == navItem.route,
                    onClick = {
                        navController.navigate(navItem.route) {
                            popUpTo(navController.graph.findStartDestination().id) { }
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = navItem.image,
                            contentDescription = navItem.title,
                            tint = MaterialTheme.colors.onBackground
                        )
                    })
            }
        }
        CustomListDisplayContent()
    }
}

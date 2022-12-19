package com.livingtechusa.reflexion.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.Desk
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Face
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING

object NavBarItems {
    val HomeBarItems = listOf(
        BarItem(
            title = "New",
            image = Icons.Filled.DeveloperMode,
            route = Screen.BuildItemScreen.route + "/" + -1L
        ),
        BarItem(
            title = "Topics",
            image = Icons.Filled.DataArray,
            route = Screen.ListScreen.route + "/" + -1L
        ),
        BarItem(
            title = "CustomLists",
            image = Icons.Filled.AddLink,
            route = Screen.CustomLists.route
        )
    )

    val BuildBarItems = listOf(
        BarItem(
            title = "Home",
            image = Icons.Filled.AddHome,
            route = Screen.HomeScreen.route
        ),
        BarItem(
            title = "Topics",
            image = Icons.Filled.DataArray,
            route = Screen.ListScreen.route + "/" + -1L
        ),
        BarItem(
            title = "CustomLists",
            image = Icons.Filled.AddLink,
            route = Screen.CustomLists.route
        )
    )

    val ListBarItems = listOf(
        BarItem(
            title = "New",
            image = Icons.Filled.DeveloperMode,
            route = Screen.BuildItemScreen.route
        ),
        BarItem(
            title = "CustomLists",
            image = Icons.Filled.AddLink,
            route = Screen.CustomLists.route
        ),
        BarItem(
            title = "Home",
            image = Icons.Filled.AddHome,
            route = Screen.HomeScreen.route
        )
    )

    val CustomListsBarItems = listOf(
        BarItem(
            title = "Home",
            image = Icons.Filled.AddHome,
            route = Screen.HomeScreen.route
        ),
        BarItem(
            title = "Topics",
            image = Icons.Filled.DataArray,
            route = Screen.ListScreen.route + "/" + -1L
        ),
        BarItem(
            title = "New",
            image = Icons.Filled.DeveloperMode,
            route = Screen.BuildItemScreen.route
        ),
    )
}

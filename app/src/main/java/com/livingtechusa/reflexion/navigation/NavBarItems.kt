package com.livingtechusa.reflexion.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.ViewList
import com.livingtechusa.reflexion.util.Constants.EMPTY_PK

object NavBarItems {
    val HomeBarItems = listOf(
        BarItem(
            title = "New",
            image = Icons.Filled.DeveloperMode,
            route = Screen.BuildItemScreen.route + "/" + EMPTY_PK
        ),
        BarItem(
            title = "Topics",
            image = Icons.Filled.DataArray,
            route = Screen.TopicScreen.route + "/" + EMPTY_PK
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
            route = Screen.TopicScreen.route + "/" + EMPTY_PK
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
            route = Screen.BuildItemScreen.route + "/" + EMPTY_PK
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
            route = Screen.TopicScreen.route + "/" + EMPTY_PK
        ),
        BarItem(
            title = "New",
            image = Icons.Filled.DeveloperMode,
            route = Screen.BuildItemScreen.route + "/" + EMPTY_PK
        )
    )

    val CustomListsDisplayBarItems = listOf(
        BarItem(
            title = "Lists",
            image = Icons.Filled.AddLink,
            route = Screen.CustomLists.route
        ),
        BarItem(
            title = "Topics",
            image = Icons.Filled.DataArray,
            route = Screen.TopicScreen.route + "/" + EMPTY_PK
        ),
        BarItem(
            title = "New",
            image = Icons.Filled.DeveloperMode,
            route = Screen.BuildItemScreen.route + "/" + EMPTY_PK
        ),
        BarItem(
            title = "Home",
            image = Icons.Filled.AddHome,
            route = Screen.HomeScreen.route
        )
    )
}

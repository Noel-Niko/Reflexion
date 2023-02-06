package com.livingtechusa.reflexion.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.DeveloperMode
import com.livingtechusa.reflexion.util.Constants.EMPTY_PK

object NavBarItems {
    val SettingsBarItems: List<BarItem> = listOf(
        BarItem(
            title = "Home",
            image = Icons.Filled.AddHome,
            route = Screen.HomeScreen.route
        ),
        BarItem(
            title = "Bookmark",
            image = Icons.Filled.Bookmark,
            route = Screen.BookmarkScreen.route
        ),
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
        )
    )
    val HomeBarItems = listOf(
        BarItem(
            title = "Bookmark",
            image = Icons.Filled.Bookmark,
            route = Screen.BookmarkScreen.route
        ),
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
        )
    )

    val BuildBarItems = listOf(
        BarItem(
            title = "Home",
            image = Icons.Filled.AddHome,
            route = Screen.HomeScreen.route
        ),
        BarItem(
            title = "Bookmark",
            image = Icons.Filled.Bookmark,
            route = Screen.BookmarkScreen.route
        ),
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
    )

    val TopicsBarItems = listOf(
        BarItem(
            title = "Home",
            image = Icons.Filled.AddHome,
            route = Screen.HomeScreen.route
        ),
        BarItem(
            title = "Bookmark",
            image = Icons.Filled.Bookmark,
            route = Screen.BookmarkScreen.route
        ),
        BarItem(
            title = "Lists",
            image = Icons.Filled.AddLink,
            route = Screen.CustomLists.route
        ),
        BarItem(
            title = "New",
            image = Icons.Filled.DeveloperMode,
            route = Screen.BuildItemScreen.route + "/" + EMPTY_PK
        ),

    )

    val CustomListsBarItems = listOf(
        BarItem(
            title = "Home",
            image = Icons.Filled.AddHome,
            route = Screen.HomeScreen.route
        ),
        BarItem(
            title = "Bookmark",
            image = Icons.Filled.Bookmark,
            route = Screen.BookmarkScreen.route
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
            title = "Home",
            image = Icons.Filled.AddHome,
            route = Screen.HomeScreen.route
        ),
        BarItem(
            title = "Bookmark",
            image = Icons.Filled.Bookmark,
            route = Screen.BookmarkScreen.route
        ),
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
        )

    )

    val BookMarksBarItems: List<BarItem> = listOf(
        BarItem(
            title = "Home",
            image = Icons.Filled.AddHome,
            route = Screen.HomeScreen.route
        ),
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
        )
    )
}

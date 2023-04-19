package com.livingtechusa.reflexion.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.DeveloperMode
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.util.Constants.EMPTY_PK
import com.livingtechusa.reflexion.util.ResourceProviderSingleton

object NavBarItems {
    val resource = ResourceProviderSingleton
    val SettingsBarItems: List<BarItem> = listOf(
        BarItem(
            title = resource.getString(R.string.home),
            image = Icons.Filled.AddHome,
            route = Screen.HomeScreen.route
        ),
        BarItem(
            title = resource.getString(R.string.bookmark_title_case),
            image = Icons.Filled.Bookmark,
            route = Screen.BookmarkScreen.route
        ),
        BarItem(
            title = resource.getString(R.string.lists_title_case),
            image = Icons.Filled.AddLink,
            route = Screen.CustomLists.route
        ),
        BarItem(
            title = resource.getString(R.string.topics_title_case),
            image = Icons.Filled.DataArray,
            route = Screen.TopicScreen.route + "/" + EMPTY_PK
        ),
        BarItem(
            title = resource.getString(R.string.new_tab),
            image = Icons.Filled.DeveloperMode,
            route = Screen.BuildItemScreen.route + "/" + EMPTY_PK
        )
    )
    val HomeBarItems = listOf(
        BarItem(
            title = resource.getString(R.string.bookmark_title_case),
            image = Icons.Filled.Bookmark,
            route = Screen.BookmarkScreen.route
        ),
        BarItem(
            title = resource.getString(R.string.lists_title_case),
            image = Icons.Filled.AddLink,
            route = Screen.CustomLists.route
        ),
        BarItem(
            title = resource.getString(R.string.topics_title_case),
            image = Icons.Filled.DataArray,
            route = Screen.TopicScreen.route + "/" + EMPTY_PK
        ),
        BarItem(
            title = resource.getString(R.string.new_tab),
            image = Icons.Filled.DeveloperMode,
            route = Screen.BuildItemScreen.route + "/" + EMPTY_PK
        )
    )

    val BuildBarItems = listOf(
        BarItem(
            title = resource.getString(R.string.home),
            image = Icons.Filled.AddHome,
            route = Screen.HomeScreen.route
        ),
        BarItem(
            title = resource.getString(R.string.bookmark_title_case),
            image = Icons.Filled.Bookmark,
            route = Screen.BookmarkScreen.route
        ),
        BarItem(
            title = resource.getString(R.string.lists_title_case),
            image = Icons.Filled.AddLink,
            route = Screen.CustomLists.route
        ),
        BarItem(
            title = resource.getString(R.string.topics_title_case),
            image = Icons.Filled.DataArray,
            route = Screen.TopicScreen.route + "/" + EMPTY_PK
        ),
    )

    val TopicsBarItems = listOf(
        BarItem(
            title = resource.getString(R.string.home),
            image = Icons.Filled.AddHome,
            route = Screen.HomeScreen.route
        ),
        BarItem(
            title = resource.getString(R.string.bookmark_title_case),
            image = Icons.Filled.Bookmark,
            route = Screen.BookmarkScreen.route
        ),
        BarItem(
            title = resource.getString(R.string.lists_title_case),
            image = Icons.Filled.AddLink,
            route = Screen.CustomLists.route
        ),
        BarItem(
            title = resource.getString(R.string.new_tab),
            image = Icons.Filled.DeveloperMode,
            route = Screen.BuildItemScreen.route + "/" + EMPTY_PK
        ),

    )

    val CustomListsBarItems = listOf(
        BarItem(
            title = resource.getString(R.string.home),
            image = Icons.Filled.AddHome,
            route = Screen.HomeScreen.route
        ),
        BarItem(
            title = resource.getString(R.string.bookmark_title_case),
            image = Icons.Filled.Bookmark,
            route = Screen.BookmarkScreen.route
        ),
        BarItem(
            title = resource.getString(R.string.topics_title_case),
            image = Icons.Filled.DataArray,
            route = Screen.TopicScreen.route + "/" + EMPTY_PK
        ),
        BarItem(
            title = resource.getString(R.string.new_tab),
            image = Icons.Filled.DeveloperMode,
            route = Screen.BuildItemScreen.route + "/" + EMPTY_PK
        )
    )

    val CustomListsDisplayBarItems = listOf(
        BarItem(
            title = resource.getString(R.string.home),
            image = Icons.Filled.AddHome,
            route = Screen.HomeScreen.route
        ),
        BarItem(
            title = resource.getString(R.string.bookmark_title_case),
            image = Icons.Filled.Bookmark,
            route = Screen.BookmarkScreen.route
        ),
        BarItem(
            title = resource.getString(R.string.lists_title_case),
            image = Icons.Filled.AddLink,
            route = Screen.CustomLists.route
        ),
        BarItem(
            title = resource.getString(R.string.topics_title_case),
            image = Icons.Filled.DataArray,
            route = Screen.TopicScreen.route + "/" + EMPTY_PK
        ),
        BarItem(
            title = resource.getString(R.string.new_tab),
            image = Icons.Filled.DeveloperMode,
            route = Screen.BuildItemScreen.route + "/" + EMPTY_PK
        )

    )

    val BookMarksBarItems: List<BarItem> = listOf(
        BarItem(
            title = resource.getString(R.string.home),
            image = Icons.Filled.AddHome,
            route = Screen.HomeScreen.route
        ),
        BarItem(
            title = resource.getString(R.string.lists_title_case),
            image = Icons.Filled.AddLink,
            route = Screen.CustomLists.route
        ),
        BarItem(
            title = resource.getString(R.string.topics_title_case),
            image = Icons.Filled.DataArray,
            route = Screen.TopicScreen.route + "/" + EMPTY_PK
        ),
        BarItem(
            title = resource.getString(R.string.new_tab),
            image = Icons.Filled.DeveloperMode,
            route = Screen.BuildItemScreen.route + "/" + EMPTY_PK
        )
    )
}

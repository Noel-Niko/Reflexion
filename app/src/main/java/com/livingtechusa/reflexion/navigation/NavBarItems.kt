package com.livingtechusa.reflexion.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Face
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING

object NavBarItems {
    val HomeBarItems = listOf(
        BarItem(
            title = "New",
            image = Icons.Filled.Face,
            route = Screen.BuildItemScreen.route + "/" + -1L
        ),
        BarItem(
            title = "Topics",
            image = Icons.Filled.Favorite,
            route = Screen.ListScreen.route + "/" + -1L
        ),

    )
}

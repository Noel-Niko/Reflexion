package com.livingtechusa.reflexion.ui.customLists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowWidthSizeClass
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.util.ReflexionArrayItem

@Composable
fun CustomListContent(
    navController: NavHostController,
    viewModel: CustomListsViewModel = hiltViewModel()
) {
    val lists by viewModel.listOfLists.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(lists.size) { index ->

            LazyRow(
                modifier = Modifier.fillMaxHeight(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(lists.size) {
                    Text(text = lists[index].reflexionItemName.toString())
                }
            }
        }
    }
}



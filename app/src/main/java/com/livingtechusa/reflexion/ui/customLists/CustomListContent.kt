package com.livingtechusa.reflexion.ui.customLists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.util.ReflexionArrayItem

@Composable
fun CustomListContent(
    navController: NavHostController,
    viewModel: CustomListsViewModel = hiltViewModel()
) {
    val customList by viewModel.customList.collectAsState()
    val listOfLists = mutableListOf<ReflexionArrayItem>(customList)
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(listOfLists.size ?: 1) { index ->
            Column(modifier = Modifier.background(Color.LightGray)) {
                Text(text = "List Of Lists")
                Spacer(modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth())
                LazyRow(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (customList.items.isNullOrEmpty().not()) {
                        val count: Int = customList.items?.size ?: 0
                        items(count) {
                            Text(text = customList.items?.get(it)?.reflexionItemName.toString() ?: "Empty")
                        }
                    }
                }
            }
        }
    }
}



package com.livingtechusa.reflexion.ui.build

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel

@Composable
fun BuildItemScreenLandscape() {
    val configuration = LocalConfiguration.current
    if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
        BuildItemScreen()
    } else {
        val itemViewModel: ItemViewModel = viewModel(ItemViewModel::class.java)
        val reflexionItem by itemViewModel.reflexionItem.collectAsState()
        val isParent by itemViewModel.isParent.collectAsState()
        val topic by itemViewModel.topic.collectAsState()
        val parentName by itemViewModel.parentName.collectAsState()
        val keyWords by itemViewModel.keyWords.collectAsState()
        val linkedLists by itemViewModel.linkedLists.collectAsState()
        val siblings by itemViewModel.siblings.collectAsState()
        val children by itemViewModel.children.collectAsState()


        val scaffoldState = rememberScaffoldState()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()

        ) {
            if (reflexionItem.name.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(25.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                item {

                }
            }
        }
    }
}
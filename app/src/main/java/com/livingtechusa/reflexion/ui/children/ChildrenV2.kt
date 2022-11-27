package com.livingtechusa.reflexion.ui.children

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.ui.components.ItemRecyclerView
import com.livingtechusa.reflexion.ui.components.ReflexionItemListUI
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.ResourceProviderSingleton

const val ChildRouteV2 = "children"
@Composable
fun ChildrenV2(
    itemViewModel: ItemViewModel = hiltViewModel(),
    navController: NavHostController,
    pk: Long
) {
    val configuration = LocalConfiguration.current
    itemViewModel.onTriggerEvent(ChildEvent.GetChildren(pk))
    val resource = ResourceProviderSingleton

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        // TODO
    } else {
        val viewModel: ItemViewModel = itemViewModel
        val reflexionItem by itemViewModel.reflexionItem.collectAsState()
        val isParent by itemViewModel.isParent.collectAsState()
        val parentName by itemViewModel.parentName.collectAsState()
        val keyWords by itemViewModel.keyWords.collectAsState()
        val linkedLists by itemViewModel.linkedLists.collectAsState()
        val siblings by itemViewModel.siblings.collectAsState()
        val children by itemViewModel.children.collectAsState()
        val context = LocalContext.current
        val tempReflexionItem: ReflexionItem = remember { reflexionItem }

        if(children.isEmpty()){
            Toast.makeText(context, resource.getString(R.string.no_children_found), Toast.LENGTH_SHORT).show()
        }
        ReflexionItemListUI(children, navController)
    }
}
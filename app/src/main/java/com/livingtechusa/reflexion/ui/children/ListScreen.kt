package com.livingtechusa.reflexion.ui.children

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.ui.components.ReflexionItemListUI
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.ResourceProviderSingleton

const val ListRoute = "list"

@Composable
fun ListDisplay(
    itemViewModel: ItemViewModel = hiltViewModel(),
    navController: NavHostController,
    pk: Long
) {
    val configuration = LocalConfiguration.current
    itemViewModel.onTriggerEvent(ListEvent.GetList(pk))
    val reflexionItemList by itemViewModel.list.collectAsState()
    val context = LocalContext.current
    val resource = ResourceProviderSingleton

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        // TODO
    } else {
        if (reflexionItemList.isEmpty()) {
            Toast.makeText(context, resource.getString(R.string.no_items_found), Toast.LENGTH_SHORT)
                .show()
        } else {
            ReflexionItemListUI(reflexionItemList, navController, viewModel = itemViewModel)
        }
    }
}
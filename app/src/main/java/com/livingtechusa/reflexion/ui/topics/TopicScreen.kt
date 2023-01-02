package com.livingtechusa.reflexion.ui.topics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowWidthSizeClass
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.ui.components.ReflexionItemListUI
import com.livingtechusa.reflexion.ui.viewModels.ListViewModel
import com.livingtechusa.reflexion.util.extensions.findActivity

const val ListRoute = "list"

@Composable
fun ListDisplay(
    viewModel: ListViewModel = hiltViewModel(),
    navHostController: NavHostController,
    windowSize: WindowWidthSizeClass,
    pk: Long?
) {
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    // If `lifecycleOwner` changes, dispose and reset the effect
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer = LifecycleEventObserver { _, event ->
                viewModel.onTriggerEvent(ListEvent.GetList(pk))
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val context = LocalContext.current
    val icons = NavBarItems.ListBarItems

    val search by viewModel.search.collectAsState()

    if (context.findActivity() != null) {
        when (windowSize) {
            WindowWidthSizeClass.COMPACT -> {
                CompactScreen(navHostController, icons, viewModel, search, viewModel::searchEvent, viewModel::onUp)
            }

            WindowWidthSizeClass.MEDIUM -> {
                MediumScreen(navHostController, icons, viewModel, search, viewModel::searchEvent, viewModel::onUp)
            }

//            WindowWidthSizeClass.EXPANDED -> {
//                ExpandedScreen(navHostController, icons, viewModel, pk)
//                viewModel.navigationType = ReflexionNavigationType.PERMANENT_NAVIGATION_DRAWER
//            }

            else -> CompactScreen(navHostController, icons, viewModel, search, viewModel::searchEvent, viewModel::onUp)
        }
    }
}

@Composable
fun ListContent(
    itemViewModel: ListViewModel,
    navController: NavHostController
) {
    ReflexionItemListUI(
        navController = navController,
        viewModel = itemViewModel
    )
}


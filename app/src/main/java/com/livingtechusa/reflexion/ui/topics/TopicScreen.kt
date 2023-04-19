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
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.ui.viewModels.TopicsViewModel
import com.livingtechusa.reflexion.util.extensions.findActivity

const val ListRoute = "list"

@Composable
fun ListDisplay(
    viewModel: TopicsViewModel = hiltViewModel(),
    navHostController: NavHostController,
    pk: Long?
) {
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    // If `lifecycleOwner` changes, dispose and reset the effect
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            viewModel.onTriggerEvent(TopicItemEvent.GetTopicItem(pk))
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val context = LocalContext.current
    val icons = NavBarItems.TopicsBarItems

    val search by viewModel.search.collectAsState()

    if (context.findActivity() != null) {
        CompactScreen(
            navHostController,
            icons,
            viewModel,
            search,
            viewModel::searchEvent,
            viewModel::onUp,
            viewModel::bookmark
        )
    }
}

@Composable
fun ListContent(
    itemViewModel: TopicsViewModel,
    navController: NavHostController
) {
    ReflexionItemListUI(
        navController = navController,
        viewModel = itemViewModel
    )
}


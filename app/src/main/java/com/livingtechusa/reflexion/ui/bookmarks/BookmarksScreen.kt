package com.livingtechusa.reflexion.ui.bookmarks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowWidthSizeClass
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.ui.viewModels.BookmarksViewModel
import com.livingtechusa.reflexion.util.extensions.findActivity

const val BOOKMARKS = "bookmarks"

@Composable
fun BookmarksScreen(
    navHostController: NavHostController,
    windowSize: WindowWidthSizeClass,
    viewModel: BookmarksViewModel = hiltViewModel()
) {

    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer = LifecycleEventObserver { owner, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                viewModel.onTriggerEvent(BookmarksEvent.GetAllBookmarks)
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val context = LocalContext.current
    val icons = NavBarItems.BookMarksBarItems

    val search by viewModel.search.collectAsState()

    if (context.findActivity() != null) {
        when (windowSize) {
            WindowWidthSizeClass.COMPACT -> {
                BookmarksCompactScreen(navHostController, icons, viewModel, search, viewModel::searchEvent)
            }

//            WindowWidthSizeClass.MEDIUM -> {
//                Landscape(navHostController, icons)
//            }

//            WindowWidthSizeClass.EXPANDED -> {
//                ExpandedScreen(navHostController, icons)
//                viewModel.navigationType = ReflexionNavigationType.PERMANENT_NAVIGATION_DRAWER
//            }

            else -> BookmarksCompactScreen(
                navHostController,
                icons,
                viewModel,
                search,
                viewModel::searchEvent
            )
        }
    }
}


@Composable
fun BookmarksContent(
    viewModel: BookmarksViewModel,
    navController: NavHostController
) {
    ReflexionItemListUIForBookmarks(
        navController = navController,
        viewModel = viewModel
    )
}

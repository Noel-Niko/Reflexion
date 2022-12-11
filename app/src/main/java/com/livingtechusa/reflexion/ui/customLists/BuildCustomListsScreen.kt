package com.livingtechusa.reflexion.ui.customLists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowWidthSizeClass
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.extensions.findActivity

const val BuildCustomList = "build_custom_lists"

@Composable
fun BuildCustomListsScreen(
    navController: NavHostController,
    windowSize: WindowWidthSizeClass,
    viewModel: CustomListsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val icons = NavBarItems.CustomListsBarItems

    if (context.findActivity() != null) {
        when (windowSize) {
            WindowWidthSizeClass.COMPACT -> {
                CustomListCompactScreen(
                    navController = navController,
                    icons = icons,
                    viewModel = viewModel,
                )
            }

            WindowWidthSizeClass.MEDIUM -> {
                // todo
            }

//            WindowWidthSizeClass.EXPANDED -> {
//                ExpandedScreen(navHostController, icons, viewModel)
//                viewModel.navigationType = ReflexionNavigationType.PERMANENT_NAVIGATION_DRAWER
//            }

            else -> CustomListCompactScreen(
                navController = navController,
                icons = icons,
                viewModel = viewModel,
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomListsContent(
    navController: NavHostController, viewModel: CustomListsViewModel, paddingValues: PaddingValues
) {
    Box(modifier = Modifier.padding(paddingValues = paddingValues)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                Text(text = "this is the custom list screen")
            }
        }
    }
}

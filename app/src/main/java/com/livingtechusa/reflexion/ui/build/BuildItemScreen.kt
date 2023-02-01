package com.livingtechusa.reflexion.ui.build

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowWidthSizeClass
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.extensions.findActivity

const val BuildRoute = "build"

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun BuildItemScreen(
    pk: Long,
    navHostController: NavHostController,
    windowSize: WindowWidthSizeClass,
    viewModel: ItemViewModel,
) {
    val context = LocalContext.current
    val icons = NavBarItems.BuildBarItems
    if (context.findActivity() != null) {
        when (windowSize) {
            WindowWidthSizeClass.COMPACT -> {
                BuildItemCompactScreen(pk, navHostController, icons, viewModel)
            }

            WindowWidthSizeClass.MEDIUM -> {
                MediumScreen(pk, navHostController, icons, viewModel)
            }

//            WindowWidthSizeClass.EXPANDED -> {
//                ExpandedScreen(navHostController, icons, viewModel)
//                viewModel.navigationType = ReflexionNavigationType.PERMANENT_NAVIGATION_DRAWER
//            }

            else -> BuildItemCompactScreen(pk, navHostController, icons, viewModel)
        }
    }
}
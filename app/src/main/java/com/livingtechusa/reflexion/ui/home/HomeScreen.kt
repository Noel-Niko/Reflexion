package com.livingtechusa.reflexion.ui.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowWidthSizeClass
import com.livingtechusa.reflexion.navigation.CompactScreen
import com.livingtechusa.reflexion.navigation.ExpandedScreen
import com.livingtechusa.reflexion.navigation.MediumScreen
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.navigation.calculateWindowSizeClass
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.extensions.findActivity

const val HOME = "home"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: ItemViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val icons = NavBarItems.HomeBarItems
    if(context.findActivity() != null) {
        when(calculateWindowSizeClass(context.findActivity()!!)) {
            WindowWidthSizeClass.COMPACT -> { CompactScreen(navHostController, icons) }
            WindowWidthSizeClass.MEDIUM -> { MediumScreen(navHostController, icons) }
            WindowWidthSizeClass.EXPANDED -> { ExpandedScreen(navHostController, icons) }
            else -> CompactScreen(navHostController, icons)
        }
    }
}

package com.livingtechusa.reflexion.ui.build

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.ui.viewModels.BuildItemViewModel
import com.livingtechusa.reflexion.util.extensions.findActivity

const val BuildRoute = "build"

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun BuildItemScreen(
    pk: Long,
    navHostController: NavHostController,
    viewModel: BuildItemViewModel,
) {
    val context = LocalContext.current
    val icons = NavBarItems.BuildBarItems
    if (context.findActivity() != null) {
        BuildItemCompactScreen(pk, navHostController, icons, viewModel)
    }
}
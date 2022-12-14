package com.livingtechusa.reflexion.ui.customLists

import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.window.core.layout.WindowWidthSizeClass
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.ui.components.cascade.CascadeDropdownMenu
import com.livingtechusa.reflexion.ui.components.cascade.rememberCascadeState
import com.livingtechusa.reflexion.ui.components.menu.CustomDropDownMenu
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.extensions.findActivity
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch

const val BuildCustomList = "build_custom_lists"

@Composable
fun BuildCustomListsScreen(
    navController: NavHostController,
    windowSize: WindowWidthSizeClass,
    viewModel: CustomListsViewModel = hiltViewModel()
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


@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun CustomListsContent(
    navController: NavHostController, viewModel: CustomListsViewModel, paddingValues: PaddingValues
) {
    val scope = rememberCoroutineScope()
    val listItems = viewModel.topicsList.collectAsState()
    val childList = viewModel.childList.collectAsState()
    val state = rememberCascadeState()
    var selectedItem by remember {
        mutableStateOf("")
    }
    var expanded by remember {
        mutableStateOf(true)
    }
    Scaffold(modifier = Modifier.padding(paddingValues)) { innerPadding ->
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .align(
                        Alignment.CenterVertically,
                    )
                    .padding(innerPadding)
                    .background(color = MaterialTheme.colors.background)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .border(
                            1.dp,
                            Color.Black,
                            RectangleShape
                        )
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // filter options based on text field value
                    val filteringOptions =
                        listItems.value.filter { it.name.contains(selectedItem, ignoreCase = true) }
                    CustomDropDownMenu(isOpen = expanded, setIsOpen = {
                        expanded = !expanded
                    }, itemSelected = viewModel::selectItem)

                }
            }
        }
    }
}



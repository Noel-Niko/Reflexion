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
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.AddCard
import androidx.compose.material.icons.twotone.AirlineStops
import androidx.compose.material.icons.twotone.Anchor
import androidx.compose.material.icons.twotone.Architecture
import androidx.compose.material.icons.twotone.Archive
import androidx.compose.material.icons.twotone.Circle
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material.icons.twotone.FileCopy
import androidx.compose.material.icons.twotone.Lan
import androidx.compose.material.icons.twotone.Language
import androidx.compose.material.icons.twotone.Share
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowWidthSizeClass
import com.androidpoet.dropdown.DropDownMenuBuilder
import com.androidpoet.dropdown.MenuItem
import com.androidpoet.dropdown.dropDownMenu
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.ui.components.cascade.rememberCascadeState
import com.livingtechusa.reflexion.ui.components.menu.CustomDropDownMenu
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.util.ReflexionArrayItem
import com.livingtechusa.reflexion.util.ReflexionArrayItem.Companion.traverseDepthFirst
import com.livingtechusa.reflexion.util.extensions.findActivity

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
    val itemTree = viewModel.itemTree.collectAsState()
    val traversedList = mutableListOf<ReflexionArrayItem>()
    traverseDepthFirst(itemTree.value) { RAI ->
        traversedList.add(RAI)
    }
    val state = rememberCascadeState()
    var selectedItem by remember {
        mutableStateOf("")
    }
    var expanded by remember {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = Modifier.padding(paddingValues),
        backgroundColor = Color.LightGray

    ) { innerPadding ->
        Spacer(Modifier.height(16.dp))
        Column(modifier = Modifier.fillMaxSize()) {
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
                        ExposedDropdownMenuBox(
                            modifier = Modifier
                                .fillMaxWidth(),
                            expanded = expanded,
                            onExpandedChange = {
                                expanded = !expanded
                            }
                        ) {
                            TextField(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally),
                                value = selectedItem,
                                onValueChange = { selectedItem = it },
                                label = {
                                    Text(
                                        text = "Topic"
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded
                                    )
                                },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(
                                    textColor = Color.Black,
                                    backgroundColor = Color.Transparent
                                )
                            )

//                        // filter options based on text field value
//                        val filteringOptions =
//                            itemTree.value.filter { abridgedParent ->
//                                abridgedParent?.name?.contains(
//                                    selectedItem,
//                                    ignoreCase = true
//                                ) ?: true
//                            }
//
//                        if (filteringOptions.isNotEmpty()) {

                            CustomDropDownMenu(
                                isOpen = expanded,
                                setIsOpen = {
                                    expanded = !expanded
                                },
                                itemSelected = viewModel::selectItem,
                                menu = getMenu(itemTree.value)
                            )
                        }
                    }
                }
            }
            Row(
                Modifier
                    .fillMaxSize()
                    .background(Color.Red)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Yellow)
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .background(Color.Blue)
                    )
                    Text(text = "Custom Lists")
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .background(Color.Green)
                    )
                CustomListContent(navController = navController, viewModel = viewModel)
                }
            }
        }

//        Spacer(Modifier.height(16.dp))
//        Row(modifier = Modifier.fillMaxSize()) {
//            Column(modifier = Modifier.fillMaxSize()) {
//                CustomListContent(navController = navController, viewModel = viewModel)
//            }
//        }
    }
}


fun getItems(
    reflexionArrayItem: ReflexionArrayItem,
    newBuilder: DropDownMenuBuilder<String>,
    list: List<ImageVector>
): DropDownMenuBuilder<String> {
    var next = list.random()
    newBuilder.item(
        reflexionArrayItem.reflexionItemPk.toString(),
        reflexionArrayItem.reflexionItemName.toString()
    ) {
        icon(next)
        reflexionArrayItem.items?.forEach {
            getItems(it, this, list)
        }
    }
    return newBuilder
}


fun getMenu(
    tree: ReflexionArrayItem
): MenuItem<String> {
    val list: MutableList<ImageVector> = mutableListOf(
        Icons.TwoTone.Language,
        Icons.TwoTone.FileCopy,
        Icons.TwoTone.Share,
        Icons.TwoTone.Done,
        Icons.TwoTone.AirlineStops,
        Icons.TwoTone.Lan,
        Icons.TwoTone.AddCard,
        Icons.TwoTone.Circle,
        Icons.TwoTone.Anchor,
        Icons.TwoTone.Architecture,
        Icons.TwoTone.Archive
    )
    val menu = dropDownMenu<String> {
        getItems(tree, this, list)
    }
    return menu
}





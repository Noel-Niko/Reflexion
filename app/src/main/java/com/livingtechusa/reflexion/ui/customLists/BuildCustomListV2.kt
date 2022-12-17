package com.livingtechusa.reflexion.ui.customLists

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.twotone.Abc
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.DeleteSweep
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material.icons.twotone.FileCopy
import androidx.compose.material.icons.twotone.Satellite
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
    val state = rememberCascadeState()
    var selectedItem by remember {
        mutableStateOf("")
    }
    var expanded by remember {
        mutableStateOf(false)
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
                            //.offset(100.dp),
                            value = selectedItem,
                            onValueChange = { selectedItem = it },
                            label = { Text(text = "Topic") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded
                                )
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
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
    }
}

private

fun getMenu(tree: ReflexionArrayItem): MenuItem<String> {

//    fun nextLevel(item: ReflexionArrayItem): MutableList<MenuItem<String>>? {
//        this.menu.children.add()
//        var list : MutableList<MenuItem<String>>? =  mutableListOf()
//        val menuItem: MenuItem<String> = CustomDropDownMenuBuilder().MenuItem<String>(item.reflexionItemPk.toString(), item.reflexionItemName)
//        list?.add(menuItem)
//    }


    val menuBuilder = DropDownMenuBuilder<String>()
    fun getChild(reflxionArrayItemList: MutableList<ReflexionArrayItem>?): MutableList<MenuItem<String>>? {
        val itemList: MutableList<MenuItem<String>> =  mutableListOf()
        reflxionArrayItemList?.forEach() {
           itemList.add( menuBuilder.menu.apply {
                this.id = it.reflexionItemPk.toString()
                this.title = it.reflexionItemName
                children = getChild(it.items?.toMutableList())
            }
           )
        }
        return itemList
    }

    fun addItem(reflexionArrayItem: ReflexionArrayItem) {

    }

    val menu = dropDownMenu<String> {
        item("copy", "Copy") {
            icon(Icons.TwoTone.FileCopy)
        }
//        item(tree.reflexionItemPk.toString(), tree.reflexionItemName) {
//            icon(Icons.TwoTone.Language)
//            tree.items?.forEach() { it1 ->
//                item(it1.reflexionItemPk.toString(), it1.reflexionItemName) {
//                    it1.items?.forEach { it2 ->
//                        item(it2.reflexionItemPk.toString(), it2.reflexionItemName) {
//                            it2.items?.forEach { it3 ->
//                                item(it3.reflexionItemPk.toString(), it3.reflexionItemName)
//                            }
//                        }
//                    }
//                }
//            }
//        }
        item(tree.reflexionItemPk.toString(), tree.reflexionItemName) {
            icon(Icons.TwoTone.Abc)
            tree.items?.forEach() { it2 ->
                item(it2.reflexionItemPk.toString(), it2.reflexionItemName) {
                        var next = it2.items
                        while(next.isNullOrEmpty().not()) {
                            next?.forEach {
                                item(it.reflexionItemPk.toString(), it.reflexionItemName) {
                                }
                                next = it.items
                            }
                        }

                    }
                }
            }
        item("remove", "Remove") {
            icon(Icons.TwoTone.DeleteSweep)
            item("yep", "Yep") {
                icon(Icons.TwoTone.Done)
            }
            item("go_back", "Go back") {
                icon(Icons.TwoTone.Close)
            }
        }
    }
    return menu
}

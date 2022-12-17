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
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.DeleteSweep
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material.icons.twotone.FileCopy
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowWidthSizeClass
import com.androidpoet.dropdown.MenuItem
import com.androidpoet.dropdown.dropDownMenu
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.models.AbridgedReflexionItem
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

//
//fun getMenu(
//    tree: ReflexionArrayItem
//): MenuItem<String> {
//    val menu = dropDownMenu<String> {
//        tree.  forEach() { abridgedParent ->
//            item(abridgedParent?.autogenPK.toString(), abridgedParent?.name.toString()) {
//                icon(Icons.TwoTone.Share)
//                children.forEach() { abridgedChild ->
//                    item(abridgedChild?.autogenPK.toString(), abridgedChild?.name.toString()) {
//                    }
//                }
////                item("as_a_file", "As a file") {
////                    item("pdf", "PDF")
////                    item("epub", "EPUB")
////                    item("web_page", "Web page")
////                    item("microsoft_word", "Microsoft word")
////                }
//            }
//            item("remove", "Remove") {
//                icon(Icons.TwoTone.DeleteSweep)
//                item("yep", "Yep") {
//                    icon(Icons.TwoTone.Done)
//                }
//                item("go_back", "Go back") {
//                    icon(Icons.TwoTone.Close)
//                }
//            }
//        }
//
//    }
//    return menu
//}


fun getMenu(tree: ReflexionArrayItem): MenuItem<String> {
    val menu = dropDownMenu<String> {
        item("copy", "Copy") {
            icon(Icons.TwoTone.FileCopy)
        }
        item(tree.reflexionItemPk.toString(), tree.reflexionItemName) {
            icon(Icons.TwoTone.Language)
            tree.items?.forEach() { it1 ->
                item(it1.reflexionItemPk.toString(), it1.reflexionItemName) {
                    it1.items?.forEach { it2 ->
                        item(it2.reflexionItemPk.toString(), it2.reflexionItemName) {
                            it2.items?.forEach { it3 ->
                                item(it3.reflexionItemPk.toString(), it3.reflexionItemName)
                            }
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

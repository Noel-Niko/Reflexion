package com.livingtechusa.reflexion.ui.customLists

import android.graphics.Bitmap
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Lan
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowWidthSizeClass
import com.androidpoet.dropdown.DropDownMenuBuilder
import com.androidpoet.dropdown.MenuItem
import com.androidpoet.dropdown.dropDownMenu
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.ui.components.cascade.rememberCascadeState
import com.livingtechusa.reflexion.ui.components.menu.CustomDropDownMenu
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import com.livingtechusa.reflexion.util.ReflexionArrayItem
import com.livingtechusa.reflexion.util.ReflexionArrayItem.Companion.traverseBreadthFirst
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

//            WindowWidthSizeClass.MEDIUM -> {
//                // todo
//            }

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


@OptIn(
    ExperimentalMaterialApi::class, ExperimentalAnimationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun CustomListsContent(
    navController: NavHostController, viewModel: CustomListsViewModel, paddingValues: PaddingValues
) {
    val scope = rememberCoroutineScope()
    val itemTree = viewModel.itemTree.collectAsState()
    val traversedList = mutableListOf<ReflexionArrayItem>()

    val state = rememberCascadeState()
    var searchText by remember {
        mutableStateOf(EMPTY_STRING)
    }
    var expanded by remember {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = Modifier.padding(paddingValues),
    ) { innerPadding ->
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .align(
                            Alignment.CenterVertically,
                        )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
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
                                    .align(Alignment.CenterHorizontally)
                                    .shadow(20.dp)
                                    .fillMaxWidth(),
                                value = searchText,
                                onValueChange = {
                                    searchText = it
                                },
                                label = {
                                    Text(
                                        text = stringResource(R.string.search)
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded
                                    )
                                },
                                colors = TextFieldDefaults.textFieldColors(
                                    textColor = MaterialTheme.colorScheme.primary,
                                    // containerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                            CustomDropDownMenu(
                                modifier = Modifier
                                    .fillMaxWidth(.75f),
                                isOpen = expanded,
                                setIsOpen = {
                                    expanded = !expanded
                                },
                                itemSelected = viewModel::selectItem,
                                menu = getMenu(itemTree.value),
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // filter options based on text field value
                val filteringOptions: MutableList<ReflexionArrayItem> = mutableListOf()
                if (searchText != EMPTY_STRING) {
                    itemTree.value.let { abridgedParent ->
                        // Breadth first Search for search item
                        traverseBreadthFirst(itemTree.value) { RAI ->
                            if (RAI.itemName?.contains(
                                    searchText,
                                    ignoreCase = true
                                ) == true
                            ) {
                                filteringOptions.add(RAI)
                            }
                        }
                    }
                }
                if (filteringOptions.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(filteringOptions.size) {
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                                    .clickable {
                                        viewModel.selectItem(filteringOptions[it].itemPK.toString())
                                        searchText = EMPTY_STRING
                                    }
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .align(Alignment.CenterHorizontally),
                                shape = MaterialTheme.shapes.extraLarge,
                                elevation = CardDefaults.elevatedCardElevation(4.dp)
                            ) {
                                Text(
                                    text = filteringOptions[it].itemName
                                        ?: stringResource(R.string.no_match_found),
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(16.dp, 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                } else {
//                    Divider()
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
//    list: List<ImageVector>
): DropDownMenuBuilder<String> {
    newBuilder.item(
        reflexionArrayItem.itemPK.toString(),
        reflexionArrayItem.itemName.toString()
    ) {
        icon(Icons.TwoTone.Lan)
        reflexionArrayItem.children.forEach { reflexionArrayItem ->
            getItems(reflexionArrayItem, this) //, list)
        }
    }
    return newBuilder
}


fun getMenu(
    tree: ReflexionArrayItem
): MenuItem<String> {
//    val list: MutableList<ImageVector> = mutableListOf(
//        Icons.TwoTone.Language,
//        Icons.TwoTone.FileCopy,
//        Icons.TwoTone.Share,
//        Icons.TwoTone.Done,
//        Icons.TwoTone.AirlineStops,
//        Icons.TwoTone.Lan,
//        Icons.TwoTone.AddCard,
//        Icons.TwoTone.Circle,
//        Icons.TwoTone.FormatListBulleted,
//        Icons.TwoTone.Anchor,
//        Icons.TwoTone.Architecture,
//        Icons.TwoTone.Archive
//    )
    val menu = dropDownMenu<String> {
        getItems(tree, this) //, list)
    }
    return menu
}





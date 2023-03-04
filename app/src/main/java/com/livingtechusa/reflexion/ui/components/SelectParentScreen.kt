package com.livingtechusa.reflexion.ui.components

import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.twotone.Lan
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.androidpoet.dropdown.DropDownMenuBuilder
import com.androidpoet.dropdown.MenuItem
import com.androidpoet.dropdown.dropDownMenu
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.models.ReflexionArrayItem
import com.livingtechusa.reflexion.ui.components.menu.CustomDropDownMenu
import com.livingtechusa.reflexion.ui.viewModels.BuildItemViewModel
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.util.Constants
import com.livingtechusa.reflexion.util.ResourceProviderSingleton

const val SELECT_PARENT = "SelectParentDialog"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectParentScreen(
    navController: NavHostController,
    buildViewModel: BuildItemViewModel,
    viewModel: CustomListsViewModel = hiltViewModel()
) {
    Scaffold() { paddingValues ->
        ParentSelectUI(
            navController = navController,
            viewModel = viewModel,
            paddingValues = paddingValues,
            buildViewModel = buildViewModel
        )
    }
}

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalAnimationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ParentSelectUI(
    navController: NavHostController,
    viewModel: CustomListsViewModel,
    paddingValues: PaddingValues,
    buildViewModel: BuildItemViewModel
) {
    val context = LocalContext.current
    val resource = ResourceProviderSingleton
    val itemTree = viewModel.itemTree.collectAsState()
    var searchText by remember {
        mutableStateOf(Constants.EMPTY_STRING)
    }
    var expanded by remember {
        mutableStateOf(false)
    }
    val selectedParent = viewModel.selectedParent.collectAsState()
    Scaffold(
        modifier = Modifier.padding(paddingValues),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.set_or_change_parent),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    Row() {
                        IconButton(
                            onClick = {
                                viewModel.sendPKToBuildViewModel(
                                    selectedParent.value,
                                    buildViewModel
                                )
                                Toast.makeText(
                                    context,
                                    buildString {
                                        append(resource.getString(R.string.saving_as_the_new_parent))
                                        append(selectedParent.value.name)
                                    },
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = "save",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            },
                        )
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            },
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.changing_an_item_s_parent_will_also_move_any_of_the_item_s_children)
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    modifier = Modifier
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
                        androidx.compose.material.ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded,
                            onIconClick = {
                                searchText = Constants.EMPTY_STRING
                                expanded = !expanded
                            }
                        )
                    },
                )
            }
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
                        Row(modifier = Modifier.fillMaxWidth()) {
                            ExposedDropdownMenuBox(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                expanded = expanded,
                                onExpandedChange = {
                                    expanded = !expanded
                                }
                            ) {
                                CustomDropDownMenu(
                                    modifier = Modifier
                                        .fillMaxWidth(.75f),
                                    isOpen = expanded,
                                    setIsOpen = {
                                        expanded = !expanded
                                    },
                                    itemSelected = viewModel::selectParentItem,
                                    menu = getMenu(itemTree.value)
                                )
                            }
                        }
                    }
                }
            }
            Row() {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // filter options based on text field value
                    val filteringOptions: MutableList<ReflexionArrayItem> = mutableListOf()
                    if (searchText != Constants.EMPTY_STRING) {
                        itemTree.value.let { abridgedParent ->
                            // Breadth first Search for search item
                            ReflexionArrayItem.traverseBreadthFirst(itemTree.value) { RAI ->
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
                                            viewModel.selectParentItem(filteringOptions[it].itemPK.toString())
                                            searchText = Constants.EMPTY_STRING
                                        }
                                        .align(Alignment.CenterHorizontally),
                                    shape = MaterialTheme.shapes.extraLarge,
                                    elevation = CardDefaults.elevatedCardElevation(4.dp)
                                ) {
                                    Text(
                                        text = filteringOptions[it].itemName
                                            ?: stringResource(R.string.no_match_found),
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .padding(16.dp, 2.dp)
                                            .clickable {
                                                viewModel.selectParentItem(filteringOptions[it].itemPK.toString())
                                            }
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}


fun getItems(
    reflexionArrayItem: ReflexionArrayItem,
    newBuilder: DropDownMenuBuilder<String>
): DropDownMenuBuilder<String> {
    newBuilder.item(
        reflexionArrayItem.itemPK.toString(),
        reflexionArrayItem.itemName.toString()
    ) {
        icon(Icons.TwoTone.Lan)
        reflexionArrayItem.children.forEach { reflexionArrayItem ->
            getItems(reflexionArrayItem, this)
        }
    }
    return newBuilder
}


fun getMenu(
    tree: ReflexionArrayItem
): MenuItem<String> {
    val menu = dropDownMenu<String> {
        getItems(tree, this)
    }
    return menu
}
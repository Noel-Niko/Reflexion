//package com.livingtechusa.reflexion.ui.customLists
//
//import android.widget.Toast
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material.DropdownMenuItem
//import androidx.compose.material.ExperimentalMaterialApi
//import androidx.compose.material.ExposedDropdownMenuBox
//import androidx.compose.material.ExposedDropdownMenuDefaults
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.Scaffold
//import androidx.compose.material.Text
//import androidx.compose.material.TextField
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.RectangleShape
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavHostController
//import androidx.recyclerview.widget.RecyclerView
//import androidx.window.core.layout.WindowWidthSizeClass
//import com.livingtechusa.reflexion.navigation.NavBarItems
//import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
//import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
//import com.livingtechusa.reflexion.util.extensions.findActivity
//
//const val BuildCustomList = "build_custom_lists"
//
//@Composable
//fun BuildCustomListsScreen(
//    navController: NavHostController,
//    windowSize: WindowWidthSizeClass,
//    viewModel: CustomListsViewModel = hiltViewModel(),
//) {
//    val context = LocalContext.current
//    val icons = NavBarItems.CustomListsBarItems
//
//    if (context.findActivity() != null) {
//        when (windowSize) {
//            WindowWidthSizeClass.COMPACT -> {
//                CustomListCompactScreen(
//                    navController = navController,
//                    icons = icons,
//                    viewModel = viewModel,
//                )
//            }
//
//            WindowWidthSizeClass.MEDIUM -> {
//                // todo
//            }
//
////            WindowWidthSizeClass.EXPANDED -> {
////                ExpandedScreen(navHostController, icons, viewModel)
////                viewModel.navigationType = ReflexionNavigationType.PERMANENT_NAVIGATION_DRAWER
////            }
//
//            else -> CustomListCompactScreen(
//                navController = navController,
//                icons = icons,
//                viewModel = viewModel,
//            )
//        }
//    }
//}
//
//
//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun CustomListsContent(
//    navController: NavHostController, viewModel: CustomListsViewModel, paddingValues: PaddingValues
//) {
//
//    val listItems = viewModel.topicsList.collectAsState()
//    Scaffold(modifier = Modifier.padding(paddingValues)) { innerPadding ->
//        Spacer(Modifier.height(16.dp))
//        Row(Modifier.fillMaxWidth()) {
//            Box(
//                modifier = Modifier
//                    .align(
//                        Alignment.CenterVertically,
//                    )
//                    .padding(innerPadding)
//                    .background(color = MaterialTheme.colors.background)
//            ) {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier
//                        .border(
//                            1.dp,
//                            Color.Black,
//                            RectangleShape
//                        )
//                        .fillMaxWidth()
//                        .padding(20.dp)
//                ) {
//                    var selectedItem by remember { mutableStateOf("") }
//                    var expanded by remember { mutableStateOf(false) }
//                    ExposedDropdownMenuBox(
//                        modifier = Modifier
//                            .fillMaxWidth(),
//                        expanded = expanded,
//                        onExpandedChange = {
//                            expanded = !expanded
//                        }
//                    ) {
//                        TextField(
//                            modifier = Modifier
//                                .align(Alignment.CenterHorizontally),
//                            //.offset(100.dp),
//                            value = selectedItem,
//                            onValueChange = { selectedItem = it },
//                            label = { Text(text = "Topic") },
//                            trailingIcon = {
//                                ExposedDropdownMenuDefaults.TrailingIcon(
//                                    expanded = expanded
//                                )
//                            },
//                            colors = ExposedDropdownMenuDefaults.textFieldColors()
//                        )
//
//                        // filter options based on text field value
//                        val filteringOptions =
//                            listItems.value.filter { it.name.contains(selectedItem, ignoreCase = true) }
//
//                        if (filteringOptions.isNotEmpty()) {

//                            ExposedDropdownMenu(
//                                expanded = expanded,
//                                onDismissRequest = { expanded = false }
//                            ) {
//                                filteringOptions.forEach { selectionOption ->
//                                    DropdownMenuItem(
//                                        onClick = {
//                                            selectedItem = selectionOption.name
//                                            expanded = false
//                                        }
//                                    ) {
//                                        Text(text = selectionOption.name)
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//

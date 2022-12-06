package com.livingtechusa.reflexion.ui.build

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
import kotlinx.coroutines.launch


@Composable
fun drawerNavContent(
    navHostController: NavHostController, itemViewModel: ItemViewModel, reflexionItem: ReflexionItem
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val resource = ResourceProviderSingleton
    //val scaffoldState = rememberScaffoldState()
    Row(
        Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .align(
                    Alignment.CenterVertically,
                )
                .background(color = MaterialTheme.colorScheme.onSurface)
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .border(
                        1.dp, Color.Black, RectangleShape
                    )
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(16.dp))
                /* SIBLINGS */
                Text(
                    modifier = Modifier.clickable(onClick = {
                        if (reflexionItem.parent != null) {
                            scope.launch {
                                val hasSiblings =
                                    itemViewModel.hasNoSiblings(reflexionItem.autogenPK, reflexionItem.parent ?: 0L)
                                navHostController.navigate(Screen.ListScreen.route + "/" + reflexionItem.parent) {
                                    popUpTo(navHostController.graph.findStartDestination().id) {
//                                    saveState = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                            //scaffoldState.drawerState.isClosed
                        } else {
                            Toast.makeText(
                                context,
                                resource.getString(R.string.no_siblings_found),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }),
                    text = stringResource(R.string.siblings),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(16.dp))/* CHILDREN */
                Text(
                    modifier = Modifier.clickable(onClick = {
                        scope.launch {
                            val noChildren =
                                itemViewModel.hasNoChildren(reflexionItem.autogenPK)
                            if (noChildren) {
                                Toast.makeText(
                                    context,
                                    resource.getString(R.string.no_children_found),
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {
                                navHostController.navigate(Screen.ListScreen.route + "/" + reflexionItem.autogenPK) {
                                    popUpTo(navHostController.graph.findStartDestination().id) {  }
                                    launchSingleTop = true
                                }
                            }
                        }
                    }),
                    text = stringResource(R.string.children),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(16.dp))
                /* ADD SIBLING */
                Text(
                    modifier = Modifier.clickable(onClick = {
                        val parent = reflexionItem.parent
                        navHostController.navigate(Screen.BuildItemScreen.route)
                        itemViewModel.onTriggerEvent(BuildEvent.ClearReflexionItem)
                        if (parent != null) {
                            itemViewModel.onTriggerEvent(BuildEvent.SetParent(parent))
                            //scaffoldState.drawerState.isClosed
                        } else {
                            Toast.makeText(
                                context,
                                resource.getString(R.string.no_parent),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }),
                    text = stringResource(R.string.add_sibling),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(16.dp))
                /* ADD CHILD */
                Text(
                    modifier = Modifier.clickable(onClick = {
                        val parent = reflexionItem.autogenPK
                        itemViewModel.onTriggerEvent(BuildEvent.ClearReflexionItem)
                        itemViewModel.onTriggerEvent(BuildEvent.SetParent(parent))
                        navHostController.navigate(Screen.BuildItemScreen.route)
                        //scaffoldState.drawerState.isClosed
                    }),
                    text = stringResource(R.string.add_child),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(16.dp))
                /* DELETE ITEM */
                Text(
                    modifier = Modifier.clickable(onClick = {
                        scope.launch {
                            val noChildren =
                                itemViewModel.hasNoChildren(reflexionItem.autogenPK)
                            if (noChildren) {
                                itemViewModel.onTriggerEvent(BuildEvent.Delete)
                                navHostController.navigate(Screen.BuildItemScreen.route)
                            } else {
                                Toast.makeText(
                                    context,
                                    resource.getString(R.string.is_parent),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }),
                    text = stringResource(R.string.delete),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(16.dp))/* NEW ITEM */
                Text(
                    modifier = Modifier.clickable(onClick = {
                        itemViewModel.onTriggerEvent(BuildEvent.ClearReflexionItem)
                        //scaffoldState.drawerState.isClosed
                    }),
                    text = stringResource(R.string.new_item),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(16.dp))/* MOVE TO PARENT */
                Text(
                    modifier = Modifier.clickable(onClick = {
                        val parent = reflexionItem.parent
                        if (parent != null) {
                            itemViewModel.onTriggerEvent(BuildEvent.ClearReflexionItem)
                            itemViewModel.onTriggerEvent(
                                BuildEvent.GetSelectedReflexionItem(
                                    parent
                                )
                            )
                            navHostController.navigate(Screen.BuildItemScreen.route)
                        } else {
                            Toast.makeText(
                                context,
                                resource.getString(R.string.no_parent_found),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }),
                    text = stringResource(R.string.go_to_parent),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}

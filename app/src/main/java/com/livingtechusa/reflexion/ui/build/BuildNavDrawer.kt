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
import androidx.compose.material.ScaffoldState
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
import com.livingtechusa.reflexion.ui.viewModels.BuildItemViewModel
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
import kotlinx.coroutines.launch


@Composable
fun DrawerNavContent(
    navHostController: NavHostController,
    viewModel: BuildItemViewModel,
    reflexionItem: ReflexionItem,
    scaffoldState: ScaffoldState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val resource = ResourceProviderSingleton
    Row(
        Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .align(
                    Alignment.CenterVertically,
                )
                .background(color = MaterialTheme.colorScheme.inverseSurface)
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
                        scope.launch {
                            val hasNoSiblings =
                                viewModel.hasNoSiblings(
                                    reflexionItem.autogenPk,
                                    reflexionItem.parent ?: 0L
                                )
                            if (reflexionItem.parent != null || hasNoSiblings.not()) {
                                navHostController.navigate(Screen.TopicScreen.route + "/" + reflexionItem.parent) {
                                    popUpTo(navHostController.graph.findStartDestination().id) {
                                    }
                                    launchSingleTop = true
                                }
                                scaffoldState.drawerState.close()
                            } else {
                                Toast.makeText(
                                    context,
                                    resource.getString(R.string.no_siblings_found),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }),
                    text = stringResource(R.string.siblings),
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(16.dp))/* CHILDREN */
                Text(
                    modifier = Modifier.clickable(onClick = {
                        scope.launch {
                            val hasNoChildren =
                                viewModel.hasNoChildren(reflexionItem.autogenPk)
                            if (hasNoChildren) {
                                Toast.makeText(
                                    context,
                                    resource.getString(R.string.no_children_found),
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {
                                navHostController.navigate(Screen.TopicScreen.route + "/" + reflexionItem.autogenPk) {
                                    popUpTo(navHostController.graph.findStartDestination().id) { }
                                    launchSingleTop = true
                                }
                                scaffoldState.drawerState.close()
                            }
                        }
                    }),
                    text = stringResource(R.string.children),
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(16.dp))
                /* ADD SIBLING */
                Text(
                    modifier = Modifier.clickable(onClick = {
                        val parent = reflexionItem.parent
                        viewModel.onTriggerEvent(BuildEvent.ClearReflexionItem)
                        if (parent != null) {
                            viewModel.onTriggerEvent(BuildEvent.SetParent(parent))
                            scope.launch {
                                scaffoldState.drawerState.close()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                resource.getString(R.string.no_parent),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }),
                    text = stringResource(R.string.add_sibling),
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(16.dp))
                /* ADD CHILD */
                Text(
                    modifier = Modifier.clickable(onClick = {
                        val parent = reflexionItem.autogenPk
                        viewModel.onTriggerEvent(BuildEvent.ClearReflexionItem)
                        viewModel.onTriggerEvent(BuildEvent.SetParent(parent))
                        scope.launch {
                            scaffoldState.drawerState.close()
                        }
                    }),
                    text = stringResource(R.string.add_child),
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(16.dp))
                /* DELETE ITEM */
                Text(
                    modifier = Modifier.clickable(onClick = {
                        scope.launch {
                            val noChildren =
                                viewModel.hasNoChildren(reflexionItem.autogenPk)
                            if (noChildren) {
                                viewModel.onTriggerEvent(BuildEvent.Delete)
                                scaffoldState.drawerState.close()
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
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(16.dp))/* NEW ITEM */
                Text(
                    modifier = Modifier.clickable(onClick = {
                        viewModel.onTriggerEvent(BuildEvent.ClearReflexionItem)
                       scope.launch {
                           scaffoldState.drawerState.close()
                       }
                    }),
                    text = stringResource(R.string.new_item),
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(16.dp))/* MOVE TO PARENT */
                Text(
                    modifier = Modifier.clickable(onClick = {
                        val parent = reflexionItem.parent
                        if (parent != null) {
                            viewModel.onTriggerEvent(BuildEvent.ClearReflexionItem)
                            viewModel.onTriggerEvent(
                                BuildEvent.GetSelectedReflexionItem(
                                    parent
                                )
                            )
                            scope.launch {
                                scaffoldState.drawerState.close()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                resource.getString(R.string.no_parent_found),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }),
                    text = stringResource(R.string.go_to_parent),
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    style = MaterialTheme.typography.headlineSmall
                )
                /* CHANGE OR SET ITEM PARENT */
                Spacer(Modifier.height(16.dp))
                Text(
                    modifier = Modifier.clickable(onClick = {
                        navHostController.navigate(Screen.SelectParentScreen.route)
                    }),
                    text = stringResource(R.string.set_or_change_parent),
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}
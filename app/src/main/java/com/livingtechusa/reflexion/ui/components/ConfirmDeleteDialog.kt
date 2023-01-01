package com.livingtechusa.reflexion.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.customLists.CustomListEvent
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel

const val CONFIRM_DELETE = "ConfirmDeleteDialog"
@Composable
fun ConfirmDeleteDialog(
    viewModel: CustomListsViewModel = hiltViewModel(),
    navController: NavHostController,
    index: Int?,
    listName: String?
) {
    //MaterialTheme {
        Column {
            val openDialog = remember { mutableStateOf(true) }
            if(openDialog.value) {
                AlertDialog(
                    onDismissRequest = { openDialog.value = false },
                    title = {
                        Text(text = stringResource(R.string.confirm_to_delete_list))
                    },
                    text = {
                        Text("Do you want to delete $listName")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                index?.let { CustomListEvent.DeleteList(it) }
                                    ?.let { viewModel.onTriggerEvent(it) }
                                openDialog.value = false
                                navController.popBackStack()
                            }) {
                            Text(stringResource(R.string.delete))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                openDialog.value = false
                                navController.popBackStack()
                            }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )
            }
        }
    }
//}
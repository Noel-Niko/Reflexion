package com.livingtechusa.reflexion.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.ui.customLists.CustomListEvent
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import com.livingtechusa.reflexion.util.ResourceProviderSingleton.getString

const val CONFIRM_DELETE_LIST = "ConfirmDeleteListDialog"
@Composable
fun ConfirmDeleteListDialog(
    viewModel: CustomListsViewModel = hiltViewModel(),
    navController: NavHostController,
    index: Int?,
    itemToDelete: String?
) {
    //MaterialTheme {
        Column {
            val openDialog = remember { mutableStateOf(true) }
            if(openDialog.value) {
                AlertDialog(
                    onDismissRequest = { openDialog.value = false },
                    title = {
                        Text(text = stringResource(R.string.confirm_to_delete))
                    },
                    text = {
                        Text(text = getString(R.string.do_you_want_to_delete, itemToDelete ?: EMPTY_STRING))
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                index?.let { CustomListEvent.DeleteList(it) }
                                    ?.let { viewModel.onTriggerEvent(it) }
                                openDialog.value = false
                                navController.popBackStack()
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor =  MaterialTheme.colorScheme.primary, contentColor =  MaterialTheme.colorScheme.onPrimary)
                        ) {
                            Text(text = stringResource(R.string.delete), color = MaterialTheme.colorScheme.onPrimary)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                openDialog.value = false
                                navController.popBackStack()
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor =  MaterialTheme.colorScheme.primary, contentColor =  MaterialTheme.colorScheme.onPrimary)
                        ) {
                            Text(text = stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                )
            }
        }
    }
//}
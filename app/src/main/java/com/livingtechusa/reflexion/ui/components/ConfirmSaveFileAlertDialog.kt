package com.livingtechusa.reflexion.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.ui.viewModels.BuildItemViewModel
import com.livingtechusa.reflexion.util.Constants.JSON
import com.livingtechusa.reflexion.util.Constants.USE_TOP_ITEM
import com.livingtechusa.reflexion.util.Constants.ZIP
import com.livingtechusa.reflexion.util.TemporarySingleton

const val CONFIRM_SAVE_FILE = "ConfirmSaveFileAlertDialog"

@Composable
fun ConfirmSaveFileAlertDialog(
    type: String,
    navController: NavHostController,
    viewModel: BuildItemViewModel = hiltViewModel()
) {
    val filePath = TemporarySingleton.file
    MaterialTheme {
        Column {
            val openDialog = remember { mutableStateOf(true) }
            if (openDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog when the user clicks outside the dialog or on the back
                        // button. If you want to disable that functionality, simply use an empty
                        // onCloseRequest.
                        openDialog.value = false
                    },
                    title = {
                        Text(text = stringResource(R.string.confirm_save_file))
                    },
                    text = {
                        Text(stringResource(R.string.do_you_want_to_save_the_contents_of) + filePath.toString())
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                openDialog.value = false
                                navController.navigate(Screen.BuildItemScreen.route + "/" + USE_TOP_ITEM)
                                if (type == ZIP) {
                                    viewModel.onTriggerEvent(BuildEvent.SaveAndDisplayZipFile)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                stringResource(R.string.yes),
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                openDialog.value = false
                                navController.navigate(Screen.HomeScreen.route) {
                                    launchSingleTop = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                stringResource(R.string.no),
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            }
        }
    }
}
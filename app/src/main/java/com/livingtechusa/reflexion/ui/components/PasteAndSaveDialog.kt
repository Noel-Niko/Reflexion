package com.livingtechusa.reflexion.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.ui.viewModels.BuildItemViewModel
import com.livingtechusa.reflexion.util.Constants.DO_NOT_UPDATE
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import com.livingtechusa.reflexion.util.Constants.VIDEO_URL

const val PASTE_SAVE = "PasteAndSaveDialog"

@Composable
fun PasteAndSaveDialog(
    viewModel: BuildItemViewModel = hiltViewModel(),
    navController: NavHostController
) {

    val webAddress = remember { mutableStateOf(EMPTY_STRING) }

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
                        Text(text = stringResource(R.string.paste_web_address_here))
                    },
                    text = {
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = webAddress.value ?: "",
                            onValueChange = {
                                webAddress.value = it
                            }
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.onTriggerEvent(
                                    BuildEvent.UpdateDisplayedReflexionItem(
                                        subItem = VIDEO_URL, newVal = webAddress.value
                                    )
                                )
                                openDialog.value = false
                                navController.navigate(Screen.BuildItemScreen.route + "/" + DO_NOT_UPDATE) {
                                    launchSingleTop = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                stringResource(R.string.save),
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                openDialog.value = false
                                navController.navigate(Screen.BuildItemScreen.route + "/" + DO_NOT_UPDATE) {
                                    launchSingleTop = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                stringResource(R.string.close),
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            }
        }
    }
}
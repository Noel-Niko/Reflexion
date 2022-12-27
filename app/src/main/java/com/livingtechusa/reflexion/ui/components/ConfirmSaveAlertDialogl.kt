package com.livingtechusa.reflexion.ui.components


import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.MainActivity
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.ui.build.BuildRoute
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import com.livingtechusa.reflexion.util.Temporary

const val CONFIRM_SAVE = "ConfirmSaveAlertDialog"

@Composable
fun ConfirmSaveAlertDialog(
    navController: NavHostController,
    viewModel: ItemViewModel = hiltViewModel()
) {
    val url = Temporary.url
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
                        Text(text = stringResource(R.string.confirm_to_add_content_link))
                    },
                    text = {
                        Text("Do you want to add: $url")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.onTriggerEvent(BuildEvent.UpdateVideoURL(url ?: EMPTY_STRING))
                                openDialog.value = false
                                navController.navigate(Screen.BuildItemScreen.route + "/" + -2L)
                            }) {
                            Text(stringResource(R.string.yes))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                Temporary.url = EMPTY_STRING
                                openDialog.value = false
                                navController.navigate(Screen.BuildItemScreen.route + "/" + -1L)
                            }) {
                            Text(stringResource(R.string.no))
                        }
                    }
                )
            }
        }
    }
}
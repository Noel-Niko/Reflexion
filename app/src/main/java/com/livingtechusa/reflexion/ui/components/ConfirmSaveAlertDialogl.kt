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
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.ui.build.BuildRoute
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.Temporary

const val CONFIRM_SAVE = "ConfirmSaveAlertDialog"

@Composable
fun ConfirmSaveAlertDialog(
    navController: NavHostController
) {

    val urlString = Temporary.url
    MaterialTheme {
        val context = LocalContext.current
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
                        Text("Do you want to add: $urlString")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                openDialog.value = false
                                Temporary.url = urlString
                                Temporary.tempReflexionItem.videoUrl = urlString
                                Temporary.use = true
                                navController.navigate(BuildRoute)
                            }) {
                            Text(stringResource(R.string.yes))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                openDialog.value = false
                                startActivity(
                                    context,
                                    Intent(context, MainActivity::class.java),
                                    null
                                )
                            }) {
                            Text(stringResource(R.string.no))
                        }
                    }
                )
            }
        }
    }
}
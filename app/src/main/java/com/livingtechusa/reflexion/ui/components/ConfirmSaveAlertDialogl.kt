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
import com.livingtechusa.reflexion.MainActivity
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.util.Constants


@Composable
fun ConfirmSaveAlertDialog(saveAbleText: String) {
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
                        Text(text = "Confirm to add content link")
                    },
                    text = {
                        Text("Do you want to add: $saveAbleText")
                    },
                    confirmButton = {
                        Button(

                            onClick = {
                                openDialog.value = false
                                val intent = Intent(
                                    context,
                                    MainActivity::class.java
                                ).putExtra(Constants.SAVE_URL, saveAbleText)
                                startActivity(context, intent, null)
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
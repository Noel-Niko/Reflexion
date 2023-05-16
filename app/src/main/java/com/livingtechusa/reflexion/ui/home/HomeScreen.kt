package com.livingtechusa.reflexion.ui.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.ui.viewModels.BuildItemViewModel
import com.livingtechusa.reflexion.util.Constants
import com.livingtechusa.reflexion.util.extensions.findActivity
import com.livingtechusa.reflexion.util.sharedPreferences.UserPreferencesUtil
import com.livingtechusa.reflexion.util.sharedPreferences.UserPreferencesUtil.PREFERENCE_TYPE


const val HOME = "home"

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: BuildItemViewModel
) {
    val context = LocalContext.current
    val icons = NavBarItems.HomeBarItems
    if (context.findActivity() != null) {
        CompactScreen(navHostController, icons, viewModel = viewModel)
    }
}

@Composable
fun HomeContent(paddingValues: PaddingValues, viewModel: BuildItemViewModel) {
    val context = LocalContext.current
    val launchCount = UserPreferencesUtil.getPopUpViews(context)
    val showAlertDialog = launchCount < 4
    val openDialog = remember { mutableStateOf(true) }

    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    // If `lifecycleOwner` changes, dispose and reset the effect
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                // Increment launch count and save to SharedPreferences
                UserPreferencesUtil.setPopUpViews(context, launchCount + 1)
            }
        }
        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)
        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(paddingValues),
        horizontalAlignment = Alignment.Start
    ) {
        if (showAlertDialog && openDialog.value) {
            Popup(
                alignment = Alignment.TopEnd
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                ) {
                    AlertDialog(
                        icon = {
                            androidx.compose.material.Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "settings",
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        },
                        onDismissRequest = { openDialog.value = false },
                        title = { Text(stringResource(R.string.how_to_use_reflexion)) },
                        text = { Text(text = stringResource(R.string.click_settings_download_the_app_guide)) },
                        confirmButton = {
                            if (launchCount >= 2) {
                                Button(
                                    onClick = {
                                        UserPreferencesUtil.setPopUpViews(context, 5)
                                        openDialog.value = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Text(stringResource(R.string.do_not_show_again))
                                }
                            }
                        }
                    )
                }
            }
        }
        Row(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(16.dp)
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(8.dp))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .border(
                            1.dp,
                            Color.Black,
                            RoundedCornerShape(8.dp)
                        )
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.welcome_to_reflexion),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        lineHeight = 30.sp
                    )
                }
            }
        }
    }
}
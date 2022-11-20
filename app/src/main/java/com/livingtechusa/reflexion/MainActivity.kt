package com.livingtechusa.reflexion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.livingtechusa.reflexion.navigation.NavigationHosting
import com.livingtechusa.reflexion.ui.components.ConfirmSaveAlertDialog
import com.livingtechusa.reflexion.ui.theme.ReflexionTheme
import com.livingtechusa.reflexion.util.BaseApplication
import com.livingtechusa.reflexion.util.Constants
import com.livingtechusa.reflexion.util.MediaUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val Main_Activity = "Main_Activity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var app: BaseApplication

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReflexionTheme {
                val permissionsState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        android.Manifest.permission.RECORD_AUDIO,
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.INTERNET
                    )
                )
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(
                    key1 = lifecycleOwner,
                    effect = {
                        val observer = LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_START) {
                                permissionsState.launchMultiplePermissionRequest()
                                MediaUtil().verifyStoragePermission(this@MainActivity)
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)
                        onDispose {
                            lifecycleOwner.lifecycle.removeObserver(observer)
                        }
                    }
                )

            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (intent.extras?.get(Constants.URL) != null && intent?.extras?.get(Constants.URL) != Constants.EMPTY_STRING) {
            val url = intent.extras?.get(Constants.URL).toString()
            setContent {
                ConfirmSaveAlertDialog(url)
            }
        } else {
            setContent {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    var urlText: String? = null
                    if (intent.extras?.get(Constants.SAVE_URL) != null && intent?.extras?.get(
                            Constants.SAVE_URL
                        ).toString() != Constants.EMPTY_STRING
                    ) {
                        urlText = intent.extras?.get(Constants.SAVE_URL).toString()
                        NavigationHosting(urlText)
                    } else {
                        NavigationHosting(urlText)
                    }
                }
            }
        }
    }
}

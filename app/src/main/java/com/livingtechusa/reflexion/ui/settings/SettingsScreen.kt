package com.livingtechusa.reflexion.ui.settings

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowWidthSizeClass
import coil.compose.rememberImagePainter
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.ui.components.ImageCard
import com.livingtechusa.reflexion.ui.viewModels.SettingsViewModel
import com.livingtechusa.reflexion.util.Constants
import com.livingtechusa.reflexion.util.extensions.findActivity

const val SETTINGS = "settings"

@Composable
fun SettingsScreen(
    navHostController: NavHostController,
    windowSize: WindowWidthSizeClass,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer = LifecycleEventObserver { owner, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                viewModel.onTriggerEvent(SettingsEvent.getIconImages)
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val context = LocalContext.current
    val icons = NavBarItems.SettingsBarItems
    if (context.findActivity() != null) {
        when (windowSize) {
            WindowWidthSizeClass.COMPACT -> {
                CompactScreen(navHostController, icons, viewModel)
            }

//            WindowWidthSizeClass.MEDIUM -> {
//                Landscape(navHostController, icons)
//            }

//            WindowWidthSizeClass.EXPANDED -> {
//                ExpandedScreen(navHostController, icons)
//                viewModel.navigationType = ReflexionNavigationType.PERMANENT_NAVIGATION_DRAWER
//            }

            else -> CompactScreen(navHostController, icons, viewModel)
        }
    }
}

@Composable
fun IconImageCard(
    image: Bitmap,
    iconNumber: Int,
    totalIndices: Int
) {
    val context = LocalContext.current
    val screenPixelDensity = context.resources.displayMetrics.density
    val size = (512f / 2) / screenPixelDensity
    val imagePainter = rememberImagePainter(
        data = image,
        builder = {
            allowHardware(false)
        }
    )
    OutlinedCard(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .height(size.dp)
            .width(size.dp)
            .padding(4.dp)
            .background(Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Image(
            painter = imagePainter,
            contentDescription = "Icon Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                //.rotate(90f)
                .clickable(
                    onClick = {
                        showNewIconImage(context = context, int = iconNumber, totalIndices)
                    }
                )
        )
    }
}

@Composable
fun HorizontalScrollableIconRowComponent(
    list: List<Bitmap>,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(list.size) { listitemIndex ->
                if (list.isEmpty().not() && list[listitemIndex] != null) {
                    IconImageCard(list[listitemIndex]!!, listitemIndex, list.size)
                }
            }
        }
    }
}

fun showNewIconImage(context: Context, int: Int, totalIndices: Int) {

    val packageManager = context.packageManager

    // enable new icon
    packageManager.setComponentEnabledSetting(
        ComponentName(
            context,
            "com.livingtechusa.reflexion.MainActivityAlias${int.toString()}"
        ),
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP
    )

    // address original  icon
    packageManager.setComponentEnabledSetting(
        ComponentName(
            context,
            "com.livingtechusa.reflexion.MainActivity"
        ),
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER,
        PackageManager.DONT_KILL_APP
    )

    var count = 0
    while (count <= (totalIndices - 1)) {
        if (count != int) {
            // disable each other icon
            packageManager.setComponentEnabledSetting(
                ComponentName(
                    context,
                    "com.livingtechusa.reflexion.MainActivityAlias$count"
                ),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }
        count++
    }
    Toast
        .makeText(
            context, "Icon Selected",
            Toast.LENGTH_LONG
        )
        .show()
}

@Composable
fun settingsContent(paddingValues: PaddingValues, viewModel: SettingsViewModel) {
    val bitmapList by viewModel.iconImages.collectAsState()

    Scaffold(Modifier.padding(paddingValues = paddingValues)) {
        LazyColumn(
            modifier = Modifier.padding(paddingValues = it),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(5) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Box {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            elevation = 10.dp,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.fillMaxWidth(.3f)) {
                                    Text(
                                        text = stringResource(R.string.app_icon_images),
                                        modifier = Modifier
                                            .padding(8.dp),
                                        style = MaterialTheme.typography.subtitle2
                                    )
                                }
                                Column(modifier = Modifier.fillMaxWidth(1f)) {
                                    HorizontalScrollableIconRowComponent(bitmapList)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.livingtechusa.reflexion.ui.settings

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowWidthSizeClass
import coil.compose.rememberImagePainter
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.ui.components.MaterialRadioButtonGroupComponent
import com.livingtechusa.reflexion.ui.viewModels.SettingsViewModel
import com.livingtechusa.reflexion.util.extensions.findActivity


const val SETTINGS = "settings"

@Composable
fun SettingsScreen(
    navHostController: NavHostController,
    windowSize: WindowWidthSizeClass,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val isDark = isSystemInDarkTheme()
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer = LifecycleEventObserver { owner, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                viewModel.onTriggerEvent(SettingsEvent.getIconImages)
                viewModel.setIsDark(isDark)
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
            .padding(4.dp),
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
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}

@Composable
fun HorizontalScrollableIconRowComponent(
    list: List<Bitmap>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .background(MaterialTheme.colorScheme.primary),
        verticalArrangement = Arrangement.Center
    ) {
        LazyRow(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(list.size) { listitemIndex ->
                if (list.isEmpty().not()) {
                    IconImageCard(list[listitemIndex], listitemIndex, list.size)
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

    // address original  icon - prevents reloading
//    packageManager.setComponentEnabledSetting(
//        ComponentName(
//            context,
//            "com.livingtechusa.reflexion.util.BaseApplication/roundIcon"
//        ),
//        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//        PackageManager.DONT_KILL_APP
//    )

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun settingsContent(paddingValues: PaddingValues, viewModel: SettingsViewModel, navHostController: NavHostController) {
    val bitmapList by viewModel.iconImages.collectAsState()
    val context = LocalContext.current
    val isDark = viewModel.isDark.collectAsState()
    Scaffold(Modifier.padding(paddingValues = paddingValues)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = it), //.background(MaterialTheme.colorScheme.primaryContainer)
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Icons
            val screenPixelDensity = context.resources.displayMetrics.density
            val height = (800f / 2) / screenPixelDensity
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height.dp)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart
                ) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .align(Alignment.Center),
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.elevatedCardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(.3f)
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                            ) {
                                Text(
                                    text = stringResource(R.string.app_icon_images),
                                    modifier = Modifier
                                        .padding(8.dp),
                                    //style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                HorizontalScrollableIconRowComponent(bitmapList)
                            }
                        }
                    }
                }
            }

            // Theme
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((height * 3).dp)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart,
                ) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .align(Alignment.Center),
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.elevatedCardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            var selected by remember { mutableStateOf(false) }
                            Column(modifier = Modifier.fillMaxWidth(.3f)) {
                                Text(
                                    text = stringResource(R.string.select_theme_color_restart_app),
                                    modifier = Modifier.padding(8.dp),
                                    //style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            val themeMap = mapOf<String, Int>(
                                "Theme 1" to 1,
                                "Theme 2" to 2,
                                "Theme 3" to 3,
                                "Theme 4" to 4,
                                "Theme 5" to 5,
                                "Theme 6" to 6,
                                stringResource(R.string.default_derived_from_your_wallpaper_selection) to -1
                            )
                            MaterialRadioButtonGroupComponent(themeMap, viewModel::setTheme)
                        }
                    }
                }
            }

            // Light / Dark Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart,
                ) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .align(Alignment.Center),
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.elevatedCardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val items = (0..3)
                            var activeTabIndex by remember { mutableStateOf(0) }

                            TabRow(
                                selectedTabIndex = activeTabIndex,
                                containerColor = Color.Transparent,
                                indicator = {
                                    Box(
                                        Modifier
                                            .tabIndicatorOffset(it[activeTabIndex])
                                            .fillMaxSize()
                                            .background(color = MaterialTheme.colorScheme.primary)
                                            .zIndex(-1F)
                                    )
                                },
                            ) {
                                items.mapIndexed { i, item ->
                                    Tab(
                                        selected = activeTabIndex == i,
                                        onClick = { activeTabIndex = i }
                                    ) {
                                        if (i == 0) {
                                            Text(
                                                modifier = Modifier.padding(4.dp),
                                                text = stringResource(R.string.light_theme),
                                                color = if (0 == activeTabIndex) {
                                                    MaterialTheme.colorScheme.onPrimary

                                                } else {
                                                    MaterialTheme.colorScheme.onSecondaryContainer
                                                }
                                            )
                                            if (0 == activeTabIndex && isDark.value) {
                                                viewModel.onTriggerEvent(SettingsEvent.setMode(false))
                                                viewModel.toggleLightDarkMode(
                                                    navHostController,
                                                    isDark.value
                                                )
                                            }
                                        }
                                        if (i == 1) {
                                            Text(
                                                modifier = Modifier.padding(4.dp),
                                                text = stringResource(R.string.dark_theme),
                                                color = if (1 == activeTabIndex) {
                                                    MaterialTheme.colorScheme.onPrimary
                                                } else {
                                                    MaterialTheme.colorScheme.onSecondaryContainer
                                                }
                                            )
                                            if (1 == activeTabIndex && isDark.value.not()) {
                                                viewModel.onTriggerEvent(SettingsEvent.setMode(true))
                                                viewModel.toggleLightDarkMode(
                                                    navHostController,
                                                    isDark.value
                                                )
                                            }
                                            if (i == 2) {
                                                Text(
                                                    modifier = Modifier.padding(4.dp),
                                                    text = stringResource(R.string.match_phone),
                                                    color = if (2 == activeTabIndex) {
                                                        MaterialTheme.colorScheme.onPrimary
                                                    } else {
                                                        MaterialTheme.colorScheme.onSecondaryContainer
                                                    }
                                                )
                                                if (1 == activeTabIndex && isDark.value.not()) {
                                                    viewModel.onTriggerEvent(
                                                        SettingsEvent.setMode(
                                                            true
                                                        )
                                                    )
                                                    viewModel.toggleLightDarkMode(
                                                        navHostController,
                                                        isDark.value
                                                    )
                                                }
                                            }
                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_camera_24),
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.padding(vertical = 20.dp)
                                            )
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}
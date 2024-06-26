package com.livingtechusa.reflexion.ui.settings

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.ui.components.MaterialRadioButtonGroupComponent
import com.livingtechusa.reflexion.ui.viewModels.SettingsViewModel
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
import com.livingtechusa.reflexion.util.extensions.findActivity
import com.livingtechusa.reflexion.util.sharedPreferences.UserPreferencesUtil


const val SETTINGS = "settings"

@Composable
fun SettingsScreen(
    navHostController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                viewModel.onTriggerEvent(SettingsEvent.GetIconImages)
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
        CompactScreen(navHostController, icons, viewModel)
    }
}

@Composable
fun IconImageCard(
    image: Bitmap,
    iconNumber: Int,
    totalIndices: Int,
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val screenPixelDensity = context.resources.displayMetrics.density
    val size = (512f / 2) / screenPixelDensity
    val selectedIconIndex by viewModel.iconSelected.collectAsState()
    val isSelected = (selectedIconIndex == iconNumber)
    val outlineColor = if (isSelected) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.primary
    }
    val imagePainter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(data = image).apply(block = fun ImageRequest.Builder.() {
            allowHardware(false)
        }).build()
    )
    OutlinedCard(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .height(size.dp)
            .width(size.dp)
            .padding(4.dp),
        border = CardDefaults.outlinedCardBorder(isSelected),
        colors = CardDefaults.outlinedCardColors(containerColor = outlineColor)
    ) {
        Image(
            painter = imagePainter,
            contentDescription = "Icon Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    onClick = {
                        viewModel.icon = true
                        viewModel.iconNumber = iconNumber
                        viewModel.totalIndices = totalIndices
                        viewModel.onTriggerEvent(SettingsEvent.SetIconSelected(iconNumber))
                    }
                ),
            alignment = Alignment.Center
        )
    }
}

@Composable
fun HorizontalScrollableIconRowComponent(
    list: List<Bitmap>,
    viewModel: SettingsViewModel
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
                    IconImageCard(
                        list[listitemIndex],
                        listitemIndex,
                        list.size,
                        viewModel = viewModel
                    )
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    paddingValues: PaddingValues,
    viewModel: SettingsViewModel
) {
    val apply by viewModel.apply.collectAsState()
    val bitmapList by viewModel.iconImages.collectAsState()
    val startMode: Int =
        if (UserPreferencesUtil.getCurrentUserModeSelection(LocalContext.current) != -1) {
            UserPreferencesUtil.getCurrentUserModeSelection(LocalContext.current)
        } else {
            if (isSystemInDarkTheme()) 1 else 0
        }
    val context = LocalContext.current
    val resource = ResourceProviderSingleton
    Scaffold(Modifier.padding(paddingValues = paddingValues)) {
        if (apply) {
            if (viewModel.mode && viewModel.isDarkMode != null) {
                viewModel.isDarkMode?.let { isDark -> viewModel.ToggleLightDarkMode(isDark) }
            }
            viewModel.icon = false
            viewModel.iconNumber = null
            viewModel.theme = false
            viewModel.themeNumber = null
            viewModel.totalIndices = null
            viewModel.mode = false
            viewModel.isDarkMode = null
            viewModel.setApply(false)
            viewModel.restartApp()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = it)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Icons
            val screenPixelDensity = context.resources.displayMetrics.density
            val height = (768f / 2) / screenPixelDensity
            Row(
                Modifier
                    .fillMaxHeight(.15f)
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.background),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.background)
                            .fillMaxSize()
                    ) {"Didi"
                        val url: String = stringResource(R.string.https_sites_google_com_view_reflexionweb_home)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .align(Alignment.Center),
                                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = MaterialTheme.shapes.large,
                                elevation = CardDefaults.elevatedCardElevation(4.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.import_directions),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable(onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            try {
                                                startActivity(context, intent, null)
                                            } catch (e: Exception) {
                                                Toast.makeText(
                                                    context,
                                                    R.string.error_did_you_grant_internet_access_permission,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        })
                                )
                            }
                        }
                    }
                }
            }

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
                                HorizontalScrollableIconRowComponent(bitmapList, viewModel)
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
                            Column(modifier = Modifier.fillMaxWidth(.3f)) {
                                Text(
                                    text = stringResource(R.string.select_theme_color_restart_app),
                                    modifier = Modifier.padding(8.dp),
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
                            val items = (0..1)
                            var activeTabIndex by remember { mutableStateOf(startMode) }

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
                                items.mapIndexed { i, _ ->
                                    Tab(
                                        selected = activeTabIndex == i,
                                        onClick = {
                                            viewModel.mode = true
                                            activeTabIndex = i
                                        }
                                    ) {
                                        if (i == 0) {
                                            val color = if (0 == activeTabIndex) {
                                                MaterialTheme.colorScheme.onPrimary
                                            } else {
                                                MaterialTheme.colorScheme.onSecondaryContainer
                                            }
                                            Text(
                                                modifier = Modifier.padding(4.dp),
                                                text = stringResource(R.string.light_theme),
                                                color = color
                                            )
                                            if (0 == activeTabIndex) {
                                                viewModel.isDarkMode = false
                                            }
                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_camera_24),
                                                contentDescription = null,
                                                tint = color,
                                                modifier = Modifier.padding(vertical = 20.dp)
                                            )
                                        }
                                        if (i == 1) {
                                            val color = if (1 == activeTabIndex) {
                                                MaterialTheme.colorScheme.onPrimary
                                            } else {
                                                MaterialTheme.colorScheme.onSecondaryContainer
                                            }
                                            Text(
                                                modifier = Modifier.padding(4.dp),
                                                text = stringResource(R.string.dark_theme),
                                                color = color
                                            )
                                            if (1 == activeTabIndex) {
                                                viewModel.isDarkMode = true
                                            }
                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_camera_24),
                                                contentDescription = null,
                                                tint = color,
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

            // Apply Settings
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                //.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.Center),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primary),
                        elevation = CardDefaults.elevatedCardElevation(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.apply_changes),
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .clickable {
                                    if (viewModel.icon && (viewModel.iconNumber != null) && (viewModel.totalIndices != null)) {
                                        viewModel.iconNumber?.let { iconNum ->
                                            viewModel.totalIndices?.let { index ->
                                                showNewIconImage(
                                                    context = context,
                                                    int = iconNum,
                                                    totalIndices = index
                                                )
                                            }
                                        }
                                    }
                                    // Toggle for light/dark mode Set when collecting Apply State
                                    if (viewModel.theme && (viewModel.themeNumber != null)) {
                                        viewModel.themeNumber?.let { theme ->
                                            UserPreferencesUtil.setCurrentUserThemeSelection(
                                                context,
                                                theme
                                                )
                                        }
                                    }
                                    viewModel.setApply(true)
                                    Toast
                                        .makeText(
                                            context,
                                            resource.getString(R.string.applying_changes_and_restarting),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                        )
                    }
                }
            }

            // Reset settings
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                //.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.Center),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.elevatedCardElevation(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.app_reset),
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .clickable {
                                    UserPreferencesUtil.setCurrentUserModeSelection(context, -1)
                                    resetIconImage(context, bitmapList.size)
                                    UserPreferencesUtil.setCurrentUserThemeSelection(context, -1)
                                    viewModel.restartApp()
                                    Toast
                                        .makeText(
                                            context,
                                            resource.getString(R.string.applying_changes_and_restarting),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                })
                    }
                }
            }
        }
    }
}

fun resetIconImage(context: Context, iconListSize: Int) {
    val packageManager = context.packageManager

    // enable default icon
    packageManager.setComponentEnabledSetting(
        ComponentName(
            context,
            "com.livingtechusa.reflexion.MainActivity"
        ),
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP
    )

    var count = 0
    while (count <= (iconListSize - 1)) {
        // disable each other icon
        packageManager.setComponentEnabledSetting(
            ComponentName(
                context,
                "com.livingtechusa.reflexion.MainActivityAlias$count"
            ),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        count++
    }
    Toast
        .makeText(
            context, context.getString(R.string.app_reset),
            Toast.LENGTH_LONG
        )
        .show()
}

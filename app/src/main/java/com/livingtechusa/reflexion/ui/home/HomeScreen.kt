package com.livingtechusa.reflexion.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowWidthSizeClass
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.navigation.ReflexionNavigationType
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.extensions.findActivity

const val HOME = "home"

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    windowSize: WindowWidthSizeClass,
    viewModel: ItemViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val icons = NavBarItems.HomeBarItems
    if (context.findActivity() != null) {
        when (windowSize) {
            WindowWidthSizeClass.COMPACT -> {
                CompactScreen(navHostController, icons)
            }

            WindowWidthSizeClass.MEDIUM -> {
                Landscape(navHostController, icons)
            }

//            WindowWidthSizeClass.EXPANDED -> {
//                ExpandedScreen(navHostController, icons)
//                viewModel.navigationType = ReflexionNavigationType.PERMANENT_NAVIGATION_DRAWER
//            }

            else -> CompactScreen(navHostController, icons)
        }
    }
}

@Composable
fun homeContent() {
    Row(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(
                Alignment.CenterVertically,
                )
                .background(color = MaterialTheme.colors.background)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .border(
                        1.dp,
                        Color.Black,
                        RectangleShape
                    )
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                androidx.compose.material3.Text(
                    fontSize = 30.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    text = stringResource(R.string.welcome_to_reflexion),
                    color = MaterialTheme.colors.onBackground
                )
            }
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ExpandedScreen(navController: NavHostController, icons: List<BarItem>) {
//    val backStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = backStackEntry?.destination?.route
//    PermanentNavigationDrawer(
//        drawerContent = {
//            icons.forEach { navItem ->
//                NavigationDrawerItem(
//                    icon = {
//                        Icon(
//                            imageVector = navItem.image,
//                            contentDescription = navItem.title,
//                            tint = MaterialTheme.colorScheme.onBackground
//                        )
//                    },
//                    label = { Text(text = navItem.title) },
//                    selected = currentRoute == navItem.route,
//                    onClick = {
//                        navController.navigate(navItem.route) {
//                            popUpTo(navController.graph.findStartDestination().id) {
//                                saveState = true
//                            }
//                            launchSingleTop = true
//                            restoreState = true
//                        }
//                    }
//                )
//            }
//        },
//        content = {
//           homeContent()
//        }
//    )
//}
//

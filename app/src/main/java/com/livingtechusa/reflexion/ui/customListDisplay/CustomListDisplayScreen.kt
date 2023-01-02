package com.livingtechusa.reflexion.ui.customListDisplay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowWidthSizeClass
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.util.extensions.findActivity

const val CUSTOM_LIST_DISPLAY = "customListDisplay"

@Composable
fun CustomListDisplayScreen(
    navController: NavHostController,
    windowSize: WindowWidthSizeClass,
    viewModel: CustomListsViewModel = hiltViewModel(),
    headNodePk: Long
) {

    val context = LocalContext.current
    val icons = NavBarItems.CustomListsDisplayBarItems
    if (context.findActivity() != null) {
        when (windowSize) {
            WindowWidthSizeClass.COMPACT -> {
                CompactScreen(navController, icons)
            }

            WindowWidthSizeClass.MEDIUM -> {
                Landscape(navController, icons)
            }

//            WindowWidthSizeClass.EXPANDED -> {
//                ExpandedScreen(navHostController, icons)
//                viewModel.navigationType = ReflexionNavigationType.PERMANENT_NAVIGATION_DRAWER
//            }

            else -> CompactScreen(navController, icons)
        }
    }
}

@Composable
fun CustomListDisplayContent() {
    Row(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterVertically,)
                .padding(16.dp)
                .shadow(elevation = 12.dp,  shape = RoundedCornerShape(8.dp))
                .background(color = MaterialTheme.colors.background)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .border(
                        1.dp,
                        Color.Black,
                        RoundedCornerShape(8.dp)
                    )
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    fontSize = 30.sp,
                    fontStyle = MaterialTheme.typography.h6.fontStyle,
                    fontWeight = FontWeight.Bold,
                    text = stringResource(R.string.welcome_to_reflexion),
                    color = MaterialTheme.colors.onBackground
                )
            }
        }
    }
}

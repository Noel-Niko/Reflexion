package com.livingtechusa.reflexion.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowWidthSizeClass
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.NavBarItems
import com.livingtechusa.reflexion.util.extensions.findActivity

const val HOME = "home"

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    windowSize: WindowWidthSizeClass
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
fun HomeContent() {
    Row(
        Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)) {
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

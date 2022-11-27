package com.livingtechusa.reflexion.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.components.MainTopBar
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel

const val HOME = "home"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: ItemViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    Scaffold(
        topBar = { MainTopBar() },
    ) {
        it
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(32.dp))
            Button(onClick = {
                navHostController.navigate(Screen.BuildItemScreen.route)
            }
            ) {
                Text(stringResource(R.string.BuildScreen))
            }
//            Spacer(Modifier.height(16.dp))
//            Button(onClick = {
//                navHostController.navigate(Screen.ChildV2Screen.route + "/")
//            }
//            ) {
//                Text(stringResource(R.string.children))
//            }
        }

    }
}

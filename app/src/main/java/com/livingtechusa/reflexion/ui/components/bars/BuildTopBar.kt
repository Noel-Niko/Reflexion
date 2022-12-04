//package com.livingtechusa.reflexion.ui.components.bars
//
//import androidx.compose.foundation.layout.RowScope
//import androidx.compose.material.IconButton
//import androidx.compose.material.Text
//import androidx.compose.material.TopAppBar
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Menu
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.unit.dp
//import com.livingtechusa.reflexion.R
//
//
//@Composable
//fun BuildTopBar(
//    onNavigationClick: () -> Unit
//) {
//    TopAppBar(
//        title = {
//            Text(text = stringResource(id = R.string.app_name))
//        },
//        backgroundColor = MaterialTheme.colorScheme.onPrimaryContainer,
//        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//        elevation = 4.dp,
//        navigationIcon = {
//            IconButton(onClick = onNavigationClick) {
//                Icon(
//                    imageVector = Icons.Default.Menu,
//                    contentDescription = stringResource(R.string.toggle_drawer)
//                )
//            }
//        }
//    )
//}
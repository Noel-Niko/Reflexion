package com.livingtechusa.reflexion.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.util.Constants.IMAGE

@Composable
fun ImageCard(
    image: ByteArray?,
    navController: NavController,
) {
    if (image != null) {
        val imagePainter = rememberImagePainter(
            data = image,
            builder = {
                allowHardware(false)
            }
        )
        OutlinedCard(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .height(400.dp)
                .padding(4.dp)
                .background(Color.Red),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Image(
                painter = imagePainter,
                contentDescription = "Your Image",
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        onClick = {
                            navController.navigate(Screen.ConfirmDeleteSubItemScreen.route + "/" + IMAGE )
                        }
                    )
            )
        }
    }
}
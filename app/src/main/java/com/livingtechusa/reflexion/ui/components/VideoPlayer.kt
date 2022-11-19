package com.livingtechusa.reflexion.ui.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import com.livingtechusa.reflexion.util.Constants.URI

const val VideoScreenRoute = "view_video_screen"

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer(
    sourceType: String,
    itemViewModel: ItemViewModel
) {
    val context = LocalContext.current

    val savedflexionItem by itemViewModel.reflexionItem.collectAsState()

    val videoSource: String = if (sourceType == URI) {
        savedflexionItem.videoUri ?: EMPTY_STRING
    } else {
        savedflexionItem.videoUrl ?: EMPTY_STRING
    }

    val exoPlayer = com.google.android.exoplayer2.ExoPlayer.Builder(LocalContext.current)
        .build()
        .also { exoPlayer ->
            val mediaItem = com.google.android.exoplayer2.MediaItem.Builder()
                .setUri(videoSource)
                .build()
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }

    DisposableEffect(
        AndroidView(factory = {
            StyledPlayerView(context).apply {
                player = exoPlayer
            }
        })
    ) {
        onDispose {
            exoPlayer.release()
        }
    }
}
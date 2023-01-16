package com.livingtechusa.reflexion.ui.components


import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.livingtechusa.reflexion.data.Converters
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel

const val VideoScreenRoute = "view_video_screen"

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer(
    viewModel: ItemViewModel
) {
    val context = LocalContext.current

    val videoUri by viewModel.videoUri.collectAsState()
    val uri = videoUri?.let { Converters().convertStringToUri(it) }

    val exoPlayer = ExoPlayer.Builder(LocalContext.current)
        .build()
        .also { exoPlayer ->
            val mediaItem = MediaItem.Builder()
                .setUri(uri)
                .build()
            exoPlayer
                .setMediaItem(mediaItem)
            exoPlayer.prepare()
        }

    DisposableEffect(
        AndroidView(factory = {
            StyledPlayerView(context).apply {
                player = exoPlayer
            }
        })
    ) {
        onDispose { exoPlayer.release() }
    }
}

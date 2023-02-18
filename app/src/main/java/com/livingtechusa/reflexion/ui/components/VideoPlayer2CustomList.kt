package com.livingtechusa.reflexion.ui.components


import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING

const val VideoCustomListScreenRoute = "view_video_custom_list_screen"

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer2CustomList(
    index: Int?,
    viewModel: CustomListsViewModel
) {
    val childList by viewModel.children.collectAsState()
    val context = LocalContext.current

    if (childList.isNullOrEmpty().not()) {
        val uri: String = index?.let { childList[it].videoUri } ?: EMPTY_STRING
        if (uri != EMPTY_STRING) {
            val exoPlayer = ExoPlayer.Builder(LocalContext.current)
                .build()
                .also { exoPlayer ->
                    val mediaItem = MediaItem.Builder()
                        .setUri(uri)
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
                onDispose { exoPlayer.release() }
            }
        }
    }
}

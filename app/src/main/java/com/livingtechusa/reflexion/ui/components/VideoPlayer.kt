package com.livingtechusa.reflexion.ui.components


import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.livingtechusa.reflexion.ui.build.BuildRoute
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import com.livingtechusa.reflexion.util.Constants
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import com.livingtechusa.reflexion.util.Constants.URI
import com.livingtechusa.reflexion.util.Constants.URL
import com.livingtechusa.reflexion.util.Temporary
import kotlinx.coroutines.launch

const val VideoScreenRoute = "view_video_screen"

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer(
    sourceType: String,
    itemViewModel: ItemViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current

    val savedReflexionItem by itemViewModel.reflexionItem.collectAsState()

    if (sourceType == URL) {
        if(!savedReflexionItem.videoUrl.isNullOrEmpty()) {
            val intent = Intent(
                Intent.ACTION_VIEW, Uri.parse(savedReflexionItem.videoUrl)
            )
            startActivity(context, intent, null)
            navController.popBackStack(BuildRoute, false )
        } else {
            navController.popBackStack(BuildRoute, false )
        }
    } else {
        val videoSource: String = savedReflexionItem.videoUri ?: EMPTY_STRING
        val exoPlayer = ExoPlayer.Builder(LocalContext.current)
            .build()
            .also { exoPlayer ->
                val mediaItem = MediaItem.Builder()
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
            onDispose { exoPlayer.release() }
        }
    }
}
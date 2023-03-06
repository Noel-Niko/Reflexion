package com.livingtechusa.reflexion.ui.components


import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.util.Constants
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING

const val VideoCustomListScreenRoute = "view_video_custom_list_screen"

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer2CustomList(
    index: Int?,
    viewModel: CustomListsViewModel = hiltViewModel(),
    navHostController: NavHostController
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
    } else {
        Toast.makeText(LocalContext.current, stringResource(R.string.video_could_not_be_found), Toast.LENGTH_SHORT).show()
        navHostController.popBackStack()
    }
}

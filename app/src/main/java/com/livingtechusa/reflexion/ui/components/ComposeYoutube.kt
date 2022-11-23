//package com.livingtechusa.reflexion.ui.components
//
//import android.R
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.fragment.app.FragmentManager
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
//
//
//@Composable
//fun ComposeYoutube(
//    modifier: Modifier,
//    playList: List<String>,
//    supportFragmentManager: FragmentManager,
//    onError: (String) -> Unit
//) {
//
//    val youTubePlayerView: YouTubePlayerView = YouTubePlayerView(LocalContext.current)
//        getLifecycle().addObserver(youTubePlayerView)
//
//    youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
//        override fun onReady(youTubePlayer: YouTubePlayer) {
//            val videoId = "S0Q4gqBUs7c"
//            youTubePlayer.loadVideo(videoId, 0f)
//        }
//    })
//}
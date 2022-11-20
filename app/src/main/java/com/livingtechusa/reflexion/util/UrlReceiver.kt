package com.livingtechusa.reflexion.util

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.livingtechusa.reflexion.MainActivity
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import com.livingtechusa.reflexion.navigation.NavigationHosting
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint

class UrlReceiver() : ComponentActivity() {
    val context = this
    @Inject
    lateinit var localServiceImpl: LocalServiceImpl

    override fun onResume() {
        super.onResume()
//        when (intent?.action) {
//            Intent.ACTION_SEND -> {
//                if("text/plain" == intent.type) {
//                    val reflexionItem = ReflexionItem(
//                        autogenPK = Long.MAX_VALUE,
//                        videoUrl = intent.clipData?.getItemAt(0)?.text.toString()
//                    )
//                    lifecycleScope.launch {
//                        withContext(Dispatchers.Main) {
//                            localServiceImpl.setItem(reflexionItem)
//                            val newIntent = Intent(
//                                context, MainActivity::class.java
//                            )
//                            newIntent.putExtra(Constants.URL, intent.clipData?.getItemAt(0)?.text)
//                            startActivity(newIntent)
//                        }
//                    }
//                }
//            }
//        }
        val newIntent = Intent(
            context, MainActivity::class.java
        )
        newIntent.putExtra(Constants.URL, intent.clipData?.getItemAt(0)?.text)
        startActivity(newIntent)
//        setContent {
//            NavigationHosting()
//        }
//        val url = intent.clipData?.getItemAt(0)?.text
    }
}
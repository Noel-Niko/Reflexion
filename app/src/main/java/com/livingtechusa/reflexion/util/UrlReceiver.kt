package com.livingtechusa.reflexion.util

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import androidx.activity.ComponentActivity
import com.livingtechusa.reflexion.MainActivity
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UrlReceiver() : ComponentActivity() {
    val context: Context = BaseApplication.getInstance()

    @Inject
    lateinit var localServiceImpl: LocalServiceImpl

    override fun onResume() {
        super.onResume()
        val newIntent = Intent(
            context, MainActivity::class.java
        )
        newIntent.putExtra(Constants.URL, intent.clipData?.getItemAt(0)?.text)
//        newIntent.flags = FLAG_ACTIVITY_SINGLE_TOP
        newIntent.flags = FLAG_ACTIVITY_NEW_TASK
        context.startActivity(newIntent)
    }
}
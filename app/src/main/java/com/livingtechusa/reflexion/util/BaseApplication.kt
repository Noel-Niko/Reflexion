package com.livingtechusa.reflexion.util

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import com.facebook.stetho.Stetho
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class BaseApplication : Application() {

    companion object {
        private lateinit var sInstance: BaseApplication

        @JvmStatic
        fun getInstance(): BaseApplication = sInstance
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        Stetho.initializeWithDefaults(this)
    }

    private val isDark = mutableStateOf(false)

    fun toggleTheme() {
        isDark.value = !isDark.value
    }
}
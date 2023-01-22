package com.livingtechusa.reflexion.ui.viewModels

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.MainActivity
import com.livingtechusa.reflexion.ui.settings.SettingsEvent
import com.livingtechusa.reflexion.util.BaseApplication
import com.livingtechusa.reflexion.util.Constants.APP_ICON_LIST
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
import com.livingtechusa.reflexion.util.sharedPreferences.UserPreferencesUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(): ViewModel() {
    private val TAG = "SettingsViewModel"
    private val context: Context
        get() = BaseApplication.getInstance()
    val resource = ResourceProviderSingleton
    private val _iconImages = MutableStateFlow(emptyList<Bitmap>())
    val iconImages: StateFlow<List<Bitmap>> get() = _iconImages

    private fun getAppBitmapList() {
        val bitmapList = mutableListOf<Bitmap>()
        val ids = APP_ICON_LIST.let { list ->
            list.forEach { id ->
                bitmapList.add(resource.getDrawable(id).toBitmap())
            }
        }
        _iconImages.value = bitmapList
    }

    fun onTriggerEvent(event: SettingsEvent) {
        viewModelScope.launch {
            try {
                when (event) {
                    is SettingsEvent.getIconImages -> {
                        getAppBitmapList()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "ERROR " + e.message + " WITH CAUSE " + e.cause)
            }
        }
    }

    fun setTheme(int: Int) {
        UserPreferencesUtil.setCurrentUserThemeSelection(context, int)
        val mStartActivity = Intent(context, MainActivity::class.java)
        val mPendingIntentId = 123456
        val mPendingIntent = PendingIntent.getActivity(
            context,
            mPendingIntentId,
            mStartActivity,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val mgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] = mPendingIntent
    }
}

package com.livingtechusa.reflexion.ui.viewModels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.Converters
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import com.livingtechusa.reflexion.navigation.ReflexionNavigationType
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.ui.list.ListEvent
import com.livingtechusa.reflexion.util.BaseApplication
import com.livingtechusa.reflexion.util.MediaStoreUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


const val STATE_KEY_URL = "com.livingtechusa.reflexion.ui.build.BuildItemScreen.url"

enum class ApiStatus { PRE_INIT, LOADING, ERROR, DONE }


@HiltViewModel
class ItemViewModel @Inject constructor(
    private val localServiceImpl: LocalServiceImpl
) : ViewModel() {

    private val TAG = "ItemViewModel"

//    var navigationType = ReflexionNavigationType.BOTTOM_NAVIGATION
//
    private val _reflexionItem = MutableStateFlow(ReflexionItem())
    val reflexionItem: StateFlow<ReflexionItem> get() = _reflexionItem



    private val context: Context
        get() = BaseApplication.getInstance()

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow: SharedFlow<String> = _errorFlow

    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.i(TAG, "ERROR: $throwable")
        throwable.printStackTrace()
    }

    private val _SaveNow = MutableStateFlow(false)
    val saveNow: StateFlow<Boolean> get() = _SaveNow

    suspend fun hasNoChildren(pk: Long): Boolean {
        return localServiceImpl.selectChildren(pk).isEmpty()
    }

    suspend fun hasNoSiblings(pk: Long, parentPk: Long): Boolean {
        return localServiceImpl.selectSiblings(pk, parentPk).isEmpty()
    }

    fun onTriggerEvent(event: BuildEvent) {
        viewModelScope.launch {
            try {
                when (event) {

                    is BuildEvent.UpdateReflexionItem -> {
                        withContext(Dispatchers.IO) {
                            _reflexionItem.value = event.reflexionItem
                            localServiceImpl.updateReflexionItem(event.reflexionItem)
                        }
                    }

                    is BuildEvent.SaveNew -> {
                        withContext(Dispatchers.IO) {
                            localServiceImpl.setItem(event.reflexionItem)
                            _reflexionItem.value =
                                localServiceImpl.selectReflexionItemByName(event.reflexionItem.name)
                        }
                    }

                    is BuildEvent.UpdateDisplayedReflexionItem -> {
                        _reflexionItem.value = event.reflexionItem
                    }

                    is BuildEvent.GetSelectedReflexionItem -> {
                        withContext(Dispatchers.Main) {
                            when (event.pk) {
                                -2L ->
                                    _reflexionItem.value = ReflexionItem()

                                else ->
                                    _reflexionItem.value =
                                        event.pk?.let { localServiceImpl.selectItem(it) }
                                            ?: ReflexionItem()
                            }
                        }
                    }

                    is BuildEvent.Delete -> {
                        withContext(Dispatchers.IO) {
                            localServiceImpl.deleteReflexionItem(
                                _reflexionItem.value.autogenPK,
                                _reflexionItem.value.name
                            )
                            _reflexionItem.value = ReflexionItem()
                        }
                    }

                    is BuildEvent.ShowVideo -> {
                        if (event.uri.isNullOrEmpty().not()) {
                            event.uri?.let {

                            }
                        }
                    }

                    is BuildEvent.ClearReflexionItem -> {
                        _reflexionItem.value = ReflexionItem()
                    }

                    is BuildEvent.UpdateVideoURL -> {
                        _reflexionItem.value.videoUrl = event.videoUrl
                    }

                    is BuildEvent.SetParent -> {
                        _reflexionItem.value.parent = event.parent
                    }

                    is BuildEvent.SendText -> {
                        val text = reflexionItem.value.name + "\n" + reflexionItem.value.description +
                                " \n" + reflexionItem.value.detailedDescription + "\n" + reflexionItem.value.videoUrl

                        val video = reflexionItem.value.videoUri?.let {
                            Converters().convertStringToUri(
                                it
                            )
                        }
                         val shareIntent = Intent()
                        shareIntent.action = Intent.ACTION_SEND
                        shareIntent.type = "text/*"

                        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
                        //shareIntent.putExtra(Intent.EXTRA_STREAM, video)

                        //shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(context, shareIntent, null)
  }

                    is BuildEvent.SaveFromTopBar -> {
                        _SaveNow.value = true
                    }

                    else -> {

                    }
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Exception: ${e.message}  with cause: ${e.cause}"
                )
            }
        }
    }


    suspend fun createVideoUri(): Uri? {
        val filename = context.getString(R.string.app_name) + "${System.currentTimeMillis()}.mp4"
        val uri = MediaStoreUtils.createVideoUri(context, filename)
        return if (uri != null) {
            uri
        } else {
            _errorFlow.emit("Could not create a video Uri\n$filename")
            null
        }
    }

    //    private fun effect(block: suspend () -> Unit) {
//        viewModelScope.launch(Dispatchers.IO) { block() }
//    }


    fun setSaveNow(saveNow: Boolean) {
            _SaveNow.value = saveNow
    }
}
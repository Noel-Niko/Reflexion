package com.livingtechusa.reflexion.ui.viewModels

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.Converters
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import com.livingtechusa.reflexion.ui.build.BuildEvent
import com.livingtechusa.reflexion.util.BaseApplication
import com.livingtechusa.reflexion.util.Constants.DESCRIPTION
import com.livingtechusa.reflexion.util.Constants.DETAILED_DESCRIPTION
import com.livingtechusa.reflexion.util.Constants.DO_NOT_UPDATE
import com.livingtechusa.reflexion.util.Constants.EMPTY_PK
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import com.livingtechusa.reflexion.util.Constants.IMAGE
import com.livingtechusa.reflexion.util.Constants.NAME
import com.livingtechusa.reflexion.util.Constants.PARENT
import com.livingtechusa.reflexion.util.Constants.VIDEO_URI
import com.livingtechusa.reflexion.util.Constants.VIDEO_URL
import com.livingtechusa.reflexion.util.scopedStorageUtils.FileResource
import com.livingtechusa.reflexion.util.scopedStorageUtils.MediaStoreUtils
import com.livingtechusa.reflexion.util.scopedStorageUtils.SafUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.FileDescriptor
import javax.inject.Inject


const val STATE_KEY_URL = "com.livingtechusa.reflexion.ui.build.BuildItemScreen.url"

enum class ApiStatus { PRE_INIT, LOADING, ERROR, DONE }


@HiltViewModel
class ItemViewModel @Inject constructor(
    private val localServiceImpl: LocalServiceImpl
) : ViewModel() {

    companion object {
        private val TAG = this::class.java.simpleName
    }

    // The active item being viewed in build or selected from a list to be viewed in build
    private val _reflexionItem = MutableStateFlow(ReflexionItem())
    val reflexionItem: StateFlow<ReflexionItem> get() = _reflexionItem

//    // Currently displayed list of items in the list
//    private val _list = MutableStateFlow(emptyList<ReflexionItem>())
//    val list: StateFlow<List<ReflexionItem>> get() = _list

//    // Term for query
//    private val _search = MutableStateFlow(null as String?)
//    val search: StateFlow<String?> = _search

    private val context: Context
        get() = BaseApplication.getInstance()

    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.i(TAG, "ERROR: $throwable")
        throwable.printStackTrace()
    }

    private val _SaveNow = MutableStateFlow(false)
    val saveNow: StateFlow<Boolean> get() = _SaveNow

//    var listPK = 0L

    suspend fun hasNoChildren(pk: Long): Boolean {
        return localServiceImpl.selectChildren(pk).isEmpty()
    }

    suspend fun hasNoSiblings(pk: Long, parentPk: Long): Boolean {
        return localServiceImpl.selectSiblings(pk, parentPk).isEmpty()
    }

    /**
     * We keep the current media [Uri] in the savedStateHandle to re-render it if there is a
     * configuration change and we expose it as a [LiveData] to the UI
     */
//    val selectedFile: LiveData<FileResource?> =
//        savedStateHandle.getLiveData<FileResource?>(SELECTED_FILE_KEY)
//    private val uri: Uri? = reflexionItem.value.videoUri?.let { Converters().convertStringToUri(it)}
    private val _selectedFile: MutableStateFlow<FileResource?> = MutableStateFlow(null)
    val selectedFile get() = _selectedFile

    fun getSelectedFile() {
        try {
            val uri: Uri? =
                reflexionItem.value.videoUri?.let { Converters().convertStringToUri(it) }
            if(uri != null) {
                viewModelScope.launch {
//                    val parcelFileDescriptor: ParcelFileDescriptor? =
//                        context.applicationContext.contentResolver.openFileDescriptor(uri, "r")
//                    val fileDescriptor: FileDescriptor = parcelFileDescriptor?.fileDescriptor
//                    val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
//                    parcelFileDescriptor?.close()
                    _selectedFile.value = SafUtils.getResourceByUri(context = context, uri = uri)
                }
            }

        } catch(e: java.lang.Exception) {
            Log.e(TAG, "Unable to get File Resource")
        }
    }

    fun onTriggerEvent(event: BuildEvent) {
        viewModelScope.launch {
            try {
                when (event) {
                    is BuildEvent.UpdateReflexionItem -> {
                        viewModelScope.launch {
                            _reflexionItem.value = event.reflexionItem
                            localServiceImpl.updateReflexionItem(event.reflexionItem)
                        }
                    }
                    is BuildEvent.DeleteReflexionItemSubItemByName -> {
                        viewModelScope.launch {
                            val updatedReflexionItem = reflexionItem.value
                            when(event.subItem) {
                                NAME -> {
                                    updatedReflexionItem.name = EMPTY_STRING
                                }
                                DESCRIPTION -> {
                                    updatedReflexionItem.description = EMPTY_STRING
                                }
                                DETAILED_DESCRIPTION -> {
                                    updatedReflexionItem.detailedDescription = EMPTY_STRING
                                }
                                IMAGE -> {
                                    updatedReflexionItem.image = null
                                }
                                VIDEO_URI -> {
                                    updatedReflexionItem.videoUri = EMPTY_STRING
                                }
                                VIDEO_URL -> {
                                    updatedReflexionItem.videoUrl = EMPTY_STRING
                                }
                                PARENT -> {
                                    updatedReflexionItem.parent = null
                                }
                            }
                            _reflexionItem.value = updatedReflexionItem
                            localServiceImpl.updateReflexionItem(reflexionItem.value)
                        }
                    }
                    is BuildEvent.SaveNew -> {
                        viewModelScope.launch {
                            localServiceImpl.setItem(event.reflexionItem)
                            _reflexionItem.value =
                                localServiceImpl.selectReflexionItemByName(event.reflexionItem.name)
                        }
                    }

                    is BuildEvent.UpdateDisplayedReflexionItem -> {
                        _reflexionItem.value = event.reflexionItem
                    }

                    is BuildEvent.GetSelectedReflexionItem -> {
                        viewModelScope.launch {
                            when (event.pk) {
                                EMPTY_PK -> {
                                    _reflexionItem.value = ReflexionItem()
                                }

                                DO_NOT_UPDATE -> {}
                                else ->
                                    _reflexionItem.value =
                                        event.pk?.let { localServiceImpl.selectItem(it) }
                                            ?: ReflexionItem()
                            }
                        }
                    }

                    is BuildEvent.Delete -> {
                        viewModelScope.launch {
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

                    is BuildEvent.BluetoothSend -> {
                        val text =
                            reflexionItem.value.name + "\n" + reflexionItem.value.description +
                                    " \n" + reflexionItem.value.detailedDescription + "\n" + reflexionItem.value.videoUrl

                        val video = reflexionItem.value.videoUri?.let {
                            Converters().convertStringToUri(
                                it
                            )
                        }

                        val resolver: ContentResolver = context.contentResolver
                        val shareIntent = Intent()
                        shareIntent.action = Intent.ACTION_OPEN_DOCUMENT
                        shareIntent.type = "video/*"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
                        shareIntent.setDataAndType(video, video?.let { resolver.getType(it) })
                        shareIntent.putExtra(Intent.EXTRA_STREAM, video)
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        shareIntent.action = Intent.ACTION_SEND
                        startActivity(context, shareIntent, null)
                    }

                    is BuildEvent.SendText -> {
                        val text =
                            reflexionItem.value.name + "\n" + reflexionItem.value.description +
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
//
//    fun onTriggerEvent(event: ListEvent) {
//        viewModelScope.launch {
//            try {
//                when (event) {
//                    is ListEvent.GetList -> {
//                        if (event.pk == null || event.pk == EMPTY_PK) {
//                            _list.value = localServiceImpl.getAllTopics() as List<ReflexionItem>
//                        } else {
//                            _list.value =
//                                localServiceImpl.selectChildren(event.pk) as List<ReflexionItem>
//                        }
//                    }
//
//                    is ListEvent.Search -> {
//                        if (event.pk == EMPTY_PK) {
//                            if (event.search.isNullOrEmpty()) {
//                                _list.value = localServiceImpl.getAllTopics() as List<ReflexionItem>
//                            } else {
//                                // Topic key work search
//                                _list.value =
//                                    localServiceImpl.getAllTopicsContainingString(event.search) as List<ReflexionItem>
//                            }
//                        } else {
//                            // Children keyword search
//                            _list.value =
//                                localServiceImpl.selectChildrenContainingString(
//                                    event.pk,
//                                    event.search
//                                ) as List<ReflexionItem>
//                        }
//                    }
//
//                    is ListEvent.ClearList -> {
//                        _list.value = emptyList()
//                    }
//
//                    else -> {
//                        Toast.makeText(context, "No matching items", Toast.LENGTH_SHORT).show()
//                    }
//
//                }
//            } catch (e: Exception) {
//                Log.e(
//                    TAG,
//                    "Exception: ${e.message}  with cause: ${e.cause}"
//                )
//            }
//        }
//    }

    suspend fun createVideoUri(): Uri? {
        val filename = context.getString(R.string.app_name) + "${System.currentTimeMillis()}.mp4"
        val uri = MediaStoreUtils.createVideoUri(context, filename)
        return if (uri != null) {
            uri
        } else {
            Toast.makeText(context, "Could not create video Uri\n$filename", Toast.LENGTH_SHORT)
                .show()
            null
        }
    }

    suspend fun createImageUri(): Uri? {
        val filename = context.getString(R.string.app_name) + "${System.currentTimeMillis()}.jpg"
        val uri = MediaStoreUtils.createImageUri(context, filename)
        return if (uri != null) {
            uri
        } else {
            Toast.makeText(context, "Could not create image Uri\n$filename", Toast.LENGTH_SHORT)
                .show()
            null
        }
    }

    //    private fun effect(block: suspend () -> Unit) {
//        viewModelScope.launch(Dispatchers.IO) { block() }
//    }
//    fun searchEvent(term: String?) {
//        _search.value = term
//        if (term.isNullOrEmpty()) {
//            onTriggerEvent(ListEvent.GetList(listPK))
//        } else {
//            onTriggerEvent(ListEvent.Search(term, reflexionItem.value.autogenPK))
//        }
//    }

    fun setSaveNow(saveNow: Boolean) {
        _SaveNow.value = saveNow
    }
}
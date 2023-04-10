package com.livingtechusa.reflexion.ui.viewModels

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.Converters
import com.livingtechusa.reflexion.data.entities.Bookmarks
import com.livingtechusa.reflexion.data.entities.ListNode
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import com.livingtechusa.reflexion.data.models.ReflexionItemAsJson
import com.livingtechusa.reflexion.data.toAListNode
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
import com.livingtechusa.reflexion.util.Constants.USE_TOP_ITEM
import com.livingtechusa.reflexion.util.Constants.VIDEO_URI
import com.livingtechusa.reflexion.util.Constants.VIDEO_URL
import com.livingtechusa.reflexion.util.TemporarySingleton
import com.livingtechusa.reflexion.util.json.FileUtil
import com.livingtechusa.reflexion.util.json.ReflexionJsonWriter
import com.livingtechusa.reflexion.util.scopedStorageUtils.FileResource
import com.livingtechusa.reflexion.util.scopedStorageUtils.ImageUtils
import com.livingtechusa.reflexion.util.scopedStorageUtils.ImageUtils.rotateImage
import com.livingtechusa.reflexion.util.scopedStorageUtils.MediaStoreUtils
import com.livingtechusa.reflexion.util.scopedStorageUtils.SafeUtils
import com.livingtechusa.reflexion.util.sharedPreferences.UserPreferencesUtil
import com.livingtechusa.reflexion.util.sharedPreferences.UserPreferencesUtil.resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject


const val STATE_KEY_URL = "com.livingtechusa.reflexion.ui.build.BuildItemScreen.url"


@HiltViewModel
class BuildItemViewModel @Inject constructor(
    private val localServiceImpl: LocalServiceImpl
) : ViewModel() {
    companion object {
        private val TAG = this::class.java.simpleName
    }

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    // Used in Nav Drawer to ensure the most up-to-date-state provided
    private var _reflexionItemState = MutableStateFlow(ReflexionItem())
    val reflexionItemState: StateFlow<ReflexionItem> get() = _reflexionItemState

    // Used to provide initial UI and for saving updated UI values to the DB.
    private var _reflexionItem = ReflexionItem()
    val reflexionItem: ReflexionItem get() = _reflexionItem

    private val _autogenPK = MutableStateFlow(0L)
    val autogenPK: StateFlow<Long> get() = _autogenPK

    private val _name = MutableStateFlow(EMPTY_STRING)
    val name: StateFlow<String> get() = _name

    private val _description = MutableStateFlow(EMPTY_STRING)
    val description: StateFlow<String?> get() = _description

    private val _detailedDescription = MutableStateFlow(EMPTY_STRING)
    val detailedDescription: StateFlow<String?> get() = _detailedDescription

    private val _image = MutableStateFlow<Bitmap?>(null)
    val image: StateFlow<Bitmap?> get() = _image.asStateFlow()

    private val _videoUri = MutableStateFlow(EMPTY_STRING)
    val videoUri: StateFlow<String?> get() = _videoUri

    private val _videoUrl = MutableStateFlow(EMPTY_STRING)
    val videoUrl: StateFlow<String?> get() = _videoUrl

    private val _parent: MutableStateFlow<Long?> = MutableStateFlow(null)
    val parent: StateFlow<Long?> get() = _parent


    private val context: Context
        get() = BaseApplication.getInstance()

    private val _saveNowFromTopBar = MutableStateFlow(false)
    val saveNowFromTopBar: StateFlow<Boolean> get() = _saveNowFromTopBar

    private var topItem: Long? = null

    fun setTopItem(pk: Long) {
        topItem = pk
    }

    suspend fun hasNoChildren(pk: Long): Boolean {
        return localServiceImpl.selectChildren(pk).isEmpty()
    }

    suspend fun hasNoSiblings(pk: Long, parentPk: Long): Boolean {
        return localServiceImpl.selectSiblings(pk, parentPk).isEmpty()
    }

    private val _selectedFile: MutableStateFlow<FileResource?> = MutableStateFlow(null)
    val selectedFile get() = _selectedFile

    private val itemsFromFile = mutableListOf<ReflexionItem>()
    private val oldToNewPkTable = mutableMapOf<Long?, Long?>()
    private var fileListTopic: Long? = null
    private val mutex = Mutex()

    fun getSelectedFile() {
        try {
            val uri: Uri? =
                videoUri.value?.let { Converters().convertStringToUri(it) }
            if (uri != null) {
                viewModelScope.launch {
                    _selectedFile.value =
                        SafeUtils.getResourceByUriPersistently(context = context, uri = uri)
                }
            }

        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Unable to get File Resource")
        }
    }

    fun onTriggerEvent(event: BuildEvent) {
        viewModelScope.launch {
            try {
                when (event) {
                    is BuildEvent.UpdateReflexionItem -> {
                        viewModelScope.launch() {
                            withContext(Dispatchers.IO) {
                                val updates = ReflexionItem(
                                    autogenPk = autogenPK.value,
                                    name = name.value,
                                    description = description.value,
                                    detailedDescription = detailedDescription.value,
                                    image = image.value?.let {
                                        Converters().getByteArrayFromBitmap(
                                            it
                                        )
                                    },
                                    videoUri = videoUri.value,
                                    videoUrl = videoUrl.value,
                                    parent = parent.value
                                )
                                val priorImagePk = _reflexionItem.imagePk
                                _reflexionItem = updates
                                _reflexionItemState.value = updates
                                localServiceImpl.updateReflexionItem(updates, priorImagePk)
                            }
                        }
                    }

                    is BuildEvent.DeleteReflexionItemSubItemByName -> {
                        viewModelScope.launch {
                            val updatedReflexionItem = reflexionItem
                            when (event.subItem) {
                                NAME -> {
                                    updatedReflexionItem.name = EMPTY_STRING
                                    _name.value = EMPTY_STRING
                                }

                                DESCRIPTION -> {
                                    updatedReflexionItem.description = EMPTY_STRING
                                    _description.value = EMPTY_STRING
                                }

                                DETAILED_DESCRIPTION -> {
                                    updatedReflexionItem.detailedDescription = EMPTY_STRING
                                    _detailedDescription.value = EMPTY_STRING
                                }

                                IMAGE -> {
                                    val job = launch {
                                        reflexionItemState.value.imagePk?.let { imageKey ->
                                            localServiceImpl.deleteImageAndAssociation(
                                                imagePk = imageKey,
                                                itemPk = reflexionItem.autogenPk
                                            )
                                        }
                                    }
                                    job.join()
                                    updatedReflexionItem.image = null
                                    updatedReflexionItem.imagePk = null
                                    _image.value = null
                                }

                                VIDEO_URI -> {
                                    updatedReflexionItem.videoUri = EMPTY_STRING
                                    _videoUri.value = EMPTY_STRING
                                }

                                VIDEO_URL -> {
                                    updatedReflexionItem.videoUrl = EMPTY_STRING
                                    _videoUrl.value = EMPTY_STRING
                                }

                                PARENT -> {
                                    updatedReflexionItem.parent = null
                                    _parent.value = null
                                }
                            }
                            _reflexionItem = updatedReflexionItem
                            _reflexionItemState.value = updatedReflexionItem
                            localServiceImpl.updateReflexionItem(reflexionItem, null)
                        }
                    }

                    is BuildEvent.SaveNew -> {
                        viewModelScope.launch {
                            val newItem = ReflexionItem(
                                autogenPk = autogenPK.value,
                                name = name.value,
                                description = description.value,
                                detailedDescription = detailedDescription.value,
                                imagePk = reflexionItem.imagePk,
                                image = image.value?.let {
                                    Converters().getByteArrayFromBitmap(
                                        it
                                    )
                                },
                                videoUri = videoUri.value,
                                videoUrl = videoUrl.value,
                                parent = parent.value
                            )

                            _reflexionItem =
                                localServiceImpl.selectItem(localServiceImpl.saveNewItem(newItem))
                                    ?: ReflexionItem()
                            _reflexionItemState.value = _reflexionItem
                            _autogenPK.value = _reflexionItem.autogenPk
                        }
                    }

                    is BuildEvent.UpdateDisplayedReflexionItem -> {
                        when (event.subItem) {
                            NAME -> {
                                _name.value = event.newVal as String
                            }

                            DESCRIPTION -> {
                                _description.value = event.newVal as String
                            }

                            DETAILED_DESCRIPTION -> {
                                _detailedDescription.value = event.newVal as String
                            }

                            IMAGE -> {
                                _image.value = event.newVal as Bitmap
                            }

                            VIDEO_URI -> {
                                _videoUri.value = event.newVal as String
                            }

                            VIDEO_URL -> {
                                _videoUrl.value = event.newVal as String
                            }

                            PARENT -> {
                                _parent.value = event.newVal as Long?
                            }
                        }
                    }

                    is BuildEvent.GetSelectedReflexionItem -> {
                        viewModelScope.launch {
                            when (event.pk) {
                                EMPTY_PK -> {
                                    _reflexionItem = ReflexionItem()
                                    updateAllDisplayedSubItemsToViewModelVersion()
                                    _reflexionItemState.value = _reflexionItem
                                }

                                DO_NOT_UPDATE -> {}

                                USE_TOP_ITEM -> {
                                    _reflexionItem =
                                        event.pk.let { localServiceImpl.selectItem(topItem ?: 1) }
                                            ?: ReflexionItem()
                                    updateAllDisplayedSubItemsToViewModelVersion()
                                    _reflexionItemState.value = _reflexionItem
                                }

                                else -> {
                                    _reflexionItem =
                                        event.pk?.let { localServiceImpl.selectItem(it) }
                                            ?: ReflexionItem()
                                    updateAllDisplayedSubItemsToViewModelVersion()
                                    _reflexionItemState.value = _reflexionItem
                                }
                            }
                        }
                    }

                    is BuildEvent.Delete -> {
                        viewModelScope.launch {
                            localServiceImpl.deleteReflexionItem(
                                _reflexionItem.autogenPk,
                                _reflexionItem.name,
                                _reflexionItem.imagePk
                            )
                            _reflexionItem = ReflexionItem()
                            updateAllDisplayedSubItemsToViewModelVersion()
                            _reflexionItemState.value = _reflexionItem
                        }
                    }

                    is BuildEvent.ClearReflexionItem -> {
                        _reflexionItem = ReflexionItem()
                        _reflexionItemState.value = _reflexionItem
                        updateAllDisplayedSubItemsToViewModelVersion()
                    }


                    is BuildEvent.SetParent -> {
                        viewModelScope.launch {
                            val parent = localServiceImpl.selectItem(event.parent)
                            val item =
                                ReflexionItem(parent = parent?.autogenPk, imagePk = parent?.imagePk)
                            item.image =
                                Converters().getByteArrayFromBitmap(parent?.imagePk?.let { parentImagePk ->
                                    localServiceImpl.selectImage(
                                        parentImagePk
                                    )
                                })
                            _reflexionItem = item
                            _reflexionItemState.value = _reflexionItem
                            updateAllDisplayedSubItemsToViewModelVersion()
                        }
                    }

                    is BuildEvent.SetSelectedParent -> {
                        viewModelScope.launch {
                            val item = _reflexionItem
                            item.parent = event.parent.autogenPk
                            item.image = event.parent.image
                            item.imagePk = event.parent.imagePk
                            _reflexionItem = item
                            _reflexionItemState.value = _reflexionItem
                            updateAllDisplayedSubItemsToViewModelVersion()
                            this@BuildItemViewModel.onTriggerEvent(BuildEvent.Save)
                        }
                    }

                    is BuildEvent.SendText -> {
                        val shareIntent = Intent()
                        shareIntent.type = "text/*"
                        val text =
                            context.getString(R.string.title) + name.value + "\n" + context.getString(
                                R.string.description
                            ) + description.value +
                                    " \n" + context.getString(R.string.detailedDescription) + detailedDescription.value + "\n" + videoUrl.value + "\n\n" +
                                    context.getString(R.string.sent_with_reflexion_from_the_google_play_store) + "\n" +
                                    context.getString(R.string.reflexion_link)
                        shareIntent.putExtra(Intent.EXTRA_TEXT, text)

                        val video = videoUri.value?.let {
                            Converters().convertStringToUri(
                                it
                            )
                        }
                        if (video != null && video.equals(EMPTY_STRING).not()) {
                            val resolver: ContentResolver = context.contentResolver
                            shareIntent.action = Intent.ACTION_OPEN_DOCUMENT
                            shareIntent.type = "video/*"
                            shareIntent.setDataAndType(video, video.let { resolver.getType(it) })
                            shareIntent.putExtra(Intent.EXTRA_STREAM, video)
                            shareIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        shareIntent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                        shareIntent.action = Intent.ACTION_SEND
                        startActivity(context, shareIntent, null)
                    }

                    is BuildEvent.SendFile -> {
                        viewModelScope.launch {
                            withContext(Dispatchers.Main) {
                                try {
                                    // create a json file
                                    val title = reflexionItemState.value.name.replace(" ", "_")

                                    val filename =
                                        context.getString(R.string.app_name) + title + "${System.currentTimeMillis()}.json"
                                    val file = File(context.filesDir, filename)
                                    file.setExecutable(true, false)
                                    file.setReadable(true, false)
                                    file.setWritable(true, false)


                                    // save data to the file
                                    val outputStream: FileOutputStream = FileOutputStream(file)
                                    val reflexionItemList = mutableListOf(reflexionItemState.value)
                                    ReflexionJsonWriter()
                                        .writeJsonStream(
                                            outputStream,
                                            reflexionItemList
                                        )

                                    // generate uri to the file with permissions
                                    val contentUri: Uri = FileProvider.getUriForFile(
                                        context.applicationContext,
                                        "com.livingtechusa.reflexion.fileprovider",
                                        file
                                    )
                                    context.grantUriPermission(
                                        context.applicationContext.packageName,
                                        contentUri,
                                        FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_WRITE_URI_PERMISSION
                                    )

                                    TemporarySingleton.sharedFileList.add(contentUri)
                                    // record file for later deletion
                                    val fileSet: MutableSet<String> = mutableSetOf()
                                    TemporarySingleton.sharedFileList.forEach { uri ->
                                        fileSet.add(
                                            Converters().convertUriToString(uri) ?: EMPTY_STRING
                                        )
                                    }
                                    UserPreferencesUtil.setFilesSaved(context, fileSet)
                                    // Create intent
                                    val shareIntent = Intent()
                                    val text =
                                        context.getString(R.string.this_file_was_sent_with_reflexion_and_requires_the_reflexion_app_to_open) + "\n" + context.getString(
                                            R.string.sent_with_reflexion_from_the_google_play_store
                                        ) + "\n" + context.getString(R.string.reflexion_link)
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, text)

                                    if (contentUri.equals(EMPTY_STRING).not()) {
                                        val resolver: ContentResolver = context.contentResolver
                                        shareIntent.type = "plain/text"
                                        shareIntent.putExtra(
                                            Intent.EXTRA_SUBJECT,
                                            "Sending: ${reflexionItemState.value.name}"
                                        )
                                        shareIntent.action = Intent.ACTION_OPEN_DOCUMENT
                                        shareIntent.setDataAndType(
                                            contentUri,
                                            contentUri.let { resolver.getType(it) })
                                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                                        shareIntent.flags =
                                            FLAG_ACTIVITY_NEW_TASK or FLAG_GRANT_READ_URI_PERMISSION
                                        shareIntent.action = Intent.ACTION_SEND
                                        startActivity(context, shareIntent, null)
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        R.string.an_error_occurred_please_try_again,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }

                    is BuildEvent.SaveAndDisplayedReflexionItemFile -> {
                        // Show Loading
                        _loading.value = true
                        try {
                            // read the json
                            val reflexionFile: List<ReflexionItemAsJson>? =
                                TemporarySingleton.file?.let {
                                    FileUtil(context).getObjectFromFile(it)
                                }

                            reflexionFile?.forEach {
                                itemsFromFile.add(it.toReflexionItem())
                            }
                            // itemsFromFile is global to the viewModel and processed in this function
                            fileListTopic = itemsFromFile[0].autogenPk
                            saveItemFromFile(null, null, null, null)

                        } catch (e: Exception) {
                            _loading.value = false
                            Toast.makeText(
                                context,
                                R.string.an_error_occurred_please_try_again,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        _loading.value = false
                    }

                    is BuildEvent.Save -> {
                        _saveNowFromTopBar.value = true
                    }

                    is BuildEvent.Bookmark -> {
                        viewModelScope.launch {
                            val bookMark = Bookmarks(
                                autoGenPk = autogenPK.value,
                                ITEM_PK = event.itemPk,
                                LIST_PK = null,
                                LEVEL_PK = null,
                                title = name.value
                            )
                            localServiceImpl.setBookMarks(bookMark)
                        }
                    }

                    is BuildEvent.RotateImage -> {
                        var image = image.value
                        if (image != null) {
                            image = rotateImage(image, -90f)
                            _image.value = image
                        }
                    }

                    is BuildEvent.CreateThumbnailImage -> {
                        val iStream: InputStream? =
                            context.contentResolver.openInputStream(event.uri ?: Uri.EMPTY)
                        if (iStream != null) {
                            //Reduce the image to a thumbnail & save
                            val bitmap = BitmapFactory.decodeStream(iStream)
                            var thumbNail = ImageUtils.extractThumbnail(bitmap, 100, 100)
                            if (thumbNail != null) {
                                thumbNail = rotateImage(thumbNail, 90f)
                                _image.value = thumbNail
                            }
                        }
                        withContext(Dispatchers.IO) {
                            iStream?.close()
                        }
                    }

                    is BuildEvent.SearchUri -> {
                        viewModelScope.launch {
                            val _savedRI = async {
                                localServiceImpl.selectItemByUri(event.uri)
                            }
                            val savedRI: ReflexionItem? = _savedRI.await()
                            updateAllDisplayedSubItemsToViewModelVersion()
                            if (savedRI != null) {
                                _reflexionItem = savedRI
                                _reflexionItemState.value = _reflexionItem
                            } else {
                                this@BuildItemViewModel.onTriggerEvent(
                                    BuildEvent.UpdateDisplayedReflexionItem(
                                        VIDEO_URI,
                                        event.uri
                                    )
                                )
                            }
                            TemporarySingleton.url = EMPTY_STRING
                            TemporarySingleton.uri = EMPTY_STRING
                            TemporarySingleton.useUri = false
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Exception: ${e.message}  with cause: ${e.cause}"
                )
                Toast.makeText(
                    context,
                    resource.getString(R.string.an_error_occurred_please_try_again),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateAllDisplayedSubItemsToViewModelVersion() {
        _autogenPK.value = reflexionItem.autogenPk
        _name.value = reflexionItem.name
        _description.value = reflexionItem.description ?: EMPTY_STRING
        _detailedDescription.value = reflexionItem.detailedDescription ?: EMPTY_STRING
        _image.value = reflexionItem.image?.let {
            Converters().getBitmapFromByteArray(
                it
            )
        }
        _videoUri.value = reflexionItem.videoUri ?: EMPTY_STRING
        _videoUrl.value = reflexionItem.videoUrl ?: EMPTY_STRING
        _parent.value = reflexionItem.parent
    }

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

    fun setSaveNowFromTopBar(saveNow: Boolean) {
        _saveNowFromTopBar.value = saveNow
    }

    // Functions to Save File Contents
    private suspend fun saveItemFromFile(filePk: Long?, dbPk: Long?, fileGrandParentPk: Long?, dbGrandparentPk: Long?) {
        var oldPrimaryKey: Long? = filePk
        var newPrimaryKey: Long? = null
        // if a child of saved item, save all in depthful way
        itemsFromFile.forEach {
            if (it.parent == filePk) {
                oldPrimaryKey = it.autogenPk
                newPrimaryKey = localServiceImpl.saveNewItem(
                    itemsFromFile[0].copy(
                        autogenPk = 0L,
                        parent = dbPk
                    )
                )
                mutex.withLock { itemsFromFile.remove(it) }
                mutex.withLock { oldToNewPkTable[it.autogenPk] = newPrimaryKey }
                saveItemFromFile(oldPrimaryKey, newPrimaryKey, filePk, dbPk)
            }
        }
        // sava all topics and orphaned parents at present level
        itemsFromFile.forEach {
            if (!hasParentInList(it.parent) && it.parent != filePk) {
                oldPrimaryKey = it.autogenPk
                // siblings share the "grandparent" as parentPk, orphans and topics have null as the parent
                var dbParent  = if (it.parent == fileGrandParentPk) { dbGrandparentPk } else { null }
                // check if a child of a topic previously removed from the list
                if (oldToNewPkTable.containsKey(it.parent)) {
                    dbParent = oldToNewPkTable[it.parent]
                }
                newPrimaryKey = localServiceImpl.saveNewItem(
                    itemsFromFile[0].copy(
                        autogenPk = 0L,
                        parent = dbParent
                    )
                )
                mutex.withLock { itemsFromFile.remove(it) }
                mutex.withLock { oldToNewPkTable[it.autogenPk] = newPrimaryKey }
                saveItemFromFile(it.autogenPk, newPrimaryKey, null, null)
            }
        }

        val job = viewModelScope.launch {  createList(oldToNewPkTable) }
        job.join()
        mutex.withLock { itemsFromFile.clear() }
    }

    private suspend fun createList(oldToNewPkTable: MutableMap<Long?, Long?>) {
        if(oldToNewPkTable.isEmpty().not()) {
            val newList = mutableListOf<Long>()
            // Add items from file to newList
            oldToNewPkTable.forEach {
                it.value?.let { newPk -> newList.add(newPk) }
            }
            val topicPk = oldToNewPkTable[fileListTopic]
            mutex.withLock { oldToNewPkTable.clear() }

            // for each item in newList, convert to a linked ListNode and save
            var parentPk: Long? = null
            newList.forEachIndexed { index, pk ->
                val node = localServiceImpl.selectReflexionArrayItemByPk(pk)
                    ?.toAListNode(topic = topicPk, parentPk = parentPk)
                parentPk = node?.let { listNode -> localServiceImpl.insertNewNode(listNode) }
            }
        }
    }

    private fun hasParentInList(itemsParent: Long?): Boolean {
        if (itemsParent == null) return false
        val parent = itemsFromFile.firstOrNull() { itemInList ->
            (itemInList.autogenPk == itemsParent)
        }
        return parent != null
    }
}
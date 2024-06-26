package com.livingtechusa.reflexion.ui.viewModels

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.Converters
import com.livingtechusa.reflexion.data.entities.Bookmarks
import com.livingtechusa.reflexion.data.entities.ListNode
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import com.livingtechusa.reflexion.data.models.ReflexionList
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
import com.livingtechusa.reflexion.util.json.writeReflexionItemListToZipFile
import com.livingtechusa.reflexion.util.scopedStorageUtils.FileResource
import com.livingtechusa.reflexion.util.scopedStorageUtils.ImageUtils
import com.livingtechusa.reflexion.util.scopedStorageUtils.ImageUtils.rotateImage
import com.livingtechusa.reflexion.util.scopedStorageUtils.MediaStoreUtils
import com.livingtechusa.reflexion.util.scopedStorageUtils.MediaStoreUtils.createMediaFile
import com.livingtechusa.reflexion.util.scopedStorageUtils.SafeUtils
import com.livingtechusa.reflexion.util.sharedPreferences.UserPreferencesUtil
import com.livingtechusa.reflexion.util.sharedPreferences.UserPreferencesUtil.resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.LinkedList
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
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

    private val oldToNewPkList = LinkedList<PkPairs>()

    private var fileListTopic: Long? = null
    private var listTitle: String? = EMPTY_STRING

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
                                localServiceImpl.updateReflexionItemImage(updates, priorImagePk)
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
                            localServiceImpl.updateReflexionItemImage(reflexionItem, null)
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
                                localServiceImpl.selectReflexionItemByPk(
                                    localServiceImpl.saveNewItem(
                                        newItem
                                    )
                                )
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
                                        event.pk.let {
                                            localServiceImpl.selectReflexionItemByPk(
                                                topItem ?: 1
                                            )
                                        }
                                            ?: ReflexionItem()
                                    updateAllDisplayedSubItemsToViewModelVersion()
                                    _reflexionItemState.value = _reflexionItem
                                }

                                else -> {
                                    _reflexionItem =
                                        event.pk?.let { localServiceImpl.selectReflexionItemByPk(it) }
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
                            val parent = localServiceImpl.selectReflexionItemByPk(event.parent)
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
                            if (item.image == null) {
                                item.image = event.parent.image
                                item.imagePk = event.parent.imagePk
                            }
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
                        if (video != null && video.path != "") {
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
                        _loading.value = true
                        viewModelScope.launch {
                            withContext(Dispatchers.IO) {
                                try {
                                    val filename =
                                        context.getString(R.string.app_name) + reflexionItemState.value.name + "${System.currentTimeMillis()}.zip"
                                    val file = File(context.filesDir, filename)
                                    file.setExecutable(true, false)
                                    file.setReadable(true, false)
                                    val zipValues = writeReflexionItemListToZipFile(
                                        context,
                                        listOf(reflexionItemState.value),
                                        reflexionItemState.value.name,
                                        file
                                    )

                                    // create list of files to remove next onCreate
                                    TemporarySingleton.sharedFileList.addAll(zipValues.uriList)
                                    TemporarySingleton.sharedFileList.add(zipValues.zipUri)
                                    val fileSet: MutableSet<String> = mutableSetOf()
                                    TemporarySingleton.sharedFileList.forEach { uri ->
                                        fileSet.add(
                                            Converters().convertUriToString(uri) ?: EMPTY_STRING
                                        )
                                    }
                                    UserPreferencesUtil.setFilesSaved(context, fileSet)

                                    // create the share intent
                                    val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
                                    shareIntent.type = "application/zip"
                                    shareIntent.putExtra(
                                        Intent.EXTRA_SUBJECT,
                                        "Sending: ${reflexionItemState.value.name}"
                                    )
                                    val text =
                                        context.getString(R.string.this_file_was_sent_with_reflexion_and_requires_the_reflexion_app_to_open) + "\n" + context.getString(
                                            R.string.sent_with_reflexion_from_the_google_play_store
                                        ) + "\n" + context.getString(R.string.reflexion_link)
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, text)
                                    // Attach the files
                                    val resolver: ContentResolver = context.contentResolver
                                    shareIntent.action = Intent.ACTION_OPEN_DOCUMENT
                                    shareIntent.setDataAndType(
                                        zipValues.zipUri,
                                        zipValues.zipUri.let { resolver.getType(it) })
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, zipValues.zipUri)
                                    shareIntent.addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_GRANT_READ_URI_PERMISSION)
                                    shareIntent.action = Intent.ACTION_SEND
                                    withContext(Dispatchers.Main) { _loading.value = false }
                                    startActivity(context, shareIntent, null)
                                } catch (e: Exception) {
                                    Log.e(
                                        TAG,
                                        "Error " + e.message + " with cause " + e.cause + " and Stack trace of: " + e.stackTrace
                                    )
                                    withContext(Dispatchers.Main) {
                                        _loading.value = false
                                        Toast.makeText(
                                            context,
                                            R.string.an_error_occurred_please_try_again,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }

                    }

                    is BuildEvent.SaveAndDisplayZipFile -> {
                        // Show Loading
                        _loading.value = true
                        openZipFile(TemporarySingleton.file)
                    }

                    is BuildEvent.Save -> {
                        _saveNowFromTopBar.value = true
                    }

                    is BuildEvent.Bookmark -> {
                        viewModelScope.launch {
                            if (localServiceImpl.selectReflexionItemByPk(autogenPK.value) != null) {
                                val bookMark = Bookmarks(
                                    autoGenPk = autogenPK.value,
                                    ITEM_PK = event.itemPk,
                                    LIST_PK = null,
                                    LEVEL_PK = null,
                                    title = name.value
                                )
                                localServiceImpl.setBookMarks(bookMark)
                                viewModelScope.launch(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        resource.getString(R.string.bookmarked),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.you_must_save_the_item_before_it_can_be_bookmarked),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
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

                    BuildEvent.RemoveListNodesForDeletedItems -> {
                        withContext(Dispatchers.IO) { localServiceImpl.removeLinkNodesForDeletedLists() }
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

    private suspend fun processJsonFile(inputStream: InputStream?): Boolean {
        val itemsFromFile = mutableListOf<ReflexionItem>()
        _loading.value = true
        val job = viewModelScope.async {
            try {
                val job = viewModelScope.async {
                    withContext(Dispatchers.IO) {
                        val reflexionFile: ReflexionList? = if (inputStream == null) {
                            // read the json
                            TemporarySingleton.file?.let {
                                FileUtil(context).getObjectFromFile(
                                    it,
                                    ReflexionList::class.java
                                )
                            }
                        } else {
                            FileUtil(context).getObjectFromInputStream(
                                inputStream = inputStream,
                                ReflexionList::class.java
                            )
                        }

                        reflexionFile?.reflexionItems?.forEach {
                            it?.toReflexionItem()?.let { it1 -> itemsFromFile.add(it1) }
                        }

                        listTitle = reflexionFile?.List_Title
                        fileListTopic = itemsFromFile[0].autogenPk
                        itemsFromFile.forEach {
                            oldToNewPkList.add(PkPairs(it.autogenPk, null))
                        }
                        val job = viewModelScope.async {
                            withContext(Dispatchers.IO) {
                                val job = async {
                                    saveItemsFromFile(
                                        null,
                                        null,
                                        null,
                                        null,
                                        itemsFromFile
                                    )
                                }
                                job.await()
                                if (oldToNewPkList.size > 1) {
                                    createList(oldToNewPkList)
                                }
                            }
                        }
                        job.await()
                    }
                }
                job.await()
            } catch (e: Exception) {
                _loading.value = false
                Toast.makeText(
                    context,
                    R.string.an_error_occurred_please_try_again,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        job.await()
        TemporarySingleton.file = null
        _loading.value = false
        return true
    }

    private suspend fun openZipFile(filePath: Uri?) {
        withContext(Dispatchers.IO) {
            val tempFile = File.createTempFile("temp", ".zip")
            try {
                if (filePath != null) {
                    // Create a ZipFile object for the zip file
                    val contentResolver = context.contentResolver
                    val inputStream = contentResolver.openInputStream(filePath)
                    val outputStream = FileOutputStream(tempFile)
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()
                    val zipFile = ZipFile(tempFile)

                    // Get the entries in the zip file
                    val entries = zipFile.entries()
                    val jsonEntries = mutableListOf<ZipEntry>()
                    val mp4Entries = mutableListOf<ZipEntry>()

                    // Categorize entries as JSON or MP4 files
                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement()
                        if (!entry.isDirectory) {
                            if (entry.name.endsWith(".json")) {
                                jsonEntries.add(entry)
                            } else if (entry.name.endsWith(".mp4")) {
                                mp4Entries.add(entry)
                            }
                        }
                    }

                    // Process the JSON files first
                    val jsonJobs = jsonEntries.map { entry ->
                        async(Dispatchers.IO) {
                            val stream = zipFile.getInputStream(entry)
                            // Process the contents of the JSON file
                            val job = async(Dispatchers.IO) { processJsonFile(stream) }
                            job.await()
                            stream.close()
                        }
                    }
                    jsonJobs.awaitAll()

                    // Process the MP4 files next
                    val mp4Jobs = mp4Entries.map { entry ->
                        async(Dispatchers.IO) {
                            val inputStream = zipFile.getInputStream(entry)
                            // Save item to file
                            val uri: Uri? = createMediaFile(
                                inputStream = inputStream,
                                mimeType = "video",
                                displayName = entry.name,
                                context = context
                            )
                            val pattern = Regex("""\d+""")
                            val matchResult = pattern.find(entry.name)
                            val originalPk = matchResult?.value?.toLong()
                            val reflexionItem = localServiceImpl.selectReflexionItemByPk(
                                oldToNewPkList.firstOrNull { it.originalPk == originalPk }?.newPk
                            )
                            // Save video uri to associated reflexion item
                            if (reflexionItem != null && uri != null) {
                                localServiceImpl.updateReflexionItemVideoUri(reflexionItem, uri)
                            }
                            inputStream.close()
                        }
                    }
                    mp4Jobs.awaitAll()

                    // Close the zip file
                    zipFile.close()
                }
                TemporarySingleton.file = null
                // Create list of files to remove next onCreate
                if (filePath != null) {
                    TemporarySingleton.sharedFileList.add(filePath)
                    val fileSet = mutableSetOf<String>()
                    TemporarySingleton.sharedFileList.forEach { uri ->
                        fileSet.add(Converters().convertUriToString(uri) ?: EMPTY_STRING)
                    }
                    UserPreferencesUtil.setFilesSaved(context, fileSet)
                }
                withContext(Dispatchers.Main) { _loading.value = false }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "ERROR: " + e.message + " WITH CAUSE: " + e.cause + "STACK TRACE: " + e.stackTrace
                )
                withContext(Dispatchers.Main) { _loading.value = false }
            } finally {
                try {
                    tempFile.delete()
                } catch (e: Exception) {
                    Log.e(TAG, "ERROR: " + e.message + " WITH CAUSE: " + e.cause)
                }
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
    private suspend fun saveItemsFromFile(
        filePk: Long?,
        dbPk: Long?,
        fileGrandParentPk: Long?,
        dbGrandparentPk: Long?,
        itemsFromFile: MutableList<ReflexionItem>
    ) {
        var oldPrimaryKey: Long? = filePk
        var newPrimaryKey: Long?
        // Create a copy of the list before iterating over it
        val itemsCopy1 = itemsFromFile.toList()
        // if a child of saved item, save all in depthful way
        itemsCopy1.forEach { reflexionItem ->
            if (reflexionItem.parent == filePk && filePk != null) {
                oldPrimaryKey = reflexionItem.autogenPk
                newPrimaryKey =
                    itemsFromFile.firstOrNull { it.autogenPk == reflexionItem.autogenPk }?.copy(
                        autogenPk = 0L,
                        parent = dbPk
                    )?.let { saveItem ->
                        localServiceImpl.saveNewItem(
                            saveItem
                        )
                    }
                itemsFromFile.removeIf { item -> item.autogenPk == reflexionItem.autogenPk }
                if (newPrimaryKey != null) {
                    // null occurs when an item is in a list more than 1 x
                    oldToNewPkList.forEach { PkPairs ->
                        if (PkPairs.originalPk == reflexionItem.autogenPk) {
                            PkPairs.newPk = newPrimaryKey
                        }
                    }
                }
                saveItemsFromFile(oldPrimaryKey, newPrimaryKey, filePk, dbPk, itemsFromFile)
            }
        }


        val itemsCopy2 = itemsFromFile.toList()
        // sava all topics and orphaned parents at present level
        itemsCopy2.forEach { reflexionItem ->
            if (!hasParentInList(
                    reflexionItem.parent,
                    itemsFromFile
                ) && (reflexionItem.parent != filePk || filePk == null)
            ) {
                oldPrimaryKey = reflexionItem.autogenPk
                // siblings share the "grandparent" as parentPk, orphans and topics have null as the parent
                var dbParent = if (reflexionItem.parent == fileGrandParentPk) {
                    dbGrandparentPk
                } else {
                    null
                }
                // check if a child of a topic previously removed from the list
                if (oldToNewPkList.firstOrNull { it.originalPk == reflexionItem.parent } != null) {
                    dbParent =
                        oldToNewPkList.firstOrNull { it.originalPk == reflexionItem.parent }?.newPk
                }
                newPrimaryKey =
                    itemsFromFile.firstOrNull { it.autogenPk == reflexionItem.autogenPk }?.copy(
                        autogenPk = 0L,
                        parent = dbParent
                    )?.let { saveItem ->
                        localServiceImpl.saveNewItem(
                            saveItem
                        )
                    }

                itemsFromFile.removeIf { it.autogenPk == reflexionItem.autogenPk }
                if (newPrimaryKey != null) {
                    // null occurs when an item is in a list more than 1 x
                    oldToNewPkList.forEach { PkPairs ->
                        if (PkPairs.originalPk == reflexionItem.autogenPk) {
                            PkPairs.newPk = newPrimaryKey
                        }
                    }
                }
                saveItemsFromFile(reflexionItem.autogenPk, newPrimaryKey, null, null, itemsFromFile)
            }
        }
    }

    private suspend fun createList(oldToNewPkList: LinkedList<PkPairs>) {
        val topicPk = oldToNewPkList.firstOrNull { it.originalPk == fileListTopic }?.newPk
        setTopItem(topicPk ?: 0)
        if (oldToNewPkList.isEmpty().not()) {
            val newList = mutableListOf<Long>()
            // Add items from file to newList
            oldToNewPkList.forEach { PkPairs ->
                PkPairs.newPk?.let { newPk -> newList.add(newPk) }
            }
            // create title node
            val title = localServiceImpl.insertNewNode(
                ListNode(
                    nodePk = 0L,
                    topic = topicPk ?: 0L,
                    title = listTitle.toString(),
                    itemPK = -1L,
                    parentPk = null,
                    childPk = null
                )
            )
            // for each item in newList save
            if (newList.size > 1) {
                var parentPk: Long? = title
                newList.forEach { pk ->
                    val node = localServiceImpl.selectReflexionArrayItemByPk(pk)
                        ?.toAListNode(topic = topicPk, parentPk = parentPk)
                    parentPk =
                        node?.let { listNode -> localServiceImpl.insertNewNode(listNode) }
                }
            }
        }
    }

    private fun hasParentInList(
        itemsParent: Long?,
        itemsFromFile: MutableList<ReflexionItem>
    ): Boolean {
        if (itemsParent == null) return false
        val parent = itemsFromFile.firstOrNull() { itemInList ->
            (itemInList.autogenPk == itemsParent)
        }
        return parent != null
    }
}
package com.livingtechusa.reflexion.util.scopedStorageUtils


import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns.DATE_ADDED
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.checkSelfPermission
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.coroutines.resume


object MediaStoreUtils {
    const val TAG = "MediaStoreUtils"

    /**
     * Check if the app can read the shared storage
     */
    fun canReadInMediaStore(context: Context) =
        checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    /**
     * Check if the app can writes on the shared storage
     *
     * On Android 10 (API 29), we can add media to MediaStore without having to request the
     * [WRITE_EXTERNAL_STORAGE] permission, so we only check on pre-API 29 devices
     */
    fun canWriteInMediaStore(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true
        } else {
            checkSelfPermission(
                context,
                WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * We create a MediaStore [Uri] where an image will be stored
     */
    suspend fun createImageUri(context: Context, filename: String): Uri? {
        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        return withContext(Dispatchers.IO) {
            val newImage = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            }

            // This method will perform a binder transaction which is better to execute off the main
            // thread
            return@withContext context.contentResolver.insert(imageCollection, newImage)
        }
    }

    /**
     * We create a MediaStore [Uri] where a video will be stored
     */
    suspend fun createVideoUri(context: Context, filename: String): Uri? {
        val videoCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        return withContext(Dispatchers.IO) {
            val newVideo = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, filename)
            }

            // This method will perform a binder transaction which is better to execute off the main
            // thread
            return@withContext context.contentResolver.insert(videoCollection, newVideo)
        }
    }

    /**
     * We create a MediaStore [Uri] where an image will be stored
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun createDownloadUri(context: Context, filename: String): Uri? {
        val downloadsCollection =
            MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        return withContext(Dispatchers.IO) {
            val newImage = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, filename)
            }

            // This method will perform a binder transaction which is better to execute off the main
            // thread
            return@withContext context.contentResolver.insert(downloadsCollection, newImage)
        }
    }

    /**
     * Convert a media [Uri] to a content [Uri] to be used when requesting
     * [MediaStore.Files.FileColumns] values.
     *
     * Some columns are only available on the [MediaStore.Files] collection and this method converts
     * [Uri] from other MediaStore collections (e.g. [MediaStore.Images])
     *
     * @param uri [Uri] representing the MediaStore entry.
     */
    private fun convertMediaUriToContentUri(uri: Uri): Uri? {
        var entryId = uri.lastPathSegment ?: return null
        entryId = entryId.replace("\\D".toRegex(), "")

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            var volumeName = EMPTY_STRING
            try {
                volumeName = MediaStore.getVolumeName(uri)
            } catch (e: Exception) {
                Log.e(TAG, "Error " + e.message + " with cause : " + e.cause)
            }
            if (volumeName.isEmpty()) volumeName = "external"
            MediaStore.Files.getContentUri(volumeName, entryId.toLong())
        } else {
            MediaStore.Files.getContentUri(uri.pathSegments[0], entryId.toLong())
        }
    }

    suspend fun scanPath(context: Context, path: String, mimeType: String): Uri? {
        return suspendCancellableCoroutine { continuation ->
            MediaScannerConnection.scanFile(
                context,
                arrayOf(path),
                arrayOf(mimeType)
            ) { _, scannedUri ->
                if (scannedUri == null) {
                    continuation.cancel(Exception("File $path could not be scanned"))
                } else {
                    continuation.resume(scannedUri)
                }
            }
        }
    }

    suspend fun scanUri(context: Context, uri: Uri, mimeType: String): Uri? {
        val cursor = context.contentResolver.query(
            uri,
            arrayOf(MediaStore.Files.FileColumns.DATA),
            null,
            null,
            null
        ) ?: throw Exception("Uri $uri could not be found")

        val path = cursor.use {
            if (!cursor.moveToFirst()) {
                throw Exception("Uri $uri could not be found")
            }

            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
        }

        return suspendCancellableCoroutine { continuation ->
            MediaScannerConnection.scanFile(
                context,
                arrayOf(path),
                arrayOf(mimeType)
            ) { _, scannedUri ->
                if (scannedUri == null) {
                    continuation.cancel(Exception("File $path could not be scanned"))
                } else {
                    continuation.resume(scannedUri)
                }
            }
        }
    }

    /**
     * Returns a [FileResource] if it finds its [Uri] in MediaStore.
     */
    suspend fun getResourceByUri(context: Context, uri: Uri): FileResource {
        return withContext(Dispatchers.IO) {
            // Convert generic media uri to content uri to get FileColumns.MEDIA_TYPE value
            val contentUri = convertMediaUriToContentUri(uri)

            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.DATA,
            )

            val cursor = contentUri?.let {
                context.contentResolver.query(
                    it,
                    projection,
                    null,
                    null,
                    null
                )
            } ?: throw Exception("Uri $uri could not be found")

            cursor.use {
                if (!cursor.moveToFirst()) {
                    throw Exception("Uri $uri could not be found")
                }

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                val mediaTypeColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
                val mimeTypeColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)

                FileResource(
                    uri = contentUri,
                    filename = cursor.getString(displayNameColumn),
                    size = cursor.getLong(sizeColumn),
                    type = FileType.getEnum(cursor.getInt(mediaTypeColumn)),
                    mimeType = cursor.getString(mimeTypeColumn),
                    path = cursor.getString(dataColumn),
                )
            }
        }
    }


    /**
     * Returns a [FileResource] if it finds its [Uri] in MediaStore.
     */
    suspend fun getMediaResources(context: Context, limit: Int = 50): List<FileResource> {
        return withContext(Dispatchers.IO) {
            val mediaList = mutableListOf<FileResource>()
            val externalContentUri = MediaStore.Files.getContentUri("external")
                ?: throw Exception("External Storage not available")

            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.DATA,
            )

            val cursor = context.contentResolver.query(
                externalContentUri,
                projection,
                null,
                null,
                "$DATE_ADDED DESC"
            ) ?: throw Exception("Query could not be executed")

            cursor.use {
                while (cursor.moveToNext() && mediaList.size < limit) {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                    val displayNameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                    val mediaTypeColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
                    val mimeTypeColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
                    val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)

                    val id = cursor.getInt(idColumn)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        externalContentUri,
                        id.toLong()
                    )

                    mediaList += FileResource(
                        uri = contentUri,
                        filename = cursor.getString(displayNameColumn),
                        size = cursor.getLong(sizeColumn),
                        type = FileType.getEnum(cursor.getInt(mediaTypeColumn)),
                        mimeType = cursor.getString(mimeTypeColumn),
                        path = cursor.getString(dataColumn),
                    )
                }
            }

            return@withContext mediaList
        }
    }

    @Throws(IOException::class)
    fun uriToByteArray(uri: Uri, context: Context): ByteArray? {
        // Creating an object of FileInputStream to
        // read from a file

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val inputStream = context.contentResolver.openInputStream(uri)

            if (inputStream != null) {
                // Now creating byte array of same length as file
                val arr = inputStream.readAllBytes()


                // Reading file content to byte array
                // using standard read() method

                // lastly closing an instance of file input stream
                // to avoid memory leakage
                inputStream.close()

                // Returning above byte array
                return arr
            } else {
                return null
            }
        } else {
            val inputStream = context.contentResolver.openInputStream(uri)
            return if (inputStream != null) {
                readAllBytes(inputStream)
            } else {
                null
            }
        }
    }

    @Throws(IOException::class)
    fun readAllBytes(inputStream: InputStream): ByteArray? {
        val bufLen = 1024
        val buf = ByteArray(bufLen)
        var readLen: Int
        var exception: IOException? = null
        return try {
            val outputStream = ByteArrayOutputStream()
            while (inputStream.read(buf, 0, bufLen).also { readLen = it } != -1) outputStream.write(
                buf,
                0,
                readLen
            )
            outputStream.toByteArray()
        } catch (e: IOException) {
            exception = e
            throw e
        } finally {
            if (exception == null) inputStream.close() else try {
                inputStream.close()
            } catch (e: IOException) {
                exception.addSuppressed(e)
            }
        }
    }


    @Throws(Throwable::class)
    fun retrieveVideoFrameFromVideo(videoPath: String?): Bitmap? {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            if (Build.VERSION.SDK_INT >= 14) mediaMetadataRetriever.setDataSource(
                videoPath,
                HashMap()
            ) else mediaMetadataRetriever.setDataSource(videoPath)
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (e: Exception) {
            e.printStackTrace()
            throw Throwable("Exception in retrieveVideoFrameFromVideo(String videoPath)" + e.message)
        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }

    fun createMediaFile(
        inputStream: InputStream,
        mimeType: String,
        displayName: String,
        context: Context
    ): Uri? {
        val resolver = context.contentResolver
        val directory = if (mimeType.contains("video")) { Environment.DIRECTORY_MOVIES } else {  Environment.DIRECTORY_PICTURES }
            val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                directory + File.separator + context.packageName
            )
        }

        var outputStream: OutputStream? = null
        var uri: Uri? = null

        try {
            val collection = if (mimeType.contains("video")) {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            }
            uri = resolver.insert(collection, contentValues)
            uri?.let {
                outputStream = resolver.openOutputStream(it)
                outputStream?.let { stream -> inputStream.copyTo(stream) }
            }
        } catch (e: Exception) {
            Log.e("createMediaFile", "Error creating media file", e)
            if (uri != null) {
                resolver.delete(uri, null, null)
            }
            uri = null
        } finally {
            outputStream?.close()
            inputStream.close()
        }

        return uri
    }

}
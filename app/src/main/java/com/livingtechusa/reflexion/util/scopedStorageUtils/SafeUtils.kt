package com.livingtechusa.reflexion.util.scopedStorageUtils


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import com.livingtechusa.reflexion.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SafeUtils {
    val TAG = "SafeUtils"

    /**
     * Returns a [FileResource] if it finds its related DocumentsProvider
     */
    suspend fun getResourceByUri(context: Context, uri: Uri): FileResource {
        return withContext(Dispatchers.IO) {

            val projection = arrayOf(
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_SIZE,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
            )

            val cursor = context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                null
            ) ?: throw Exception("Uri $uri could not be found")

            cursor.use {
                if (!cursor.moveToFirst()) {
                    throw Exception("Uri $uri could not be found")
                }

                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                val sizeColumn =
                    cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_SIZE)
                val mimeTypeColumn =
                    cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)

                FileResource(
                    uri = uri,
                    filename = cursor.getString(displayNameColumn),
                    size = cursor.getLong(sizeColumn),
                    type = FileType.DOCUMENT,
                    mimeType = cursor.getString(mimeTypeColumn),
                    path = null,
                )
            }
        }
    }


    suspend fun getResourceByUriPersistently(context: Context, uri: Uri): FileResource? {
        return withContext(Dispatchers.IO) {
            try {
                val projection = arrayOf(
                    DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                    DocumentsContract.Document.COLUMN_SIZE,
                    DocumentsContract.Document.COLUMN_MIME_TYPE,
                )

                val cursor = context.contentResolver.query(
                    uri,
                    projection,
                    null,
                    null,
                    null
                ) ?: throw Exception("Uri $uri could not be found")

                cursor.use {
                    if (!cursor.moveToFirst()) {
                        throw Exception("Uri $uri could not be found")
                    }

                    val displayNameColumn =
                        cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                    val sizeColumn =
                        cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_SIZE)
                    val mimeTypeColumn =
                        cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)
                    try {
                        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        context.contentResolver.takePersistableUriPermission(uri, takeFlags)
                    } catch (e: Exception) {
                        Log.e(TAG, context.getString(R.string.no_persistable_flags_present_to_take))
                    }

                    FileResource(
                        uri = uri,
                        filename = cursor.getString(displayNameColumn),
                        size = cursor.getLong(sizeColumn),
                        type = FileType.DOCUMENT,
                        mimeType = cursor.getString(mimeTypeColumn),
                        path = null,
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "ERROR: " + e.message + " WITH CAUSE " + e.cause)
                return@withContext null
            }
        }
    }

    suspend fun getThumbnail(context: Context, uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)
            } catch (e: Exception) {
                Log.e(TAG, context.getString(R.string.no_persistable_flags_present_to_take))
            }
            return@withContext DocumentsContract.getDocumentThumbnail(
                context.contentResolver,
                uri,
                Point(512, 512),
                null
            )
        }
    }
}
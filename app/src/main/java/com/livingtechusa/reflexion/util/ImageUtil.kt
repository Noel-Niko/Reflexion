package com.livingtechusa.reflexion.util
/*
https://developer.android.com/training/data-storage/shared/photopicker
To use the photo picker support library, include version 1.6.1 or higher of the androidx.activity library.

The support library uses the following activity result contracts to launch the photo picker:

PickVisualMedia, to select a single image or video.
PickMultipleVisualMedia, to select multiple images or videos.
If the photo picker isn't available on a device, the support library automatically invokes the ACTION_OPEN_DOCUMENT intent action instead. This intent is supported on devices that run Android 4.4 (API level 19) or higher.
You can verify whether the photo picker is available on a given device by calling isPhotoPickerAvailable(). If you want to customize the photo picker launch behavior, you can also use framework methods to check whether the photo picker is available.
 */

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.icu.text.DateFormat.getDateTimeInstance
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ext.SdkExtensions.getExtensionVersion
import android.provider.MediaStore
import android.provider.MediaStore.Video
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.net.toUri
import androidx.room.TypeConverter
import com.livingtechusa.reflexion.R
import java.io.File

private fun isPhotoPickerAvailable(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        true
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (Build.VERSION.SDK_INT >= 33) {
            getExtensionVersion(Build.VERSION_CODES.R) >= 2
        } else {
            false
        }
    } else {
        false
    }
}


@Composable
fun ImagePicker(
    modifier: Modifier = Modifier,
) {
    // 1
    var hasImage by remember {
        mutableStateOf(false)
    }
    // 2
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            // 3
            hasImage = uri != null
            imageUri = uri
        }
    )
}


//object ImageUtil {
//
//
//    const val MEDIA_TYPE_IMAGE = 1
//    const val MEDIA_TYPE_VIDEO = 2
//
//    @TypeConverter
//    fun convertStringToUri(string: String): Uri? {
//        return Uri.parse(string)
//    }
//
//    @TypeConverter
//    fun convertUriToString(uri: Uri): String? {
//        return uri.toString()
//    }
//
//    /**
//     * Create a file Uri for saving an image or video
//     *
//     * @param type
//     * MEDIA_TYPE_IMAGE or MEDIA_TYPE_VIDEO
//     * @return a uri
//     */
//    fun getOutputMediaFileUri(type: Int): Uri {
//        return Uri.fromFile(getOutputMediaFile(type))
//    }
//
//    /**
//     * Create a File for saving an image or video
//     *
//     * @param type
//     * MEDIA_TYPE_IMAGE or MEDIA_TYPE_VIDEO
//     * @return a file
//     */
//    fun getOutputMediaFile(type: Int): File? {
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//        val mediaStorageDir = File(
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//            "MyCameraApp"
//        )
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.d("MyCameraApp", "failed to create directory")
//                return null
//            }
//        }
//
//        // Create a media file name
//        val timeStamp = getDateTimeInstance()
//        val mediaFile: File
//        mediaFile = if (type == MEDIA_TYPE_IMAGE) {
//            File(mediaStorageDir.path + File.separator + "IMG_" + timeStamp + ".jpg")
//        } else if (type == MEDIA_TYPE_VIDEO) {
//            File(mediaStorageDir.path + File.separator + "VID_" + timeStamp + ".mp4")
//        } else {
//            return null
//        }
//        return mediaFile
//    }
//
////    fun getVideoUri(cursor: Cursor): Uri {
////        return getMediaUri(cursor, Video.Media.EXTERNAL_CONTENT_URI)
////    }
////
////    fun getMediaUri(cursor: Cursor, uri: Uri?): Uri {
////        val id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
////        return Uri.withAppendedPath(uri, id)
////    }
//
//    fun getVideoThumbnail(context: Context?, videoUri: Uri): Bitmap? {
//        val path = videoUri.path //convert to path
//        return ThumbnailUtils.createVideoThumbnail(path!!, MediaStore.Images.Thumbnails.MINI_KIND)
//    }
//
//    fun playVideo(context: Context, uri: Uri) {
//        val intent = Intent(Intent.ACTION_VIEW)
//        var type: String? = "video/*"
//        val extension = MimeTypeMap.getFileExtensionFromUrl(
//            uri
//                .toString()
//        )
//        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
//            extension
//        )
//        intent.setDataAndType(uri, type)
//        try {
//            context.startActivity(intent)
//        } catch (e: ActivityNotFoundException) {
//            e.printStackTrace()
//        }
//    }
//
//    fun getVideoUriFromMediaProvider(ctxt: Context, videoFile: String): Uri? {
//        val selection = Video.VideoColumns.DATA + "=?"
//        val selectArgs = arrayOf(videoFile)
//        val projection = arrayOf(Video.VideoColumns._ID)
//        var c: Cursor? = null
//        return try {
//            c = ctxt.contentResolver.query(
//                Video.Media.EXTERNAL_CONTENT_URI, projection,
//                selection, selectArgs, null
//            )
//            if (c != null) {
//                if (c.count > 0) {
//                    c.moveToFirst()
//                    val id = c.getString(c. getColumnIndexOrThrow(Video.VideoColumns._ID))
//                    return Uri.withAppendedPath(Video.Media.EXTERNAL_CONTENT_URI, id)
//                }
//            }
//            null
//        } finally {
//            c?.close()
//        }
//    }
//    val REQUEST_VIDEO_CAPTURE = 1
//    val REQUEST_MEDIA = 1
//
//    fun dispatchTakeVideoIntent(context: Context, activity: Activity) {
//        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
//            takeVideoIntent.resolveActivity( context.getPackageManager())?.also {
//                startActivityForResult(activity, takeVideoIntent, REQUEST_VIDEO_CAPTURE, null)
//            } ?: run {
//                Toast.makeText(context, context.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    fun dispatchSelectVideoOrImageIntent(context: Context): Uri? {
//        var uri: Uri? = null
//        if (Build.VERSION.SDK_INT >= 33) {
//            Intent(MediaStore.ACTION_PICK_IMAGES).also { takeVideoIntent ->
//                takeVideoIntent.resolveActivity( context.getPackageManager())?.also {
//                    context.getActivity()?.let { activity -> startActivityForResult(activity, takeVideoIntent, REQUEST_MEDIA, null) }
//                } ?: run {
//                    uri =  this.getOutputMediaFileUri(MEDIA_TYPE_VIDEO)
//                }
//            }
//        } else {
//            if (isPhotoPickerAvailable()) {
//                val pickMedia = context.getActivity()?.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { selectedUri ->
//                    // Callback is invoked after the user selects a media item or closes the
//                    // photo picker.
//                    if (selectedUri != null) {
//                        uri = selectedUri
//                    } else {
//                        Toast.makeText(context, context.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
//                    }
//                }
//                // Launch the photo picker and allow the user to choose only videos.
//                pickMedia?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
//            } else {
//                uri = "no uri".toUri()
//            }
//
//        }
//        return uri
//    }
//
///*
//    // Include only one of the following calls to launch(), depending on the types
//    // of media that you want to allow the user to choose from.
//
//    // Launch the photo picker and allow the user to choose images and videos.
//    pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageAndVideo))
//
//    // Launch the photo picker and allow the user to choose only images.
//    pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
//
//    // Launch the photo picker and allow the user to choose only videos.
//    pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.VideoOnly))
//
//    // Launch the photo picker and allow the user to choose only images/videos of a
//    // specific MIME type, such as GIFs.
//    val mimeType = "image/gif"
//    pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.SingleMimeType(mimeType)))
// */
//
//
//    fun Context.getActivity(): AppCompatActivity? = when (this) {
//        is AppCompatActivity -> this
//        is ContextWrapper -> baseContext.getActivity()
//        else -> null
//    }
//
//    @Composable
//    fun getImageUri(): Uri? {
//        var imageUri by remember {
//            mutableStateOf<Uri?>(null)
//        }
//        val launcher = rememberLauncherForActivityResult(contract =
//                                                         ActivityResultContracts.GetContent()) { uri: Uri? ->
//            imageUri = uri
//        }
//        launcher.launch("image/*")
//        return imageUri
//    }
//
//    @Composable
//    fun convertUriToBitmap(context: Context, uri: Uri): Bitmap? {
//        var bitmap = remember {
//            mutableStateOf<Bitmap?>(null)
//        }
//
//        uri.let {
//            if (Build.VERSION.SDK_INT < 28) {
//                bitmap.value = MediaStore.Images
//                    .Media.getBitmap(context.contentResolver, it)
//            } else {
//                val source = ImageDecoder
//                    .createSource(context.contentResolver, it)
//                bitmap.value = ImageDecoder.decodeBitmap(source)
//            }
//
//            bitmap.let { btm ->
//                btm.value?.let { bitmapValue ->
//                    Image(
//                        bitmap = bitmapValue.asImageBitmap(),
//                        contentDescription = null,
//                        modifier = Modifier.size(400.dp)
//                    )
//                }
//            }
//        }
//        return bitmap.value
//    }
//
//
//    private const val REQUEST_EXTERNAL_STORAGE = 1
//    private val permissionStorage = arrayOf(
//        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        Manifest.permission.READ_EXTERNAL_STORAGE
//    )
//
//    fun verifyStoragePermission(activity: Activity?) {
//        val permissionWrite = ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        val permissionRead = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
//        if (permissionWrite != PackageManager.PERMISSION_GRANTED || permissionRead != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                activity,
//                permissionStorage,
//                REQUEST_EXTERNAL_STORAGE
//            )
//        }
//    }
//}
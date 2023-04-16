package com.livingtechusa.reflexion.util.json

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.models.ReflexionList
import com.livingtechusa.reflexion.data.models.toReflexionItemAsJson
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun writeReflexionItemListToZipFile(
    context: Context,
    reflexionItems: List<ReflexionItem>,
    title: String,
    outputZipFile: File
): ZipValues {
    val fileUris = mutableListOf<Uri>()
    val mediaFileList = mutableListOf<File>()
    val gson = Gson()

    // create the reflexion list Json file
    val reflexionListFile = File(context.filesDir, "reflexionList_$title.json")
    reflexionListFile.writeText(
        gson.toJson(
            ReflexionList(
                title,
                reflexionItems.map { toReflexionItemAsJson(it) })
        )
    )

    fileUris.add(
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            reflexionListFile
        )
    )

    // create the files for the images and videos
    reflexionItems.forEachIndexed { index, reflexionItem ->
        reflexionItem.image?.let { image ->
            val imageFile = File(context.filesDir, "reflexion_${reflexionItem.autogenPk}_image.jpg")
            imageFile.writeBytes(image)
            mediaFileList.add(imageFile)
            fileUris.add(
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    imageFile
                )
            )
        }
        reflexionItem.videoUri?.let { videoUri ->
            val videoDescriptor =
                context.contentResolver.openFileDescriptor(Uri.parse(videoUri), "r")
            val videoFile = File(context.filesDir, "video_${reflexionItem.autogenPk}.mp4")
            ParcelFileDescriptor.AutoCloseInputStream(videoDescriptor).use { input ->
                FileOutputStream(videoFile).use { output ->
                    input.copyTo(output)
                }
            }
            mediaFileList.add(videoFile)
            fileUris.add(
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    videoFile
                )
            )
        }
    }

    val zipFile = createZipFile(
        jsonFile = reflexionListFile,
        mediaFiles = mediaFileList,
        outputZipFile = outputZipFile
    )
    val zipUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", zipFile)

    return ZipValues(zipFile, zipUri, fileUris)
}

fun createZipFile(jsonFile: File, mediaFiles: List<File>, outputZipFile: File): File {
    val buffer = ByteArray(1024)
    val out = ZipOutputStream(FileOutputStream(outputZipFile))

    // add JSON file to ZIP file
    val jsonEntry = ZipEntry(jsonFile.name)
    out.putNextEntry(jsonEntry)
    FileInputStream(jsonFile).use { input ->
        var len = input.read(buffer)
        while (len > 0) {
            out.write(buffer, 0, len)
            len = input.read(buffer)
        }
    }
    out.closeEntry()

    // add media files to ZIP file
    for (mediaFile in mediaFiles) {
        val mediaEntry = ZipEntry(mediaFile.name)
        out.putNextEntry(mediaEntry)
        FileInputStream(mediaFile).use { input ->
            var len = input.read(buffer)
            while (len > 0) {
                out.write(buffer, 0, len)
                len = input.read(buffer)
            }
        }
        out.closeEntry()
    }

    out.close()
    return outputZipFile
}


/*
// Function to write a list of ReflexionItem objects and their media files to a JSON file
fun writeReflexionItemsToFile(context: Context, reflexionItems: List<ReflexionItem>, listTitle: String?): Uri {
    // Create a new JSON object
    val json = JsonObject()

    // Add the List_Title property
    json.addProperty("List_Title", listTitle)

    // Create an array for the reflexionItems
    val itemsArray = JsonArray()

    // Loop through each ReflexionItem object
    for (item in reflexionItems) {
        // Create a new JSON object for the current ReflexionItem
        val itemObject = JsonObject()

        // Add the properties to the JSON object
        itemObject.addProperty("autogenPk", item.autogenPk)
        itemObject.addProperty("name", item.name)
        itemObject.addProperty("description", item.description)
        itemObject.addProperty("detailedDescription", item.detailedDescription)
        itemObject.addProperty("parent", item.parent)

        // Write the image file to disk and add the file path to the JSON object
        item.image?.let { image ->
            val imageFileName = "image_${item.autogenPk}.jpg"
            val imageFile = File(context.filesDir, imageFileName)
            FileOutputStream(imageFile).use { output ->
                output.write(image)
            }
            itemObject.addProperty("image", imageFile.absolutePath)
        }

        // Write the video file to disk and add the file path to the JSON object
        item.videoUri?.let { videoUri ->
            val videoDescriptor = context.contentResolver.openFileDescriptor(Uri.parse(videoUri), "r")
            val videoFile = File(context.filesDir, "video_${item.autogenPk}.mp4")
            ParcelFileDescriptor.AutoCloseInputStream(videoDescriptor).use { input ->
                FileOutputStream(videoFile).use { output ->
                    input.copyTo(output)
                }
            }
            itemObject.addProperty("videoUri", videoFile.absolutePath)
        }

        // Add the completed JSON object for the current ReflexionItem to the array
        itemsArray.add(itemObject)
    }

    // Add the completed array to the main JSON object
    json.add("reflexionItems", itemsArray)

    // Write the JSON object to a file on disk
    val fileName = "reflexion_items.json"
    val file = File(context.filesDir, fileName)
    FileWriter(file).use { writer ->
        Gson().toJson(json, writer)
    }

    file.setExecutable(true, false)
    file.setReadable(true, false)
    file.setWritable(true, false)

    // Return a FileProvider URI for the file
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
 */
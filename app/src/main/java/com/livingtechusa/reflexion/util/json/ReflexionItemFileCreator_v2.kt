//package com.livingtechusa.reflexion.util.json
//
//import android.content.Context
//import android.net.Uri
//import android.os.Environment
//import android.util.Base64
//import com.google.gson.stream.JsonWriter
//import com.livingtechusa.reflexion.data.entities.ReflexionItem
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//import java.io.OutputStreamWriter
//
//class ReflexionJsonWriter(private val context: Context) {
//    companion object {
//        const val IMAGE_DIRECTORY = "reflexion_images"
//    }
//
//    private val imageDir: File = File(
//        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
//        IMAGE_DIRECTORY
//    )
//
//    init {
//        if (!imageDir.exists()) {
//            imageDir.mkdirs()
//        }
//    }
//
//    @Throws(IOException::class)
//    fun writeJsonStream(out: FileOutputStream, reflexionItems: List<ReflexionItem>, title: String) {
//        val writer = JsonWriter(OutputStreamWriter(out, "UTF-8"))
//        writer.setIndent("  ")
//        writer.beginObject()
//        writer.name("List_Title").value(title)
//        writer.name("reflexionItems")
//        writeReflexionItemArray(writer, reflexionItems)
//        writer.endObject()
//        writer.close()
//    }
//
//    @Throws(IOException::class)
//    fun writeReflexionItemArray(writer: JsonWriter, reflexionItems: List<ReflexionItem>) {
//        writer.beginArray()
//        for (reflexionItem in reflexionItems) {
//            writeReflexionItem(writer, reflexionItem)
//        }
//        writer.endArray()
//    }
//
//    @Throws(IOException::class)
//    fun writeReflexionItem(writer: JsonWriter, reflexionItem: ReflexionItem) {
//        writer.beginObject()
//        writer.name("autogenPk").value(reflexionItem.autogenPk)
//        writer.name("name").value(reflexionItem.name)
//        writer.name("description").value(reflexionItem.description)
//        writer.name("detailedDescription").value(reflexionItem.detailedDescription)
//        if (reflexionItem.image != null) {
//            val imageName = saveImage(reflexionItem.image!!)
//            writer.name("imageFileName").value(imageName)
//        }
//        writer.name("videoUrl").value(reflexionItem.videoUrl)
//        writer.name("parent").value(reflexionItem.parent)
//        writer.endObject()
//    }
//
//    private fun saveImage(imageData: ByteArray): String {
//        // Use the MD5 hash of the image data as the file name to avoid duplicates
//        val imageName = HashUtils.md5(imageData) + ".jpg"
//        val imageFile = File(imageDir, imageName)
//        if (!imageFile.exists()) {
//            FileOutputStream(imageFile).use { out ->
//                out.write(imageData)
//                out.flush()
//            }
//        }
//        return imageName
//    }
//
//    private object HashUtils {
//        private const val HEX_CHARS = "0123456789ABCDEF"
//
//        fun md5(data: ByteArray): String {
//            val digest = java.security.MessageDigest.getInstance("MD5").apply {
//                update(data)
//            }
//            return digest.digest().joinToString("") {
//                val i = it.toInt()
//                "${HEX_CHARS[i shr 4 and 0x0f]}${HEX_CHARS[i and 0x0f]}"
//            }
//        }
//    }
//}

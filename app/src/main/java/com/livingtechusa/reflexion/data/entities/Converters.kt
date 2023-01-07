package com.livingtechusa.reflexion.data.entities

import android.net.Uri
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

import java.io.IOException

import java.io.InputStream




class Converters {
    @TypeConverter
    fun convertStringToUri(string: String): Uri? {
        return Uri.parse(string)
    }

    @TypeConverter
    fun convertUriToString(uri: Uri): String? {
        return uri.toString()
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray? {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        inputStream.close()
        return byteBuffer.toByteArray()
    }
}
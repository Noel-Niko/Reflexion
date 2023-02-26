package com.livingtechusa.reflexion.data.entities

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream


class TypeConverters {
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

    fun convertBitMapToByteArray(bitmap: Bitmap):  ByteArray {
        val bos: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50 /*ignored for PNG*/, bos);
        return bos.toByteArray();
    }
}
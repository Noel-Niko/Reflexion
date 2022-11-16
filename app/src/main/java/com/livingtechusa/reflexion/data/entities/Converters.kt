package com.livingtechusa.reflexion.data.entities

import android.net.Uri
import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun convertStringToUri(string: String): Uri? {
        return Uri.parse(string)
    }

    @TypeConverter
    fun convertUriToString(uri: Uri): String? {
        return uri.toString()
    }
}
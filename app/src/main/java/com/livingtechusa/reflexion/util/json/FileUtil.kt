package com.livingtechusa.reflexion.util.json

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Type

class FileUtil(val context: Context) {

    private val gson: Gson
        get() = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()

    @Throws(JsonSyntaxException::class)
    inline fun <reified T> getObjectFromFile(filePath: Uri): T {
        val type = object : TypeToken<T>() {}.type
        return getObjectFromFile(filePath, type)
    }

    @Throws(JsonSyntaxException::class)
    fun <T> getObjectFromFile(filePath: Uri, classOfT: Class<T>): T {
        val fileAsString = openFileAsString(filePath)
        return gson.fromJson(fileAsString, classOfT)
    }

    @Throws(JsonSyntaxException::class)
    fun <T> getObjectFromInputStream(inputStream: InputStream, classOfT: Class<T>): T {
        val fileAsString = openInputStreamAsString(inputStream)
        return gson.fromJson(fileAsString, classOfT)
    }

    @Throws(JsonSyntaxException::class)
    fun <T> getObjectFromFile(filePath: Uri, type: Type?): T {
        if (type == null) {
            throw JsonSyntaxException("Type is required")
        }
        val fileAsString = openFileAsString(filePath)
        return gson.fromJson(fileAsString, type)
    }

    private fun openFileAsString(filePath: Uri): String {
        val inputStream = context.contentResolver.openInputStream(filePath)

        val r = BufferedReader(InputStreamReader(inputStream))
        val total = StringBuilder()
        var line: String?

        try {
            line = r.readLine()

            while (line != null) {
                total.append(line).append('\n')
                line = r.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return total.toString()
    }

    private fun openInputStreamAsString(inputStream: InputStream): String {
        val r = BufferedReader(InputStreamReader(inputStream))
        val total = StringBuilder()
        var line: String?

        try {
            line = r.readLine()

            while (line != null) {
                total.append(line).append('\n')
                line = r.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return total.toString()
    }


}

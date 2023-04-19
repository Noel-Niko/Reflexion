package com.livingtechusa.reflexion.util.sharedPreferences

import android.content.Context


open class SharedPreferencesUtil {
    fun setString(preferenceType: String?, context: Context, stringId: Int, string: String?) {
        val editor = context.getSharedPreferences(preferenceType, Context.MODE_PRIVATE).edit()
        editor.putString(context.getString(stringId), string)
        editor.apply()
    }

    fun getString(
        preferenceType: String?,
        context: Context,
        stringId: Int,
        defaultValue: String?
    ): String? {
        var ret = defaultValue
        val sharedPreferences = context.getSharedPreferences(preferenceType, Context.MODE_PRIVATE)
        val preferenceId = context.getString(stringId)
        try {
            ret = sharedPreferences.getString(preferenceId, defaultValue)
        } catch (e: ClassCastException) {
            val editor = sharedPreferences.edit()
            editor.putString(preferenceId, defaultValue)
            editor.commit()
        }
        return ret
    }

    fun setStringSet(preferenceType: String?, context: Context, stringId: Int, stringSet: Set<String>) {
        val editor = context.getSharedPreferences(preferenceType, Context.MODE_PRIVATE).edit()
        editor.putStringSet(context.getString(stringId), stringSet)
        editor.apply()
    }

    fun getStringSet(
        preferenceType: String?,
        context: Context,
        stringId: Int,
        defaultValue: Set<String?>?
    ): Set<String?>? {
        var ret = defaultValue
        val sharedPreferences = context.getSharedPreferences(preferenceType, Context.MODE_PRIVATE)
        val preferenceId = context.getString(stringId)
        try {
            ret = sharedPreferences.getStringSet(preferenceId, defaultValue)
        } catch (e: ClassCastException) {
            val editor = sharedPreferences.edit()
            editor.putStringSet(preferenceId, defaultValue)
            editor.commit()
        }
        return ret
    }

    fun setInt(preferenceType: String?, context: Context, stringId: Int, i: Int) {
        val editor = context.getSharedPreferences(preferenceType, Context.MODE_PRIVATE).edit()
        editor.putInt(context.getString(stringId), i)
        editor.apply()
    }

    fun getInt(preferenceType: String?, context: Context, stringId: Int, defaultValue: Int): Int {
        var ret = defaultValue
        val sharedPreferences = context.getSharedPreferences(preferenceType, Context.MODE_PRIVATE)
        val preferenceId = context.getString(stringId)
        try {
            ret = sharedPreferences.getInt(preferenceId, defaultValue)
        } catch (e: ClassCastException) {
            val editor = sharedPreferences.edit()
            editor.putInt(preferenceId, defaultValue)
            editor.commit()
        }
        return ret
    }

    fun setLong(preferenceType: String?, context: Context, stringId: Int, l: Long) {
        val editor = context.getSharedPreferences(preferenceType, Context.MODE_PRIVATE).edit()
        editor.putLong(context.getString(stringId), l)
        editor.apply()
    }

    fun getLong(
        preferenceType: String?,
        context: Context,
        stringId: Int,
        defaultValue: Long
    ): Long {
        var ret = defaultValue
        val sharedPreferences = context.getSharedPreferences(preferenceType, Context.MODE_PRIVATE)
        val preferenceId = context.getString(stringId)
        try {
            ret = sharedPreferences.getLong(preferenceId, defaultValue)
        } catch (e: ClassCastException) {
            val editor = sharedPreferences.edit()
            editor.putLong(preferenceId, defaultValue)
            editor.commit()
        }
        return ret
    }

    fun setBoolean(preferenceType: String?, context: Context, stringId: Int, bool: Boolean) {
        // only save boolean if it is not found or it doesn't equal the saved value
        if (!hasSharedPref(preferenceType, context, stringId) || getBoolean(
                preferenceType,
                context,
                stringId,
                false
            ) != bool
        ) {
            val editor = context.getSharedPreferences(preferenceType, Context.MODE_PRIVATE).edit()
            editor.putBoolean(context.getString(stringId), bool)
            editor.apply()
        }
    }

    fun getBoolean(
        preferenceType: String?,
        context: Context,
        stringId: Int,
        defaultValue: Boolean
    ): Boolean {
        var ret = defaultValue
        val sharedPreferences = context.getSharedPreferences(preferenceType, Context.MODE_PRIVATE)
        val preferenceId = context.getString(stringId)
        try {
            ret = sharedPreferences.getBoolean(preferenceId, defaultValue)
        } catch (e: ClassCastException) {
            val editor = sharedPreferences.edit()
            editor.putBoolean(preferenceId, defaultValue)
            editor.commit()
        }
        return ret
    }

    fun hasSharedPref(preferenceType: String?, context: Context, stringId: Int): Boolean {
        return context.getSharedPreferences(preferenceType, Context.MODE_PRIVATE)
            .contains(context.getString(stringId))
    }

    fun deleteSharedPref(preferenceType: String?, context: Context, stringId: Int) {
        context.getSharedPreferences(preferenceType, Context.MODE_PRIVATE).edit()
            .remove(context.getString(stringId)).apply()
    }

    fun removeAllPreferences(preferenceType: String?, context: Context) {
        context.getSharedPreferences(preferenceType, Context.MODE_PRIVATE).edit().clear().commit()
    }
}
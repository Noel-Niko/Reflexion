package com.livingtechusa.reflexion.util.sharedPreferences


import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class EncryptedSharedPreferencesUtil {

    companion object {

        private const val sharedPrefsFile: String = "encrypted_shared_prefs"

        // Although you can define your own key generation parameter specification, it's
        // recommended that you use the value specified here.
        private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        private val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        fun set(context: Context, keyId: Int, value: String) {
            val key = context.getString(keyId)
            val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
                sharedPrefsFile,
                mainKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            with(sharedPreferences.edit()) {
                putString(key, value)
                apply()
            }
        }

        fun get(context: Context, keyId: Int): String? {
            val key = context.getString(keyId)
            val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
                sharedPrefsFile,
                mainKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            return sharedPreferences.getString(key, null)
        }
    }
}
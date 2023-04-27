package com.livingtechusa.reflexion.util.sharedPreferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.livingtechusa.reflexion.R.string.shared_prefs_biometric_authentication_enabled
import com.livingtechusa.reflexion.R.string.shared_prefs_biometric_authentication_opt_in
import com.livingtechusa.reflexion.R.string.shared_prefs_biometric_should_use_manual_pw
import com.livingtechusa.reflexion.R.string.shared_prefs_current_mode_selected
import com.livingtechusa.reflexion.R.string.shared_prefs_current_theme_selected
import com.livingtechusa.reflexion.R.string.shared_prefs_displayed_user_name
import com.livingtechusa.reflexion.R.string.shared_prefs_encrypted_biometric_password
import com.livingtechusa.reflexion.R.string.shared_prefs_encrypted_password
import com.livingtechusa.reflexion.R.string.shared_prefs_encrypted_user_password
import com.livingtechusa.reflexion.R.string.shared_prefs_files_saved_list
import com.livingtechusa.reflexion.R.string.shared_prefs_logged_in_user_name
import com.livingtechusa.reflexion.R.string.shared_prefs_pop_up_views
import com.livingtechusa.reflexion.util.ResourceProviderSingleton


object UserPreferencesUtil : SharedPreferencesUtil() {
    const val PREFERENCE_TYPE = "USER"
    val resource = ResourceProviderSingleton

    /*
     * Username
     */
    fun getDisplayedUserName(context: Context): String? {
        return getString(PREFERENCE_TYPE, context, shared_prefs_displayed_user_name, "")
    }

    fun setDisplayedUserName(context: Context, username: String?) {
        setString(PREFERENCE_TYPE, context, shared_prefs_displayed_user_name, username)
    }

    fun getLoggedInUserName(context: Context): String? {
        return getString(PREFERENCE_TYPE, context, shared_prefs_logged_in_user_name, "")
    }

    fun setLoggedInUserName(context: Context, username: String?) {
        setString(PREFERENCE_TYPE, context, shared_prefs_logged_in_user_name, username)
    }

    /*
     * Biometric Authentication
     */
    fun setBiometricAuthenticationEnabled(context: Context, enabled: Boolean) {
        setBoolean(
            PREFERENCE_TYPE,
            context,
            shared_prefs_biometric_authentication_enabled,
            enabled
        )
    }

    fun getBiometricAuthenticationEnabled(context: Context): Boolean {
        return getBoolean(
            PREFERENCE_TYPE,
            context,
            shared_prefs_biometric_authentication_enabled,
            false
        )
    }

    fun setBiometricAuthShouldUseManualPw(context: Context, enabled: Boolean) {
        setBoolean(
            PREFERENCE_TYPE,
            context,
            shared_prefs_biometric_should_use_manual_pw,
            enabled
        )
    }

    fun getBiometricAuthShouldUseManualPw(context: Context): Boolean {
        return getBoolean(
            PREFERENCE_TYPE,
            context,
            shared_prefs_biometric_should_use_manual_pw,
            false
        )
    }

    fun setBiometricOptIn(context: Context, enabled: Boolean) {
        setBoolean(
            PREFERENCE_TYPE,
            context,
            shared_prefs_biometric_authentication_opt_in,
            enabled
        )
    }

    fun getBiometricOptIn(context: Context): Boolean {
        return getBoolean(
            PREFERENCE_TYPE,
            context,
            shared_prefs_biometric_authentication_opt_in,
            true
        )
    }

    fun getEncryptedBiometricPassword(context: Context): String? {
        return getString(
            PREFERENCE_TYPE,
            context,
            shared_prefs_encrypted_biometric_password,
            ""
        )
    }

    fun setEncryptedBiometricPassword(context: Context, password: String?) {
        setString(
            PREFERENCE_TYPE,
            context,
            shared_prefs_encrypted_biometric_password,
            password
        )
    }

    fun getEncryptedUserPassword(context: Context): String? {
        return EncryptedSharedPreferencesUtil.get(
            context,
            shared_prefs_encrypted_user_password
        )
    }

    fun setEncryptedUserPassword(context: Context, password: String) {
        EncryptedSharedPreferencesUtil.set(
            context,
            shared_prefs_encrypted_user_password,
            password
        )
    }

    /*
     * Encrypted Password
     */
    fun getPassword(context: Context): String? {
        return EncryptedSharedPreferencesUtil.get(
            context,
            shared_prefs_encrypted_password
        )
    }

    fun setPassword(context: Context, password: String) {
        EncryptedSharedPreferencesUtil.set(
            context,
            shared_prefs_encrypted_password,
            password
        )
    }


    /*
     * User Settings
     */


    /*
     * List user preferences
     */
    fun getCurrentUserThemeSelection(context: Context): Int {
        return getInt(PREFERENCE_TYPE, context, shared_prefs_current_theme_selected, -1)
    }

    fun setCurrentUserThemeSelection(context: Context, selection: Int) {
        setInt(PREFERENCE_TYPE, context, shared_prefs_current_theme_selected, selection)
    }

    fun getCurrentUserModeSelection(context: Context): Int {
        return getInt(PREFERENCE_TYPE, context, shared_prefs_current_mode_selected, -1)
    }

    // -1 = nothing set, 1 = darkmode, 0 = lightMode
    fun setCurrentUserModeSelection(context: Context, selection: Int) {
        setInt(PREFERENCE_TYPE, context, shared_prefs_current_mode_selected, selection)
    }

    /*
    * File Management
     */
    fun setFilesSaved(context: Context, fileList: Set<String>) {
        setStringSet(PREFERENCE_TYPE, context, shared_prefs_files_saved_list, fileList)
    }

    fun clearFilesSaved(context: Context) {
        val editor: SharedPreferences.Editor =
            PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.remove(context.getString(shared_prefs_files_saved_list))
        editor.apply()
    }

    fun getFilesSaved(context: Context): Set<String?>? {
        return getStringSet(PREFERENCE_TYPE, context, shared_prefs_files_saved_list, emptySet())
    }

    // PopUp Notice
    fun getPopUpViews(context: Context): Int {
        return getInt(PREFERENCE_TYPE, context, shared_prefs_pop_up_views, 0)
    }

    fun setPopUpViews(context: Context, count: Int) {
        setInt(PREFERENCE_TYPE, context, shared_prefs_pop_up_views, count)
    }
}

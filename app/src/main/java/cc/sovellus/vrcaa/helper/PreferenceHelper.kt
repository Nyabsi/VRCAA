package cc.sovellus.vrcaa.helper

import android.content.SharedPreferences
import androidx.core.content.edit
import cc.sovellus.vrcaa.manager.NotificationManager
import com.google.gson.Gson

// extend SharedPreferences
internal var SharedPreferences.userCredentials: Pair<String?, String?>
    get() {
        return Pair(
            getString("userCredentials_username", "")!!,
            getString("userCredentials_password", "")!!
        )
    }
    set(it) = edit {
        putString("userCredentials_username", it.first)
        putString("userCredentials_password", it.second)
    }

internal var SharedPreferences.cookies: String
    get() = getString("cookies", "")!!
    set(it) = edit { putString("cookies", it) }

internal var SharedPreferences.twoFactorAuth: String
    get() = getString("TwoFactorAuth", "")!!
    set(it) = edit { putString("TwoFactorAuth", it) }

internal var SharedPreferences.isExpiredSession: Boolean
    get() = getBoolean("isExpiredSession", false)
    set(it) = edit { putBoolean("isExpiredSession", it) }

internal var SharedPreferences.notificationWhitelist: NotificationManager.NotificationPermissions
    get() {
        val result = getString("notificationWhitelist", "")
        if (result?.isNotEmpty() == true) {
            return Gson().fromJson(result, NotificationManager.NotificationPermissions::class.java)
        }
        return NotificationManager.NotificationPermissions()
    }
    set(it) = edit { putString("notificationWhitelist", Gson().toJson(it)) }
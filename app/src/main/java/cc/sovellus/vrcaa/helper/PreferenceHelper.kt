package cc.sovellus.vrcaa.helper

import android.content.SharedPreferences
import androidx.core.content.edit

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

internal val SharedPreferences.PREFERENCE_TAG: String
    get() = "vrcaa_prefs"
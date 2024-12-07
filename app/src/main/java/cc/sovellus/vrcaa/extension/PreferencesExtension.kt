package cc.sovellus.vrcaa.extension

import android.content.SharedPreferences
import androidx.core.content.edit
import cc.sovellus.vrcaa.helper.NotificationHelper
import com.google.gson.Gson

// User Auth //
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

internal var SharedPreferences.authToken: String
    get() = getString("cookies", "")!!
    set(it) = edit { putString("cookies", it) }

internal var SharedPreferences.twoFactorToken: String
    get() = getString("TwoFactorAuth", "")!!
    set(it) = edit { putString("TwoFactorAuth", it) }


// Notification Checks //
internal var SharedPreferences.notificationWhitelist: NotificationHelper.NotificationPermissions
    get() {
        val result = getString("notificationWhitelist", "")
        if (result?.isNotEmpty() == true) {
            return Gson().fromJson(result, NotificationHelper.NotificationPermissions::class.java)
        }
        return NotificationHelper.NotificationPermissions()
    }
    set(it) = edit { putString("notificationWhitelist", Gson().toJson(it)) }

// Discord RPC //
internal var SharedPreferences.discordToken: String
    get() = getString("discordToken", "")!!
    set(it) = edit { putString("discordToken", it) }

internal var SharedPreferences.richPresenceEnabled: Boolean
    get() = getBoolean("richPresenceEnabled", false)
    set(it) = edit { putBoolean("richPresenceEnabled", it) }

internal var SharedPreferences.richPresenceWarningAcknowledged: Boolean
    get() = getBoolean("richPresenceWarningAcknowledged", false)
    set(it) = edit { putBoolean("richPresenceWarningAcknowledged", it) }

internal var SharedPreferences.richPresenceWebhookUrl: String
    get() = getString("richPresenceWebhookUrl", "")!!
    set(it) = edit { putString("richPresenceWebhookUrl", it) }

// Search Preferences //
internal var SharedPreferences.searchFeaturedWorlds: Boolean
    get() = false // getBoolean("searchFeaturedWorlds", false)
    set(it) = edit { putBoolean("searchFeaturedWorlds", it) }

internal var SharedPreferences.sortWorlds: String
    get() = getString("sortWorlds", "relevance")!!
    set(it) = edit { putString("sortWorlds", it) }

internal var SharedPreferences.worldsAmount: Int
    get() = getInt("worldsAmount", 50)
    set(it) = edit { putInt("worldsAmount", it) }

internal var SharedPreferences.usersAmount: Int
    get() = getInt("usersAmount", 50)
    set(it) = edit { putInt("usersAmount", it) }

internal var SharedPreferences.groupsAmount: Int
    get() = getInt("groupsAmount", 50)
    set(it) = edit { putInt("groupsAmount", it) }

internal var SharedPreferences.avatarsAmount: Int
    get() = getInt("groupsAmount", 50)
    set(it) = edit { putInt("groupsAmount", it) }

internal var SharedPreferences.avatarProvider: String
    get() = getString("avatarProviderPreference", "avtrdb")!!
    set(it) = edit { putString("avatarProviderPreference", it) }

internal var SharedPreferences.networkLogging: Boolean
    get() = getBoolean("isDeveloperModeEnabled", false)
    set(it) = edit { putBoolean("isDeveloperModeEnabled", it) }

// Customization //
internal var SharedPreferences.minimalistMode: Boolean
    get() = getBoolean("isMinimalistModeEnabled", false)
    set(it) = edit { putBoolean("isMinimalistModeEnabled", it) }

/* Theme settings (0: Light, 1: Dark, System: 2) */
internal var SharedPreferences.currentThemeOption: Int
    get() = getInt("currentThemeOption", 2)
    set(it) = edit { putInt("currentThemeOption", it) }

/* Tab settings (0: Shown, 1: Folded, 2: Hidden) */
internal var SharedPreferences.homeTab: Int
    get() = getInt("homeTab", 0)
    set(it) = edit { putInt("homeTab", it) }

internal var SharedPreferences.friendsTab: Int
    get() = getInt("friendsTab", 0)
    set(it) = edit { putInt("friendsTab", it) }

internal var SharedPreferences.favoritesTab: Int
    get() = getInt("favoritesTab", 0)
    set(it) = edit { putInt("favoritesTab", it) }

internal var SharedPreferences.FeedTab: Int
    get() = getInt("FeedTab", 0)
    set(it) = edit { putInt("FeedTab", it) }

internal var SharedPreferences.userHome: Int
    get() = getInt("userHome", 0)
    set(it) = edit { putInt("userHome", it) }

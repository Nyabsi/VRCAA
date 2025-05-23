/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.extension

import android.content.SharedPreferences
import androidx.core.content.edit
import cc.sovellus.vrcaa.helper.NotificationHelper
import com.google.gson.Gson

// extend SharedPreferences
internal var SharedPreferences.userCredentials: Pair<String, String>
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

internal var SharedPreferences.notificationWhitelist: NotificationHelper.NotificationPermissions
    get() {
        val result = getString("notificationWhitelist", "")
        if (result?.isNotEmpty() == true) {
            return Gson().fromJson(result, NotificationHelper.NotificationPermissions::class.java)
        }
        return NotificationHelper.NotificationPermissions()
    }
    set(it) = edit { putString("notificationWhitelist", Gson().toJson(it)) }

internal var SharedPreferences.discordToken: String
    get() = getString("discordToken", "")!!
    set(it) = edit { putString("discordToken", it) }

internal var SharedPreferences.richPresenceEnabled: Boolean
    get() = getBoolean("richPresenceEnabled", false)
    set(it) = edit { putBoolean("richPresenceEnabled", it) }

internal var SharedPreferences.searchFeaturedWorlds: Boolean
    get() = getBoolean("searchFeaturedWorlds", false)
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

internal var SharedPreferences.richPresenceWarningAcknowledged: Boolean
    get() = getBoolean("richPresenceWarningAcknowledged", false)
    set(it) = edit { putBoolean("richPresenceWarningAcknowledged", it) }

internal var SharedPreferences.richPresenceWebhookUrl: String
    get() = getString("richPresenceWebhookUrl", "")!!
    set(it) = edit { putString("richPresenceWebhookUrl", it) }

internal var SharedPreferences.avatarProvider: String
    get() = getString("avatarProviderPreference", "avtrdb")!!
    set(it) = edit { putString("avatarProviderPreference", it) }

internal var SharedPreferences.networkLogging: Boolean
    get() = getBoolean("isDeveloperModeEnabled", false)
    set(it) = edit { putBoolean("isDeveloperModeEnabled", it) }

internal var SharedPreferences.minimalistMode: Boolean
    get() = getBoolean("isMinimalistModeEnabled", false)
    set(it) = edit { putBoolean("isMinimalistModeEnabled", it) }

internal var SharedPreferences.currentThemeOption: Int
    get() = getInt("currentThemeOption", 2)
    set(it) = edit { putInt("currentThemeOption", it) }

internal var SharedPreferences.columnCountOption: Int
    get() = getInt("columnCountOption", 0)
    set(value) = edit { putInt("columnCountOption", value) }

internal var SharedPreferences.fixedColumnSize: Int
    get() = getInt("fixedColumnSize", 2)
    set(it) = edit { putInt("fixedColumnSize", it) }

internal var SharedPreferences.crashAnalytics: Boolean
    get() = getBoolean("crashAnalytics", true)
    set(it) = edit { putBoolean("crashAnalytics", it) }
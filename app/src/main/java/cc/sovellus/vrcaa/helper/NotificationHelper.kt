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

package cc.sovellus.vrcaa.helper

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.notificationWhitelist
import com.google.gson.annotations.SerializedName

object NotificationHelper {
    private val preferences: SharedPreferences = App.getContext().getSharedPreferences(App.PREFERENCES_NAME, 0)
    private var notificationCounter: Int = 0

    enum class Intents {
        FRIEND_FLAG_ONLINE,
        FRIEND_FLAG_OFFLINE,
        FRIEND_FLAG_LOCATION,
        FRIEND_FLAG_STATUS
    }

    class NotificationPermissions : ArrayList<NotificationPermissions.NotificationPermission>() {
        data class NotificationPermission(
            @SerializedName("friendId")
            val friendId: String,
            @SerializedName("intents")
            val intents: ArrayList<Intents> = ArrayList()
        )
    }

    fun isOnWhitelist(friendId: String): Boolean {
        return preferences.notificationWhitelist.find { it.friendId == friendId } != null
    }

    fun isIntentEnabled(friendId: String, intent: Intents): Boolean {
        val intents = preferences.notificationWhitelist.find { it.friendId == friendId }?.intents
            ?: return false

        for (target in intents) {
            if (target == intent)
                return true
        }
        return false
    }

    fun enableIntent(friendId: String, intent: Intents) {
        val tmp = preferences.notificationWhitelist
        tmp.find { it.friendId == friendId }
            ?.intents?.add(intent)
        preferences.notificationWhitelist = tmp
    }

    fun disableIntent(friendId: String, intent: Intents) {
        val tmp = preferences.notificationWhitelist
        tmp.find { it.friendId == friendId }
            ?.intents?.remove(tmp.find { it.friendId == friendId }?.intents?.find { it == intent })
        preferences.notificationWhitelist = tmp
    }

    fun addToWhitelist(friendId: String) {
        val tmp = preferences.notificationWhitelist
        tmp.add(
            NotificationPermissions.NotificationPermission(
                friendId = friendId
            )
        )
        preferences.notificationWhitelist = tmp
    }

    fun removeFromWhiteList(friendId: String) {
        val tmp = preferences.notificationWhitelist
        tmp.remove(
            preferences.notificationWhitelist.find { it.friendId == friendId }
        )
        preferences.notificationWhitelist = tmp
    }

    fun pushNotification(
        title: String,
        content: String,
        channel: String,
        persistent: Boolean = false
    ) {
        val flags = NotificationCompat.FLAG_ONGOING_EVENT

        val builder = NotificationCompat.Builder(App.getContext(), channel)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(flags)
            .setOngoing(persistent)

        val notificationManager = NotificationManagerCompat.from(App.getContext())

        if (ActivityCompat.checkSelfPermission(
                App.getContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(notificationCounter, builder.build())
            notificationCounter++
        }
    }

    fun createNotificationChannels() {

        val notificationManager: NotificationManager =
            App.getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val defaultChannel = NotificationChannel(
            CHANNEL_DEFAULT_ID,
            CHANNEL_DEFAULT_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_DEFAULT_DESCRIPTION
        }

        notificationManager.createNotificationChannel(defaultChannel)

        val onlineChannel = NotificationChannel(
            CHANNEL_ONLINE_ID,
            CHANNEL_ONLINE_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_ONLINE_DESCRIPTION
        }

        notificationManager.createNotificationChannel(onlineChannel)

        val offlineChannel = NotificationChannel(
            CHANNEL_OFFLINE_ID,
            CHANNEL_OFFLINE_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_OFFLINE_DESCRIPTION
        }

        notificationManager.createNotificationChannel(offlineChannel)

        val locationChannel = NotificationChannel(
            CHANNEL_LOCATION_ID,
            CHANNEL_LOCATION_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_LOCATION_DESCRIPTION
        }

        notificationManager.createNotificationChannel(locationChannel)

        val statusChannel = NotificationChannel(
            CHANNEL_STATUS_ID,
            CHANNEL_STATUS_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_STATUS_DESCRIPTION
        }

        notificationManager.createNotificationChannel(statusChannel)
    }

    // Default Channel
    const val CHANNEL_DEFAULT_ID = "VRCAA_notifications"
    private const val CHANNEL_DEFAULT_NAME = "Default Notifications"
    private const val CHANNEL_DEFAULT_DESCRIPTION = "Default Notifications"

    // Online Channel
    const val CHANNEL_ONLINE_ID = "VRCAA_notifications_on"
    private const val CHANNEL_ONLINE_NAME = "Online Notifications"
    private const val CHANNEL_ONLINE_DESCRIPTION = "Friend Online notifications"

    // Offline Channel
    const val CHANNEL_OFFLINE_ID = "VRCAA_notifications_off"
    private const val CHANNEL_OFFLINE_NAME = "Offline Notifications"
    private const val CHANNEL_OFFLINE_DESCRIPTION = "Friend Offline Notifications"

    // Location Channel
    const val CHANNEL_LOCATION_ID = "VRCAA_notifications_loc"
    private const val CHANNEL_LOCATION_NAME = "Location Notifications"
    private const val CHANNEL_LOCATION_DESCRIPTION = "Friend Location Notifications"

    // Status Channel
    const val CHANNEL_STATUS_ID = "VRCAA_notifications_sta"
    private const val CHANNEL_STATUS_NAME = "Status Notifications"
    private const val CHANNEL_STATUS_DESCRIPTION = "Friend Status Notifications"
}
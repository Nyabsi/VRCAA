package cc.sovellus.vrcaa.manager

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.notificationWhitelist
import com.google.gson.annotations.SerializedName

class NotificationManager(
    context: Context
): ContextWrapper(context) {
    private val preferences: SharedPreferences = getSharedPreferences("vrcaa_prefs", 0)
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
        channel: String
    ) {
        val flags = NotificationCompat.FLAG_ONGOING_EVENT

        val builder = NotificationCompat.Builder(this, channel)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(flags)

        val notificationManager = NotificationManagerCompat.from(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(notificationCounter, builder.build())
            notificationCounter++
        }
    }

    companion object {
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

        // Update Channel
        const val CHANNEL_UPDATE_ID = "VRCAA_notifications_upd"
        private const val CHANNEL_UPDATE_NAME = "VRCAA Updates"
        private const val CHANNEL_UPDATE_DESCRIPTION = "Shows progress indicator for updates"

        @SuppressLint("NewApi")
        fun createNotificationChannels(context: Context) {

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val defaultChannel = NotificationChannel(
                CHANNEL_DEFAULT_ID,
                CHANNEL_DEFAULT_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = CHANNEL_DEFAULT_DESCRIPTION
            }

            notificationManager.createNotificationChannel(defaultChannel)

            val onlineChannel = NotificationChannel(
                CHANNEL_ONLINE_ID,
                CHANNEL_ONLINE_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_ONLINE_DESCRIPTION
            }

            notificationManager.createNotificationChannel(onlineChannel)

            val offlineChannel = NotificationChannel(
                CHANNEL_OFFLINE_ID,
                CHANNEL_OFFLINE_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_OFFLINE_DESCRIPTION
            }

            notificationManager.createNotificationChannel(offlineChannel)

            val locationChannel = NotificationChannel(
                CHANNEL_LOCATION_ID,
                CHANNEL_LOCATION_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_LOCATION_DESCRIPTION
            }

            notificationManager.createNotificationChannel(locationChannel)

            val statusChannel = NotificationChannel(
                CHANNEL_STATUS_ID,
                CHANNEL_STATUS_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_STATUS_DESCRIPTION
            }

            notificationManager.createNotificationChannel(statusChannel)

            val updateChannel = NotificationChannel(
                CHANNEL_UPDATE_ID,
                CHANNEL_UPDATE_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_UPDATE_DESCRIPTION
            }

            notificationManager.createNotificationChannel(updateChannel)
        }
    }
}
package cc.sovellus.vrcaa

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import cc.sovellus.vrcaa.activity.crash.CrashActivity
import cc.sovellus.vrcaa.activity.crash.GlobalExceptionHandler

class App : Application() {

    companion object {
        // Default Channel
        const val CHANNEL_DEFAULT_ID = "VRCAA_notifications"
        const val CHANNEL_DEFAULT_NAME = "Default Notifications"
        const val CHANNEL_DEFAULT_DESCRIPTION = "Default Notifications"

        // Online Channel
        const val CHANNEL_ONLINE_ID = "VRCAA_notifications_on"
        const val CHANNEL_ONLINE_NAME = "Online Notifications"
        const val CHANNEL_ONLINE_DESCRIPTION = "Friend Online notifications"

        // Offline Channel
        const val CHANNEL_OFFLINE_ID = "VRCAA_notifications_off"
        const val CHANNEL_OFFLINE_NAME = "Offline Notifications"
        const val CHANNEL_OFFLINE_DESCRIPTION = "Friend Offline Notifications"

        // Location Channel
        const val CHANNEL_LOCATION_ID = "VRCAA_notifications_loc"
        const val CHANNEL_LOCATION_NAME = "Location Notifications"
        const val CHANNEL_LOCATION_DESCRIPTION = "Friend Location Notifications"

        // Status Channel
        const val CHANNEL_STATUS_ID = "VRCAA_notifications_sta"
        const val CHANNEL_STATUS_NAME = "Status Notifications"
        const val CHANNEL_STATUS_DESCRIPTION = "Friend Status Notifications"
    }

    override fun onCreate() {
        super.onCreate()

        GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)
        createNotificationChannels()
    }

    @SuppressLint("NewApi")
    private fun createNotificationChannels() {

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
    }
}
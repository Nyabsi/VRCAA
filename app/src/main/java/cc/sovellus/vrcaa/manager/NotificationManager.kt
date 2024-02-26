package cc.sovellus.vrcaa.manager

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.helper.notificationWhitelist
import com.google.gson.annotations.SerializedName

class NotificationManager(
    private val context: Context
) {
    private val preferences: SharedPreferences = context.getSharedPreferences("vrcaa_prefs", 0)
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
            // if "intents" is empty, it will send nothing, even if friend is whitelisted.
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

        val builder = NotificationCompat.Builder(context, channel)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(flags)

        val notificationManager = NotificationManagerCompat.from(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(notificationCounter, builder.build())
            notificationCounter++
        }
    }
}
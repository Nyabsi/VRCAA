package cc.sovellus.vrcaa.ui.screen.notification

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.manager.NotificationManager

class NotificationScreenModel(
    context: Context,
    private val friendId: String
) : ScreenModel {
    private val notificationManager = NotificationManager(context)

    val isNotificationsEnabled = mutableStateOf(notificationManager.isOnWhitelist(friendId))

    val isOnlineIntentEnabled = mutableStateOf(
        notificationManager.isIntentEnabled(
            friendId,
            NotificationManager.Intents.FRIEND_FLAG_ONLINE
        )
    )
    val isOfflineIntentEnabled = mutableStateOf(
        notificationManager.isIntentEnabled(
            friendId,
            NotificationManager.Intents.FRIEND_FLAG_OFFLINE
        )
    )
    val isLocationIntentEnabled = mutableStateOf(
        notificationManager.isIntentEnabled(
            friendId,
            NotificationManager.Intents.FRIEND_FLAG_LOCATION
        )
    )
    val isStatusIntentEnabled = mutableStateOf(
        notificationManager.isIntentEnabled(
            friendId,
            NotificationManager.Intents.FRIEND_FLAG_STATUS
        )
    )


    fun toggleNotifications(toggle: Boolean) {

        if (toggle) {
            notificationManager.addToWhitelist(friendId)
            isNotificationsEnabled.value = true
        } else {
            notificationManager.removeFromWhiteList(friendId)
            isNotificationsEnabled.value = false
        }
    }

    fun toggleIntent(toggle: Boolean, intent: NotificationManager.Intents) {

        if (toggle) {
            notificationManager.enableIntent(friendId, intent)
        } else {
            notificationManager.disableIntent(friendId, intent)
        }

        when (intent) {
            NotificationManager.Intents.FRIEND_FLAG_ONLINE -> isOnlineIntentEnabled.value = toggle
            NotificationManager.Intents.FRIEND_FLAG_OFFLINE -> isOfflineIntentEnabled.value = toggle
            NotificationManager.Intents.FRIEND_FLAG_LOCATION -> isLocationIntentEnabled.value = toggle
            NotificationManager.Intents.FRIEND_FLAG_STATUS -> isStatusIntentEnabled.value = toggle
        }
    }
}
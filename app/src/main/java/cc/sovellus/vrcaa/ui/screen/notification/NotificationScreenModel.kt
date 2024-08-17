package cc.sovellus.vrcaa.ui.screen.notification

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.helper.NotificationHelper

class NotificationScreenModel(
    context: Context,
    private val friendId: String
) : ScreenModel {
    private val notificationHelper = NotificationHelper(context)

    val isNotificationsEnabled = mutableStateOf(notificationHelper.isOnWhitelist(friendId))

    val isOnlineIntentEnabled = mutableStateOf(
        notificationHelper.isIntentEnabled(
            friendId,
            NotificationHelper.Intents.FRIEND_FLAG_ONLINE
        )
    )
    val isOfflineIntentEnabled = mutableStateOf(
        notificationHelper.isIntentEnabled(
            friendId,
            NotificationHelper.Intents.FRIEND_FLAG_OFFLINE
        )
    )
    val isLocationIntentEnabled = mutableStateOf(
        notificationHelper.isIntentEnabled(
            friendId,
            NotificationHelper.Intents.FRIEND_FLAG_LOCATION
        )
    )
    val isStatusIntentEnabled = mutableStateOf(
        notificationHelper.isIntentEnabled(
            friendId,
            NotificationHelper.Intents.FRIEND_FLAG_STATUS
        )
    )


    fun toggleNotifications(toggle: Boolean) {

        if (toggle) {
            notificationHelper.addToWhitelist(friendId)
            isNotificationsEnabled.value = true
        } else {
            notificationHelper.removeFromWhiteList(friendId)
            isNotificationsEnabled.value = false
        }
    }

    fun toggleIntent(toggle: Boolean, intent: NotificationHelper.Intents) {

        if (toggle) {
            notificationHelper.enableIntent(friendId, intent)
        } else {
            notificationHelper.disableIntent(friendId, intent)
        }

        when (intent) {
            NotificationHelper.Intents.FRIEND_FLAG_ONLINE -> isOnlineIntentEnabled.value = toggle
            NotificationHelper.Intents.FRIEND_FLAG_OFFLINE -> isOfflineIntentEnabled.value = toggle
            NotificationHelper.Intents.FRIEND_FLAG_LOCATION -> isLocationIntentEnabled.value = toggle
            NotificationHelper.Intents.FRIEND_FLAG_STATUS -> isStatusIntentEnabled.value = toggle
        }
    }
}
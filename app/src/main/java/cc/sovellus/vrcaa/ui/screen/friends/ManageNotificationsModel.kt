package cc.sovellus.vrcaa.ui.screen.friends

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.manager.NotificationManager

class ManageNotificationsModel(
    context: Context,
    private val friendId: String
) : ScreenModel {
    private val notificationManager = NotificationManager(context)

    val isNotificationsEnabled = mutableStateOf(notificationManager.isOnWhitelist(friendId))
    val isIntentEnabled = mutableMapOf(
        NotificationManager.Intents.FRIEND_FLAG_OFFLINE to notificationManager.isIntentEnabled(friendId, NotificationManager.Intents.FRIEND_FLAG_OFFLINE),
        NotificationManager.Intents.FRIEND_FLAG_ONLINE to notificationManager.isIntentEnabled(friendId, NotificationManager.Intents.FRIEND_FLAG_ONLINE),
        NotificationManager.Intents.FRIEND_FLAG_LOCATION to notificationManager.isIntentEnabled(friendId, NotificationManager.Intents.FRIEND_FLAG_LOCATION)
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
            isIntentEnabled[intent] = true
        } else {
            notificationManager.disableIntent(friendId, intent)
            isIntentEnabled[intent] = false
        }
    }
}
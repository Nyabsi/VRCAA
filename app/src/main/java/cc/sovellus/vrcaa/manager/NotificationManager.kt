package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.vrchat.http.models.Notification
import cc.sovellus.vrcaa.api.vrchat.http.models.NotificationV2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object NotificationManager {

    private val notificationsStateFlow = MutableStateFlow<List<Notification>>(emptyList())
    val notificationsState: StateFlow<List<Notification>> = notificationsStateFlow.asStateFlow()

    private val notificationsV2StateFlow = MutableStateFlow<List<NotificationV2>>(emptyList())
    val notificationsV2State: StateFlow<List<NotificationV2>> = notificationsV2StateFlow.asStateFlow()

    private val notificationCountStateFlow = MutableStateFlow(0)
    val notificationCountState: StateFlow<Int> = notificationCountStateFlow.asStateFlow()

    fun setNotifications(newNotifications: List<Notification>) {
        notificationsStateFlow.update { newNotifications }
        notificationCountStateFlow.update { current -> current + notificationsState.value.size }
    }

    fun setNotificationsV2(newNotifications: List<NotificationV2>) {
        notificationsV2StateFlow.update { newNotifications }
        notificationCountStateFlow.update { current -> current + notificationsV2State.value.size }
    }

    fun addNotification(notification: Notification) {
        val oldSize = notificationsState.value.size
        notificationsStateFlow.update { current ->
            if (current.none { it.id == notification.id })
                listOf(notification) + current
            else
                current
        }
        if (notificationsState.value.size > oldSize)
            notificationCountStateFlow.update { current -> current + 1 }
    }

    fun addNotificationV2(notification: NotificationV2) {
        val oldSize = notificationsV2State.value.size
        notificationsV2StateFlow.update { current ->
            if (current.none { it.id == notification.id })
                listOf(notification) + current
            else
                current
        }
        if (notificationsV2State.value.size > oldSize)
            notificationCountStateFlow.update { current -> current + 1 }
    }

    fun removeNotification(notificationId: String) {
        notificationsStateFlow.update { current -> current.filterNot { it.id == notificationId } }
        notificationCountStateFlow.update { current -> current - 1 }
    }

    fun removeNotificationV2(notificationId: String) {
        notificationsV2StateFlow.update { current -> current.filterNot { it.id == notificationId } }
        notificationCountStateFlow.update { current -> current - 1 }
    }
}
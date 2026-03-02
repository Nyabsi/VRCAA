package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.vrchat.http.models.Notification
import cc.sovellus.vrcaa.api.vrchat.http.models.NotificationV2
import cc.sovellus.vrcaa.base.BaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object NotificationManager: BaseManager<NotificationManager.NotificationListener>() {

    interface NotificationListener {
        fun onUpdateNotifications(notifications: List<Notification>)
        fun onUpdateNotificationsV2(notifications: List<NotificationV2>)
        fun onUpdateNotificationCount(count: Int)
    }

    private val notificationLock = Any()
    private val notificationLockV2 = Any()

    private val notifications: MutableList<Notification> = mutableListOf()
    private val notificationsV2: MutableList<NotificationV2> = mutableListOf()

    private val notificationsStateFlow = MutableStateFlow<List<Notification>>(emptyList())
    val notificationsState: StateFlow<List<Notification>> = notificationsStateFlow.asStateFlow()

    private val notificationsV2StateFlow = MutableStateFlow<List<NotificationV2>>(emptyList())
    val notificationsV2State: StateFlow<List<NotificationV2>> = notificationsV2StateFlow.asStateFlow()

    private val notificationCountStateFlow = MutableStateFlow(0)
    val notificationCountState: StateFlow<Int> = notificationCountStateFlow.asStateFlow()

    fun setNotifications(newNotifications: List<Notification>) {
        synchronized(notificationLock) {
            notifications.clear()
            notifications.addAll(newNotifications)
        }
        publishNotifications()
    }

    fun setNotificationsV2(newNotifications: List<NotificationV2>) {
        synchronized(notificationLockV2) {
            notificationsV2.clear()
            notificationsV2.addAll(newNotifications)
        }
        publishNotificationsV2()
        notifyListeners()
    }

    fun addNotification(notification: Notification) {
        synchronized(notificationLock) {
            if (notifications.none { it.id == notification.id }) {
                notifications.add(notification)
            }
        }
        publishNotifications()
    }

    fun addNotificationV2(notification: NotificationV2) {
        synchronized(notificationLockV2) {
            if (notificationsV2.none { it.id == notification.id }) {
                notificationsV2.add(notification)
            }
        }
        publishNotificationsV2()
        notifyListeners()
    }

    fun removeNotification(notificationId: String) {
        synchronized(notificationLock) {
            notifications.removeIf { it.id == notificationId }
        }
        publishNotifications()
    }

    fun removeNotificationV2(notificationId: String) {
        synchronized(notificationLockV2) {
            notificationsV2.removeIf { it.id == notificationId }
        }
        publishNotificationsV2()
        notifyListeners()
    }

    fun getNotifications(): List<Notification> {
        return notificationsStateFlow.value
    }

    fun getNotificationsV2(): List<NotificationV2> {
        return notificationsV2StateFlow.value
    }

    private fun publishNotifications() {
        synchronized(notificationLock) {
            notificationsStateFlow.value = notifications.toList()
        }
        notifyListeners()
    }

    private fun publishNotificationsV2() {
        synchronized(notificationLockV2) {
            notificationsV2StateFlow.value = notificationsV2.toList()
        }
    }

    private fun notifyListeners() {
        val i1 = notificationsStateFlow.value
        val i2 = notificationsV2StateFlow.value
        val count = i1.size + i2.size
        notificationCountStateFlow.value = count
        getListeners().forEach {
            it.onUpdateNotifications(i1)
            it.onUpdateNotificationsV2(i2)
            it.onUpdateNotificationCount(count)
        }
    }
}
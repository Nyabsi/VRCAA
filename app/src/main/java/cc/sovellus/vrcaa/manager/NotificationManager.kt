package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.vrchat.http.models.Notification
import cc.sovellus.vrcaa.api.vrchat.http.models.NotificationV2
import cc.sovellus.vrcaa.base.BaseManager

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

    fun setNotifications(newNotifications: List<Notification>) {
        synchronized(notificationLock) {
            notifications.clear()
            notifications.addAll(newNotifications)
        }
        notifyListeners()
    }

    fun setNotificationsV2(newNotifications: List<NotificationV2>) {
        synchronized(notificationLockV2) {
            notificationsV2.clear()
            notificationsV2.addAll(newNotifications)
        }
        notifyListeners()
    }

    fun addNotification(notification: Notification) {
        synchronized(notificationLock) {
            notifications.add(notification)
        }
        notifyListeners()
    }

    fun addNotificationV2(notification: NotificationV2) {
        synchronized(notificationLockV2) {
            notificationsV2.add(notification)
        }
        notifyListeners()
    }

    fun removeNotification(notificationId: String) {
        synchronized(notificationLock) {
            notifications.removeIf { it.id == notificationId }
        }
        notifyListeners()
    }

    fun removeNotificationV2(notificationId: String) {
        synchronized(notificationLockV2) {
            notificationsV2.removeIf { it.id == notificationId }
        }
        notifyListeners()
    }

    fun getNotifications(): List<Notification> {
        synchronized(notificationLock) {
            return notifications.toList()
        }
    }

    fun getNotificationsV2(): List<NotificationV2> {
        synchronized(notificationLockV2) {
            return notificationsV2.toList()
        }
    }

    private fun notifyListeners() {
        getListeners().forEach {
            it.onUpdateNotifications(notifications.toList())
            it.onUpdateNotificationsV2(notificationsV2.toList())
            it.onUpdateNotificationCount(notifications.size + notificationsV2.size)
        }
    }
}
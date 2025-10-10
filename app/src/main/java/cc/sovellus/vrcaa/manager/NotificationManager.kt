package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.vrchat.http.models.Notification
import cc.sovellus.vrcaa.base.BaseManager

object NotificationManager: BaseManager<NotificationManager.NotificationListener>() {

    interface NotificationListener {
        fun onUpdateNotifications(notifications: List<Notification>)
    }

    private val notificationLock = Any()
    private val notifications: MutableList<Notification> = mutableListOf()

    fun setNotifications(newNotifications: List<Notification>) {
        val snapshot: List<Notification>
        synchronized(notificationLock) {
            notifications.clear()
            notifications.addAll(newNotifications)
            snapshot = notifications.toList()
        }
        notifyListeners(snapshot)
    }

    fun addNotification(notification: Notification) {
        val snapshot: List<Notification>?
        synchronized(notificationLock) {
            if (notifications.none { it.id == notification.id }) {
                notifications.add(notification)
                snapshot = notifications.toList()
            } else {
                snapshot = null
            }
        }
        snapshot?.let {
            notifyListeners(it)
        }
    }

    fun removeNotification(notificationId: String) {
        val snapshot: List<Notification>?
        synchronized(notificationLock) {
            val removed = notifications.removeIf { it.id == notificationId }
            snapshot = if (removed) notifications.toList() else null
        }
        snapshot?.let {
            notifyListeners(it)
        }
    }

    fun getNotifications(): List<Notification> {
        synchronized(notificationLock) {
            return notifications.toList()
        }
    }

    private fun notifyListeners(snapshot: List<Notification>) {
        getListeners().forEach { it.onUpdateNotifications(snapshot) }
    }
}
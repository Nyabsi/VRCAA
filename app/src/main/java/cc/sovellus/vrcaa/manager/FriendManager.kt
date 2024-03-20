package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.http.models.LimitedUser

object FriendManager {

    @Volatile private var friendListener: FriendListener? = null
    @Volatile private var syncedFriends: MutableList<LimitedUser> = ArrayList()

    interface FriendListener {
        fun onUpdateFriends(friends: MutableList<LimitedUser>, offline: Boolean)
    }

    @Synchronized
    fun setFriendListener(listener: FriendListener) {
        synchronized(listener) {
            friendListener = listener
        }
    }

    @Synchronized
    fun setFriends(friends: MutableList<LimitedUser>) {
        synchronized(friends) {
            syncedFriends = friends
        }
    }

    @Synchronized
    fun addFriend(friend: LimitedUser) {
        if (syncedFriends.find { it.id == friend.id } == null) {
            synchronized(friend) {
                syncedFriends.add(friend)
                friendListener?.onUpdateFriends(syncedFriends, false)
            }
        }
    }

    @Synchronized
    fun removeFriend(userId: String) {
        synchronized(userId) {
            val friend = syncedFriends.find { it.id == userId }

            friend?.let {
                syncedFriends.remove(friend)
            }

            friendListener?.onUpdateFriends(syncedFriends, false)
        }
    }

    fun getFriend(userId: String): LimitedUser? {
        return syncedFriends.find { it.id == userId }
    }

    @Synchronized
    fun updateFriend(friend: LimitedUser) {
        synchronized(friend) {
            val tmp = syncedFriends.find { it.id == friend.id }
            tmp?.let {
                syncedFriends.set(syncedFriends.indexOf(tmp), friend)
            }
            friendListener?.onUpdateFriends(syncedFriends, false)
        }
    }

    fun getFriends(): MutableList<LimitedUser> {
        return syncedFriends
    }
}
package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.models.LimitedUser

class FriendManager {

    interface FriendListener {
        fun onUpdateFriends(friends: MutableList<LimitedUser>, offline: Boolean)
    }

    companion object {
        @Volatile
        private var friendListener: FriendListener? = null
        @Volatile
        private var syncedFriends: MutableList<LimitedUser> = ArrayList()
    }

    fun setFriendListener(listener: FriendListener) {
        synchronized(listener) {
            friendListener = listener
        }
    }

    fun setFriends(friends: MutableList<LimitedUser>) {
        synchronized(friends) {
            syncedFriends = friends
        }
    }

    fun addFriend(friend: LimitedUser) {
        if (syncedFriends.find { it.id == friend.id } == null) {
            synchronized(friend) {
                syncedFriends.add(friend)
                friendListener?.onUpdateFriends(syncedFriends, false)
            }
        }
    }

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
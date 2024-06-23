package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser

object FriendManager {

    @Volatile private var friendListeners: MutableList<FriendListener> = mutableListOf()
    @Volatile private var friends: MutableList<LimitedUser> = ArrayList()

    interface FriendListener {
        fun onUpdateFriends(friends: MutableList<LimitedUser>)
    }

    @Synchronized
    fun addFriendListener(listener: FriendListener) {
        synchronized(listener) {
            friendListeners.add(listener)
        }
    }

    @Synchronized
    fun setFriends(friends: MutableList<LimitedUser>) {
        synchronized(friends) {
            this.friends = friends
            friendListeners.map {
                it.onUpdateFriends(friends)
            }
        }
    }

    @Synchronized
    fun addFriend(friend: LimitedUser) {
        if (friends.find { it.id == friend.id } == null) {
            synchronized(friend) {
                friends.add(friend)
                friendListeners.map {
                    it.onUpdateFriends(friends)
                }
            }
        }
    }

    @Synchronized
    fun removeFriend(userId: String) {
        synchronized(userId) {
            val friend = friends.find { it.id == userId }

            friend?.let {
                friends.remove(friend)
            }

            friendListeners.map {
                it.onUpdateFriends(friends)
            }
        }
    }

    fun getFriend(userId: String): LimitedUser? {
        return friends.find { it.id == userId }
    }

    @Synchronized
    fun updateFriend(friend: LimitedUser) {
        synchronized(friend) {
            val it = friends.find { it.id == friend.id }
            it?.let {
                friend.isFavorite = it.isFavorite
                friends.set(friends.indexOf(it), friend)
            }

            friendListeners.map {
                it.onUpdateFriends(friends)
            }
        }
    }

    @Synchronized
    fun updateLocation(userId: String, location: String) {
        synchronized(friends) {
            val it = friends.find { it.id == userId }
            it?.let {
                it.location = location
            }

            friendListeners.map {
                it.onUpdateFriends(friends)
            }
        }
    }

    @Synchronized
    fun updateStatus(userId: String, status: String) {
        synchronized(friends) {
            val it = friends.find { it.id == userId }
            it?.let {
                it.status = status
                friends.set(friends.indexOf(it), it)
            }

            friendListeners.map {
                it.onUpdateFriends(friends)
            }
        }
    }

    fun getFriends(): MutableList<LimitedUser> {
        return friends
    }
}
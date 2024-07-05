package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.models.websocket.UpdateUser

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
    fun updateFriend(friend: UpdateUser) {
        synchronized(friend) {
            val it = friends.find { it.id == friend.id }
            it?.let {
                it.status = friend.status
                it.statusDescription = friend.statusDescription
                it.bio = friend.bio
                it.bioLinks = it.bioLinks
                it.tags = friend.tags
                it.profilePicOverride = friend.profilePicOverride
                it.profilePicOverrideThumbnail = friend.profilePicOverrideThumbnail
                it.pronouns = friend.pronouns
                it.displayName = friend.displayName
                it.userIcon = friend.userIcon
                it.state = friend.state
                friends.set(friends.indexOf(it), it)
            }
        }
        friendListeners.map {
            it.onUpdateFriends(friends)
        }
    }

    @Synchronized
    fun updateLocation(userId: String, location: String) {
        synchronized(friends) {
            val it = friends.find { it.id == userId }
            it?.let {
                it.location = location
            }
        }
        friendListeners.map {
            it.onUpdateFriends(friends)
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
        }
        friendListeners.map {
            it.onUpdateFriends(friends)
        }
    }

    fun getFriends(): MutableList<LimitedUser> {
        return friends
    }
}
package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.vrchat.models.Friend
import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.models.websocket.UpdateUser

object FriendManager {

    private var friendListeners: MutableList<FriendListener> = mutableListOf()
    private var friends: MutableList<Friend> = ArrayList()

    interface FriendListener {
        fun onUpdateFriends(friends: MutableList<Friend>)
    }

    fun addFriendListener(listener: FriendListener) {
        friendListeners.add(listener)
    }

    fun setFriends(friends: MutableList<Friend>) {
        this.friends = friends
        friendListeners.map {
            it.onUpdateFriends(friends)
        }
    }

    fun addFriend(friend: Friend) {
        if (friends.find { it.id == friend.id } == null) {
            friends.add(friend)
            friendListeners.map {
                it.onUpdateFriends(friends)
            }
        }
    }

    fun removeFriend(userId: String) {
        val friend = friends.find { it.id == userId }

        friend?.let {
            friends.remove(friend)
        }

        friendListeners.map {
            it.onUpdateFriends(friends)
        }
    }

    fun getFriend(userId: String): Friend? {
        return friends.find { it.id == userId }
    }

    fun updateFriend(friend: Friend) {
        val it = friends.find { it.id == friend.id }
        it?.let {
            it.status = friend.status
            it.statusDescription = friend.statusDescription
            it.bio = friend.bio
            it.tags = friend.tags
            it.profilePicOverride = friend.profilePicOverride
            it.profilePicOverrideThumbnail = friend.profilePicOverrideThumbnail
            it.displayName = friend.displayName
            it.userIcon = friend.userIcon
            friends.set(friends.indexOf(it), it)
        }

        friendListeners.map {
            it.onUpdateFriends(friends)
        }
    }

    fun updateLocation(userId: String, location: String) {
        val it = friends.find { it.id == userId }
        it?.let {
            it.location = location
            friends.set(friends.indexOf(it), it)
        }
        friendListeners.map {
            it.onUpdateFriends(friends)
        }
    }

    fun updateStatus(userId: String, status: String) {
        val it = friends.find { it.id == userId }
        it?.let {
            it.status = status
            friends.set(friends.indexOf(it), it)
        }

        friendListeners.map {
            it.onUpdateFriends(friends)
        }
    }

    fun getFriends(): MutableList<Friend> {
        return friends
    }
}
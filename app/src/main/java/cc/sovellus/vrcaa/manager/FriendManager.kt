package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.base.BaseManager

object FriendManager : BaseManager<FriendManager.FriendListener>() {

    interface FriendListener {
        fun onUpdateFriends(friends: MutableList<Friend>)
    }

    private var friends: MutableList<Friend> = ArrayList()

    fun setFriends(friends: MutableList<Friend>) {
        this.friends = friends
        getListeners().forEach { listener ->
            listener.onUpdateFriends(friends)
        }
    }

    fun addFriend(friend: Friend) {
        if (friends.find { it.id == friend.id } == null) {
            friends.add(friend)
            getListeners().forEach { listener ->
                listener.onUpdateFriends(friends)
            }
        }
    }

    fun removeFriend(userId: String) {
        val friend = friends.find { it.id == userId }

        friend?.let {
            friends.remove(friend)
        }

        getListeners().forEach { listener ->
            listener.onUpdateFriends(friends)
        }
    }

    fun getFriend(userId: String): Friend? {
        return friends.find { it.id == userId }
    }

    fun updateFriend(friend: Friend) {
        val it = friends.find { it.id == friend.id }
        it?.let {
            friend.platform = it.platform
            friend.location = it.location
            friends.set(friends.indexOf(it), friend)
        }

        getListeners().forEach { listener ->
            listener.onUpdateFriends(friends)
        }
    }

    fun updateLocation(userId: String, location: String) {
        val it = friends.find { it.id == userId }
        it?.let {
            it.location = location
            friends.set(friends.indexOf(it), it)
        }
        getListeners().forEach { listener ->
            listener.onUpdateFriends(friends)
        }
    }

    fun updateStatus(userId: String, status: String) {
        val it = friends.find { it.id == userId }
        it?.let {
            it.status = status
            friends.set(friends.indexOf(it), it)
        }

        getListeners().forEach { listener ->
            listener.onUpdateFriends(friends)
        }
    }

    fun updatePlatform(userId: String, platform: String) {
        val it = friends.find { it.id == userId }
        it?.let {
            it.platform = platform
            friends.set(friends.indexOf(it), it)
        }

        getListeners().forEach { listener ->
            listener.onUpdateFriends(friends)
        }
    }

    fun getFriends(): MutableList<Friend> {
        return friends
    }
}

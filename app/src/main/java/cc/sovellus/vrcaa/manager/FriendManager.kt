package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.api.vrchat.pipeline.models.PartialFriend
import cc.sovellus.vrcaa.base.BaseManager
import cc.sovellus.vrcaa.helper.JsonHelper

object FriendManager : BaseManager<FriendManager.FriendListener>() {

    interface FriendListener {
        fun onUpdateFriends(friends: List<Friend>)
    }

    private val friendsLock = Any()
    private val friends: MutableList<Friend> = mutableListOf()

    fun setFriends(newFriends: List<Friend>) {
        val snapshot: List<Friend>
        synchronized(friendsLock) {
            friends.clear()
            friends.addAll(newFriends)
            snapshot = friends.toList()
        }
        notifyListeners(snapshot)
    }

    fun addFriend(friend: Friend) {
        val snapshot: List<Friend>?
        synchronized(friendsLock) {
            if (friends.none { it.id == friend.id }) {
                friends.add(friend)
                snapshot = friends.toList()
            } else {
                snapshot = null
            }
        }
        snapshot?.let { notifyListeners(it) }
    }

    fun removeFriend(userId: String) {
        val snapshot: List<Friend>?
        synchronized(friendsLock) {
            val removed = friends.removeIf { it.id == userId }
            snapshot = if (removed) friends.toList() else null
        }
        snapshot?.let { notifyListeners(it) }
    }

    fun updateFriend(partial: PartialFriend) {
        val snapshot: List<Friend>?
        synchronized(friendsLock) {
            val index = friends.indexOfFirst { it.id == partial.id }
            if (index != -1) {
                friends[index] = JsonHelper.mergeDiffJson(friends[index], partial, Friend::class.java)
                snapshot = friends.toList()
            } else {
                snapshot = null
            }
        }
        snapshot?.let { notifyListeners(it) }
    }

    fun updateLocation(userId: String, location: String) {
        val snapshot: List<Friend>?
        synchronized(friendsLock) {
            val index = friends.indexOfFirst { it.id == userId }
            if (index != -1) {
                friends[index] = friends[index].copy(location = location)
                snapshot = friends.toList()
            } else {
                snapshot = null
            }
        }
        snapshot?.let { notifyListeners(it) }
    }

    fun updatePlatform(userId: String, platform: String) {
        val snapshot: List<Friend>?
        synchronized(friendsLock) {
            val index = friends.indexOfFirst { it.id == userId }
            if (index != -1) {
                friends[index] = friends[index].copy(platform = platform)
                snapshot = friends.toList()
            } else {
                snapshot = null
            }
        }
        snapshot?.let { notifyListeners(it) }
    }

    fun getFriend(userId: String): Friend? {
        synchronized(friendsLock) {
            return friends.find { it.id == userId }
        }
    }

    fun getFriends(): List<Friend> {
        synchronized(friendsLock) {
            return friends.toList()
        }
    }

    private fun notifyListeners(snapshot: List<Friend>) {
        getListeners().forEach { it.onUpdateFriends(snapshot) }
    }
}
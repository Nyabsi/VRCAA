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
        synchronized(friendsLock) {
            friends.clear()
            friends.addAll(newFriends)
        }
        notifyListeners()
    }

    fun addFriend(friend: Friend) {
        synchronized(friendsLock) {
            if (friends.none { it.id == friend.id }) {
                friends.add(friend)
            }
        }
        notifyListeners()
    }

    fun removeFriend(userId: String) {
        synchronized(friendsLock) {
            friends.removeIf { it.id == userId }
        }
        notifyListeners()
    }

    fun updateFriend(partial: PartialFriend) {
        synchronized(friendsLock) {
            val index = friends.indexOfFirst { it.id == partial.id }
            if (index != -1) {
                friends[index] = JsonHelper.mergeDiffJson(friends[index], partial, Friend::class.java)
            }
        }
        notifyListeners()
    }

    fun updateLocation(userId: String, location: String) {
        synchronized(friendsLock) {
            val index = friends.indexOfFirst { it.id == userId }
            if (index != -1) {
                friends[index] = friends[index].copy(location = location)
            }
        }
        notifyListeners()
    }

    fun updatePlatform(userId: String, platform: String) {
        synchronized(friendsLock) {
            val index = friends.indexOfFirst { it.id == userId }
            if (index != -1) {
                friends[index] = friends[index].copy(platform = platform)
            }
        }
        notifyListeners()
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

    private fun notifyListeners() {
        val snapshot = getFriends()
        getListeners().forEach { it.onUpdateFriends(snapshot) }
    }
}
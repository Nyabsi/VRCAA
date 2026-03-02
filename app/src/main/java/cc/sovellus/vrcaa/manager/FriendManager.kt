package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.api.vrchat.pipeline.models.PartialFriend
import cc.sovellus.vrcaa.base.BaseManager
import cc.sovellus.vrcaa.helper.JsonHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object FriendManager : BaseManager<FriendManager.FriendListener>() {

    interface FriendListener {
        fun onUpdateFriends(friends: List<Friend>)
    }

    private val friendsLock = Any()
    private val friends: MutableList<Friend> = mutableListOf()
    private val friendsStateFlow = MutableStateFlow<List<Friend>>(emptyList())

    val friendsState: StateFlow<List<Friend>> = friendsStateFlow.asStateFlow()

    fun setFriends(newFriends: List<Friend>) {
        synchronized(friendsLock) {
            friends.clear()
            friends.addAll(newFriends)
        }
        publishFriends()
    }

    fun addFriend(friend: Friend) {
        synchronized(friendsLock) {
            if (friends.none { it.id == friend.id }) {
                friends.add(friend)
            }
        }
        publishFriends()
    }

    fun removeFriend(userId: String) {
        synchronized(friendsLock) {
            friends.removeIf { it.id == userId }
        }
        publishFriends()
    }

    fun updateFriend(partial: PartialFriend) {
        synchronized(friendsLock) {
            val index = friends.indexOfFirst { it.id == partial.id }
            if (index != -1) {
                friends[index] = JsonHelper.mergeDiffJson(friends[index], partial, Friend::class.java)
            }
        }
        publishFriends()
    }

    fun updateLocation(userId: String, location: String) {
        synchronized(friendsLock) {
            val index = friends.indexOfFirst { it.id == userId }
            if (index != -1) {
                friends[index] = friends[index].copy(location = location)
            }
        }
        publishFriends()
    }

    fun updatePlatform(userId: String, platform: String) {
        synchronized(friendsLock) {
            val index = friends.indexOfFirst { it.id == userId }
            if (index != -1) {
                friends[index] = friends[index].copy(platform = platform)
            }
        }
        publishFriends()
    }

    fun getFriend(userId: String): Friend? {
        synchronized(friendsLock) {
            return friends.find { it.id == userId }
        }
    }

    fun getFriends(): List<Friend> {
        return friendsStateFlow.value
    }

    private fun publishFriends() {
        synchronized(friendsLock) {
            friendsStateFlow.value = friends.toList()
        }
        val snapshot = friendsStateFlow.value
        getListeners().forEach { it.onUpdateFriends(snapshot) }
    }
}
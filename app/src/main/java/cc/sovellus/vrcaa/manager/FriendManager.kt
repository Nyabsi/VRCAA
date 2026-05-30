package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.api.vrchat.pipeline.models.PartialFriend
import cc.sovellus.vrcaa.helper.JsonHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object FriendManager {

    private val friendsStateFlow = MutableStateFlow<List<Friend>>(emptyList())
    val friendsState: StateFlow<List<Friend>> = friendsStateFlow.asStateFlow()

    fun setFriends(newFriends: List<Friend>) {
        friendsStateFlow.update { newFriends }
    }

    fun addFriend(friend: Friend) {
        friendsStateFlow.update { current ->
            if (current.none { it.id == friend.id })
                listOf(friend) + current
            else
                current
        }
    }

    fun removeFriend(userId: String) {
        friendsStateFlow.update { current -> current.filterNot { it.id == userId } }
    }

    fun updateFriend(partial: PartialFriend) {
        friendsStateFlow.update { current ->
            val index = current.indexOfFirst { it.id == partial.id }
            if (index != -1) {
                current.toMutableList().apply {
                    this[index] = JsonHelper.mergeDiffJson(this[index], partial, Friend::class.java)
                }
            } else current
        }
    }

    fun updateLocation(userId: String, location: String) {
        friendsStateFlow.update { current ->
            val index = current.indexOfFirst { it.id == userId }
            if (index != -1) {
                current.toMutableList().apply {
                    this[index] = this[index].copy(location = location)
                }
            } else current
        }
    }

    fun updatePlatform(userId: String, platform: String) {
        friendsStateFlow.update { current ->
            val index = current.indexOfFirst { it.id == userId }
            if (index != -1) {
                current.toMutableList().apply {
                    this[index] = this[index].copy(platform = platform)
                }
            } else current
        }
    }

    fun getFriend(userId: String): Friend? {
        return friendsState.value.find { it.id == userId }
    }
}
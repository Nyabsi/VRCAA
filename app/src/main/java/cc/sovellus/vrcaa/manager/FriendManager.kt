/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.api.vrchat.pipeline.models.PartialFriend
import cc.sovellus.vrcaa.base.BaseManager
import cc.sovellus.vrcaa.helper.JsonHelper

object FriendManager : BaseManager<FriendManager.FriendListener>() {

    interface FriendListener {
        fun onUpdateFriends(friends: List<Friend>)
    }

    private var friends: MutableList<Friend> = ArrayList()

    fun setFriends(friends: MutableList<Friend>) {
        this.friends = friends
        val listSnapshot = FriendManager.friends.toList()
        getListeners().forEach { listener ->
            listener.onUpdateFriends(listSnapshot)
        }
    }

    fun addFriend(friend: Friend) {
        if (friends.find { it.id == friend.id } == null) {
            friends.add(friend)

            val listSnapshot = friends.toList()
            getListeners().forEach { listener ->
                listener.onUpdateFriends(listSnapshot)
            }
        }
    }

    fun removeFriend(userId: String) {
        val friend = friends.find { it.id == userId }

        friend?.let {
            friends.remove(friend)
        }

        val listSnapshot = friends.toList()
        getListeners().forEach { listener ->
            listener.onUpdateFriends(listSnapshot)
        }
    }

    fun getFriend(userId: String): Friend? {
        return friends.find { it.id == userId }
    }

    fun updateFriend(friend: PartialFriend) {

        val it = friends.find { it.id == friend.id }
        it?.let {
            try {
                val result = JsonHelper.mergeDiffJson<Friend, PartialFriend>(it, friend, Friend::class.java)
                friends.set(friends.indexOf(it), result)
            } catch (_: Exception) { }
        }

        val listSnapshot = friends.toList()
        getListeners().forEach { listener ->
            listener.onUpdateFriends(listSnapshot)
        }
    }

    fun updateLocation(userId: String, location: String) {
        val it = friends.find { it.id == userId }
        it?.let {
            it.location = location
            friends.set(friends.indexOf(it), it)
        }

        val listSnapshot = friends.toList()
        getListeners().forEach { listener ->
            listener.onUpdateFriends(listSnapshot)
        }
    }

    fun updatePlatform(userId: String, platform: String) {
        val it = friends.find { it.id == userId }
        it?.let {
            it.platform = platform
            friends.set(friends.indexOf(it), it)
        }

        val listSnapshot = friends.toList()
        getListeners().forEach { listener ->
            listener.onUpdateFriends(listSnapshot)
        }
    }

    fun getFriends(): List<Friend> {
        val listSnapshot = friends.toList()
        return listSnapshot
    }
}

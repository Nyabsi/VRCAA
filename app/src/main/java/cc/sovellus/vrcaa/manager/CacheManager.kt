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

import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.api.vrchat.http.models.User
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import cc.sovellus.vrcaa.base.BaseManager
import cc.sovellus.vrcaa.helper.JsonHelper
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.map

object CacheManager : BaseManager<CacheManager.CacheListener>() {

    interface CacheListener {
        fun updateRecentlyVisitedWorlds(worlds: List<WorldCache>) { }
        fun startCacheRefresh() { }
        fun endCacheRefresh() { }
        fun profileUpdated(profile: User) { }
    }

    data class WorldCache(
        val id: String,
        var name: String = "???",
        var thumbnailUrl: String = "",
    )

    private val worldListLock = Any()
    private val recentWorldLock = Any()

    private var profile: User? = null
    private var worldList: MutableList<WorldCache> = CopyOnWriteArrayList()
    private var recentWorldList: MutableList<WorldCache> = CopyOnWriteArrayList()

    private var cacheHasBeenBuilt: Boolean = false

    suspend fun buildCache() = coroutineScope {
        cacheHasBeenBuilt = false

        App.setLoadingText(R.string.global_app_default_loading_text)

        getListeners().forEach { listener ->
            listener.startCacheRefresh()
        }

        val user = async { api.auth.fetchCurrentUser() }.await()
        val onlineFriends = async { api.friends.fetchFriends(false) }.await()
        val offlineFriends = async { api.friends.fetchFriends(true) }.await()
        val recentWorlds = async { api.worlds.fetchRecent() }.await()
        async { FavoriteManager.refresh() }.await()

        val userLocations = onlineFriends.mapNotNull { friend ->
            friend.location.takeIf { it.contains("wrld_") }?.split(":")?.getOrNull(0)
        }.distinct().map { worldId ->
            async {
                api.worlds.fetchWorldByWorldId(worldId)
            }
        }.awaitAll()

        profile = user

        val friendList: MutableList<Friend> = mutableListOf()
        friendList.addAll((onlineFriends + offlineFriends))
        FriendManager.setFriends(friendList)

        worldList.addAll(userLocations.filterNotNull().filter { !isWorldCached(it.id) }.map {
            WorldCache(it.id).apply {
                name = it.name
                thumbnailUrl = it.thumbnailImageUrl
            }
        })

        recentWorldList.addAll(recentWorlds.map {
            WorldCache(it.id).apply {
                name = it.name
                thumbnailUrl = it.thumbnailImageUrl
            }
        })

        getListeners().forEach { listener ->
            listener.endCacheRefresh()
        }

        cacheHasBeenBuilt = true
    }


    fun isBuilt(): Boolean {
        return cacheHasBeenBuilt
    }

    fun isWorldCached(worldId: String): Boolean {
        val listSnapshot = worldList.toList()
        return listSnapshot.any { it.id == worldId }
    }

    fun getWorld(worldId: String): WorldCache {
        val listSnapshot = worldList.toList()
        return listSnapshot.firstOrNull { it.id == worldId } ?: WorldCache("invalid")
    }

    fun addWorld(world: World) {
        synchronized(worldListLock) {
            val cache = WorldCache(world.id).apply {
                name = world.name
                thumbnailUrl = world.thumbnailImageUrl
            }
            worldList.add(cache)
        }
    }

    fun updateWorld(world: World) {
        synchronized(worldListLock) {
            val index = worldList.indexOf(worldList.find { it.id == world.id })
            worldList[index] = WorldCache(world.id).apply {
                name = world.name
                thumbnailUrl = world.thumbnailImageUrl
            }
        }
    }

    fun getProfile(): User? {
        return profile
    }

    fun updateProfile(profile: User) {
        this.profile?.let {
            val result = JsonHelper.mergeJson<User>(it, profile, User::class.java)
            this.profile = result
            getListeners().forEach { listener ->
                listener.profileUpdated(result)
            }
        }
    }

    fun getRecentWorlds(): List<WorldCache> {
        return recentWorldList
    }

    fun addRecentWorld(world: World) {
        synchronized(recentWorldLock) {
            recentWorldList.removeIf { it.id == world.id }
            recentWorldList.add(0,
                WorldCache(world.id).apply {
                    name = world.name
                    thumbnailUrl = world.thumbnailImageUrl
                }
            )
        }

        val listSnapshot = recentWorldList.toList()
        getListeners().forEach { listener ->
            listener.updateRecentlyVisitedWorlds(listSnapshot)
        }
    }
}

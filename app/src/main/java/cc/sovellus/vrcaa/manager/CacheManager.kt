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
import java.util.concurrent.CopyOnWriteArrayList

object CacheManager : BaseManager<CacheManager.CacheListener>() {

    interface CacheListener {
        fun recentlyVisitedUpdated(worlds: MutableList<WorldCache>) { }
        fun startCacheRefresh() { }
        fun endCacheRefresh() { }
        fun profileUpdated(profile: User) { }
    }

    data class WorldCache(
        val id: String,
        var name: String = "???",
        var thumbnailUrl: String = "",
    )

    private var profile: User? = null
    private var worldList: MutableList<WorldCache> = CopyOnWriteArrayList()
    private var recentWorldList: MutableList<WorldCache> = CopyOnWriteArrayList()

    private var cacheHasBeenBuilt: Boolean = false

    suspend fun buildCache() {

        getListeners().forEach { listener ->
            listener.startCacheRefresh()
        }

        App.setLoadingText(R.string.loading_text_profile)
        api.auth.fetchCurrentUser()?.let { profile = it }

        val friendList: MutableList<Friend> = mutableListOf()
        val recentWorlds: MutableList<WorldCache> = mutableListOf()

        App.setLoadingText(R.string.loading_text_online_friends)

        val t = api.friends.fetchFriends(false)

        t.forEach { friend->
            if (friend.location.contains("wrld_")) {
                val world = api.worlds.fetchWorldByWorldId(friend.location.split(":")[0])
                world?.let {
                    worldList.add(WorldCache(world.id).apply {
                        name = world.name
                        thumbnailUrl = world.thumbnailImageUrl
                    })
                }
            }
            friendList.add(friend)
        }

        App.setLoadingText(R.string.loading_text_offline_friends)

        api.friends.fetchFriends(true).forEach { friend->
            friendList.add(friend)
        }

        FriendManager.setFriends(friendList)

        App.setLoadingText(R.string.loading_text_recently_visited)

        api.worlds.fetchRecent().forEach { world->
            recentWorlds.add(WorldCache(world.id).apply {
                name = world.name
                thumbnailUrl = world.thumbnailImageUrl
            })
        }

        recentWorldList = recentWorlds

        App.setLoadingText(R.string.loading_text_favorites)

        FavoriteManager.refresh()

        getListeners().forEach { listener ->
            listener.endCacheRefresh()
        }

        if (!cacheHasBeenBuilt)
            cacheHasBeenBuilt = true
    }

    fun isBuilt(): Boolean {
        return cacheHasBeenBuilt
    }

    @Synchronized
    fun isWorldCached(worldId: String): Boolean {
        return worldList.any { it.id == worldId }
    }

    fun getWorld(worldId: String): WorldCache {
        return worldList.firstOrNull { it.id == worldId } ?: WorldCache("invalid")
    }

    fun addWorld(world: World) {
        val cache = WorldCache(world.id).apply {
            name = world.name
            thumbnailUrl = world.thumbnailImageUrl
        }
        worldList.add(cache)
    }

    fun updateWorld(world: World) {
        val index = worldList.indexOf(worldList.find { it.id == world.id })
        worldList[index] = WorldCache(world.id).apply {
            name = world.name
            thumbnailUrl = world.thumbnailImageUrl
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

    fun getRecent(): MutableList<WorldCache> {
        return recentWorldList
    }

    fun addRecent(world: World) {
        recentWorldList.removeIf { it.id == world.id }
        recentWorldList.add(0,
            WorldCache(world.id).apply {
                name = world.name
                thumbnailUrl = world.thumbnailImageUrl
            }
        )
        getListeners().forEach { listener ->
            listener.recentlyVisitedUpdated(recentWorldList)
        }
    }
}
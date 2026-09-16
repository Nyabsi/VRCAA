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
import cc.sovellus.vrcaa.api.vrchat.http.models.Profile
import cc.sovellus.vrcaa.api.vrchat.http.models.User
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import cc.sovellus.vrcaa.base.BaseManager
import cc.sovellus.vrcaa.helper.JsonHelper
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
object CacheManager : BaseManager<CacheManager.CacheListener>() {

    interface CacheListener {
        fun startCacheRefresh() { }
        fun endCacheRefresh() { }
        fun profileUpdated(profile: Profile) { }
        fun userUpdated(user: User) { }
    }

    data class WorldCache(
        val id: String,
        val name: String = "???",
        val thumbnailUrl: String = "",
    )

    private var userStateFlow = MutableStateFlow(User())
    private var profileStateFlow = MutableStateFlow(Profile())
    private var worldListStateFlow = MutableStateFlow(emptyList<WorldCache>())
    private val recentWorldsStateFlow = MutableStateFlow<List<WorldCache>>(emptyList())
    private val recommendedWorldsStateFlow = MutableStateFlow<List<World>>(emptyList())

    val recentWorldsState: StateFlow<List<WorldCache>> = recentWorldsStateFlow.asStateFlow()
    val recommendedWorldsState: StateFlow<List<World>> = recommendedWorldsStateFlow.asStateFlow()
    val worldList: StateFlow<List<WorldCache>> = worldListStateFlow.asStateFlow()
    val profile: StateFlow<Profile> = profileStateFlow.asStateFlow()
    val user: StateFlow<User> = userStateFlow.asStateFlow()

    private var isCacheBuilt = AtomicBoolean(false)

    suspend fun buildCache() = coroutineScope {

        recentWorldsStateFlow.value = emptyList()
        recommendedWorldsStateFlow.value = emptyList()

        App.setLoadingText(R.string.global_app_default_loading_text)

        isCacheBuilt.exchange(false)
        getListeners().forEach { it.startCacheRefresh() }

        val userFetch = api.auth.fetchCurrentUser()
        if (userFetch != null) {
            userStateFlow.value = userFetch
        }

        val profile = async { api.profile.fetchProfile(userFetch?.id ?: "", asSelf = true, withGroupsAndWorlds = false) }

        val onlineFriends = async { api.friends.fetchFriends(false) }
        val offlineFriends = async { api.friends.fetchFriends(true) }

        val recentWorlds = async { api.worlds.fetchRecent() }
        val recommendedWorlds = async { RecommendationManager.recommendWorlds() }

        val notifications = async { api.user.fetchNotifications() }
        val notificationsV2 = async { api.notifications.fetchNotifications() }

        val friends = async { onlineFriends.await() + offlineFriends.await() }

        val jobs = listOf(
            launch {
                profile.await()?.let { profileStateFlow.value = it }
            },

            launch {
                NotificationManager.setNotifications(notifications.await())
                NotificationManager.setNotificationsV2(notificationsV2.await())
            },

            launch {
                FriendManager.setFriends(friends.await().toMutableList())
            },

            launch { FavoriteManager.refresh() },

            launch {
                val worlds = recentWorlds.await()
                recentWorldsStateFlow.update {
                    it + worlds.map { world ->
                        WorldCache(world.id, name = world.name, thumbnailUrl = world.thumbnailImageUrl)
                    }
                }
            },

            launch {
                val locations = friends.await().mapNotNull { friend ->
                    friend.location.takeIf { it.contains("wrld_") }?.split(":")?.getOrNull(0)
                }.distinct().map { worldId ->
                    async { api.worlds.fetchWorldByWorldId(worldId) }
                }.awaitAll()

                // TODO: should there be a "WorldManager" to track all of your friends to do correlation based on timestamp to figure out who you spend time with?
                worldListStateFlow.update { current ->
                    current + locations.filterNotNull()
                        .filter { w -> current.none { it.id == w.id } }
                        .map { WorldCache(it.id, it.name, it.thumbnailImageUrl) }
                }
            },

            launch {
                recommendedWorldsStateFlow.value = recommendedWorlds.await()
            },
        )

        jobs.joinAll()

        isCacheBuilt.exchange(true)
        getListeners().forEach { it.endCacheRefresh() }
    }

    fun isBuilt(): Boolean {
        return isCacheBuilt.load()
    }

    fun getWorld(worldId: String): WorldCache? {
        return worldList.value.firstOrNull { it.id == worldId }
    }

    fun updateWorld(world: World) {
        worldListStateFlow.update { current ->
            val newCache = WorldCache(world.id, world.name, world.thumbnailImageUrl)
            if (current.any { it.id == world.id }) {
                current.map {
                    if (it.id == world.id) newCache else it
                }
            } else {
                current + newCache
            }
        }
    }

    fun updateProfile(profile: Profile) {
        profileStateFlow.update {
            JsonHelper.mergeJson(it, profile, Profile::class.java)
        }
        getListeners().forEach { it.profileUpdated(this.profile.value) }
    }

    fun <T> updateUser(user: T) {
        userStateFlow.update {
            JsonHelper.mergeDiffJson(it, user, User::class.java)
        }
        getListeners().forEach { it.userUpdated(this.user.value) }
    }

    fun addRecentWorld(world: World) {
        recentWorldsStateFlow.update { current ->
            val newCache = WorldCache(world.id, world.name, world.thumbnailImageUrl)
            listOf(newCache) + current.filterNot { it.id == world.id }
        }
    }
}

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
object CacheManager : BaseManager<CacheManager.CacheListener>() {

    sealed class Stage {
        data object Profile : Stage()
        data object Home : Stage()
        data object Friends : Stage()
        data object Feed : Stage()
        data object Favorites : Stage()
    }

    interface CacheListener {
        fun startCacheRefresh(stage: Stage) { }
        fun endCacheRefresh(stage: Stage) { }
        fun profileUpdated(profile: User) { }
    }

    data class WorldCache(
        val id: String,
        var name: String = "???",
        var thumbnailUrl: String = "",
    )

    private val profileLock = Any()

    private var profile: User? = null
    private var worldListStateFlow = MutableStateFlow(emptyList<WorldCache>())
    private val recentWorldsStateFlow = MutableStateFlow<List<WorldCache>>(emptyList())
    private val recommendedWorldsStateFlow = MutableStateFlow<List<World>>(emptyList())

    val recentWorldsState: StateFlow<List<WorldCache>> = recentWorldsStateFlow.asStateFlow()
    val recommendedWorldsState: StateFlow<List<World>> = recommendedWorldsStateFlow.asStateFlow()
    val worldList: StateFlow<List<WorldCache>> = worldListStateFlow.asStateFlow()

    private var isProfileCacheBuilt = AtomicBoolean(false)
    private var isHomeCacheBuilt = AtomicBoolean(false)
    private var isFriendsCacheBuilt = AtomicBoolean(false)
    private var isFeedCacheBuilt = AtomicBoolean(false)
    private var isFavoriteCacheBuilt = AtomicBoolean(false)

    suspend fun buildCache() = coroutineScope {

        recentWorldsStateFlow.value = emptyList()
        recommendedWorldsStateFlow.value = emptyList()

        App.setLoadingText(R.string.global_app_default_loading_text)

        val user = async { api.auth.fetchCurrentUser() }

        val onlineFriends = async { api.friends.fetchFriends(false) }
        val offlineFriends = async { api.friends.fetchFriends(true) }

        val recentWorlds = async { api.worlds.fetchRecent() }
        val recommendedWorlds = async { RecommendationManager.recommendWorlds() }

        val notifications = async { api.user.fetchNotifications() }
        val notificationsV2 = async { api.notifications.fetchNotifications() }

        val favorites = async { FavoriteManager.refresh() }

        launch {
            getListeners().forEach { listener ->
                listener.startCacheRefresh(Stage.Profile)
            }

            val result = (user).await()
            synchronized(profileLock) {
                profile = result
            }

            getListeners().forEach { listener ->
                listener.endCacheRefresh(Stage.Profile)
            }

            isProfileCacheBuilt.exchange(true)
        }

        launch {
            getListeners().forEach { listener ->
                listener.startCacheRefresh(Stage.Feed)
            }

            NotificationManager.setNotifications(notifications.await())
            NotificationManager.setNotificationsV2(notificationsV2.await())

            getListeners().forEach { listener ->
                listener.endCacheRefresh(Stage.Feed)
            }

            isFeedCacheBuilt.exchange(true)
        }

        launch {
            getListeners().forEach { listener ->
                listener.startCacheRefresh(Stage.Friends)
            }

            val online = onlineFriends.await()
            val offline = offlineFriends.await()

            val friends: MutableList<Friend> = mutableListOf()
            friends.addAll((online + offline))
            FriendManager.setFriends(friends)

            val locations = friends.mapNotNull { friend ->
                friend.location.takeIf { it.contains("wrld_") }?.split(":")?.getOrNull(0)
            }.distinct().map { worldId ->
                async {
                    api.worlds.fetchWorldByWorldId(worldId)
                }
            }.awaitAll()

            // TODO: should there be a "WorldManager" to track all of your friends to do correlation based on timestamp to figure out who you spend time with?
            worldListStateFlow.value += locations.filterNotNull()
                            .filter { !isWorldCached(it.id) }
                            .map { WorldCache(it.id).apply { name = it.name; thumbnailUrl = it.thumbnailImageUrl } }

            getListeners().forEach { listener ->
                listener.endCacheRefresh(Stage.Friends)
            }

            isFriendsCacheBuilt.exchange(true)
        }

        launch {
            getListeners().forEach { listener ->
                listener.startCacheRefresh(Stage.Home)
            }

            val worlds = recentWorlds.await()

            recentWorldsStateFlow.value +=
                worlds.map { world ->
                    WorldCache(world.id).apply {
                        name = world.name
                        thumbnailUrl = world.thumbnailImageUrl
                    }
                }

            recommendedWorldsStateFlow.value = recommendedWorlds.await().toMutableList()

            getListeners().forEach { listener ->
                listener.endCacheRefresh(Stage.Home)
            }

            isHomeCacheBuilt.exchange(true)
        }

        launch {
            getListeners().forEach { listener ->
                listener.startCacheRefresh(Stage.Favorites)
            }

            favorites.await()

            getListeners().forEach { listener ->
                listener.endCacheRefresh(Stage.Favorites)
            }

            isFavoriteCacheBuilt.exchange(true)
        }
    }

    fun isBuilt(stage: Stage): Boolean {
        return when (stage) {
            Stage.Profile -> isProfileCacheBuilt.load()
            Stage.Home -> isHomeCacheBuilt.load()
            Stage.Friends -> isFriendsCacheBuilt.load()
            Stage.Feed -> isFeedCacheBuilt.load()
            Stage.Favorites -> isFavoriteCacheBuilt.load()
        }
    }

    fun isWorldCached(worldId: String): Boolean {
        return worldList.value.any { it.id == worldId }
    }

    fun getWorld(worldId: String): WorldCache? {
        return worldList.value.firstOrNull { it.id == worldId }
    }

    fun addWorld(world: World) {
        val cache = WorldCache(world.id).apply {
            name = world.name
            thumbnailUrl = world.thumbnailImageUrl
        }
        worldListStateFlow.value += cache
    }

    fun updateWorld(world: World) {
        worldListStateFlow.update { current ->
            current.map { cache ->
                if (cache.id == world.id) {
                    WorldCache(world.id).apply {
                        name = world.name
                        thumbnailUrl = world.thumbnailImageUrl
                    }
                } else cache
            }
        }
    }

    fun getProfile(): User? {
        synchronized(profileLock) {
            return profile
        }
    }

    fun updateProfile(profile: User) {
        synchronized(profileLock) {
            this.profile?.let {
                val result = JsonHelper.mergeJson(it, profile, User::class.java)
                this.profile = result
                getListeners().forEach { listener ->
                    listener.profileUpdated(result)
                }
            }
        }
    }

    fun addRecentWorld(world: World) {
        recentWorldsStateFlow.update { current ->
            val newCache = WorldCache(world.id).apply {
                name = world.name
                thumbnailUrl = world.thumbnailImageUrl
            }
            listOf(newCache) + current.filterNot { it.id == world.id }
        }
    }
}

package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.api.vrchat.http.models.User
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import cc.sovellus.vrcaa.base.BaseManager
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlin.jvm.optionals.getOrNull

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
    private var worldList: MutableList<WorldCache> = mutableListOf()
    private var recentWorldList: MutableList<WorldCache> = mutableListOf()

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

    fun isWorldCached(worldId: String): Boolean {
        return worldList.stream().filter { it.id == worldId }.count().toInt() != 0
    }

    fun getWorld(worldId: String): WorldCache {
        return worldList.stream().filter { it.id == worldId }.findFirst().getOrNull() ?: WorldCache("invalid")
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
        this.profile = profile
        getListeners().forEach { listener ->
            listener.profileUpdated(profile)
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
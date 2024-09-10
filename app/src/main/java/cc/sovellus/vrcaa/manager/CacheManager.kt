package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.Friend
import cc.sovellus.vrcaa.api.vrchat.models.User
import cc.sovellus.vrcaa.api.vrchat.models.World
import cc.sovellus.vrcaa.manager.ApiManager.api

object CacheManager {

    private lateinit var profile: User
    private var worldList: MutableList<WorldCache> = mutableListOf()
    private var recentWorldList: MutableList<WorldCache> = mutableListOf()

    data class WorldCache(
        val id: String,
        var name: String = "???",
        var thumbnailUrl: String = "",
    )

    interface CacheListener {
        fun recentlyVisitedUpdated(worlds: MutableList<WorldCache>) { }
        fun startCacheRefresh() { }
        fun endCacheRefresh() { }
        fun profileUpdated(profile: User) { }
    }

    private var listeners: MutableList<CacheListener?> = mutableListOf()

    fun addListener(listener: CacheListener) {
        this.listeners.add(listener)
    }

    suspend fun buildCache() {
        listeners.forEach { listener ->
            listener?.startCacheRefresh()
        }

        App.setLoadingText(R.string.loading_text_profile)
        api.getSelf()?.let { profile = it }

        val friendList: MutableList<Friend> = mutableListOf()
        val recentWorlds: MutableList<WorldCache> = mutableListOf()

        App.setLoadingText(R.string.loading_text_online_friends)

        api.getFriends(false).forEach { friend->
            if (friend.location.contains("wrld_")) {
                val world = api.getWorld(friend.location.split(":")[0])
                worldList.add(WorldCache(world.id).apply {
                    name = world.name
                    thumbnailUrl = world.thumbnailImageUrl
                })
            }
            friendList.add(friend)
        }

        App.setLoadingText(R.string.loading_text_offline_friends)

        api.getFriends(true).forEach { friend->
            friendList.add(friend)
        }

        FriendManager.setFriends(friendList)

        // val intent = Intent(context, FriendWidgetReceiver::class.java).apply { action = "FRIEND_LOCATION_UPDATE" }
        // context.sendBroadcast(intent)

        App.setLoadingText(R.string.loading_text_recently_visited)

        api.getRecentWorlds()?.forEach { world->
            recentWorlds.add(WorldCache(world.id).apply {
                name = world.name
                thumbnailUrl = world.thumbnailImageUrl
            })
        }

        recentWorldList = recentWorlds

        App.setLoadingText(R.string.loading_text_favorites)

        FavoriteManager.refresh()

        listeners.forEach { listener ->
            listener?.endCacheRefresh()
        }
    }

    fun isWorldCached(worldId: String): Boolean {
        return worldList.contains(worldList.find { it.id == worldId })
    }

    fun getWorld(worldId: String): WorldCache {
        return worldList.find { it.id == worldId } ?: WorldCache("invalid")
    }

    fun addWorld(world: World) {
        worldList.add(WorldCache(world.id).apply {
            name = world.name
            thumbnailUrl = world.thumbnailImageUrl
        })
    }

    fun updateWorld(world: World) {
        val index = worldList.indexOf(worldList.find { it.id == world.id })
        worldList[index] = WorldCache(world.id).apply {
            name = world.name
            thumbnailUrl = world.thumbnailImageUrl
        }
    }

    fun getProfile(): User {
        return profile
    }

    fun updateProfile(profile: User) {
        this.profile = profile
        listeners.forEach { listener ->
            listener?.profileUpdated(profile)
        }
    }

    fun getRecent(): MutableList<WorldCache> {
        return recentWorldList
    }

    fun addRecent(world: World) {
        recentWorldList.removeIf { it.id == world.id }
        recentWorldList.add(
            0,
            WorldCache(world.id).apply {
                name = world.name
                thumbnailUrl = world.thumbnailImageUrl
            }
        )
        listeners.forEach { listener ->
            listener?.recentlyVisitedUpdated(recentWorldList)
        }
    }
}
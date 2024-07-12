package cc.sovellus.vrcaa.api.vrchat

import cc.sovellus.vrcaa.api.vrchat.models.Friend
import cc.sovellus.vrcaa.api.vrchat.models.User
import cc.sovellus.vrcaa.api.vrchat.models.World
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.FriendManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class VRChatCache : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    data class WorldCache(
        val id: String,
        val name: String,
        val thumbnailUrl: String,
        val occupants: Int
    )

    private var profile: User? = null
    private var worldCache: MutableList<WorldCache> = mutableListOf()
    private var recentlyVisited: MutableList<WorldCache> = mutableListOf()

    interface CacheListener {
        fun recentlyVisitedUpdated(worlds: MutableList<WorldCache>) { }
        fun cacheUpdated() { }
        fun profileUpdated() { }
    }

    private var listeners: MutableList<CacheListener?> = mutableListOf()

    fun addCacheListener(listener: CacheListener) {
        this.listeners.add(listener)
    }

    private val cacheWorker: Runnable = Runnable {
        launch {
            profile = api.getSelf()

            val friendList: MutableList<Friend> = mutableListOf()

            val favorites = api.getFavorites("friend")
            var friends = api.getFriends(false)

            friends.forEach { friend->
                favorites.find { favorite ->
                    favorite.favoriteId == friend.id
                }?.let {
                    friend.isFavorite = true
                }
                if (friend.location.contains("wrld_")) {
                    val world = api.getWorld(friend.location.split(":")[0])
                    val cache = WorldCache(
                        id = world.id,
                        name = world.name,
                        thumbnailUrl = world.thumbnailImageUrl,
                        occupants = world.occupants
                    )
                    worldCache.add(cache)
                }
                friendList.add(friend)
            }

            friends = api.getFriends(true)

            friends.forEach { friend->
                favorites.find { favorite ->
                    favorite.favoriteId == friend.id
                }?.let {
                    friend.isFavorite = true
                }
                friendList.add(friend)
            }

            api.getRecentWorlds()?.forEach { world->
                val cache = WorldCache(
                    id = world.id,
                    name = world.name,
                    thumbnailUrl = world.thumbnailImageUrl,
                    occupants = world.occupants
                )
                recentlyVisited.add(cache)
            }

            FriendManager.setFriends(friendList)

            listeners.forEach { listener ->
                listener?.cacheUpdated()
            }
        }
        Thread.sleep(1800000)
    }

    private var cacheThread: Thread? = null

    init {
        cacheThread = Thread(cacheWorker)
        cacheThread?.start()
    }

    fun worldExists(worldId: String): Boolean {
        return worldCache.contains(worldCache.find { it.id == worldId })
    }

    fun getWorld(worldId: String): WorldCache {
        return worldCache.find { it.id == worldId } ?:
            WorldCache(
                id = "invalid",
                name = "Invalid World",
                thumbnailUrl = "",
                occupants = -1
            )
    }

    fun addWorld(world: World) {
        val cache = WorldCache(
            id = world.id,
            name = world.name,
            thumbnailUrl = world.thumbnailImageUrl,
            occupants = world.occupants
        )
        worldCache.add(cache)
    }

    fun updateWorld(world: World) {
        val cache = WorldCache(
            id = world.id,
            name = world.name,
            thumbnailUrl = world.thumbnailImageUrl,
            occupants = world.occupants
        )
        val index = worldCache.indexOf(worldCache.find { it.id == world.id })
        worldCache[index] = cache
    }

    fun getProfile(): User? {
        return profile
    }

    fun updateProfile(profile: User?) {
        this.profile = profile
        listeners.forEach { listener ->
            listener?.profileUpdated()
        }
    }

    fun getRecentlyVisited(): MutableList<WorldCache> {
        return recentlyVisited
    }

    fun addRecentlyVisited(world: World) {
        recentlyVisited.removeIf { it.id == world.id }
        recentlyVisited.add(
            0,
            WorldCache(
                id = world.id,
                name = world.name,
                thumbnailUrl = world.thumbnailImageUrl,
                occupants = world.occupants
            )
        )
        listeners.forEach { listener ->
            listener?.recentlyVisitedUpdated(recentlyVisited)
        }
    }
}
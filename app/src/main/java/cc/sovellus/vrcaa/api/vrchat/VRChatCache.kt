package cc.sovellus.vrcaa.api.vrchat

import android.util.Log
import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
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

    private var profile: User? = null
    private var currentWorld: World? = null
    private var worlds: MutableMap<String, String> = mutableMapOf()
    private var recentWorlds: MutableList<World> = mutableListOf()

    @Volatile private var listener: CacheListener? = null
    @Volatile var isCachedLoaded: Boolean = false

    interface CacheListener {
        fun updatedLastVisited(worlds: MutableList<World>)
        fun initialCacheCreated()
    }

    init {
        launch {
            profile = api.getSelf()

            val favorites = api.getFavorites("friend")
            val friendList: MutableList<LimitedUser> = ArrayList()

            val n = 50; var offset = 0
            var friends = api.getFriends(false, n, offset)

            while (friends != null) {
                friends.forEach { friend ->
                    favorites?.find {
                        it.favoriteId == friend.id
                    }?.let {
                        friend.isFavorite = true
                    }

                    if (friend.location.contains("wrld_")) {
                        val world = api.getWorld(friend.location.split(":")[0])
                        worlds[world.id] = world.name
                    }

                    friendList.add(friend)
                }

                offset += n
                friends = api.getFriends(false, n, offset)
            }

            offset = 0
            friends = api.getFriends(true, n, offset)

            while (friends != null) {
                friends.forEach { friend ->
                    favorites?.find {
                        it.favoriteId == friend.id
                    }?.let {
                        friend.isFavorite = true
                    }

                    friendList.add(friend)
                }

                offset += n
                friends = api.getFriends(false, n, offset)
            }

            FriendManager.setFriends(friendList)

            api.getRecentWorlds()?.let { world -> recentWorlds += world }

            listener?.initialCacheCreated()
        }
    }

    @Synchronized
    fun setCacheListener(listener: CacheListener) {
        synchronized(listener) {
            this.listener = listener
        }
    }

    fun getWorld(worldId: String): String {
        return worlds[worldId].toString()
    }

    fun addWorld(worldId: String, name: String) {
        worlds[worldId] = name
    }

    fun getCurrentWorld(): World? {
        return currentWorld
    }

    fun getProfile(): User? {
        return profile
    }

    fun setProfile(profile: User?) {
        this.profile = profile
    }

    fun getRecent(): MutableList<World> {
        return recentWorlds
    }

    @Synchronized
    fun addRecent(world: World) {
        synchronized(world) {
            currentWorld = world
            recentWorlds.removeIf { it.id == world.id }
            recentWorlds.add(0, world)
            listener?.updatedLastVisited(recentWorlds)
        }
    }
}
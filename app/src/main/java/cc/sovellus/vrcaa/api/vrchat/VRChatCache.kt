package cc.sovellus.vrcaa.api.vrchat

import android.util.Log
import cc.sovellus.vrcaa.api.vrchat.models.Friend
import cc.sovellus.vrcaa.api.vrchat.models.User
import cc.sovellus.vrcaa.api.vrchat.models.World
import cc.sovellus.vrcaa.helper.StatusHelper
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
    private var worlds: MutableList<World> = mutableListOf()
    private var recentWorlds: MutableList<World> = mutableListOf()

    private var listener: CacheListener? = null
    var isCachedLoaded: Boolean = false

    private val cacheWorker: Runnable = Runnable {
        launch {
            profile = api.getSelf()

            val favorites = api.getFavorites("friend")
            val friendList: MutableList<Friend> = ArrayList()

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
                        worlds.add(world)
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
                friends = api.getFriends(true, n, offset)
            }

            api.getRecentWorlds()?.let { world -> recentWorlds += world }

            FriendManager.setFriends(friendList)

            if (!isCachedLoaded) {
                listener?.initialCacheCreated()
            }
        }
        Thread.sleep(1800000)
    }

    private var cacheThread: Thread? = null

    interface CacheListener {
        fun updatedLastVisited(worlds: MutableList<World>)
        fun initialCacheCreated()
    }

    init {
        cacheThread = Thread(cacheWorker)
        cacheThread?.start()
    }

    fun setCacheListener(listener: CacheListener) {
        this.listener = listener
    }

    fun getWorld(worldId: String): String {
        val world = worlds.find { it.id == worldId }
        if (world != null)
            return world.name
        return "null"
    }

    fun getWorldObject(worldId: String): World? {
        val world = worlds.find { it.id == worldId }
        if (world != null)
            return world
        return null
    }

    fun addWorld(world: World) {
        val exists = worlds.find { it.id != world.id }
        if (exists != null)
            worlds.add(world)
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

    fun addRecent(world: World) {
        currentWorld = world
        recentWorlds.removeIf { it.id == world.id }
        recentWorlds.add(0, world)
        listener?.updatedLastVisited(recentWorlds)
    }
}
package cc.sovellus.vrcaa.api.vrchat

import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.models.User
import cc.sovellus.vrcaa.api.vrchat.models.World
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.FriendManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.Cache
import kotlin.coroutines.CoroutineContext

class VRChatCache : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + Job()

    private var profile: User? = null
    private var friends: MutableList<LimitedUser> = ArrayList()
    private var worlds: MutableMap<String, String> = mutableMapOf()
    private var recentWorlds: MutableList<World> = mutableListOf()
    private var listener: CacheListener? = null

    interface CacheListener {
        fun updatedLastVisited(worlds: MutableList<World>)
    }

    init {
        launch {
            profile = api.getSelf()

            api.getFriends()?.let { friends += it }
            api.getFriends(true)?.let { friends += it }

            val favorites = api.getFavorites("friend")

            for (friend in friends) {
                favorites?.find {
                    it.favoriteId == friend.id
                }?.let {
                    friend.isFavorite = true
                }
            }

            FriendManager.setFriends(friends)


        }
    }

    fun setCacheListener(listener: CacheListener) {
        this.listener = listener
    }

    suspend fun getWorld(worldId: String): String {
        if (!worlds.contains(worldId)) {
            api.getWorld(worldId)?.let { world ->
                worlds[worldId] = world.name
                return world.name
            }
        }
        return worlds[worldId].toString()
    }

    fun getProfile(): User? {
        return profile
    }

    fun setProfile(profile: User?) {
        this.profile = profile
    }

    suspend fun getRecent(): MutableList<World> {
        if (recentWorlds.isEmpty())
            api.getRecentWorlds()?.let { world -> recentWorlds += world }
        return recentWorlds
    }

    fun addRecent(world: World) {
        recentWorlds += world
        listener?.updatedLastVisited(recentWorlds)
    }

    fun getFriends(): MutableList<LimitedUser> {
        return friends
    }
}
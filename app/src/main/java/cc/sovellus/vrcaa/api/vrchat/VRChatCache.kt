package cc.sovellus.vrcaa.api.vrchat

import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.models.User
import cc.sovellus.vrcaa.manager.FriendManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class VRChatCache(
    private val api: VRChatApi
): CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + Job()

    private var profile: User? = null
    private var friends: MutableList<LimitedUser> = ArrayList()
    private var worlds: MutableMap<String, String> = mutableMapOf()

    init {
        launch {
            profile = api.getSelf()

            api.getFriends()?.let {
                friends += it
            }

            api.getFriends(true)?.let {
                friends += it
            }

            val favorites = api.getFavorites("friend")

            for (friend in friends) {
                favorites?.find { it.favoriteId == friend.id }?.let { friend.isFavorite = true }
            }

            FriendManager.setFriends(friends)
        }
    }

    suspend fun getCachedWorld(worldId: String): String {
        // if the world is not cached, we should cache it.
        if (!worlds.contains(worldId)) {
            api.getWorld(worldId)?.let { world ->
                worlds[worldId] = world.name
                return world.name
            }
        }
        // once the world is cached we can return it, wrap around .toString() to prevent null error
        return worlds[worldId].toString()
    }

    fun getProfile(): User? {
        return profile
    }

    fun setProfile(profile: User?) {
        this.profile = profile
    }
}
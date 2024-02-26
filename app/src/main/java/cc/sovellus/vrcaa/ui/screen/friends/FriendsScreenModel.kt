package cc.sovellus.vrcaa.ui.screen.friends

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.helper.LocationHelper
import cc.sovellus.vrcaa.api.models.LimitedUser
import cc.sovellus.vrcaa.helper.api
import cc.sovellus.vrcaa.manager.FriendManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FriendsScreenModel(
    private val
    context: Context
) : StateScreenModel<FriendsScreenModel.FriendListState>(FriendListState.Init) {

    sealed class FriendListState {
        data object Init : FriendListState()
        data object Loading : FriendListState()
        data class Result(val favoriteFriends: List<LimitedUser>) : FriendListState()
    }

    var isRefreshing = mutableStateOf(false)
    var currentIndex = mutableIntStateOf(0)

    private val friendManager = FriendManager()

    private var onlineFriendsStateFlow = MutableStateFlow(friendManager.getFriends().toList())
    var onlineFriends = onlineFriendsStateFlow.asStateFlow()

    private var offlineFriendsStateFlow = MutableStateFlow(friendManager.getFriends(true).toList())
    var offlineFriends = offlineFriendsStateFlow.asStateFlow()

    private var favoriteFriends = mutableListOf<LimitedUser>()

    private val listener = object : FriendManager.FriendListener {
        override fun onUpdateFriends(friends: MutableList<LimitedUser>, offline: Boolean) {
            var newFriends = friends.toList()
            screenModelScope.launch {
                newFriends = getFriendLocations(newFriends)
                if (offline) {
                    offlineFriendsStateFlow.update { newFriends }
                } else {
                    onlineFriendsStateFlow.update { newFriends }
                }
            }
        }
    }

    init {
        friendManager.setFriendListener(listener)
        screenModelScope.launch {
            onlineFriendsStateFlow.update { getFriendLocations(onlineFriends.value) }
        }
        getFriends()
    }

    private fun getFriends() {
        screenModelScope.launch {
            context.api.get().getFavorites("friend")?.let { favorites ->
                for (favorite in favorites) {
                    context.api.get().getFriend(favorite.favoriteId)?.let { friend ->
                        favoriteFriends.add(friend)
                    }
                }
                getFriendLocations(favoriteFriends)
            }

            mutableState.value = FriendListState.Result(favoriteFriends = favoriteFriends)
        }
    }

    suspend fun getFriendLocations(friends: List<LimitedUser>): List<LimitedUser> {
        for (friend in friends) {
            if (friend.location.contains("wrld_")) {
                val result = LocationHelper.parseLocationIntent(friend.location)
                val world = context.api.get().getWorld(result.worldId)!!

                if (result.regionId.isNotEmpty()) {
                    friend.location =
                        "${world.name}~(${result.instanceType}) ${result.regionId.uppercase()}"
                } else {
                    friend.location = "${world.name}~(${result.instanceType}) US"
                }
            }
        }
        return friends
    }

    fun refreshFriends(context: Context) {
        screenModelScope.launch {
            favoriteFriends.clear()

            isRefreshing.value = true
            getFriends()

            // Would you ever think the application being too fast is problematic... Well, it is. So add that delay.
            delay(500)
            isRefreshing.value = false

            Toast.makeText(
                context,
                "Refreshed friend list.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
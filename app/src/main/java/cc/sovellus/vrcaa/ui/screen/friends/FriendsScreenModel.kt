package cc.sovellus.vrcaa.ui.screen.friends

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.helper.LocationHelper
import cc.sovellus.vrcaa.api.models.Friends
import cc.sovellus.vrcaa.helper.api
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FriendsScreenModel(
    private val
    context: Context
) : StateScreenModel<FriendsScreenModel.FriendListState>(FriendListState.Init) {

    sealed class FriendListState {
        data object Init : FriendListState()
        data object Loading : FriendListState()
        data class Result (val friends: List<Friends.FriendsItem>, val offlineFriends: List<Friends.FriendsItem>, val favoriteFriends: List<Friends.FriendsItem>) : FriendListState()
    }

    private var friends = mutableListOf<Friends.FriendsItem>()
    private var offlineFriends = mutableListOf<Friends.FriendsItem>()
    private var favoriteFriends = mutableListOf<Friends.FriendsItem>()
    var isRefreshing = mutableStateOf(false)
    var currentIndex = mutableIntStateOf(0)

    init {
        mutableState.value = FriendListState.Loading
        getFriends()
    }

    private fun getFriends() {
        screenModelScope.launch {

            context.api.get().getFriends()?.let {
                friends = it
                getFriendLocations(friends)
            }

            context.api.get().getFriends(true)?.let {
                offlineFriends = it
            }

            context.api.get().getFavorites("friend")?.let { favorites ->
                for (favorite in favorites) {
                    context.api.get().getFriend(favorite.favoriteId)?.let { friend ->
                        getFriendLocation(friend)
                        favoriteFriends.add(friend)
                    }
                }
            }

            mutableState.value = FriendListState.Result(
                friends = friends,
                offlineFriends = offlineFriends,
                favoriteFriends = favoriteFriends
            )
        }
    }

    private suspend fun getFriendLocation(friend: Friends.FriendsItem) {
        if (friend.location.contains("wrld_")) {
            val result = LocationHelper().parseLocationIntent(friend.location)
            val world = context.api.get().getWorld(result.worldId)!!

            if (result.regionId.isNotEmpty()) {
                friend.location = "${world.name}~(${result.instanceType}) ${result.regionId.uppercase()}"
            } else {
                friend.location = "${world.name}~(${result.instanceType}) USW"
            }
        }
    }

    private suspend fun getFriendLocations(friends: List<Friends.FriendsItem>) {
        for (friend in friends) {
            if (friend.location.contains("wrld_")) {
                val result = LocationHelper().parseLocationIntent(friend.location)
                val world = context.api.get().getWorld(result.worldId)!!

                if (result.regionId.isNotEmpty()) {
                    friend.location = "${world.name}~(${result.instanceType}) ${result.regionId.uppercase()}"
                } else {
                    friend.location = "${world.name}~(${result.instanceType})"
                }
            }
        }
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
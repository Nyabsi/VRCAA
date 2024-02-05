package cc.sovellus.vrcaa.ui.screen.friends

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.api.models.Friends
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FriendsScreenModel(
    context: Context
) : StateScreenModel<FriendsScreenModel.FriendListState>(FriendListState.Init) {
    private val api = ApiContext(context)

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

            api.getFriends()?.let {
                friends = it
                getFriendLocations(api, friends)
            }

            api.getFriends(true)?.let {
                offlineFriends = it
            }

            api.getFavorites("friend")?.let { favorites ->
                for (favorite in favorites) {
                    api.getFriend(favorite.favoriteId)?.let { friend ->
                        getFriendLocation(api, friend)
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

    private suspend fun getFriendLocation(api: ApiContext, friend: Friends.FriendsItem) {
        if (friend.location.contains("wrld_")) {
            friend.location = api.getInstance(friend.location)?.world?.name.toString()
        }
    }

    private suspend fun getFriendLocations(api: ApiContext, friends: List<Friends.FriendsItem>) {
        for (friend in friends) {
            if (friend.location.contains("wrld_")) {
                friend.location = api.getInstance(friend.location)?.world?.name.toString()
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
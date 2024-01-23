package cc.sovellus.vrcaa.ui.screen.friends

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.api.models.Friends
import kotlinx.coroutines.launch

class FriendsScreenModel(
    private val api: ApiContext
) : StateScreenModel<FriendsScreenModel.FriendListState>(FriendListState.Init) {

    sealed class FriendListState {
        data object Init : FriendListState()
        data object Loading : FriendListState()
        data class Result (val friends: List<Friends.FriendsItem>, val offlineFriends: List<Friends.FriendsItem>, val favoriteFriends: List<Friends.FriendsItem>) : FriendListState()
    }

    var friends = mutableListOf<Friends.FriendsItem>()
    var offlineFriends = mutableListOf<Friends.FriendsItem>()
    var favoriteFriends = mutableListOf<Friends.FriendsItem>()
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
            getFriends()
        }
    }
}
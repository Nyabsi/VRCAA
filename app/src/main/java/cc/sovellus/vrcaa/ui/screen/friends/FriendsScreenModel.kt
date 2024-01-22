package cc.sovellus.vrcaa.ui.screen.friends

import android.util.Log
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
        data class Result (val friends: List<Friends.FriendsItem>) : FriendListState()
    }

    var friends = mutableListOf<Friends.FriendsItem>()
    var isRefreshing = mutableStateOf(false)

    init {
        mutableState.value = FriendListState.Loading
        getFriends()
    }

    private fun getFriends() {
        screenModelScope.launch {
            val result = api.getFriends()
            if (result != null) {
                friends = result
                // This code could be prettified, can't be bothered to do so.
                for (friend in friends) {
                    if (friend.location.contains("wrld_")) {
                        friend.location = api.getInstance(friend.location)?.world?.name.toString()
                    }
                }
                mutableState.value = FriendListState.Result(friends = friends)
            } else {
                // You know, it will throw error in the ApiContext anyway, we should inform the user here with Toast
                // and probably throw them to the login screen since it is often invalid session.
                Log.e("VRCAA", "Something went horribly wrong when fetching friends.")
            }
        }
    }

    fun refreshFriends() {
        getFriends()
    }
}
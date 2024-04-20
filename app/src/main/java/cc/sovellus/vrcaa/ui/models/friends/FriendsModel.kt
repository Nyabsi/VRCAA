package cc.sovellus.vrcaa.ui.models.friends

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
import cc.sovellus.vrcaa.manager.ApiManager.api
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

    private var friendsStateFlow = MutableStateFlow(listOf<LimitedUser>())
    private var favoriteFriends = mutableListOf<LimitedUser>()

    var friends = friendsStateFlow.asStateFlow()
    val isRefreshing = mutableStateOf(false)
    var currentIndex = mutableIntStateOf(0)

    private val listener = object : FriendManager.FriendListener {
        override fun onUpdateFriends(friends: MutableList<LimitedUser>) {
            val newList = friends.toList()
            screenModelScope.launch {
                friendsStateFlow.update { newList }
            }
        }
    }

    init {
        mutableState.value = FriendListState.Loading

        FriendManager.setFriendListener(listener)
        fetchFavorites()

        screenModelScope.launch {
            while (FriendManager.getFriends().isEmpty())
                delay(10)
            friendsStateFlow.update { FriendManager.getFriends() }
            mutableState.value = FriendListState.Result(favoriteFriends = favoriteFriends)
        }
    }

    // TODO: add special flag to "Favorite" friends.
    private fun fetchFavorites() {
        screenModelScope.launch {
            api?.getFavorites("friend")?.let {
                it.forEach { favorite ->
                    api?.getFriend(favorite.id)
                        ?.let { friend -> favoriteFriends.add(friend) }
                }
            }
        }
    }

    fun refreshFriends(context: Context) {
        screenModelScope.launch {
            favoriteFriends.clear()

            isRefreshing.value = true
            fetchFavorites()
            delay(500)
            isRefreshing.value = false

            Toast.makeText(
                context,
                context.getString(R.string.friend_refreshed_favorites_message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
package cc.sovellus.vrcaa.ui.models.friends

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.helper.LocationHelper
import cc.sovellus.vrcaa.api.http.models.LimitedUser
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.FriendManager
import extensions.wu.seal.PropertySuffixSupport.append
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

    private var friendsStateFlow = MutableStateFlow(FriendManager.getFriends().toList())
    private var favoriteFriends = mutableListOf<LimitedUser>()

    var friends = friendsStateFlow.asStateFlow()
    val isRefreshing = mutableStateOf(false)
    var currentIndex = mutableIntStateOf(0)

    private val listener = object : FriendManager.FriendListener {
        override fun onUpdateFriends(friends: MutableList<LimitedUser>) {
            var newList = friends.toList()
            screenModelScope.launch {
                newList = parseReadableLocation(newList)
                friendsStateFlow.update { newList }
            }
        }
    }

    init {
        mutableState.value = FriendListState.Loading
        FriendManager.setFriendListener(listener)

        screenModelScope.launch {
            FriendManager.setFriends(parseReadableLocation(FriendManager.getFriends().toList()).toMutableList())
        }

        fetchFavorites()
        mutableState.value = FriendListState.Result(favoriteFriends = favoriteFriends)
    }

    // TODO: add special flag to "Favorite" friends.
    private fun fetchFavorites() {
        screenModelScope.launch {
            api.getFavorites("friend")?.let {
                it.forEach { favorite ->
                    api.getFriend(favorite.favoriteId)
                        ?.let { friend -> favoriteFriends.add(friend) }
                }
                parseReadableLocation(favoriteFriends)
            }
        }
    }

    suspend fun parseReadableLocation(users: List<LimitedUser>): List<LimitedUser> {
        for (user in users) {
            user.location?.let {
                if (it.contains("wrld_"))
                    user.location = LocationHelper.getReadableLocation(it)
            }
        }
        return users
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
                "Refreshed favorite friends list.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
package cc.sovellus.vrcaa.ui.screen.friends

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

    private var onlineFriendsStateFlow = MutableStateFlow(FriendManager.getFriends().toList())
    var friends = onlineFriendsStateFlow.asStateFlow()

    private var favoriteFriends = mutableListOf<LimitedUser>()

    private val listener = object : FriendManager.FriendListener {
        override fun onUpdateFriends(friends: MutableList<LimitedUser>, offline: Boolean) {
            var newFriends = friends.toList()
            screenModelScope.launch {
                newFriends = getFriendLocations(newFriends)
                onlineFriendsStateFlow.update { newFriends }
            }
        }
    }

    init {
        FriendManager.setFriendListener(listener)
        screenModelScope.launch {
            onlineFriendsStateFlow.update { getFriendLocations(friends.value) }
        }
        getFriends()
    }

    private fun getFriends() {
        screenModelScope.launch {
            api.getFavorites("friend")?.let { favorites ->
                for (favorite in favorites) {
                    api.getFriend(favorite.favoriteId)?.let { friend ->
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
                val instance = api.getInstance(friend.location)

                if (instance == null) {
                    Toast.makeText(
                        context,
                        "Failed to fetch instance due to API error, try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (result.regionId.isNotEmpty()) {
                        friend.location =
                            "${instance.world.name} (${instance.name}) ${result.instanceType} ${result.regionId.uppercase()}"
                    } else {
                        friend.location = "${instance.world.name} (${instance.name}) ${result.instanceType} (${result.instanceType}) US"
                    }
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
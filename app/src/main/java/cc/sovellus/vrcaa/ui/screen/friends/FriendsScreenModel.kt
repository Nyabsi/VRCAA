package cc.sovellus.vrcaa.ui.screen.friends

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import cafe.adriel.voyager.core.model.StateScreenModel
import cc.sovellus.vrcaa.api.vrchat.models.Friend
import cc.sovellus.vrcaa.api.vrchat.models.User
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.manager.FriendManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class FriendsState {
    data object Init : FriendsState()
    data object Loading : FriendsState()
    data object Result : FriendsState()
}

class FriendsScreenModel : StateScreenModel<FriendsState>(FriendsState.Init) {

    private var friendsStateFlow = MutableStateFlow(mutableStateListOf<Friend>())
    var friends = friendsStateFlow.asStateFlow()

    var currentIndex = mutableIntStateOf(0)

    private val listener = object : FriendManager.FriendListener {
        override fun onUpdateFriends(friends: MutableList<Friend>) {
            friendsStateFlow.value = friends.toMutableStateList()
        }
    }

    private val cacheListener = object : CacheManager.CacheListener {
        override fun profileUpdated(profile: User) {}

        override fun startCacheRefresh() {}

        override fun endCacheRefresh() {
            friendsStateFlow.value = FriendManager.getFriends().toMutableStateList()
            mutableState.value = FriendsState.Result
        }

        override fun recentlyVisitedUpdated(worlds: MutableList<CacheManager.WorldCache>) {}
    }

    init {
        mutableState.value = FriendsState.Loading
        FriendManager.addFriendListener(listener)
        CacheManager.addListener(cacheListener)

        if (!CacheManager.isRefreshing()) {
            friendsStateFlow.value = FriendManager.getFriends().toMutableStateList()
            mutableState.value = FriendsState.Result
        }
    }
}
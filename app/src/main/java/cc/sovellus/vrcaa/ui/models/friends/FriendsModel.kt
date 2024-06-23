package cc.sovellus.vrcaa.ui.models.friends

import androidx.compose.runtime.mutableIntStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
import cc.sovellus.vrcaa.manager.FriendManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FriendsModel : ScreenModel {
    private var friendsStateFlow = MutableStateFlow(mutableListOf<LimitedUser>())
    var friends = friendsStateFlow.asStateFlow()

    var currentIndex = mutableIntStateOf(0)

    private val listener = object : FriendManager.FriendListener {
        override fun onUpdateFriends(friends: MutableList<LimitedUser>) {
            screenModelScope.launch {
                friendsStateFlow.update { friends }
            }
        }
    }

    init {
        FriendManager.addFriendListener(listener)
        friendsStateFlow.update { FriendManager.getFriends() }
    }
}
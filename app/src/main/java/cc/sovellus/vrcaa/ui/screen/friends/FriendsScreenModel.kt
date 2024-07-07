package cc.sovellus.vrcaa.ui.screen.friends

import androidx.compose.runtime.mutableIntStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.api.vrchat.models.Friend
import cc.sovellus.vrcaa.manager.FriendManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FriendsScreenModel : ScreenModel {
    private var friendsStateFlow = MutableStateFlow(listOf<Friend>())
    var friends = friendsStateFlow.asStateFlow()

    var currentIndex = mutableIntStateOf(0)

    private val listener = object : FriendManager.FriendListener {
        override fun onUpdateFriends(friends: MutableList<Friend>) {
            friendsStateFlow.value = friends.toList()
        }
    }

    init {
        FriendManager.addFriendListener(listener)
        friendsStateFlow.value = FriendManager.getFriends()
    }
}
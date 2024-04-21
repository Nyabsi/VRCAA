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

class FriendsModel(
    private val
    context: Context
) : StateScreenModel<FriendsModel.FriendListState>(FriendListState.Init) {

    sealed class FriendListState {
        data object Init : FriendListState()
        data object Loading : FriendListState()
        data object Result : FriendListState()
    }

    private var friendsStateFlow = MutableStateFlow(listOf<LimitedUser>())
    var friends = friendsStateFlow.asStateFlow()

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

        screenModelScope.launch {
            while (FriendManager.getFriends().isEmpty())
                delay(10)
            friendsStateFlow.update { FriendManager.getFriends() }
            mutableState.value = FriendListState.Result
        }
    }
}
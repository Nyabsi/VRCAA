package cc.sovellus.vrcaa.ui.screen.home

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.manager.CacheManager.WorldCache
import cc.sovellus.vrcaa.api.vrchat.models.Friend
import cc.sovellus.vrcaa.manager.FriendManager
import cc.sovellus.vrcaa.widgets.FriendWidgetReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenModel(context: Context) : ScreenModel {

    private var friendsListFlow = MutableStateFlow(mutableStateListOf<Friend>())
    var friendsList = friendsListFlow.asStateFlow()

    private var recentlyVisitedFlow = MutableStateFlow(mutableStateListOf<WorldCache>())
    var recentlyVisited = recentlyVisitedFlow.asStateFlow()

    private val listener = object : FriendManager.FriendListener {
        override fun onUpdateFriends(friends: MutableList<Friend>) {
            friendsListFlow.value = friends.toMutableStateList()
        }
    }

    val initialLoadComplete = mutableStateOf(false)

    private val cacheListener = object : CacheManager.CacheListener {
        override fun recentlyVisitedUpdated(worlds: MutableList<WorldCache>) {
            recentlyVisitedFlow.value = worlds.toMutableStateList()
        }

        override fun cacheUpdated() {
            if (!initialLoadComplete.value) {
                initialLoadComplete.value = true
            }

            val intent = Intent(context, FriendWidgetReceiver::class.java).apply { action = "FRIEND_LOCATION_UPDATE" }
            context.sendBroadcast(intent)
            fetchContent()
        }
    }

    init {
        FriendManager.addFriendListener(listener)
        CacheManager.addListener(cacheListener)
        fetchContent()
    }

    private fun fetchContent() {
        screenModelScope.launch {
            friendsListFlow.value = FriendManager.getFriends().toMutableStateList()
            recentlyVisitedFlow.value = CacheManager.getRecent().toMutableStateList()
        }
    }
}
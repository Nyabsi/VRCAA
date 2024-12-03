package cc.sovellus.vrcaa.ui.screen.feed

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import cafe.adriel.voyager.core.model.StateScreenModel
import cc.sovellus.vrcaa.api.vrchat.models.User
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.manager.FeedManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FeedScreenModel : StateScreenModel<FeedScreenModel.FeedState>(FeedState.Init) {

    sealed class FeedState {
        data object Init : FeedState()
        data object Loading : FeedState()
        data object Result : FeedState()
    }

    private var feedStateFlow = MutableStateFlow(mutableStateListOf<FeedManager.Feed>())
    var feed = feedStateFlow.asStateFlow()

    var currentIndex = mutableIntStateOf(0)

    private val listener = object : FeedManager.FeedListener {
        override fun onReceiveUpdate(list: MutableList<FeedManager.Feed>) {
            feedStateFlow.value = list.toMutableStateList()
        }
    }

    private val cacheListener = object : CacheManager.CacheListener {
        override fun profileUpdated(profile: User) { }

        override fun startCacheRefresh() { }

        override fun endCacheRefresh() {
            feedStateFlow.value = FeedManager.getFeed().toMutableStateList()
            mutableState.value = FeedState.Result
        }

        override fun recentlyVisitedUpdated(worlds: MutableList<CacheManager.WorldCache>) { }
    }

    init {
        mutableState.value = FeedState.Loading
        FeedManager.setFeedListener(listener)
        CacheManager.addListener(cacheListener)

        if (!CacheManager.isRefreshing())
        {
            feedStateFlow.value = FeedManager.getFeed().toMutableStateList()
            mutableState.value = FeedState.Result
        }
    }
}
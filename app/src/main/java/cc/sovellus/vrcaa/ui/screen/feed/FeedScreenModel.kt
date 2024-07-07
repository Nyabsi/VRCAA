package cc.sovellus.vrcaa.ui.screen.feed

import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.manager.FeedManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FeedScreenModel : ScreenModel {
    private var feedStateFlow = MutableStateFlow(listOf<FeedManager.Feed>())
    var feed = feedStateFlow.asStateFlow()

    private val listener = object : FeedManager.FeedListener {
        override fun onReceiveUpdate(list: MutableList<FeedManager.Feed>) {
            feedStateFlow.value = list.toList()
        }
    }

    init {
        FeedManager.setFeedListener(listener)

        val feed = FeedManager.getFeed()
        if (feed.isNotEmpty()) feedStateFlow.value = feed
    }
}
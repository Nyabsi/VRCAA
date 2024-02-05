package cc.sovellus.vrcaa.ui.screen.feed

import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.manager.FeedManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FeedScreenModel : ScreenModel {
    private val feedManager = FeedManager()

    private var feedStateFlow = MutableStateFlow(feedManager.getFeed().toList())
    var feed = feedStateFlow.asStateFlow()

    private val listener = object : FeedManager.FeedListener {
        override fun onReceiveUpdate(list: MutableList<FeedManager.Feed>) {
            val newList = list.toList()
            feedStateFlow.update { newList }
        }
    }

    init {
        feedManager.setListener(listener)
    }
}
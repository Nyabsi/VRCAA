package cc.sovellus.vrcaa.ui.screen.activities

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.manager.FeedManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ActivitiesScreenModel : ScreenModel {
    private var feedStateFlow = MutableStateFlow(mutableStateListOf<FeedManager.Feed>())
    var feed = feedStateFlow.asStateFlow()

    var currentIndex = mutableIntStateOf(0)

    private val listener = object : FeedManager.FeedListener {
        override fun onReceiveUpdate(list: MutableList<FeedManager.Feed>) {
            feedStateFlow.value = list.toMutableStateList()
        }
    }

    init {
        FeedManager.setFeedListener(listener)

        val feed = FeedManager.getFeed()
        if (feed.isNotEmpty()) feedStateFlow.value = feed.toMutableStateList()
    }
}
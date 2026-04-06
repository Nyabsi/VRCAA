/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.base.BaseManager
import cc.sovellus.vrcaa.helper.StatusHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import java.util.UUID


object FeedManager : BaseManager<FeedManager.FeedListener>() {

    // TODO: the feed should be paginated, currently we will only show latest 1000 entries, this will suffice.
    private const val MAX_FEED_ENTRIES = 1000

    interface FeedListener {
        fun onReceiveUpdate(list: List<Feed>)
    }

    enum class FeedType {
        FRIEND_FEED_ONLINE,
        FRIEND_FEED_OFFLINE,
        FRIEND_FEED_LOCATION,
        FRIEND_FEED_STATUS,
        FRIEND_FEED_FRIEND_REQUEST,
        FRIEND_FEED_REMOVED,
        FRIEND_FEED_ADDED,
        FRIEND_FEED_AVATAR,
        FRIEND_FEED_USERNAME_CHANGE;

        companion object {
            fun fromInt(value: Int): FeedType = entries.first { it.ordinal == value }
        }
    }

    data class Feed(
        val type: FeedType,
        var feedId: UUID = UUID.randomUUID(),
        var friendId: String = "",
        var friendName: String = "",
        var friendPictureUrl: String = "",
        var friendStatus: StatusHelper.Status = StatusHelper.Status.Offline,
        var travelDestination: String = "",
        var worldId: String = "",
        var avatarName: String = "",
        var feedTimestamp: LocalDateTime = LocalDateTime.now()
    )

    private val feedLock = Any()
    private val feedList: MutableList<Feed> = ArrayList()
    private val feedStateFlow = MutableStateFlow<List<Feed>>(emptyList())

    val feedState: StateFlow<List<Feed>> = feedStateFlow.asStateFlow()

    init {
        synchronized(feedLock) {
            DatabaseManager.readFeeds(MAX_FEED_ENTRIES).forEach {
                feedList.add(it)
            }
            feedStateFlow.value = feedList.toList()
        }
    }

    fun addFeed(feed: Feed) {
        synchronized(feedLock) {
            if (feedList.size >= MAX_FEED_ENTRIES) {
                feedList.removeAt(0)
            }
            feedList.add(feed)
            feedStateFlow.value = feedList.toList()
        }
        DatabaseManager.writeFeed(feed)
        val snapshot = feedStateFlow.value
        getListeners().forEach { listener ->
            listener.onReceiveUpdate(snapshot)
        }
    }

    fun getFeed(): List<Feed> {
        return feedStateFlow.value
    }
}
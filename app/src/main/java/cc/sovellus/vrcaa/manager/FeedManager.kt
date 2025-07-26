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
import java.time.LocalDateTime
import java.util.UUID


object FeedManager : BaseManager<FeedManager.FeedListener>() {

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
        FRIEND_FEED_AVATAR;

        companion object {
            fun fromInt(value: Int) = entries.first { it.ordinal == value }
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

    private var feedList: MutableList<Feed> = ArrayList()

    init {
        DatabaseManager.readFeeds().map {
            feedList.add(it)
        }
    }

    fun addFeed(feed: Feed) {
        feedList.add(feed)
        DatabaseManager.writeFeed(feed)
        val listSnapshot = feedList.toList()
        getListeners().forEach { listener ->
            listener.onReceiveUpdate(listSnapshot)
        }
    }

    fun getFeed(): List<Feed> {
        val listSnapshot = feedList.toList()
        return listSnapshot
    }
}
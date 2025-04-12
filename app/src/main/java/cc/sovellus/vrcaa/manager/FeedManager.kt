package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.base.BaseManager
import cc.sovellus.vrcaa.helper.StatusHelper
import java.time.LocalDateTime
import java.util.UUID


object FeedManager : BaseManager<FeedManager.FeedListener>() {

    interface FeedListener {
        fun onReceiveUpdate(list: MutableList<Feed>)
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

    init {
        DatabaseManager.readFeeds().map {
            feedList.add(it)
        }
    }

    private var feedList: MutableList<Feed> = ArrayList()

    fun addFeed(feed: Feed) {
        feedList.add(feed)
        DatabaseManager.writeFeed(feed)
        getListeners().forEach { listener ->
            listener.onReceiveUpdate(feedList)
        }
    }

    fun getFeed(): MutableList<Feed> {
        return feedList
    }
}
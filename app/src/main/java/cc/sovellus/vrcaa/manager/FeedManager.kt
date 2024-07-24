package cc.sovellus.vrcaa.manager

import android.util.Log
import cc.sovellus.vrcaa.extension.milliseconds
import cc.sovellus.vrcaa.helper.StatusHelper
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds


object FeedManager {

    private var feedListener: FeedListener? = null
    private var feedList: MutableList<Feed> = ArrayList()

    enum class FeedType {
        FRIEND_FEED_ONLINE,
        FRIEND_FEED_OFFLINE,
        FRIEND_FEED_LOCATION,
        FRIEND_FEED_STATUS,
        FRIEND_FEED_FRIEND_REQUEST,
        FRIEND_FEED_REMOVED,
        FRIEND_FEED_ADDED
    }

    data class Feed(
        val type: FeedType,
        var feedId: UUID = UUID.randomUUID(),
        var friendId: String = "",
        var friendName: String = "",
        var friendPictureUrl: String = "",
        var friendStatus: StatusHelper.Status = StatusHelper.Status.Offline,
        var travelDestination: String = "",
        var feedTimestamp: LocalDateTime = LocalDateTime.now()
    )

    interface FeedListener {
        fun onReceiveUpdate(list: MutableList<Feed>)
    }

    fun isDuplicate(feed: Feed): Boolean {
        val lastFeed = feedList.findLast { it.type == feed.type && it.friendId == feed.friendId }
        if (lastFeed != null) {
            val last: Long = lastFeed.feedTimestamp.milliseconds
            val incoming: Long = feed.feedTimestamp.milliseconds
            return abs(last - incoming) >= 1500
        }
        return false
    }

    fun addFeed(feed: Feed) {
        feedList.add(feed)
        feedListener?.onReceiveUpdate(feedList)
    }

    fun getFeed(): MutableList<Feed> {
        return feedList
    }

    fun setFeedListener(listener: FeedListener) {
        feedListener = listener
    }
}
package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.helper.StatusHelper
import cc.sovellus.vrcaa.api.models.LimitedUser
import java.time.LocalDateTime
import java.util.UUID


class FeedManager {

    enum class FeedType {
        FRIEND_FEED_UNKNOWN,
        FRIEND_FEED_ONLINE,
        FRIEND_FEED_OFFLINE,
        FRIEND_FEED_LOCATION,
        FRIEND_FEED_STATUS,
        FRIEND_FEED_FRIEND_REQUEST,
        FRIEND_FEED_REMOVED,
        FRIEND_FEED_ADDED
    }

    data class Feed(val type: FeedType) {
        var feedId: UUID = UUID.randomUUID()
        var friendId: String = ""
        var friendName: String = ""
        var friendPictureUrl: String = ""
        var friendStatus: StatusHelper.Status = StatusHelper.Status.Offline
        var travelDestination: String = ""
        var feedTimestamp: LocalDateTime = LocalDateTime.now()
    }

    interface FeedListener {
        fun onReceiveUpdate(list: MutableList<Feed>)
    }

    // the feedList should be shared across *any* instance of "FeedManager"
    // since it is accessed by the MainThread (UI) and other threads (ie. Service)
    companion object {
        @Volatile private var feedList: ArrayList<Feed> = ArrayList()
        @Volatile private var feedListener: FeedListener? = null
        @Volatile private var syncedFriends: ArrayList<LimitedUser> = ArrayList()
        @Volatile private var syncedOfflineFriends: ArrayList<LimitedUser> = ArrayList()
    }

    fun addFeed(feed: Feed) {
        synchronized(feedList) {
            feedList.add(feed)
            feedListener?.onReceiveUpdate(feedList)
        }
    }

    fun getFeed(): MutableList<Feed> {
        synchronized(feedList) {
            return feedList
        }
    }

    fun setListener(listener: FeedListener) {
        synchronized(listener) {
            feedListener = listener
        }
    }

    fun setFriends(friends: ArrayList<LimitedUser>, offline: Boolean = false) {
        if (!offline) {
            synchronized(syncedFriends) {
                syncedFriends = friends
            }
        } else {
            synchronized(syncedOfflineFriends) {
                syncedOfflineFriends = friends
            }
        }
    }

    fun addFriend(friend: LimitedUser, offline: Boolean = false) {
        if (!offline) {
            synchronized(syncedFriends) {
                syncedFriends.add(friend)
                removeFriend(friend.id, true)
            }
        } else {
            synchronized(syncedOfflineFriends) {
                syncedOfflineFriends.add(friend)
            }
        }
    }

    fun removeFriend(userId: String, offline: Boolean = false) {
        if (!offline) {
            synchronized(syncedFriends) {
                val friend = syncedFriends.find { it.id == userId }!!
                syncedFriends.remove(friend)
                addFriend(friend, true)
            }
        } else {
            synchronized(syncedOfflineFriends) {
                syncedOfflineFriends.remove(syncedOfflineFriends.find { it.id == userId })
            }
        }
    }

    fun getFriend(userId: String): LimitedUser? {
        synchronized(syncedFriends) {
            return syncedFriends.find { it.id == userId }
        }
    }

    fun updateFriend(friend: LimitedUser) {
        synchronized(syncedFriends) {
            syncedFriends.set(syncedFriends.indexOf(syncedFriends.find { it.id == friend.id }!!), friend)
        }
    }

    fun getFriends(offline: Boolean = false): ArrayList<LimitedUser> {
        return if (!offline) {
            syncedFriends
        } else {
            syncedOfflineFriends
        }
    }
}
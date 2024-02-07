package cc.sovellus.vrcaa.manager

import android.util.Log
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

    interface FriendListener {
        fun onUpdateFriends(friends: MutableList<LimitedUser>, offline: Boolean)
    }

    // the feedList should be shared across *any* instance of "FeedManager"
    // since it is accessed by the MainThread (UI) and other threads (ie. Service)
    companion object {
        @Volatile private var feedListener: FeedListener? = null
        @Volatile private var friendListener: FriendListener? = null
        @Volatile private var feedList: MutableList<Feed> = ArrayList()
        @Volatile private var syncedFriends: MutableList<LimitedUser> = ArrayList()
        @Volatile private var syncedOfflineFriends: MutableList<LimitedUser> = ArrayList()
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

    fun setFeedListener(listener: FeedListener) {
        synchronized(listener) {
            feedListener = listener
        }
    }

    fun setFriendListener(listener: FriendListener) {
        synchronized(listener) {
            friendListener = listener
        }
    }

    fun setFriends(friends: ArrayList<LimitedUser>, offline: Boolean = false) {
        if (!offline) {
            synchronized(friends) {
                syncedFriends = friends
            }
        } else {
            synchronized(friends) {
                syncedOfflineFriends = friends
            }
        }
    }

    fun addFriend(friend: LimitedUser) {
        if (syncedFriends.find { it.id == friend.id } == null)
        {
            synchronized(friend) {
                syncedFriends.add(friend)
                friendListener?.onUpdateFriends(syncedFriends, false)
            }
        }

        if (syncedOfflineFriends.find { it.id == friend.id } != null)
        {
            synchronized(friend) {
                syncedOfflineFriends.remove(friend)
                friendListener?.onUpdateFriends(syncedOfflineFriends, true)
            }
        }
    }

    fun removeFriend(userId: String, user: LimitedUser) {
        synchronized(userId) {
            val friend = syncedFriends.find { it.id == userId }

            friend?.let {
                syncedOfflineFriends.add(user)
            }

            friendListener?.onUpdateFriends(syncedOfflineFriends, true)

            friend?.let {
                syncedFriends.remove(friend)
            }

            friendListener?.onUpdateFriends(syncedFriends, false)
        }
    }

    fun getFriend(userId: String): LimitedUser? {
        return syncedFriends.find { it.id == userId }
    }

    fun updateFriend(friend: LimitedUser) {
        synchronized(friend) {
            val tmp = syncedFriends.find { it.id == friend.id }
            tmp?.let {
                syncedFriends.set(syncedFriends.indexOf(tmp), friend)
            }
            friendListener?.onUpdateFriends(syncedFriends, false)
        }
    }

    fun getFriends(offline: Boolean = false): MutableList<LimitedUser> {
        return if (!offline) {
            syncedFriends
        } else {
            syncedOfflineFriends
        }
    }
}
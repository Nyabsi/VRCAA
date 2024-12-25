package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.vrchat.http.models.Friend

object FriendManager {

    private var friendListeners: MutableList<FriendListener> = mutableListOf()
    private var friends: MutableList<Friend> = ArrayList()

    interface FriendListener {
        fun onUpdateFriends(friends: MutableList<Friend>)
    }

    fun addFriendListener(listener: FriendListener) {
        friendListeners.add(listener)
    }

    fun setFriends(friends: MutableList<Friend>) {
        this.friends = friends
        friendListeners.map {
            it.onUpdateFriends(friends)
        }
    }

    fun addFriend(friend: Friend) {
        if (friends.find { it.id == friend.id } == null) {
            friends.add(friend)
            friendListeners.map {
                it.onUpdateFriends(friends)
            }
        }
    }

    fun removeFriend(userId: String) {
        val friend = friends.find { it.id == userId }

        friend?.let {
            friends.remove(friend)
        }

        friendListeners.map {
            it.onUpdateFriends(friends)
        }
    }

    fun getFriend(userId: String): Friend? {
        return friends.find { it.id == userId }
    }

    fun updateFriend(friend: Friend) {
        val it = friends.find { it.id == friend.id }
        it?.let {
            it.bio = friend.bio
            it.bioLinks = friend.bioLinks
            it.currentAvatarImageUrl = friend.currentAvatarImageUrl
            it.currentAvatarTags = friend.currentAvatarTags
            it.currentAvatarThumbnailImageUrl = friend.currentAvatarThumbnailImageUrl
            it.developerType = friend.developerType
            it.displayName = friend.displayName
            it.friendKey = friend.friendKey
            it.id = friend.id
            it.imageUrl = friend.imageUrl
            it.isFriend = friend.isFriend
            it.lastLogin = it.lastLogin
            it.lastMobile = it.lastMobile
            it.lastPlatform = friend.lastPlatform
            it.location = friend.location
            it.profilePicOverride = friend.profilePicOverride
            it.profilePicOverrideThumbnail = friend.profilePicOverrideThumbnail
            it.status = friend.status
            it.statusDescription = friend.statusDescription
            it.tags = friend.tags
            it.userIcon = friend.userIcon
            it.platform = friend.platform
            friends.set(friends.indexOf(it), it)
        }

        friendListeners.map {
            it.onUpdateFriends(friends)
        }
    }

    fun updateLocation(userId: String, location: String) {
        val it = friends.find { it.id == userId }
        it?.let {
            it.location = location
            friends.set(friends.indexOf(it), it)
        }
        friendListeners.map {
            it.onUpdateFriends(friends)
        }
    }

    fun updateStatus(userId: String, status: String) {
        val it = friends.find { it.id == userId }
        it?.let {
            it.status = status
            friends.set(friends.indexOf(it), it)
        }

        friendListeners.map {
            it.onUpdateFriends(friends)
        }
    }

    fun updatePlatform(userId: String, platform: String) {
        val it = friends.find { it.id == userId }
        it?.let {
            it.platform = platform
            friends.set(friends.indexOf(it), it)
        }

        friendListeners.map {
            it.onUpdateFriends(friends)
        }
    }

    fun getFriends(): MutableList<Friend> {
        return friends
    }
}
package cc.sovellus.vrcaa.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process.THREAD_PRIORITY_FOREGROUND
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.PipelineContext
import cc.sovellus.vrcaa.api.helper.LocationHelper
import cc.sovellus.vrcaa.api.helper.StatusHelper
import cc.sovellus.vrcaa.api.models.Friends
import cc.sovellus.vrcaa.api.models.pipeline.FriendAdd
import cc.sovellus.vrcaa.api.models.pipeline.FriendDelete
import cc.sovellus.vrcaa.api.models.pipeline.FriendLocation
import cc.sovellus.vrcaa.api.models.pipeline.FriendOffline
import cc.sovellus.vrcaa.api.models.pipeline.FriendOnline
import cc.sovellus.vrcaa.api.models.pipeline.Notification
import cc.sovellus.vrcaa.api.models.pipeline.UserBase
import cc.sovellus.vrcaa.helper.api
import cc.sovellus.vrcaa.manager.FeedManager
import cc.sovellus.vrcaa.manager.NotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PipelineService : Service(), CoroutineScope {

    override val coroutineContext = Dispatchers.Main + SupervisorJob()

    private lateinit var pipeline: PipelineContext

    private var friends: ArrayList<UserBase> = arrayListOf()
    private lateinit var activeFriends: Friends

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    private lateinit var notificationManager: NotificationManager
    private var notificationCounter: Int = 0

    private val feedManager = FeedManager()

    private fun pushNotification(
        title: String,
        content: String,
        channel: String
    ) {
        val flags = NotificationCompat.FLAG_ONGOING_EVENT

        val builder = NotificationCompat.Builder(this, channel)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(flags)

        val notificationManager = NotificationManagerCompat.from(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(notificationCounter, builder.build())
            notificationCounter++
        }
    }

    private val listener = object : PipelineContext.SocketListener {
        override fun onMessage(message: Any?) {
            if (message != null) {
                serviceHandler?.obtainMessage()?.also { msg ->
                    msg.obj = message
                    serviceHandler?.sendMessage(msg)
                }
            }
        }
    }

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            when (msg.obj) {
                is FriendOnline -> {
                    val friend = msg.obj as FriendOnline

                    // We don't want to add duplicates to the list.
                    if (friends.find { it.id == friend.userId } == null)
                        friends.add(friend.user)

                    if (notificationManager.isOnWhitelist(friend.userId) &&
                        notificationManager.isIntentEnabled(friend.userId, NotificationManager.Intents.FRIEND_FLAG_ONLINE)) {
                        pushNotification(
                            title = application.getString(R.string.notification_service_title_online),
                            content = application.getString(R.string.notification_service_description_online).format(friend.user.displayName),
                            channel = App.CHANNEL_ONLINE_ID
                        )
                    }

                    feedManager.addFeed(FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_ONLINE).apply {
                        friendId = friend.userId
                        friendName = friend.user.displayName
                        friendPictureUrl = friend.user.userIcon.ifEmpty { friend.user.currentAvatarImageUrl }
                    })
                }
                is FriendOffline -> {
                    val friend = msg.obj as FriendOffline

                    // Remove from list if found.
                    val friendObject = friends.find { it.id == friend.userId }
                    if (friendObject != null)
                    {
                        if (notificationManager.isOnWhitelist(friend.userId) &&
                            notificationManager.isIntentEnabled(friend.userId, NotificationManager.Intents.FRIEND_FLAG_OFFLINE)) {
                            pushNotification(
                                title = application.getString(R.string.notification_service_title_offline),
                                content = application.getString(R.string.notification_service_description_offline).format(friendObject.displayName),
                                channel = App.CHANNEL_OFFLINE_ID
                            )
                        }

                        feedManager.addFeed(FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_OFFLINE).apply {
                            friendId = friend.userId
                            friendName = friendObject.displayName
                            friendPictureUrl = friendObject.userIcon.ifEmpty { friendObject.currentAvatarImageUrl }
                        })

                        friends = friends.filter { it.id != friend.userId } as ArrayList<UserBase>
                    } else {
                        // It seems it was not cached during local session, instead fallback to currentFriends
                        val fallbackFriend = activeFriends.find { it.id == friend.userId }

                        if (fallbackFriend != null) {
                            if (notificationManager.isOnWhitelist(friend.userId) &&
                                notificationManager.isIntentEnabled(friend.userId, NotificationManager.Intents.FRIEND_FLAG_OFFLINE)) {
                                pushNotification(
                                    title = application.getString(R.string.notification_service_title_offline),
                                    content = application.getString(R.string.notification_service_description_offline).format(
                                        fallbackFriend.displayName
                                    ),
                                    channel = App.CHANNEL_OFFLINE_ID
                                )
                            }

                            feedManager.addFeed(FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_OFFLINE).apply {
                                friendId = friend.userId
                                friendName = fallbackFriend.displayName
                                friendPictureUrl = fallbackFriend.userIcon.ifEmpty { fallbackFriend.currentAvatarImageUrl }
                            })

                            activeFriends.remove(activeFriends.find { it.id == friend.userId })
                        }
                    }
                }
                is FriendLocation -> {
                    val friend = msg.obj as FriendLocation

                    val friendObject = friends.find { it.id == friend.userId }
                    if (friendObject != null)
                    {
                        if (
                            StatusHelper().getStatusFromString(friend.user.status) !=
                            StatusHelper().getStatusFromString(friendObject.status))
                        {
                            if (notificationManager.isOnWhitelist(friend.userId) &&
                                notificationManager.isIntentEnabled(friend.userId, NotificationManager.Intents.FRIEND_FLAG_STATUS)) {
                                pushNotification(
                                    title = application.getString(R.string.notification_service_title_status),
                                    content = application.getString(R.string.notification_service_description_status).format(
                                        friendObject.displayName,
                                        StatusHelper().getStatusFromString(friendObject.status).toString(),
                                        StatusHelper().getStatusFromString(friend.user.status).toString()
                                    ),
                                    channel = App.CHANNEL_LOCATION_ID
                                )
                            }

                            feedManager.addFeed(FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_STATUS).apply {
                                friendId = friend.userId
                                friendName = friendObject.displayName
                                friendPictureUrl = friendObject.userIcon.ifEmpty { friendObject.currentAvatarImageUrl }
                                friendStatus = StatusHelper().getStatusFromString(friend.user.status)
                            })

                            val tmp = friends.find { it.id == friend.userId }
                            tmp?.let {
                                it.status = friend.user.status
                                friends.set(friends.indexOf(tmp), tmp)
                            }
                        }
                    } else {
                        val fallbackFriend = activeFriends.find { it.id == friend.userId }

                        if (
                            StatusHelper().getStatusFromString(friend.user.status) !=
                            StatusHelper().getStatusFromString(fallbackFriend?.status.toString())
                        )
                        {
                            if (notificationManager.isOnWhitelist(friend.userId) &&
                                notificationManager.isIntentEnabled(friend.userId, NotificationManager.Intents.FRIEND_FLAG_STATUS)) {
                                pushNotification(
                                    title = application.getString(R.string.notification_service_title_status),
                                    content = application.getString(R.string.notification_service_description_status).format(
                                        fallbackFriend?.displayName,
                                        StatusHelper().getStatusFromString(fallbackFriend?.status.toString()).toString(),
                                        StatusHelper().getStatusFromString(friend.user.status).toString()
                                    ),
                                    channel = App.CHANNEL_LOCATION_ID
                                )
                            }

                            feedManager.addFeed(FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_STATUS).apply {
                                friendId = friend.userId
                                friendName = fallbackFriend?.displayName.toString()
                                friendPictureUrl = fallbackFriend?.userIcon?.ifEmpty { fallbackFriend.currentAvatarImageUrl }.toString()
                                friendStatus = StatusHelper().getStatusFromString(friend.user.status)
                            })

                            val tmp = activeFriends.find { it.id == friend.userId }
                            tmp?.let {
                                it.status = friend.user.status
                                activeFriends.set(activeFriends.indexOf(tmp), tmp)
                            }
                        }
                    }

                    // if "friend.travelingToLocation" is not empty, it means friend is currently travelling.
                    // We want to show it only once, so only show when the travelling is done.
                    if (friend.travelingToLocation.isEmpty()) {
                        if (notificationManager.isOnWhitelist(friend.userId) &&
                            notificationManager.isIntentEnabled(friend.userId, NotificationManager.Intents.FRIEND_FLAG_LOCATION)) {
                            pushNotification(
                                title = application.getString(R.string.notification_service_title_location),
                                content = application.getString(R.string.notification_service_description_location).format(friend.user.displayName, friend.world.name),
                                channel = App.CHANNEL_LOCATION_ID
                            )
                        }

                        val result = LocationHelper().parseLocationIntent(friend.location)

                        val locationFormatted = if (result.regionId.isNotEmpty()) {
                            "${friend.world.name}~(${result.instanceType}) ${result.regionId.uppercase()}"
                        } else if (result.instanceType.isNotEmpty()) {
                            "${friend.world.name}~(${result.instanceType})"
                        } else {
                            friend.world.name
                        }

                        feedManager.addFeed(FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_LOCATION).apply {
                            friendId = friend.userId
                            friendName = friend.user.displayName
                            travelDestination = locationFormatted
                            friendPictureUrl = friend.user.userIcon.ifEmpty { friend.user.currentAvatarImageUrl }
                        })
                    }
                }
                is FriendDelete -> {
                    val friend = msg.obj as FriendDelete

                    val friendObject = friends.find { it.id == friend.userId }
                    if (friendObject != null) {
                        feedManager.addFeed(FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_REMOVED).apply {
                            friendId = friend.userId
                            friendName = friendObject.displayName
                            friendPictureUrl = friendObject.userIcon.ifEmpty { friendObject.currentAvatarImageUrl }

                        })

                        pushNotification(
                            title = application.getString(R.string.notification_service_title_friend_removed),
                            content = application.getString(R.string.notification_service_description_friend_removed).format(friendObject.displayName),
                            channel = App.CHANNEL_STATUS_ID
                        )

                        friends = friends.filter { it.id != friend.userId } as ArrayList<UserBase>
                    } else {
                        val fallbackFriend = activeFriends.find { it.id == friend.userId }

                        feedManager.addFeed(FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_REMOVED).apply {
                            friendId = friend.userId
                            friendName = fallbackFriend?.displayName.toString()
                            friendPictureUrl = fallbackFriend?.userIcon?.ifEmpty { fallbackFriend.currentAvatarImageUrl }.toString()
                        })

                        pushNotification(
                            title = application.getString(R.string.notification_service_title_friend_removed),
                            content = application.getString(R.string.notification_service_description_friend_removed).format(fallbackFriend?.displayName.toString()),
                            channel = App.CHANNEL_STATUS_ID
                        )

                        activeFriends.remove(activeFriends.find { it.id == friend.userId })
                    }
                }
                is FriendAdd -> {
                    val friend = msg.obj as FriendAdd

                    launch {
                        withContext(Dispatchers.Main) {
                            // You're unlike to get that many friends that this would be harmful, it is a shitty workaround til I refactor the code, deal with it.
                            // You would rather have this than the application crashing on your face, would you now?
                            api.get().getFriends().let { friends ->
                                if (friends != null) {
                                    activeFriends = friends
                                }
                            }
                        }
                    }

                    // Both Friend Add and Remove should send notification regardless of the specified setting.
                    pushNotification(
                        title = application.getString(R.string.notification_service_title_friend_added),
                        content = application.getString(R.string.notification_service_description_friend_added).format(friend.user.displayName),
                        channel = App.CHANNEL_STATUS_ID
                    )

                    feedManager.addFeed(FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_ADDED).apply {
                        friendId = friend.userId
                        friendName = friend.user.displayName
                        friendPictureUrl = friend.user.userIcon.ifEmpty { friend.user.currentAvatarImageUrl }
                    })
                }
                is Notification -> {
                    val notification = msg.obj as Notification

                    // Welcome to the world of Kotlin :)
                    launch {
                        withContext(Dispatchers.Main) {
                            val sender = api.get().getUser(notification.senderUserId)

                            when (notification.type) {
                                "friendRequest" -> {
                                    feedManager.addFeed(FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_FRIEND_REQUEST).apply {
                                        friendId = notification.senderUserId
                                        friendName = notification.senderUsername
                                        friendPictureUrl = sender?.let { it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl } }.toString()
                                    })
                                }
                                else -> {
                                    Log.d("VRCAA", "Received unknown notificationType: ${notification.type}")
                                }
                            }
                        }
                    }
                }
                else -> { /* Not Implemented */ }
            }
        }
    }

    override fun onCreate() {
        HandlerThread("VRCAA_BackgroundWorker", THREAD_PRIORITY_FOREGROUND).apply {
            start()

            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        this.notificationManager = NotificationManager(this)

        launch {
            withContext(Dispatchers.Main) {
                api.get().getAuth().let { token ->
                    if (!token.isNullOrEmpty()) {
                        pipeline = PipelineContext(token)
                        pipeline.let { pipeline ->
                            pipeline.connect()
                            listener.let { pipeline.setListener(it) }
                        }

                        api.get().getFriends().let { friends ->
                            if (friends != null) {
                                activeFriends = friends
                            }
                        }
                    }
                }
            }
        }

        val builder = NotificationCompat.Builder(this, App.CHANNEL_DEFAULT_ID)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(application.getString(R.string.app_name))
            .setContentText(application.getString(R.string.service_notification))
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setOngoing(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID_STICKY, builder.build(), FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            // Older versions do not require to specify the `foregroundServiceType`
            startForeground(NOTIFICATION_ID_STICKY, builder.build())
        }

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        pipeline.disconnect()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val NOTIFICATION_ID_STICKY = 42069
    }
}
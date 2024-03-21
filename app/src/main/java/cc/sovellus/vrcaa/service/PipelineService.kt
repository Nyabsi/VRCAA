package cc.sovellus.vrcaa.service

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process.THREAD_PRIORITY_FOREGROUND
import android.util.Log
import androidx.core.app.NotificationCompat
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.discord.GatewaySocket
import cc.sovellus.vrcaa.api.websocket.PipelineContext
import cc.sovellus.vrcaa.api.websocket.models.FriendAdd
import cc.sovellus.vrcaa.api.websocket.models.FriendDelete
import cc.sovellus.vrcaa.api.websocket.models.FriendLocation
import cc.sovellus.vrcaa.api.websocket.models.FriendOffline
import cc.sovellus.vrcaa.api.websocket.models.FriendOnline
import cc.sovellus.vrcaa.api.websocket.models.Notification
import cc.sovellus.vrcaa.api.websocket.models.UserLocation
import cc.sovellus.vrcaa.helper.LocationHelper
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.helper.discordToken
import cc.sovellus.vrcaa.helper.richPresenceEnabled
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.FeedManager
import cc.sovellus.vrcaa.manager.FriendManager
import cc.sovellus.vrcaa.manager.NotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PipelineService : Service(), CoroutineScope {

    override val coroutineContext = Dispatchers.IO + SupervisorJob()

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    private lateinit var notificationManager: NotificationManager

    private var pipeline: PipelineContext? = null
    private var gateway: GatewaySocket? = null

    private lateinit var preferences: SharedPreferences

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
                    friend.user.location = ""

                    if (notificationManager.isOnWhitelist(friend.userId) &&
                        notificationManager.isIntentEnabled(
                            friend.userId,
                            NotificationManager.Intents.FRIEND_FLAG_ONLINE
                        )
                    ) {
                        notificationManager.pushNotification(
                            title = application.getString(R.string.notification_service_title_online),
                            content = application.getString(R.string.notification_service_description_online)
                                .format(friend.user.displayName),
                            channel = NotificationManager.CHANNEL_ONLINE_ID
                        )
                    }

                    FeedManager.addFeed(FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_ONLINE).apply {
                        friendId = friend.userId
                        friendName = friend.user.displayName
                        friendPictureUrl = friend.user.userIcon.ifEmpty { friend.user.currentAvatarImageUrl }
                    })

                    if (FriendManager.getFriend(friend.userId) == null) {
                        FriendManager.addFriend(friend.user)
                    }
                }

                is FriendOffline -> {

                    val friend = msg.obj as FriendOffline
                    val cachedFriend = FriendManager.getFriend(friend.userId)

                    if (cachedFriend != null) {
                        if (notificationManager.isOnWhitelist(friend.userId) &&
                            notificationManager.isIntentEnabled(
                                friend.userId,
                                NotificationManager.Intents.FRIEND_FLAG_OFFLINE
                            )
                        ) {
                            notificationManager.pushNotification(
                                title = application.getString(R.string.notification_service_title_offline),
                                content = application.getString(R.string.notification_service_description_offline)
                                    .format(cachedFriend.displayName),
                                channel = NotificationManager.CHANNEL_OFFLINE_ID
                            )
                        }

                        FeedManager.addFeed(
                            FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_OFFLINE).apply {
                                friendId = friend.userId
                                friendName = cachedFriend.displayName
                                friendPictureUrl =
                                    cachedFriend.userIcon.ifEmpty { cachedFriend.currentAvatarImageUrl }
                            })
                    }

                    launch {
                        api.getUser(friend.userId)?.let { user ->
                            FriendManager.updateFriend(user)
                        }
                    }
                }

                is FriendLocation -> {

                    val friend = msg.obj as FriendLocation
                    val cachedFriend = FriendManager.getFriend(friend.userId)

                    // For some reason, VRChat doesn't also set the user object location properly...
                    // It took me literally, *hours* to figure this. I'm out.
                    friend.user.location = friend.location

                    if (cachedFriend != null) {
                        if (
                            StatusHelper.getStatusFromString(friend.user.status) !=
                            StatusHelper.getStatusFromString(cachedFriend.status)
                        ) {
                            if (notificationManager.isOnWhitelist(friend.userId) &&
                                notificationManager.isIntentEnabled(
                                    friend.userId,
                                    NotificationManager.Intents.FRIEND_FLAG_STATUS
                                )
                            ) {
                                notificationManager.pushNotification(
                                    title = application.getString(R.string.notification_service_title_status),
                                    content = application.getString(R.string.notification_service_description_status)
                                        .format(
                                            cachedFriend.displayName,
                                            StatusHelper.getStatusFromString(cachedFriend.status)
                                                .toString(),
                                            StatusHelper.getStatusFromString(friend.user.status)
                                                .toString()
                                        ),
                                    channel = NotificationManager.CHANNEL_LOCATION_ID
                                )
                            }

                            FeedManager.addFeed(
                                FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_STATUS).apply {
                                    friendId = friend.userId
                                    friendName = friend.user.displayName
                                    friendPictureUrl =
                                        friend.user.userIcon.ifEmpty { friend.user.currentAvatarImageUrl }
                                    friendStatus =
                                        StatusHelper.getStatusFromString(friend.user.status)
                                }
                            )
                        } else {
                            // if "friend.travelingToLocation" is not empty, it means friend is currently travelling.
                            // We want to show it only once, so only show when the travelling is done.
                            if (friend.travelingToLocation.isEmpty()) {
                                if (notificationManager.isOnWhitelist(friend.userId) &&
                                    notificationManager.isIntentEnabled(
                                        friend.userId,
                                        NotificationManager.Intents.FRIEND_FLAG_LOCATION
                                    )
                                ) {
                                    notificationManager.pushNotification(
                                        title = application.getString(R.string.notification_service_title_location),
                                        content = application.getString(R.string.notification_service_description_location)
                                            .format(friend.user.displayName, friend.world.name),
                                        channel = NotificationManager.CHANNEL_LOCATION_ID
                                    )
                                }

                                val result = LocationHelper.parseLocationIntent(friend.location)

                                val locationFormatted = if (result.regionId.isNotEmpty()) {
                                    "${friend.world.name} (${result.instanceType}) ${result.regionId.uppercase()}"
                                } else {
                                    "${friend.world.name} (${result.instanceType}) US"
                                }

                                FeedManager.addFeed(
                                    FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_LOCATION)
                                        .apply {
                                            friendId = friend.userId
                                            friendName = friend.user.displayName
                                            travelDestination = locationFormatted
                                            friendPictureUrl =
                                                friend.user.userIcon.ifEmpty { friend.user.currentAvatarImageUrl }
                                        })
                            }
                        }
                    }

                    FriendManager.updateFriend(friend.user)
                }

                is UserLocation -> {
                    val user = msg.obj as UserLocation

                    val status = StatusHelper.getStatusFromString(user.user.status)
                    val location = LocationHelper.parseLocationIntent(user.location)

                    if (preferences.richPresenceEnabled) {
                        launch {
                            val instance = api.getInstance(user.location)
                            if (status == StatusHelper.Status.Active || status == StatusHelper.Status.JoinMe) {
                                instance?.world?.name?.let { gateway?.sendPresence(it, "${location.instanceType} #${instance?.name} ${if (BuildConfig.FLAVOR == "quest") { "(VR)" } else { "(Mobile)" }} (${instance.nUsers} of ${instance.capacity})") }
                            } else {
                                gateway?.sendPresence(status.toString(), "User location is hidden.")
                            }
                        }
                    }
                }

                is FriendDelete -> {
                    val friend = msg.obj as FriendDelete
                    val cachedFriend = FriendManager.getFriend(friend.userId)

                    if (cachedFriend != null) {
                        FeedManager.addFeed(
                            FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_REMOVED).apply {
                                friendId = friend.userId
                                friendName = cachedFriend.displayName
                                friendPictureUrl =
                                    cachedFriend.userIcon.ifEmpty { cachedFriend.currentAvatarImageUrl }

                            })

                        notificationManager.pushNotification(
                            title = application.getString(R.string.notification_service_title_friend_removed),
                            content = application.getString(R.string.notification_service_description_friend_removed)
                                .format(cachedFriend.displayName),
                            channel = NotificationManager.CHANNEL_STATUS_ID
                        )
                    }

                    FriendManager.removeFriend(friend.userId)
                }

                is FriendAdd -> {
                    val friend = msg.obj as FriendAdd

                    // Both Friend Add and Remove should send notification regardless of the specified setting.
                    notificationManager.pushNotification(
                        title = application.getString(R.string.notification_service_title_friend_added),
                        content = application.getString(R.string.notification_service_description_friend_added)
                            .format(friend.user.displayName),
                        channel = NotificationManager.CHANNEL_STATUS_ID
                    )

                    FeedManager.addFeed(
                        FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_ADDED).apply {
                            friendId = friend.userId
                            friendName = friend.user.displayName
                            friendPictureUrl =
                                friend.user.userIcon.ifEmpty { friend.user.currentAvatarImageUrl }
                        })

                    FriendManager.addFriend(friend.user)
                }

                is Notification -> {
                    val notification = msg.obj as Notification

                    launch {
                        withContext(Dispatchers.Main) {
                            val sender = api.getUser(notification.senderUserId)

                            when (notification.type) {
                                "friendRequest" -> {
                                    FeedManager.addFeed(
                                        FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_FRIEND_REQUEST)
                                            .apply {
                                                friendId = notification.senderUserId
                                                friendName = notification.senderUsername
                                                friendPictureUrl =
                                                    sender?.let { it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl } }
                                                        .toString()
                                            })
                                }

                                else -> {
                                    Log.d(
                                        "VRCAA",
                                        "Received unknown notificationType: ${notification.type}"
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {}
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
        this.preferences = getSharedPreferences("vrcaa_prefs", 0)

        launch {
            withContext(Dispatchers.Main) {
                api.getAuth().let { token ->
                    if (!token.isNullOrEmpty()) {
                        api.getFriends().let { friends ->
                            if (friends != null) {
                                FriendManager.setFriends(friends)
                            }
                        }

                        api.getFriends(true).let { friends ->
                            if (friends != null) {
                                val onlineFriends = FriendManager.getFriends()
                                for (offline in friends) {
                                    onlineFriends.add(offline)
                                }
                                FriendManager.setFriends(onlineFriends)
                            }
                        }

                        pipeline = PipelineContext(token)
                        pipeline?.connect()
                        pipeline?.setListener(listener)

                        if (preferences.richPresenceEnabled) {
                            gateway = GatewaySocket(preferences.discordToken)
                            gateway?.connect()
                        }
                    }
                }
            }
        }

        val builder = NotificationCompat.Builder(this, NotificationManager.CHANNEL_DEFAULT_ID)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(application.getString(R.string.app_name))
            .setContentText(application.getString(R.string.service_notification))
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                builder.build(),
                FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            // Older versions do not require to specify the `foregroundServiceType`
            startForeground(NOTIFICATION_ID, builder.build())
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        pipeline?.disconnect()
        if (preferences.richPresenceEnabled) {
            gateway?.disconnect()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val NOTIFICATION_ID = 42069
    }
}

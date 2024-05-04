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
import androidx.core.app.NotificationCompat
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.discord.DiscordGateway
import cc.sovellus.vrcaa.api.vrchat.VRChatPipeline
import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendAdd
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendDelete
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendLocation
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendOffline
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendOnline
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendUpdate
import cc.sovellus.vrcaa.api.vrchat.models.websocket.Notification
import cc.sovellus.vrcaa.api.vrchat.models.websocket.UserLocation
import cc.sovellus.vrcaa.extension.discordToken
import cc.sovellus.vrcaa.extension.richPresenceEnabled
import cc.sovellus.vrcaa.extension.richPresenceWebhookUrl
import cc.sovellus.vrcaa.helper.LocationHelper
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.manager.ApiManager
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

    override val coroutineContext = Dispatchers.Main + SupervisorJob()

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    private lateinit var notificationManager: NotificationManager
    private lateinit var preferences: SharedPreferences

    private var pipeline: VRChatPipeline? = null
    private var gateway: DiscordGateway? = null

    private val listener = object : VRChatPipeline.SocketListener {
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
                    val update = msg.obj as FriendOnline

                    if (notificationManager.isOnWhitelist(update.userId) &&
                        notificationManager.isIntentEnabled(
                            update.userId,
                            NotificationManager.Intents.FRIEND_FLAG_ONLINE
                        )
                    ) {
                        notificationManager.pushNotification(
                            title = application.getString(R.string.notification_service_title_online),
                            content = application.getString(R.string.notification_service_description_online)
                                .format(update.user.displayName),
                            channel = NotificationManager.CHANNEL_ONLINE_ID
                        )
                    }

                    FeedManager.addFeed(FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_ONLINE).apply {
                        friendId = update.userId
                        friendName = update.user.displayName
                        friendPictureUrl = update.user.userIcon.ifEmpty { update.user.currentAvatarThumbnailImageUrl }
                    })

                    FriendManager.updateLocation(update.userId, "private", null)
                    FriendManager.updateStatus(update.userId, update.user.status)
                }

                is FriendOffline -> {

                    val update = msg.obj as FriendOffline
                    val friend = FriendManager.getFriend(update.userId)

                    if (friend != null) {
                        if (notificationManager.isOnWhitelist(friend.id) &&
                            notificationManager.isIntentEnabled(
                                friend.id,
                                NotificationManager.Intents.FRIEND_FLAG_OFFLINE
                            )
                        ) {
                            notificationManager.pushNotification(
                                title = application.getString(R.string.notification_service_title_offline),
                                content = application.getString(R.string.notification_service_description_offline)
                                    .format(friend.displayName),
                                channel = NotificationManager.CHANNEL_OFFLINE_ID
                            )
                        }

                        FeedManager.addFeed(
                            FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_OFFLINE).apply {
                                friendId = friend.id
                                friendName = friend.displayName
                                friendPictureUrl =
                                    friend.userIcon.ifEmpty { friend.currentAvatarImageUrl }
                            })

                        FriendManager.updateLocation(friend.id, "offline", null)
                        FriendManager.updateStatus(friend.id, "offline")
                    }
                }

                is FriendLocation -> {
                    val update = msg.obj as FriendLocation

                    // if "friend.travelingToLocation" is not empty, it means friend is currently travelling.
                    // We want to show it only once, so only show when the travelling is done.
                    if (update.travelingToLocation.isEmpty()) {
                        if (notificationManager.isOnWhitelist(update.userId) &&
                            notificationManager.isIntentEnabled(
                                update.userId,
                                NotificationManager.Intents.FRIEND_FLAG_LOCATION
                            )
                        ) {
                            notificationManager.pushNotification(
                                title = application.getString(R.string.notification_service_title_location),
                                content = application.getString(R.string.notification_service_description_location)
                                    .format(update.user.displayName, update.world.name),
                                channel = NotificationManager.CHANNEL_LOCATION_ID
                            )
                        }

                        launch {
                            FeedManager.addFeed(
                                FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_LOCATION)
                                    .apply {
                                        friendId = update.userId
                                        friendName = update.user.displayName
                                        travelDestination = LocationHelper.getReadableLocation(update.location, update.world.name)
                                        friendPictureUrl = update.user.userIcon.ifEmpty { update.user.currentAvatarImageUrl }
                                    }
                            )
                        }
                        FriendManager.updateLocation(update.userId, update.location, update.world.name)
                    }
                }

                is FriendUpdate -> {
                    val update = msg.obj as FriendUpdate
                    val friend = FriendManager.getFriend(update.userId)

                    if (friend != null) {
                        if (
                            StatusHelper.getStatusFromString(update.user.status) !=
                            StatusHelper.getStatusFromString(friend.status)
                        ) {
                            if (notificationManager.isOnWhitelist(update.userId) &&
                                notificationManager.isIntentEnabled(
                                    update.userId,
                                    NotificationManager.Intents.FRIEND_FLAG_STATUS
                                )
                            ) {
                                notificationManager.pushNotification(
                                    title = application.getString(R.string.notification_service_title_status),
                                    content = application.getString(R.string.notification_service_description_status)
                                        .format(
                                            update.user.displayName,
                                            StatusHelper.getStatusFromString(friend.status)
                                                .toString(),
                                            StatusHelper.getStatusFromString(update.user.status)
                                                .toString()
                                        ),
                                    channel = NotificationManager.CHANNEL_LOCATION_ID
                                )
                            }

                            FeedManager.addFeed(
                                FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_STATUS).apply {
                                    friendId = update.userId
                                    friendName = update.user.displayName
                                    friendPictureUrl = update.user.userIcon.ifEmpty { update.user.currentAvatarImageUrl }
                                    friendStatus = StatusHelper.getStatusFromString(update.user.status)
                                }
                            )
                        }
                    }

                    FriendManager.updateFriend(update.user)
                }

                is UserLocation -> {
                    val user = msg.obj as UserLocation

                    val status = StatusHelper.getStatusFromString(user.user.status)
                    val location = LocationHelper.parseLocationInfo(user.location)

                    if (preferences.richPresenceEnabled) {
                        launch {
                            val instance = api.getInstance(user.location)
                            if (status == StatusHelper.Status.Active || status == StatusHelper.Status.JoinMe) {
                                instance?.world?.name?.let { gateway?.sendPresence(it, "${location.instanceType} #${instance.name} ${if (BuildConfig.FLAVOR == "quest") { "(VR)" } else { "(Mobile)" }} (${instance.nUsers} of ${instance.capacity})", instance.world.imageUrl, status) }
                            } else {
                                gateway?.sendPresence(status.toString(), "User location is hidden.", null, status)
                            }
                        }
                    }
                }

                is FriendDelete -> {
                    val update = msg.obj as FriendDelete
                    val friend = FriendManager.getFriend(update.userId)

                    if (friend != null) {
                        FeedManager.addFeed(
                            FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_REMOVED).apply {
                                friendId = update.userId
                                friendName = friend.displayName
                                friendPictureUrl =
                                    friend.userIcon.ifEmpty { friend.currentAvatarImageUrl }

                            }
                        )

                        notificationManager.pushNotification(
                            title = application.getString(R.string.notification_service_title_friend_removed),
                            content = application.getString(R.string.notification_service_description_friend_removed)
                                .format(friend.displayName),
                            channel = NotificationManager.CHANNEL_STATUS_ID
                        )
                    }

                    FriendManager.removeFriend(update.userId)
                }

                is FriendAdd -> {
                    val update = msg.obj as FriendAdd

                    notificationManager.pushNotification(
                        title = application.getString(R.string.notification_service_title_friend_added),
                        content = application.getString(R.string.notification_service_description_friend_added)
                            .format(update.user.displayName),
                        channel = NotificationManager.CHANNEL_STATUS_ID
                    )

                    FeedManager.addFeed(
                        FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_ADDED).apply {
                            friendId = update.userId
                            friendName = update.user.displayName
                            friendPictureUrl =
                                update.user.userIcon.ifEmpty { update.user.currentAvatarImageUrl }
                        })

                    FriendManager.addFriend(update.user)
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
                                            }
                                    )
                                }

                                else -> {}
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }

    override fun onCreate() {

        this.notificationManager = NotificationManager(this)
        this.preferences = getSharedPreferences("vrcaa_prefs", 0)

        HandlerThread("VRCAA_BackgroundWorker", THREAD_PRIORITY_FOREGROUND).apply {
            start()

            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        launch {
            val friends: MutableList<LimitedUser> = ArrayList()

            api.getFriends()?.let { friends += it }
            api.getFriends(true)?.let { friends += it }

            val favorites = api.getFavorites("friend")

            for (friend in friends) {
                friend.location.let { intent ->
                    if (intent.contains("wrld_")) {
                        val info = LocationHelper.parseLocationInfo(friend.location)
                        api.getWorld(info.worldId)?.let { friend.worldName = it.name }
                    } else {
                        friend.worldName = ""
                    }
                }

                favorites?.find { it.favoriteId == friend.id }?.let { friend.isFavorite = true }
            }

            FriendManager.setFriends(friends).also {
                api.getAuth()?.let { token ->
                    pipeline = VRChatPipeline(token)
                    pipeline?.connect()
                    pipeline?.setListener(listener)
                }

                if (preferences.richPresenceEnabled) {
                    gateway = DiscordGateway(preferences.discordToken, preferences.richPresenceWebhookUrl)
                    gateway?.connect()
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

        return START_STICKY
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

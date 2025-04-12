package cc.sovellus.vrcaa.service

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process.THREAD_PRIORITY_FOREGROUND
import androidx.core.app.NotificationCompat
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.discord.DiscordGateway
import cc.sovellus.vrcaa.api.vrchat.pipeline.PipelineSocket
import cc.sovellus.vrcaa.api.vrchat.pipeline.models.FriendActive
import cc.sovellus.vrcaa.api.vrchat.pipeline.models.FriendAdd
import cc.sovellus.vrcaa.api.vrchat.pipeline.models.FriendDelete
import cc.sovellus.vrcaa.api.vrchat.pipeline.models.FriendLocation
import cc.sovellus.vrcaa.api.vrchat.pipeline.models.FriendOffline
import cc.sovellus.vrcaa.api.vrchat.pipeline.models.FriendOnline
import cc.sovellus.vrcaa.api.vrchat.pipeline.models.FriendUpdate
import cc.sovellus.vrcaa.api.vrchat.pipeline.models.Notification
import cc.sovellus.vrcaa.api.vrchat.pipeline.models.UserLocation
import cc.sovellus.vrcaa.api.vrchat.pipeline.models.UserUpdate
import cc.sovellus.vrcaa.extension.discordToken
import cc.sovellus.vrcaa.extension.richPresenceEnabled
import cc.sovellus.vrcaa.extension.richPresenceWebhookUrl
import cc.sovellus.vrcaa.helper.ApiHelper
import cc.sovellus.vrcaa.helper.LocationHelper
import cc.sovellus.vrcaa.helper.NotificationHelper
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.manager.FeedManager
import cc.sovellus.vrcaa.manager.FriendManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class PipelineService : Service(), CoroutineScope {

    override val coroutineContext = Dispatchers.Main + SupervisorJob()

    private lateinit var preferences: SharedPreferences

    private var pipeline: PipelineSocket? = null
    private var gateway: DiscordGateway? = null

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    private var refreshTask: Runnable = Runnable {
        launch {
            CacheManager.buildCache()
        }
    }

    private val listener = object : PipelineSocket.SocketListener {
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

                    val friend = FriendManager.getFriend(update.userId)

                    val feed = FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_ONLINE).apply {
                        friendId = update.userId
                        friendName = update.user.displayName
                        friendPictureUrl = update.user.userIcon.ifEmpty { update.user.profilePicOverride.ifEmpty { update.user.currentAvatarImageUrl } }
                    }

                    if (NotificationHelper.isOnWhitelist(update.userId) &&
                        NotificationHelper.isIntentEnabled(
                            update.userId,
                            NotificationHelper.Intents.FRIEND_FLAG_ONLINE
                        )
                    ) {
                        NotificationHelper.pushNotification(
                            title = application.getString(R.string.notification_service_title_online),
                            content = application.getString(R.string.notification_service_description_online)
                                .format(update.user.displayName),
                            channel = NotificationHelper.CHANNEL_ONLINE_ID
                        )
                    }

                    FeedManager.addFeed(feed)

                    FriendManager.updatePlatform(update.userId, update.platform)
                    FriendManager.updateLocation(update.userId, update.location)
                    FriendManager.updateFriend(update.user)
                }

                is FriendOffline -> {

                    val update = msg.obj as FriendOffline
                    val friend = FriendManager.getFriend(update.userId)

                    if (friend != null) {
                        val feed = FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_OFFLINE).apply {
                            friendId = friend.id
                            friendName = friend.displayName
                            friendPictureUrl = friend.userIcon.ifEmpty { friend.profilePicOverride.ifEmpty { friend.currentAvatarImageUrl } }
                        }

                        if (NotificationHelper.isOnWhitelist(friend.id) &&
                            NotificationHelper.isIntentEnabled(
                                friend.id,
                                NotificationHelper.Intents.FRIEND_FLAG_OFFLINE
                            )
                        ) {
                            NotificationHelper.pushNotification(
                                title = application.getString(R.string.notification_service_title_offline),
                                content = application.getString(R.string.notification_service_description_offline)
                                    .format(friend.displayName),
                                channel = NotificationHelper.CHANNEL_OFFLINE_ID
                            )
                        }

                        FeedManager.addFeed(feed)

                        FriendManager.updateLocation(friend.id, "offline")
                        FriendManager.updateStatus(friend.id, "offline")
                        FriendManager.updatePlatform(friend.id, update.platform)
                    }
                }

                is FriendLocation -> {
                    val update = msg.obj as FriendLocation
                    val friend = FriendManager.getFriend(update.userId)

                    update.world?.let {
                        if (CacheManager.isWorldCached(it.id))
                            CacheManager.updateWorld(update.world)
                        else
                            CacheManager.addWorld(update.world)
                    }

                    // if "friend.travelingToLocation" is not empty, it means friend is currently travelling.
                    // We want to show it only once, so only show when the travelling is done.
                    if (update.travelingToLocation.isEmpty() && update.world != null && friend?.location != update.location)
                    {
                        if (NotificationHelper.isOnWhitelist(update.userId) &&
                            NotificationHelper.isIntentEnabled(
                                update.userId,
                                NotificationHelper.Intents.FRIEND_FLAG_LOCATION
                            )
                        ) {
                            NotificationHelper.pushNotification(
                                title = application.getString(R.string.notification_service_title_location),
                                content = application.getString(R.string.notification_service_description_location)
                                    .format(update.user.displayName, update.world.name),
                                channel = NotificationHelper.CHANNEL_LOCATION_ID
                            )
                        }

                        val feed = FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_LOCATION).apply {
                            friendId = update.userId
                            friendName = update.user.displayName
                            travelDestination = LocationHelper.getReadableLocation(update.location)
                            worldId = update.worldId
                            friendPictureUrl = update.user.userIcon.ifEmpty { update.user.profilePicOverride.ifEmpty { update.user.currentAvatarImageUrl } }
                        }

                        FeedManager.addFeed(feed)
                    }

                    FriendManager.updatePlatform(update.userId, update.platform)
                    FriendManager.updateLocation(update.userId, update.location)
                    FriendManager.updateFriend(update.user)
                }

                is FriendUpdate -> {
                    val update = msg.obj as FriendUpdate
                    val friend = FriendManager.getFriend(update.userId)

                    if (friend != null) {
                        if (
                            StatusHelper.getStatusFromString(update.user.status) !=
                            StatusHelper.getStatusFromString(friend.status)
                        ) {
                            val feed = FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_STATUS).apply {
                                friendId = update.userId
                                friendName = update.user.displayName
                                friendPictureUrl = update.user.userIcon.ifEmpty { update.user.profilePicOverride.ifEmpty { update.user.currentAvatarImageUrl } }
                                friendStatus = StatusHelper.getStatusFromString(update.user.status)
                            }

                            if (NotificationHelper.isOnWhitelist(update.userId) &&
                                NotificationHelper.isIntentEnabled(
                                    update.userId,
                                    NotificationHelper.Intents.FRIEND_FLAG_STATUS
                                )
                            ) {
                                NotificationHelper.pushNotification(
                                    title = application.getString(R.string.notification_service_title_status),
                                    content = application.getString(R.string.notification_service_description_status)
                                        .format(
                                            update.user.displayName,
                                            StatusHelper.getStatusFromString(friend.status)
                                                .toString(),
                                            StatusHelper.getStatusFromString(update.user.status)
                                                .toString()
                                        ),
                                    channel = NotificationHelper.CHANNEL_LOCATION_ID
                                )
                            }

                            FeedManager.addFeed(feed)
                        }

                        // Oh... You don't have VRChat+ I'm sorry to hear that...
                        if (friend.currentAvatarImageUrl != update.user.currentAvatarImageUrl) {
                            launch {
                                val fileId = ApiHelper.extractFileIdFromUrl(update.user.currentAvatarImageUrl)
                                fileId?.let {
                                    api.files.fetchMetadataByFileId(fileId)?.let { metadata ->
                                        var name = metadata.name

                                        name = name.substring(name.indexOf("- ") + 3)
                                        name = name.substring(0, name.indexOf("- ") - 3)

                                        val feed = FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_AVATAR).apply {
                                            friendId = update.userId
                                            friendName = update.user.displayName
                                            friendPictureUrl = update.user.userIcon.ifEmpty { update.user.profilePicOverride.ifEmpty { update.user.currentAvatarImageUrl } }
                                            avatarName = name
                                        }

                                        FeedManager.addFeed(feed)
                                    }
                                }
                            }

                        }
                    }

                    FriendManager.updateFriend(update.user)
                }

                is UserLocation -> {
                    val user = msg.obj as UserLocation

                    if (user.location.contains("wrld_")) {
                        launch {
                            val location = LocationHelper.parseLocationInfo(user.location)
                            val instance = api.instances.fetchInstance(user.location)
                            instance?.let {
                                CacheManager.addWorld(instance.world)
                                if (preferences.richPresenceEnabled) {
                                    val status = StatusHelper.getStatusFromString(user.user.status)
                                    gateway?.sendPresence(instance.world.name, "${location.instanceType} #${instance.name} (${instance.nUsers} of ${instance.capacity})", instance.world.imageUrl, status)
                                }
                            }
                        }
                    }
                }

                is UserUpdate -> {
                    val user = msg.obj as UserUpdate

                    if (preferences.richPresenceEnabled) {
                        val status = StatusHelper.getStatusFromString(user.user.status)
                        launch {
                            gateway?.sendPresence(null, null, null, status)
                        }
                    }

                    CacheManager.updateProfile(user.user)
                }

                is FriendDelete -> {
                    val update = msg.obj as FriendDelete
                    val friend = FriendManager.getFriend(update.userId)

                    if (friend != null) {
                        val feed = FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_REMOVED).apply {
                            friendId = update.userId
                            friendName = friend.displayName
                            friendPictureUrl = friend.userIcon.ifEmpty { friend.profilePicOverride.ifEmpty { friend.currentAvatarImageUrl } }
                        }

                        NotificationHelper.pushNotification(
                            title = application.getString(R.string.notification_service_title_friend_removed),
                            content = application.getString(R.string.notification_service_description_friend_removed)
                                .format(friend.displayName),
                            channel = NotificationHelper.CHANNEL_STATUS_ID
                        )

                        FeedManager.addFeed(feed)
                        FriendManager.removeFriend(update.userId)
                    }
                }

                is FriendAdd -> {
                    val update = msg.obj as FriendAdd

                    val feed = FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_ADDED).apply {
                        friendId = update.userId
                        friendName = update.user.displayName
                        friendPictureUrl = update.user.userIcon.ifEmpty { update.user.profilePicOverride.ifEmpty { update.user.currentAvatarImageUrl } }
                    }

                    NotificationHelper.pushNotification(
                        title = application.getString(R.string.notification_service_title_friend_added),
                        content = application.getString(R.string.notification_service_description_friend_added)
                            .format(update.user.displayName),
                        channel = NotificationHelper.CHANNEL_STATUS_ID
                    )

                    FeedManager.addFeed(feed)
                    FriendManager.addFriend(update.user)
                }

                is FriendActive -> {
                    val update = msg.obj as FriendActive

                    val feed = FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_ONLINE).apply {
                        friendId = update.userId
                        friendName = update.user.displayName
                        friendPictureUrl = update.user.userIcon.ifEmpty { update.user.profilePicOverride.ifEmpty { update.user.currentAvatarImageUrl } }
                    }

                    if (NotificationHelper.isOnWhitelist(update.userId) &&
                        NotificationHelper.isIntentEnabled(
                            update.userId,
                            NotificationHelper.Intents.FRIEND_FLAG_ONLINE
                        )
                    ) {
                        NotificationHelper.pushNotification(
                            title = application.getString(R.string.notification_service_title_online),
                            content = application.getString(R.string.notification_service_description_online)
                                .format(update.user.displayName),
                            channel = NotificationHelper.CHANNEL_ONLINE_ID
                        )
                    }

                    FeedManager.addFeed(feed)

                    FriendManager.updatePlatform(update.userId, update.platform)
                    FriendManager.updateFriend(update.user)
                }

                is Notification -> {
                    val notification = msg.obj as Notification

                    launch {
                        withContext(Dispatchers.Main) {
                            val sender = api.users.fetchUserByUserId(notification.senderUserId)

                            when (notification.type) {
                                "friendRequest" -> {
                                    val feed = FeedManager.Feed(FeedManager.FeedType.FRIEND_FEED_FRIEND_REQUEST).apply {
                                        friendId = notification.senderUserId
                                        friendName = notification.senderUsername
                                        friendPictureUrl = sender?.let { it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl } }.toString()
                                    }

                                    FeedManager.addFeed(feed)
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

        this.preferences = getSharedPreferences("vrcaa_prefs", 0)

        launch {
            api.auth.fetchToken()?.let { token ->
                pipeline = PipelineSocket(token)
                pipeline?.let { pipeline ->
                    pipeline.setListener(listener)
                    pipeline.connect()
                }
            }

            if (preferences.richPresenceEnabled) {
                gateway = DiscordGateway(preferences.discordToken, preferences.richPresenceWebhookUrl)
                gateway?.connect()
            }
        }

        HandlerThread("VRCAA_BackgroundWorker", THREAD_PRIORITY_FOREGROUND).apply {
            start()

            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val builder = NotificationCompat.Builder(this, NotificationHelper.CHANNEL_DEFAULT_ID)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(application.getString(R.string.app_name))
            .setContentText(application.getString(R.string.service_notification))
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                builder.build(),
                FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            // Older versions do not require to specify the `foregroundServiceType`
            startForeground(NOTIFICATION_ID, builder.build())
        }

        val skipCacheInit = intent?.extras?.getBoolean("SKIP_INIT_CACHE") ?: false

        var initialRefresh = INITIAL_INTERVAL
        if (skipCacheInit)
            initialRefresh = RESTART_INTERVAL

        scheduler.scheduleWithFixedDelay(refreshTask, initialRefresh, RESTART_INTERVAL, TimeUnit.MILLISECONDS)

        return START_STICKY_COMPATIBILITY
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
        private const val NOTIFICATION_ID: Int = 42069
        private const val INITIAL_INTERVAL: Long = 1000
        private const val RESTART_INTERVAL: Long = 1800000
    }
}

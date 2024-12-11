package cc.sovellus.vrcaa.api.vrchat.pipeline

import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendActive
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendAdd
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendDelete
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendLocation
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendOffline
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendOnline
import cc.sovellus.vrcaa.api.vrchat.models.websocket.FriendUpdate
import cc.sovellus.vrcaa.api.vrchat.models.websocket.Notification
import cc.sovellus.vrcaa.api.vrchat.models.websocket.NotificationV2
import cc.sovellus.vrcaa.api.vrchat.models.websocket.UpdateModel
import cc.sovellus.vrcaa.api.vrchat.models.websocket.UserLocation
import cc.sovellus.vrcaa.api.vrchat.models.websocket.UserUpdate
import cc.sovellus.vrcaa.api.vrchat.Config
import cc.sovellus.vrcaa.manager.DebugManager
import com.google.gson.Gson
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class PipelineSocket(
    private val token: String
) {
    private val client: OkHttpClient by lazy { OkHttpClient() }
    private lateinit var socket: WebSocket
    private var shouldReconnect: Boolean = false

    interface SocketListener {
        fun onMessage(message: Any?)
    }

    private var socketListener: SocketListener? = null

    private val listener by lazy {
        object : WebSocketListener() {
            override fun onOpen(
                webSocket: WebSocket, response: Response
            ) {
                shouldReconnect = true
            }

            override fun onMessage(
                webSocket: WebSocket, text: String
            ) {
                val update = Gson().fromJson(text, UpdateModel::class.java)
                var isUnknown = false

                when (update.type) {
                    "friend-location" -> {
                        val location = Gson().fromJson(update.content, FriendLocation::class.java)
                        socketListener?.onMessage(location)
                    }

                    "friend-active" -> {
                        val friend = Gson().fromJson(update.content, FriendActive::class.java)
                        socketListener?.onMessage(friend)
                    }

                    "friend-online" -> {
                        val friend = Gson().fromJson(update.content, FriendOnline::class.java)
                        socketListener?.onMessage(friend)
                    }

                    "friend-offline" -> {
                        val friend = Gson().fromJson(update.content, FriendOffline::class.java)
                        socketListener?.onMessage(friend)
                    }

                    "friend-delete" -> {
                        val friend = Gson().fromJson(update.content, FriendDelete::class.java)
                        socketListener?.onMessage(friend)
                    }

                    "friend-add" -> {
                        val friend = Gson().fromJson(update.content, FriendAdd::class.java)
                        socketListener?.onMessage(friend)
                    }

                    "friend-update" -> {
                        val friend = Gson().fromJson(update.content, FriendUpdate::class.java)
                        socketListener?.onMessage(friend)
                    }

                    "user-location" -> {
                        val location = Gson().fromJson(update.content, UserLocation::class.java)
                        socketListener?.onMessage(location)
                    }

                    "user-update" -> {
                        val location = Gson().fromJson(update.content, UserUpdate::class.java)
                        socketListener?.onMessage(location)
                    }

                    "notification" -> {
                        val notification = Gson().fromJson(update.content, Notification::class.java)
                        socketListener?.onMessage(notification)
                    }

                    "notification-v2" -> {
                        val notification =
                            Gson().fromJson(update.content, NotificationV2::class.java)
                        socketListener?.onMessage(notification)
                    }

                    else -> {
                        isUnknown = true
                    }
                }

                if (App.isNetworkLoggingEnabled()) {
                    DebugManager.addDebugMetadata(
                        DebugManager.DebugMetadataData(
                            type = DebugManager.DebugType.DEBUG_TYPE_PIPELINE,
                            name = update.type,
                            unknown = isUnknown,
                            payload = update.content
                        )
                    )
                }
            }

            override fun onClosing(
                webSocket: WebSocket, code: Int, reason: String
            ) {
                webSocket.close(1000, null)
                shouldReconnect = false
            }

            override fun onFailure(
                webSocket: WebSocket, t: Throwable, response: Response?
            ) {
                when (response?.code) {
                    401 -> {
                        shouldReconnect = false
                    }
                }

                if (shouldReconnect)
                {
                    webSocket.close(1000, null)
                    Thread.sleep(RECONNECTION_INTERVAL)
                    connect()
                }
            }
        }
    }

    fun connect() {

        shouldReconnect = false

        val headers = Headers.Builder()
            .add("User-Agent", Config.API_USER_AGENT)

        val request = Request.Builder()
            .url(url = "${Config.PIPELINE_BASE_URL}/?auth=${token}")
            .headers(headers = headers.build())
            .build()

        socket = client.newWebSocket(request, listener)
    }

    fun disconnect() {
        socket.close(1000, null)
    }

    fun setListener(listener: SocketListener) {
        socketListener = listener
    }

    companion object {
        private const val RECONNECTION_INTERVAL: Long = 30000 // 30s
    }
}
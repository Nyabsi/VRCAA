package cc.sovellus.vrcaa.api.discord.websocket

import android.util.ArrayMap
import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

data class IdentifyPayload(
    @SerializedName("heartbeat_interval")
    val heartbeatInterval: Long,
    @SerializedName("trace")
    val trace: List<String>?
)

// This code is referenced from https://github.com/khanhduytran0/MRPC/blob/main/app/src/main/java/com/kdt/mrpc/DiscordSocketClient.java
class GatewaySocket(
    private val token: String
) {
    private lateinit var socket: WebSocket
    private val client: OkHttpClient by lazy { OkHttpClient() }
    private var shouldReconnect: Boolean = false

    private var sequence: Int = 0
    private var interval: Long = 0
    private val since: Long = System.currentTimeMillis() / 1000

    private val heartbeatWorker: Runnable = Runnable {
        Thread.sleep(interval)
        val heartbeatPayload = ArrayMap<String, Any>()
        heartbeatPayload["op"] = 1
        heartbeatPayload["d"] = (if (sequence == 0) "null" else sequence.toString())

        socket.send(Gson().toJson(heartbeatPayload))
    }

    private lateinit var heartbeatThread: Thread

    private val listener by lazy {
        object : WebSocketListener() {
            override fun onOpen(
                webSocket: WebSocket, response: Response
            ) {
                Log.d("VRCAA", "Connected to the Discord gateway.")
                shouldReconnect = true
            }

            override fun onMessage(
                webSocket: WebSocket, text: String
            ) {

                val payload: ArrayMap<String, Any> = Gson().fromJson(
                    text, object : TypeToken<ArrayMap<String?, Any?>?>() {}.type
                )

                if (payload["s"] != null) {
                    sequence = (payload["s"] as Double).toInt()
                }

                when((payload["op"] as Double).toInt()) {
                    0 -> handleDispatch(payload)
                    1 -> handleHeartbeat(payload)
                    10 -> handleIdentity(payload)
                    11 -> handleHeartbeatAck(payload)
                }
            }

            override fun onClosing(
                webSocket: WebSocket, code: Int, reason: String
            ) {
                shouldReconnect = false
            }

            override fun onClosed(
                webSocket: WebSocket, code: Int, reason: String
            ) {
                Log.d("VRCAA", "complain is $code and $reason")
                connect()
            }

            override fun onFailure(
                webSocket: WebSocket, t: Throwable, response: Response?
            ) {
                Log.d("VRCAA", "response error is ${t.message}")
            }
        }
    }

    // TODO: update gateway version to newer, although it does require adding some more packets.
    private val gatewayUrl: String = "wss://gateway.discord.gg/?encoding=json&v=9"

    fun connect() {
        val request = Request.Builder()
            .url(url = gatewayUrl)
            .build()

        socket = client.newWebSocket(request, listener)
    }

    fun disconnect() {

        val presence = ArrayMap<String, Any?>()
        presence["afk"] = true
        presence["status"] = "idle"

        val presencePayload = ArrayMap<String, Any>()
        presencePayload["op"] = 3
        presencePayload["d"] = presence

        socket.send(Gson().toJson(presencePayload))

        shouldReconnect = false
        socket.close(1000, "Closed by user")
        client.dispatcher.executorService.shutdown()
    }

    private fun handleDispatch(payload: ArrayMap<String, Any>) {
        when (payload["t"] as String) {
            "READY" -> {
                Log.d("VRCAA", "Discord Gateway was authenticated without errors.")
            }
        }
    }

    fun sendPresence(playerStatus: String, worldName: String) {

        val assets = ArrayMap<String, String>()
        assets["large_image"] = "../../app-assets/1219592758914977913/1219760377718636665.png#"
        assets["large_text"] = "Powered by VRCAA"

        val timestamps = ArrayMap<String, Any>()
        timestamps["start"] = System.currentTimeMillis()

        val activity = ArrayMap<String, Any>()
        activity["name"] = "VRChat"
        activity["state"] = worldName
        activity["details"] = playerStatus
        activity["type"] = 0
        activity["timestamps"] = timestamps
        activity["assets"] = assets
        activity["application_id"] = "1219592758914977913"

        val presence = ArrayMap<String, Any?>()
        presence["activities"] = arrayOf<Any>(activity)
        presence["afk"] = true
        presence["since"] = 0
        presence["status"] = "idle"

        val presencePayload = ArrayMap<String, Any>()
        presencePayload["op"] = 3
        presencePayload["d"] = presence

        socket.send(Gson().toJson(presencePayload))
    }

    private fun handleIdentity(payload: ArrayMap<String, Any>) {

        val deviceProperties = ArrayMap<String, Any>()
        deviceProperties["\$os"] = "linux"
        deviceProperties["\$browser"] = "Discord Android"
        deviceProperties["\$device"] = "unknown"

        val data = ArrayMap<String, Any>()
        data["token"] = token
        data["properties"] = deviceProperties
        data["compress"] = false
        data["intents"] = 0

        val identityPayload = ArrayMap<String, Any>()
        identityPayload["op"] = 2
        identityPayload["d"] = data

        socket.send(Gson().toJson(identityPayload))

        val payloadData: IdentifyPayload = Gson().fromJson(Gson().toJsonTree(payload["d"]), IdentifyPayload::class.java)
        interval = payloadData.heartbeatInterval

        heartbeatThread = Thread(heartbeatWorker)
        heartbeatThread.start()
    }

    private fun handleHeartbeat(payload: ArrayMap<String, Any>) {
        heartbeatThread.interrupt() // this will not have any effect unless it is already interrupted.

        val heartbeatPayload = ArrayMap<String, Any>()
        heartbeatPayload["op"] = 1
        heartbeatPayload["d"] = (if (sequence == 0) "null" else sequence.toString())

        socket.send(Gson().toJson(heartbeatPayload))
    }

    private fun handleHeartbeatAck(payload: ArrayMap<String, Any>) {
        heartbeatThread.interrupt()
        // restart socket after we've handled the heartbeat.
        heartbeatThread = Thread(heartbeatWorker)
        heartbeatThread.start()
    }
}
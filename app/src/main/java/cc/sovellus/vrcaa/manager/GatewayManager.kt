package cc.sovellus.vrcaa.manager

object GatewayManager {

    private var gatewayListener: GatewayListener? = null

    interface GatewayListener {
        suspend fun onUpdateWorld(name: String, metadata: String, imageUrl: String, status: String)
        suspend fun onUpdateStatus(status: String)
    }

    fun setListener(listener: GatewayListener) {
        gatewayListener = listener
    }

    suspend fun updateWorld(name: String, metadata: String, imageUrl: String, status: String) {
        gatewayListener?.onUpdateWorld(name, metadata, imageUrl, status)
    }

    suspend fun updateStatus(status: String) {
        gatewayListener?.onUpdateStatus(status)
    }
}
package cc.sovellus.vrcaa.manager

object GatewayManager {

    private var gatewayListener: GatewayListener? = null

    interface GatewayListener {
        suspend fun onUpdateWorld(name: String, metadata: String, imageUrl: String, status: String, id: String)
        suspend fun onUpdateStatus(status: String)
    }

    fun setListener(listener: GatewayListener) {
        gatewayListener = listener
    }

    suspend fun updateWorld(name: String, metadata: String, imageUrl: String, status: String, id: String) {
        gatewayListener?.onUpdateWorld(name, metadata, imageUrl, status, id)
    }

    suspend fun updateStatus(status: String) {
        gatewayListener?.onUpdateStatus(status)
    }
}
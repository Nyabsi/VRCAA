package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.base.BaseManager

object GatewayManager : BaseManager<GatewayManager.GatewayListener>() {

    interface GatewayListener {
        suspend fun onUpdateWorld(name: String, metadata: String, imageUrl: String, status: String, id: String)
        suspend fun onUpdateStatus(status: String)
    }

    suspend fun updateWorld(name: String, metadata: String, imageUrl: String, status: String, id: String) {
        getListeners().forEach { listener ->
            listener.onUpdateWorld(name, metadata, imageUrl, status, id)
        }
    }

    suspend fun updateStatus(status: String) {
        getListeners().forEach { listener ->
            listener.onUpdateStatus(status)
        }
    }
}
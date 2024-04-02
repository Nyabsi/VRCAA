package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.vrchat.http.VRChatApi

object ApiManager {

    @Volatile lateinit var api: VRChatApi

    @Synchronized
    fun set(api: VRChatApi) {
        synchronized(this) {
            this.api = api
        }
    }
}
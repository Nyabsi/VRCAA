package cc.sovellus.vrcaa.manager

import android.annotation.SuppressLint
import cc.sovellus.vrcaa.api.vrchat.VRChatApi

object ApiManager {

    @Volatile lateinit var api: VRChatApi

    @Synchronized
    fun set(api: VRChatApi) {
        synchronized(this) {
            this.api = api
        }
    }
}
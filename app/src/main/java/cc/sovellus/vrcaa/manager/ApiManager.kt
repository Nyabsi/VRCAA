package cc.sovellus.vrcaa.manager

import android.annotation.SuppressLint
import cc.sovellus.vrcaa.api.vrchat.VRChatApi

object ApiManager {

    @SuppressLint("StaticFieldLeak")
    @Volatile var api: VRChatApi? = null

    @Synchronized
    fun set(api: VRChatApi) {
        synchronized(this) {
            this.api = api
        }
    }
}
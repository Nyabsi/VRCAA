package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.vrchat.VRChatApi

object ApiManager {
    val api by lazy { VRChatApi() }
}
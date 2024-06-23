package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.vrchat.VRChatApi
import cc.sovellus.vrcaa.api.vrchat.VRChatCache

object ApiManager {
    val api by lazy { VRChatApi() }
    val cache by lazy { VRChatCache() }
}
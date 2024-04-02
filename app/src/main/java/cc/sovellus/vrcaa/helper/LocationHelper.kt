package cc.sovellus.vrcaa.helper

import cc.sovellus.vrcaa.api.http.models.Instance
import cc.sovellus.vrcaa.manager.ApiManager.api
import extensions.wu.seal.PropertySuffixSupport.append
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object LocationHelper: CoroutineScope {

    override val coroutineContext = Dispatchers.Main + SupervisorJob()

    data class LocationInfo(
        var worldId: String = "",
        var hiddenId: String = "",
        var privateId: String = "",
        var friendId: String = "",
        var canRequestInvite: Boolean = false,
        var regionId: String = "",
        var groupId: String = "",
        var groupAccessType: String = "",
        var strict: String = "",
        var instanceType: String = "Public"
    )

    // Reference from https://github.com/vrcx-team/VRCX/blob/master/html/src/app.js#L699-L804
    fun parseLocationInfo(intent: String): LocationInfo {

        val result = LocationInfo()

        val intents = intent.split('~')
        result.worldId = intents[0].split(':')[0]

        for (i in intents) {

            val begin = i.indexOf('(')
            val end = if (begin >= 0) {
                i.indexOf(')')
            } else {
                -1
            }

            val key = if (end >= 0) {
                i.substring(0, begin)
            } else {
                ""
            }
            val value = if (begin < end) {
                i.substring(begin + 1, end)
            } else {
                ""
            }

            if (key.isNotEmpty() && value.isNotEmpty()) {
                when (key) {
                    "hidden" -> result.hiddenId = value
                    "private" -> result.privateId = value
                    "friends" -> result.friendId = value
                    "canRequestInvite" -> result.canRequestInvite = value.toBoolean()
                    "region" -> result.regionId = value
                    "group" -> result.groupId = value
                    "groupAccessType" -> result.groupAccessType = value
                    "strict" -> result.strict = value
                }

                if (result.privateId.isNotEmpty()) {
                    if (result.canRequestInvite) {
                        result.instanceType = "Invite+"
                    } else {
                        result.instanceType = "Invite"
                    }
                } else if (result.friendId.isNotEmpty()) {
                    result.instanceType = "Friends"
                } else if (result.hiddenId.isNotEmpty()) {
                    result.instanceType = "Friends+"
                } else if (result.groupId.isNotEmpty()) {
                    result.instanceType = "Group"
                    if (result.groupAccessType.isNotEmpty()) {
                        if (result.groupAccessType == "public") {
                            result.groupAccessType = "Public"
                        } else {
                            result.groupAccessType = "Plus"
                        }
                    }
                }
            }
        }

        return result
    }

    suspend fun getReadableLocation(location: String): String {
        val info = parseLocationInfo(location)

        val instance: Instance? = api.getInstance(location)

        instance?.let {
            val result = "${it.world.name} (${it.name}) ${info.instanceType} "
            if (info.regionId.isNotEmpty()) location.append(info.regionId)
            return result
        }

        return ""
    }
}
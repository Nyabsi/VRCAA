package cc.sovellus.vrcaa.helper

import cc.sovellus.vrcaa.manager.CacheManager
import extensions.wu.seal.PropertySuffixSupport.append

object LocationHelper {

    data class LocationInfo(
        var worldId: String = "",
        var instanceId: String = "",
        var hiddenId: String = "",
        var privateId: String = "",
        var friendId: String = "",
        var canRequestInvite: Boolean = false,
        var regionId: String = "",
        var groupId: String = "",
        var groupAccessType: String = "",
        var strict: String = "",
        var instanceType: String = "Public",
        var ageGated: Boolean = false
    )

    // Reference from https://github.com/vrcx-team/VRCX/blob/master/html/src/app.js#L699-L804
    fun parseLocationInfo(intent: String): LocationInfo {
        val result = LocationInfo()

        val intents = intent.split('~')
        val info = intents[0].split(':')
        if (info.size == 1) {
            if (info[0].contains("wrld_")) {
                result.worldId = info[0]
            } else {
                result.instanceId = info[0]
            }
        }
        else {
            result.worldId = info[0]
            result.instanceId = info[1]
        }

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
                    "ageGate" -> result.ageGated = value.toBoolean()
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
                        if (result.groupAccessType == "plus") {
                            result.groupAccessType = "Plus"
                            result.instanceType = "Group+"
                        } else {
                            result.groupAccessType = "Public"
                            result.instanceType = "Group"
                        }
                    }
                }
            }
        }

        return result
    }

    fun getReadableLocation(location: String): String {

        if (!location.contains("wrld_"))
            return location

        val info = parseLocationInfo(location)

        val result = "${CacheManager.getWorld(info.worldId).name} #${info.instanceId} ${info.instanceType} "
        if (info.regionId.isNotEmpty()) location.append(info.regionId)
        return result
    }
}

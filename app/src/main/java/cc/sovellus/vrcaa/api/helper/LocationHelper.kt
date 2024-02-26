package cc.sovellus.vrcaa.api.helper

class LocationHelper {

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

    companion object {
        // Reference from https://github.com/vrcx-team/VRCX/blob/master/html/src/app.js#L699-L804
        fun parseLocationIntent(intent: String): LocationInfo {

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
    }
}
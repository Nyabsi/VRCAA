package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import cc.sovellus.vrcaa.api.vrchat.http.models.GroupInstance
import cc.sovellus.vrcaa.api.vrchat.http.models.Instance

interface IInstances {

    // Intent = <worldId>:<InstanceId>:<Nonce>

    enum class InstanceType {
        Public,
        FriendsPlus,
        Friends,
        InvitePlus,
        Invite;

        override fun toString(): String {
            return when (this) {
                Public -> "public"
                FriendsPlus -> "hidden"
                Friends -> "friends"
                InvitePlus -> "private"
                Invite -> "private"
            }
        }
    }

    enum class InstanceRegion {
        Europe,
        Japan,
        AmericaDefault,
        AmericaEast,
        AmericaSouth;

        override fun toString(): String {
            return when (this) {
                Europe -> "eu"
                Japan -> "jp"
                AmericaDefault -> "us"
                AmericaEast -> "use"
                AmericaSouth -> "usw"
            }
        }
    }

    suspend fun fetchInstance(intent: String): Instance?
    suspend fun selfInvite(intent: String): Boolean
    suspend fun fetchGroupInstancesById(groupId: String): ArrayList<GroupInstance>
    suspend fun createInstance(worldId: String, type: InstanceType, region: InstanceRegion, ownerId: String?, canRequestInvite: Boolean): Instance?
}
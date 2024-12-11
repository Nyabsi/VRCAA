package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import cc.sovellus.vrcaa.api.vrchat.http.models.GroupInstance
import cc.sovellus.vrcaa.api.vrchat.http.models.Instance

interface IInstances {

    // Intent = <worldId>:<InstanceId>:<Nonce>

    suspend fun fetchInstance(intent: String): Instance?
    suspend fun selfInvite(intent: String): Boolean
    suspend fun fetchGroupInstancesById(groupId: String): ArrayList<GroupInstance>
}
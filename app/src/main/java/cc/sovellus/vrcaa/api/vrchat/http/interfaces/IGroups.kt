package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import cc.sovellus.vrcaa.api.vrchat.http.models.Group

interface IGroups {

    suspend fun fetchGroupsByName(query: String, n: Int = 50, offset: Int = 0, groups: ArrayList<Group> = arrayListOf()): ArrayList<Group>
    suspend fun fetchGroupByGroupId(groupId: String): Group?
    suspend fun joinGroupByGroupId(groupId: String): Boolean
    suspend fun leaveGroupByGroupId(groupId: String): Boolean
    suspend fun withdrawRequestByGroupId(groupId: String): Boolean
}
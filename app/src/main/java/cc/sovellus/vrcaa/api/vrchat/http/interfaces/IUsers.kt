package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import cc.sovellus.vrcaa.api.vrchat.http.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.http.models.UserGroup

interface IUsers {

    suspend fun fetchUserByUserId(userId: String): LimitedUser?
    suspend fun fetchUsersByName(query: String, n: Int = 50, offset: Int = 0, users: ArrayList<LimitedUser> = arrayListOf()): ArrayList<LimitedUser>
    suspend fun fetchGroupsByUserId(userId: String): ArrayList<UserGroup>
}
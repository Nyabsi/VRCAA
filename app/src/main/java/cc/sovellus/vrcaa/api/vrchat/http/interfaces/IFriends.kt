package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import cc.sovellus.vrcaa.api.vrchat.http.models.Friend

interface IFriends {

    suspend fun fetchFriends(offline: Boolean, n: Int = 50, offset: Int = 0, friends: ArrayList<Friend> = arrayListOf()): ArrayList<Friend>
}
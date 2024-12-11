package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import cc.sovellus.vrcaa.api.vrchat.http.models.World

interface IWorlds {

    suspend fun fetchRecent(): ArrayList<World>
    suspend fun fetchWorldsByName(query: String, sort: String, n: Int = 50, offset: Int = 0, worlds: ArrayList<World> = arrayListOf()): ArrayList<World>
    suspend fun fetchWorldsByAuthorId(userId: String, private: Boolean, n: Int = 50, offset: Int = 0, worlds: ArrayList<World> = arrayListOf()): ArrayList<World>
    suspend fun fetchWorldByWorldId(worldId: String): World?
}
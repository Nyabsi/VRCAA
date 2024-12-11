package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import cc.sovellus.vrcaa.api.vrchat.http.models.Avatar

interface IAvatars {
    suspend fun selectAvatarById(avatarId: String): Boolean
    suspend fun fetchAvatarById(avatarId: String): Avatar?
}
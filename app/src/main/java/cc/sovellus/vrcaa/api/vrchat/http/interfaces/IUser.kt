package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import cc.sovellus.vrcaa.api.vrchat.http.models.Avatar
import cc.sovellus.vrcaa.api.vrchat.http.models.User

interface IUser {

    suspend fun updateProfileByUserId(userId: String, newStatus: String, newDescription: String, newBio: String, newBioLinks: List<String>, newAgeVerificationStatus: String): User?
    suspend fun fetchOwnedAvatars(n: Int = 50, offset: Int = 0, avatars: ArrayList<Avatar> = arrayListOf()): ArrayList<Avatar>
}
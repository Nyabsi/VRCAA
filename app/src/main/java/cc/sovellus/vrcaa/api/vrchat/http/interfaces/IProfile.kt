package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import cc.sovellus.vrcaa.api.vrchat.http.models.Profile
import cc.sovellus.vrcaa.api.vrchat.http.models.User

interface IProfile {
    suspend fun fetchProfile(userId: String, asSelf: Boolean, withGroupsAndWorlds: Boolean): Profile?
    suspend fun updateProfile(userId: String, newBio: String, newBioLinks: List<String>): Profile?
}
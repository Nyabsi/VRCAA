package cc.sovellus.vrcaa.ui.screen.profile

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.search.justhparty.JustHPartyProvider
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites
import cc.sovellus.vrcaa.api.vrchat.http.models.Instance
import cc.sovellus.vrcaa.api.vrchat.http.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.http.models.UserGroup
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.FavoriteManager
import kotlinx.coroutines.launch

class UserProfileScreenModel(
    private val userId: String
) : StateScreenModel<UserProfileScreenModel.UserProfileState>(UserProfileState.Init) {

    sealed class UserProfileState {
        data object Init : UserProfileState()
        data object Loading : UserProfileState()
        data class Result(val profile: LimitedUser?, val instance: Instance?, val worlds: ArrayList<World>, val groups: ArrayList<UserGroup>) : UserProfileState()
    }

    private val avatarProvider = JustHPartyProvider()

    private var profile: LimitedUser? = null
    private var instance: Instance? = null
    private lateinit var worlds: ArrayList<World>
    private lateinit var groups: ArrayList<UserGroup>

    init {
        mutableState.value = UserProfileState.Loading
        fetchProfile()
    }

    private fun fetchProfile() {
        screenModelScope.launch {
            App.setLoadingText(R.string.loading_text_user)
            api.users.fetchUserByUserId(userId)?.let {
                it.location.let { location ->
                    if (it.isFriend &&
                        location.isNotEmpty() &&
                        location != "private" &&
                        location != "traveling" &&
                        location != "offline") {
                        instance = api.instances.fetchInstance(location)
                    }
                }
                profile = it
            }

            groups = api.users.fetchGroupsByUserId(userId)
            worlds = api.worlds.fetchWorldsByAuthorId(userId, false)
            mutableState.value = UserProfileState.Result(profile, instance, worlds, groups)
        }
    }

    fun findAvatar(callback: ((userId: String?) -> Unit?)) {
        screenModelScope.launch {
            profile?.let {
                val fileId = extractFileIdFromUrl(it.currentAvatarImageUrl)

                if (fileId != null) {
                    api.files.fetchMetadataByFileId(fileId)?.let { metadata ->
                        var name = metadata.name

                        name = name.substring(9)
                        name = name.substring(0, name.indexOf('-') - 1)

                        val searchAvatarsByName = avatarProvider.search(name)
                        if (searchAvatarsByName.isNotEmpty()) {
                            for (avatar in searchAvatarsByName) {
                                avatar.imageUrl?.let {
                                    val avatarFileId = extractFileIdFromUrl(avatar.imageUrl)
                                    if (avatarFileId == fileId) {
                                        callback(avatar.id)
                                        return@launch
                                    }
                                }
                            }
                        }

                        // fallback to using author search
                        val searchAvatarsByAuthor = avatarProvider.searchByAuthor(metadata.ownerId)
                        if (searchAvatarsByAuthor.isNotEmpty()) {
                            for (avatar in searchAvatarsByAuthor) {
                                avatar.imageUrl?.let {
                                    val avatarFileId = extractFileIdFromUrl(avatar.imageUrl)
                                    if (avatarFileId == fileId) {
                                        callback(avatar.id)
                                        return@launch
                                    }
                                }
                            }
                        }
                        callback(null)
                    }
                }
            }
        }
    }

    private fun extractFileIdFromUrl(imageUrl: String): String? {
        val startIndex = imageUrl.indexOf("file_")
        val endIndex = imageUrl.indexOf("/", startIndex)
        if (startIndex != -1 && endIndex != -1) {
            val fileId = imageUrl.substring(startIndex, endIndex)
            return fileId
        }
        return null
    }

    fun inviteToFriend(intent: String) {
        screenModelScope.launch {
            api.instances.selfInvite(intent)
        }
    }

    fun removeFavorite(callback: (result: Boolean) -> Unit) {
        screenModelScope.launch {
            profile?.let {
                val result = FavoriteManager.removeFavorite(IFavorites.FavoriteType.FAVORITE_FRIEND, it.id)
                callback(result)
            }
        }
    }
}
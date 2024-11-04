package cc.sovellus.vrcaa.ui.screen.profile

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.api.vrchat.models.User

sealed class ProfileState {
    data object Init : ProfileState()
    data object Loading : ProfileState()
    data class Result(val profile: User) : ProfileState()
}

class ProfileScreenModel : StateScreenModel<ProfileState>(ProfileState.Init) {

    private val cacheListener = object : CacheManager.CacheListener {
        override fun profileUpdated(profile: User) {
            mutableState.value = ProfileState.Loading
            mutableState.value = ProfileState.Result(profile)
        }

        override fun startCacheRefresh() { }

        override fun endCacheRefresh() { }

        override fun recentlyVisitedUpdated(worlds: MutableList<CacheManager.WorldCache>) { }
    }

    init {
        mutableState.value = ProfileState.Loading
        CacheManager.addListener(cacheListener)
        fetchProfile()
    }

    private fun fetchProfile() {
        mutableState.value = ProfileState.Result(CacheManager.getProfile())
    }
}
package cc.sovellus.vrcaa.ui.screen.profile

import cafe.adriel.voyager.core.model.StateScreenModel
import cc.sovellus.vrcaa.api.vrchat.models.User
import cc.sovellus.vrcaa.manager.CacheManager

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

        override fun endCacheRefresh() { fetchProfile() }

        override fun recentlyVisitedUpdated(worlds: MutableList<CacheManager.WorldCache>) { }
    }

    init {
        mutableState.value = ProfileState.Loading
        CacheManager.addListener(cacheListener)

        if (!CacheManager.isRefreshing())
            fetchProfile()
    }

    private fun fetchProfile() {
        val profile = CacheManager.getProfile()
        if (profile == null)
            fetchProfile()
        else
            mutableState.value = ProfileState.Result(profile)
    }
}
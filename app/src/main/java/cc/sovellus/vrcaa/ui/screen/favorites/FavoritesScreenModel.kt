package cc.sovellus.vrcaa.ui.screen.favorites

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import cafe.adriel.voyager.core.model.StateScreenModel
import cc.sovellus.vrcaa.api.vrchat.models.User
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.manager.FavoriteManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FavoritesScreenModel : StateScreenModel<FavoritesScreenModel.FavoriteState>(FavoriteState.Init) {

    sealed class FavoriteState {
        data object Init : FavoriteState()
        data object Loading : FavoriteState()
        data object Result : FavoriteState()
    }

    private var worldListFlow = MutableStateFlow(mutableStateMapOf<String, SnapshotStateList<FavoriteManager.FavoriteMetadata>>())
    var worldList = worldListFlow.asStateFlow()

    private var avatarListFlow = MutableStateFlow(mutableStateMapOf<String, SnapshotStateList<FavoriteManager.FavoriteMetadata>>())
    var avatarList = avatarListFlow.asStateFlow()

    var currentIndex = mutableIntStateOf(0)
    var currentSelectedGroup = mutableStateOf("")
    var editDialogShown = mutableStateOf(false)

    private val cacheListener = object : CacheManager.CacheListener {
        override fun profileUpdated(profile: User) { }

        override fun startCacheRefresh() { }

        override fun endCacheRefresh() {
            fetchContent()
            mutableState.value = FavoriteState.Result
        }

        override fun recentlyVisitedUpdated(worlds: MutableList<CacheManager.WorldCache>) { }
    }

    init {
        mutableState.value = FavoriteState.Loading
        CacheManager.addListener(cacheListener)

        if (!CacheManager.isRefreshing())
        {
            fetchContent()
            mutableState.value = FavoriteState.Result
        }
    }

    private fun fetchContent() {
        worldListFlow.update { FavoriteManager.getWorldList() }
        avatarListFlow.update { FavoriteManager.getAvatarList() }
    }
}
package cc.sovellus.vrcaa.ui.screen.favorites

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.manager.FavoriteManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FavoritesScreenModel : ScreenModel {

    private var worldListFlow = MutableStateFlow(mutableStateMapOf<String, SnapshotStateList<FavoriteManager.FavoriteMetadata>>())
    var worldList = worldListFlow.asStateFlow()

    private var avatarListFlow = MutableStateFlow(mutableStateMapOf<String, SnapshotStateList<FavoriteManager.FavoriteMetadata>>())
    var avatarList = avatarListFlow.asStateFlow()

    var currentIndex = mutableIntStateOf(0)

    init {
        fetchContent()
    }

    private fun fetchContent() {
        worldListFlow.update { FavoriteManager.getWorldList() }
        avatarListFlow.update { FavoriteManager.getAvatarList() }
    }
}
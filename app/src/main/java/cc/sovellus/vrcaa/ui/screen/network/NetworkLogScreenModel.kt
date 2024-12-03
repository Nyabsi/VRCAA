package cc.sovellus.vrcaa.ui.screen.network

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.manager.DebugManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkLogScreenModel : ScreenModel {
    private var metadataStateFlow = MutableStateFlow(mutableStateListOf<DebugManager.DebugMetadataData>())
    var metadata = metadataStateFlow.asStateFlow()

    var currentIndex = mutableIntStateOf(0)

    private val listener = object : DebugManager.DebugListener {
        override fun onUpdateMetadata(metadata: MutableList<DebugManager.DebugMetadataData>) {
            metadataStateFlow.value = metadata.toMutableStateList()
        }
    }

    init {
        DebugManager.setListener(listener)
        val metadata = DebugManager.getMetadata()
        if (metadata.isNotEmpty())
            metadataStateFlow.value = metadata
    }
}
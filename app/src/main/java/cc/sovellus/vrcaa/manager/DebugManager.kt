package cc.sovellus.vrcaa.manager

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import cc.sovellus.vrcaa.base.BaseManager

object DebugManager : BaseManager<DebugManager.DebugListener>() {

    interface DebugListener {
        fun onUpdateMetadata(metadata: MutableList<DebugMetadataData>)
    }

    enum class DebugType {
        DEBUG_TYPE_HTTP,
        DEBUG_TYPE_PIPELINE
    }

    data class DebugMetadataData(
        val type: DebugType,
        val methodType: String = "",
        val url: String = "",
        val code: Int = 0,
        val name: String = "",
        val unknown: Boolean = false,
        val payload: String
    )

    private var metadataList: MutableList<DebugMetadataData> = ArrayList()

    fun addDebugMetadata(metadata: DebugMetadataData) {
        metadataList.add(metadata)
        getListeners().forEach { listener ->
            listener.onUpdateMetadata(metadataList)
        }
    }

    fun getMetadata(): SnapshotStateList<DebugMetadataData> {
        return metadataList.toMutableStateList()
    }
}
package cc.sovellus.vrcaa.manager

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

object DebugManager {

    private var metadataList: MutableList<DebugMetadataData> = ArrayList()
    private var debugListener: DebugListener? = null

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

    fun addDebugMetadata(metadata: DebugMetadataData) {
        metadataList.add(metadata)
        debugListener?.onUpdateMetadata(metadataList)
    }

    fun getMetadata(): SnapshotStateList<DebugMetadataData> {
        return metadataList.toMutableStateList()
    }

    fun setListener(listener: DebugListener) {
        debugListener = listener
    }
}
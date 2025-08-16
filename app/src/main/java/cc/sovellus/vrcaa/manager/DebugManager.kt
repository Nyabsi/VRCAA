/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.base.BaseManager
import cc.sovellus.vrcaa.helper.NotificationHelper

object DebugManager : BaseManager<DebugManager.DebugListener>() {

    interface DebugListener {
        fun onUpdateMetadata(metadata: List<DebugMetadataData>)
    }

    enum class DebugType {
        DEBUG_TYPE_HTTP,
        DEBUG_TYPE_PIPELINE,
        DEBUG_TYPE_GATEWAY
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
        try {
            metadataList.add(metadata)
            val listSnapshot = metadataList.toList()
            getListeners().forEach { listener ->
                listener.onUpdateMetadata(listSnapshot)
            }
        } catch (_: Throwable) {
            metadataList.clear()
            NotificationHelper.pushNotification(
                App.getContext().getString(R.string.debug_notification_title_out_of_memory),
                App.getContext().getString(R.string.debug_notification_content_out_of_memory),
                NotificationHelper.CHANNEL_DEFAULT_ID
            )
        }
    }

    fun getMetadata(): List<DebugMetadataData> {
        val listSnapshot = metadataList.toList()
        return listSnapshot
    }
}
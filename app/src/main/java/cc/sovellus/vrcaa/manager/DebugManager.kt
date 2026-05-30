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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object DebugManager {

    private const val MAX_DEBUG_ENTRIES = 1000

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

    private val metadataStateFlow = MutableStateFlow<List<DebugMetadataData>>(emptyList())
    val metadataState: StateFlow<List<DebugMetadataData>> = metadataStateFlow.asStateFlow()

    fun addDebugMetadata(metadata: DebugMetadataData) {
        try {
            metadataStateFlow.update { current -> listOf(metadata) + current }
        } catch (_: Throwable) {
            metadataStateFlow.value = emptyList()
            NotificationHelper.pushNotification(
                App.getContext().getString(R.string.debug_notification_title_out_of_memory),
                App.getContext().getString(R.string.debug_notification_content_out_of_memory),
                NotificationHelper.CHANNEL_DEFAULT_ID
            )
        }
    }
}
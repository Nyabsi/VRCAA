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

package cc.sovellus.vrcaa.ui.screen.network

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.manager.DebugManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NetworkLogScreenModel : ScreenModel {
    private var metadataStateFlow = MutableStateFlow(mutableStateListOf<DebugManager.DebugMetadataData>())
    var metadata = metadataStateFlow.asStateFlow()

    var currentIndex = mutableIntStateOf(0)

    private val listener = object : DebugManager.DebugListener {
        override fun onUpdateMetadata(metadata: List<DebugManager.DebugMetadataData>) {
            metadataStateFlow.value = metadata.toMutableStateList()
        }
    }

    init {
        DebugManager.addListener(listener)
        metadataStateFlow.update { DebugManager.getMetadata().toMutableStateList() }
    }
}
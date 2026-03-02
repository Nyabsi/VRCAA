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

package cc.sovellus.vrcaa.ui.screen.feed

import cafe.adriel.voyager.core.model.StateScreenModel
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.manager.FeedManager
import kotlinx.coroutines.flow.StateFlow

class FeedScreenModel : StateScreenModel<FeedScreenModel.FeedState>(FeedState.Init) {

    sealed class FeedState {
        data object Init : FeedState()
        data object Loading : FeedState()
        data object Result : FeedState()
    }

    val feed: StateFlow<List<FeedManager.Feed>> = FeedManager.feedState

    private val cacheListener = object : CacheManager.CacheListener {
        override fun startCacheRefresh() {
            mutableState.value = FeedState.Loading
        }

        override fun endCacheRefresh() {
            mutableState.value = FeedState.Result
        }
    }

    init {
        mutableState.value = FeedState.Loading
        CacheManager.addListener(cacheListener)

        if (CacheManager.isBuilt())
        {
            mutableState.value = FeedState.Result
        }
    }

    override fun onDispose() {
        CacheManager.removeListener(cacheListener)
        super.onDispose()
    }
}
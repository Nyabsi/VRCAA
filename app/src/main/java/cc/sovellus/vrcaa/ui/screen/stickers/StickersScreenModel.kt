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

package cc.sovellus.vrcaa.ui.screen.stickers

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IInventory
import cc.sovellus.vrcaa.api.vrchat.http.models.Inventory
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

class StickersScreenModel : StateScreenModel<StickersScreenModel.StickerState>(StickerState.Init) {

    sealed class StickerState {
        data object Init : StickerState()
        data object Loading : StickerState()
        data object Empty : StickerState()
        data class Result(
            val stickers: ArrayList<Inventory.Data>,
            val userStickers: ArrayList<Inventory.Data>,
            val archivedStickers: ArrayList<Inventory.Data>
        ) : StickerState()
    }

    private val context: Context = App.getContext()
    val preferences: SharedPreferences = context.getSharedPreferences(App.PREFERENCES_NAME, MODE_PRIVATE)

    private var stickers: ArrayList<Inventory.Data> = arrayListOf()
    private var userStickers: ArrayList<Inventory.Data> = arrayListOf()
    private var archivedStickers: ArrayList<Inventory.Data> = arrayListOf()

    var currentIndex = mutableIntStateOf(0)
    var previewItem = mutableStateOf<Inventory.Data?>(null)

    init {
        fetchStickers()
    }

    fun fetchStickers() {
        mutableState.value = StickerState.Loading
        App.setLoadingText(R.string.loading_text_stickers)
        screenModelScope.launch {
            stickers = api.inventory.fetchInventory(IInventory.PropType.STICKER, arrayListOf(), arrayListOf(), arrayListOf("ugc"), false)
            userStickers = api.inventory.fetchInventory(IInventory.PropType.STICKER, arrayListOf("Custom Sticker"), arrayListOf("ugc"), arrayListOf(), false)
            archivedStickers = api.inventory.fetchInventory(IInventory.PropType.STICKER, arrayListOf(), arrayListOf(), arrayListOf("ugc"), true)

            if (stickers.isEmpty() && userStickers.isEmpty() && archivedStickers.isEmpty())
                mutableState.value = StickerState.Empty
            else
                mutableState.value = StickerState.Result(stickers, userStickers, archivedStickers)
        }
    }
}
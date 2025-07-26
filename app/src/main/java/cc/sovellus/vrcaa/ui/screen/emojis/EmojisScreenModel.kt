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

package cc.sovellus.vrcaa.ui.screen.emojis

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Inventory
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

class EmojisScreenModel : StateScreenModel<EmojisScreenModel.EmojiState>(EmojiState.Init) {

    sealed class EmojiState {
        data object Init : EmojiState()
        data object Loading : EmojiState()
        data object Empty : EmojiState()
        data class Result(
            val emojis: ArrayList<Inventory.Data>,
            val userEmojis: ArrayList<Inventory.Data>,
            val archivedEmojis: ArrayList<Inventory.Data>
        ) : EmojiState()
    }

    private val context: Context = App.getContext()
    val preferences: SharedPreferences = context.getSharedPreferences(App.PREFERENCES_NAME, MODE_PRIVATE)

    private var emojis: ArrayList<Inventory.Data> = arrayListOf()
    private var userEmojis: ArrayList<Inventory.Data> = arrayListOf()
    private var archivedEmojis: ArrayList<Inventory.Data> = arrayListOf()

    var currentIndex = mutableIntStateOf(0)
    var previewItem = mutableStateOf<Inventory.Data?>(null)
    var currentUri = mutableStateOf<Uri?>(null)

    init {
        fetchEmojis()
    }

    private fun fetchEmojis() {
        mutableState.value = EmojiState.Loading
        App.setLoadingText(R.string.loading_text_emojis)
        screenModelScope.launch {
            emojis = api.inventory.fetchEmojis(false, false)
            userEmojis = api.inventory.fetchEmojis(true, false)
            archivedEmojis = api.inventory.fetchEmojis(false, true)

            if (emojis.isEmpty() && userEmojis.isEmpty() && archivedEmojis.isEmpty())
                mutableState.value = EmojiState.Empty
            else
                mutableState.value = EmojiState.Result(emojis, userEmojis, archivedEmojis)
        }
    }

    fun uploadFile(type: String, uri: Uri) {
        screenModelScope.launch {
            api.files.uploadEmoji(type, uri)?.let {
                fetchEmojis()
            }
        }
    }
}
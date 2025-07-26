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

package cc.sovellus.vrcaa.ui.screen.items

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.compose.runtime.mutableIntStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Inventory
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

class ItemsScreenModel : StateScreenModel<ItemsScreenModel.ItemsState>(ItemsState.Init) {

    sealed class ItemsState {
        data object Init : ItemsState()
        data object Loading : ItemsState()
        data object Empty : ItemsState()
        data class Result(
            val items: ArrayList<Inventory.Data>,
            val archivedItems: ArrayList<Inventory.Data>
        ) : ItemsState()
    }

    private val context: Context = App.getContext()
    val preferences: SharedPreferences = context.getSharedPreferences(App.PREFERENCES_NAME, MODE_PRIVATE)

    private var items: ArrayList<Inventory.Data> = arrayListOf()
    private var archivedItems: ArrayList<Inventory.Data> = arrayListOf()

    var currentIndex = mutableIntStateOf(0)

    init {
        fetchItems()
    }

    fun fetchItems() {
        mutableState.value = ItemsState.Loading
        App.setLoadingText(R.string.loading_text_items)
        screenModelScope.launch {
            items = api.inventory.fetchProps(false, false)
            archivedItems = api.inventory.fetchProps(false, true)

            if (items.isEmpty() && archivedItems.isEmpty())
                mutableState.value = ItemsState.Empty
            else
                mutableState.value = ItemsState.Result(items, archivedItems)
        }
    }
}
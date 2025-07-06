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

package cc.sovellus.vrcaa.ui.screen.prints

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.api.vrchat.http.models.Print
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

class PrintsScreenModel(
    private val userId: String
) : StateScreenModel<PrintsScreenModel.PrintsState>(PrintsState.Init) {

    sealed class PrintsState {
        data object Init : PrintsState()
        data object Loading : PrintsState()
        data object Empty : PrintsState()
        data class Result(
            val prints: ArrayList<Print>
        ) : PrintsState()
    }

    private val context: Context = App.getContext()
    val preferences: SharedPreferences = context.getSharedPreferences(App.PREFERENCES_NAME, MODE_PRIVATE)
    private var prints: ArrayList<Print> = arrayListOf()

    init {
        mutableState.value = PrintsState.Loading
        fetchAvatars()
    }

    private fun fetchAvatars() {
        screenModelScope.launch {
            prints = api.prints.fetchPrintsByUserId(userId)

            if (prints.isEmpty())
                mutableState.value = PrintsState.Empty
            else
                mutableState.value = PrintsState.Result(prints)
        }
    }
}
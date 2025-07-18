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

package cc.sovellus.vrcaa.ui.screen.gallery

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.net.Uri
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.File
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.CacheManager
import kotlinx.coroutines.launch

class IconGalleryScreenModel : StateScreenModel<IconGalleryScreenModel.IconGalleryState>(IconGalleryState.Init) {

    sealed class IconGalleryState {
        data object Init : IconGalleryState()
        data object Loading : IconGalleryState()
        data object Empty : IconGalleryState()
        data class Result(
            val files: ArrayList<File>
        ) : IconGalleryState()
    }

    private val context: Context = App.getContext()
    val preferences: SharedPreferences = context.getSharedPreferences(App.PREFERENCES_NAME, MODE_PRIVATE)
    private var files: ArrayList<File> = arrayListOf()

    init {
        fetchIcons()
    }

    private fun fetchIcons() {
        mutableState.value = IconGalleryState.Loading
        App.setLoadingText(R.string.loading_text_icons)
        screenModelScope.launch {
            files = api.files.fetchFilesByTag("icon")

            if (files.isEmpty())
                mutableState.value = IconGalleryState.Empty
            else
                mutableState.value = IconGalleryState.Result(files)
        }
    }

    fun uploadFile(uri: Uri?) {
        CacheManager.getProfile()?.let { profile ->
            uri?.let {
                screenModelScope.launch {
                    api.files.uploadImage("icon", uri)?.let {
                        fetchIcons()
                    }
                }
            }
        }
    }
}
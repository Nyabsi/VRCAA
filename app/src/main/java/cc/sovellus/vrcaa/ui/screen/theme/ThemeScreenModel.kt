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

package cc.sovellus.vrcaa.ui.screen.theme

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.extension.columnCountOption
import cc.sovellus.vrcaa.extension.currentThemeOption
import cc.sovellus.vrcaa.extension.fixedColumnSize
import cc.sovellus.vrcaa.extension.minimalistMode

class ThemeScreenModel : ScreenModel {
    val preferences: SharedPreferences = App.getContext().getSharedPreferences(App.PREFERENCES_NAME, MODE_PRIVATE)
    val minimalistMode = mutableStateOf(preferences.minimalistMode)
    var currentIndex = mutableIntStateOf(preferences.currentThemeOption)
    var currentColumnIndex = mutableIntStateOf(preferences.columnCountOption)
    var currentColumnAmount = mutableFloatStateOf(preferences.fixedColumnSize.toFloat())
}

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

package cc.sovellus.vrcaa.base

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.extension.currentThemeOption
import cc.sovellus.vrcaa.manager.ThemeManager
import cc.sovellus.vrcaa.ui.theme.LocalTheme
import cc.sovellus.vrcaa.ui.theme.Theme

open class BaseActivity : ComponentActivity() {

    private val currentTheme = mutableIntStateOf(-1)
    val preferences: SharedPreferences = App.getPreferences()

    private val themeListener = object : ThemeManager.ThemeListener {
        override fun onPreferenceUpdate(theme: Int) {
            currentTheme.intValue = theme
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ThemeManager.addListener(themeListener)
        currentTheme.intValue = preferences.currentThemeOption

        setContent {
            CompositionLocalProvider(LocalTheme provides currentTheme.intValue) {
                Theme(LocalTheme.current) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Content(savedInstanceState)
                    }
                }
            }
        }
    }

    @Composable
    open fun Content(bundle: Bundle?) {
        throw RuntimeException("Did you forgot to override Content?")
    }
}
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

package cc.sovellus.vrcaa.ui.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.base.BaseTab
import cc.sovellus.vrcaa.ui.screen.home.HomeScreen

object HomeTab : BaseTab() {

    @Composable
    override fun Content() {
        Navigator(HomeScreen())
    }

    @Composable
    override fun provideOptions(): TabOptions {
        val icon = rememberVectorPainter(Icons.Default.Home)
        val tabName = stringResource(R.string.tabs_label_home)

        return remember {
            TabOptions(
                index = 0u,
                title = tabName,
                icon = icon
            )
        }
    }

    private fun readResolve(): Any = HomeTab
}
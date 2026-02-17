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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.manager.FeedManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FeedSearchScreen : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow

        val input = remember { mutableStateOf("") }
        val filteredFeedStateFlow = remember { MutableStateFlow(listOf<FeedManager.Feed>()) }
        val filteredFeed = filteredFeedStateFlow.asStateFlow()

        Scaffold { padding ->
            SearchBar(
                inputField = {
                    InputField(
                        enabled = true,
                        query = input.value,
                        onQueryChange = {
                            input.value = it
                        },
                        expanded = true,
                        onExpandedChange = { },
                        placeholder = { Text(text = stringResource(id = R.string.feed_search_placeholder)) },
                        leadingIcon = {
                            IconButton(onClick = {
                                navigator.pop()
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                input.value = ""
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null
                                )
                            }
                        },
                        onSearch = {
                            filteredFeedStateFlow.value = FeedManager.getFeed().filter { feed ->
                                feed.friendName.contains(input.value, ignoreCase = true) || (feed.travelDestination.contains(input.value, ignoreCase = true) && feed.type == FeedManager.FeedType.FRIEND_FEED_LOCATION) || (feed.avatarName.contains(input.value, ignoreCase = true) && feed.type == FeedManager.FeedType.FRIEND_FEED_AVATAR)
                            }.toMutableStateList()
                        }
                    )
                },
                modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
                expanded = true,
                onExpandedChange = {},
                tonalElevation = 8.dp,
                shadowElevation = 4.dp
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val feed = filteredFeed.collectAsState()
                    FeedList(feed.value, true)
                }
            }
        }
    }
}

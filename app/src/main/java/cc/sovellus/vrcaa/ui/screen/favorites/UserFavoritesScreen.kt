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

package cc.sovellus.vrcaa.ui.screen.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Avatar
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import cc.sovellus.vrcaa.ui.components.layout.FavoriteHorizontalRow
import cc.sovellus.vrcaa.ui.components.layout.RowItem
import cc.sovellus.vrcaa.ui.screen.avatar.UserAvatarScreen
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldScreen

class UserFavoritesScreen(
    private val userId: String
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val model = rememberScreenModel { UserFavoritesScreenModel(userId) }

        val state by model.state.collectAsState()

        when (val result = state) {
            is UserFavoritesScreenModel.UserFavoriteState.Loading -> LoadingIndicatorScreen().Content()
            is UserFavoritesScreenModel.UserFavoriteState.Result -> ShowScreen(model, result.worlds, result.avatars)
            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowScreen(
        model: UserFavoritesScreenModel,
        worlds: MutableMap<String, SnapshotStateList<World?>>,
        avatars: MutableMap<String, SnapshotStateList<Avatar?>>
    ) {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    title = {
                        Text(text = stringResource(R.string.favorite_page_title))
                    }
                )
            }
        ) { innerPadding ->
            val options = stringArrayResource(R.array.user_favorites_selection_options)
            val icons = listOf(Icons.Filled.Cabin, Icons.Filled.Person)

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MultiChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    options.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = options.size
                            ),
                            icon = {
                                SegmentedButtonDefaults.Icon(
                                    active = index == model.currentIndex.intValue,
                                    activeContent = {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize).offset(y = 2.5.dp)
                                        )
                                    },
                                    inactiveContent = {
                                        Icon(
                                            imageVector = icons[index],
                                            contentDescription = null,
                                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize).offset(y = 2.5.dp)
                                        )
                                    }
                                )
                            },
                            onCheckedChange = {
                                model.currentIndex.intValue = index
                            },
                            checked = index == model.currentIndex.intValue
                        ) {
                            Text(text = label, softWrap = true, maxLines = 1)
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(4.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    item {
                        when (model.currentIndex.intValue) {
                            0 -> ShowWorlds(worlds)
                            1 -> ShowAvatars(avatars)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ShowWorlds(
        list: MutableMap<String, SnapshotStateList<World?>>
    ) {
        val navigator = LocalNavigator.currentOrThrow

        if (list.isNotEmpty()) {
            list.forEach { item ->
                if (item.value.isNotEmpty()) {
                    FavoriteHorizontalRow(
                        title = item.key,
                        allowEdit = false,
                        onEdit = {}
                    ) {
                        items(item.value) {
                            it?.let {
                                RowItem(name = it.name, url = it.thumbnailImageUrl) {
                                    if (it.name != "???") {
                                        navigator.push(WorldScreen(it.id))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.padding(4.dp))
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.result_not_found))
            }
        }
    }

    @Composable
    fun ShowAvatars(
        list: MutableMap<String, SnapshotStateList<Avatar?>>
    ) {
        val navigator = LocalNavigator.currentOrThrow

        if (list.isNotEmpty()) {
            list.forEach { item ->
                if (item.value.isNotEmpty()) {
                    FavoriteHorizontalRow(
                        title = item.key,
                        allowEdit = false,
                        onEdit = {}
                    ) {
                        items(item.value) {
                            it?.let {
                                RowItem(name = it.name, url = it.thumbnailImageUrl) {
                                    if (it.name != "???") {
                                        navigator.push(UserAvatarScreen(it))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.padding(4.dp))
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.result_not_found))
            }
        }
    }
}
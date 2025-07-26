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

package cc.sovellus.vrcaa.ui.screen.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.search.models.SearchAvatar
import cc.sovellus.vrcaa.extension.columnCountOption
import cc.sovellus.vrcaa.extension.fixedColumnSize
import cc.sovellus.vrcaa.ui.screen.avatar.AvatarScreen
import cc.sovellus.vrcaa.ui.screen.group.GroupScreen
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import cc.sovellus.vrcaa.ui.screen.search.SearchResultScreenModel.SearchState
import cc.sovellus.vrcaa.ui.screen.world.WorldScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

class SearchResultScreen(
    private val query: String
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val model = rememberScreenModel { SearchResultScreenModel(query) }

        val state by model.state.collectAsState()

        when (state) {
            is SearchState.Loading -> LoadingIndicatorScreen().Content()
            is SearchState.Result -> MultiChoiceHandler(model)

            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MultiChoiceHandler(
        model: SearchResultScreenModel
    ) {

        val navigator = LocalNavigator.currentOrThrow

        BackHandler(enabled = model.currentIndex.intValue != 0, onBack = {
            model.currentIndex.intValue = 0
        })

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
                    title = { Text(text = "${stringResource(R.string.search_text_result)} $query") }
                )
            },
            content = {

            val options = stringArrayResource(R.array.search_selection_options)
            val icons = listOf(
                Icons.Filled.Cabin,
                Icons.Filled.Person,
                Icons.Filled.Visibility,
                Icons.Filled.Groups
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()
                    ),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MultiChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    options.forEachIndexed { index, label ->
                        SegmentedButton(shape = SegmentedButtonDefaults.itemShape(
                            index = index, count = options.size
                        ), icon = {
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
                        }, onCheckedChange = {
                            model.currentIndex.intValue = index
                        }, checked = index == model.currentIndex.intValue
                        ) {
                            Text(text = label, softWrap = true, maxLines = 1)
                        }
                    }
                }

                when (model.currentIndex.intValue) {
                    0 -> ShowWorlds(model)
                    1 -> ShowUsers(model)
                    2 -> ShowAvatars(model)
                    3 -> ShowGroups(model)
                }
            }
        })
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun SearchRowItem(
        name: String, url: String, count: Int?, onClick: () -> Unit
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .height(150.dp)
                .width(200.dp)
                .clickable(onClick = { onClick() })
        ) {

            GlideImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .width(200.dp),
                contentScale = ContentScale.Crop,
                loading = placeholder(R.drawable.image_placeholder),
                failure = placeholder(R.drawable.image_placeholder)
            )

            Row {
                Text(
                    text = name,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(0.80f).padding(start = 6.dp)
                )
                if (count != null) {
                    Row(modifier = Modifier.weight(0.20f).padding(end = 2.dp)) {
                        Text(
                            text = count.toString(),
                            textAlign = TextAlign.Start,
                        )
                        Icon(
                            imageVector = Icons.Filled.Group, contentDescription = null
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun SearchRowItemAvatar(
        name: String, url: String, avatar: SearchAvatar, onClick: () -> Unit
    ) {
        var foundWindows by remember { mutableStateOf(false) }
        var foundAndroid by remember { mutableStateOf(false) }
        var foundDarwin by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            avatar.compatibility.forEach { platform ->
                when (platform) {
                    "pc" -> {
                        foundWindows = true
                    }
                    "android" -> {
                        foundAndroid = true
                    }
                    "ios" -> {
                        foundDarwin = true
                    }
                }
            }
        }

        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .height(150.dp)
                .width(200.dp)
                .clickable(onClick = { onClick() })
        ) {

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.TopStart
            ) {
                GlideImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .width(200.dp)
                        .zIndex(0f),
                    contentScale = ContentScale.Crop,
                    loading = placeholder(R.drawable.image_placeholder),
                    failure = placeholder(R.drawable.image_placeholder)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp)
                        .zIndex(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (foundWindows) {
                        Badge(
                            containerColor = Color(0, 168, 252, 191),
                            modifier = Modifier
                                .height(height = 26.dp)
                                .padding(start = 2.dp, top = 8.dp),
                            content = {
                                Text(
                                    text = "Windows"
                                )
                            }
                        )
                    }

                    if (foundAndroid) {
                        Badge(
                            containerColor = Color(103, 215, 129, 191),
                            modifier = Modifier
                                .height(height = 26.dp)
                                .padding(start = 2.dp, top = 8.dp),
                            content = {
                                Text(
                                    text = "Android"
                                )
                            }
                        )
                    }

                    if (foundDarwin) {
                        Badge(
                            containerColor = Color(121, 136, 151, 191),
                            modifier = Modifier
                                .height(height = 26.dp)
                                .padding(start = 2.dp, top = 8.dp),
                            content = {
                                Text(
                                    text = "iOS"
                                )
                            }
                        )
                    }
                }
            }

            Row {
                Text(
                    text = name,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(0.80f).padding(start = 6.dp)
                )
            }
        }
    }

    @Composable
    private fun ShowWorlds(
        model: SearchResultScreenModel
    ) {
        val state = model.worlds.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        if (state.value.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.result_not_found))
            }
        } else {
            LazyVerticalGrid(
                columns = when (model.preferences.columnCountOption) {
                    0 -> GridCells.Adaptive(180.dp)
                    else -> GridCells.Fixed(model.preferences.fixedColumnSize)
                },contentPadding = PaddingValues(
                    start = 12.dp, top = 16.dp, end = 16.dp, bottom = 16.dp
                ), content = {
                    items(state.value) { world ->
                        SearchRowItem(
                            name = world.name, url = world.imageUrl, count = world.occupants
                        ) { navigator.push(WorldScreen(world.id)) }
                    }

                    if (!model.worldLimitReached.value) {
                        item(span = { GridItemSpan(if (model.preferences.columnCountOption == 0) { 2 } else { model.preferences.fixedColumnSize })}) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Button(onClick = { model.fetchMoreWorlds() }) {
                                    Text(text = stringResource(R.string.search_button_more))
                                }
                            }
                        }
                    }
                }
            )
        }
    }

    @Composable
    private fun ShowUsers(
        model: SearchResultScreenModel
    ) {
        val state = model.users.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        if (state.value.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.result_not_found))
            }
        } else {
            LazyVerticalGrid(
                columns = when (model.preferences.columnCountOption) {
                    0 -> GridCells.Adaptive(180.dp)
                    else -> GridCells.Fixed(model.preferences.fixedColumnSize)
                },contentPadding = PaddingValues(
                    start = 12.dp, top = 16.dp, end = 16.dp, bottom = 16.dp
                ), content = {
                    items(state.value) { user ->
                        SearchRowItem(
                            name = user.displayName,
                            url = user.profilePicOverride.ifEmpty { user.currentAvatarImageUrl },
                            count = null
                        ) {
                            navigator.push(UserProfileScreen(user.id))
                        }
                    }

                    if (!model.userLimitReached.value) {
                        item(span = { GridItemSpan(if (model.preferences.columnCountOption == 0) { 2 } else { model.preferences.fixedColumnSize })}) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Button(onClick = { model.fetchMoreUsers() }) {
                                    Text(text = stringResource(R.string.search_button_more))
                                }
                            }
                        }
                    }
                }
            )
        }
    }

    @Composable
    private fun ShowAvatars(
        model: SearchResultScreenModel
    ) {
        val state = model.avatars.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.value.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.result_not_found))
                }
            } else {
                LazyVerticalGrid(
                    columns = when (model.preferences.columnCountOption) {
                        0 -> GridCells.Adaptive(180.dp)
                        else -> GridCells.Fixed(model.preferences.fixedColumnSize)
                    },contentPadding = PaddingValues(
                        start = 12.dp, top = 16.dp, end = 16.dp, bottom = 16.dp
                    ), content = {
                        items(state.value) { avatar ->
                            SearchRowItemAvatar(
                                name = avatar.name, url = avatar.imageUrl, avatar = avatar
                            ) {
                                navigator.push(AvatarScreen(avatar.vrcId))
                            }
                        }

                        if (!model.avatarLimitReached.value) {
                            item(span = { GridItemSpan(if (model.preferences.columnCountOption == 0) { 2 } else { model.preferences.fixedColumnSize })}) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Button(onClick = { model.fetchMoreAvatars() }) {
                                        Text(text = stringResource(R.string.search_button_more))
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    @Composable
    private fun ShowGroups(
        model: SearchResultScreenModel
    ) {
        val state = model.groups.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        if (state.value.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.result_not_found))
            }
        } else {
            LazyVerticalGrid(
                columns = when (model.preferences.columnCountOption) {
                    0 -> GridCells.Adaptive(180.dp)
                    else -> GridCells.Fixed(model.preferences.fixedColumnSize)
                },contentPadding = PaddingValues(
                    start = 12.dp, top = 16.dp, end = 16.dp, bottom = 16.dp
                ), content = {
                    items(state.value) { group ->
                        SearchRowItem(
                            name = group.name, url = group.bannerUrl, count = null
                        ) {
                            navigator.push(GroupScreen(group.id))
                        }
                    }

                    if (!model.groupLimitReached.value) {
                        item(span = { GridItemSpan(if (model.preferences.columnCountOption == 0) { 2 } else { model.preferences.fixedColumnSize })}) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Button(onClick = { model.fetchMoreGroups() }) {
                                    Text(text = stringResource(R.string.search_button_more))
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.columnCountOption
import cc.sovellus.vrcaa.extension.fixedColumnSize
import cc.sovellus.vrcaa.extension.worldsAmount
import cc.sovellus.vrcaa.ui.components.layout.GridItem
import cc.sovellus.vrcaa.ui.components.misc.LimitedChipSelect
import cc.sovellus.vrcaa.ui.components.misc.SearchFilterSection
import cc.sovellus.vrcaa.ui.components.misc.SnappedCountSlider
import cc.sovellus.vrcaa.ui.screen.avatar.AvatarScreen
import cc.sovellus.vrcaa.ui.screen.group.GroupScreen
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import cc.sovellus.vrcaa.ui.screen.search.SearchResultScreenModel.SearchState
import cc.sovellus.vrcaa.ui.screen.world.WorldScreen
import kotlinx.coroutines.launch

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
        val settingsSheetState = rememberModalBottomSheetState()
        var showSettingsSheet by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

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
                    actions = {
                        IconButton(onClick = {
                            showSettingsSheet = true
                        }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
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
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                            index = index, count = options.size
                        ), icon = {
                            SegmentedButtonDefaults.Icon(
                                active = index == model.currentIndex.intValue,
                                activeContent = {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                                            .offset(y = 2.5.dp)
                                    )
                                },
                                inactiveContent = {
                                    Icon(
                                        imageVector = icons[index],
                                        contentDescription = null,
                                        modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                                            .offset(y = 2.5.dp)
                                    )
                                }
                            )
                        }, onCheckedChange = {
                            model.currentIndex.intValue = index
                        }, checked = index == model.currentIndex.intValue
                        ) {
                            Text(
                                text = label,
                                softWrap = true,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                when (model.currentIndex.intValue) {
                    0 -> ShowWorlds(model)
                    1 -> ShowUsers(model)
                    2 -> ShowAvatars(model)
                    3 -> ShowGroups(model)
                }

                if (showSettingsSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            showSettingsSheet = false
                        }, sheetState = settingsSheetState
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 8.dp,
                                bottom = 24.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            when (model.currentIndex.intValue) {
                                                0 -> model.resetWorldFilters()
                                                1 -> model.resetUserFilters()
                                                2 -> model.resetAvatarFilters()
                                                3 -> model.resetGroupFilters()
                                            }
                                        }
                                    ) {
                                        Text(stringResource(R.string.search_filter_button_reset))
                                    }

                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            scope.launch {
                                                when (model.currentIndex.intValue) {
                                                    0 -> model.filterWorlds()
                                                    1 -> model.filterUsers()
                                                    2 -> model.filterAvatars()
                                                    3 -> model.filterGroups()
                                                }
                                                settingsSheetState.hide()
                                            }.invokeOnCompletion {
                                                if (!settingsSheetState.isVisible) {
                                                    showSettingsSheet = false
                                                }
                                            }
                                        }
                                    ) {
                                        Text(stringResource(R.string.search_filter_button_apply))
                                    }
                                }
                            }
                            item {
                                when (model.currentIndex.intValue) {
                                    0 -> { // worlds
                                        SearchFilterSection(
                                            title = stringResource(R.string.search_filter_label_count),
                                            icon = Icons.Outlined.Devices
                                        ) {
                                            SnappedCountSlider(
                                                value = model.worldsAmount.intValue,
                                                onValueChange = { model.worldsAmount.intValue = it }
                                            )
                                        }

                                        HorizontalDivider(
                                            Modifier,
                                            DividerDefaults.Thickness,
                                            DividerDefaults.color
                                        )

                                        SearchFilterSection(
                                            title = stringResource(R.string.search_filter_label_platform),
                                            icon = Icons.Outlined.Devices
                                        ) {
                                            LimitedChipSelect(
                                                items = listOf("PC", "Android", "iOS"),
                                                selected = model.worldPlatformFilterSelection.value,
                                                minSelected = 1,
                                                maxSelected = 3,
                                                onSelectedChange = { changed ->
                                                    model.worldPlatformFilterSelection.value = changed
                                                }
                                            )
                                        }

                                        HorizontalDivider(
                                            Modifier,
                                            DividerDefaults.Thickness,
                                            DividerDefaults.color
                                        )

                                        SearchFilterSection(
                                            title = stringResource(R.string.search_filter_label_content_gating),
                                            icon = Icons.Outlined.WarningAmber
                                        ) {
                                            LimitedChipSelect(
                                                items = listOf("Sexually Suggestive", " Adult Language and Themes", "Graphic Violence", "Excessive Gore", "Extreme Horror"),
                                                selected = model.worldContentFilterSelection.value,
                                                minSelected = 0,
                                                maxSelected = 5,
                                                onSelectedChange = { changed ->
                                                    model.worldContentFilterSelection.value = changed
                                                }
                                            )
                                        }
                                    }
                                    1 -> { // users
                                        SearchFilterSection(
                                            title = stringResource(R.string.search_filter_label_count),
                                            icon = Icons.Outlined.Devices
                                        ) {
                                            SnappedCountSlider(
                                                value = model.usersAmount.intValue,
                                                onValueChange = { model.usersAmount.intValue = it }
                                            )
                                        }
                                    }
                                    2 -> { // avatars
                                        SearchFilterSection(
                                            title = stringResource(R.string.search_filter_label_count),
                                            icon = Icons.Outlined.Devices
                                        ) {
                                            SnappedCountSlider(
                                                value = model.avatarsAmount.intValue,
                                                onValueChange = { model.avatarsAmount.intValue = it }
                                            )
                                        }
                                        HorizontalDivider(
                                            Modifier,
                                            DividerDefaults.Thickness,
                                            DividerDefaults.color
                                        )
                                        SearchFilterSection(
                                            title = stringResource(R.string.search_filter_label_platform),
                                            icon = Icons.Outlined.Devices
                                        ) {
                                            LimitedChipSelect(
                                                items = listOf("PC", "Android", "iOS"),
                                                selected = model.avatarPlatformFilterSelection.value,
                                                minSelected = 1,
                                                maxSelected = 3,
                                                onSelectedChange = { changed ->
                                                    model.avatarPlatformFilterSelection.value = changed
                                                }
                                            )
                                        }
                                        HorizontalDivider(
                                            Modifier,
                                            DividerDefaults.Thickness,
                                            DividerDefaults.color
                                        )
                                        SearchFilterSection(
                                            title = stringResource(R.string.search_filter_label_performance_rank),
                                            icon = Icons.Outlined.Shield
                                        ) {
                                            LimitedChipSelect(
                                                items = listOf("Very Poor", "Poor", "Medium", "Good", "Excellent"),
                                                selected = model.avatarPerformanceFilterSelection.value,
                                                minSelected = 1,
                                                maxSelected = 5,
                                                onSelectedChange = { changed ->
                                                    model.avatarPerformanceFilterSelection.value = changed
                                                }
                                            )
                                        }
                                        HorizontalDivider(
                                            Modifier,
                                            DividerDefaults.Thickness,
                                            DividerDefaults.color
                                        )
                                        SearchFilterSection(
                                            title = stringResource(R.string.search_filter_label_content_gating),
                                            icon = Icons.Outlined.WarningAmber
                                        ) {
                                            LimitedChipSelect(
                                                items = listOf("Sexually Suggestive", " Adult Language and Themes", "Graphic Violence", "Excessive Gore", "Extreme Horror"),
                                                selected = model.avatarContentFilterSelection.value,
                                                minSelected = 0,
                                                maxSelected = 5,
                                                onSelectedChange = { changed ->
                                                    model.avatarContentFilterSelection.value = changed
                                                }
                                            )
                                        }
                                    }
                                    3 -> { // groups
                                        SearchFilterSection(
                                            title = stringResource(R.string.search_filter_label_count),
                                            icon = Icons.Outlined.Devices
                                        ) {
                                            SnappedCountSlider(
                                                value = model.groupsAmount.intValue,
                                                onValueChange = { model.groupsAmount.intValue = it }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
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
                    0 -> GridCells.Adaptive(166.dp)
                    else -> GridCells.Fixed(model.preferences.fixedColumnSize)
                },contentPadding = PaddingValues(
                    start = 12.dp, top = 16.dp, end = 16.dp, bottom = 16.dp
                ), content = {
                    items(state.value) { world ->
                        GridItem(
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
                    0 -> GridCells.Adaptive(166.dp)
                    else -> GridCells.Fixed(model.preferences.fixedColumnSize)
                },contentPadding = PaddingValues(
                    start = 12.dp, top = 16.dp, end = 16.dp, bottom = 16.dp
                ), content = {
                    items(state.value) { user ->
                        GridItem(
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
                        0 -> GridCells.Adaptive(166.dp)
                        else -> GridCells.Fixed(model.preferences.fixedColumnSize)
                    },contentPadding = PaddingValues(
                        start = 12.dp, top = 16.dp, end = 16.dp, bottom = 16.dp
                    ), content = {
                        items(state.value) { avatar ->
                            GridItem(
                                name = avatar.name, url = avatar.imageUrl ?: "", count = null
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
                    0 -> GridCells.Adaptive(166.dp)
                    else -> GridCells.Fixed(model.preferences.fixedColumnSize)
                },contentPadding = PaddingValues(
                    start = 12.dp, top = 16.dp, end = 16.dp, bottom = 16.dp
                ), content = {
                    items(state.value) { group ->
                        GridItem(
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

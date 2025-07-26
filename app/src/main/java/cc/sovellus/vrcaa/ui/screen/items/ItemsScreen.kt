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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.BorderAll
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.Card
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Inventory
import cc.sovellus.vrcaa.extension.columnCountOption
import cc.sovellus.vrcaa.extension.fixedColumnSize
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

class ItemsScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val model = rememberScreenModel { ItemsScreenModel() }
        val state by model.state.collectAsState()

        when (val result = state) {
            is ItemsScreenModel.ItemsState.Loading -> LoadingIndicatorScreen().Content()
            is ItemsScreenModel.ItemsState.Empty -> HandleEmpty()
            is ItemsScreenModel.ItemsState.Result -> DisplayResult(result.items, result.archivedItems, model)
            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun HandleEmpty() {
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
                        Text(text = stringResource(R.string.items_page_title))
                    }
                )
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = padding.calculateTopPadding(),
                            bottom = padding.calculateBottomPadding()
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.result_not_found))
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
    @Composable
    private fun DisplayResult(
        items: ArrayList<Inventory.Data>,
        archivedItems: ArrayList<Inventory.Data>,
        model: ItemsScreenModel
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
                        Text(text = stringResource(R.string.items_page_title))
                    }
                )
            },
            content = { padding ->

                val options = stringArrayResource(R.array.items_page_sections)
                val icons = listOf(Icons.Filled.Unarchive, Icons.Filled.Archive)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = padding.calculateTopPadding(),
                            bottom = padding.calculateBottomPadding()
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
                        0 -> ShowItems(items, model)
                        1 -> ShowItems(archivedItems, model)
                    }
                }
            }
        )
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun ShowItems(items: ArrayList<Inventory.Data>, model: ItemsScreenModel) {

        if (items.isEmpty()) {
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
                    0 -> GridCells.Adaptive(133.dp)
                    else -> GridCells.Fixed(model.preferences.fixedColumnSize)
                },
                contentPadding = PaddingValues(
                    start = 12.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),
                content = {
                    items(items.size) {
                        val item = items[it]
                        Card(Modifier.padding(4.dp)) {
                            GlideImage(
                                model = item.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                                    .height(100.dp)
                                    .width(133.dp)
                                    .clip(RoundedCornerShape(10)),
                                contentScale = ContentScale.FillBounds,
                                loading = placeholder(R.drawable.image_placeholder),
                                failure = placeholder(R.drawable.image_placeholder)
                            )
                        }
                    }
                }
            )
        }
    }
}

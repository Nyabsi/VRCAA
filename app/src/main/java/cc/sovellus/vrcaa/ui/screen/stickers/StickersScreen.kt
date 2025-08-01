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

package cc.sovellus.vrcaa.ui.screen.stickers

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFiles.ImageAspectRatio
import cc.sovellus.vrcaa.api.vrchat.http.models.Inventory
import cc.sovellus.vrcaa.extension.columnCountOption
import cc.sovellus.vrcaa.extension.fixedColumnSize
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.ui.components.dialog.ImagePreviewDialog
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import kotlinx.coroutines.launch

class StickersScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val model = rememberScreenModel { StickersScreenModel() }
        val state by model.state.collectAsState()

        when (val result = state) {
            is StickersScreenModel.StickerState.Loading -> LoadingIndicatorScreen().Content()
            is StickersScreenModel.StickerState.Empty -> HandleEmpty()
            is StickersScreenModel.StickerState.Result -> DisplayResult(result.stickers, result.userStickers, result.archivedStickers, model)
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
                        Text(text = stringResource(R.string.stickers_page_title))
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
        stickers: ArrayList<Inventory.Data>,
        userStickers: ArrayList<Inventory.Data>,
        archivedStickers: ArrayList<Inventory.Data>,
        model: StickersScreenModel
    ) {
        val navigator = LocalNavigator.currentOrThrow


        val scope = rememberCoroutineScope()
        val pickImage = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            uri?.let {
                scope.launch {
                    api.files.uploadImage("sticker", uri, ImageAspectRatio.IMAGE_ASPECT_RATIO_SQUARE)?.let {
                        model.fetchStickers()
                    }
                }
            }
        }

        Scaffold(
            modifier = Modifier.blur(if (model.previewItem.value != null) { 100.dp } else { 0.dp }),
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
                        Text(text = stringResource(R.string.stickers_page_title))
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(4.dp),
                    onClick = {
                        pickImage.launch(arrayOf("image/png", "image/jpeg", "image/gif"))
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(stringResource(R.string.stickers_page_button_upload))
                    }
                )
            },
            content = { padding ->

                val options = stringArrayResource(R.array.sticker_page_sections)
                val icons = listOf(Icons.Filled.Unarchive, Icons.Filled.UploadFile, Icons.Filled.Archive)

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
                        0 -> ShowItems(stickers, model)
                        1 -> ShowItems(userStickers, model)
                        2 -> ShowItems(archivedStickers, model)
                    }
                }
            }
        )

        model.previewItem.value?.let {
            ImagePreviewDialog(
                url = it.imageUrl,
                name = it.name,
                onDismiss = { model.previewItem.value = null }
            )
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun ShowItems(items: ArrayList<Inventory.Data>, model: StickersScreenModel) {

        if (items.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.result_not_found))
            }
        } else {
            val window = LocalWindowInfo.current
            LazyVerticalGrid(
                columns = when (model.preferences.columnCountOption) {
                    0 -> GridCells.Adaptive(132.dp)
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
                                    .heightIn(132.dp)
                                    .widthIn(132.dp, (window.containerSize.width.dp / 2))
                                    .aspectRatio(1f / 1f)
                                    .clip(RoundedCornerShape(10))
                                    .clickable(onClick = {
                                        model.previewItem.value = item
                                    }),
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

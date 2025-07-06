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

package cc.sovellus.vrcaa.ui.screen.prints

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Print
import cc.sovellus.vrcaa.extension.columnCountOption
import cc.sovellus.vrcaa.extension.fixedColumnSize
import cc.sovellus.vrcaa.ui.components.dialog.ImageZoomDialog
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

class PrintsScreen(
    private val userId: String
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val model = rememberScreenModel { PrintsScreenModel(userId) }
        val state by model.state.collectAsState()

        when (val result = state) {
            is PrintsScreenModel.PrintsState.Loading -> LoadingIndicatorScreen().Content()
            is PrintsScreenModel.PrintsState.Empty -> HandleEmpty()
            is PrintsScreenModel.PrintsState.Result -> DisplayResult(result.prints, model)
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
                        Text(text = "Prints")
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
    private fun DisplayResult(prints: ArrayList<Print>, model: PrintsScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        var previewFile by remember { mutableStateOf<Print.Files?>(null) }

        Scaffold(
            modifier = Modifier.blur(if (previewFile != null) { 100.dp } else { 0.dp }),
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
                        Text(text = "Prints") // stringResource(R.string.avatars_page_title)
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
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LazyVerticalGrid(
                        columns = when (model.preferences.columnCountOption) {
                            0 -> GridCells.Adaptive(180.dp)
                            else -> GridCells.Fixed(model.preferences.fixedColumnSize)
                        },
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        ),
                        content = {
                            items(prints.size) {
                                val print = prints[it]
                                GlideImage(
                                    model = print.files.image,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                        .height(140.dp)
                                        .width(200.dp)
                                        .clip(RoundedCornerShape(10))
                                        .clickable(onClick = {
                                            previewFile = print.files
                                        }),
                                    contentScale = ContentScale.FillBounds,
                                    loading = placeholder(R.drawable.image_placeholder),
                                    failure = placeholder(R.drawable.image_placeholder)
                                )
                            }
                        }
                    )
                }

                previewFile?.let {
                    ImageZoomDialog(
                        url = it.image,
                        name = it.fileId,
                        onDismiss = { previewFile = null }
                    )
                }
            }
        )
    }
}

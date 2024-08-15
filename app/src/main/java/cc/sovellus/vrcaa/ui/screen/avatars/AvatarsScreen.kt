package cc.sovellus.vrcaa.ui.screen.avatars

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.Avatar
import cc.sovellus.vrcaa.ui.screen.avatar.AvatarScreen
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

class AvatarsScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val model = rememberScreenModel { AvatarsScreenModel() }
        val state by model.state.collectAsState()

        when (val result = state) {
            is AvatarsState.Loading -> LoadingIndicatorScreen().Content()
            is AvatarsState.Result -> DisplayResult(result.avatars)
            else -> {}
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun SearchRowItem(
        name: String,
        url: String,
        count: Int?,
        onClick: () -> Unit
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
                contentDescription = stringResource(R.string.preview_image_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .width(200.dp),
                contentScale = ContentScale.Crop,
                loading = placeholder(R.drawable.image_placeholder),
                failure = placeholder(R.drawable.image_placeholder)
            )

            Row(
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = name,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (count != null) {
                    Text(
                        text = count.toString(), textAlign = TextAlign.End, modifier = Modifier
                            .padding(end = 2.dp)
                    )
                    Icon(
                        imageVector = Icons.Filled.Group,
                        contentDescription = stringResource(R.string.preview_image_description)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun DisplayResult(avatars: ArrayList<Avatar>) {
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
                        Text(text = stringResource(R.string.avatars_page_title))
                    }
                )
            },
            content = { padding ->
                if (avatars.isEmpty()) {
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
                } else {
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
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(
                                start = 12.dp,
                                top = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                            content = {
                                items(avatars.size) {
                                    val avatar = avatars[it]
                                    SearchRowItem(
                                        name = avatar.name,
                                        url = avatar.thumbnailImageUrl,
                                        count = null
                                    ) {
                                        navigator.push(AvatarScreen(avatar.id))
                                    }
                                }
                            }
                        )
                    }
                }
            }
        )
    }
}
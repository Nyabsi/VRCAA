package cc.sovellus.vrcaa.ui.screen.world


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.models.World
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldInfoScreenModel.WorldInfoState
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

class WorldInfoScreen(
    private val worldId: String
) : Screen {

    @Composable
    override fun Content() {

        val context = LocalContext.current

        val model = rememberScreenModel { WorldInfoScreenModel(context, worldId) }
        val state by model.state.collectAsState()

        when (val result = state) {
            is WorldInfoState.Loading -> LoadingIndicatorScreen().Content()
            is WorldInfoState.Result -> RenderWorld(result.world)
            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun RenderWorld(world: World) {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go Back"
                            )
                        }
                    },

                    title = { Text(text = world.name) }
                )
            },
            content = { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = padding.calculateTopPadding()),
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            world.let {
                                WorldCard(
                                    url = it.imageUrl,
                                    name = it.name,
                                    author = "By ${it.authorName}"
                                )
                            }
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.Start
                        ) {
                            SubHeader(title = stringResource(R.string.world_label_description))
                            Description(text = world.description)

                            SubHeader(title = stringResource(R.string.world_label_tags))
                            AuthorTags(tags = world.tags)
                        }
                    }
                }
            }
        )
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun WorldCard(
        url: String,
        name: String,
        author: String
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .height(320.dp)
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            GlideImage(
                model = url,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = name,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left
            )

            Text(
                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                text = author,
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }

    @Composable
    fun SubHeader(title: String) {
        Text(
            modifier = Modifier.padding(start = 24.dp),
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
            overflow = TextOverflow.Ellipsis
        )
    }

    @Composable
    fun Description(text: String?) {
        Column(
            modifier = Modifier
                .padding(24.dp)
        ) {
            Text(
                modifier = Modifier.padding(start = 2.dp),
                text = if (text.isNullOrEmpty()) { stringResource(R.string.world_text_no_description) } else { text },
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    @Composable
    fun AuthorTags(tags: List<String>) {
        Row(
            modifier = Modifier.padding(24.dp)
        ) {
            if (tags.isEmpty()) {

                Text(stringResource(R.string.world_text_no_tags))
            } else {
                tags.let {
                    for (tag in tags) {
                        if (tag.contains("author_tag_")) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .height(height = 24.dp)
                                    .padding(start = 2.dp),
                                content = { Text( text = tag.substring("author_tag_".length).uppercase() ) }
                            )
                        }
                    }
                }
            }
        }
    }
}
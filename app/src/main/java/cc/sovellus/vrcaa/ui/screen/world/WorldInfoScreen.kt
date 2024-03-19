package cc.sovellus.vrcaa.ui.screen.world


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.http.models.World
import cc.sovellus.vrcaa.ui.components.misc.BadgesFromTags
import cc.sovellus.vrcaa.ui.components.misc.Description
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldInfoScreenModel.WorldInfoState
import cc.sovellus.vrcaa.ui.components.card.WorldCard
import java.text.SimpleDateFormat
import java.util.Locale

class WorldInfoScreen(
    private val worldId: String
) : Screen {

    override val key = uniqueScreenKey

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
    fun RenderWorld(world: World?) {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        if (world == null) {
            Toast.makeText(
                context,
                "World is private, or it doesn't exist.",
                Toast.LENGTH_SHORT
            ).show()
            navigator.pop()
        } else {
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
                            .padding(
                                top = padding.calculateTopPadding(),
                                bottom = padding.calculateBottomPadding()
                            ),
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

                                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                                val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)

                                val createdAtFormatted = parser.parse(world.createdAt)
                                    ?.let { formatter.format(it) }

                                val updatedAtFormatted = parser.parse(world.updatedAt)
                                    ?.let { formatter.format(it) }

                                SubHeader(title = stringResource(R.string.world_title_created_at))
                                Description(text = createdAtFormatted)

                                SubHeader(title = stringResource(R.string.world_title_updated_at))
                                Description(text = updatedAtFormatted)

                                SubHeader(title = stringResource(R.string.world_label_tags))
                                BadgesFromTags(
                                    tags = world.tags,
                                    tagPropertyName = "author_tag",
                                    localizationResourceInt = R.string.world_text_no_tags
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}
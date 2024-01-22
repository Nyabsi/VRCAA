package cc.sovellus.vrcaa.ui.screen.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.api.models.Users
import cc.sovellus.vrcaa.api.models.Worlds
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.misc.NestedPlaceholderScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import cc.sovellus.vrcaa.ui.screen.search.SearchResultScreenModel.SearchState
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

class SearchResultScreen(
    private val query: String
) : Screen {

    @Composable
    override fun Content() {

        val context = LocalContext.current

        val model = rememberScreenModel { SearchResultScreenModel(query = query, api = ApiContext(context)) }

        val state by model.state.collectAsState()

        when (val result = state) {
            is SearchState.Loading -> LoadingIndicatorScreen().Content()
            is SearchState.Result -> DisplayResult(result.foundWorlds, result.foundUsers, model)
            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
    @Composable
    fun DisplayResult(
        worlds: MutableList<Worlds.WorldItem>,
        users: MutableList<Users.UsersItem>,
        model: SearchResultScreenModel
    ) {

        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.popUntil { it.key == "main" } }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go Back"
                            )
                        }
                    },

                    title = { Text(text = "Search Results for $query") }
                )
            },
            content = {

                val options = listOf("Worlds", "Users", "Avatars")
                val icons = listOf(Icons.Filled.Cabin, Icons.Filled.Person, Icons.Filled.Visibility)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = it.calculateTopPadding()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MultiChoiceSegmentedButtonRow {
                        options.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                                icon = {
                                    SegmentedButtonDefaults.Icon(active = index == model.currentIndex.intValue) {
                                        Icon(
                                            imageVector = icons[index],
                                            contentDescription = null,
                                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                                        )
                                    }
                                },
                                onCheckedChange = {
                                    model.currentIndex.intValue = index
                                },
                                checked = index == model.currentIndex.intValue
                            ) {
                                Text(label)
                            }
                        }
                    }

                    when(model.currentIndex.intValue) {
                        0 -> ShowWorlds(worlds)
                        1 -> ShowUsers(users)
                        2 -> ShowAvatars()
                    }
                }
            }
        )
    }

    @Composable
    private fun VerticalColumn(
        content: LazyListScope.() -> Unit
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content
        )
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun RowItem(
        name: String,
        url: String,
        onClick: () -> Unit
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(
                onClick = { onClick() }
            ).padding(4.dp).fillMaxWidth()
        ) {
            GlideImage(
                model = url,
                contentDescription = "Preview Image",
                modifier = Modifier
                    .height(120.dp)
                    .width(200.dp)
                    .clip(RoundedCornerShape(10)),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
            Text(text = name, overflow = TextOverflow.Ellipsis, maxLines = 1, modifier = Modifier.width(200.dp).fillMaxWidth())
        }
    }

    @Composable
    private fun ShowWorlds(
        worlds: MutableList<Worlds.WorldItem>
    ) {
        val navigator = LocalNavigator.currentOrThrow

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            content = {
                items(worlds.size) {
                    val world = worlds[it]
                    RowItem(
                        name = world.name,
                        url = world.imageUrl,
                        onClick = { navigator.push(NestedPlaceholderScreen()) }
                    )
                }
            }
        )
    }

    @Composable
    private fun ShowUsers(
        users: MutableList<Users.UsersItem>
    ) {
        val navigator = LocalNavigator.currentOrThrow

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            content = {
                items(users.size) {
                    val user = users[it]
                    RowItem(
                        name = user.displayName,
                        url = user.profilePicOverride.ifEmpty { user.currentAvatarImageUrl },
                        onClick = { navigator.push(UserProfileScreen(user)) }
                    )
                }
            }
        )
    }

    @Composable
    private fun ShowAvatars() {
        VerticalColumn {
            item {
                Text(text = "Not Implemented")
            }
        }
    }
}
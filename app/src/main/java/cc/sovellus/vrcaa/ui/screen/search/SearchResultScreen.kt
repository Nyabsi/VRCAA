package cc.sovellus.vrcaa.ui.screen.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.api.models.Users
import cc.sovellus.vrcaa.api.models.Worlds
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.misc.NestedPlaceholderScreen
import cc.sovellus.vrcaa.ui.screen.search.SearchResultScreenModel.SearchState
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

class SearchResultScreen(
    private val query: String
) : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val screenModel = rememberScreenModel { SearchResultScreenModel(query = query, api = ApiContext(context)) }

        val state by screenModel.state.collectAsState()

        when (val result = state) {
            is SearchState.Loading -> LoadingIndicatorScreen().Content()
            is SearchState.Result -> DisplayResult(result.foundWorlds, result.foundUsers)
            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
    @Composable
    fun DisplayResult(worlds: MutableList<Worlds.WorldItem>, users: MutableList<Users.UsersItem>) {

        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.popUntil { it.key == "main" } }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Go Back"
                            )
                        }
                    },

                    title = { Text(text = "Search Results for $query") }
                )
            },
            content = { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = padding.calculateTopPadding(), start = 16.dp, end = 16.dp)
                ) {
                    item {
                        if (worlds.isEmpty())
                            EmptyRowItem("Could not find worlds with the specified keywords.")
                        else {
                            HorizontalRow(
                                title = "Worlds"
                            ) {
                                items(worlds.size) {
                                    val world = worlds[it]
                                    RowItem(
                                        name = world.name,
                                        url = world.imageUrl,
                                        onClick = { navigator.push(NestedPlaceholderScreen()) }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        if (users.isEmpty())
                            EmptyRowItem("Could not find users with the specified keywords.")
                        else {
                            HorizontalRow(
                                title = "Users"
                            ) {
                                items(users.size) {
                                    val user = users[it]
                                    RowItem(
                                        name = user.displayName,
                                        url = user.profilePicOverride.ifEmpty { user.currentAvatarImageUrl },
                                        onClick = { navigator.push(NestedPlaceholderScreen()) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    @Composable
    private fun HorizontalRow(
        title: String,
        content: LazyListScope.() -> Unit
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
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
            )
        ) {
            GlideImage(
                model = url,
                contentDescription = "Preview Image",
                modifier = Modifier
                    .height(150.dp)
                    .width(240.dp)
                    .clip(RoundedCornerShape(10)),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
            Text(text = name)
        }
    }

    @Composable
    private fun EmptyRowItem(label: String) {
        Column(
            modifier = Modifier
                .height(150.dp)
                .width(240.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label)
        }
    }
}
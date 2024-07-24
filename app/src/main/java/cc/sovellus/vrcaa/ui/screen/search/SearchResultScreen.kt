package cc.sovellus.vrcaa.ui.screen.search

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
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
import cc.sovellus.vrcaa.api.justhparty.JustHPartyProvider
import cc.sovellus.vrcaa.api.vrchat.models.Group
import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.models.World
import cc.sovellus.vrcaa.ui.screen.avatar.AvatarScreen
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import cc.sovellus.vrcaa.ui.screen.search.SearchResultScreenModel.SearchState
import cc.sovellus.vrcaa.ui.screen.group.GroupScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldInfoScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

class SearchResultScreen(
    private val query: String
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val context = LocalContext.current

        val model = rememberScreenModel { SearchResultScreenModel(context, query) }

        val state by model.state.collectAsState()

        when (val result = state) {
            is SearchState.Loading -> LoadingIndicatorScreen().Content()
            is SearchState.Result -> MultiChoiceHandler(
                result.worlds,
                result.users,
                result.avatars,
                result.groups,
                model
            )

            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MultiChoiceHandler(
        worlds: ArrayList<World>?,
        users: MutableList<LimitedUser>?,
        avatars: ArrayList<JustHPartyProvider.Avatar>?,
        groups: ArrayList<Group>?,
        model: SearchResultScreenModel
    ) {

        val navigator = LocalNavigator.currentOrThrow

        BackHandler(
            enabled = model.navigationStack.isNotEmpty(),
            onBack = {
                val index = model.navigationStack.last()
                model.currentIndex.intValue = index
                model.navigationStack.removeLast()
            }
        )

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
                val icons = listOf(Icons.Filled.Cabin, Icons.Filled.Person, Icons.Filled.Visibility, Icons.Filled.Groups)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = it.calculateTopPadding(),
                            bottom = it.calculateBottomPadding()
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
                                    index = index,
                                    count = options.size
                                ),
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
                                    model.navigationStack.add(model.currentIndex.intValue)
                                    model.currentIndex.intValue = index
                                },
                                checked = index == model.currentIndex.intValue
                            ) {
                                Text(text = label, softWrap = true, maxLines = 1)
                            }
                        }
                    }

                    when (model.currentIndex.intValue) {
                        0 -> ShowWorlds(worlds)
                        1 -> ShowUsers(users)
                        2 -> ShowAvatars(avatars)
                        3 -> ShowGroups(groups)
                    }
                }
            }
        )
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun RowItem(
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
                .clickable(onClick = { onClick() })
        ) {

            GlideImage(
                model = url,
                contentDescription = stringResource(R.string.preview_image_description),
                modifier = Modifier
                    .height(120.dp)
                    .width(200.dp),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                loading = placeholder(R.drawable.image_placeholder),
                failure = placeholder(R.drawable.image_placeholder)
            )

            Row(
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = name,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(0.70f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (count != null) {
                    Text(
                        text = count.toString(), textAlign = TextAlign.End, modifier = Modifier
                            .weight(0.30f)
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

    @Composable
    private fun ShowWorlds(
        worlds: ArrayList<World>?
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        if (worlds == null) {
            Toast.makeText(
                context,
                stringResource(R.string.search_failed_to_fetch_worlds_message),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            if (worlds.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.result_not_found))
                }
            } else {
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
                                count = world.occupants
                            ) { navigator.push(WorldInfoScreen(world.id)) }
                        }
                    }
                )
            }
        }
    }

    @Composable
    private fun ShowUsers(
        users: MutableList<LimitedUser>?
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        if (users == null) {
            Toast.makeText(
                context,
                stringResource(R.string.search_failed_to_fetch_users_message),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            if (users.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.result_not_found))
                }
            } else {
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
                                count = null
                            ) {
                                navigator.push(UserProfileScreen(user.id))
                            }
                        }
                    }
                )
            }
        }
    }

    @Composable
    private fun ShowAvatars(avatars: ArrayList<JustHPartyProvider.Avatar>?) {
        val context = LocalContext.current

        if (avatars == null) {
            Toast.makeText(
                context,
                stringResource(R.string.search_failed_to_fetch_avatars_message),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val navigator = LocalNavigator.currentOrThrow

                if (avatars.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(R.string.result_not_found))
                    }
                } else {
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
                                RowItem(
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
    }

    @Composable
    private fun ShowGroups(groups: ArrayList<Group>?) {
        val context = LocalContext.current

        if (groups == null) {
            Toast.makeText(
                context,
                stringResource(R.string.search_failed_to_fetch_avatars_message),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val navigator = LocalNavigator.currentOrThrow

                if (groups.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(R.string.result_not_found))
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        ),
                        content = {
                            items(groups.size) {
                                val group = groups[it]
                                RowItem(
                                    name = group.name,
                                    url = group.bannerUrl,
                                    count = null
                                ) {
                                    navigator.push(GroupScreen(group.id))
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
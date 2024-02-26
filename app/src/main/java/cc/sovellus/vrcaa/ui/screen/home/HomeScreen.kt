package cc.sovellus.vrcaa.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.models.Avatar
import cc.sovellus.vrcaa.api.models.Worlds
import cc.sovellus.vrcaa.api.models.LimitedUser
import cc.sovellus.vrcaa.api.models.World
import cc.sovellus.vrcaa.ui.screen.avatar.AvatarScreen
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.home.HomeScreenModel.HomeState
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldInfoScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

class HomeScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val model = navigator.rememberNavigatorScreenModel { HomeScreenModel(context) }
        val state by model.state.collectAsState()

        when (val result = state) {
            is HomeState.Loading -> LoadingIndicatorScreen().Content()
            is HomeState.Result -> DisplayHome(
                result.friends,
                result.lastVisited,
                result.featuredAvatars,
                result.offlineFriends,
                result.featuredWorlds
            )

            else -> {}
        }
    }

    @Composable
    fun DisplayHome(
        friends: MutableList<LimitedUser>,
        lastVisited: MutableList<World>,
        featuredAvatars: MutableList<Avatar>,
        offlineFriends: MutableList<LimitedUser>,
        featuredWorlds: MutableList<World>
    ) {
        val navigator = LocalNavigator.currentOrThrow

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            item {
                HorizontalRow(
                    title = stringResource(R.string.home_active_friends)
                ) {
                    items(
                        friends.size,
                        key = { item -> friends[item].id }
                    ) {
                        val friend = friends[it]
                        RowItemRounded(
                            name = friend.displayName,
                            url = friend.userIcon.ifEmpty { friend.imageUrl },
                            onClick = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(2.dp))
            }

            item {
                HorizontalRow(
                    title = stringResource(R.string.home_recently_visited)
                ) {
                    items(
                        lastVisited.size,
                        key = { item -> lastVisited[item].id }
                    ) {
                        val world = lastVisited[it]
                        WorldRow(
                            name = world.name,
                            url = world.imageUrl,
                            count = world.occupants,
                            onClick = { navigator.parent?.parent?.push(WorldInfoScreen(world.id)) }
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(2.dp))
            }

            item {
                HorizontalRow(
                    title = stringResource(R.string.home_featured_avatars)
                ) {
                    items(
                        featuredAvatars.size,
                        key = { item -> featuredAvatars[item].id }
                    ) {
                        val avatar = featuredAvatars[it]
                        WorldRow(
                            name = avatar.name,
                            url = avatar.imageUrl,
                            count = null,
                            onClick = { navigator.parent?.parent?.push(AvatarScreen(avatar.id)) }
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(2.dp))
            }

            item {
                HorizontalRow(
                    title = stringResource(R.string.home_offline_friends)
                ) {
                    items(
                        offlineFriends.size,
                        key = { item -> offlineFriends[item].id }
                    ) {
                        val friend = offlineFriends[it]
                        WorldRow(
                            name = friend.displayName,
                            url = friend.profilePicOverride.ifEmpty { friend.currentAvatarImageUrl },
                            count = null,
                            onClick = { navigator.parent?.parent?.push(UserProfileScreen(friend.id)) }
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(2.dp))
            }

            item {
                HorizontalRow(
                    title = stringResource(R.string.home_featured_worlds)
                ) {
                    items(
                        featuredWorlds.size,
                        key = { item -> featuredWorlds[item].id }
                    ) {
                        val world = featuredWorlds[it]
                        WorldRow(
                            name = world.name,
                            url = world.imageUrl,
                            count = world.occupants,
                            onClick = { navigator.parent?.parent?.push(WorldInfoScreen(world.id)) }
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(2.dp))
            }
        }
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
    private fun WorldRow(
        name: String,
        count: Int?,
        url: String,
        onClick: () -> Unit
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
            modifier = Modifier
                .height(180.dp)
                .width(240.dp)
                .fillMaxWidth()
                .clickable(onClick = { onClick() })
        ) {

            GlideImage(
                model = url,
                contentDescription = stringResource(R.string.preview_image_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
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
                    modifier = Modifier.weight(0.80f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (count != null) {
                    Text(
                        text = count.toString(), textAlign = TextAlign.End, modifier = Modifier
                            .weight(0.20f)
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

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun RowItemRounded(
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
                contentDescription = stringResource(R.string.preview_image_description),
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(50)),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                loading = placeholder(R.drawable.icon_placeholder),
                failure = placeholder(R.drawable.icon_placeholder)
            )
            Text(text = name)
        }
    }
}
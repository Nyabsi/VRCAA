package cc.sovellus.vrcaa.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
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
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.api.models.Avatars
import cc.sovellus.vrcaa.api.models.Friends
import cc.sovellus.vrcaa.api.models.Worlds
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.home.HomeScreenModel.HomeState
import cc.sovellus.vrcaa.ui.screen.misc.NestedPlaceholderScreen
import cc.sovellus.vrcaa.ui.screen.profile.FriendProfileScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

class HomeScreen : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val model = navigator.rememberNavigatorScreenModel { HomeScreenModel(api = ApiContext(context)) }
        val state by model.state.collectAsState()

        when (val result = state) {
            is HomeState.Loading -> LoadingIndicatorScreen().Content()
            is HomeState.Result -> DisplayHome(result.friends, result.lastVisited, result.featuredAvatars, result.offlineFriends, result.featuredWorlds)
            else -> {}
        }
    }

    @Composable
    fun DisplayHome(
        friends: MutableList<Friends.FriendsItem>,
        lastVisited: MutableList<Worlds.WorldItem>,
        featuredAvatars: MutableList<Avatars.AvatarsItem>,
        offlineFriends: MutableList<Friends.FriendsItem>,
        featuredWorlds: MutableList<Worlds.WorldItem>
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
                   title = "Active Friends"
               ) {
                   items(friends.size) {
                       val friend = friends[it]
                       RowItemRounded(
                           name = friend.displayName,
                           url = friend.imageUrl.ifEmpty { friend.currentAvatarImageUrl },
                           onClick = { navigator.parent?.parent?.push(FriendProfileScreen(friend)) }
                       )
                   }
               }
               Spacer(modifier = Modifier.padding(2.dp))
           }

           item {
               HorizontalRow(
                   title = "Recently Visited"
               ) {
                   items(lastVisited.size) {
                       val world = lastVisited[it]
                       RowItem(
                           name = world.name,
                           url = world.imageUrl,
                           onClick = { navigator.parent?.parent?.push(NestedPlaceholderScreen()) }
                       )
                   }
               }
               Spacer(modifier = Modifier.padding(2.dp))
           }

           item {
               HorizontalRow(
                   title = "Featured Avatars"
               ) {
                   items(featuredAvatars.size) {
                       val avatar = featuredAvatars[it]
                       RowItem(
                           name = avatar.name,
                           url = avatar.imageUrl,
                           onClick = { navigator.parent?.parent?.push(NestedPlaceholderScreen()) }
                       )
                   }
               }
               Spacer(modifier = Modifier.padding(2.dp))
           }

           item {
               HorizontalRow(
                   title = "Offline Friends"
               ) {
                   items(offlineFriends.size) {
                       val friend = offlineFriends[it]
                       RowItem(
                           name = friend.displayName,
                           url = friend.profilePicOverride.ifEmpty { friend.currentAvatarImageUrl },
                           onClick = { navigator.parent?.parent?.push(FriendProfileScreen(friend)) }
                       )
                   }
               }
               Spacer(modifier = Modifier.padding(2.dp))
           }

           item {
               HorizontalRow(
                   title = "Featured Worlds"
               ) {
                   items(featuredWorlds.size) {
                       val world = featuredWorlds[it]
                       RowItem(
                           name = world.name,
                           url = world.imageUrl,
                           onClick = { navigator.parent?.parent?.push(NestedPlaceholderScreen()) }
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
                contentDescription = "Preview Image",
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(50)),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
            Text(text = name)
        }
    }
}
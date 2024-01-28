package cc.sovellus.vrcaa.ui.screen.friends

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.api.helper.StatusHelper
import cc.sovellus.vrcaa.api.models.Friends
import cc.sovellus.vrcaa.ui.screen.friends.FriendsScreenModel.FriendListState
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

class FriendsScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val model = navigator.rememberNavigatorScreenModel { FriendsScreenModel(context) }
        val state by model.state.collectAsState()

        when (val result = state) {
            is FriendListState.Loading -> LoadingIndicatorScreen().Content()
            is FriendListState.Result -> RenderList(result.friends, result.offlineFriends, result.favoriteFriends, model)
            else -> {}
        }
    }

    @OptIn(
        ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class
    )
    @Composable
    private fun RenderList(
        friends: List<Friends.FriendsItem>,
        offlineFriends: List<Friends.FriendsItem>,
        favoriteFriends: List<Friends.FriendsItem>,
        model: FriendsScreenModel
    ) {

        val context = LocalContext.current
        val stateRefresh = rememberPullRefreshState(model.isRefreshing.value, onRefresh = { model.refreshFriends(context) })

        val options = stringArrayResource(R.array.friend_selection_options)
        val icons = listOf(Icons.Filled.Star, Icons.Filled.Person, Icons.Filled.PersonOff)

        Box(Modifier.pullRefresh(stateRefresh).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
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
                            Text(text = label)
                        }
                    }
                }

                when(model.currentIndex.intValue) {
                    0 -> ShowFriends(favoriteFriends)
                    1 -> ShowFriends(friends)
                    2 -> ShowFriends(offlineFriends)
                }
            }

            PullRefreshIndicator(model.isRefreshing.value, stateRefresh, Modifier.align(Alignment.TopCenter))
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun ShowFriends(friends: List<Friends.FriendsItem>) {

        if (friends.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.result_not_found))
            }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(1.dp)
            ) {
                val friendsSorted = friends.sortedBy { StatusHelper().getStatusFromString(it.status) }
                val friendsFiltered = friendsSorted.filter { it.location != "offline" }

                items(friendsFiltered.count()) {
                    val navigator = LocalNavigator.currentOrThrow
                    val friend = friendsFiltered[it]

                    ListItem(
                        headlineContent = { Text(friend.statusDescription.ifEmpty { StatusHelper.Status.toString(StatusHelper().getStatusFromString(friend.status)) }, maxLines = 1) },
                        overlineContent = { Text(friend.displayName) },
                        supportingContent = { Text(text = friend.location, maxLines = 1) },
                        leadingContent = {
                            GlideImage(
                                model = friend.userIcon.ifEmpty { friend.currentAvatarImageUrl },
                                contentDescription = stringResource(R.string.preview_image_description),
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(50)),
                                contentScale = ContentScale.FillBounds,
                                alignment = Alignment.Center
                            )
                        },
                        trailingContent = {
                            Badge(containerColor = StatusHelper.Status.toColor(StatusHelper().getStatusFromString(friend.status)), modifier = Modifier.size(16.dp))
                        },
                        modifier = Modifier.clickable(
                            onClick = {
                                navigator.parent?.parent?.push(UserProfileScreen(friend.id))
                            }
                        )
                    )
                }
            }
        }
    }
}
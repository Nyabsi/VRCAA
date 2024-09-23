package cc.sovellus.vrcaa.ui.screen.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.Instance
import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.models.UserGroups
import cc.sovellus.vrcaa.api.vrchat.models.World
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.helper.TrustHelper
import cc.sovellus.vrcaa.manager.FavoriteManager
import cc.sovellus.vrcaa.manager.FriendManager
import cc.sovellus.vrcaa.ui.components.card.InstanceCard
import cc.sovellus.vrcaa.ui.components.card.ProfileCard
import cc.sovellus.vrcaa.ui.components.dialog.FavoriteDialog
import cc.sovellus.vrcaa.ui.components.layout.RowItem
import cc.sovellus.vrcaa.ui.components.misc.Description
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import cc.sovellus.vrcaa.ui.screen.avatar.AvatarScreen
import cc.sovellus.vrcaa.ui.screen.group.GroupScreen
import cc.sovellus.vrcaa.ui.screen.group.UserGroupsScreen
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.notification.NotificationScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldInfoScreen
import cc.sovellus.vrcaa.ui.screen.worlds.WorldsScreen

class UserProfileScreen(
    private val userId: String
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val model = rememberScreenModel { UserProfileScreenModel(userId) }

        val state by model.state.collectAsState()

        when (val result = state) {
            is UserProfileState.Loading -> LoadingIndicatorScreen().Content()
            is UserProfileState.Result -> Profile(result.profile, result.instance, result.worlds, result.groups, model)
            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Profile(
        profile: LimitedUser?,
        instance: Instance?,
        worlds: ArrayList<World>,
        groups: ArrayList<UserGroups.Group>,
        model: UserProfileScreenModel
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        var isMenuExpanded by remember { mutableStateOf(false) }
        var favoriteDialogShown by remember { mutableStateOf(false) }

        if (profile == null) {
            Toast.makeText(
                context,
                stringResource(R.string.profile_user_not_found_message),
                Toast.LENGTH_SHORT
            ).show()

            LaunchedEffect(userId) {
                navigator.pop()
            }
        } else {
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
                        actions = {
                            IconButton(onClick = { isMenuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = null
                                )

                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    DropdownMenu(
                                        expanded = isMenuExpanded,
                                        onDismissRequest = { isMenuExpanded = false },
                                        offset = DpOffset(0.dp, 0.dp)
                                    ) {
                                        if (profile.isFriend) {
                                            DropdownMenuItem(
                                                onClick = {
                                                    navigator.push(
                                                        NotificationScreen(
                                                            profile.id,
                                                            profile.displayName
                                                        )
                                                    )
                                                    isMenuExpanded = false
                                                },
                                                text = { Text(stringResource(R.string.profile_user_dropdown_manage_notifications)) }
                                            )
                                        }
                                        DropdownMenuItem(
                                            onClick = {
                                                model.findAvatar { avatarId ->
                                                    if (avatarId == null)  {
                                                        Toast.makeText(
                                                            context,
                                                            context.getString(R.string.profile_user_avatar_private),
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        navigator.push(
                                                            AvatarScreen(avatarId)
                                                        )
                                                    }
                                                }
                                                isMenuExpanded = false
                                            },
                                            text = { Text(stringResource(R.string.profile_user_dropdown_view_avatar)) }
                                        )
                                        if (instance != null) {
                                            DropdownMenuItem(
                                                onClick = {

                                                        model.inviteToFriend(profile.location)
                                                        Toast.makeText(
                                                            context,
                                                            context.getString(R.string.profile_user_toast_invite_sent),
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    isMenuExpanded = false
                                                },
                                                text = { Text(stringResource(R.string.profile_user_dropdown_invite_self)) }
                                            )
                                        }
                                        if (FavoriteManager.isFavorite("friend", profile.id)) {
                                            DropdownMenuItem(
                                                onClick = {
                                                    model.removeFavorite { result ->
                                                        if (result) {
                                                            Toast.makeText(
                                                                context,
                                                                context.getString(R.string.favorite_toast_favorite_removed)
                                                                    .format(profile.displayName),
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                context.getString(R.string.favorite_toast_favorite_removed_failed)
                                                                    .format(profile.displayName),
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                    isMenuExpanded = false
                                                },
                                                text = { Text(stringResource(R.string.favorite_label_remove)) }
                                            )
                                        } else {
                                            DropdownMenuItem(
                                                onClick = {
                                                    favoriteDialogShown = true
                                                    isMenuExpanded = false
                                                },
                                                text = { Text(stringResource(R.string.favorite_label_add)) }
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        title = { Text(
                            text = profile.displayName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        ) }
                    )
                },
                content = { padding ->

                    if (favoriteDialogShown) {
                        FavoriteDialog(
                            type = "friend",
                            id = profile.id,
                            metadata = FavoriteManager.FavoriteMetadata(profile.id, "", profile.displayName, ""),
                            onDismiss = { favoriteDialogShown = false },
                            onConfirmation = { result ->
                                if (result) {
                                    FriendManager.setIsFavorite(profile.id, true)
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.favorite_toast_favorite_added).format(profile.displayName),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.favorite_toast_favorite_added_failed).format(profile.displayName),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                favoriteDialogShown = false
                            }
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(
                                top = padding.calculateTopPadding(),
                                bottom = padding.calculateBottomPadding()
                            ),
                    ) {
                        item {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                profile.let {
                                    ProfileCard(
                                        thumbnailUrl = it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl },
                                        iconUrl = it.userIcon.ifEmpty { it.currentAvatarImageUrl },
                                        displayName = it.displayName,
                                        statusDescription = it.statusDescription.ifEmpty {
                                            StatusHelper.getStatusFromString(it.status).toString()
                                        },
                                        trustRankColor = TrustHelper.getTrustRankFromTags(it.tags).toColor(),
                                        statusColor = StatusHelper.getStatusFromString(it.status).toColor(),
                                        tags = profile.tags
                                    )
                                }
                            }
                        }

                        item {
                            if (instance != null) {
                                Column(
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.Start,
                                    modifier = Modifier.padding(top = 16.dp)
                                ) {
                                    InstanceCard(profile = profile, instance = instance) {
                                        navigator.push(WorldInfoScreen(instance.worldId))
                                    }
                                }
                            }
                        }

                        item {
                            Column(
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.Start
                            ) {
                                ElevatedCard(
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 6.dp
                                    ),
                                    modifier = Modifier.padding(top = 16.dp).defaultMinSize(minHeight = 80.dp),
                                ) {
                                    SubHeader(title = stringResource(R.string.profile_label_biography))
                                    Description(text = profile.bio)
                                }
                            }
                        }

                        item {
                            if (worlds.isNotEmpty()) {
                                Column(
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    SubHeader(title = stringResource(R.string.worlds_page_title).format(profile.displayName))

                                    LazyRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        items(worlds, key = { it.id }) { world ->
                                            RowItem(
                                                name = world.name,
                                                url = world.thumbnailImageUrl,
                                                onClick = {
                                                    navigator.push(
                                                        WorldInfoScreen(world.id)
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            val nonMutualGroups = groups.filter { !it.mutualGroup }
                            if (nonMutualGroups.isNotEmpty()) {
                                Column(
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.Start,
                                ) {
                                    SubHeader(title = stringResource(R.string.group_user_viewing_groups_username).format(profile.displayName))

                                    LazyRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        items(nonMutualGroups, key = { it.groupId }) { group ->
                                            RowItem(
                                                name = group.name,
                                                url = group.bannerUrl,
                                                onClick = {
                                                    navigator.push(
                                                        GroupScreen(group.groupId)
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            val mutualGroups = groups.filter { it.mutualGroup }
                            if (mutualGroups.isNotEmpty()) {
                                Column(
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.Start,
                                ) {

                                    SubHeader(title = stringResource(R.string.profile_mutual_groups_text))

                                    LazyRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        items(mutualGroups, key = { it.groupId }) { group ->
                                            RowItem(
                                                name = group.name,
                                                url = group.bannerUrl,
                                                onClick = {
                                                    navigator.push(
                                                        GroupScreen(group.groupId)
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}
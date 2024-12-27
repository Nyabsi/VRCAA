package cc.sovellus.vrcaa.ui.screen.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites
import cc.sovellus.vrcaa.api.vrchat.http.models.Instance
import cc.sovellus.vrcaa.api.vrchat.http.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.http.models.UserGroup
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.helper.TrustHelper
import cc.sovellus.vrcaa.manager.FavoriteManager
import cc.sovellus.vrcaa.ui.components.card.InstanceCard
import cc.sovellus.vrcaa.ui.components.card.ProfileCard
import cc.sovellus.vrcaa.ui.components.dialog.FavoriteDialog
import cc.sovellus.vrcaa.ui.components.layout.RowItem
import cc.sovellus.vrcaa.ui.components.misc.Description
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import cc.sovellus.vrcaa.ui.screen.avatar.AvatarScreen
import cc.sovellus.vrcaa.ui.screen.group.GroupScreen
import cc.sovellus.vrcaa.ui.screen.group.UserGroupsScreen
import cc.sovellus.vrcaa.ui.screen.group.UserGroupsScreenModel
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.notification.NotificationScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldInfoScreen
import cc.sovellus.vrcaa.ui.screen.worlds.WorldsScreen
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

class UserProfileScreen(
    private val userId: String
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val model = rememberScreenModel { UserProfileScreenModel(userId) }

        val state by model.state.collectAsState()

        when (val result = state) {
            is UserProfileScreenModel.UserProfileState.Loading -> LoadingIndicatorScreen().Content()
            is UserProfileScreenModel.UserProfileState.Result -> Profile(
                result.profile, result.instance, model
            )

            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Profile(
        profile: LimitedUser?,
        instance: Instance?,
        model: UserProfileScreenModel
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        var isMenuExpanded by remember { mutableStateOf(false) }
        var favoriteDialogShown by remember { mutableStateOf(false) }

        if (profile == null) {
            Toast.makeText(
                context, stringResource(R.string.profile_user_not_found_message), Toast.LENGTH_SHORT
            ).show()

            LaunchedEffect(userId) {
                navigator.pop()
            }
        } else {
            Scaffold(topBar = {
                TopAppBar(navigationIcon = {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }, actions = {
                    IconButton(onClick = { isMenuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert, contentDescription = null
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
                                    DropdownMenuItem(onClick = {
                                        navigator.push(
                                            NotificationScreen(
                                                profile.id, profile.displayName
                                            )
                                        )
                                        isMenuExpanded = false
                                    },
                                        text = { Text(stringResource(R.string.profile_user_dropdown_manage_notifications)) })
                                }
                                DropdownMenuItem(onClick = {
                                    model.findAvatar { avatarId ->
                                        if (profile.profilePicOverride.isNotEmpty()) {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.profile_user_avatar_unreachable),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@findAvatar Unit
                                        }
                                        if (avatarId == null) {
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
                                DropdownMenuItem(onClick = {
                                    navigator.push(UserGroupsScreen(profile.displayName, profile.id))
                                    isMenuExpanded = false
                                },
                                    text = { Text(stringResource(R.string.user_dropdown_view_groups)) }
                                )
                                DropdownMenuItem(onClick = {
                                    navigator.push(WorldsScreen(profile.displayName, profile.id, false))
                                    isMenuExpanded = false
                                },
                                    text = { Text(stringResource(R.string.user_dropdown_view_worlds)) }
                                )
                                if (instance != null) {
                                    DropdownMenuItem(onClick = {

                                        model.inviteToFriend(profile.location)
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.profile_user_toast_invite_sent),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        isMenuExpanded = false
                                    },
                                        text = { Text(stringResource(R.string.profile_user_dropdown_invite_self)) })
                                }
                                if (profile.isFriend) {
                                    if (FavoriteManager.isFavorite("friend", profile.id)) {
                                        DropdownMenuItem(onClick = {
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
                                            text = { Text(stringResource(R.string.favorite_label_remove)) })
                                    } else {
                                        DropdownMenuItem(onClick = {
                                            favoriteDialogShown = true
                                            isMenuExpanded = false
                                        },
                                            text = { Text(stringResource(R.string.favorite_label_add)) })
                                    }
                                }
                                DropdownMenuItem(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText(null, profile.id)
                                        clipboard.setPrimaryClip(clip)

                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.copied_toast).format(profile.
                                            displayName),
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        isMenuExpanded = false
                                    },
                                    text = { Text(stringResource(R.string.copy_id_label)) }
                                )
                            }
                        }
                    }
                }, title = {
                    Text(
                        text = profile.displayName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                })
            }, content = { padding ->

                if (favoriteDialogShown) {
                    FavoriteDialog(type = IFavorites.FavoriteType.FAVORITE_FRIEND,
                        id = profile.id,
                        metadata = FavoriteManager.FavoriteMetadata(
                            profile.id, "", profile.displayName, ""
                        ),
                        onDismiss = { favoriteDialogShown = false },
                        onConfirmation = { favoriteDialogShown = false })
                }

                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                        .padding(
                            top = padding.calculateTopPadding(),
                            bottom = padding.calculateBottomPadding()
                        ),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        profile.let {
                            ProfileCard(
                                thumbnailUrl = it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl },
                                iconUrl = it.userIcon.ifEmpty { it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl } },
                                displayName = it.displayName,
                                statusDescription = it.statusDescription.ifEmpty {
                                    StatusHelper.getStatusFromString(it.status).toString()
                                },
                                trustRankColor = TrustHelper.getTrustRankFromTags(it.tags)
                                    .toColor(),
                                statusColor = StatusHelper.getStatusFromString(it.status)
                                    .toColor(),
                                tags = profile.tags,
                                badges = profile.badges
                            )
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

                    if (profile.note.isNotEmpty()) {
                        item {
                            Column(
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.Start
                            ) {
                                ElevatedCard(
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 6.dp
                                    ),
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                        .defaultMinSize(minHeight = 80.dp)
                                        .widthIn(Dp.Unspecified, 520.dp),
                                ) {
                                    SubHeader(title = stringResource(R.string.profile_label_note))
                                    Description(text = profile.note)
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
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .defaultMinSize(minHeight = 80.dp)
                                    .widthIn(Dp.Unspecified, 520.dp),
                            ) {
                                SubHeader(title = stringResource(R.string.profile_label_biography))
                                Description(text = profile.bio)
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
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .defaultMinSize(minHeight = 80.dp)
                                    .widthIn(Dp.Unspecified, 520.dp),
                            ) {
                                if (profile.lastActivity.isNotEmpty()) {
                                    val userTimeZone = TimeZone.getDefault().toZoneId()
                                    val formatter = DateTimeFormatter.ofLocalizedDateTime(java.time.format.FormatStyle.SHORT)
                                        .withLocale(Locale.getDefault())

                                    val lastActivity = ZonedDateTime.parse(profile.lastActivity).withZoneSameInstant(userTimeZone).format(formatter)

                                    SubHeader(title = stringResource(R.string.profile_label_last_activity))
                                    Description(text = lastActivity)
                                }

                                SubHeader(title = stringResource(R.string.profile_label_date_joined))
                                Description(text = profile.dateJoined)
                            }
                        }
                    }
                }
            })
        }
    }
}
/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.ui.screen.profile

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites
import cc.sovellus.vrcaa.api.vrchat.http.models.Instance
import cc.sovellus.vrcaa.api.vrchat.http.models.LimitedUser
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.helper.TrustHelper
import cc.sovellus.vrcaa.manager.FavoriteManager
import cc.sovellus.vrcaa.ui.components.card.InstanceCard
import cc.sovellus.vrcaa.ui.components.card.ProfileCard
import cc.sovellus.vrcaa.ui.components.card.QuickMenuCard
import cc.sovellus.vrcaa.ui.components.dialog.FavoriteDialog
import cc.sovellus.vrcaa.ui.components.misc.Description
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import cc.sovellus.vrcaa.ui.screen.avatar.AvatarScreen
import cc.sovellus.vrcaa.ui.screen.group.UserGroupsScreen
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.notification.NotificationScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldScreen
import cc.sovellus.vrcaa.ui.screen.worlds.WorldsScreen
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

class UserProfileScreen(
    private val userId: String,
    private val peek: Boolean = false
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val model = rememberScreenModel { UserProfileScreenModel(userId) }
        val state by model.state.collectAsState()

        when (val result = state) {
            is UserProfileScreenModel.UserProfileState.Loading -> LoadingIndicatorScreen().Content()
            is UserProfileScreenModel.UserProfileState.Failure -> HandleFailure()
            is UserProfileScreenModel.UserProfileState.Result -> Profile(
                result.profile, result.instance, model
            )

            else -> {}
        }
    }

    @Composable
    private fun HandleFailure() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        Toast.makeText(
            context,
            stringResource(R.string.profile_user_not_found_message),
            Toast.LENGTH_SHORT
        ).show()

        if (peek) {
            if (context is Activity) {
                context.finish()
            }
        } else {
            navigator.pop()
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

        var favoriteDialogShown by remember { mutableStateOf(false) }
        var isQuickMenuExpanded by remember { mutableStateOf(false) }

        if (profile == null) {
            Toast.makeText(
                context, stringResource(R.string.profile_user_not_found_message), Toast.LENGTH_SHORT
            ).show()

            if (peek) {
                if (context is Activity) {
                    context.finish()
                }
            } else {
                navigator.pop()
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                isQuickMenuExpanded = false
                            },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                        .blur(
                            if (isQuickMenuExpanded) {
                                100.dp
                            } else {
                                0.dp
                            }
                        ),
                    topBar = {
                        TopAppBar(navigationIcon = {
                            IconButton(onClick = {
                                if (peek) {
                                    if (context is Activity) {
                                        context.finish()
                                    }
                                } else {
                                    navigator.pop()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }, actions = {
                            IconButton(onClick = { isQuickMenuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert, contentDescription = null
                                )
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
                            FavoriteDialog(
                                type = IFavorites.FavoriteType.FAVORITE_FRIEND,
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
                                        badges = profile.badges,
                                        pronouns = profile.pronouns,
                                        ageVerificationStatus = profile.ageVerificationStatus
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
                                        InstanceCard(profile = profile, clickable = !isQuickMenuExpanded, instance = instance) {
                                            navigator.push(WorldScreen(instance.worldId))
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
                                        if (profile.note.isNotEmpty()) {
                                            SubHeader(title = stringResource(R.string.profile_label_note))
                                            Description(text = profile.note)
                                        }

                                        SubHeader(title = stringResource(R.string.profile_label_biography))
                                        Description(text = profile.bio)

                                        if (profile.lastActivity.isNotEmpty()) {
                                            val userTimeZone = TimeZone.getDefault().toZoneId()
                                            val formatter =
                                                DateTimeFormatter.ofLocalizedDateTime(java.time.format.FormatStyle.SHORT)
                                                    .withLocale(Locale.getDefault())

                                            val lastActivity =
                                                ZonedDateTime.parse(profile.lastActivity)
                                                    .withZoneSameInstant(userTimeZone)
                                                    .format(formatter)

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
                AnimatedVisibility(
                    visible = isQuickMenuExpanded,
                    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                    modifier = Modifier
                        .systemBarsPadding()
                        .navigationBarsPadding()
                        .align(Alignment.TopEnd)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxHeight()
                            .clip(
                                RoundedCornerShape(
                                    topStart = 10.dp,
                                    bottomStart = 10.dp,
                                    topEnd = 0.dp,
                                    bottomEnd = 0.dp
                                )
                            )
                            .fillMaxWidth(0.7f)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .zIndex(1f),
                        shadowElevation = 8.dp
                    ) {
                        LazyColumn {
                            item {
                                profile.let {
                                    QuickMenuCard(
                                        thumbnailUrl = it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl },
                                        iconUrl = it.userIcon.ifEmpty { it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl } },
                                        displayName = it.displayName,
                                        statusDescription = it.statusDescription.ifEmpty {
                                            StatusHelper.getStatusFromString(
                                                it.status
                                            ).toString()
                                        },
                                        trustRankColor = TrustHelper.getTrustRankFromTags(it.tags)
                                            .toColor(),
                                        statusColor = StatusHelper.getStatusFromString(it.status)
                                            .toColor(),
                                        tags = it.tags,
                                        badges = it.badges
                                    )
                                }
                            }

                            item {
                                Column(
                                    modifier = Modifier.padding(
                                        start = 8.dp,
                                        end = 8.dp,
                                        top = 16.dp,
                                        bottom = 16.dp
                                    )
                                ) {

                                    val options: MutableList<String> = mutableListOf<String>()
                                    val icons: MutableList<ImageVector> =
                                        mutableListOf<ImageVector>()

                                    var notificationIndex = -1
                                    var favoriteIndex = -1
                                    var avatarIndex = -1
                                    var worldsIndex = -1
                                    var groupsIndex = -1
                                    var favoritesIndex = -1
                                    var copyIndex = -1

                                    if (profile.isFriend) {
                                        options.add(stringResource(R.string.profile_user_dropdown_manage_notifications))
                                        icons.add(Icons.Default.NotificationsActive)
                                        notificationIndex = options.size - 1
                                    }

                                    if (profile.isFriend) {
                                        if (FavoriteManager.isFavorite("friend", profile.id)) {
                                            options.add(stringResource(R.string.favorite_label_remove))
                                            icons.add(Icons.Default.Star)
                                            favoriteIndex = options.size - 1
                                        } else {
                                            options.add(stringResource(R.string.favorite_label_add))
                                            icons.add(Icons.Default.Star)
                                            favoriteIndex = options.size - 1
                                        }
                                    }

                                    options.add(stringResource(R.string.user_overlay_find_avatar))
                                    icons.add(Icons.Default.Person)
                                    avatarIndex = options.size - 1

                                    options.add(stringResource(R.string.user_overlay_worlds))
                                    icons.add(Icons.Default.Cabin)
                                    worldsIndex = options.size - 1

                                    options.add(stringResource(R.string.user_overlay_groups))
                                    icons.add(Icons.Default.Group)
                                    groupsIndex = options.size - 1

                                    options.add(stringResource(R.string.user_overlay_favorites))
                                    icons.add(Icons.Default.Star)
                                    favoritesIndex = options.size - 1

                                    options.add(stringResource(R.string.copy_id_label))
                                    icons.add(Icons.Default.ContentCopy)
                                    copyIndex = options.size - 1

                                    options.forEachIndexed { index, label ->

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp, horizontal = 4.dp)
                                                .clip(RoundedCornerShape(80))
                                                .background(
                                                    MaterialTheme.colorScheme.secondary.copy(
                                                        alpha = 0.12f
                                                    )
                                                )
                                                .clickable(onClick = {
                                                    when (index) {
                                                        notificationIndex -> {
                                                            navigator.push(
                                                                NotificationScreen(
                                                                    profile.id,
                                                                    profile.displayName
                                                                )
                                                            )
                                                        }

                                                        favoriteIndex -> {
                                                            if (FavoriteManager.isFavorite(
                                                                    "friend",
                                                                    profile.id
                                                                )
                                                            ) {
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
                                                            } else {
                                                                favoriteDialogShown = true
                                                            }
                                                        }

                                                        avatarIndex -> {
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
                                                        }

                                                        worldsIndex -> {
                                                            navigator.push(
                                                                WorldsScreen(
                                                                    profile.displayName,
                                                                    profile.id,
                                                                    false
                                                                )
                                                            )
                                                        }

                                                        groupsIndex -> {
                                                            navigator.push(
                                                                UserGroupsScreen(
                                                                    profile.displayName,
                                                                    profile.id
                                                                )
                                                            )
                                                        }

                                                        favoritesIndex -> {

                                                        }

                                                        copyIndex -> {
                                                            val clipboard =
                                                                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                            val clip =
                                                                ClipData.newPlainText(
                                                                    null,
                                                                    profile.id
                                                                )
                                                            clipboard.setPrimaryClip(clip)

                                                            Toast.makeText(
                                                                context,
                                                                context.getString(R.string.copied_toast)
                                                                    .format(
                                                                        profile.displayName
                                                                    ),
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                    isQuickMenuExpanded = false
                                                })
                                                .padding(vertical = 16.dp, horizontal = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = icons[index],
                                                contentDescription = null
                                            )

                                            Text(
                                                text = label,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.padding(start = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
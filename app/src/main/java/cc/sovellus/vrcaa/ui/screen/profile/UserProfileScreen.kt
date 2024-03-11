package cc.sovellus.vrcaa.ui.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.helper.TrustHelper
import cc.sovellus.vrcaa.api.http.models.Instance
import cc.sovellus.vrcaa.api.http.models.LimitedUser
import cc.sovellus.vrcaa.ui.components.misc.Description
import cc.sovellus.vrcaa.ui.components.card.InstanceCard
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.notifications.ManageNotificationsScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreenModel.UserProfileState
import cc.sovellus.vrcaa.ui.components.misc.Languages
import cc.sovellus.vrcaa.ui.components.card.ProfileCard
import cc.sovellus.vrcaa.ui.screen.world.WorldInfoScreen

class UserProfileScreen(
    private val userId: String
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val context = LocalContext.current

        val model = rememberScreenModel { UserProfileScreenModel(context, userId) }

        val state by model.state.collectAsState()

        when (val result = state) {
            is UserProfileState.Loading -> LoadingIndicatorScreen().Content()
            is UserProfileState.Result -> RenderProfile(result.profile, result.instance, model)
            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun RenderProfile(profile: LimitedUser, instance: Instance?, model: UserProfileScreenModel) {

        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.preview_image_description)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { model.isMenuExpanded.value = true }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = stringResource(R.string.preview_image_description)
                            )

                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                DropdownMenu(
                                    expanded = model.isMenuExpanded.value,
                                    onDismissRequest = { model.isMenuExpanded.value = false },
                                    offset = DpOffset(0.dp, 0.dp)
                                ) {
                                    if (profile.isFriend) {
                                        DropdownMenuItem(
                                            onClick = {
                                                navigator.push(
                                                    ManageNotificationsScreen(
                                                        profile.id,
                                                        profile.displayName
                                                    )
                                                )
                                                model.isMenuExpanded.value = false
                                            },
                                            text = { Text("Manage notifications") }
                                        )
                                    }
                                }
                            }
                        }
                    },
                    title = { Text(text = profile.displayName) }
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
                            profile.let {
                                ProfileCard(
                                    thumbnailUrl = it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl },
                                    displayName = it.displayName,
                                    statusDescription = it.statusDescription.ifEmpty {
                                        StatusHelper.Status.toString(
                                            StatusHelper.getStatusFromString(it.status)
                                        )
                                    },
                                    trustRankColor = TrustHelper.Rank.toColor(
                                        TrustHelper.getTrustRankFromTags(
                                            it.tags
                                        )
                                    ),
                                    statusColor = StatusHelper.Status.toColor(
                                        StatusHelper.getStatusFromString(
                                            it.status
                                        )
                                    )
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
                            if (instance != null) {
                                InstanceCard(profile = profile, instance = instance) {
                                    navigator.push(WorldInfoScreen(instance.worldId))
                                }
                            }

                            SubHeader(title = stringResource(R.string.profile_label_biography))
                            Description(text = profile.bio)

                            SubHeader(title = stringResource(R.string.profile_label_languages))
                            Languages(languages = profile.tags)
                        }
                    }
                }
            }
        )
    }
}
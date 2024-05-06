package cc.sovellus.vrcaa.ui.screen.profile

import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.User
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.helper.TrustHelper
import cc.sovellus.vrcaa.ui.components.card.ProfileCard
import cc.sovellus.vrcaa.ui.components.dialog.ProfileEditDialog
import cc.sovellus.vrcaa.ui.components.misc.Description
import cc.sovellus.vrcaa.ui.components.misc.Languages
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import cc.sovellus.vrcaa.ui.models.profile.ProfileModel
import cc.sovellus.vrcaa.ui.models.profile.ProfileModel.ProfileState
import cc.sovellus.vrcaa.ui.screen.group.UserGroupsScreen
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import kotlinx.coroutines.launch

class ProfileScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val context = LocalContext.current

        val model = rememberScreenModel { ProfileModel(context) }

        val state by model.state.collectAsState()

        when (val result = state) {
            is ProfileState.Loading -> LoadingIndicatorScreen().Content()
            is ProfileState.Result -> RenderProfile(result.profile, model)
            else -> {}
        }
    }

    @Composable
    private fun RenderProfile(profile: User?, model: ProfileModel) {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        var isMenuExpanded by remember { mutableStateOf(false) }
        var isEditingProfile by remember { mutableStateOf(false) }

        if (profile == null) {
            Toast.makeText(
                context,
                stringResource(R.string.profile_user_not_found_message),
                Toast.LENGTH_SHORT
            ).show()
            navigator.pop()
        } else {

            if (isEditingProfile) {
                ProfileEditDialog(
                    onDismiss = { isEditingProfile = false },
                    onConfirmation = {
                        isEditingProfile = false
                        model.updateProfile()
                    },
                    title = stringResource(R.string.profile_edit_dialog_title_edit_profile)
                )
            }

            LazyColumn {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        profile.let {
                            Box(
                                modifier = Modifier
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = {
                                                isMenuExpanded = true
                                            }
                                        )
                                    }
                            ) {
                                ProfileCard(
                                    thumbnailUrl = it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl },
                                    displayName = it.displayName,
                                    statusDescription = it.statusDescription,
                                    trustRankColor = TrustHelper.getTrustRankFromTags(it.tags).toColor(),
                                    statusColor = StatusHelper.getStatusFromString(it.status).toColor(),
                                )

                                DropdownMenu(
                                    expanded = isMenuExpanded,
                                    onDismissRequest = { isMenuExpanded = false },
                                    offset = DpOffset(24.dp, 0.dp)
                                ) {
                                    DropdownMenuItem(
                                        onClick = {
                                            navigator.parent?.parent?.push(
                                                UserGroupsScreen(profile.displayName, profile.id)
                                            )
                                            isMenuExpanded = false
                                        },
                                        text = { Text(stringResource(R.string.user_dropdown_view_groups)) }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            isMenuExpanded = false
                                            isEditingProfile = true
                                        },
                                        text = { Text(stringResource(R.string.profile_edit_dialog_title_edit_profile)) }
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.Start
                    ) {
                        ElevatedCard(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 6.dp
                            ),
                            modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth().defaultMinSize(minHeight = 80.dp),
                        ) {
                            SubHeader(title = stringResource(R.string.profile_label_biography))
                            Description(text = profile.bio)
                        }

                        ElevatedCard(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 6.dp
                            ),
                            modifier = Modifier.padding(bottom = 16.dp).height(70.dp).fillMaxWidth(),
                        ) {
                            SubHeader(title = stringResource(R.string.profile_label_languages))
                            Languages(languages = profile.tags)
                        }
                    }
                }
            }
        }
    }
}
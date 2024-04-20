package cc.sovellus.vrcaa.ui.screen.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import cc.sovellus.vrcaa.ui.components.misc.Description
import cc.sovellus.vrcaa.ui.components.misc.Languages
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import cc.sovellus.vrcaa.ui.models.profile.ProfileModel
import cc.sovellus.vrcaa.ui.models.profile.ProfileModel.ProfileState
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen

class ProfileScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val context = LocalContext.current

        val model = rememberScreenModel { ProfileModel(context) }

        val state by model.state.collectAsState()

        when (val result = state) {
            is ProfileState.Loading -> LoadingIndicatorScreen().Content()
            is ProfileState.Result -> RenderProfile((result.profile))
            else -> {}
        }
    }

    @Composable
    private fun RenderProfile(profile: User?) {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        if (profile == null) {
            Toast.makeText(
                context,
                stringResource(R.string.profile_user_not_found_message),
                Toast.LENGTH_SHORT
            ).show()
            navigator.pop()
        } else {
            LazyColumn {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        profile.let {
                            ProfileCard(
                                thumbnailUrl = it.profilePicOverride.ifEmpty { it.currentAvatarThumbnailImageUrl },
                                displayName = it.displayName,
                                statusDescription = it.statusDescription,
                                trustRankColor = TrustHelper.getTrustRankFromTags(it.tags).toColor(),
                                statusColor = StatusHelper.getStatusFromString(it.status).toColor(),
                                userId = it.id
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
                        SubHeader(title = stringResource(R.string.profile_label_biography))
                        Description(text = profile.bio)

                        SubHeader(title = stringResource(R.string.profile_label_languages))
                        Languages(languages = profile.tags)
                    }
                }
            }
        }
    }
}
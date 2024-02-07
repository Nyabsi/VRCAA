package cc.sovellus.vrcaa.ui.screen.profile

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
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.helper.StatusHelper
import cc.sovellus.vrcaa.api.helper.TrustHelper
import cc.sovellus.vrcaa.api.models.User
import cc.sovellus.vrcaa.ui.components.Description
import cc.sovellus.vrcaa.ui.components.SubHeader
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.profile.ProfileScreenModel.ProfileState
import cc.sovellus.vrcaa.ui.screen.profile.components.Languages
import cc.sovellus.vrcaa.ui.screen.profile.components.ProfileCard

class ProfileScreen : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        
        val context = LocalContext.current

        // don't store it in the `Navigator` because it *may* change, this is not a proper way to handle it either,
        // we really should just store a global `synchronized` variable in `apiContext` that contains the current `User` state.
        val model = rememberScreenModel { ProfileScreenModel(context) }

        val state by model.state.collectAsState()

        when (val result = state) {
            is ProfileState.Loading -> LoadingIndicatorScreen().Content()
            is ProfileState.Result -> RenderProfile((result.profile))
            else -> {}
        }
    }

    @Composable
    private fun RenderProfile(profile: User) {
        LazyColumn {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    profile.let {
                        ProfileCard(
                            thumbnailUrl = it.currentAvatarThumbnailImageUrl,
                            displayName = it.displayName,
                            statusDescription = it.statusDescription,
                            trustRankColor = TrustHelper.Rank.toColor(TrustHelper.getTrustRankFromTags(it.tags)),
                            statusColor = StatusHelper.Status.toColor(StatusHelper.getStatusFromString(it.status))
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
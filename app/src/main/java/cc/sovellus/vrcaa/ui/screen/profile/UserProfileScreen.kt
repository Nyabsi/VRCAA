package cc.sovellus.vrcaa.ui.screen.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Badge
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.helper.LocationHelper
import cc.sovellus.vrcaa.api.helper.StatusHelper
import cc.sovellus.vrcaa.api.helper.TrustHelper
import cc.sovellus.vrcaa.api.models.Instance
import cc.sovellus.vrcaa.api.models.LimitedUser
import cc.sovellus.vrcaa.ui.components.Description
import cc.sovellus.vrcaa.ui.components.SubHeader
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.notifications.ManageNotificationsScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreenModel.UserProfileState
import cc.sovellus.vrcaa.ui.screen.profile.components.Languages
import cc.sovellus.vrcaa.ui.screen.profile.components.ProfileCard
import cc.sovellus.vrcaa.ui.screen.world.WorldInfoScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

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

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
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
                                SubHeader(title = stringResource(id = R.string.profile_label_current_location))
                                ElevatedCard(
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 6.dp
                                    ),
                                    modifier = Modifier
                                        .height(220.dp)
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .clickable(
                                            onClick = {
                                                navigator.push(WorldInfoScreen(instance.worldId))
                                            }
                                        )
                                ) {

                                    GlideImage(
                                        model = instance.world.imageUrl,
                                        contentDescription = stringResource(R.string.preview_image_description),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp),
                                        contentScale = ContentScale.Crop,
                                        loading = placeholder(R.drawable.image_placeholder),
                                        failure = placeholder(R.drawable.image_placeholder)
                                    )

                                    val result =
                                        LocationHelper.parseLocationIntent(profile.location)

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Start,
                                        modifier = Modifier.padding(4.dp)
                                    ) {
                                        Text(
                                            text = "${instance.world.name} (${instance.name}) ${result.instanceType} ${instance.nUsers}/${instance.world.capacity}",
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Left,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (result.regionId.isNotEmpty()) {
                                            when (result.regionId.lowercase()) {
                                                "eu" -> Image(
                                                    painter = painterResource(R.drawable.flag_eu),
                                                    modifier = Modifier.padding(start = 2.dp),
                                                    contentDescription = "Region flag"
                                                )
                                                "jp" -> Image(
                                                    painter = painterResource(R.drawable.flag_jp),
                                                    modifier = Modifier.padding(start = 2.dp),
                                                    contentDescription = "Region flag"
                                                )
                                                "us" -> Image(
                                                    painter = painterResource(R.drawable.flag_us),
                                                    modifier = Modifier.padding(start = 2.dp),
                                                    contentDescription = "Region flag"
                                                )
                                                "use" -> Image(
                                                    painter = painterResource(R.drawable.flag_us),
                                                    modifier = Modifier.padding(start = 2.dp),
                                                    contentDescription = "Region flag"
                                                )
                                                "usw" -> Image(
                                                    painter = painterResource(R.drawable.flag_us),
                                                    modifier = Modifier.padding(start = 2.dp),
                                                    contentDescription = "Region flag"
                                                )
                                            }
                                        } else {
                                            Image(
                                                painter = painterResource(R.drawable.flag_us),
                                                modifier = Modifier.padding(start = 2.dp),
                                                contentDescription = "Region flag",
                                            )
                                        }
                                    }
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
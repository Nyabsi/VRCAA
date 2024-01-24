package cc.sovellus.vrcaa.ui.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.helper.StatusHelper
import cc.sovellus.vrcaa.api.helper.TrustHelper
import cc.sovellus.vrcaa.api.models.Users
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

class UserProfileScreen(
    private val user: Users.UsersItem
): Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

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

                    title = { Text(text = user.displayName) }
                )
            },
            content = { padding ->
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(top = padding.calculateTopPadding()),
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            user.let {
                                ProfileCard(
                                    thumbnailUrl = it.profilePicOverride.ifEmpty { it.currentAvatarImageUrl },
                                    displayName = it.displayName,
                                    statusDescription = it.statusDescription.ifEmpty { StatusHelper.Status.toString(
                                        StatusHelper().getStatusFromString(it.status)) },
                                    trustRankColor = TrustHelper.Rank.toColor(TrustHelper().getTrustRankFromTags(it.tags)),
                                    statusColor = StatusHelper.Status.toColor(StatusHelper().getStatusFromString(it.status))
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
                            Description(text = user.bio)

                            SubHeader(title = stringResource(R.string.profile_label_languages))
                            Languages(languages = user.tags)
                        }
                    }
                }
            }
        )
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun ProfileCard(
        thumbnailUrl: String,
        displayName: String,
        statusDescription: String,
        trustRankColor: Color,
        statusColor: Color
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .height(320.dp)
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            GlideImage(
                model = thumbnailUrl,
                contentDescription = stringResource(R.string.preview_image_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = displayName,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                color = trustRankColor
            )

            Row(
                modifier = Modifier.padding(start = 12.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Badge(containerColor = statusColor, modifier = Modifier.size(16.dp))
                Text(
                    modifier = Modifier.padding(start = 12.dp),
                    text = statusDescription,
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }

    @Composable
    fun SubHeader(title: String) {
        Text(
            modifier = Modifier.padding(start = 24.dp),
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
            overflow = TextOverflow.Ellipsis
        )
    }

    @Composable
    fun Description(text: String?) {
        Column(
            modifier = Modifier
                .padding(24.dp)
        ) {
            Text(
                modifier = Modifier.padding(start = 2.dp),
                text = if (text.isNullOrEmpty()) { stringResource(R.string.profile_text_no_biography) } else { text },
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    @Composable
    fun Languages(languages: List<String>) {
        Row(
            modifier = Modifier.padding(24.dp)
        ) {
            if (languages.isEmpty()) {
                Text(stringResource(R.string.profile_text_no_languages))
            } else {
                languages.let {
                    for (language in languages) {
                        if (language.contains("language_")) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .height(height = 24.dp)
                                    .padding(start = 2.dp),
                                content = { Text( text = language.substring("language_".length).uppercase() ) }
                            )
                        }
                    }
                }
            }
        }
    }
}
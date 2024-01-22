package cc.sovellus.vrcaa.ui.screen.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.models.Friends
import cc.sovellus.vrcaa.api.utils.StatusUtils
import cc.sovellus.vrcaa.api.utils.TrustRank
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

class FriendProfileScreen(
    private val friend: Friends.FriendsItem
) : Screen {

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
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Go Back"
                            )
                        }
                    },

                    title = { Text(text = friend.displayName) }
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
                            friend.let {
                                ProfileCard(
                                    thumbnailUrl = it.imageUrl,
                                    displayName = it.displayName,
                                    statusDescription = it.statusDescription.ifEmpty { StatusUtils.Status.toString(StatusUtils().getStatusFromString(it.status)) },
                                    trustRankColor = TrustRank.Rank.toColor(TrustRank().getTrustRankFromTags(it.tags)),
                                    statusColor = StatusUtils.Status.toColor(StatusUtils().getStatusFromString(it.status))
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
                            SubHeader(title = "Bio")
                            Description(text = friend.bio)

                            SubHeader(title = "Languages")
                            val list: List<String> = listOf("Finnish", "Swedish", "Chinese")
                            Languages(languages = list)
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
                contentDescription = "Profile Picture",
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
    fun Description(text: String) {
        Column(
            modifier = Modifier
                .padding(24.dp)
        ) {
            Text(
                modifier = Modifier.padding(start = 2.dp),
                text = text,
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    @SuppressLint("ResourceAsColor")
    @Composable
    fun Languages(languages: List<String>) {
        Row(
            modifier = Modifier.padding(24.dp)
        ) {
            languages.let {
                for (language in languages) {
                    Badge(
                        containerColor = Color(R.color.accent),
                        modifier = Modifier
                            .size(width = 64.dp, height = 24.dp)
                            .padding(start = 2.dp),
                        content = { Text( text = language ) }
                    )
                }
            }
        }
    }
}
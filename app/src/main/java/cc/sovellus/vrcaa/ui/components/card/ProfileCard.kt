package cc.sovellus.vrcaa.ui.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.ui.components.misc.Languages
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileCard(
    thumbnailUrl: String,
    iconUrl: String,
    displayName: String,
    statusDescription: String,
    trustRankColor: Color,
    statusColor: Color,
    tags: List<String>
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .height(270.dp)
            .fillMaxWidth()
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy((-50).dp),
            modifier = Modifier.height(270.dp).fillMaxSize()
        ) {
            item {
                GlideImage(
                    model = thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop,
                    loading = placeholder(R.drawable.image_placeholder),
                    failure = placeholder(R.drawable.image_placeholder)
                )
            }

            item {
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        GlideImage(
                            model = iconUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(50)),
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                            loading = placeholder(R.drawable.icon_placeholder),
                            failure = placeholder(R.drawable.icon_placeholder)
                        )
                    }
                }
            }

            item {
                Row {
                    Text(
                        text = displayName,
                        modifier = Modifier.padding(start = 12.dp, top = 60.dp).weight(0.70f),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Left,
                        color = trustRankColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().weight(0.30f).padding(end = 4.dp), horizontalArrangement = Arrangement.End
                    ) {
                        Languages(languages = tags, modifier = Modifier.padding(top = 20.dp))
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.padding(start = 12.dp, top = 50.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Badge(containerColor = statusColor, modifier = Modifier.size(16.dp))
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = statusDescription,
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.SemiBold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
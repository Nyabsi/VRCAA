package cc.sovellus.vrcaa.ui.components.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.ui.components.dialog.ProfileEditDialog
import cc.sovellus.vrcaa.ui.screen.group.UserGroupsScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileCard(
    thumbnailUrl: String,
    displayName: String,
    statusDescription: String,
    trustRankColor: Color,
    statusColor: Color,
) {
    val navigator = LocalNavigator.currentOrThrow

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .height(280.dp)
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        GlideImage(
            model = thumbnailUrl,
            contentDescription = stringResource(R.string.preview_image_description),
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            contentScale = ContentScale.Crop,
            loading = placeholder(R.drawable.image_placeholder),
            failure = placeholder(R.drawable.image_placeholder)
        )

        Text(
            text = displayName,
            modifier = Modifier.padding(start = 12.dp, top = 4.dp),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
            color = trustRankColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
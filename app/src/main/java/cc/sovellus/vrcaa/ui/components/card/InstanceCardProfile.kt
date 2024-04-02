package cc.sovellus.vrcaa.ui.components.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Instance
import cc.sovellus.vrcaa.api.vrchat.http.models.LimitedUser
import cc.sovellus.vrcaa.helper.LocationHelper
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun InstanceCardProfile(profile: LimitedUser, instance: Instance, callback: () -> Unit) {
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
                    callback()
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

        val result = LocationHelper.parseLocationInfo(profile.location!!)

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
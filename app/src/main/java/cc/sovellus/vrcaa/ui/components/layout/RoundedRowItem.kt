package cc.sovellus.vrcaa.ui.components.layout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RoundedRowItem(
    name: String,
    url: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            onClick = { onClick() }
        )
    ) {
        GlideImage(
            model = url,
            contentDescription = stringResource(R.string.preview_image_description),
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(50)),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            loading = placeholder(R.drawable.icon_placeholder),
            failure = placeholder(R.drawable.icon_placeholder)
        )
        Text(text = name)
    }
}
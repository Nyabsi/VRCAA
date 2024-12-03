package cc.sovellus.vrcaa.ui.components.misc

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R

@Composable
fun Logo(size: Dp) {
    Box(
        modifier = Modifier
            .height(size)
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = if (App.isAppInDarkTheme()) {
                painterResource(R.drawable.logo_dark)
            } else {
                painterResource(R.drawable.logo_white)
            },
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            alignment = Alignment.Center
        )
    }
}
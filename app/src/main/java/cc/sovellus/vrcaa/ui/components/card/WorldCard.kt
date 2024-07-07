package cc.sovellus.vrcaa.ui.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Badge
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.UnityPackage
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WorldCard(
    url: String,
    name: String,
    author: String,
    packages: List<UnityPackage>
) {
    var foundWindows by remember { mutableStateOf(false) }
    var foundAndroid by remember { mutableStateOf(false) }
    var foundDarwin by remember { mutableStateOf(false) }

    LaunchedEffect(packages) {
        packages.forEach {
            when (it.platform) {
                "android" -> {
                    foundAndroid = true
                }
                "standalonewindows" -> {
                    foundWindows = true
                }
                "ios" -> {
                    foundDarwin = true
                }
            }
        }
    }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .height(240.dp)
            .fillMaxWidth()
    ) {

        LazyColumn(
            modifier = Modifier
                .height(240.dp)
                .fillMaxSize()
        ) {
            item {
                GlideImage(
                    model = url,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop,
                    loading = placeholder(R.drawable.image_placeholder),
                    failure = placeholder(R.drawable.image_placeholder)
                )
            }

            item {
                Row {
                    Text(
                        text = name,
                        modifier = Modifier
                            .padding(start = 8.dp, top = 4.dp)
                            .weight(0.50f),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Left,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.50f)
                            .padding(end = 4.dp), horizontalArrangement = Arrangement.End
                    ) {
                        if (foundWindows) {
                            Badge(
                                containerColor = Color(0, 168, 252),
                                modifier = Modifier
                                    .height(height = 26.dp)
                                    .padding(start = 2.dp, top = 8.dp),
                                content = {
                                    Text(
                                        text = "Windows"
                                    )
                                }
                            )
                        }

                        if (foundAndroid) {
                            Badge(
                                containerColor = Color(103, 215, 129),
                                modifier = Modifier
                                    .height(height = 26.dp)
                                    .padding(start = 2.dp, top = 8.dp),
                                content = {
                                    Text(
                                        text = "Android"
                                    )
                                }
                            )
                        }

                        if (foundDarwin) {
                            Badge(
                                containerColor = Color(121, 136, 151),
                                modifier = Modifier
                                    .height(height = 26.dp)
                                    .padding(start = 2.dp, top = 8.dp),
                                content = {
                                    Text(
                                        text = "iOS"
                                    )
                                }
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = author,
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}
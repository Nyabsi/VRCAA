package cc.sovellus.vrcaa.ui.screen.pictures

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cc.sovellus.vrcaa.R
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import java.io.File

class PicturesScreen : Screen {

    override val key = uniqueScreenKey

    @SuppressLint("SdCardPath")
    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    override fun Content() {
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(4.dp),
            state = rememberLazyListState()
        ) {
            File("/sdcard/Pictures/VRChat/").walkTopDown().forEach { path ->
                if (path.isFile) {
                    item {
                        Text(text = path.name)
                        GlideImage(
                            model = path.absolutePath,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillBounds,
                            loading = placeholder(R.drawable.image_placeholder),
                            failure = placeholder(R.drawable.image_placeholder)
                        )
                    }
                }
            }
        }
    }
}
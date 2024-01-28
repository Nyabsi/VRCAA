package cc.sovellus.vrcaa.ui.screen.avatar

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import cc.sovellus.vrcaa.api.models.Avatar
import cc.sovellus.vrcaa.ui.screen.avatar.AvatarViewScreenModel.AvatarState
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import java.text.SimpleDateFormat
import java.util.Locale

class AvatarViewScreen(
    private val avatarId: String
) : Screen {

    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val context = LocalContext.current

        val model = rememberScreenModel { AvatarViewScreenModel(context, avatarId) }

        val state by model.state.collectAsState()

        when (val result = state) {
            is AvatarState.Loading -> LoadingIndicatorScreen().Content()
            is AvatarState.Result -> DisplayResult(result.avatar, model)
            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun DisplayResult(avatar: Avatar?, model: AvatarViewScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

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
                                    if (avatar != null) {
                                        DropdownMenuItem(
                                            onClick = {
                                                model.selectAvatar(model.avatar!!.id)

                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.avatar_dropdown_toast_select_avatar),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            text = { Text(stringResource(R.string.avatar_dropdown_label_select)) }
                                        )
                                        // TODO: when implementing favorites, check here if it's favorited or not.
                                        DropdownMenuItem(
                                            onClick = {
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.avatar_dropdown_toast_not_implemented),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            text = { Text(stringResource(R.string.avatar_dropdown_label_favorite)) }
                                        )
                                    }
                                }
                            }
                        }
                    },
                    title = {
                        if (avatar != null) {
                            Text(text = avatar.name)
                        } else {
                            Text(text = "Invalid Avatar")
                        }
                    }
                )
            },
            content = { padding ->
                if (avatar != null) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = padding.calculateTopPadding()),
                    ) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                avatar.let {
                                    AvatarCard(
                                        thumbnailUrl = it.imageUrl,
                                        name = it.name,
                                        authorName = it.authorName
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
                                SubHeader(title = stringResource(R.string.avatar_title_description))
                                Description(text = avatar.description)

                                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                                val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)

                                val createdAtFormatted = parser.parse(avatar.createdAt)
                                    ?.let { formatter.format(it) }

                                val updatedAtFormatted = parser.parse(avatar.updatedAt)
                                    ?.let { formatter.format(it) }

                                SubHeader(title = stringResource(R.string.avatar_title_created_at))
                                Description(text = createdAtFormatted)

                                SubHeader(title = stringResource(R.string.avatar_title_updated_at))
                                Description(text = updatedAtFormatted)

                                SubHeader(title = stringResource(R.string.avatar_title_content_labels))
                                ContentWarnings(tags = avatar.tags)
                            }
                        }
                    }
                } else {
                    if (!model.once.value) {
                        model.once.value = true
                        navigator.pop()
                        Toast.makeText(
                            context,
                            "The avatar is either private or invalid!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun AvatarCard(
        thumbnailUrl: String,
        name: String,
        authorName: String
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
                text = name,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
            )

            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "By $authorName",
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
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
    fun ContentWarnings(tags: List<String>) {
        Row(
            modifier = Modifier.padding(24.dp)
        ) {
            tags.let {
                var foundEvenOne = false
                for (tag in tags) {
                    if (tag.contains("content_")) {
                        foundEvenOne = true
                        Badge(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .height(height = 24.dp)
                                .padding(start = 2.dp),
                            content = { Text( text = tag.substring("content_".length).uppercase() ) }
                        )
                    }
                }

                if (!foundEvenOne) {
                    Text(text = stringResource(R.string.avatar_text_content_labels_not_found))
                }
            }
        }
    }
}
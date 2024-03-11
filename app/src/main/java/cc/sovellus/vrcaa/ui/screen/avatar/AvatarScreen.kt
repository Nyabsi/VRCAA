package cc.sovellus.vrcaa.ui.screen.avatar

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.http.models.Avatar
import cc.sovellus.vrcaa.ui.components.misc.BadgesFromTags
import cc.sovellus.vrcaa.ui.components.misc.Description
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import cc.sovellus.vrcaa.ui.screen.avatar.AvatarScreenModel.AvatarState
import cc.sovellus.vrcaa.ui.components.card.AvatarCard
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import java.text.SimpleDateFormat
import java.util.Locale

class AvatarScreen(
    private val avatarId: String
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val context = LocalContext.current

        val model = rememberScreenModel { AvatarScreenModel(context, avatarId) }

        val state by model.state.collectAsState()

        when (val result = state) {
            is AvatarState.Loading -> LoadingIndicatorScreen().Content()
            is AvatarState.Result -> DisplayResult(result.avatar, model)
            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun DisplayResult(avatar: Avatar?, model: AvatarScreenModel) {
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
                            .padding(
                                top = padding.calculateTopPadding(),
                                bottom = padding.calculateBottomPadding()
                            ),
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

                                val parser =
                                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
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
                                BadgesFromTags(
                                    tags = avatar.tags,
                                    tagPropertyName = "content",
                                    localizationResourceInt = R.string.avatar_text_content_labels_not_found
                                )
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
}